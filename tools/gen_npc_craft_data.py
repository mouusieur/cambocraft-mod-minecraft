#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Génère les données JSON des 16 PNJ, 16 arbres de dialogues à embranchements, 8 catalogues
marchands, 16 recettes de cuisine khmère à buffs, les recettes d'établi/four Minecraft 1.21.1
et la documentation complète docs/RECETTES.md (Phase 4).
"""
import json
import os
import sys

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(RACINE, 'src', 'main', 'resources', 'data', 'cambocraft')
CAMBO = os.path.join(DATA, 'cambo')


def ecrire_json(chemin, contenu):
    os.makedirs(os.path.dirname(chemin), exist_ok=True)
    with open(chemin, 'w', encoding='utf-8') as f:
        json.dump(contenu, f, indent=2, ensure_ascii=False, sort_keys=True)
        f.write('\n')


PNJS = [
    ('vendeur_de_riz', 'Sokha — Marchand de Riz', 'Sokha — Rice Merchant', 'tonle', 'chaleureux', 'vendeur_de_riz', True),
    ('vendeur_de_bambou', 'Vannak — Artisan du Bambou', 'Vannak — Bamboo Craftsman', 'srok', 'patient', 'vendeur_de_bambou', True),
    ('forgeron', 'Dara — Maître Forgeron', 'Dara — Master Blacksmith', 'srok', 'rigoureux', 'forgeron', False),
    ('pecheur_marchand', 'Rith — Pêcheur du Tonlé', 'Rith — Tonle Fisherman', 'tonle', 'jovial', 'pecheur_marchand', True),
    ('tisserand_de_soie', 'Maly — Maîtresse Tisserande', 'Maly — Master Silk Weaver', 'ly', 'minutieuse', 'tisserand_de_soie', False),
    ('cuisinier_de_rue', 'Bopha — Cuisinière du Marché', 'Bopha — Street Chef', 'tonle', 'généreuse', 'cuisinier_de_rue', False),
    ('sculpteur', 'Sovann — Sculpteur de Grès', 'Sovann — Sandstone Sculptor', 'kompong', 'contemplatif', 'sculpteur', False),
    ('apprenti_moine', 'Kosal — Apprenti Moine', 'Kosal — Apprentice Monk', 'tonle', 'paisible', '', False),
    ('maitre_de_quete', 'Ancien Chann — Chroniqueur du Village', 'Elder Chann — Village Chronicler', 'tonle', 'sage', '', False),
    ('guerisseur', 'Neary — Herboriste et Guérisseuse', 'Neary — Herbalist Healer', 'ly', 'attentive', 'guerisseur', False),
    ('guide_de_la_riziere', 'Phirun — Guide des Terrasses', 'Phirun — Terrace Guide', 'ly', 'observateur', '', True),
    ('gardien_du_temple', 'Narak — Gardien du Prasat', 'Narak — Prasat Guardian', 'kompong', 'intraitable', '', False),
    ('capitaine_de_pirogue', 'Sarith — Capitaine de Pirogue', 'Sarith — Pirogue Captain', 'tonle', 'aventureux', '', False),
    ('maitre_d_arene', 'Vireak — Maître de Pradal Serey', 'Vireak — Pradal Serey Master', 'srok', 'martial', '', False),
    ('conteur', 'Lok Ta Meas — Conteur des Anciens', 'Lok Ta Meas — Elder Storyteller', 'kompong', 'poétique', '', False),
    ('maitre_moine_du_nen', 'Vénérable Preah Thera — Maître du Nen', 'Venerable Preah Thera — Nen Master', 'kompong', 'serein', '', False),
]

CATALOGUES = {
    'vendeur_de_riz': ('tonle', [
        ('cambocraft:graine_de_riz', 4, 3, 'connu', True),
        ('cambocraft:riz_brut', 4, 5, 'connu', True),
        ('cambocraft:riz_cuit', 2, 7, 'connu', True),
        ('cambocraft:bol_de_riz', 1, 10, 'respecte', True),
        ('cambocraft:farine_de_riz', 2, 8, 'respecte', True),
        ('cambocraft:sucre_de_palme', 2, 9, 'honore', False),
        ('cambocraft:bol_de_riz_beni', 1, 45, 'honore', True),
    ]),
    'vendeur_de_bambou': ('srok', [
        ('cambocraft:bambou_brut', 8, 4, 'connu', False),
        ('cambocraft:fleche_en_bambou', 16, 8, 'connu', False),
        ('cambocraft:pique_en_bambou', 1, 18, 'respecte', False),
        ('cambocraft:arc_composite_en_bambou', 1, 32, 'respecte', False),
        ('cambocraft:bouclier_de_rotin', 1, 28, 'honore', False),
        ('cambocraft:arbalete_de_rotin', 1, 48, 'legendaire', False),
    ]),
    'forgeron': ('srok', [
        ('cambocraft:pierre_d_affutage', 1, 12, 'connu', False),
        ('cambocraft:dague_khmer', 1, 22, 'connu', False),
        ('cambocraft:epee_khmer', 1, 35, 'respecte', False),
        ('cambocraft:cristal_de_forge_mineur', 1, 30, 'respecte', False),
        ('cambocraft:lance_de_prasat', 1, 60, 'honore', False),
        ('cambocraft:cristal_de_forge_majeur', 1, 75, 'honore', False),
        ('cambocraft:sabre_de_garde_royal', 1, 120, 'legendaire', False),
        ('cambocraft:cristal_de_forge_parfait', 1, 180, 'legendaire', False),
    ]),
    'pecheur_marchand': ('tonle', [
        ('cambocraft:canne_a_pecher', 1, 16, 'connu', False),
        ('cambocraft:appat_de_riz_fermente', 4, 6, 'connu', True),
        ('cambocraft:filet_de_peche', 1, 20, 'respecte', False),
        ('cambocraft:canne_a_pecher_en_bambou_renforce', 1, 45, 'respecte', False),
        ('cambocraft:appat_de_crevette_doree', 4, 18, 'honore', False),
        ('cambocraft:canne_a_pecher_du_maitre', 1, 110, 'legendaire', False),
    ]),
    'tisserand_de_soie': ('ly', [
        ('cambocraft:feuille_de_murier', 4, 4, 'connu', False),
        ('cambocraft:fil_de_soie', 4, 10, 'connu', False),
        ('cambocraft:tissu_de_soie', 1, 22, 'respecte', False),
        ('cambocraft:krama_traditionnel', 1, 30, 'respecte', False),
        ('cambocraft:tunique_de_soie_et_lotus', 1, 85, 'honore', False),
        ('cambocraft:krama_de_soie_doree', 1, 140, 'legendaire', False),
    ]),
    'cuisinier_de_rue': ('tonle', [
        ('cambocraft:poisson_grille', 2, 8, 'connu', True),
        ('cambocraft:num_pang', 2, 12, 'connu', True),
        ('cambocraft:nom_banh_chok', 1, 16, 'respecte', True),
        ('cambocraft:amok', 1, 24, 'respecte', True),
        ('cambocraft:lok_lak', 1, 30, 'honore', True),
        ('cambocraft:crabe_au_poivre_vert', 1, 45, 'legendaire', False),
    ]),
    'sculpteur': ('kompong', [
        ('cambocraft:bloc_de_gres_brut', 4, 8, 'connu', False),
        ('cambocraft:maillet_de_sculpteur', 1, 18, 'connu', False),
        ('cambocraft:ciseau_de_sculpteur', 1, 18, 'connu', False),
        ('cambocraft:statuette_de_naga', 1, 40, 'respecte', False),
        ('cambocraft:statuette_d_apsara', 1, 55, 'honore', False),
        ('cambocraft:pierre_de_teleportation', 2, 65, 'legendaire', False),
    ]),
    'guerisseur': ('ly', [
        ('cambocraft:bandage_de_soie', 2, 14, 'connu', False),
        ('cambocraft:infusion_de_citronnelle', 2, 12, 'connu', False),
        ('cambocraft:onguent_de_lotus', 1, 28, 'respecte', False),
        ('cambocraft:infusion_de_lotus', 1, 32, 'honore', False),
        ('cambocraft:encens_beni', 2, 40, 'legendaire', False),
    ]),
}

CUISINE = [
    ('amok', 'Amok de Poisson au Coco', 'Fish Amok in Coconut Cream', 'cambocraft:amok',
     ['cambocraft:poisson_serpent', 'cambocraft:lait_de_coco', 'cambocraft:pate_de_kroeung_jaune'],
     'tonle', 'respecte', 'minecraft:regeneration', 1, 3600),
    ('nom_banh_chok', 'Nom Banh Chok aux Herbes', 'Herbal Nom Banh Chok', 'cambocraft:nom_banh_chok',
     ['cambocraft:nouilles_de_riz', 'cambocraft:poisson_chat', 'cambocraft:citronnelle_coupee'],
     'tonle', 'connu', 'minecraft:speed', 0, 2400),
    ('lok_lak', 'Lok Lak au Poivre de Kampot', 'Kampot Pepper Lok Lak', 'cambocraft:lok_lak',
     ['cambocraft:riz_cuit', 'cambocraft:poivre_noir_de_kampot', 'cambocraft:ail_grille'],
     'srok', 'honore', 'minecraft:strength', 1, 3600),
    ('bai_sach_chrouk', 'Bai Sach Chrouk Matinal', 'Morning Bai Sach Chrouk', 'cambocraft:bai_sach_chrouk',
     ['cambocraft:riz_cuit', 'cambocraft:sucre_de_palme', 'cambocraft:ail_grille'],
     'ly', 'connu', 'minecraft:haste', 0, 2400),
    ('num_pang', 'Num Pang Croustillant', 'Crispy Num Pang', 'cambocraft:num_pang',
     ['cambocraft:farine_de_riz', 'cambocraft:papaye_verte', 'cambocraft:piment_rouge'],
     'tonle', 'connu', 'minecraft:saturation', 0, 1200),
    ('kuy_teav', 'Soupe Kuy Teav du Port', 'Harbor Kuy Teav Soup', 'cambocraft:kuy_teav',
     ['cambocraft:nouilles_de_riz', 'cambocraft:crevette_de_riviere', 'cambocraft:ail_grille'],
     'tonle', 'respecte', 'minecraft:water_breathing', 0, 3600),
    ('samlor_korko', 'Samlor Korko Traditionnel', 'Traditional Samlor Korko', 'cambocraft:samlor_korko',
     ['cambocraft:pate_de_prahok', 'cambocraft:papaye_verte', 'cambocraft:pate_de_kroeung_vert'],
     'ly', 'respecte', 'minecraft:resistance', 0, 3600),
    ('samlor_machu', 'Soupe Aigre Samlor Machu', 'Sour Samlor Machu', 'cambocraft:samlor_machu',
     ['cambocraft:poisson_tilapia', 'cambocraft:tamarin_gousse', 'cambocraft:citronnelle_coupee'],
     'tonle', 'respecte', 'minecraft:luck', 0, 3600),
    ('prahok_ktis', 'Prahok Ktis au Lait de Coco', 'Coconut Prahok Ktis', 'cambocraft:prahok_ktis',
     ['cambocraft:pate_de_prahok', 'cambocraft:lait_de_coco', 'cambocraft:piment_rouge'],
     'ly', 'honore', 'minecraft:resistance', 1, 3600),
    ('pleah_sach_ko', 'Salade Citronnée au Combava', 'Kaffir Lime Salad', 'cambocraft:pleah_sach_ko',
     ['cambocraft:combava_zeste', 'cambocraft:citronnelle_coupee', 'cambocraft:piment_rouge'],
     'srok', 'respecte', 'minecraft:speed', 1, 3000),
    ('banh_chao', 'Crêpe Dorée au Curcuma', 'Golden Turmeric Crepe', 'cambocraft:banh_chao',
     ['cambocraft:farine_de_riz', 'cambocraft:curcuma_racine', 'cambocraft:crevette'],
     'ly', 'connu', 'minecraft:jump_boost', 0, 2400),
    ('kralan', 'Kralan de Riz Gluant en Bambou', 'Bamboo Sticky Rice Kralan', 'cambocraft:kralan',
     ['cambocraft:riz_gluant', 'cambocraft:noix_de_coco', 'cambocraft:bambou_brut'],
     'srok', 'connu', 'minecraft:absorption', 0, 3000),
    ('num_ansorm', 'Gâteau de Fête Num Ansorm', 'Festive Num Ansorm Cake', 'cambocraft:num_ansorm',
     ['cambocraft:riz_gluant', 'cambocraft:banane_plantain', 'cambocraft:lait_de_coco'],
     'kompong', 'honore', 'minecraft:Health_Boost'.lower(), 1, 4800),
    ('num_kom', 'Pyramide Sucrée Num Kom', 'Sweet Num Kom Pyramid', 'cambocraft:num_kom',
     ['cambocraft:farine_de_riz', 'cambocraft:sucre_de_palme', 'cambocraft:noix_de_coco'],
     'kompong', 'respecte', 'minecraft:regeneration', 0, 3000),
    ('crabe_au_poivre_vert', 'Crabe au Poivre Vert Frais', 'Fresh Green Pepper Crab', 'cambocraft:crabe_au_poivre_vert',
     ['cambocraft:crabe_de_riziaire', 'cambocraft:poivre_vert_frais', 'cambocraft:ail_grille'],
     'tonle', 'legendaire', 'minecraft:strength', 1, 4800),
    ('infusion_de_lotus', 'Infusion Sacrée de Lotus', 'Sacred Lotus Infusion', 'cambocraft:infusion_de_lotus',
     ['cambocraft:lotus_brut', 'cambocraft:graine_de_lotus', 'cambocraft:sucre_de_palme'],
     'kompong', 'honore', 'minecraft:regeneration', 1, 4200),
]


def main():
    for ident, fr, en, faction, perso, marchand, deguise in PNJS:
        ecrire_json(os.path.join(CAMBO, 'npcs', ident + '.json'), {
            'nom': {'fr': fr, 'en': en},
            'role': ident,
            'faction': faction,
            'personnalite': perso,
            'marchand_id': marchand,
            'dialogue_initial': ident,
            'peut_etre_ninja_deguise': deguise,
            'routine': [
                {'debut_tick': 0, 'fin_tick': 2500, 'activite': 'salut_matinal_et_offrande', 'lieu': 'pagode_ou_foyer'},
                {'debut_tick': 2500, 'fin_tick': 9000, 'activite': 'travail_principal', 'lieu': 'atelier_ou_marche'},
                {'debut_tick': 9000, 'fin_tick': 13000, 'activite': 'echanges_et_conseils', 'lieu': 'place_du_village'},
                {'debut_tick': 13000, 'fin_tick': 24000, 'activite': 'repos_sous_pilotis', 'lieu': 'maison_pilotis'},
            ],
        })

        ecrire_json(os.path.join(CAMBO, 'dialogues', ident + '.json'), {
            'pnj': ident,
            'noeud_initial': 'accueil',
            'noeuds': {
                'accueil': {
                    'texte': {
                        'fr': f"« Choumreap sour, voyageur. Je suis {fr}. Que cherches-tu dans la province de {faction.capitalize()} ? »",
                        'en': f"\"Choumreap sour, traveler. I am {en}. What brings you to {faction.capitalize()} province?\"",
                    },
                    'choix': [
                        {
                            'reponse': {'fr': 'Parlez-moi de votre métier et de la saison.', 'en': 'Tell me about your craft and the current season.'},
                            'suivant': 'conseil',
                            'reputation_min': -10000,
                            'quete_requise': '',
                            'action': '',
                        },
                        {
                            'reponse': {'fr': 'Montrez-moi vos spécialités réservées aux amis du village.', 'en': 'Show me the specialties reserved for trusted friends.'},
                            'suivant': 'confiance',
                            'reputation_min': 200,
                            'quete_requise': '',
                            'action': 'ouvrir_marchand' if marchand else 'indice_quete',
                        },
                        {
                            'reponse': {'fr': 'Je souhaite faire amende honorable auprès du village.', 'en': 'I wish to make amends with the village.'},
                            'suivant': 'redemption',
                            'reputation_min': -10000,
                            'quete_requise': '',
                            'action': 'offrande_reputation',
                        },
                    ],
                },
                'conseil': {
                    'texte': {
                        'fr': "« Lorsque la mousson gonfle le Grand Lac, les rizières prospèrent mais les sentiers deviennent glissants. Prépare tes provisions avant le départ. »",
                        'en': "\"When the monsoon swells the Great Lake, the paddies thrive but the paths grow slick. Prepare your supplies before setting out.\"",
                    },
                    'choix': [],
                },
                'confiance': {
                    'texte': {
                        'fr': "« Ta parole est respectée parmi nous. Voici ce que nous réservons à ceux qui protègent nos foyers. »",
                        'en': "\"Your word is respected among us. Here is what we keep for those who protect our homes.\"",
                    },
                    'choix': [],
                },
                'redemption': {
                    'texte': {
                        'fr': "« Une offrande sincère de fleurs de lotus et l'aide apportée aux rizières apaisent toujours les cœurs. »",
                        'en': "\"A sincere lotus flower offering and helping in the rice fields always restore harmony.\"",
                    },
                    'choix': [],
                },
            },
        })

    for m_id, (faction, offres) in CATALOGUES.items():
        ecrire_json(os.path.join(CAMBO, 'merchants', m_id + '.json'), {
            'pnj': m_id,
            'faction': faction,
            'offres': [
                {
                    'item': it,
                    'quantite': qte,
                    'prix_base': prix,
                    'palier_min': pal,
                    'sensible_au_riz': riz,
                }
                for (it, qte, prix, pal, riz) in offres
            ],
        })

    for (cid, fr, en, res, ingr, fac, pal, eff, amp, dur) in CUISINE:
        ecrire_json(os.path.join(CAMBO, 'cooking', cid + '.json'), {
            'nom': {'fr': fr, 'en': en},
            'resultat': res,
            'ingredients': ingr,
            'faction': fac,
            'palier_min': pal,
            'buff_effet': eff,
            'buff_amplificateur': amp,
            'buff_duree_ticks': dur,
        })

    # Recettes d'établi clés (portail, bol béni, outils, krama, armes)
    recettes_vanilla = {
        'cadre_du_portail': {
            'type': 'minecraft:crafting_shaped',
            'pattern': ['PLP', 'LCL', 'PLP'],
            'key': {
                'P': {'item': 'cambocraft:pierre_du_lotus'},
                'L': {'item': 'cambocraft:lingot_de_lotus'},
                'C': {'item': 'cambocraft:cristal_de_lotus'},
            },
            'result': {'id': 'cambocraft:cadre_du_portail', 'count': 4},
        },
        'bol_de_riz_beni': {
            'type': 'minecraft:crafting_shapeless',
            'ingredients': [
                {'item': 'cambocraft:bol_de_riz'},
                {'item': 'cambocraft:fleur_de_lotus'},
                {'item': 'cambocraft:poudre_de_lotus'},
            ],
            'result': {'id': 'cambocraft:bol_de_riz_beni', 'count': 1},
        },
        'carnet_du_nen': {
            'type': 'minecraft:crafting_shapeless',
            'ingredients': [
                {'item': 'minecraft:book'},
                {'item': 'cambocraft:fil_de_soie'},
                {'item': 'cambocraft:fleur_de_lotus'},
            ],
            'result': {'id': 'cambocraft:carnet_du_nen', 'count': 1},
        },
        'canne_a_pecher': {
            'type': 'minecraft:crafting_shaped',
            'pattern': ['  B', ' BS', 'B S'],
            'key': {
                'B': {'item': 'cambocraft:bambou_brut'},
                'S': {'item': 'cambocraft:fil_de_soie'},
            },
            'result': {'id': 'cambocraft:canne_a_pecher', 'count': 1},
        },
        'epee_khmer': {
            'type': 'minecraft:crafting_shaped',
            'pattern': [' L ', ' L ', ' T '],
            'key': {
                'L': {'item': 'cambocraft:lingot_de_lotus'},
                'T': {'item': 'cambocraft:teck_planks'},
            },
            'result': {'id': 'cambocraft:epee_khmer', 'count': 1},
        },
    }
    for nom_r, spec_r in recettes_vanilla.items():
        ecrire_json(os.path.join(DATA, 'recipe', nom_r + '.json'), spec_r)

    # Documentation docs/RECETTES.md
    lignes_doc = [
        "# Catalogue des recettes, de l'économie et de l'artisanat — Cambocraft",
        "",
        "Ce document regroupe les recettes d'établi essentielles, les **16 recettes de cuisine khmère**",
        "(débloquées par réputation), la **chaîne de la soie**, la **sculpture sur grès**, le système",
        "de **forge +1 à +5** et la grille tarifaire des **8 marchands** en *Pièces du Lotus*.",
        "",
        "## 1. Recettes fondamentales (Accès à la dimension et Nen)",
        "",
        "| Objet | Type | Ingrédients | Résultat |",
        "|---|---|---|---|",
        "| **Cadre du Portail** (`cadre_du_portail`) | Établi 3×3 | 4× `pierre_du_lotus`, 4× `lingot_de_lotus`, 1× `cristal_de_lotus` | 4× Cadre du Portail |",
        "| **Bol de Riz Béni** (`bol_de_riz_beni`) | Sans forme | 1× `bol_de_riz` + 1× `fleur_de_lotus` + 1× `poudre_de_lotus` (ou via Apprenti Moine) | 1× Bol de Riz Béni |",
        "| **Carnet du Nen** (`carnet_du_nen`) | Sans forme | 1× `minecraft:book` + 1× `fil_de_soie` + 1× `fleur_de_lotus` | 1× Carnet du Nen |",
        "| **Canne à Pêcher** (`canne_a_pecher`) | Établi 3×3 | 3× `bambou_brut` + 2× `fil_de_soie` | 1× Canne à Pêcher |",
        "| **Épée Khmère** (`epee_khmer`) | Établi 3×3 | 2× `lingot_de_lotus` + 1× `teck_planks` | 1× Épée Khmère |",
        "",
        "## 2. Les 16 recettes de cuisine khmère (`data/cambocraft/cambo/cooking/`)",
        "",
        "| Plat | Faction | Palier requis | Ingrédients | Effet (Buff) | Durée |",
        "|---|---|---|---|---|---|",
    ]
    for cid, fr, en, res, ingr, fac, pal, eff, amp, dur in CUISINE:
        lignes_doc.append(f"| **{fr}** (`{cid}`) | `{fac}` | `{pal}` | {', '.join(f'`{i}`' for i in ingr)} | `{eff}` (niv. {amp + 1}) | {dur // 20} s |")

    lignes_doc.extend([
        "",
        "## 3. Chaîne de la soie (`SilkChain`) et teintures naturelles",
        "",
        "1. `3× feuille_de_murier` (élevage du `ver_a_soie`) → `2× cocon_de_soie`",
        "2. `2× cocon_de_soie` (`cuve_de_soie`) → `4× fil_de_soie`",
        "3. `4× fil_de_soie` (`cuve_de_soie`) → `1× echeveau_de_soie_ecrue`",
        "4. Teinture : `curcuma_racine` → Or · `fleur_d_hibiscus` → Pourpre · `fleur_de_bougainvillier` → Indigo · `feuille_de_lotus` → Émeraude",
        "5. Tissage (`metier_a_tisser`) : `tissu_de_soie`, `krama_traditionnel`, `krama_de_soie_doree`, tenues de soie et lotus.",
        "",
        "## 4. Forge et renforcement d'équipement (`+1` à `+5`, `ForgeUpgrade`)",
        "",
        "| Niveau cible | Multiplicateur stats | Chance de succès | Cristal requis | Coût (Pièces du Lotus) |",
        "|---|---|---|---|---|",
        "| **+1** | ×1,08 (+8 %) | 100 % | `cristal_de_forge_mineur` | 15 |",
        "| **+2** | ×1,16 (+16 %) | 85 % | `cristal_de_forge_mineur` | 35 |",
        "| **+3** | ×1,25 (+25 %) | 70 % | `cristal_de_forge_majeur` | 75 |",
        "| **+4** | ×1,36 (+36 %) | 50 % | `cristal_de_forge_majeur` | 150 |",
        "| **+5** | ×1,50 (+50 %) | 35 % | `cristal_de_forge_parfait` | 300 |",
        "",
        "## 5. Catalogues des 8 marchands PNJ (`data/cambocraft/cambo/merchants/`)",
        "",
        "| Marchand | Faction | Offres principales (prix de base en Pièces du Lotus) |",
        "|---|---|---|",
    ])
    for m_id, (fac, offres) in CATALOGUES.items():
        desc = ", ".join(f"`{it.split(':')[1]}` ×{q} ({p} p., `{pal}`)" for it, q, p, pal, _ in offres)
        lignes_doc.append(f"| `{m_id}` | `{fac}` | {desc} |")

    with open(os.path.join(RACINE, 'docs', 'RECETTES.md'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(lignes_doc) + '\n')

    print(f"pnj & artisanat : {len(PNJS)} PNJ, {len(PNJS)} dialogues, {len(CATALOGUES)} marchands, {len(CUISINE)} plats khmers")
    return 0


if __name__ == '__main__':
    sys.exit(main())
