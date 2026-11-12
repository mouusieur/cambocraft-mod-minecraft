#!/usr/bin/env bash
# Compilation de contrôle hors ligne (voir docs/TECHNIQUE.md, section « Chaîne de compilation »).
# 1. génère les stubs Yarn, 2. compile les stubs (+ les stubs écrits à la main pour Fabric API et Java),
# 3. compile le code du mod, 4. compile et lance les tests du noyau.
# Ce contrôle ne remplace pas `./gradlew build` : il valide les noms et les types vis-à-vis de Yarn.
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$HERE/../../.." && pwd)"
TC=/home/user/toolchain
JAVA="$TC/jre21/bin/java"
# Encodage forcé en UTF-8 : les messages du compilateur contiennent des accents.
JOPTS=(-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8)
JDT="$TC/jars/jdt-analyzer-17.jar"
# Compilateur : Eclipse JDT (analyseur @ctxo, JDT 2024) — accepte la syntaxe Java 21.
COMPILER="org.eclipse.jdt.internal.compiler.batch.Main"
STUB_SRC="$TC/build/stubs-src"
STUB_OUT="$TC/build/stubs-classes"
HW_OUT="$TC/build/handwritten-classes"
MOD_OUT="$TC/build/mod-classes"
TEST_OUT="$TC/build/test-classes"
CP_JARS="$JDT:$TC/jars/deps.jar"
mkdir -p "$TC/build" "$TC/jars" /home/user/refs

# Auto-bootstrap de la chaîne hors-repo si le conteneur a redémarré
if [ ! -x "$JAVA" ]; then
  pip install --break-system-packages --quiet jdk4py
  python3 -c 'import os, jdk4py; d="/home/user/toolchain/jre21"; os.path.lexists(d) and os.unlink(d); os.symlink(str(jdk4py.JAVA_HOME), d)'
fi
if [ ! -f "$JDT" ]; then
  mkdir -p /tmp/cambo-boot
  (cd /tmp/cambo-boot && npm pack --quiet @ctxo/lang-java-analyzer@0.13.0 >/dev/null && tar -xzf ctxo-lang-java-analyzer-0.13.0.tgz && cp package/jar/ctxo-jdt-analyzer-17.jar "$JDT")
fi
if [ ! -f "$TC/jars/deps.jar" ]; then
  [ -d /home/user/refs/brigadier ] || git clone --depth 1 https://github.com/Mojang/brigadier.git /home/user/refs/brigadier
  mkdir -p "$TC/build/deps-classes"
  find /home/user/refs/brigadier/src/main/java "$HERE/handwritten/org/slf4j" -name "*.java" > "$TC/build/deps.txt"
  "$JAVA" -cp "$JDT" "$COMPILER" -source 17 -target 17 -nowarn -proc:none -d "$TC/build/deps-classes" @"$TC/build/deps.txt"
  python3 -c 'import os,zipfile; r="/home/user/toolchain/build/deps-classes"; z=zipfile.ZipFile("/home/user/toolchain/jars/deps.jar","w"); [z.write(os.path.join(dp,f),os.path.relpath(os.path.join(dp,f),r)) for dp,_,fn in os.walk(r) for f in fn]; z.close()'
fi
if [ ! -d /home/user/refs/yarn/mappings ]; then
  rm -rf /home/user/refs/yarn "$TC/build/yarn_stub_index.pkl"
  git clone --depth 1 --branch 1.21.1 https://github.com/FabricMC/yarn.git /home/user/refs/yarn
fi
if [ ! -f "$TC/build/jre_classes.txt" ]; then
  python3 - <<'PY'
import subprocess
code = """
import java.net.URI; import java.nio.file.*; import java.util.stream.*;
public class ListJre {
  public static void main(String[] a) throws Exception {
    FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
    try (Stream<Path> s = Files.walk(fs.getPath("/modules"))) {
      s.filter(p -> p.getNameCount() > 2 && p.toString().endsWith(".class") && !p.toString().contains("$") && !p.toString().endsWith("module-info.class"))
       .forEach(p -> { String r = p.subpath(2, p.getNameCount()).toString(); System.out.println(r.substring(0, r.length() - 6)); });
    }
  }
}"""
open('/tmp/ListJre.java', 'w').write(code)
subprocess.check_call(['/home/user/toolchain/jre21/bin/java', '-cp', '/home/user/toolchain/jars/jdt-analyzer-17.jar', 'org.eclipse.jdt.internal.compiler.batch.Main', '-source', '17', '-target', '17', '/tmp/ListJre.java', '-d', '/tmp'])
open('/home/user/toolchain/build/jre_classes.txt', 'w').write(subprocess.check_output(['/home/user/toolchain/jre21/bin/java', '-cp', '/tmp', 'ListJre']).decode())
PY
fi
fail=0

