#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Générateur de « stubs » Java à partir des mappings Yarn (format Enigma).

But : permettre une compilation de contrôle HORS LIGNE du mod (sans accès aux dépôts
Maven ni au jar de Minecraft). Les stubs reprennent les noms de classes, de méthodes et
de champs réels de Yarn, avec leurs descripteurs. Ce que Yarn ne contient pas (hiérarchie,
généricité, caractère statique des méthodes sans argument, interfaces) est décrit dans
les fichiers `model/*.txt`, rédigés à la main.

Ce contrôle ne remplace PAS `./gradlew build` : il attrape les fautes de nommage, d'arité,
de types et toutes les incohérences internes du code, mais pas les erreurs qui dépendent
de la hiérarchie réelle ou de la généricité réelle de Minecraft.

Directives des fichiers model/*.txt (une par ligne, `#` = commentaire) :
  interface <Classe>
  enum <Classe>            /  class <Classe>   (force le type)
  super <Classe> : <Super>[, <Interface>...]   (`-` = pas de super-classe)
  generic <Classe> <T extends X, U>
  static <Classe> <méthode>          (méthode statique que l'heuristique ne détecte pas)
  instance <Classe> <méthode>        (l'inverse)
  staticfield <Classe> <champ>   /   instancefield <Classe> <champ>
  drop <Classe> <membre>             (retire un membre du stub)
  sig <Classe> <méthode> :: <déclaration Java complète>   (remplace toutes les surcharges)
  add <Classe> :: <déclaration Java complète>             (ajoute un membre)
  fieldtype <Classe> <champ> :: <type Java>               (type générique d'un champ)
  const <Classe> <NOM>                                    (constante d'énumération non mappée)
  import <Classe> :: <nom.complet.Importé>                (import supplémentaire du fichier)
Les classes sont désignées par leur nom simple s'il est unique (`Entity`, `Block.Settings`)
ou par leur chemin complet (`net/minecraft/entity/Entity`).
"""
import os, sys, re, pickle, hashlib, json, glob

HERE = os.path.dirname(os.path.abspath(__file__))
YARN_DIR = os.environ.get('CAMBO_YARN_DIR', '/home/user/refs/yarn')
OUT = os.environ.get('CAMBO_STUB_OUT', '/home/user/toolchain/build/stubs-src')

PRIM = {'V': 'void', 'Z': 'boolean', 'B': 'byte', 'C': 'char', 'S': 'short', 'I': 'int', 'J': 'long', 'F': 'float', 'D': 'double'}
# Sous-classes de Block dont Yarn ne declare pas le constructeur (il est herite ou
# non enumere). Sans cette liste, le controle hors ligne echouerait sur des faux negatifs.
BLOCS_A_CONSTRUCTEUR = {
    'net/minecraft/block/PillarBlock',
    'net/minecraft/block/Block',
}

# Constructeurs herites que Yarn n'enumere pas dans la classe fille (Enigma ne les copie pas).
# Sans eux, une sous-classe du mod ne peut pas appeler `super(type, world)`.
# Visibilite reelle des methodes : Yarn ne la donne pas (tout est emis `public`).
VISIBILITE = {
    ('net/minecraft/entity/mob/MobEntity', 'initGoals'): 'public',
}

CONSTRUCTEURS_HERITES = {
    'net/minecraft/entity/mob/PathAwareEntity': [
        '(net.minecraft.entity.EntityType p0, net.minecraft.world.World p1)',
    ],
    'net/minecraft/entity/mob/MobEntity': [
        '(net.minecraft.entity.EntityType p0, net.minecraft.world.World p1)',
    ],
    'net/minecraft/entity/passive/PassiveEntity': [
        '(net.minecraft.entity.EntityType p0, net.minecraft.world.World p1)',
    ],
}

# Methodes statiques que Yarn ne marque pas `static` (il n'indique pas ce modificateur).
# Sans cette table, le controle hors ligne rejette un appel correct.
METHODES_STATIQUES = {
    'net/minecraft/entity/mob/HostileEntity': {'createHostileAttributes'},
    'net/minecraft/entity/mob/MobEntity': {'createMobAttributes'},
    'net/minecraft/entity/passive/PassiveEntity': {'createPassiveAttributes'},
    'net/minecraft/entity/animal/AnimalEntity': {'createAnimalAttributes'},
    'net/minecraft/entity/attribute/DefaultAttributeContainer'
    'net/minecraft/entity/EntityType': set(),
    'net/minecraft/client/render/RenderLayer': {
        'getSolid', 'getCutout', 'getCutoutMipped', 'getTranslucent', 'getTranslucentMovingBlock',
        'getTripwire', 'getEntitySolid', 'getEntityCutout', 'getEntityCutoutNoCull',
        'getEntityCutoutNoCullZOffset', 'getItemEntityTranslucentCull', 'getEntityTranslucentCull',
        'getEntityTranslucent', 'getEntitySmoothCutout', 'getBeaconBeam', 'getEntityDecal',
        'getEntityNoOutline', 'getEntityShadow', 'getEntityAlpha', 'getEyes', 'getEnergySwirl',
        'getArmorCutoutNoCull', 'getDirectEntityGlint', 'getEntityGlint', 'getItemEntityGlint',
        'getEntityGlintDirect', 'getDragonRays', 'getDragonRaysDepth', 'getCrumbling', 'getText',
        'getTextSeeThrough', 'getTextIntensity', 'getTextIntensitySeeThrough',
        'getTextPolygonOffset', 'getTextIntensityPolygonOffset', 'getLightning', 'getWaterMask',
        'getLeash', 'getLineStrip', 'getDebugLineStrip', 'getDebugQuads', 'getEndPortal',
        'getDragonExplosionAlpha', 'getEntityTranslucentEmissive',
    },
}

ENUM_RESERVED = set('valueOf values equals hashCode name ordinal compareTo finalize clone getDeclaringClass'.split())
KEYWORDS = set('abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for goto if implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while var record'.split())

def parse_yarn():
    root = os.path.join(YARN_DIR, 'mappings')
    classes = {}
    for dp, dn, fn in os.walk(root):
        for f in fn:
            if not f.endswith('.mapping'): continue
            stack = []; cur = None
            with open(os.path.join(dp, f), encoding='utf-8') as fh:
                for line in fh:
                    s = line.rstrip('\n')
                    if not s.strip(): continue
                    ind = len(s) - len(s.lstrip('\t'))
                    tok = s.strip().split(' ')
                    k = tok[0]
                    if k == 'CLASS':
                        while stack and stack[-1][0] >= ind: stack.pop()
                        inter = tok[1]; named = tok[2] if len(tok) > 2 else None
                        if stack:
                            inter_full = stack[-1][1] + '$' + inter
                            named_full = stack[-1][2] + '$' + (named if named else inter)
                        else:
                            inter_full = inter; named_full = named if named else inter
                        stack.append((ind, inter_full, named_full))
                        classes[named_full] = {'inter': inter_full, 'fields': [], 'methods': []}
                        cur = None
                    elif k == 'FIELD':
                        while stack and stack[-1][0] >= ind: stack.pop()
                        c = classes[stack[-1][2]]
                        if len(tok) == 4: c['fields'].append((tok[2], tok[3]))
                        elif len(tok) == 3 and not tok[1].startswith(('field_', 'comp_')): c['fields'].append((tok[1], tok[2]))
                        cur = None
                    elif k == 'METHOD':
                        while stack and stack[-1][0] >= ind: stack.pop()
                        c = classes[stack[-1][2]]
                        if len(tok) == 4: m = [tok[2], tok[3], []]
                        elif len(tok) == 3: m = [tok[1], tok[2], []]
                        else: cur = None; continue
                        if m[0].startswith(('method_', 'comp_')) or (m[0].startswith('lambda$')) or m[0] in ('<clinit>',):
                            cur = None; continue
                        c['methods'].append(m); cur = m
                    elif k == 'ARG':
                        if cur is not None and len(tok) >= 2:
                            try: cur[2].append(int(tok[1]))
                            except ValueError: pass
    return classes

def load_yarn():
    cache = os.path.join(os.path.dirname(OUT), 'yarn_stub_index.pkl')
    if os.path.exists(cache):
        cached = pickle.load(open(cache, 'rb'))
        if cached:
            return cached
    c = parse_yarn()
    if c:
        os.makedirs(os.path.dirname(cache), exist_ok=True)
        pickle.dump(c, open(cache, 'wb'))
    return c

class Model:
    def __init__(self, classes):
        self.classes = classes
        self.inter2named = {v['inter']: k for k, v in classes.items()}
        self.simple = {}
        for k in classes:
            self.simple.setdefault(k.split('/')[-1].replace('$', '.'), []).append(k)
        self.kind = {}; self.supers = {}; self.generic = {}; self.static = set(); self.instance = set()
        self.staticfield = set(); self.instancefield = set(); self.drop = set()
        self.sig = {}; self.add = {}; self.imports = {}; self.fieldtype = {}; self.consts = {}
        self.errors = []

    def resolve(self, name):
        if name in self.classes: return name
        n2 = name.replace('.', '/')
        if n2 in self.classes: return n2
        # nom simple (avec éventuels niveaux imbriqués séparés par des points)
        c = self.simple.get(name)
        if c and len(c) == 1: return c[0]
        if c and len(c) > 1:
            self.errors.append('nom ambigu: %s -> %s' % (name, c)); return None
        # chemin complet avec $ pour les classes imbriquées
        n3 = name.replace('.', '$')
        cands = [k for k in self.classes if k.endswith('/' + n3)]
        if len(cands) == 1: return cands[0]
        self.errors.append('classe inconnue: %s' % name); return None

    def load(self, path):
        for ln, line in enumerate(open(path, encoding='utf-8'), 1):
            s = line.strip()
            if not s or s.startswith('#'): continue
            try:
                self._directive(s)
            except Exception as e:
                self.errors.append('%s:%d: %s (%s)' % (os.path.basename(path), ln, s, e))

    def _directive(self, s):
        head, _, rest = s.partition(' ')
        if head in ('interface', 'enum', 'class', 'annotation'):
            c = self.resolve(rest.strip());
            if c: self.kind[c] = head
        elif head == 'super':
            cn, _, sup = rest.partition(':')
            c = self.resolve(cn.strip())
            if c: self.supers[c] = [x.strip() for x in sup.split(',') if x.strip()]
        elif head == 'generic':
            cn, _, g = rest.partition(' ')
            c = self.resolve(cn.strip())
            if c: self.generic[c] = g.strip()
        elif head in ('static', 'instance', 'staticfield', 'instancefield', 'drop'):
            cn, _, m = rest.partition(' ')
            c = self.resolve(cn.strip())
            if c: getattr(self, head).add((c, m.strip()))
        elif head == 'sig':
            left, _, decl = rest.partition('::')
            cn, _, m = left.strip().partition(' ')
            c = self.resolve(cn.strip())
            if c: self.sig.setdefault((c, m.strip()), []).append(decl.strip())
        elif head == 'const':
            cn, _, m = rest.partition(' ')
            c = self.resolve(cn.strip())
            if c: self.consts.setdefault(c, []).append(m.strip())
        elif head == 'fieldtype':
            left, _, ty = rest.partition('::')
            cn, _, f = left.strip().partition(' ')
            c = self.resolve(cn.strip())
            if c: self.fieldtype[(c, f.strip())] = ty.strip()
        elif head == 'add':
            cn, _, decl = rest.partition('::')
            c = self.resolve(cn.strip())
            if c: self.add.setdefault(c, []).append(decl.strip())
        elif head == 'import':
            cn, _, imp = rest.partition('::')
            c = self.resolve(cn.strip())
            if c: self.imports.setdefault(c, []).append(imp.strip())
        else:
            raise ValueError('directive inconnue')

def signature_de(ligne):
    """Signature simplifiée (nom + types élagués) d'une méthode, pour dédoublonner les ajouts."""
    m = re.search(r'([A-Za-z_$][A-Za-z0-9_$]*)\s*\(([^)]*)\)', ligne)
    if not m:
        return None
    types = []
    for p in m.group(2).split(','):
        p = p.strip()
        if not p:
            continue
        p = re.sub(r'<[^>]*>', '', p)          # paramètres génériques retirés
        parts = p.split()
        type_ = parts[0] if len(parts) == 1 else ' '.join(parts[:-1])
        type_ = type_.replace('...', '[]')
        type_ = type_.split('.')[-1]           # paquet retiré
        types.append(type_)
    return (m.group(1), ', '.join(types))


def jname(n):
    return n.replace('/', '.').replace('$', '.')

class Gen:
    def __init__(self, model, provided_external):
        self.m = model
        self.classes = model.classes
        self.external = {}  # nom interne -> True
        self.provided = provided_external

    def conv(self, d, i):
        arr = 0
        while d[i] == '[': arr += 1; i += 1
        if d[i] == 'L':
            j = d.index(';', i); n = d[i + 1:j]
            n = self.m.inter2named.get(n, n)
            if n not in self.classes and not n.startswith('java/'):
                self.external[n] = True
            elif n not in self.classes and n.startswith('java/'):
                self.external[n] = True  # filtré plus tard (présence dans le JRE)
            return jname(n), arr, j + 1
        return PRIM[d[i]], arr, i + 1

    def parse_desc(self, desc):
        if desc.startswith('('):
            i = 1; ps = []
            while desc[i] != ')':
                t, arr, i = self.conv(desc, i); ps.append((t, arr))
            r, arr, _ = self.conv(desc, i + 1)
            return ps, (r, arr)
        t, arr, _ = self.conv(desc, 0)
        return None, (t, arr)

    def kind_of(self, name):
        k = self.m.kind.get(name)
        if k: return k
        c = self.classes[name]
        own = 'L' + c['inter'] + ';'
        consts = [f for f in c['fields'] if f[1] == own and re.match(r'^[A-Z][A-Z0-9_]*$', f[0])]
        if len(consts) >= 2:
            # Les constructeurs d'énumération commencent tous par (String nom, int ordinal, ...)
            ctors = [mm for mm in c['methods'] if mm[0] == '<init>']
            if all(mm[1].startswith('(Ljava/lang/String;I') for mm in ctors): return 'enum'
        return 'class'

    def default_value(self, t, arr):
        if arr: return 'null'
        if t == 'boolean': return 'false'
        if t in ('byte', 'short', 'int', 'long', 'float', 'double', 'char'): return '0'
        return 'null'

    def emit_class(self, name, children, indent, top):
        m = self.m; c = self.classes[name]
        kind = self.kind_of(name)
        simple = name.split('/')[-1].split('$')[-1]
        pad = '    ' * indent
        gen = m.generic.get(name, '')
        gen = ('<' + gen.strip('<>') + '>') if gen else ''
        sup = [x.replace('/', '.') for x in m.supers.get(name, [])]
        ext = ''; impl = []
        if sup:
            if kind == 'interface':
                impl = [s for s in sup if s != '-']
            else:
                if sup[0] != '-' and kind != 'enum': ext = sup[0]; impl = sup[1:]
                elif kind == 'enum': impl = [s for s in sup if s != '-']
                else: impl = sup[1:]
        out = []
        mods = 'public ' + ('' if top else ('static ' if kind in ('class',) else ''))
        if kind == 'interface':
            head = '%spublic interface %s%s%s {' % (pad, simple, gen, (' extends ' + ', '.join(impl)) if impl else '')
        elif kind == 'enum':
            head = '%spublic enum %s%s {' % (pad, simple, (' implements ' + ', '.join(impl)) if impl else '')
        elif kind == 'annotation':
            head = '%spublic @interface %s {' % (pad, simple)
        else:
            head = '%s%sclass %s%s%s%s {' % (pad, mods, simple, gen, (' extends ' + ext) if ext else '', (' implements ' + ', '.join(impl)) if impl else '')
        out.append(head)
        ip = pad + '    '
        own = 'L' + c['inter'] + ';'
        seen_fields = set()
        if kind == 'enum':
            consts = [f[0] for f in c['fields'] if f[1] == own and re.match(r'^[A-Z][A-Z0-9_]*$', f[0]) and (name, f[0]) not in m.drop]
            for extra in m.consts.get(name, []):
                if extra not in consts:
                    consts.append(extra)
            out.append(ip + ', '.join(consts) + ';')
            seen_fields.update(consts)
        if kind != 'annotation':
            for fname, fdesc in c['fields']:
                if fname in seen_fields or (name, fname) in m.drop or fname in KEYWORDS: continue
                seen_fields.add(fname)
                _, (t, arr) = self.parse_desc(fdesc)
                ty = t + '[]' * arr
                if (name, fname) in m.fieldtype: ty = m.fieldtype[(name, fname)]
                is_static = bool(re.match(r'^[A-Z][A-Z0-9_]*$', fname))
                if (name, fname) in m.staticfield: is_static = True
                if (name, fname) in m.instancefield: is_static = False
                if kind == 'interface':
                    out.append('%s%s %s = %s;' % (ip, ty, fname, self.default_value(t, arr)))
                else:
                    out.append('%spublic %s%s %s;' % (ip, 'static ' if is_static else '', ty, fname))
            seen = set(); has_noarg_ctor = False; ctor_count = 0
            replaced = set(k[1] for k in m.sig if k[0] == name)
            for mname, mdesc, args in c['methods']:
                if (name, mname) in m.drop or mname in replaced: continue
                ps, (rt, rarr) = self.parse_desc(mdesc)
                key = (mname, tuple(ps))
                if key in seen: continue
                seen.add(key)
                params = []
                for idx, (t, arr) in enumerate(ps):
                    if idx == len(ps) - 1 and arr >= 1:
                        params.append('%s%s... p%d' % (t, '[]' * (arr - 1), idx))
                    else:
                        params.append('%s%s p%d' % (t, '[]' * arr, idx))
                pstr = ', '.join(params)
                if mname == '<init>':
                    if kind in ('interface', 'enum'): continue
                    ctor_count += 1
                    if not ps: has_noarg_ctor = True
                    out.append('%spublic %s(%s) { }' % (ip, simple, pstr))
                    continue
                if mname in KEYWORDS: continue
                if kind == 'enum' and mname in ENUM_RESERVED: continue
                if mname in ('getClass', 'notify', 'notifyAll', 'wait') or (mname == 'finalize' and not ps): continue
                is_static = bool(args) and min(args) == 0 and len(ps) > 0
                if (name, mname) in m.static: is_static = True
                if (name, mname) in m.instance: is_static = False
                if mname in METHODES_STATIQUES.get(name, ()): is_static = True
                rty = rt + '[]' * rarr
                body = '{ throw new RuntimeException(); }'
                if kind == 'interface':
                    out.append('%s%s %s %s(%s) %s' % (ip, 'static' if is_static else 'default', rty, mname, pstr, body))
                else:
                    out.append('%spublic %s%s %s(%s) %s' % (ip, 'static ' if is_static else '', rty, mname, pstr, body))
            # Yarn n'enumere pas les constructeurs herites : on ajoute ceux que les
            # sous-classes de Block utilisent couramment (ex. PillarBlock(Settings)).
            sous_classe_de_bloc = (
                'net/minecraft/class_2248' in m.supers.get(name, [])
                or name in BLOCS_A_CONSTRUCTEUR
            )
            if kind == 'class' and sous_classe_de_bloc:
                if (name, '<init>') not in m.sig:
                    out.append('%spublic %s(net.minecraft.block.AbstractBlock.Settings p0) { }' % (ip, simple))
            if kind == 'class' and not has_noarg_ctor:
                out.append('%spublic %s() { }' % (ip, simple))
            if kind == 'class':
                for signature in CONSTRUCTEURS_HERITES.get(name, ()):
                    out.append('%spublic %s%s { }' % (ip, simple, signature))
            for (cn, mn), decls in m.sig.items():
                if cn == name:
                    for d in decls: out.append(ip + d)
            signatures = set()
            for ligne in out:
                sig = signature_de(ligne)
                if sig:
                    signatures.add(sig)
            for d in m.add.get(name, []):
                sig = signature_de(d)
                if sig and sig in signatures:
                    continue
                if sig:
                    signatures.add(sig)
                out.append(ip + d)
        for ch in sorted(children.get(name, [])):
            last = ch.split('$')[-1]
            if last.isdigit(): continue
            out.extend(self.emit_class(ch, children, indent + 1, False))
        out.append(pad + '}')
        return out

    def generate(self):
        children = {}
        tops = []
        for k in self.classes:
            if '$' in k:
                parent = k.rsplit('$', 1)[0]
                children.setdefault(parent, []).append(k)
            else:
                tops.append(k)
        files = {}
        for t in sorted(tops):
            pkg = '.'.join(t.split('/')[:-1])
            body = self.emit_class(t, children, 0, True)
            # imports : noms simples uniques cités dans les déclarations manuscrites de ce fichier
            manual = []
            group = [t] + [k for k in self.classes if k.startswith(t + '$')]
            for g in group:
                manual += self.m.supers.get(g, []) + [self.m.generic.get(g, '')] + self.m.add.get(g, [])
                manual += [v for (cn, fn), v in self.m.fieldtype.items() if cn == g]
                for (cn, mn), decls in self.m.sig.items():
                    if cn == g: manual += decls
            imps = set(['java.util.*', 'java.util.function.*', 'java.util.stream.*'])
            text = ' '.join(manual)
            for ident in set(re.findall(r'\b[A-Z][A-Za-z0-9_]*\b', text)):
                cands = self.m.simple.get(ident)
                if cands and len(cands) == 1 and '$' not in cands[0]:
                    cpkg = '.'.join(cands[0].split('/')[:-1])
                    if cpkg != pkg: imps.add(jname(cands[0]))
            for g in group:
                for i in self.m.imports.get(g, []): imps.add(i)
            src = []
            if pkg: src.append('package %s;' % pkg)
            for i in sorted(imps): src.append('import %s;' % i)
            src.append('@SuppressWarnings("all")')
            src.extend(body)
            files[t + '.java'] = '\n'.join(src) + '\n'
        return files

def main():
    classes = load_yarn()
    model = Model(classes)
    for p in sorted(glob.glob(os.path.join(HERE, 'model', '*.txt'))):
        model.load(p)
    if model.errors:
        print('\n'.join('MODELE: ' + e for e in model.errors)); sys.exit(2)
    # classes externes fournies par les sources manuscrites
    provided = set()
    hw = os.path.join(HERE, 'handwritten')
    for dp, dn, fn in os.walk(hw):
        for f in fn:
            if f.endswith('.java'):
                provided.add(os.path.relpath(os.path.join(dp, f), hw)[:-5])
    extra = os.environ.get('CAMBO_STUB_PROVIDED', '')
    for p in extra.split(':'):
        if p and os.path.exists(p):
            for line in open(p):
                provided.add(line.strip())
    # Bibliothèques fournies sous forme de vrais jars : ne jamais générer de stub pour elles.
    global FOURNIS
    FOURNIS = tuple(os.environ.get('CAMBO_STUB_FOURNIS', 'com/google/gson,com/mojang/brigadier,org/slf4j').split(','))
    gen = Gen(model, provided)
    files = gen.generate()
    # externes : classes référencées hors Yarn
    ext = sorted(gen.external)
    ext_tops = {}
    for e in ext:
        top = e.split('$')[0]
        ext_tops.setdefault(top, set()).add(e)
    jre_list = os.environ.get('CAMBO_JRE_CLASSES', '/home/user/toolchain/build/jre_classes.txt')
    jre = set(l.strip() for l in open(jre_list)) if os.path.exists(jre_list) else None
    n_ext = 0
    for top, members in sorted(ext_tops.items()):
        if top in provided: continue
        if any(top.startswith(prefix) for prefix in FOURNIS): continue
        if jre is not None and top in jre: continue
        if jre is None and top.startswith(('java/', 'javax/')): continue
        # arbre des classes imbriquées
        def emit(name, indent):
            pad = '    ' * indent; simple = name.split('/')[-1].split('$')[-1]
            lines = ['%spublic %sclass %s {' % (pad, '' if indent == 0 else 'static ', simple)]
            for mm in sorted(members):
                if mm.startswith(name + '$') and '$' not in mm[len(name) + 1:]:
                    lines.extend(emit(mm, indent + 1))
            lines.append(pad + '}')
            return lines
        # s'assurer que les parents intermédiaires existent
        for mm in list(members):
            parts = mm.split('$')
            for i in range(1, len(parts)): members.add('$'.join(parts[:i]))
        pkg = '.'.join(top.split('/')[:-1])
        src = ('package %s;\n' % pkg if pkg else '') + '@SuppressWarnings("all")\n' + '\n'.join(emit(top, 0)) + '\n'
        files[top + '.java'] = src; n_ext += 1
    # écriture (uniquement les fichiers modifiés)
    written = 0
    existing = set()
    for dp, dn, fn in os.walk(OUT):
        for f in fn: existing.add(os.path.relpath(os.path.join(dp, f), OUT))
    for rel, src in files.items():
        p = os.path.join(OUT, rel)
        existing.discard(rel)
        if os.path.exists(p) and open(p, encoding='utf-8').read() == src: continue
        os.makedirs(os.path.dirname(p), exist_ok=True)
        open(p, 'w', encoding='utf-8').write(src); written += 1
    for rel in existing:
        os.remove(os.path.join(OUT, rel))
    print('stubs: %d fichiers (%d externes), %d écrits, %d supprimés' % (len(files), n_ext, written, len(existing)))

if __name__ == '__main__':
    main()
