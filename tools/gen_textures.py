#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Générateur procédural déterministe des textures 16×16 (blocs et objets) et de l'icône du mod.

Aucune dépendance externe (encodeur PNG pur Python via struct + zlib).
Entrées :
  - assets_src/registry/blocks.yml (palettes et motifs des blocs)
  - assets_src/registry/items.yml  (catalogue des objets)
  - src/main/resources/assets/cambocraft/models/{block,item}/*.json (références effectives)
Sorties :
  - src/main/resources/assets/cambocraft/textures/block/*.png (16×16 RGBA)
  - src/main/resources/assets/cambocraft/textures/item/*.png  (16×16 RGBA)
  - src/main/resources/assets/cambocraft/icon.png             (64×64 RGBA)
  - assets_src/textures/manifest_textures.json                (traçabilité des palettes)
"""
import hashlib
import json
import os
import struct
import sys
import zlib
import yaml

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(RACINE, 'src', 'main', 'resources', 'assets', 'cambocraft')
TEX_BLOCK = os.path.join(ASSETS, 'textures', 'block')
TEX_ITEM = os.path.join(ASSETS, 'textures', 'item')
MANIFEST = os.path.join(RACINE, 'assets_src', 'textures', 'manifest_textures.json')


def hex_rgba(code, alpha=255):
    code = code.lstrip('#')
    return (int(code[0:2], 16), int(code[2:4], 16), int(code[4:6], 16), alpha)


def melanger(c1, c2, t):
    return (
        max(0, min(255, int(round(c1[0] * (1 - t) + c2[0] * t)))),
        max(0, min(255, int(round(c1[1] * (1 - t) + c2[1] * t)))),
        max(0, min(255, int(round(c1[2] * (1 - t) + c2[2] * t)))),
        max(0, min(255, int(round(c1[3] * (1 - t) + c2[3] * t)))),
    )


def eclaircir(c, delta):
    return (
        max(0, min(255, c[0] + delta)),
        max(0, min(255, c[1] + delta)),
        max(0, min(255, c[2] + delta)),
        c[3],
    )


def bruit(graine, x, y):
    h = hashlib.blake2b(f"{graine}:{x}:{y}".encode('utf-8'), digest_size=2).digest()
    return (h[0] / 255.0) - 0.5


def encoder_png(largeur, hauteur, pixels):
    """Encode une grille [y][x] de tuples (R, G, B, A) en PNG RGBA 8 bits."""
    lignes = []
    for y in range(hauteur):
        rang = bytearray([0])  # filtre None
        for x in range(largeur):
            r, g, b, a = pixels[y][x]
            rang.extend((r & 255, g & 255, b & 255, a & 255))
        lignes.append(bytes(rang))
    brut = b''.join(lignes)

    def chunk(type_octets, donnees):
        crc = zlib.crc32(type_octets + donnees) & 0xFFFFFFFF
        return struct.pack('>I', len(donnees)) + type_octets + donnees + struct.pack('>I', crc)

    ihdr = struct.pack('>IIBBBBB', largeur, hauteur, 8, 6, 0, 0, 0)
    idat = zlib.compress(brut, level=9)
    return b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', ihdr) + chunk(b'IDAT', idat) + chunk(b'IEND', b'')


def ecrire_png(chemin, largeur, hauteur, pixels):
    os.makedirs(os.path.dirname(chemin), exist_ok=True)
    donnees = encoder_png(largeur, hauteur, pixels)
    if os.path.exists(chemin):
        with open(chemin, 'rb') as f:
            if f.read() == donnees:
                return False
    with open(chemin, 'wb') as f:
        f.write(donnees)
    return True


def dessiner_bloc(nom, spec):
    motif = spec.get('pattern', 'stone')
    base = hex_rgba(spec.get('base', '#8C9089'))
    accent = hex_rgba(spec.get('accent', '#6E736C'))
    alt = hex_rgba(spec.get('alt', '#A3A79F'))
    vide = (0, 0, 0, 0)
    grille = [[base for _ in range(16)] for _ in range(16)]

    for y in range(16):
        for x in range(16):
            n = bruit(nom, x, y)
            c = eclaircir(base, int(n * 14))
            if motif in ('bricks', 'tiles'):
                decal = 4 if (y // 4) % 2 == 1 else 0
                if y % 4 == 0 or (x + decal) % 8 == 0:
                    c = accent
                elif y % 4 == 1:
                    c = melanger(base, alt, 0.35)
            elif motif == 'polished':
                if x in (0, 15) or y in (0, 15):
                    c = accent if (x == 15 or y == 15) else alt
                elif (x + y) % 7 == 0:
                    c = melanger(base, alt, 0.3)
            elif motif in ('carving', 'relief', 'faces', 'runes'):
                if y in (0, 1, 14, 15) or x in (0, 15):
                    c = accent
                elif 3 <= x <= 12 and 3 <= y <= 12 and ((x in (4, 11)) or (y in (4, 11)) or (x == y)):
                    c = alt if motif == 'runes' else accent
                elif (x + y) % 4 == 0:
                    c = melanger(base, alt, 0.4)
            elif motif == 'planks':
                if y % 4 == 0:
                    c = accent
                elif (x == 7 and (y // 4) % 2 == 0) or (x == 13 and (y // 4) % 2 == 1):
                    c = accent
                elif y % 4 == 1:
                    c = melanger(base, alt, 0.25)
            elif motif in ('bark', 'column', 'bamboo_block'):
                if x % 4 == 0:
                    c = accent
                elif x % 4 == 2:
                    c = melanger(base, alt, 0.3)
                if motif == 'bamboo_block' and y in (0, 7, 15):
                    c = accent
            elif motif == 'wood_end':
                dx, dy = x - 7.5, y - 7.5
                d = int((dx * dx + dy * dy) ** 0.5)
                if d >= 7:
                    c = accent
                elif d % 2 == 0:
                    c = alt
            elif motif in ('leaves', 'palm_leaves', 'leaves_fruit'):
                if (x + y * 3) % 7 == 0 and n < -0.15:
                    c = accent
                elif motif == 'leaves_fruit' and (x, y) in ((4, 5), (11, 9), (7, 12)):
                    c = accent
                elif n > 0.15:
                    c = alt
            elif motif in ('sapling', 'flower', 'crop', 'torch', 'incense'):
                # Motifs découpés sur fond transparent
                dx = abs(x - 7.5)
                if y < 2 or dx > 5.5:
                    c = vide
                elif motif == 'torch':
                    if 6 <= x <= 9 and 6 <= y <= 15:
                        c = base if x in (7, 8) else accent
                    elif 6 <= x <= 9 and 3 <= y <= 5:
                        c = alt if y == 4 else accent
                    else:
                        c = vide
                elif motif == 'flower':
                    if 7 <= x <= 8 and y >= 8:
                        c = hex_rgba('#3C7A38')
                    elif 4 <= x <= 11 and 3 <= y <= 8 and (abs(x - 7.5) + abs(y - 5.5) <= 4.2):
                        c = alt if (abs(x - 7.5) <= 1.2 and abs(y - 5.5) <= 1.2) else base
                    else:
                        c = vide
                else:
                    if (x in (7, 8) and y >= 5) or (y >= 4 and abs(x - 7.5) <= (15 - y) * 0.45 and (x + y) % 2 == 0):
                        c = alt if y < 8 else (accent if (x + y) % 3 == 0 else base)
                    else:
                        c = vide
            elif motif == 'ore':
                if (x, y) in ((4, 4), (5, 4), (4, 5), (10, 6), (11, 6), (6, 11), (7, 11), (11, 12)):
                    c = accent
            elif motif == 'stained_glass':
                if x in (0, 15) or y in (0, 15) or x == y or x + y == 15:
                    c = accent
                else:
                    c = (base[0], base[1], base[2], 180)
            grille[y][x] = c
    return grille


def palette_objet(ident):
    """Choisit une palette et une silhouette en fonction de l'identifiant d'objet."""
    s = ident.lower()
    if any(k in s for k in ('lotus', 'beni', 'cristal', 'orbe', 'essence', 'larme', 'coeur', 'relique')):
        base, acc, alt = hex_rgba('#7FC8C0'), hex_rgba('#3F7A78'), hex_rgba('#D8F6F0')
    elif any(k in s for k in ('or', 'dore', 'couronne', 'sceptre', 'royal', 'piece')):
        base, acc, alt = hex_rgba('#D9AE4A'), hex_rgba('#8C6620'), hex_rgba('#F6DC86')
    elif any(k in s for k in ('ombre', 'nuit', 'kunai', 'shuriken', 'faux', 'noire', 'cendre')):
        base, acc, alt = hex_rgba('#484A58'), hex_rgba('#262833'), hex_rgba('#8C64B0')
    elif any(k in s for k in ('soie', 'krama', 'fil', 'tissu', 'banniere', 'bandage', 'cocon')):
        base, acc, alt = hex_rgba('#C84C44'), hex_rgba('#7E2824'), hex_rgba('#F0D8B8')
    elif any(k in s for k in ('riz', 'paille', 'farine', 'galette', 'nouille', 'nom_banh', 'sticky')):
        base, acc, alt = hex_rgba('#E6D9B2'), hex_rgba('#A68E58'), hex_rgba('#FFF8E4')
    elif any(k in s for k in ('poisson', 'trey', 'prahok', 'anguille', 'crevette', 'crabe', 'silure',
                              'carpe', 'loche', 'perche', 'barbeau', 'raie', 'mormyre', 'gourami', 'pangasius')):
        base, acc, alt = hex_rgba('#5C8AA6'), hex_rgba('#2F5268'), hex_rgba('#A6CFE2')
    elif any(k in s for k in ('amok', 'soupe', 'bouillon', 'cari', 'lok_lak', 'brochette', 'grille',
                              'salade', 'dessert', 'the', 'infusion', 'jus', 'sirop', 'lait', 'vin')):
        base, acc, alt = hex_rgba('#C97A3E'), hex_rgba('#7A4420'), hex_rgba('#F2C278')
    elif any(k in s for k in ('mangue', 'coco', 'jacque', 'banane', 'papaye', 'citron', 'piment',
                              'poivre', 'curcuma', 'galanga', 'ail', 'tamarin', 'sucre', 'sel',
                              'aubergine', 'concombre', 'herbe', 'feuille', 'fleur', 'graine', 'murier')):
        base, acc, alt = hex_rgba('#649E42'), hex_rgba('#3B6624'), hex_rgba('#D9B448')
    elif any(k in s for k in ('bambou', 'bois', 'teck', 'arc', 'arbalete', 'canne', 'pirogue', 'baton', 'flute')):
        base, acc, alt = hex_rgba('#B58C52'), hex_rgba('#6E4E28'), hex_rgba('#D9B87A')
    else:
        base, acc, alt = hex_rgba('#989EA6'), hex_rgba('#585E66'), hex_rgba('#CED4DC')

    if any(k in s for k in ('epee', 'sabre', 'dague', 'lame', 'katana', 'lance', 'hallebarde',
                            'faux', 'marteau', 'masse', 'hache', 'pioche', 'pelle', 'houe',
                            'ciseau', 'burin', 'baton', 'sceptre', 'canne', 'kunai')):
        forme = 'arme'
    elif any(k in s for k in ('arc', 'arbalete')):
        forme = 'arc'
    elif any(k in s for k in ('casque', 'couronne', 'masque', 'bandeau', 'chapeau')):
        forme = 'casque'
    elif any(k in s for k in ('plastron', 'tunique', 'robe', 'armure', 'cape', 'manteau')):
        forme = 'plastron'
    elif any(k in s for k in ('jambieres', 'pantalon', 'sarong', 'sampot')):
        forme = 'jambieres'
    elif any(k in s for k in ('bottes', 'sandales', 'chaussures')):
        forme = 'bottes'
    elif any(k in s for k in ('poisson', 'trey', 'anguille', 'crevette', 'crabe', 'silure',
                              'carpe', 'loche', 'perche', 'barbeau', 'raie', 'mormyre', 'gourami', 'pangasius')):
        forme = 'poisson'
    elif any(k in s for k in ('bol', 'amok', 'soupe', 'bouillon', 'cari', 'riz', 'salade', 'dessert',
                              'the', 'infusion', 'jus', 'lait', 'marmite', 'plat', 'nom_banh', 'lok_lak')):
        forme = 'bol'
    elif any(k in s for k in ('carnet', 'livre', 'parchemin', 'carte', 'lettre', 'traite', 'manuel', 'journal')):
        forme = 'livre'
    elif any(k in s for k in ('piece', 'jeton', 'medaille', 'sceau', 'anneau', 'bague', 'amulette', 'talisman', 'pendentif')):
        forme = 'medaille'
    else:
        forme = 'gemme'
    return forme, base, acc, alt


def dessiner_objet(ident):
    forme, base, acc, alt = palette_objet(ident)
    vide = (0, 0, 0, 0)
    g = [[vide for _ in range(16)] for _ in range(16)]
    manche = hex_rgba('#6E4A2C')

    for y in range(16):
        for x in range(16):
            n = int(bruit(ident, x, y) * 12)
            cb = eclaircir(base, n)
            if forme == 'arme':
                # Diagonale de lame (bas-gauche -> haut-droite)
                if 2 <= x <= 13 and 2 <= y <= 13 and abs((15 - y) - x) <= 1:
                    if x <= 5 and y >= 10:
                        g[y][x] = manche
                    elif x == 5 and y == 9 or x == 6 and y == 10:
                        g[y][x] = acc
                    else:
                        g[y][x] = alt if (15 - y) == x else cb
                elif 2 <= x <= 13 and 2 <= y <= 13 and abs((15 - y) - x) == 2 and x >= 5:
                    g[y][x] = acc
            elif forme == 'arc':
                dx, dy = x - 6, y - 8
                d = (dx * dx + dy * dy) ** 0.5
                if 4.3 <= d <= 5.8 and x >= 5:
                    g[y][x] = cb if d < 5.1 else acc
                elif x == 5 and 3 <= y <= 13:
                    g[y][x] = alt
            elif forme == 'casque':
                if 3 <= x <= 12 and 3 <= y <= 11:
                    if y in (3, 4) or x in (3, 12):
                        g[y][x] = acc
                    elif y >= 9 and 6 <= x <= 9:
                        g[y][x] = vide
                    else:
                        g[y][x] = alt if y == 5 else cb
            elif forme == 'plastron':
                if 3 <= x <= 12 and 3 <= y <= 13:
                    if y <= 4 and 6 <= x <= 9:
                        g[y][x] = vide
                    elif x in (3, 12) or y == 13:
                        g[y][x] = acc
                    else:
                        g[y][x] = alt if x in (7, 8) else cb
            elif forme == 'jambieres':
                if 4 <= x <= 11 and 3 <= y <= 13:
                    if y >= 7 and 7 <= x <= 8:
                        g[y][x] = vide
                    elif x in (4, 11) or y in (3, 13):
                        g[y][x] = acc
                    else:
                        g[y][x] = cb
            elif forme == 'bottes':
                if (4 <= x <= 7 or 9 <= x <= 12) and 7 <= y <= 13:
                    g[y][x] = acc if y == 13 else cb
            elif forme == 'poisson':
                dx, dy = (x - 7.5) / 4.8, (y - 8.0) / 2.8
                if dx * dx + dy * dy <= 1.0:
                    g[y][x] = alt if y < 8 else cb
                    if x == 5 and y == 7:
                        g[y][x] = acc
                elif x in (12, 13) and 6 <= y <= 10 and abs(y - 8) >= (x - 11):
                    g[y][x] = acc
            elif forme == 'bol':
                if 3 <= x <= 12 and 8 <= y <= 12 and (y < 12 or (5 <= x <= 10)):
                    g[y][x] = acc if y >= 11 or x in (3, 12) else hex_rgba('#8C5A34')
                elif 4 <= x <= 11 and 6 <= y <= 8:
                    g[y][x] = alt if (x + y) % 3 == 0 else cb
            elif forme == 'livre':
                if 3 <= x <= 12 and 2 <= y <= 13:
                    if x in (3, 12) or y in (2, 13):
                        g[y][x] = acc
                    elif x == 4:
                        g[y][x] = alt
                    else:
                        g[y][x] = cb
            elif forme == 'medaille':
                dx, dy = x - 7.5, y - 8.5
                d = (dx * dx + dy * dy) ** 0.5
                if d <= 4.6:
                    g[y][x] = acc if d >= 3.6 else (alt if d <= 1.6 else cb)
                elif y in (2, 3) and x in (7, 8):
                    g[y][x] = acc
            else:
                # Gemme / ingrédient octogonal
                dx, dy = abs(x - 7.5), abs(y - 7.5)
                if dx + dy <= 6.2 and dx <= 4.5 and dy <= 4.5:
                    if dx + dy >= 5.0:
                        g[y][x] = acc
                    elif x < 7 and y < 7:
                        g[y][x] = alt
                    else:
                        g[y][x] = cb
    return g


def dessiner_icone_mod():
    """Icône 64×64 : ciel de mousson doré, silhouette d'un Prasat et fleur de lotus au premier plan."""
    ciel_haut = hex_rgba('#243B55')
    ciel_bas = hex_rgba('#D99B52')
    temple = hex_rgba('#2A2421')
    eau = hex_rgba('#1F4E5F')
    lotus = hex_rgba('#F2B6C6')
    coeur = hex_rgba('#F5D76E')
    g = [[ciel_haut for _ in range(64)] for _ in range(64)]
    for y in range(64):
        for x in range(64):
            if y < 44:
                c = melanger(ciel_haut, ciel_bas, y / 44.0)
                # Trois tours de Prasat en silhouette
                for cx, h_tour, larg in ((32, 14, 7), (18, 22, 5), (46, 22, 5)):
                    if y >= h_tour and abs(x - cx) <= larg - max(0, (28 - y) // 3):
                        c = temple
                if 36 <= y < 44 and 8 <= x <= 56:
                    c = temple
            else:
                c = eclaircir(eau, int(bruit('eau', x, y) * 10))
                # Fleur de lotus stylisée au premier plan
                dx, dy = (x - 32) / 14.0, (y - 52) / 7.0
                if dx * dx + dy * dy <= 1.0 and y <= 56:
                    c = coeur if abs(x - 32) <= 3 and 49 <= y <= 53 else lotus
            if x in (0, 63) or y in (0, 63):
                c = hex_rgba('#8C6A2E')
            g[y][x] = c
    return g


def collecter_textures_modeles():
    """Parcourt tous les modèles JSON pour trouver chaque texture block/ et item/ référencée."""
    blocs_req = set()
    items_req = set()
    for sous in ('block', 'item'):
        d = os.path.join(ASSETS, 'models', sous)
        if not os.path.isdir(d):
            continue
        for fn in os.listdir(d):
            if not fn.endswith('.json'):
                continue
            with open(os.path.join(d, fn), encoding='utf-8') as f:
                data = json.load(f)
            for val in (data.get('textures') or {}).values():
                if val.startswith('cambocraft:block/'):
                    blocs_req.add(val.split('/', 1)[1])
                elif val.startswith('cambocraft:item/'):
                    items_req.add(val.split('/', 1)[1])
    return sorted(blocs_req), sorted(items_req)


def main():
    with open(os.path.join(RACINE, 'assets_src', 'registry', 'blocks.yml'), encoding='utf-8') as f:
        doc_blocs = yaml.safe_load(f)
    specs_blocs = dict(doc_blocs.get('textures') or {})

    blocs_req, items_req = collecter_textures_modeles()
    for nom in specs_blocs:
        if nom not in blocs_req:
            blocs_req.append(nom)
    blocs_req = sorted(set(blocs_req))

    nb_blocs_ecrits = 0
    manifest_blocs = {}
    for nom in blocs_req:
        spec = specs_blocs.get(nom)
        if not spec:
            # Dérivation automatique (ex: *_stripped_log_side, *_pillar_top)
            racine = nom
            for suf in ('_stripped_log_side', '_stripped_log_top', '_side', '_top', '_bottom'):
                if nom.endswith(suf):
                    racine = nom[:-len(suf)]
                    break
            spec = (specs_blocs.get(racine + '_planks')
                    or specs_blocs.get(racine + '_side')
                    or specs_blocs.get(racine)
                    or {'pattern': 'stone', 'base': '#8C9089', 'accent': '#6E736C', 'alt': '#A3A79F'})
        manifest_blocs[nom] = spec
        if ecrire_png(os.path.join(TEX_BLOCK, nom + '.png'), 16, 16, dessiner_bloc(nom, spec)):
            nb_blocs_ecrits += 1

    nb_items_ecrits = 0
    for nom in items_req:
        if ecrire_png(os.path.join(TEX_ITEM, nom + '.png'), 16, 16, dessiner_objet(nom)):
            nb_items_ecrits += 1

    ecrire_png(os.path.join(ASSETS, 'icon.png'), 64, 64, dessiner_icone_mod())

    os.makedirs(os.path.dirname(MANIFEST), exist_ok=True)
    with open(MANIFEST, 'w', encoding='utf-8') as f:
        json.dump({
            'textures_blocs': len(blocs_req),
            'textures_objets': len(items_req),
            'palettes_blocs': manifest_blocs,
        }, f, indent=2, ensure_ascii=False, sort_keys=True)
        f.write('\n')

    print(f"textures : {len(blocs_req)} blocs ({nb_blocs_ecrits} écrits), "
          f"{len(items_req)} objets ({nb_items_ecrits} écrits), icon.png 64x64")
    return 0


if __name__ == '__main__':
    sys.exit(main())
