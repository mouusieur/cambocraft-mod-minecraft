#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Génère les données JSON de la dimension cambocraft:cambodia, des 15 biomes, du bruit
personnalisé (karst, terrasses, rivières, Grand Lac), de la flore et des 12 zones de progression.
"""
import json
import os
import sys

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(RACINE, 'src', 'main', 'resources', 'data', 'cambocraft')


def ecrire_json(chemin, contenu):
    os.makedirs(os.path.dirname(chemin), exist_ok=True)
    texte = json.dumps(contenu, indent=2, ensure_ascii=False, sort_keys=True) + '\n'
    if os.path.exists(chemin):
        with open(chemin, encoding='utf-8') as f:
            if f.read() == texte:
                return
    with open(chemin, 'w', encoding='utf-8') as f:
        f.write(texte)


BIOMES = [
    # (id, temp, humid, cont, erosion, weird, sky_hex, fog_hex, water_hex, grass_hex, foliage_hex, surface_block, under_block, arbres, fleurs)
    ('rizieres_en_terrasses', 0.85, 0.90, 0.15, 0.45, 0.05, 0x84B8FF, 0xC8E2FF, 0x3D8596, 0x76B843, 0x62A836,
     'cambocraft:boue_de_riziaire', 'cambocraft:terre_lateritique', ['arbre_palmier_a_sucre'], ['flore_riz_sauvage', 'flore_lotus']),
    ('foret_tropicale', 0.90, 0.95, 0.25, 0.20, 0.10, 0x76AEFF, 0xBBD8F8, 0x2E7485, 0x4E9A34, 0x3B8628,
     'minecraft:grass_block', 'cambocraft:terre_lateritique', ['arbre_teck', 'arbre_manguier', 'arbre_jacquier'], ['flore_orchidee', 'flore_fougere']),
    ('bambouseraie', 0.80, 0.88, 0.30, 0.30, -0.15, 0x82B6F5, 0xCFE4EC, 0x387E88, 0x68B03E, 0x589E32,
     'minecraft:grass_block', 'cambocraft:terre_lateritique', ['arbre_bambou', 'arbre_teck'], ['flore_citronnelle', 'flore_fougere']),
    ('savane_claire', 0.95, 0.30, 0.35, 0.55, 0.20, 0x92BCFF, 0xE2D8C0, 0x427C88, 0xA8A24A, 0x8F8C3B,
     'cambocraft:terre_lateritique', 'cambocraft:laterite', ['arbre_palmier_a_sucre'], ['flore_herbe_savane']),
    ('mangrove_et_marais', 0.88, 0.98, -0.25, 0.65, -0.10, 0x78A8D8, 0xB8CECA, 0x2E5E54, 0x4C8438, 0x3C6E2B,
     'cambocraft:boue_de_riziaire', 'cambocraft:mousson_vase', ['arbre_paletuvier', 'arbre_cocotier'], ['flore_lotus', 'flore_riz_sauvage']),
    ('riviere_du_tonle', 0.85, 0.85, -0.35, 0.70, 0.00, 0x80B4FF, 0xC6E0FF, 0x2F6E8E, 0x62A642, 0x529436,
     'cambocraft:sable_de_riviere', 'cambocraft:gravier_de_riviere', ['arbre_cocotier'], ['flore_lotus']),
    ('grand_lac', 0.86, 0.92, -0.60, 0.75, 0.00, 0x7AB0FF, 0xC2DEFF, 0x265C7E, 0x5CA044, 0x4C8E38,
     'cambocraft:mousson_vase', 'cambocraft:sable_de_riviere', ['arbre_paletuvier'], ['flore_lotus']),
    ('jungle_en_ruines', 0.90, 0.92, 0.40, 0.15, 0.25, 0x74A8EB, 0xB8D0DC, 0x2E6874, 0x448C32, 0x347824,
     'minecraft:grass_block', 'cambocraft:pierre_erodee', ['arbre_kapokier', 'arbre_teck'], ['flore_orchidee', 'flore_fougere']),
    ('montagnes_karstiques', 0.65, 0.75, 0.70, -0.65, 0.60, 0x88BAFF, 0xD4E2F0, 0x347292, 0x5E9848, 0x4E863A,
     'cambocraft:calcaire_karstique', 'cambocraft:calcaire_karstique', ['arbre_pin_du_cambodge', 'arbre_teck'], ['flore_orchidee']),
    ('plateau_de_laterite', 0.92, 0.45, 0.55, -0.25, 0.35, 0x90B8F8, 0xE0CCB8, 0x3E7482, 0x8E8842, 0x787234,
     'cambocraft:terre_lateritique', 'cambocraft:laterite', ['arbre_teck', 'arbre_palmier_a_sucre'], ['flore_herbe_savane']),
    ('sanctuaire_de_lotus', 0.82, 0.90, 0.05, 0.50, 0.45, 0x96C8FF, 0xDCF0FF, 0x3898B0, 0x62B85C, 0x52A84C,
     'cambocraft:pierre_du_lotus', 'cambocraft:pierre_du_lotus', ['arbre_manguier', 'arbre_kapokier'], ['flore_lotus', 'flore_orchidee']),
    ('pinedes_seches', 0.60, 0.35, 0.60, -0.35, -0.30, 0x8CBEFF, 0xD8E4F2, 0x366E8C, 0x6A924E, 0x587E3E,
     'cambocraft:terre_lateritique', 'cambocraft:gres_khmer', ['arbre_pin_du_cambodge'], ['flore_herbe_savane']),
    ('plaine_inondable', 0.86, 0.94, -0.15, 0.60, -0.20, 0x82B6FF, 0xC8E2FA, 0x347A92, 0x6AB244, 0x589E36,
     'cambocraft:boue_de_riziaire', 'cambocraft:mousson_vase', ['arbre_palmier_a_sucre', 'arbre_cocotier'], ['flore_riz_sauvage', 'flore_lotus']),
    ('foret_de_kapokiers', 0.88, 0.86, 0.35, 0.10, -0.40, 0x78B0F5, 0xC0D8E8, 0x326E80, 0x52963A, 0x42822C,
     'minecraft:grass_block', 'cambocraft:gres_khmer', ['arbre_kapokier', 'arbre_jacquier'], ['flore_orchidee', 'flore_fougere']),
    ('iles_flottantes_des_garudas', 0.55, 0.60, 0.85, -0.80, 0.80, 0xA4D4FF, 0xEAF6FF, 0x4CA8C8, 0x78C268, 0x64B054,
     'cambocraft:gres_khmer_poli', 'cambocraft:pierre_du_lotus', ['arbre_kapokier'], ['flore_lotus', 'flore_orchidee']),
]

ARBRES = {
    'arbre_teck': ('cambocraft:teck_log', 'cambocraft:teck_leaves', 6, 3),
    'arbre_kapokier': ('cambocraft:kapokier_log', 'cambocraft:kapokier_leaves', 10, 4),
    'arbre_cocotier': ('cambocraft:cocotier_log', 'cambocraft:cocotier_leaves', 7, 2),
    'arbre_manguier': ('cambocraft:manguier_log', 'cambocraft:manguier_leaves', 5, 3),
    'arbre_jacquier': ('cambocraft:jacquier_log', 'cambocraft:jacquier_leaves', 6, 2),
    'arbre_palmier_a_sucre': ('cambocraft:palmier_a_sucre_log', 'cambocraft:palmier_a_sucre_leaves', 8, 2),
    'arbre_paletuvier': ('cambocraft:paletuvier_log', 'cambocraft:paletuvier_leaves', 5, 3),
    'arbre_pin_du_cambodge': ('cambocraft:pin_du_cambodge_log', 'cambocraft:pin_du_cambodge_leaves', 8, 3),
    'arbre_bambou': ('cambocraft:bambou_bloc', 'cambocraft:bambou_leaves', 9, 2),
}

FLORES = {
    'flore_lotus': 'cambocraft:fleur_de_lotus',
    'flore_riz_sauvage': 'cambocraft:riz_sauvage',
    'flore_orchidee': 'cambocraft:orchidee_khmer',
    'flore_fougere': 'cambocraft:fougere_tropicale',
    'flore_citronnelle': 'cambocraft:citronnelle',
    'flore_herbe_savane': 'cambocraft:herbe_de_savane',
}

ZONES = [
    (1, 'village_de_tonle', 'Village de Tonlé', 'Tonle Village', 1, 15,
     ['rizieres_en_terrasses', 'riviere_du_tonle'], None, 'chef_des_bandits_de_la_riviere', (0, 72, 0),
     ['bandit_de_riviere', 'pillard_des_berges', 'initie_ombre', 'crabe_geant_des_vases']),
    (2, 'rizieres_de_ly', 'Rizières de Ly', 'Ly Rice Terraces', 15, 30,
     ['rizieres_en_terrasses', 'plaine_inondable'], 'chef_des_bandits_de_la_riviere', 'le_buffle_d_or', (320, 74, -180),
     ['maraudeur_des_digues', 'sanglier_des_rizieres', 'initie_ombre', 'garde_corrompu']),
    (3, 'bambouseraie_de_srok', 'Bambouseraie de Srok', 'Srok Bamboo Grove', 30, 50,
     ['bambouseraie', 'foret_tropicale'], 'le_buffle_d_or', 'maitre_des_lames_du_clan_ombre', (-420, 78, 260),
     ['lame_ombre', 'archer_des_bambous', 'tigre_des_brumes', 'guetteur_nocturne']),
    (4, 'grand_lac', 'Grand Lac', 'Great Lake', 50, 70,
     ['grand_lac', 'mangrove_et_marais'], 'maitre_des_lames_du_clan_ombre', 'le_crocodile_des_profondeurs', (150, 64, 640),
     ['pirate_du_lac', 'crocodile_des_marais', 'lame_ombre', 'gardien_englouti']),
    (5, 'foret_de_kompong', 'Forêt de Kompong', 'Kompong Forest', 70, 90,
     ['foret_tropicale', 'foret_de_kapokiers'], 'le_crocodile_des_profondeurs', 'le_serpent_de_la_foret', (-680, 82, -420),
     ['chasseur_sylvestre', 'vipere_des_frondaisons', 'ombre_silencieuse', 'esprit_des_racines']),
    (6, 'temples_de_prasat_sovann', 'Temples de Prasat Sovann', 'Prasat Sovann Temples', 90, 110,
     ['jungle_en_ruines', 'foret_de_kapokiers'], 'le_serpent_de_la_foret', 'gardien_de_pierre', (820, 86, 120),
     ['sentinelle_de_gres', 'automate_de_laterite', 'ombre_silencieuse', 'profanateur_de_relique']),
    (7, 'forteresse_ombre', 'Forteresse Ombre', 'Shadow Fortress', 110, 130,
     ['plateau_de_laterite', 'pinedes_seches'], 'gardien_de_pierre', 'generalissime_du_clan_ombre', (-960, 96, 780),
     ['ombre_silencieuse', 'executeur_d_ardoise', 'maitre_shuriken', 'garde_noir_de_la_citadelle']),
    (8, 'montagnes_karstiques', 'Montagnes Karstiques', 'Karst Mountains', 130, 150,
     ['montagnes_karstiques', 'plateau_de_laterite'], 'generalissime_du_clan_ombre', 'le_phenix_de_laterite', (1180, 128, -860),
     ['faucon_de_braise', 'golem_de_calcaire', 'ermite_corrompu', 'ombre_des_cretes']),
    (9, 'labyrinthe_des_visages', 'Labyrinthe des Visages', 'Labyrinth of Faces', 150, 165,
     ['jungle_en_ruines', 'savane_claire'], 'le_phenix_de_laterite', 'l_eveil_du_visage', (-1340, 88, -1120),
     ['colosse_du_labyrinthe', 'illusionniste_de_pierre', 'spectre_des_galeries', 'gardien_du_seuil']),
    (10, 'iles_des_garudas', 'Îles des Garudas', 'Garuda Isles', 165, 185,
     ['iles_flottantes_des_garudas'], 'l_eveil_du_visage', 'garuda_de_la_tempete', (1560, 168, 1240),
     ['lancier_celeste', 'aigle_de_foudre', 'elementaire_de_mousson', 'champion_des_nuages']),
    (11, 'sanctuaire_du_lotus', 'Sanctuaire du Lotus', 'Lotus Sanctuary', 185, 200,
     ['sanctuaire_de_lotus'], 'garuda_de_la_tempete', 'le_gardien_de_l_equilibre', (0, 92, 1800),
     ['ascete_de_l_epreuve', 'lion_sacree_anime', 'reflet_du_nen', 'gardien_de_cristal']),
    (12, 'royaume_des_esprits', 'Royaume des Esprits', 'Spirit Realm', 200, 200,
     ['sanctuaire_de_lotus', 'iles_flottantes_des_garudas'], 'le_gardien_de_l_equilibre', 'l_ombre_premiere', (0, 140, -2200),
     ['avatar_du_vide', 'spectre_ancestral', 'archonte_de_l_ombre', 'seraphin_du_lotus']),
]


def generer_dimension():
    ecrire_json(os.path.join(DATA, 'dimension_type', 'cambodia.json'), {
        'ultrawarm': False,
        'natural': True,
        'piglin_safe': False,
        'respawn_anchor_works': False,
        'bed_works': True,
        'has_raids': False,
        'has_skylight': True,
        'has_ceiling': False,
        'coordinate_scale': 1.0,
        'ambient_light': 0.05,
        'logical_height': 384,
        'effects': 'minecraft:overworld',
        'infiniburn': '#minecraft:infiniburn_overworld',
        'min_y': -64,
        'height': 384,
        'monster_spawn_light_level': {'type': 'minecraft:uniform', 'min_inclusive': 0, 'max_inclusive': 7},
        'monster_spawn_block_light_limit': 0,
    })

    entrees_biomes = []
    for b in BIOMES:
        ident, temp, humid, cont, erosion, weird = b[:6]
        entrees_biomes.append({
            'biome': f'cambocraft:{ident}',
            'parameters': {
                'temperature': temp - 0.5,
                'humidity': humid - 0.5,
                'continentalness': cont,
                'erosion': erosion,
                'weirdness': weird,
                'depth': 0.0,
                'offset': 0.0,
            },
        })

    ecrire_json(os.path.join(DATA, 'dimension', 'cambodia.json'), {
        'type': 'cambocraft:cambodia',
        'generator': {
            'type': 'minecraft:noise',
            'settings': 'cambocraft:cambodia_noise',
            'biome_source': {
                'type': 'minecraft:multi_noise',
                'biomes': entrees_biomes,
            },
        },
    })


def generer_noise_settings():
    regles_surface = []
    for b in BIOMES:
        ident = b[0]
        top_blk = b[11]
        under_blk = b[12]
        regles_surface.append({
            'type': 'minecraft:condition',
            'if_true': {'type': 'minecraft:biome', 'biome_is': [f'cambocraft:{ident}']},
            'then_run': {
                'type': 'minecraft:sequence',
                'sequence': [
                    {
                        'type': 'minecraft:condition',
                        'if_true': {'type': 'minecraft:stone_depth', 'offset': 0, 'surface_type': 'floor', 'add_surface_depth': False, 'secondary_depth_range': 0},
                        'then_run': {'type': 'minecraft:block', 'result_state': {'Name': top_blk}},
                    },
                    {
                        'type': 'minecraft:condition',
                        'if_true': {'type': 'minecraft:stone_depth', 'offset': 0, 'surface_type': 'floor', 'add_surface_depth': True, 'secondary_depth_range': 3},
                        'then_run': {'type': 'minecraft:block', 'result_state': {'Name': under_blk}},
                    },
                ],
            },
        })

    ecrire_json(os.path.join(DATA, 'worldgen', 'noise_settings', 'cambodia_noise.json'), {
        'sea_level': 63,
        'disable_mob_generation': False,
        'aquifers_enabled': True,
        'ore_veins_enabled': True,
        'legacy_random_source': False,
        'default_block': {'Name': 'cambocraft:gres_khmer'},
        'default_fluid': {'Name': 'minecraft:water', 'Properties': {'level': '0'}},
        'noise': {'min_y': -64, 'height': 384, 'size_horizontal': 1, 'size_vertical': 2},
        'noise_router': {
            'barrier': 0.0,
            'fluid_level_floodedness': 0.0,
            'fluid_level_spread': 0.0,
            'lava': 0.0,
            'temperature': {'type': 'minecraft:shifted_noise', 'noise': 'minecraft:temperature', 'xz_scale': 0.25, 'y_scale': 0.0, 'shift_x': 'minecraft:shift_x', 'shift_y': 0.0, 'shift_z': 'minecraft:shift_z'},
            'vegetation': {'type': 'minecraft:shifted_noise', 'noise': 'minecraft:vegetation', 'xz_scale': 0.25, 'y_scale': 0.0, 'shift_x': 'minecraft:shift_x', 'shift_y': 0.0, 'shift_z': 'minecraft:shift_z'},
            'continents': 'minecraft:overworld/continents',
            'erosion': 'minecraft:overworld/erosion',
            'depth': 'minecraft:overworld/depth',
            'ridges': 'minecraft:overworld/ridges',
            'initial_density_without_jaggedness': {'type': 'minecraft:y_clamped_gradient', 'from_y': -64, 'to_y': 256, 'from_value': 1.5, 'to_value': -1.5},
            'final_density': {'type': 'minecraft:y_clamped_gradient', 'from_y': -64, 'to_y': 256, 'from_value': 1.5, 'to_value': -1.5},
            'vein_toggle': 0.0,
            'vein_ridged': 0.0,
            'vein_gap': 0.0,
        },
        'spawn_target': [],
        'surface_rule': {
            'type': 'minecraft:sequence',
            'sequence': [
                {
                    'type': 'minecraft:condition',
                    'if_true': {'type': 'minecraft:vertical_gradient', 'random_name': 'minecraft:bedrock_floor', 'true_at_and_below': {'above_bottom': 0}, 'false_at_and_above': {'above_bottom': 5}},
                    'then_run': {'type': 'minecraft:block', 'result_state': {'Name': 'minecraft:bedrock'}},
                },
                *regles_surface,
            ],
        },
    })


def generer_features_et_biomes():
    # Minerai de lotus
    ecrire_json(os.path.join(DATA, 'worldgen', 'configured_feature', 'minerai_de_lotus.json'), {
        'type': 'minecraft:ore',
        'config': {
            'size': 8,
            'discard_chance_on_air_exposure': 0.0,
            'targets': [
                {'target': {'predicate_type': 'minecraft:block_match', 'block': 'cambocraft:gres_khmer'}, 'state': {'Name': 'cambocraft:minerai_de_lotus'}},
                {'target': {'predicate_type': 'minecraft:block_match', 'block': 'cambocraft:calcaire_karstique'}, 'state': {'Name': 'cambocraft:minerai_de_lotus'}},
            ],
        },
    })
    ecrire_json(os.path.join(DATA, 'worldgen', 'placed_feature', 'minerai_de_lotus.json'), {
        'feature': 'cambocraft:minerai_de_lotus',
        'placement': [
            {'type': 'minecraft:count', 'count': 10},
            {'type': 'minecraft:in_square'},
            {'type': 'minecraft:height_range', 'height': {'type': 'minecraft:uniform', 'min_inclusive': {'absolute': -32}, 'max_inclusive': {'absolute': 80}}},
            {'type': 'minecraft:biome'},
        ],
    })

    for id_arbre, (tronc, feuilles, h_base, h_rand) in ARBRES.items():
        ecrire_json(os.path.join(DATA, 'worldgen', 'configured_feature', id_arbre + '.json'), {
            'type': 'minecraft:tree',
            'config': {
                'ignore_vines': True,
                'force_dirt': False,
                'minimum_size': {'type': 'minecraft:two_layers_feature_size', 'limit': 1, 'lower_size': 0, 'upper_size': 1},
                'dirt_provider': {'type': 'minecraft:simple_state_provider', 'state': {'Name': 'cambocraft:terre_lateritique'}},
                'trunk_provider': {'type': 'minecraft:simple_state_provider', 'state': {'Name': tronc, 'Properties': {'axis': 'y'}}},
                'trunk_placer': {'type': 'minecraft:straight_trunk_placer', 'base_height': h_base, 'height_rand_a': h_rand, 'height_rand_b': 0},
                'foliage_provider': {'type': 'minecraft:simple_state_provider', 'state': {'Name': feuilles}},
                'foliage_placer': {'type': 'minecraft:blob_foliage_placer', 'radius': 2, 'offset': 0, 'height': 3},
                'decorators': [],
            },
        })
        ecrire_json(os.path.join(DATA, 'worldgen', 'placed_feature', id_arbre + '.json'), {
            'feature': f'cambocraft:{id_arbre}',
            'placement': [
                {'type': 'minecraft:count', 'count': 4},
                {'type': 'minecraft:in_square'},
                {'type': 'minecraft:heightmap', 'heightmap': 'OCEAN_FLOOR'},
                {'type': 'minecraft:biome'},
            ],
        })

    for id_flore, bloc_flore in FLORES.items():
        ecrire_json(os.path.join(DATA, 'worldgen', 'configured_feature', id_flore + '.json'), {
            'type': 'minecraft:simple_block',
            'config': {'to_place': {'type': 'minecraft:simple_state_provider', 'state': {'Name': bloc_flore}}},
        })
        ecrire_json(os.path.join(DATA, 'worldgen', 'placed_feature', id_flore + '.json'), {
            'feature': f'cambocraft:{id_flore}',
            'placement': [
                {'type': 'minecraft:rarity_filter', 'chance': 4},
                {'type': 'minecraft:in_square'},
                {'type': 'minecraft:heightmap', 'heightmap': 'MOTION_BLOCKING'},
                {'type': 'minecraft:biome'},
            ],
        })

    for b in BIOMES:
        ident, temp, humid, _, _, _, sky, fog, water, grass, foliage, _, _, arbres, flores = b
        vegetal = [f'cambocraft:{a}' for a in arbres] + [f'cambocraft:{fl}' for fl in flores]
        ecrire_json(os.path.join(DATA, 'worldgen', 'biome', ident + '.json'), {
            'has_precipitation': humid >= 0.40,
            'temperature': temp,
            'downfall': humid,
            'effects': {
                'sky_color': sky,
                'fog_color': fog,
                'water_color': water,
                'water_fog_color': max(0, water - 0x101820),
                'grass_color': grass,
                'foliage_color': foliage,
            },
            'carvers': {'air': ['minecraft:cave', 'minecraft:canyon']},
            'features': [
                [], [], [], [], [], [],
                ['cambocraft:minerai_de_lotus'],
                [],
                [],
                vegetal,
                [],
            ],
            'spawners': {'monster': [], 'creature': [], 'ambient': [], 'water_creature': [], 'water_ambient': [], 'underground_water_creature': [], 'misc': []},
            'spawn_costs': {},
        })


def generer_zones():
    for ordre, ident, fr, en, nmin, nmax, biomes, boss_req, boss_id, tp, ennemis in ZONES:
        ecrire_json(os.path.join(DATA, 'cambo', 'zones', ident + '.json'), {
            'ordre': ordre,
            'nom': {'fr': fr, 'en': en},
            'niveau_min': nmin,
            'niveau_max': nmax,
            'biomes': [f'cambocraft:{b}' for b in biomes],
            'boss_requis': boss_req,
            'boss_id': boss_id,
            'teleporteur': {'x': tp[0], 'y': tp[1], 'z': tp[2]},
            'ennemis': ennemis,
        })


def main():
    generer_dimension()
    generer_noise_settings()
    generer_features_et_biomes()
    generer_zones()
    print(f"worldgen : 1 dimension, {len(BIOMES)} biomes, {len(ARBRES) + len(FLORES) + 1} features, {len(ZONES)} zones")
    return 0


if __name__ == '__main__':
    sys.exit(main())
