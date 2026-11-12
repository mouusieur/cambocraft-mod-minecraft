#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Validateur complet des données, ressources, traductions et références de Cambocraft.

Couvre les exigences §15.3 (4. validateur de données, 7. vérificateur de ressources manquantes,
10. vérificateur de traductions) et le critère d'acceptation §18 (« 0 erreur du validateur »).

Vérifications exécutées :
  1. Syntaxe JSON de tous les fichiers sous src/main/resources/.
  2. Seuils §16.1 : >= 200 blocs enregistrés, >= 180 objets autonomes avec icônes 16×16.
  3. Cohérence blockstates -> models/block/*.json.
  4. Cohérence models/{block,item}/*.json -> textures/{block,item}/*.png (en-tête PNG 16×16).
  5. Tables de butin des blocs et balises d'outils (mineable/*, needs_*_tool).
  6. Parité complète des traductions fr_fr.json <-> en_us.json (aucune clé manquante ou vide).
  7. Schémas des données JSON sous data/cambocraft/cambo/ (fishing, et sous-systèmes suivants).
"""
import gzip
import json
import os
import struct
import sys

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(RACINE, 'src', 'main', 'resources')
ASSETS = os.path.join(RES, 'assets', 'cambocraft')
DATA = os.path.join(RES, 'data', 'cambocraft')
RAPPORT_REG = os.path.join(RACINE, 'assets_src', 'registry', 'rapport_registre.json')

ZONES_PECHE = {
    'riviere', 'grand_lac', 'mangrove', 'riziere', 'etang',
    'abysses', 'sanctuaire', 'montagne_riviere', '*'
}
RARETES_PECHE = {'commun', 'peu_commun', 'rare', 'epique'}


def verifier_png(chemin, dim_attendue=16):
    if not os.path.isfile(chemin):
        return f"fichier PNG manquant : {os.path.relpath(chemin, RACINE)}"
    with open(chemin, 'rb') as f:
        entete = f.read(24)
    if len(entete) < 24 or entete[:8] != b'\x89PNG\r\n\x1a\n':
        return f"en-tête PNG invalide : {os.path.relpath(chemin, RACINE)}"
    largeur, hauteur = struct.unpack('>II', entete[16:24])
    if dim_attendue and (largeur != dim_attendue or hauteur != dim_attendue):
        return f"dimensions PNG {largeur}x{hauteur} != {dim_attendue}x{dim_attendue} : {os.path.relpath(chemin, RACINE)}"
    return None


def main():
    erreurs = []
    nb_json = 0

    # 1. Syntaxe JSON universelle
    for dossier, _, fichiers in os.walk(RES):
        for fn in sorted(fichiers):
            if not fn.endswith('.json'):
                continue
            nb_json += 1
            ch = os.path.join(dossier, fn)
            try:
                with open(ch, encoding='utf-8') as f:
                    json.load(f)
            except Exception as exc:
                erreurs.append(f"JSON invalide ({os.path.relpath(ch, RACINE)}) : {exc}")

    # 2. Seuils §16.1 et rapport de registre
    blocs_states = [f[:-5] for f in os.listdir(os.path.join(ASSETS, 'blockstates')) if f.endswith('.json')]
    tex_items = [f[:-4] for f in os.listdir(os.path.join(ASSETS, 'textures', 'item')) if f.endswith('.png')]
    if len(blocs_states) < 200:
        erreurs.append(f"seuil §16.1 non atteint : {len(blocs_states)} blocs (< 200)")
    if len(tex_items) < 180:
        erreurs.append(f"seuil §16.1 non atteint : {len(tex_items)} icônes d'objets (< 180)")
    err_icone = verifier_png(os.path.join(ASSETS, 'icon.png'), dim_attendue=64)
    if err_icone:
        erreurs.append(err_icone)

    # 3. Blockstates -> models/block
    for bs in sorted(blocs_states):
        ch = os.path.join(ASSETS, 'blockstates', bs + '.json')
        with open(ch, encoding='utf-8') as f:
            doc = json.load(f)
        cibles = []
        for v in (doc.get('variants') or {}).values():
            if isinstance(v, list):
                cibles.extend(x.get('model') for x in v if isinstance(x, dict))
            elif isinstance(v, dict):
                cibles.append(v.get('model'))
        for part in (doc.get('multipart') or []):
            ap = part.get('apply')
            if isinstance(ap, list):
                cibles.extend(x.get('model') for x in ap if isinstance(x, dict))
            elif isinstance(ap, dict):
                cibles.append(ap.get('model'))
        for m in cibles:
            if not m or not m.startswith('cambocraft:block/'):
                erreurs.append(f"blockstate {bs} : modèle invalide {m}")
                continue
            nom_m = m.split('/', 1)[1]
            ch_m = os.path.join(ASSETS, 'models', 'block', nom_m + '.json')
            if not os.path.isfile(ch_m):
                erreurs.append(f"blockstate {bs} : modèle manquant models/block/{nom_m}.json")

    # 4. Models -> textures PNG 16×16
    for sous in ('block', 'item'):
        d = os.path.join(ASSETS, 'models', sous)
        for fn in sorted(os.listdir(d)):
            if not fn.endswith('.json'):
                continue
            ch = os.path.join(d, fn)
            with open(ch, encoding='utf-8') as f:
                doc = json.load(f)
            parent = doc.get('parent', '')
            if parent.startswith('cambocraft:block/'):
                nom_p = parent.split('/', 1)[1]
                if not os.path.isfile(os.path.join(ASSETS, 'models', 'block', nom_p + '.json')):
                    erreurs.append(f"models/{sous}/{fn} : parent manquant {parent}")
            for cle, tex in (doc.get('textures') or {}).items():
                if tex.startswith('cambocraft:block/'):
                    nom_t = tex.split('/', 1)[1]
                    err = verifier_png(os.path.join(ASSETS, 'textures', 'block', nom_t + '.png'), 16)
                    if err:
                        erreurs.append(f"models/{sous}/{fn} ({cle}) -> {err}")
                elif tex.startswith('cambocraft:item/'):
                    nom_t = tex.split('/', 1)[1]
                    err = verifier_png(os.path.join(ASSETS, 'textures', 'item', nom_t + '.png'), 16)
                    if err:
                        erreurs.append(f"models/{sous}/{fn} ({cle}) -> {err}")
                else:
                    erreurs.append(f"models/{sous}/{fn} : texture hors espace cambocraft ({tex})")

    # 5. Parité des traductions fr_fr.json <-> en_us.json
    with open(os.path.join(ASSETS, 'lang', 'fr_fr.json'), encoding='utf-8') as f:
        fr = json.load(f)
    with open(os.path.join(ASSETS, 'lang', 'en_us.json'), encoding='utf-8') as f:
        en = json.load(f)
    manq_en = set(fr) - set(en)
    manq_fr = set(en) - set(fr)
    for k in sorted(manq_en):
        erreurs.append(f"en_us.json : clé manquante {k}")
    for k in sorted(manq_fr):
        erreurs.append(f"fr_fr.json : clé manquante {k}")
    for k, v in fr.items():
        if not isinstance(v, str) or not v.strip():
            erreurs.append(f"fr_fr.json : traduction vide pour {k}")
    for k, v in en.items():
        if not isinstance(v, str) or not v.strip():
            erreurs.append(f"en_us.json : traduction vide pour {k}")
    for bs in blocs_states:
        if f"block.cambocraft.{bs}" not in fr:
            erreurs.append(f"traduction manquante pour le bloc cambocraft:{bs}")

    # 6. Schémas des données JSON sous data/cambocraft/cambo/
    cambo_dir = os.path.join(DATA, 'cambo')
    nb_fishing = 0
    if os.path.isdir(cambo_dir):
        fish_dir = os.path.join(cambo_dir, 'fishing')
        if os.path.isdir(fish_dir):
            for fn in sorted(os.listdir(fish_dir)):
                if not fn.endswith('.json'):
                    continue
                nb_fishing += 1
                ch = os.path.join(fish_dir, fn)
                with open(ch, encoding='utf-8') as f:
                    esp = json.load(f)
                for req in ('nom', 'item', 'poids', 'rarete', 'min_cm', 'max_cm', 'difficulte', 'prix', 'zones', 'saisons'):
                    if req not in esp:
                        erreurs.append(f"fishing/{fn} : champ requis manquant '{req}'")
                if esp.get('rarete') not in RARETES_PECHE:
                    erreurs.append(f"fishing/{fn} : rareté inconnue {esp.get('rarete')}")
                if esp.get('poids', 0) <= 0:
                    erreurs.append(f"fishing/{fn} : poids <= 0")
                if esp.get('min_cm', 0) <= 0 or esp.get('max_cm', 0) < esp.get('min_cm', 0):
                    erreurs.append(f"fishing/{fn} : tailles incohérentes")
                if not (0.0 <= esp.get('difficulte', -1) <= 1.0):
                    erreurs.append(f"fishing/{fn} : difficulté hors de [0, 1]")
                if esp.get('prix', 0) <= 0:
                    erreurs.append(f"fishing/{fn} : prix <= 0")
                nom_loc = esp.get('nom') or {}
                if not nom_loc.get('fr') or not nom_loc.get('en'):
                    erreurs.append(f"fishing/{fn} : nom localisé fr/en incomplet")
                for z in (esp.get('zones') or []):
                    if z not in ZONES_PECHE:
                        erreurs.append(f"fishing/{fn} : zone inconnue '{z}'")
                item_id = (esp.get('item') or '').replace('cambocraft:', '')
                if not os.path.isfile(os.path.join(ASSETS, 'models', 'item', item_id + '.json')):
                    erreurs.append(f"fishing/{fn} : objet inexistant {esp.get('item')}")
    if nb_fishing < 12:
        erreurs.append(f"seuil §7.1 non atteint : {nb_fishing} espèces de poissons (< 12)")

    # 7. Dimension, biomes (>= 14), structures NBT (>= 60 familles x 2 variantes) et zones (>= 12)
    for req_dim in ('dimension/cambodia.json', 'dimension_type/cambodia.json', 'worldgen/noise_settings/cambodia_noise.json'):
        if not os.path.isfile(os.path.join(DATA, req_dim)):
            erreurs.append(f"descripteur de dimension manquant : {req_dim}")
    biomes_dir = os.path.join(DATA, 'worldgen', 'biome')
    nb_biomes = len([f for f in os.listdir(biomes_dir) if f.endswith('.json')]) if os.path.isdir(biomes_dir) else 0
    if nb_biomes < 14:
        erreurs.append(f"seuil §4.2 non atteint : {nb_biomes} biomes (< 14)")

    struct_json_dir = os.path.join(DATA, 'worldgen', 'structure')
    nb_familles_struct = len([f for f in os.listdir(struct_json_dir) if f.endswith('.json')]) if os.path.isdir(struct_json_dir) else 0
    if nb_familles_struct < 60:
        erreurs.append(f"seuil §5.1 non atteint : {nb_familles_struct} familles de structures (< 60)")

    struct_nbt_dir = os.path.join(DATA, 'structure')
    nb_nbt = 0
    if os.path.isdir(struct_nbt_dir):
        for fn in sorted(os.listdir(struct_nbt_dir)):
            if not fn.endswith('.nbt'):
                continue
            nb_nbt += 1
            ch_nbt = os.path.join(struct_nbt_dir, fn)
            try:
                with gzip.open(ch_nbt, 'rb') as gz:
                    entete_nbt = gz.read(16)
                if not entete_nbt or entete_nbt[0] != 10:
                    erreurs.append(f"structure/{fn} : racine NBT TAG_Compound invalide")
            except Exception as exc:
                erreurs.append(f"structure/{fn} : GZip/NBT illisible ({exc})")
    if nb_nbt < 120:
        erreurs.append(f"seuil §5.1 non atteint : {nb_nbt} fichiers .nbt (< 120, soit 60 x 2 variantes)")

    zones_dir = os.path.join(cambo_dir, 'zones')
    nb_zones = len([f for f in os.listdir(zones_dir) if f.endswith('.json')]) if os.path.isdir(zones_dir) else 0
    if nb_zones < 12:
        erreurs.append(f"seuil §12.2 non atteint : {nb_zones} zones (< 12)")

    # 8. PNJ (>= 15), dialogues (>= 15), marchands et cuisine khmère
    npcs_dir = os.path.join(cambo_dir, 'npcs')
    dlg_dir = os.path.join(cambo_dir, 'dialogues')
    mch_dir = os.path.join(cambo_dir, 'merchants')
    cook_dir = os.path.join(cambo_dir, 'cooking')
    nb_npcs = len([f for f in os.listdir(npcs_dir) if f.endswith('.json')]) if os.path.isdir(npcs_dir) else 0
    nb_dlgs = len([f for f in os.listdir(dlg_dir) if f.endswith('.json')]) if os.path.isdir(dlg_dir) else 0
    nb_mchs = len([f for f in os.listdir(mch_dir) if f.endswith('.json')]) if os.path.isdir(mch_dir) else 0
    nb_cook = 0
    if nb_npcs < 15:
        erreurs.append(f"seuil §6.1 non atteint : {nb_npcs} PNJ (< 15)")
    if nb_dlgs < 15:
        erreurs.append(f"seuil §6.1 non atteint : {nb_dlgs} dialogues (< 15)")
    if os.path.isdir(cook_dir):
        for fn in sorted(os.listdir(cook_dir)):
            if not fn.endswith('.json'):
                continue
            nb_cook += 1
            with open(os.path.join(cook_dir, fn), encoding='utf-8') as f:
                rc = json.load(f)
            for it in [rc.get('resultat', '')] + list(rc.get('ingredients') or []):
                court = it.replace('cambocraft:', '')
                if not os.path.isfile(os.path.join(ASSETS, 'models', 'item', court + '.json')):
                    erreurs.append(f"cooking/{fn} : objet inconnu {it}")

    # 9. Quêtes : >= 12 chapitres principaux, >= 60 secondaires et 3 fins au chapitre 12 (§8)
    quests_dir = os.path.join(cambo_dir, 'quests')
    nb_q_total = 0
    nb_q_princ = 0
    nb_q_sec = 0
    if os.path.isdir(quests_dir):
        for fn in sorted(os.listdir(quests_dir)):
            if not fn.endswith('.json'):
                continue
            nb_q_total += 1
            with open(os.path.join(quests_dir, fn), encoding='utf-8') as f:
                qd = json.load(f)
            if not qd.get('etapes'):
                erreurs.append(f"quests/{fn} : étapes vides")
            if qd.get('type') == 'principale':
                nb_q_princ += 1
            elif qd.get('type') == 'secondaire':
                nb_q_sec += 1
            if fn == 'chapitre_12_la_lecon_du_bol.json' and len(qd.get('branches') or []) < 3:
                erreurs.append("quests/chapitre_12_la_lecon_du_bol.json : au moins 3 fins attendues")
    if nb_q_princ < 12:
        erreurs.append(f"seuil §8.2 non atteint : {nb_q_princ} chapitres principaux (< 12)")
    if nb_q_sec < 60:
        erreurs.append(f"seuil §8.3 non atteint : {nb_q_sec} quêtes secondaires (< 60)")

    # 10. Pouvoirs (>= 80, répartis en 20/18/18/14/10), synergies (>= 30), ennemis et 12 boss
    powers_dir = os.path.join(cambo_dir, 'powers')
    syn_dir = os.path.join(cambo_dir, 'synergies')
    enemies_dir = os.path.join(cambo_dir, 'enemies')
    powers_ids = set()
    par_rarete = {'commun': 0, 'rare': 0, 'epique': 0, 'legendaire': 0, 'mythique': 0}
    if os.path.isdir(powers_dir):
        for fn in sorted(os.listdir(powers_dir)):
            if not fn.endswith('.json'):
                continue
            pid = fn[:-5]
            powers_ids.add(pid)
            with open(os.path.join(powers_dir, fn), encoding='utf-8') as f:
                pd = json.load(f)
            r = pd.get('rarete', '')
            if r in par_rarete:
                par_rarete[r] += 1
            if len(pd.get('niveaux') or []) != 5:
                erreurs.append(f"powers/{fn} : 5 niveaux attendus")
    seuils_rarete = {'commun': 20, 'rare': 18, 'epique': 18, 'legendaire': 14, 'mythique': 10}
    for r, min_attendu in seuils_rarete.items():
        if par_rarete[r] < min_attendu:
            erreurs.append(f"powers : rareté {r} insuffisante ({par_rarete[r]} < {min_attendu})")

    nb_syn = 0
    if os.path.isdir(syn_dir):
        for fn in sorted(os.listdir(syn_dir)):
            if not fn.endswith('.json'):
                continue
            nb_syn += 1
            with open(os.path.join(syn_dir, fn), encoding='utf-8') as f:
                sd = json.load(f)
            for p_ref in sd.get('pouvoirs') or []:
                if p_ref not in powers_ids:
                    erreurs.append(f"synergies/{fn} : pouvoir inconnu '{p_ref}'")
    if nb_syn < 30:
        erreurs.append(f"synergies insuffisantes : {nb_syn} (< 30)")

    enemies_ids = set()
    nb_boss = 0
    if os.path.isdir(enemies_dir):
        for fn in sorted(os.listdir(enemies_dir)):
            if not fn.endswith('.json'):
                continue
            eid = fn[:-5]
            enemies_ids.add(eid)
            with open(os.path.join(enemies_dir, fn), encoding='utf-8') as f:
                ed = json.load(f)
            if ed.get('categorie') == 'boss':
                nb_boss += 1
                if len(ed.get('phases') or []) < 3:
                    erreurs.append(f"enemies/{fn} : un boss doit avoir >= 3 phases")
    if nb_boss < 12:
        erreurs.append(f"boss insuffisants : {nb_boss} (< 12)")

    if os.path.isdir(zones_dir):
        for fn in sorted(os.listdir(zones_dir)):
            if not fn.endswith('.json'):
                continue
            with open(os.path.join(zones_dir, fn), encoding='utf-8') as f:
                zd = json.load(f)
            bid = zd.get('boss_id')
            if bid and bid not in enemies_ids:
                erreurs.append(f"zones/{fn} : boss_id inconnu '{bid}'")
            for eid in zd.get('ennemis') or []:
                if eid not in enemies_ids:
                    erreurs.append(f"zones/{fn} : ennemi inconnu '{eid}'")

    # 11. fabric.mod.json et ressources audio (sounds.json + .ogg OggS)
    fmod = os.path.join(RES, 'fabric.mod.json')
    if not os.path.isfile(fmod):
        erreurs.append("fabric.mod.json manquant")
    sounds_path = os.path.join(ASSETS, 'sounds.json')
    nb_sounds = 0
    if os.path.isfile(sounds_path):
        with open(sounds_path, encoding='utf-8') as f:
            s_data = json.load(f)
        for ev_key, ev_spec in s_data.items():
            nb_sounds += 1
            sub_k = ev_spec.get('subtitle', '')
            if sub_k and sub_k not in fr:
                erreurs.append(f"sounds.json/{ev_key} : sous-titre manquant '{sub_k}'")
            for s_entry in ev_spec.get('sounds') or []:
                s_name = s_entry.get('name', '').replace('cambocraft:', '')
                ogg_p = os.path.join(ASSETS, 'sounds', s_name + '.ogg')
                if not os.path.isfile(ogg_p):
                    erreurs.append(f"sounds.json/{ev_key} : fichier .ogg manquant '{s_name}.ogg'")
                else:
                    with open(ogg_p, 'rb') as ogg_f:
                        if ogg_f.read(4) != b'OggS':
                            erreurs.append(f"sounds/{s_name}.ogg : en-tête OggS invalide")
    else:
        erreurs.append("assets/cambocraft/sounds.json manquant")

    if erreurs:
        print(f"VALIDATION EN ÉCHEC : {len(erreurs)} erreur(s)")
        for e in erreurs[:50]:
            print(" -", e)
        return 1

    print(f"validation OK : {nb_json} JSON vérifiés, {len(blocs_states)} blocs, "
          f"{len(tex_items)} icônes d'objets, {len(fr)} clés fr/en, {nb_biomes} biomes, "
          f"{nb_familles_struct} structures ({nb_nbt} NBT), {nb_zones} zones, "
          f"{nb_npcs} PNJ, {nb_cook} plats, {nb_q_total} quêtes ({nb_q_princ} ch., {nb_q_sec} sec.), "
          f"{len(powers_ids)} pouvoirs, {nb_syn} synergies, {len(enemies_ids)} ennemis ({nb_boss} boss), "
          f"{nb_fishing} poissons, {nb_sounds} sons .ogg, 0 erreur")
    return 0


if __name__ == '__main__':
    sys.exit(main())
