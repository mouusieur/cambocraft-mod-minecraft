# Regeneration des captures du README.
#
# Les scenes sont construites et photographiees par la CI (.github/workflows/captures.yml),
# parce que ce bac a sable n'a pas d'acces a Maven/Mojang pour remapper le mod. Les PNG
# reviennent en morceaux base64 dans des check runs « capture-<fichier>-NNN ».
#
#   1. recuperer les captures de la CI :  make recuperer SHA=<sha du commit>
#   2. fabriquer les illustrations   :  make illustrations
#
SHA ?= HEAD

recuperer:
	@test -n "$(SHA)" || { echo "usage : make recuperer SHA=<sha>"; exit 1; }
	@python3 - <<'PY'
import base64, os, subprocess
sha = os.environ["SHA"]
depot = "mouusieur/cambocraft-mod-minecraft"
def checks():
    return subprocess.check_output(["gh", "api",
        f"repos/{depot}/commits/{sha}/check-runs?per_page=100",
        "--jq", '.check_runs[] | "\\(.name) \\(.id)"']).decode().split("\n")
paires = [l.split() for l in checks() if l.strip()]
bases = sorted({n.rsplit("-", 1)[0] for n, _ in paires
                if n.startswith("capture-") and n.rsplit("-", 1)[-1].isdigit()})
for base in bases:
    donnees = ""
    for nom, ident in sorted(n for n in paires if n[0].startswith(base + "-")):
        donnees += subprocess.check_output(["gh", "api",
            f"repos/{depot}/check-runs/{ident}", "--jq", ".output.text"]).decode()
    fichier = os.path.join("captures", base.replace("capture-", ""))
    open(fichier, "wb").write(base64.b64decode(donnees.replace("\n", "").strip()))
    print(fichier, os.path.getsize(fichier), "octets")
PY

illustrations:
	python3 tools/dev/captures/illustrations.py
	@python3 - <<'PY'
import glob, subprocess
for f in glob.glob("docs/captures/*.png"):
    subprocess.check_call(["convert", f, "-depth", "8", "-strip",
                           "-define", "png:compression-level=9", f + ".tmp"])
    subprocess.check_call(["mv", f + ".tmp", f])
print("illustrations optimisees (8 bits)")
PY

.PHONY: recuperer illustrations
