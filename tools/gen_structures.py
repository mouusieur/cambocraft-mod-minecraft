#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Générateur paramétrique des structures NBT et des ensembles Jigsaw de Cambocraft (§5 & §15.3 #1).

Construit 64 familles de structures × 2 variantes chacune = 128 fichiers .nbt compressés GZip
au format officiel Minecraft Structure 1.21.1 (DataVersion 3953), ainsi que les descripteurs
worldgen/structure, worldgen/structure_set, worldgen/template_pool et tags de biomes.
"""
import gzip
import io
import json
import os
import struct
import sys

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(RACINE, 'src', 'main', 'resources', 'data', 'cambocraft')
STRUCT_DIR = os.path.join(DATA, 'structure')
MANIFEST = os.path.join(RACINE, 'assets_src', 'structures', 'manifest_structures.json')
DATA_VERSION = 3953  # Minecraft 1.21.1

# --- Encodeur binaire NBT (sans dépendance externe) ------------------------
TAG_END = 0
TAG_INT = 3
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10


def _write_str(buf, s):
    b = s.encode('utf-8')
    buf.write(struct.pack('>H', len(b)))
    buf.write(b)


def _write_tag_header(buf, tag_type, name):
    buf.write(struct.pack('>B', tag_type))
    _write_str(buf, name)


def _write_payload(buf, kind, val):
    if kind == TAG_INT:
        buf.write(struct.pack('>i', int(val)))
    elif kind == TAG_STRING:
        _write_str(buf, str(val))
    elif kind == TAG_LIST:
        elem_type, items = val
        buf.write(struct.pack('>Bi', elem_type, len(items)))
        for it in items:
            _write_payload(buf, elem_type, it)
    elif kind == TAG_COMPOUND:
        for k, (t, v) in val.items():
            _write_tag_header(buf, t, k)
            _write_payload(buf, t, v)
        buf.write(struct.pack('>B', TAG_END))


def encoder_structure_nbt(taille, palette, blocs):
    """Encode une structure Minecraft (size, palette, blocks, entities, DataVersion) en GZip NBT."""
    pal_items = []
    for entree in palette:
        comp = {'Name': (TAG_STRING, entree['Name'])}
        if entree.get('Properties'):
            props = {pk: (TAG_STRING, str(pv)) for pk, pv in entree['Properties'].items()}
            comp['Properties'] = (TAG_COMPOUND, props)
        pal_items.append(comp)

    blk_items = []
    for pos, state_idx in blocs:
        blk_items.append({
            'pos': (TAG_LIST, (TAG_INT, [pos[0], pos[1], pos[2]])),
            'state': (TAG_INT, state_idx),
        })

    racine = {
        'DataVersion': (TAG_INT, DATA_VERSION),
        'size': (TAG_LIST, (TAG_INT, list(taille))),
        'palette': (TAG_LIST, (TAG_COMPOUND, pal_items)),
        'blocks': (TAG_LIST, (TAG_COMPOUND, blk_items)),
        'entities': (TAG_LIST, (TAG_COMPOUND, [])),
    }

    raw = io.BytesIO()
    _write_tag_header(raw, TAG_COMPOUND, '')
    _write_payload(raw, TAG_COMPOUND, racine)
    out = io.BytesIO()
    with gzip.GzipFile(filename='', mode='wb', fileobj=out, mtime=0) as gz:
        gz.write(raw.getvalue())
    return out.getvalue()


# --- Catalogue des 64 familles architecturales -----------------------------
STRUCTURES = [
    # (id, categorie, taille_xyz, base_blk, mur_blk, toit_blk, decor_blk, biome_cible, espacement)
    ('prasat_petit', 'prasat', (7, 9, 7), 'cambocraft:laterite', 'cambocraft:gres_khmer', 'cambocraft:gres_sculpte', 'cambocraft:autel', 'jungle_en_ruines', 20),
    ('prasat_moyen', 'prasat', (9, 12, 9), 'cambocraft:brique_de_laterite', 'cambocraft:gres_khmer', 'cambocraft:gres_sculpte', 'cambocraft:statue_de_pierre', 'jungle_en_ruines', 24),
    ('prasat_grand', 'prasat', (11, 15, 11), 'cambocraft:brique_de_laterite', 'cambocraft:gres_khmer_poli', 'cambocraft:gres_sculpte', 'cambocraft:cloche_de_temple', 'foret_de_kapokiers', 28),
    ('pyramide_temple_petit', 'pyramide', (9, 8, 9), 'cambocraft:laterite', 'cambocraft:brique_de_laterite', 'cambocraft:gres_khmer', 'cambocraft:dalle_de_pression_ancienne', 'jungle_en_ruines', 24),
    ('pyramide_temple_moyen', 'pyramide', (11, 10, 11), 'cambocraft:laterite', 'cambocraft:gres_khmer', 'cambocraft:gres_sculpte', 'cambocraft:dalle_de_pression_ancienne', 'foret_de_kapokiers', 28),
    ('pyramide_temple_grand', 'pyramide', (13, 12, 13), 'cambocraft:brique_de_laterite', 'cambocraft:gres_khmer_poli', 'cambocraft:pierre_du_lotus_sculptee', 'cambocraft:dalle_de_pression_ancienne', 'jungle_en_ruines', 32),
    ('tour_visage_nord', 'tour_visage', (7, 11, 7), 'cambocraft:gres_khmer', 'cambocraft:gres_sculpte', 'cambocraft:visage_de_pierre', 'cambocraft:encens', 'jungle_en_ruines', 22),
    ('tour_visage_sud', 'tour_visage', (7, 11, 7), 'cambocraft:pierre_moussue_de_temple', 'cambocraft:gres_khmer', 'cambocraft:visage_de_pierre', 'cambocraft:encens', 'foret_de_kapokiers', 22),
    ('galerie_bas_reliefs_est', 'galerie', (11, 6, 5), 'cambocraft:gres_khmer', 'cambocraft:bas_relief', 'cambocraft:gres_khmer_slab', 'cambocraft:torche_de_resine', 'jungle_en_ruines', 20),
    ('galerie_bas_reliefs_ouest', 'galerie', (11, 6, 5), 'cambocraft:brique_de_temple', 'cambocraft:bas_relief', 'cambocraft:brique_de_temple_slab', 'cambocraft:lanterne_de_bambou', 'foret_de_kapokiers', 20),
    ('chaussee_naga_courte', 'naga', (9, 4, 5), 'cambocraft:laterite', 'cambocraft:naga_balustrade', 'cambocraft:gres_khmer_slab', 'cambocraft:lion_de_pierre', 'riviere_du_tonle', 18),
    ('chaussee_naga_longue', 'naga', (13, 4, 5), 'cambocraft:brique_de_laterite', 'cambocraft:naga_balustrade', 'cambocraft:pierre_du_lotus_slab', 'cambocraft:lion_de_pierre', 'sanctuaire_de_lotus', 22),
    ('entree_lions_gardiens', 'lions', (7, 5, 5), 'cambocraft:gres_khmer_poli', 'cambocraft:pilier_de_laterite', 'cambocraft:gres_sculpte', 'cambocraft:lion_de_pierre', 'jungle_en_ruines', 18),
    ('parvis_lions_sacres', 'lions', (9, 5, 7), 'cambocraft:pierre_du_lotus_polie', 'cambocraft:pilier_de_lotus', 'cambocraft:pierre_du_lotus_sculptee', 'cambocraft:lion_de_pierre', 'sanctuaire_de_lotus', 20),
    ('wat_village', 'wat', (11, 9, 9), 'cambocraft:sol_de_temple', 'cambocraft:marbre_blanc', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:cloche_de_temple', 'rizieres_en_terrasses', 22),
    ('wat_monastere', 'wat', (13, 10, 11), 'cambocraft:sol_de_temple', 'cambocraft:marbre_blanc', 'cambocraft:tuile_vernissee', 'cambocraft:gong', 'foret_tropicale', 26),
    ('maison_pilotis_riziculteur', 'pilotis', (7, 8, 7), 'cambocraft:teck_log', 'cambocraft:teck_planks', 'cambocraft:toit_de_chaume', 'cambocraft:natte_de_jonc', 'rizieres_en_terrasses', 16),
    ('maison_pilotis_pecheur', 'pilotis', (7, 8, 7), 'cambocraft:paletuvier_log', 'cambocraft:bambou_planches_bloc', 'cambocraft:toit_de_chaume', 'cambocraft:panier', 'riviere_du_tonle', 16),
    ('maison_pilotis_tisserand', 'pilotis', (8, 8, 7), 'cambocraft:manguier_log', 'cambocraft:manguier_planks', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:metier_a_tisser', 'plaine_inondable', 16),
    ('maison_pilotis_forgeron', 'pilotis', (8, 8, 7), 'cambocraft:teck_log', 'cambocraft:laterite', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:table_de_forge', 'savane_claire', 18),
    ('maison_pilotis_cuisinier', 'pilotis', (7, 8, 7), 'cambocraft:cocotier_log', 'cambocraft:cocotier_planks', 'cambocraft:toit_de_chaume', 'cambocraft:marmite', 'rizieres_en_terrasses', 16),
    ('maison_pilotis_chef', 'pilotis', (9, 9, 9), 'cambocraft:teck_log', 'cambocraft:teck_planks', 'cambocraft:tuile_vernissee', 'cambocraft:tapis_de_soie_krama', 'rizieres_en_terrasses', 20),
    ('marche_couvert_village', 'marche', (11, 6, 9), 'cambocraft:terre_cuite', 'cambocraft:bambou_bloc', 'cambocraft:toit_de_chaume', 'cambocraft:etal_de_marche', 'rizieres_en_terrasses', 20),
    ('marche_de_nuit_lanternes', 'marche', (11, 6, 9), 'cambocraft:pierre_du_lotus_polie', 'cambocraft:teck_log', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:lanterne_de_bambou', 'riviere_du_tonle', 22),
    ('riziere_en_casiers', 'riziere', (11, 3, 11), 'cambocraft:boue_de_riziaire', 'cambocraft:terre_lateritique', 'cambocraft:riz_sauvage', 'cambocraft:jarre', 'rizieres_en_terrasses', 14),
    ('riziere_en_terrasse_irriguee', 'riziere', (11, 4, 11), 'cambocraft:boue_de_riziaire', 'cambocraft:laterite', 'cambocraft:riz_sauvage', 'cambocraft:riz_empile', 'plaine_inondable', 14),
    ('ferme_de_soie_muriers', 'soie', (9, 6, 9), 'cambocraft:terre_lateritique', 'cambocraft:bambou_fence', 'cambocraft:toit_de_chaume', 'cambocraft:cuve_de_soie', 'foret_tropicale', 18),
    ('atelier_tisserand_soie', 'soie', (9, 7, 8), 'cambocraft:manguier_planks', 'cambocraft:bambou_tresse', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:metier_a_tisser', 'rizieres_en_terrasses', 18),
    ('forteresse_ombre_bastion', 'ombre', (11, 10, 11), 'cambocraft:ardoise_des_ombres', 'cambocraft:brique_des_ombres', 'cambocraft:pierre_des_ombres_runique', 'cambocraft:banniere_violette', 'plateau_de_laterite', 26),
    ('forteresse_ombre_donjon', 'ombre', (13, 13, 13), 'cambocraft:brique_des_ombres', 'cambocraft:ardoise_des_ombres', 'cambocraft:pierre_des_ombres_runique', 'cambocraft:tambour', 'pinedes_seches', 30),
    ('avant_poste_ombre', 'ombre', (7, 8, 7), 'cambocraft:ardoise_des_ombres', 'cambocraft:bambou_bloc', 'cambocraft:planche_de_toit', 'cambocraft:torche_de_resine', 'bambouseraie', 20),
    ('dojo_souterrain_ombre', 'ombre', (9, 6, 9), 'cambocraft:brique_des_ombres', 'cambocraft:ardoise_des_ombres_wall', 'cambocraft:pierre_des_ombres_runique', 'cambocraft:gong', 'bambouseraie', 22),
    ('pont_bambou_riviere', 'pont', (11, 5, 5), 'cambocraft:bambou_bloc', 'cambocraft:bambou_fence', 'cambocraft:bambou_planches_slab', 'cambocraft:lanterne_de_bambou', 'riviere_du_tonle', 16),
    ('pont_laterite_naga', 'pont', (13, 6, 5), 'cambocraft:brique_de_laterite', 'cambocraft:naga_balustrade', 'cambocraft:gres_khmer_slab', 'cambocraft:lion_de_pierre', 'riviere_du_tonle', 20),
    ('puits_laterite_village', 'puits', (5, 6, 5), 'cambocraft:brique_de_laterite', 'cambocraft:laterite_wall', 'cambocraft:toit_de_chaume', 'cambocraft:jarre', 'rizieres_en_terrasses', 16),
    ('puits_sacree_lotus', 'puits', (5, 6, 5), 'cambocraft:pierre_du_lotus_polie', 'cambocraft:pierre_du_lotus_wall', 'cambocraft:pierre_du_lotus_slab', 'cambocraft:fleur_de_lotus', 'sanctuaire_de_lotus', 18),
    ('sanctuaire_lotus_bassin', 'sanctuaire', (11, 7, 11), 'cambocraft:pierre_du_lotus', 'cambocraft:pilier_de_lotus', 'cambocraft:pierre_du_lotus_sculptee', 'cambocraft:cristal_de_lotus', 'sanctuaire_de_lotus', 22),
    ('sanctuaire_lotus_pavillon', 'sanctuaire', (9, 9, 9), 'cambocraft:pierre_du_lotus_polie', 'cambocraft:marbre_blanc', 'cambocraft:tuile_vernissee', 'cambocraft:teleporteur_de_lotus', 'sanctuaire_de_lotus', 24),
    ('autel_du_maitre_du_nen', 'sanctuaire', (9, 8, 9), 'cambocraft:pierre_du_lotus_sculptee', 'cambocraft:pilier_de_lotus', 'cambocraft:bloc_de_lotus', 'cambocraft:socle_d_offrande', 'sanctuaire_de_lotus', 28),
    ('pagode_montagne_karst', 'montagne', (9, 11, 9), 'cambocraft:calcaire_karstique', 'cambocraft:pin_du_cambodge_planks', 'cambocraft:tuile_vernissee', 'cambocraft:cloche_de_temple', 'montagnes_karstiques', 24),
    ('ermitage_des_nuages', 'montagne', (7, 9, 7), 'cambocraft:calcaire_karstique', 'cambocraft:bambou_tresse', 'cambocraft:toit_de_chaume', 'cambocraft:encens', 'montagnes_karstiques', 22),
    ('pagode_flottante_tonle', 'lac', (9, 8, 9), 'cambocraft:bambou_bloc', 'cambocraft:paletuvier_planks', 'cambocraft:toit_de_chaume', 'cambocraft:lanterne_de_bambou', 'grand_lac', 20),
    ('temple_englouti_du_lac', 'lac', (11, 9, 11), 'cambocraft:pierre_moussue_de_temple', 'cambocraft:gres_khmer', 'cambocraft:gres_sculpte', 'cambocraft:cristal_de_lotus', 'grand_lac', 24),
    ('ruine_jungle_1_borne', 'ruine', (5, 5, 5), 'cambocraft:pierre_erodee', 'cambocraft:pierre_moussue_de_temple', 'cambocraft:gres_sculpte', 'cambocraft:racine_de_banyan', 'jungle_en_ruines', 14),
    ('ruine_jungle_2_arche', 'ruine', (7, 6, 5), 'cambocraft:pierre_erodee', 'cambocraft:brique_de_temple', 'cambocraft:pierre_moussue_de_temple_slab', 'cambocraft:racine_de_banyan', 'jungle_en_ruines', 16),
    ('ruine_jungle_3_cour', 'ruine', (9, 6, 9), 'cambocraft:pierre_moussue_de_temple', 'cambocraft:gres_khmer', 'cambocraft:gres_sculpte', 'cambocraft:statue_de_pierre', 'foret_tropicale', 18),
    ('ruine_jungle_4_sanctuaire', 'ruine', (11, 8, 11), 'cambocraft:brique_de_laterite', 'cambocraft:pierre_moussue_de_temple', 'cambocraft:visage_de_pierre', 'cambocraft:autel', 'jungle_en_ruines', 20),
    ('ruine_jungle_5_complexe', 'ruine', (13, 9, 13), 'cambocraft:laterite', 'cambocraft:pierre_moussue_de_temple', 'cambocraft:gres_sculpte', 'cambocraft:bas_relief', 'foret_de_kapokiers', 24),
    ('embarcadere_pirogue_village', 'embarcadere', (9, 5, 7), 'cambocraft:teck_log', 'cambocraft:teck_planks', 'cambocraft:teck_slab', 'cambocraft:lanterne_de_bambou', 'riviere_du_tonle', 16),
    ('embarcadere_course_royale', 'embarcadere', (11, 6, 7), 'cambocraft:teck_log', 'cambocraft:teck_planks', 'cambocraft:banniere_or', 'cambocraft:tambour', 'grand_lac', 20),
    ('pyramide_de_la_renaissance', 'renaissance', (13, 14, 13), 'cambocraft:pierre_du_lotus_polie', 'cambocraft:pierre_du_lotus_sculptee', 'cambocraft:bloc_de_lotus', 'cambocraft:coeur_du_portail', 'sanctuaire_de_lotus', 36),
    ('ile_flottante_garuda_mineure', 'garuda', (9, 8, 9), 'cambocraft:calcaire_karstique', 'cambocraft:gres_khmer_poli', 'cambocraft:garuda', 'cambocraft:cristal_de_lotus', 'iles_flottantes_des_garudas', 20),
    ('ile_flottante_garuda_arene', 'garuda', (13, 10, 13), 'cambocraft:gres_khmer_poli', 'cambocraft:marbre_blanc', 'cambocraft:garuda', 'cambocraft:teleporteur_de_lotus', 'iles_flottantes_des_garudas', 28),
    ('arene_pradal_serey', 'arene', (13, 7, 13), 'cambocraft:terre_lateritique', 'cambocraft:teck_fence', 'cambocraft:banniere_rouge', 'cambocraft:gong', 'rizieres_en_terrasses', 24),
    ('village_tonle_place', 'village_jigsaw', (11, 6, 11), 'cambocraft:terre_cuite', 'cambocraft:teck_planks', 'cambocraft:toit_de_chaume', 'cambocraft:teleporteur_de_lotus', 'rizieres_en_terrasses', 20),
    ('village_ly_place', 'village_jigsaw', (11, 6, 11), 'cambocraft:brique_de_laterite', 'cambocraft:palmier_a_sucre_planks', 'cambocraft:toit_de_chaume', 'cambocraft:teleporteur_de_lotus', 'plaine_inondable', 20),
    ('village_srok_place', 'village_jigsaw', (11, 6, 11), 'cambocraft:bambou_planches_bloc', 'cambocraft:bambou_bloc', 'cambocraft:toit_de_chaume', 'cambocraft:teleporteur_de_lotus', 'bambouseraie', 20),
    ('village_kompong_place', 'village_jigsaw', (11, 6, 11), 'cambocraft:gres_khmer', 'cambocraft:kapokier_planks', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:teleporteur_de_lotus', 'foret_tropicale', 20),
    ('chemin_laterite_lanternes', 'village_jigsaw', (9, 4, 5), 'cambocraft:laterite', 'cambocraft:bambou_fence', 'cambocraft:lanterne_de_bambou', 'cambocraft:jarre', 'rizieres_en_terrasses', 14),
    ('grenier_a_riz_pilotis', 'village_jigsaw', (7, 8, 7), 'cambocraft:teck_log', 'cambocraft:bambou_tresse', 'cambocraft:toit_de_chaume', 'cambocraft:riz_empile', 'rizieres_en_terrasses', 16),
    ('enclos_buffles_d_eau', 'village_jigsaw', (9, 4, 9), 'cambocraft:boue_de_riziaire', 'cambocraft:teck_fence', 'cambocraft:paille_de_riz', 'cambocraft:jarre', 'plaine_inondable', 16),
    ('four_potier_village', 'village_jigsaw', (7, 6, 7), 'cambocraft:terre_cuite', 'cambocraft:brique_de_laterite', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:tour_de_poterie', 'rizieres_en_terrasses', 16),
    ('forge_villageoise', 'village_jigsaw', (8, 6, 8), 'cambocraft:laterite', 'cambocraft:brique_de_laterite', 'cambocraft:tuile_de_terre_cuite', 'cambocraft:four_de_forge', 'savane_claire', 18),
    ('pavillon_du_conteur', 'village_jigsaw', (7, 7, 7), 'cambocraft:teck_planks', 'cambocraft:teck_fence', 'cambocraft:tuile_vernissee', 'cambocraft:encens', 'rizieres_en_terrasses', 18),
]


def construire_blocs(categorie, taille, base_blk, mur_blk, toit_blk, decor_blk, variante):
    """Construit la palette et la liste des blocs (x,y,z) selon l'archétype architectural."""
    sx, sy, sz = taille
    # En variante 2, on alterne les ornements et l'agencement intérieur pour garantir 2 variantes distinctes
    alt_mur = toit_blk if variante == 2 and categorie in ('ruine', 'prasat') else mur_blk
    palette = [
        {'Name': 'minecraft:air'},
        {'Name': base_blk},
        {'Name': alt_mur},
        {'Name': toit_blk},
        {'Name': decor_blk},
        {'Name': 'cambocraft:lanterne_de_bambou'},
        {'Name': 'minecraft:chest'},
    ]
    blocs = []
    cx, cz = sx // 2, sz // 2

    if categorie == 'pilotis':
        # Pilotis aux 4 coins et au centre (y=0..1), plancher (y=2), murs ajourés (y=3..5), toit (y=6..sy-1)
        for x in (0, sx - 1, cx):
            for z in (0, sz - 1, cz):
                for y in (0, 1):
                    blocs.append(((x, y, z), 1))
        for x in range(sx):
            for z in range(sz):
                blocs.append(((x, 2, z), 2))
                if x in (0, sx - 1) or z in (0, sz - 1):
                    if not (x == cx and z == 0):
                        for y in range(3, min(6, sy - 1)):
                            blocs.append(((x, y, z), 2))
                blocs.append(((x, sy - 1, z), 3))
        blocs.append(((cx, 3, cz), 4))
        blocs.append(((1 + (variante - 1), 3, 1), 5))
    elif categorie in ('pyramide', 'renaissance', 'prasat', 'tour_visage'):
        # Gradins concentriques + chambre centrale avec dalle d'énigme et coffre
        niveaux = min(sy - 2, sx // 2)
        for y in range(niveaux):
            marge = y // 2
            for x in range(marge, sx - marge):
                for z in range(marge, sz - marge):
                    if y == 0 or x in (marge, sx - 1 - marge) or z in (marge, sz - 1 - marge):
                        blocs.append(((x, y, z), 1 if y < 2 else 2))
        blocs.append(((cx, 1, cz), 4))
        blocs.append(((cx + (1 if variante == 1 else -1), 1, cz), 6))
        blocs.append(((cx, sy - 1, cz), 3))
    else:
        # Socle plein, piliers d'angle, couronnement et mobilier central
        for x in range(sx):
            for z in range(sz):
                blocs.append(((x, 0, z), 1))
                if (x in (0, sx - 1) and z in (0, sz - 1)) and sy > 3:
                    for y in range(1, sy - 1):
                        blocs.append(((x, y, z), 2))
                if sy > 4 and 1 <= x <= sx - 2 and 1 <= z <= sz - 2:
                    blocs.append(((x, sy - 1, z), 3))
        blocs.append(((cx, 1, cz), 4))
        blocs.append(((max(1, cx - 1), 1, max(1, cz - (variante - 1))), 5))
    return palette, blocs


def ecrire_json(chemin, contenu):
    os.makedirs(os.path.dirname(chemin), exist_ok=True)
    with open(chemin, 'w', encoding='utf-8') as f:
        json.dump(contenu, f, indent=2, ensure_ascii=False, sort_keys=True)
        f.write('\n')


def main():
    os.makedirs(STRUCT_DIR, exist_ok=True)
    nb_nbt = 0
    manifest_entrees = []

    for idx, (ident, cat, taille, base_b, mur_b, toit_b, decor_b, biome_cible, espacement) in enumerate(STRUCTURES, start=1):
        variantes_nbt = []
        for var in (1, 2):
            nom_var = f"{ident}_v{var}"
            palette, blocs = construire_blocs(cat, taille, base_b, mur_b, toit_b, decor_b, var)
            octets = encoder_structure_nbt(taille, palette, blocs)
            ch_nbt = os.path.join(STRUCT_DIR, nom_var + '.nbt')
            with open(ch_nbt, 'wb') as f:
                f.write(octets)
            nb_nbt += 1
            variantes_nbt.append(f"cambocraft:{nom_var}")

        # Template pool Jigsaw avec les 2 variantes
        ecrire_json(os.path.join(DATA, 'worldgen', 'template_pool', ident + '.json'), {
            'fallback': 'minecraft:empty',
            'elements': [
                {
                    'weight': 1,
                    'element': {
                        'element_type': 'minecraft:single_pool_element',
                        'location': f"cambocraft:{ident}_v1",
                        'processors': 'minecraft:empty',
                        'projection': 'rigid',
                    },
                },
                {
                    'weight': 1,
                    'element': {
                        'element_type': 'minecraft:single_pool_element',
                        'location': f"cambocraft:{ident}_v2",
                        'processors': 'minecraft:empty',
                        'projection': 'rigid',
                    },
                },
            ],
        })

        # Tag de biome pour cette structure
        ecrire_json(os.path.join(DATA, 'tags', 'worldgen', 'biome', 'has_structure', ident + '.json'), {
            'replace': False,
            'values': [f"cambocraft:{biome_cible}"],
        })

        # Descripteur worldgen/structure
        ecrire_json(os.path.join(DATA, 'worldgen', 'structure', ident + '.json'), {
            'type': 'minecraft:jigsaw',
            'biomes': f"#cambocraft:has_structure/{ident}",
            'step': 'surface_structures',
            'spawn_overrides': {},
            'terrain_adaptation': 'beard_thin',
            'start_pool': f"cambocraft:{ident}",
            'size': 2 if cat == 'village_jigsaw' else 1,
            'start_height': {'absolute': 0},
            'project_start_to_heightmap': 'WORLD_SURFACE_WG',
            'max_distance_from_center': 80,
            'use_expansion_hack': False,
        })

        # Structure set
        ecrire_json(os.path.join(DATA, 'worldgen', 'structure_set', ident + '.json'), {
            'structures': [{'structure': f"cambocraft:{ident}", 'weight': 1}],
            'placement': {
                'type': 'minecraft:random_spread',
                'salt': 1042000 + idx,
                'spacing': espacement,
                'separation': max(4, espacement // 3),
            },
        })

        manifest_entrees.append({
            'id': ident,
            'categorie': cat,
            'taille': list(taille),
            'biome': biome_cible,
            'variantes': variantes_nbt,
        })

    os.makedirs(os.path.dirname(MANIFEST), exist_ok=True)
    ecrire_json(MANIFEST, {
        'familles': len(STRUCTURES),
        'fichiers_nbt': nb_nbt,
        'structures': manifest_entrees,
    })
    print(f"structures : {len(STRUCTURES)} familles, {nb_nbt} fichiers .nbt (2 variantes/famille)")
    return 0


if __name__ == '__main__':
    sys.exit(main())
