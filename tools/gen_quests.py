#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Génère les 85 quêtes JSON de Cambocraft sous data/cambocraft/cambo/quests/ (§8) :
  - 1 quête d'introduction (Village de Tonlé)
  - 12 chapitres de la quête principale jusqu'au Maître du Nen et La Leçon du Bol (3 fins)
  - 64 quêtes secondaires (>= 60 requises) couvrant les 4 factions et tous les métiers
  - 8 quêtes répétables (saisonnières, quotidiennes, hebdomadaires)
"""
import json
import os
import sys

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
QUESTS_DIR = os.path.join(RACINE, 'src', 'main', 'resources', 'data', 'cambocraft', 'cambo', 'quests')


def ecrire_json(chemin, contenu):
    os.makedirs(os.path.dirname(chemin), exist_ok=True)
    with open(chemin, 'w', encoding='utf-8') as f:
        json.dump(contenu, f, indent=2, ensure_ascii=False, sort_keys=True)
        f.write('\n')


CHAPITRES = [
    (1, 'chapitre_01_les_berges_du_tonle',
     'Chapitre I — Les Berges du Tonlé', 'Chapter I — Shores of the Tonle',
     'Aider les habitants du Village de Tonlé et repousser le Chef des bandits de la rivière.',
     'Aid the villagers of Tonle and repel the River Bandit Chief.',
     1, 'intro_premier_bol_du_tonle', 'tonle', 'chef_des_bandits_de_la_riviere', 'cambocraft:insigne_du_chef_bandit'),
    (2, 'chapitre_02_les_terrasses_d_or',
     'Chapitre II — Les Terrasses d’Or de Ly', 'Chapter II — Golden Terraces of Ly',
     'Restaurer les diguettes des rizières de Ly et apaiser le Buffle d’Or.',
     'Restore the paddy dikes of Ly and appease the Golden Buffalo.',
     15, 'chapitre_01_les_berges_du_tonle', 'ly', 'le_buffle_d_or', 'cambocraft:corne_du_buffle_d_or'),
    (3, 'chapitre_03_murmures_dans_les_bambous',
     'Chapitre III — Murmures dans les Bambous', 'Chapter III — Whispers in the Bamboo',
     'Démasquer les éclaireurs du clan Ombre à Srok et affronter le Maître des lames.',
     'Unmask the Shadow Clan scouts in Srok and face the Blade Master.',
     30, 'chapitre_02_les_terrasses_d_or', 'srok', 'maitre_des_lames_du_clan_ombre', 'cambocraft:fourreau_du_maitre_des_lames'),
    (4, 'chapitre_04_les_eaux_profondes_du_lac',
     'Chapitre IV — Les Eaux Profondes du Grand Lac', 'Chapter IV — Deep Waters of the Great Lake',
     'Explorer les sanctuaires engloutis du Tonlé Sap et vaincre le Crocodile des Profondeurs.',
     'Explore the sunken shrines of Tonle Sap and defeat the Deep Crocodile.',
     50, 'chapitre_03_murmures_dans_les_bambous', 'tonle', 'le_crocodile_des_profondeurs', 'cambocraft:croc_du_crocodile_des_profondeurs'),
    (5, 'chapitre_05_le_souffle_de_kompong',
     'Chapitre V — Le Souffle de la Forêt de Kompong', 'Chapter V — Breath of Kompong Forest',
     'Traverser la canopée de kapokiers et triompher du Serpent de la forêt.',
     'Cross the kapok canopy and triumph over the Forest Serpent.',
     70, 'chapitre_04_les_eaux_profondes_du_lac', 'kompong', 'le_serpent_de_la_foret', 'cambocraft:ecaille_du_serpent_sylvestre'),
    (6, 'chapitre_06_les_pierres_de_prasat_sovann',
     'Chapitre VI — Les Pierres de Prasat Sovann', 'Chapter VI — Stones of Prasat Sovann',
     'Résoudre l’énigme des dalles anciennes et éveiller le Gardien de pierre.',
     'Solve the ancient pressure tile puzzle and awaken the Stone Guardian.',
     90, 'chapitre_05_le_souffle_de_kompong', 'kompong', 'gardien_de_pierre', 'cambocraft:coeur_du_gardien_de_pierre'),
    (7, 'chapitre_07_la_citadelle_sans_lune',
     'Chapitre VII — La Citadelle sans Lune', 'Chapter VII — The Moonless Citadel',
     'Infiltrer la Forteresse Ombre et défier le Généralissime.',
     'Infiltrate the Shadow Fortress and challenge the Generalissimo.',
     110, 'chapitre_06_les_pierres_de_prasat_sovann', 'srok', 'generalissime_du_clan_ombre', 'cambocraft:masque_du_generalissime'),
    (8, 'chapitre_08_les_braises_du_karst',
     'Chapitre VIII — Les Braises du Karst', 'Chapter VIII — Embers of the Karst',
     'Gravir les pitons calcaires embrumés et affronter le Phénix de latérite.',
     'Climb the mist-shrouded limestone peaks and face the Laterite Phoenix.',
     130, 'chapitre_07_la_citadelle_sans_lune', 'ly', 'le_phenix_de_laterite', 'cambocraft:plume_ardente_du_phenix'),
    (9, 'chapitre_09_le_silence_des_visages',
     'Chapitre IX — Le Silence des Tours-Visages', 'Chapter IX — Silence of the Face Towers',
     'Parcourir avec respect les galeries de bas-reliefs et réussir l’épreuve de L’Éveil du Visage.',
     'Walk the bas-relief galleries with respect and pass the trial of the Awakened Face.',
     150, 'chapitre_08_les_braises_du_karst', 'kompong', 'l_eveil_du_visage', 'cambocraft:fragment_du_visage_eveille'),
    (10, 'chapitre_10_au_dessus_de_la_mousson',
     'Chapitre X — Au-dessus de la Mousson', 'Chapter X — Above the Monsoon',
     'Atteindre les Îles Flottantes et mesurer son souffle au Garuda de la Tempête.',
     'Reach the Floating Isles and test your spirit against the Storm Garuda.',
     165, 'chapitre_09_le_silence_des_visages', 'srok', 'garuda_de_la_tempete', 'cambocraft:serre_du_garuda_de_tempete'),
    (11, 'chapitre_11_le_seuil_du_sanctuaire',
     'Chapitre XI — Le Seuil du Sanctuaire du Lotus', 'Chapter XI — Threshold of the Lotus Sanctuary',
     'Rassembler les reliques au Sanctuaire du Lotus et triompher du Gardien de l’Équilibre.',
     'Gather the relics at the Lotus Sanctuary and overcome the Guardian of Balance.',
     185, 'chapitre_10_au_dessus_de_la_mousson', 'kompong', 'le_gardien_de_l_equilibre', 'cambocraft:sceau_du_gardien_de_l_equilibre'),
    (12, 'chapitre_12_la_lecon_du_bol',
     'Chapitre XII — La Leçon du Bol', 'Chapter XII — The Lesson of the Bowl',
     'Méditer auprès du Vénérable Preah Thera, Maître du Nen, et choisir sa voie spirituelle.',
     'Meditate with Venerable Preah Thera, the Nen Master, and choose your spiritual path.',
     195, 'chapitre_11_le_seuil_du_sanctuaire', 'kompong', 'maitre_moine_du_nen', 'cambocraft:bol_du_maitre_du_nen'),
]

THEMES_SECONDAIRES = [
    ('tonle', 'vendeur_de_riz', 'recolter', 'cambocraft:riz_brut', 'Moisson du Tonlé', 'Tonle Harvest', 'cambocraft:bol_de_riz'),
    ('tonle', 'pecheur_marchand', 'pecher', 'cambocraft:poisson_chat', 'Prise des Berges', 'Shoreline Catch', 'cambocraft:appat_de_riz_fermente'),
    ('tonle', 'cuisinier_de_rue', 'cuisiner', 'cambocraft:amok', 'Parfum de Citronnelle', 'Lemongrass Aroma', 'cambocraft:amok'),
    ('tonle', 'capitaine_de_pirogue', 'visiter', 'embarcadere_pirogue_village', 'Courants du Fleuve', 'River Currents', 'cambocraft:pagaie_de_course'),
    ('ly', 'tisserand_de_soie', 'fabriquer', 'cambocraft:fil_de_soie', 'Fils d’Or de Ly', 'Golden Threads of Ly', 'cambocraft:tissu_de_soie'),
    ('ly', 'guide_de_la_riziere', 'recolter', 'cambocraft:sucre_de_palme', 'Palmiers des Diguettes', 'Dike Sugar Palms', 'cambocraft:kralan'),
    ('ly', 'guerisseur', 'livrer', 'cambocraft:fleur_de_lotus', 'Remèdes des Terrasses', 'Terrace Remedies', 'cambocraft:onguent_de_lotus'),
    ('srok', 'vendeur_de_bambou', 'recolter', 'cambocraft:bambou_brut', 'Bambous de la Brume', 'Mist Bamboo', 'cambocraft:fleche_en_bambou'),
    ('srok', 'forgeron', 'forger', 'cambocraft:epee_khmer', 'Trempe de Srok', 'Srok Tempering', 'cambocraft:cristal_de_forge_mineur'),
    ('srok', 'maitre_d_arene', 'vaincre', 'initie_ombre', 'Vigile des Sentiers', 'Trail Vigil', 'cambocraft:bandage_de_soie'),
    ('kompong', 'sculpteur', 'sculpter', 'cambocraft:bas_relief', 'Ciseau et Grès', 'Chisel and Sandstone', 'cambocraft:statuette_de_naga'),
    ('kompong', 'gardien_du_temple', 'visiter', 'prasat_moyen', 'Ronde des Lanternes', 'Lantern Watch', 'cambocraft:encens_beni'),
    ('kompong', 'conteur', 'parler', 'conteur', 'Mémoire des Anciens', 'Memory of the Elders', 'cambocraft:offrande_de_fleurs'),
    ('tonle', 'apprenti_moine', 'mediter', 'sanctuaire_lotus_bassin', 'Calme Matinal', 'Morning Calm', 'cambocraft:poudre_de_lotus'),
    ('srok', 'maitre_de_quete', 'parler', 'ninja_repenti', 'Voix du Repentir', 'Voice of Repentance', 'cambocraft:essence_d_ombre'),
    ('ly', 'cuisinier_de_rue', 'cuisiner', 'cambocraft:lok_lak', 'Poivre des Collines', 'Hill Pepper', 'cambocraft:lok_lak'),
]


def main():
    os.makedirs(QUESTS_DIR, exist_ok=True)
    nb = 0

    # 1. Quête d'introduction
    ecrire_json(os.path.join(QUESTS_DIR, 'intro_premier_bol_du_tonle.json'), {
        'chapitre': 0,
        'type': 'intro',
        'titre': {'fr': 'Le Premier Bol du Tonlé', 'en': 'The First Bowl of Tonle'},
        'resume': {
            'fr': 'Saluer l’Ancien Chann au Village de Tonlé, recevoir votre Carnet du Nen et goûter un Bol de Riz Béni auprès de l’apprenti moine Kosal.',
            'en': 'Greet Elder Chann in Tonle Village, receive your Nen Notebook, and taste a Blessed Rice Bowl with apprentice monk Kosal.',
        },
        'niveau_min': 1,
        'quete_requise': '',
        'saison_requise': '*',
        'faction': 'tonle',
        'etapes': [
            {
                'id': 'parler_ancien',
                'objectif': 'parler',
                'cible': 'maitre_de_quete',
                'quantite': 1,
                'description': {'fr': 'Parler à l’Ancien Chann sur la place de Tonlé.', 'en': 'Speak with Elder Chann in Tonle plaza.'},
            },
            {
                'id': 'parler_apprenti',
                'objectif': 'parler',
                'cible': 'apprenti_moine',
                'quantite': 1,
                'description': {'fr': 'Recevoir la bénédiction du bol auprès de l’apprenti moine Kosal.', 'en': 'Receive the bowl blessing from apprentice monk Kosal.'},
            },
        ],
        'branches': [],
        'recompenses': {
            'xp': 150,
            'pieces': 25,
            'reputation': 50,
            'objets': ['cambocraft:carnet_du_nen', 'cambocraft:bol_de_riz_beni'],
            'pouvoir': '',
        },
    })
    nb += 1

    # 2. Les 12 chapitres de la quête principale
    for (num, qid, fr_t, en_t, fr_r, en_r, nmin, req, fac, boss, relique) in CHAPITRES:
        branches = []
        if num == 12:
            branches = [
                {
                    'id': 'lumiere',
                    'titre': {'fr': 'Voie de la Lumière — Serment de Bienveillance', 'en': 'Path of Light — Vow of Benevolence'},
                    'consequence': {
                        'fr': 'Le bol déborde d’une clarté dorée : vos soins et protections rayonnent sur vos alliés.',
                        'en': 'The bowl brims with golden clarity: your healing and wards radiate to allies.',
                    },
                    'pouvoir_debloque': 'danse_de_l_apsara',
                    'bonus_reputation': 300,
                },
                {
                    'id': 'ombre',
                    'titre': {'fr': 'Voie de l’Ombre — Maîtrise du Silence', 'en': 'Path of Shadow — Mastery of Silence'},
                    'consequence': {
                        'fr': 'Vous accueillez la part d’ombre sans colère : vos frappes deviennent fulgurantes et précises.',
                        'en': 'You accept the shadow without anger: your strikes grow swift and precise.',
                    },
                    'pouvoir_debloque': 'regard_du_naga',
                    'bonus_reputation': 150,
                },
                {
                    'id': 'equilibre',
                    'titre': {'fr': 'Voie de l’Équilibre — Le Bol Immobile', 'en': 'Path of Balance — The Still Bowl'},
                    'consequence': {
                        'fr': 'Comme l’eau calme reflétant le lotus, votre énergie spirituelle se régénère en harmonie.',
                        'en': 'Like still water reflecting the lotus, your spiritual energy regenerates in harmony.',
                    },
                    'pouvoir_debloque': 'serment_de_l_esprit',
                    'bonus_reputation': 250,
                },
            ]

        ecrire_json(os.path.join(QUESTS_DIR, qid + '.json'), {
            'chapitre': num,
            'type': 'principale',
            'titre': {'fr': fr_t, 'en': en_t},
            'resume': {'fr': fr_r, 'en': en_r},
            'niveau_min': nmin,
            'quete_requise': req,
            'saison_requise': '*',
            'faction': fac,
            'etapes': [
                {
                    'id': 'enquete_province',
                    'objectif': 'parler',
                    'cible': 'maitre_de_quete' if num < 12 else 'maitre_moine_du_nen',
                    'quantite': 1,
                    'description': {'fr': 'Recueillir les témoignages de la province.', 'en': 'Gather accounts from the province.'},
                },
                {
                    'id': 'epreuve_majeure',
                    'objectif': 'vaincre' if num < 12 else 'mediter',
                    'cible': boss,
                    'quantite': 1,
                    'description': {
                        'fr': f"Surmonter l'épreuve de fin de zone ({boss}).",
                        'en': f"Overcome the zone trial ({boss}).",
                    },
                },
            ],
            'branches': branches,
            'recompenses': {
                'xp': 500 * num * num,
                'pieces': 40 * num,
                'reputation': 120,
                'objets': [relique],
                'pouvoir': 'flamme_du_lotus' if num == 1 else '',
            },
        })
        nb += 1

    # 3. 64 quêtes secondaires (4 paliers x 16 thèmes)
    for palier in range(4):
        for idx, (fac, pnj, obj, cible, fr_base, en_base, obj_rec) in enumerate(THEMES_SECONDAIRES, start=1):
            num_sec = palier * len(THEMES_SECONDAIRES) + idx
            qid = f"secondaire_{num_sec:02d}_{fac}_{obj}"
            niv = max(1, palier * 45 + idx * 2)
            qte = (palier + 1) * (3 if obj in ('recolter', 'pecher') else 1)
            ecrire_json(os.path.join(QUESTS_DIR, qid + '.json'), {
                'chapitre': 0,
                'type': 'secondaire',
                'titre': {
                    'fr': f"{fr_base} ({['I', 'II', 'III', 'IV'][palier]})",
                    'en': f"{en_base} ({['I', 'II', 'III', 'IV'][palier]})",
                },
                'resume': {
                    'fr': f"Aider {pnj} dans la province de {fac.capitalize()} ({obj} : {cible}).",
                    'en': f"Assist {pnj} in {fac.capitalize()} province ({obj}: {cible}).",
                },
                'niveau_min': niv,
                'quete_requise': '',
                'saison_requise': '*',
                'faction': fac,
                'etapes': [
                    {
                        'id': 'objectif_1',
                        'objectif': obj,
                        'cible': cible,
                        'quantite': qte,
                        'description': {'fr': f"Accomplir {qte}x {obj} ({cible}).", 'en': f"Complete {qte}x {obj} ({cible})."},
                    },
                    {
                        'id': 'retour_pnj',
                        'objectif': 'parler',
                        'cible': pnj,
                        'quantite': 1,
                        'description': {'fr': f"Rapporter le résultat à {pnj}.", 'en': f"Report back to {pnj}."},
                    },
                ],
                'branches': [],
                'recompenses': {
                    'xp': 250 * (palier + 1),
                    'pieces': 15 * (palier + 1),
                    'reputation': 35 * (palier + 1),
                    'objets': [obj_rec],
                    'pouvoir': '',
                },
            })
            nb += 1

    # 4. 8 quêtes répétables (saisonnières, quotidiennes, hebdomadaires)
    repetables = [
        ('saisonniere_crue_du_tonle', 'saisonniere', 'mousson', 'tonle', 'Crue de la Mousson', 'Monsoon Flood Patrol', 'pecher', 'cambocraft:carpe_geante', 2),
        ('saisonniere_temples_exondes', 'saisonniere', 'saison_douce', 'kompong', 'Sanctuaires Exondés', 'Uncovered Shrines', 'visiter', 'temple_englouti_du_lac', 1),
        ('saisonniere_recolte_du_sucre', 'saisonniere', 'saison_chaude', 'ly', 'Sève de Saison Chaude', 'Hot Season Palm Sap', 'recolter', 'cambocraft:sucre_de_palme', 6),
        ('quotidienne_offrande_du_matin', 'quotidienne', '*', 'kompong', 'Offrande du Matin', 'Morning Offering', 'livrer', 'cambocraft:offrande_de_fleurs', 2),
        ('quotidienne_soupe_du_marche', 'quotidienne', '*', 'tonle', 'Marmite du Marché', 'Market Pot', 'cuisiner', 'cambocraft:nom_banh_chok', 1),
        ('quotidienne_patrouille_des_digues', 'quotidienne', '*', 'ly', 'Patrouille des Diguettes', 'Dike Patrol', 'vaincre', 'initie_ombre', 3),
        ('hebdomadaire_tournoi_pradal', 'hebdomadaire', '*', 'srok', 'Défi Hebdomadaire de Pradal Serey', 'Weekly Pradal Serey Challenge', 'vaincre', 'champion_arene', 1),
        ('hebdomadaire_regate_du_tonle', 'hebdomadaire', '*', 'tonle', 'Régate Hebdomadaire en Pirogue', 'Weekly Pirogue Regatta', 'visiter', 'embarcadere_course_royale', 1),
    ]
    for qid, qtype, saison, fac, fr_t, en_t, obj, cible, qte in repetables:
        ecrire_json(os.path.join(QUESTS_DIR, qid + '.json'), {
            'chapitre': 0,
            'type': qtype,
            'titre': {'fr': fr_t, 'en': en_t},
            'resume': {'fr': f"Quête {qtype} de la province de {fac.capitalize()}.", 'en': f"{qtype.capitalize()} quest in {fac.capitalize()} province."},
            'niveau_min': 1,
            'quete_requise': '',
            'saison_requise': saison,
            'faction': fac,
            'etapes': [
                {
                    'id': 'etape_unique',
                    'objectif': obj,
                    'cible': cible,
                    'quantite': qte,
                    'description': {'fr': f"Accomplir {qte}x {obj} ({cible}).", 'en': f"Complete {qte}x {obj} ({cible})."},
                }
            ],
            'branches': [],
            'recompenses': {
                'xp': 400,
                'pieces': 25,
                'reputation': 40,
                'objets': ['cambocraft:piece_du_lotus'],
                'pouvoir': '',
            },
        })
        nb += 1

    print(f"quêtes : {nb} fichiers JSON générés (1 intro, 12 chapitres principaux, 64 secondaires, 8 répétables)")
    return 0


if __name__ == '__main__':
    sys.exit(main())