step() { printf '\n=== %s\n' "$*"; }

# 1. stubs Yarn -------------------------------------------------------------
step "Stubs Yarn"
python3 "$HERE/gen_stubs.py" || exit 2
stub_hash=$(find "$STUB_SRC" -name '*.java' -newer "$STUB_OUT/.stamp" -print -quit 2>/dev/null)
if [ ! -f "$STUB_OUT/.stamp" ] || [ -n "$stub_hash" ]; then
  rm -rf "$STUB_OUT" && mkdir -p "$STUB_OUT"
  find "$STUB_SRC" -name "*.java" > "$TC/build/stubs.txt"
  if ! "$JAVA" "${JOPTS[@]}" -Xmx2g -cp "$JDT" "$COMPILER" -source 21 -target 21 -nowarn -proc:none -encoding UTF-8 -cp "$CP_JARS" -d "$STUB_OUT" @"$TC/build/stubs.txt" > "$TC/build/stubs.log" 2>&1; then
    echo "ÉCHEC de la compilation des stubs :"; grep -A3 "ERROR in" "$TC/build/stubs.log" | head -40; fail=1
  fi
  touch "$STUB_OUT/.stamp"
else
  echo "stubs inchangés (cache)"
fi

# 2. stubs Fabric API / Java écrits à la main ------------------------------
step "Stubs Fabric API et Java"
rm -rf "$HW_OUT" && mkdir -p "$HW_OUT"
find "$HERE/handwritten" -name "*.java" > "$TC/build/hw.txt" 2>/dev/null || true
if [ -s "$TC/build/hw.txt" ]; then
  if ! "$JAVA" "${JOPTS[@]}" -Xmx1g -cp "$JDT" "$COMPILER" -source 21 -target 21 -nowarn -proc:none -encoding UTF-8 -cp "$STUB_OUT:$CP_JARS" -d "$HW_OUT" @"$TC/build/hw.txt" > "$TC/build/hw.log" 2>&1; then
    echo "ÉCHEC des stubs manuels :"; grep -A3 "ERROR in" "$TC/build/hw.log" | head -60; fail=1
  fi
fi

# 3. code du mod -----------------------------------------------------------
step "Compilation du mod"
rm -rf "$MOD_OUT" && mkdir -p "$MOD_OUT"
find "$ROOT/src/main/java" -name "*.java" 2>/dev/null | sort > "$TC/build/mod.txt"
if [ -s "$TC/build/mod.txt" ]; then
  if ! "$JAVA" "${JOPTS[@]}" -Xmx2g -cp "$JDT" "$COMPILER" -source 21 -target 21 -nowarn -proc:none -encoding UTF-8 \
      -cp "$STUB_OUT:$HW_OUT:$CP_JARS" -d "$MOD_OUT" @"$TC/build/mod.txt" > "$TC/build/mod.log" 2>&1; then
    echo "ÉCHEC de la compilation du mod :"; grep -A4 "ERROR in" "$TC/build/mod.log" | head -120; fail=1
  else
    echo "mod : OK ($(find "$MOD_OUT" -name '*.class' | wc -l) classes)"
  fi
else
  echo "aucune source Java dans src/main/java"
fi

# 4. tests ---------------------------------------------------------------
step "Compilation des tests"
rm -rf "$TEST_OUT" && mkdir -p "$TEST_OUT"
find "$ROOT/src/test/java" -name "*.java" 2>/dev/null | sort > "$TC/build/test.txt"
if [ -s "$TC/build/test.txt" ] && [ -d "$MOD_OUT" ]; then
  if ! "$JAVA" "${JOPTS[@]}" -Xmx1g -cp "$JDT" "$COMPILER" -source 21 -target 21 -nowarn -proc:none -encoding UTF-8 \
      -cp "$MOD_OUT:$STUB_OUT:$HW_OUT:$CP_JARS" -d "$TEST_OUT" @"$TC/build/test.txt" > "$TC/build/test.log" 2>&1; then
    echo "ÉCHEC de la compilation des tests :"; grep -A4 "ERROR in" "$TC/build/test.log" | head -80; fail=1
  else
    echo "tests : compilation OK"
    "$JAVA" "${JOPTS[@]}" -Xmx2g -cp "$TEST_OUT:$MOD_OUT:$STUB_OUT:$HW_OUT:$CP_JARS" com.cambocraft.test.TestMain || fail=1
  fi
fi

if [ "$fail" -ne 0 ]; then echo; echo ">>> CONTRÔLE EN ÉCHEC"; exit 1; fi
echo; echo ">>> CONTRÔLE OK"
