#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Générateur déterministe des données de la Phase 6 :
  - 84 pouvoirs du Nen (22 communs, 20 rares, 18 épiques, 14 légendaires, 10 mythiques)
    sous src/main/resources/data/cambocraft/cambo/powers/*.json
  - 34 synergies sous src/main/resources/data/cambocraft/cambo/synergies/*.json
  - 56 ennemis et boss (44 ennemis de zone/ninjas + 12 boss multi-phases)
    sous src/main/resources/data/cambocraft/cambo/enemies/*.json
  - 4 événements saisonniers sous src/main/resources/data/cambocraft/cambo/events/*.json
  - Modèles hiérarchiques d'entités et courbes d'animation sous
    src/main/resources/assets/cambocraft/{models/entity,animations}/*.json
"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CAMBO_DIR = ROOT / "src" / "main" / "resources" / "data" / "cambocraft" / "cambo"
ASSETS_DIR = ROOT / "src" / "main" / "resources" / "assets" / "cambocraft"


def write_json(path: Path, data: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


# ---------------------------------------------------------------------------
# 1. Catalogue de 84 pouvoirs du Nen (22 C, 20 R, 18 E, 14 L, 10 M)
# ---------------------------------------------------------------------------
POWERS_SPEC = [
    # === COMMUN (22) ===
    ("onde_du_tonle", "Onde du Tonlé", "Tonle Ripple", "commun", "eau", "offensif", 7.0, 0.0, 6.0, 0, 40, 14),
    ("goutte_apaisante", "Goutte Apaisante", "Soothing Droplet", "commun", "eau", "soin", 0.0, 5.0, 4.0, 40, 60, 16),
    ("pas_de_brume", "Pas de Brume", "Mist Step", "commun", "eau", "mobilite", 0.0, 0.0, 7.0, 30, 50, 12),
    ("braise_du_foyer", "Braise du Foyer", "Hearth Ember", "commun", "feu", "offensif", 7.5, 0.0, 5.5, 40, 42, 15),
    ("tison_ardent", "Tison Ardent", "Glowing Firebrand", "commun", "feu", "utilitaire", 3.0, 0.0, 5.0, 100, 60, 12),
    ("garde_de_cendre", "Garde de Cendre", "Ash Guard", "commun", "feu", "defensif", 0.0, 3.0, 3.0, 80, 70, 15),
    ("souffle_des_palmes", "Souffle des Palmes", "Palm Breeze", "commun", "air", "offensif", 6.5, 0.0, 7.5, 20, 36, 13),
    ("bond_du_bambou", "Bond du Bambou", "Bamboo Leap", "commun", "air", "mobilite", 0.0, 0.0, 8.0, 30, 44, 12),
    ("plume_legere", "Plume Légère", "Feather Poise", "commun", "air", "passif", 0.0, 2.0, 0.0, 120, 80, 10),
    ("eclat_de_laterite", "Éclat de Latérite", "Laterite Shard", "commun", "terre", "offensif", 8.0, 0.0, 5.0, 20, 45, 15),
    ("peau_d_argile", "Peau d'Argile", "Clay Skin", "commun", "terre", "defensif", 0.0, 4.0, 2.0, 100, 75, 16),
    ("ancrage_des_digues", "Ancrage des Digues", "Dike Anchor", "commun", "terre", "controle", 4.0, 0.0, 4.5, 50, 65, 15),
    ("voile_d_encre", "Voile d'Encre", "Ink Veil", "commun", "ombre", "utilitaire", 2.0, 0.0, 5.0, 60, 55, 13),
    ("griffe_nocturne", "Griffe Nocturne", "Night Claw", "commun", "ombre", "offensif", 7.8, 0.0, 4.0, 20, 38, 14),
    ("pas_feutre", "Pas Feutré", "Muffled Footstep", "commun", "ombre", "mobilite", 0.0, 0.0, 6.5, 45, 48, 12),
    ("lueur_d_encens", "Lueur d'Encens", "Incense Glow", "commun", "lumiere", "soin", 0.0, 5.5, 4.5, 60, 60, 15),
    ("etincelle_du_wat", "Étincelle du Wat", "Wat Spark", "commun", "lumiere", "offensif", 7.2, 0.0, 6.5, 20, 40, 14),
    ("regard_clair", "Regard Clair", "Clear Gaze", "commun", "lumiere", "utilitaire", 0.0, 2.0, 8.0, 100, 70, 12),
    ("bourgeon_de_riz", "Bourgeon de Riz", "Rice Sprout", "commun", "vie", "soin", 0.0, 6.0, 4.0, 50, 58, 15),
    ("racine_tenace", "Racine Tenace", "Tenacious Root", "commun", "vie", "controle", 4.5, 0.0, 5.5, 45, 60, 14),
    ("seve_vivifiante", "Sève Vivifiante", "Reviving Sap", "commun", "vie", "passif", 0.0, 4.0, 0.0, 120, 80, 12),
    ("lucioles_du_marais", "Lucioles du Marais", "Marsh Fireflies", "commun", "vie", "invocation", 5.0, 2.5, 6.0, 80, 70, 16),

    # === RARE (20) ===
    ("courant_du_mekong", "Courant du Mékong", "Mekong Current", "rare", "eau", "offensif", 11.0, 0.0, 7.5, 30, 48, 20),
    ("carapace_du_crabe", "Carapace du Crabe", "River Crab Shell", "rare", "eau", "defensif", 2.0, 6.0, 3.0, 100, 72, 22),
    ("brume_du_grand_lac", "Brume du Grand Lac", "Great Lake Fog", "rare", "eau", "controle", 6.0, 0.0, 7.0, 70, 65, 21),
    ("flamme_de_forge", "Flamme de Forge", "Forge Flame", "rare", "feu", "offensif", 12.0, 0.0, 6.0, 50, 46, 22),
    ("souffle_de_braise", "Souffle de Braise", "Cinder Breath", "rare", "feu", "controle", 8.5, 0.0, 5.5, 60, 58, 20),
    ("ardeur_du_pradal", "Ardeur du Pradal", "Pradal Fervor", "rare", "feu", "passif", 5.0, 0.0, 0.0, 140, 85, 18),
    ("rafale_des_falaises", "Rafale des Falaises", "Cliff Gust", "rare", "air", "offensif", 10.5, 0.0, 8.5, 25, 42, 19),
    ("tourbillon_de_feuilles", "Tourbillon de Feuilles", "Leaf Whirlwind", "rare", "air", "defensif", 4.0, 3.0, 4.5, 80, 64, 20),
    ("elan_du_gibbon", "Élan du Gibbon", "Gibbon Vault", "rare", "air", "mobilite", 3.0, 0.0, 10.0, 40, 45, 18),
    ("pilier_de_gres", "Pilier de Grès", "Sandstone Pillar", "rare", "terre", "offensif", 12.5, 0.0, 5.5, 40, 52, 22),
    ("rempart_de_laterite", "Rempart de Latérite", "Laterite Bulwark", "rare", "terre", "defensif", 0.0, 8.0, 3.5, 110, 78, 24),
    ("secousse_des_terrasses", "Secousse des Terrasses", "Terrace Tremor", "rare", "terre", "controle", 7.5, 0.0, 6.0, 55, 66, 22),
    ("lame_du_crepuscule", "Lame du Crépuscule", "Dusk Blade", "rare", "ombre", "offensif", 12.2, 0.0, 5.0, 30, 44, 20),
    ("saut_de_l_ombre", "Saut de l'Ombre", "Shadow Blink", "rare", "ombre", "mobilite", 4.0, 0.0, 9.5, 35, 48, 19),
    ("chaine_d_ardoise", "Chaîne d'Ardoise", "Slate Shackle", "rare", "ombre", "controle", 6.5, 0.0, 6.5, 60, 62, 21),
    ("rayon_du_prasat", "Rayon du Prasat", "Prasat Beam", "rare", "lumiere", "offensif", 11.5, 0.0, 8.0, 25, 46, 20),
    ("benediction_du_moine", "Bénédiction du Moine", "Monk Blessing", "rare", "lumiere", "soin", 0.0, 9.5, 5.5, 80, 64, 22),
    ("halo_de_sagesse", "Halo de Sagesse", "Wisdom Halo", "rare", "lumiere", "utilitaire", 0.0, 4.0, 7.0, 120, 75, 18),
    ("fleur_de_lotus_rose", "Fleur de Lotus Rose", "Pink Lotus Bloom", "rare", "vie", "soin", 0.0, 10.0, 6.0, 90, 66, 22),
    ("esprit_du_banyan", "Esprit du Banyan", "Banyan Spirit", "rare", "vie", "invocation", 8.0, 4.5, 6.5, 100, 76, 24),

    # === ÉPIQUE (18) ===
    ("crue_de_mousson", "Crue de Mousson", "Monsoon Flood", "epique", "eau", "offensif", 17.5, 0.0, 9.0, 50, 56, 28),
    ("tourbillon_du_naga", "Tourbillon du Naga", "Naga Vortex", "epique", "eau", "controle", 12.0, 0.0, 8.0, 80, 68, 30),
    ("source_du_temple_englouti", "Source du Temple Englouti", "Sunken Temple Spring", "epique", "eau", "soin", 0.0, 15.0, 7.0, 100, 70, 28),
    ("colere_du_phenix", "Colère du Phénix", "Phoenix Wrath", "epique", "feu", "offensif", 19.0, 0.0, 7.5, 60, 58, 30),
    ("armure_de_braise_sacree", "Armure de Braise Sacrée", "Sacred Ember Armor", "epique", "feu", "defensif", 6.0, 10.0, 4.0, 120, 80, 30),
    ("danse_des_flammes_khmeres", "Danse des Flammes Khmères", "Khmer Flame Dance", "epique", "feu", "mobilite", 11.0, 0.0, 9.5, 50, 54, 26),
    ("lance_de_foudre_celeste", "Lance de Foudre Céleste", "Celestial Lightning Spear", "epique", "air", "offensif", 18.5, 0.0, 11.0, 35, 52, 28),
    ("ailes_du_vent_karstique", "Ailes du Vent Karstique", "Karst Wind Wings", "epique", "air", "mobilite", 5.0, 0.0, 12.5, 80, 56, 26),
    ("oeil_du_cyclone", "Œil du Cyclone", "Eye of the Cyclone", "epique", "air", "controle", 11.5, 0.0, 8.5, 75, 66, 28),
    ("poing_du_colosse_de_pierre", "Poing du Colosse de Pierre", "Stone Colossus Fist", "epique", "terre", "offensif", 20.0, 0.0, 6.0, 45, 60, 32),
    ("bastion_d_angkor", "Bastion d'Angkor", "Bastion of Angkor", "epique", "terre", "defensif", 0.0, 14.0, 5.0, 140, 84, 32),
    ("lames_jumelles_de_l_ombre", "Lames Jumelles de l'Ombre", "Twin Shadow Blades", "epique", "ombre", "offensif", 18.8, 0.0, 6.5, 40, 50, 28),
    ("mirage_du_clan_ombre", "Mirage du Clan Ombre", "Shadow Clan Mirage", "epique", "ombre", "invocation", 13.0, 0.0, 8.0, 100, 72, 30),
    ("prison_de_nuit", "Prison de Nuit", "Nightfall Prison", "epique", "ombre", "controle", 10.5, 0.0, 7.5, 85, 68, 28),
    ("lance_solaire_du_sanctuaire", "Lance Solaire du Sanctuaire", "Sanctuary Sun Spear", "epique", "lumiere", "offensif", 18.0, 0.0, 10.0, 40, 54, 28),
    ("choeur_des_apsaras", "Chœur des Apsaras", "Chorus of Apsaras", "epique", "lumiere", "soin", 0.0, 16.0, 8.0, 110, 74, 30),
    ("chant_de_la_foret_sacree", "Chant de la Forêt Sacrée", "Sacred Forest Song", "epique", "vie", "soin", 4.0, 15.5, 8.5, 120, 72, 30),
    ("gardien_de_kapokier", "Gardien de Kapokier", "Kapok Tree Guardian", "epique", "vie", "invocation", 14.0, 8.0, 7.5, 130, 80, 32),

    # === LÉGENDAIRE (14) ===
    ("fureur_du_naga_ a_sept_tetes".replace(" ", ""), "Fureur du Naga à Sept Têtes", "Seven-Headed Naga Fury", "legendaire", "eau", "offensif", 27.0, 0.0, 11.0, 70, 68, 38),
    ("sanctuaire_des_eaux_eternelles", "Sanctuaire des Eaux Éternelles", "Eternal Waters Sanctuary", "legendaire", "eau", "soin", 6.0, 23.0, 9.5, 140, 82, 40),
    ("renaissance_du_phenix_de_laterite", "Renaissance du Phénix de Latérite", "Laterite Phoenix Rebirth", "legendaire", "feu", "soin", 18.0, 20.0, 8.0, 140, 90, 42),
    ("soleil_ardent_de_sovann", "Soleil Ardent de Sovann", "Blazing Sun of Sovann", "legendaire", "feu", "offensif", 29.0, 0.0, 10.0, 65, 70, 40),
    ("tempete_du_roi_garuda", "Tempête du Roi Garuda", "Garuda King Storm", "legendaire", "air", "offensif", 28.0, 0.0, 13.0, 70, 66, 38),
    ("ascension_des_iles_celestes", "Ascension des Îles Célestes", "Celestial Isles Ascension", "legendaire", "air", "mobilite", 12.0, 0.0, 15.0, 110, 64, 36),
    ("jugement_des_quatre_visages", "Jugement des Quatre Visages", "Four Faces Judgment", "legendaire", "terre", "controle", 22.0, 0.0, 10.0, 100, 76, 40),
    ("citadelle_vivante_de_gres", "Citadelle Vivante de Grès", "Living Sandstone Citadel", "legendaire", "terre", "defensif", 8.0, 22.0, 6.5, 160, 88, 42),
    ("eclipse_du_generalissime", "Éclipse du Généralissime", "Generalissimo Eclipse", "legendaire", "ombre", "offensif", 28.5, 0.0, 9.5, 65, 66, 38),
    ("legion_des_ombres_silencieuses", "Légion des Ombres Silencieuses", "Silent Shadows Legion", "legendaire", "ombre", "invocation", 24.0, 0.0, 10.0, 140, 84, 42),
    ("couronne_de_lumiere_d_angkor", "Couronne de Lumière d'Angkor", "Angkor Crown of Light", "legendaire", "lumiere", "defensif", 10.0, 20.0, 8.5, 150, 82, 40),
    ("grace_celeste_de_l_apsara", "Grâce Céleste de l'Apsara", "Celestial Apsara Grace", "legendaire", "lumiere", "soin", 0.0, 25.0, 10.0, 140, 78, 38),
    ("arbre_monde_du_tonle", "Arbre-Monde du Tonlé", "World-Tree of Tonle", "legendaire", "vie", "invocation", 18.0, 18.0, 10.0, 160, 86, 42),
    ("souffle_primordial_des_rizieres", "Souffle Primordial des Rizières", "Primordial Paddy Breath", "legendaire", "vie", "passif", 10.0, 16.0, 8.0, 180, 80, 36),

    # === MYTHIQUE (10) ===
    ("barattage_de_la_mer_de_lait", "Barattage de la Mer de Lait", "Churning of the Sea of Milk", "mythique", "eau", "offensif", 40.0, 12.0, 14.0, 120, 80, 50),
    ("flamme_eternelle_du_lotus_d_or", "Flamme Éternelle du Lotus d'Or", "Golden Lotus Eternal Flame", "mythique", "feu", "offensif", 42.0, 10.0, 13.0, 110, 80, 52),
    ("souverain_des_cieux_garuda", "Souverain des Cieux Garuda", "Garuda Sovereign of Skies", "mythique", "air", "invocation", 38.0, 8.0, 16.0, 150, 84, 50),
    ("eveil_du_temple_millenaire", "Éveil du Temple Millénaire", "Millennial Temple Awakening", "mythique", "terre", "defensif", 24.0, 30.0, 10.0, 180, 90, 54),
    ("abime_de_l_ombre_premiere", "Abîme de l'Ombre Première", "First Shadow Abyss", "mythique", "ombre", "offensif", 44.0, 0.0, 13.5, 110, 78, 52),
    ("voile_absolu_du_neant", "Voile Absolu du Néant", "Absolute Veil of Void", "mythique", "ombre", "controle", 32.0, 0.0, 12.5, 130, 84, 50),
    ("aube_sacree_du_cambodge", "Aube Sacrée du Cambodge", "Sacred Dawn of Cambodia", "mythique", "lumiere", "soin", 20.0, 36.0, 14.0, 160, 86, 52),
    ("jugement_radieux_du_lotus", "Jugement Radieux du Lotus", "Radiant Lotus Judgment", "mythique", "lumiere", "offensif", 41.0, 10.0, 14.0, 110, 80, 50),
    ("harmonie_du_bol_sacree", "Harmonie du Bol Sacré", "Sacred Bowl Harmony", "mythique", "vie", "passif", 22.0, 26.0, 12.0, 200, 76, 48),
    ("avatar_du_neak_ta_ancestral", "Avatar du Neak Ta Ancestral", "Ancestral Neak Ta Avatar", "mythique", "vie", "invocation", 36.0, 24.0, 13.0, 180, 88, 54),
]


def build_power_json(spec: tuple) -> dict:
    pid, fr, en, rarete, element, ptype, dmg, heal, rng, dur, cd, cost = spec
    niveaux = []
    for lvl in range(1, 6):
        scale = 1.0 + 0.22 * (lvl - 1)
        cd_scale = max(10, int(round(cd * (1.0 - 0.05 * (lvl - 1)))))
        niveaux.append({
            "niveau": lvl,
            "degats": round(dmg * scale, 2),
            "soin": round(heal * scale, 2),
            "portee": round(rng * (1.0 + 0.08 * (lvl - 1)), 2),
            "duree_ticks": int(round(dur * (1.0 + 0.12 * (lvl - 1)))),
            "recharge_ticks": cd_scale,
            "cout_energie": cost,
        })
    seuil_map = {"commun": 60, "rare": 90, "epique": 130, "legendaire": 180, "mythique": 240}
    bonus_map = {"commun": 0.18, "rare": 0.22, "epique": 0.26, "legendaire": 0.30, "mythique": 0.35}
    return {
        "nom": {"fr": fr, "en": en},
        "description": {
            "fr": f"Pouvoir spirituel du Nen ({element}, {ptype}) : {fr}. Évolue du niveau 1 au niveau 5 et débloque sa Maîtrise par la pratique.",
            "en": f"Spiritual Nen power ({element}, {ptype}): {en}. Scales from level 1 to 5 and unlocks Mastery through practice.",
        },
        "rarete": rarete,
        "element": element,
        "type": ptype,
        "niveaux": niveaux,
        "maitrise": {
            "seuil": seuil_map[rarete],
            "variante_fr": f"{fr} — Éveil du Maître",
            "variante_en": f"{en} — Master Awakening",
            "bonus": bonus_map[rarete],
        },
    }


# ---------------------------------------------------------------------------
# 2. Catalogue de 34 synergies (paires de pouvoirs distincts)
# ---------------------------------------------------------------------------
SYNERGIES_SPEC = [
    ("synergie_vapeur_du_tonle", "Vapeur Brûlante du Tonlé", "Scalding Tonle Steam", "onde_du_tonle", "braise_du_foyer", "nuage_de_vapeur", 1.22, 160),
    ("synergie_brume_et_pas", "Marcheur de la Brume", "Mist Walker", "pas_de_brume", "voile_d_encre", "furtivite_aquatique", 1.20, 180),
    ("synergie_lotus_et_encens", "Offrande du Matin Calme", "Calm Morning Offering", "lueur_d_encens", "bourgeon_de_riz", "regeneration_douce", 1.24, 200),
    ("synergie_digue_et_racine", "Rempart des Rizières", "Paddy Bulwark", "ancrage_des_digues", "racine_tenace", "enracinement_profond", 1.23, 180),
    ("synergie_vent_et_braise", "Attisement des Palmes", "Palm Fanning", "souffle_des_palmes", "tison_ardent", "embrasement_rapide", 1.22, 160),
    ("synergie_bambou_et_plume", "Acrobate de la Bambouseraie", "Bamboo Grove Acrobat", "bond_du_bambou", "plume_legere", "agilite_aerienne", 1.20, 200),
    ("synergie_argile_et_cendre", "Cuirasse de Terre Cuite", "Terracotta Cuirass", "peau_d_argile", "garde_de_cendre", "armure_minerale", 1.25, 220),
    ("synergie_griffe_et_pas", "Traqueur Nocturne", "Night Stalker", "griffe_nocturne", "pas_feutre", "embuscade_critique", 1.24, 160),
    ("synergie_wat_et_regard", "Clairvoyance du Sanctuaire", "Sanctuary Clairvoyance", "etincelle_du_wat", "regard_clair", "precision_lumineuse", 1.21, 180),
    ("synergie_seve_et_goutte", "Rosée Fertile", "Fertile Dew", "seve_vivifiante", "goutte_apaisante", "vitalite_continue", 1.22, 200),
    ("synergie_mekong_et_lac", "Marée du Grand Lac", "Great Lake Tide", "courant_du_mekong", "brume_du_grand_lac", "deferlante_brumeuse", 1.26, 200),
    ("synergie_forge_et_pradal", "Poings d'Acier Rouge", "Red Steel Fists", "flamme_de_forge", "ardeur_du_pradal", "frappe_incandescente", 1.28, 180),
    ("synergie_rafale_et_gibbon", "Voltige des Cimes", "Canopy Vault", "rafale_des_falaises", "elan_du_gibbon", "impulsion_celeste", 1.25, 180),
    ("synergie_gres_et_laterite", "Forteresse Khmère", "Khmer Fortress", "pilier_de_gres", "rempart_de_laterite", "bastion_inflexible", 1.28, 220),
    ("synergie_crepuscule_et_saut", "Frappe Fantôme", "Phantom Strike", "lame_du_crepuscule", "saut_de_l_ombre", "execution_furtive", 1.27, 160),
    ("synergie_prasat_et_moine", "Grâce de la Pagode", "Pagoda Grace", "rayon_du_prasat", "benediction_du_moine", "lumiere_salvatrice", 1.26, 200),
    ("synergie_lotus_et_banyan", "Jardin Sacré des Esprits", "Sacred Spirit Garden", "fleur_de_lotus_rose", "esprit_du_banyan", "floraison_protectrice", 1.28, 220),
    ("synergie_crue_et_naga", "Courroux du Fleuve Sacré", "Sacred River Wrath", "crue_de_mousson", "tourbillon_du_naga", "maelstrom_du_tonle", 1.32, 220),
    ("synergie_phenix_et_danse", "Valse du Phénix Ardent", "Blazing Phoenix Waltz", "colere_du_phenix", "danse_des_flammes_khmeres", "tourbillon_solaire", 1.33, 200),
    ("synergie_foudre_et_cyclone", "Orage de Mousson Céleste", "Celestial Monsoon Storm", "lance_de_foudre_celeste", "oeil_du_cyclone", "tempête_electrique", 1.34, 200),
    ("synergie_colosse_et_angkor", "Muraille Éternelle d'Angkor", "Eternal Wall of Angkor", "poing_du_colosse_de_pierre", "bastion_d_angkor", "impact_titanesque", 1.34, 240),
    ("synergie_lames_et_mirage", "Danse des Sept Ombres", "Seven Shadows Dance", "lames_jumelles_de_l_ombre", "mirage_du_clan_ombre", "clones_assassins", 1.33, 200),
    ("synergie_soleil_et_apsaras", "Hymne Solaire des Apsaras", "Solar Hymn of Apsaras", "lance_solaire_du_sanctuaire", "choeur_des_apsaras", "harmonie_radieuse", 1.32, 220),
    ("synergie_foret_et_kapokier", "Cœur de la Jungle Ancienne", "Ancient Jungle Heart", "chant_de_la_foret_sacree", "gardien_de_kapokier", "sylviculture_sacree", 1.33, 240),
    ("synergie_naga_et_eaux", "Bénédiction du Roi Naga", "Naga King Blessing", "fureur_du_naga_a_sept_tetes", "sanctuaire_des_eaux_eternelles", "souverainete_des_eaux", 1.38, 240),
    ("synergie_sovann_et_renaissance", "Feu Immortel de Sovann", "Immortal Fire of Sovann", "soleil_ardent_de_sovann", "renaissance_du_phenix_de_laterite", "flamme_de_resurrection", 1.38, 240),
    ("synergie_garuda_et_iles", "Seigneur de l'Archipel Céleste", "Lord of the Sky Archipelago", "tempete_du_roi_garuda", "ascension_des_iles_celestes", "souverainete_des_vents", 1.38, 240),
    ("synergie_visages_et_citadelle", "Sentence du Labyrinthe Sacré", "Sacred Labyrinth Sentence", "jugement_des_quatre_visages", "citadelle_vivante_de_gres", "monolithe_implacable", 1.38, 240),
    ("synergie_eclipse_et_legion", "Nuit Sans Étoiles", "Starless Night", "eclipse_du_generalissime", "legion_des_ombres_silencieuses", "domination_nocturne", 1.39, 240),
    ("synergie_couronne_et_grace", "Splendeur Royale du Lotus", "Royal Lotus Splendor", "couronne_de_lumiere_d_angkor", "grace_celeste_de_l_apsara", "benediction_souveraine", 1.38, 240),
    ("synergie_arbre_et_rizieres", "Abondance Éternelle du Cambodge", "Eternal Abundance of Cambodia", "arbre_monde_du_tonle", "souffle_primordial_des_rizieres", "vitalite_ancestrale", 1.38, 260),
    ("synergie_mer_de_lait_et_temple", "Mythe Fondateur d'Angkor", "Founding Myth of Angkor", "barattage_de_la_mer_de_lait", "eveil_du_temple_millenaire", "equilibre_cosmique", 1.45, 280),
    ("synergie_ombre_et_aube", "Crépuscule et Aube Réconciliés", "Reconciled Dusk and Dawn", "abime_de_l_ombre_premiere", "aube_sacree_du_cambodge", "dualite_du_nen", 1.46, 280),
    ("synergie_bol_et_neak_ta", "Alliance Sacrée du Bol et des Esprits", "Sacred Covenant of Bowl and Spirits", "harmonie_du_bol_sacree", "avatar_du_neak_ta_ancestral", "plenitude_du_nen", 1.48, 300),
]


# ---------------------------------------------------------------------------
# 3. 44 ennemis de zone / ninjas + 12 boss multi-phases (56 fichiers JSON)
# ---------------------------------------------------------------------------
ZONE_ENEMIES_SPEC = [
    # Zone 1
    ("bandit_de_riviere", "Bandit de la rivière", "River Bandit", "zone", "initie", "eau", 38, 6.0, 2.0, 1),
    ("pillard_des_berges", "Pillard des berges", "Riverbank Marauder", "zone", "initie", "terre", 42, 6.5, 2.5, 1),
    ("crabe_geant_des_vases", "Crabe géant des vases", "Giant Mud Crab", "zone", "initie", "eau", 48, 7.0, 5.0, 1),
    # Zone 2
    ("sanglier_des_rizieres", "Sanglier des rizières", "Paddy Wild Boar", "zone", "initie", "terre", 75, 10.0, 4.0, 2),
    ("maraudeur_des_digues", "Maraudeur des digues", "Dike Raider", "zone", "lame", "terre", 82, 11.5, 4.5, 2),
    ("initie_ombre", "Initié du Clan Ombre", "Shadow Clan Initiate", "ninja", "initie", "ombre", 88, 12.5, 4.0, 2),
    # Zone 3
    ("lame_ombre", "Lame du Clan Ombre", "Shadow Clan Blade", "ninja", "lame", "ombre", 135, 18.0, 6.5, 3),
    ("archer_des_bambous", "Archer des bambous", "Bamboo Archer", "ninja", "archer", "air", 122, 17.0, 5.5, 3),
    ("tigre_des_brumes", "Tigre des brumes", "Mist Tiger", "zone", "lame", "air", 150, 20.0, 7.0, 3),
    ("guetteur_nocturne", "Guetteur nocturne", "Night Watcher", "ninja", "lame", "ombre", 140, 19.0, 6.0, 3),
    # Zone 4
    ("crocodile_des_marais", "Crocodile des marais", "Swamp Crocodile", "zone", "lame", "eau", 220, 26.0, 10.0, 4),
    ("pirate_du_lac", "Pirate du Grand Lac", "Great Lake Pirate", "zone", "lame", "eau", 205, 24.5, 8.5, 4),
    ("gardien_englouti", "Gardien du temple englouti", "Sunken Temple Guardian", "zone", "maitre_shuriken", "eau", 240, 28.0, 12.0, 4),
    ("elementaire_de_mousson", "Élémentaire de mousson", "Monsoon Elemental", "zone", "maitre_shuriken", "eau", 230, 27.5, 9.5, 4),
    # Zone 5
    ("vipere_des_frondaisons", "Vipère des frondaisons", "Canopy Viper", "zone", "maitre_shuriken", "vie", 310, 35.0, 11.0, 5),
    ("chasseur_sylvestre", "Chasseur sylvestre", "Sylvan Hunter", "zone", "maitre_shuriken", "vie", 325, 36.5, 12.0, 5),
    ("esprit_des_racines", "Esprit des racines", "Root Spirit", "zone", "maitre_shuriken", "vie", 350, 38.0, 14.0, 5),
    ("ombre_silencieuse", "Ombre silencieuse", "Silent Shadow", "ninja", "ombre_silencieuse", "ombre", 340, 40.0, 12.5, 5),
    # Zone 6
    ("sentinelle_de_gres", "Sentinelle de grès", "Sandstone Sentinel", "zone", "maitre_shuriken", "terre", 450, 48.0, 18.0, 6),
    ("lion_sacree_anime", "Lion sacré animé", "Animated Sacred Lion", "zone", "ombre_silencieuse", "lumiere", 470, 50.0, 17.0, 6),
    ("profanateur_de_relique", "Profanateur de relique", "Relic Defiler", "ninja", "ombre_silencieuse", "ombre", 430, 52.0, 15.0, 6),
    ("spectre_des_galeries", "Spectre des galeries", "Gallery Wraith", "zone", "ombre_silencieuse", "ombre", 440, 49.0, 14.0, 6),
    # Zone 7
    ("maitre_shuriken", "Maître shuriken de l'Ombre", "Shadow Shuriken Master", "ninja", "maitre_shuriken", "ombre", 580, 62.0, 18.0, 7),
    ("executeur_d_ardoise", "Exécuteur d'ardoise", "Slate Executioner", "ninja", "ombre_silencieuse", "ombre", 620, 66.0, 21.0, 7),
    ("garde_noir_de_la_citadelle", "Garde noir de la citadelle", "Citadel Black Guard", "ninja", "ombre_silencieuse", "ombre", 650, 68.0, 23.0, 7),
    ("archonte_de_l_ombre", "Archonte de l'Ombre", "Shadow Archon", "ninja", "archonte", "ombre", 700, 74.0, 22.0, 7),
    # Zone 8
    ("golem_de_calcaire", "Golem de calcaire karstique", "Karst Limestone Golem", "zone", "ombre_silencieuse", "terre", 790, 80.0, 28.0, 8),
    ("faucon_de_braise", "Faucon de braise", "Ember Falcon", "zone", "ombre_silencieuse", "feu", 730, 84.0, 20.0, 8),
    ("automate_de_laterite", "Automate de latérite", "Laterite Automaton", "zone", "archonte", "feu", 820, 86.0, 27.0, 8),
    ("ermite_corrompu", "Ermite corrompu par l'Ombre", "Shadow-Corrupted Hermit", "ninja", "archonte", "ombre", 760, 88.0, 22.0, 8),
    # Zone 9
    ("illusionniste_de_pierre", "Illusionniste de pierre", "Stone Illusionist", "zone", "archonte", "terre", 920, 96.0, 26.0, 9),
    ("colosse_du_labyrinthe", "Colosse du labyrinthe", "Labyrinth Colossus", "zone", "archonte", "terre", 1020, 102.0, 32.0, 9),
    ("gardien_du_seuil", "Gardien du seuil", "Threshold Guardian", "zone", "archonte", "lumiere", 960, 98.0, 29.0, 9),
    ("ombre_des_cretes", "Ombre des crêtes", "Ridge Shadow", "ninja", "archonte", "ombre", 900, 104.0, 24.0, 9),
    # Zone 10
    ("lancier_celeste", "Lancier céleste", "Celestial Lancer", "zone", "archonte", "air", 1120, 114.0, 28.0, 10),
    ("aigle_de_foudre", "Aigle de foudre", "Lightning Eagle", "zone", "archonte", "air", 1060, 118.0, 25.0, 10),
    ("champion_des_nuages", "Champion des nuages", "Cloud Champion", "zone", "archonte", "air", 1180, 122.0, 30.0, 10),
    ("gardien_de_cristal", "Gardien de cristal", "Crystal Guardian", "zone", "archonte", "lumiere", 1220, 120.0, 34.0, 10),
    # Zone 11
    ("seraphin_du_lotus", "Séraphin du Lotus", "Lotus Seraph", "zone", "archonte", "lumiere", 1380, 134.0, 34.0, 11),
    ("garde_corrompu", "Garde du sanctuaire corrompu", "Corrupted Sanctuary Guard", "ninja", "archonte", "ombre", 1420, 138.0, 35.0, 11),
    ("ascete_de_l_epreuve", "Ascète de l'Épreuve", "Ordeal Ascetic", "zone", "archonte", "vie", 1460, 140.0, 36.0, 11),
    # Zone 12
    ("spectre_ancestral", "Spectre ancestral", "Ancestral Wraith", "zone", "archonte", "ombre", 1750, 162.0, 38.0, 12),
    ("avatar_du_vide", "Avatar du Vide", "Avatar of the Void", "ninja", "archonte", "ombre", 1850, 170.0, 40.0, 12),
    ("reflet_du_nen", "Reflet du Nen", "Nen Reflection", "zone", "archonte", "lumiere", 1900, 175.0, 42.0, 12),
]

BOSSES_SPEC = [
    (
        "chef_des_bandits_de_la_riviere",
        "Chef des bandits de la rivière",
        "River Bandit Chief",
        "eau",
        420.0, 16.0, 6.0, 1200, 180,
        [
            (1, 1.00, "Embuscade sur l'embarcadère", "Pier Ambush", 1.00, 1.00, "pluie_de_filets", ["entaille_du_sabre", "filet_de_peche_leste"]),
            (2, 0.65, "Appel des pillards du Tonlé", "Call of Tonle Raiders", 1.22, 1.10, "renforts_des_berges", ["tourbillon_de_rames", "renfort_bandits"]),
            (3, 0.30, "Désespoir du flibustier", "Buccaneer Despair", 1.48, 1.22, "vagues_du_fleuve", ["fureur_des_berges", "charge_de_pirogue"]),
        ],
    ),
    (
        "le_buffle_d_or",
        "Le Buffle d'Or",
        "The Golden Buffalo",
        "terre",
        850.0, 25.0, 10.0, 3200, 320,
        [
            (1, 1.00, "Gardien des terrasses sacrées", "Sacred Terrace Guardian", 1.00, 1.00, "boue_lourde", ["charge_de_cornes", "pietinement_des_digues"]),
            (2, 0.65, "Ruée dorée des moissons", "Golden Harvest Stampede", 1.25, 1.15, "ondes_sismiques", ["ruee_d_or", "geyser_de_riziere"]),
            (3, 0.30, "Colère tellurique de Ly", "Telluric Wrath of Ly", 1.52, 1.25, "tremblement_de_terrasse", ["cornes_solaires", "effondrement_des_digues"]),
        ],
    ),
    (
        "maitre_des_lames_du_clan_ombre",
        "Maître des lames du clan Ombre",
        "Shadow Clan Blade Master",
        "ombre",
        1550.0, 38.0, 13.0, 7500, 520,
        [
            (1, 1.00, "Duel sous les bambous", "Bamboo Grove Duel", 1.00, 1.05, "brume_de_bambou", ["estoc_silencieux", "eventail_de_shurikens"]),
            (2, 0.65, "Mirage des cinq ombres", "Five Shadows Mirage", 1.26, 1.18, "clones_d_ombre", ["pas_de_l_ombre", "pluie_de_lames"]),
            (3, 0.30, "Danse finale de l'ardoise", "Final Slate Dance", 1.55, 1.30, "eclipse_de_bambouseraie", ["entaille_du_crepuscule", "tempête_de_shurikens"]),
        ],
    ),
    (
        "le_crocodile_des_profondeurs",
        "Le Crocodile des Profondeurs",
        "Crocodile of the Depths",
        "eau",
        2600.0, 52.0, 17.0, 15000, 800,
        [
            (1, 1.00, "Prédateur des ruines englouties", "Sunken Ruins Predator", 1.00, 1.00, "crue_du_bassin", ["machoire_abyssale", "coup_de_queue_lacustre"]),
            (2, 0.65, "Tourbillon de la mousson", "Monsoon Whirlpool", 1.26, 1.12, "vortex_du_grand_lac", ["plongee_furtive", "raz_de_maree"]),
            (3, 0.30, "Léviathan ancestral du Tonlé", "Ancestral Tonle Leviathan", 1.55, 1.24, "inondation_totale", ["fureur_des_profondeurs", "deluge_du_sanctuaire"]),
        ],
    ),
    (
        "le_serpent_de_la_foret",
        "Le Serpent de la forêt",
        "Serpent of the Forest",
        "vie",
        3900.0, 68.0, 20.0, 28000, 1150,
        [
            (1, 1.00, "Étreinte des racines de banyan", "Banyan Root Embrace", 1.00, 1.04, "racines_vivantes", ["morsure_sylvestre", "constriction_de_lianes"]),
            (2, 0.65, "Venin des brumes d'émeraude", "Emerald Mist Venom", 1.28, 1.16, "spores_ancestrales", ["crachat_corrosif", "appel_des_viperes"]),
            (3, 0.30, "Avatar du Naga sylvestre", "Sylvan Naga Avatar", 1.58, 1.28, "foret_en_colere", ["tempete_de_seve", "jugement_de_kompong"]),
        ],
    ),
    (
        "gardien_de_pierre",
        "Gardien de pierre",
        "Stone Guardian",
        "terre",
        5600.0, 86.0, 28.0, 48000, 1600,
        [
            (1, 1.00, "Sentinelle du Prasat Sovann", "Prasat Sovann Sentinel", 1.00, 0.95, "dalles_piegees", ["marteau_de_gres", "rayon_des_bas_reliefs"]),
            (2, 0.65, "Réveil des lions sacrés", "Sacred Lions Awakening", 1.28, 1.08, "statues_animees", ["onde_de_laterite", "bouclier_monolithique"]),
            (3, 0.30, "Cœur ardent du sanctuaire", "Blazing Heart of the Sanctuary", 1.60, 1.20, "effondrement_sacree", ["jugement_du_prasat", "pluie_de_piliers"]),
        ],
    ),
    (
        "generalissime_du_clan_ombre",
        "Généralissime du clan Ombre",
        "Shadow Clan Generalissimo",
        "ombre",
        7800.0, 108.0, 27.0, 78000, 2200,
        [
            (1, 1.00, "Commandeur de la citadelle noire", "Black Citadel Commander", 1.00, 1.06, "bannieres_d_ombre", ["hallebarde_nocturne", "ordre_d_assaut"]),
            (2, 0.65, "Stratagème des cent lames", "Hundred Blades Stratagem", 1.30, 1.18, "embuscade_d_archontes", ["execution_du_general", "voile_de_la_forteresse"]),
            (3, 0.30, "Serment du crépuscule éternel", "Oath of Eternal Dusk", 1.62, 1.30, "nuit_des_ombres_totale", ["abime_d_ardoise", "legion_fantome"]),
        ],
    ),
    (
        "le_phenix_de_laterite",
        "Le Phénix de latérite",
        "The Laterite Phoenix",
        "feu",
        10200.0, 132.0, 30.0, 118000, 3000,
        [
            (1, 1.00, "Sentinelle des pitons karstiques", "Karst Peak Sentinel", 1.00, 1.08, "vents_thermiques", ["plumes_de_braise", "pique_incandescent"]),
            (2, 0.65, "Bûcher des crêtes rouges", "Red Ridge Pyre", 1.30, 1.20, "pluie_de_laterite_fondue", ["nova_solaire", "tourbillon_de_cendres"]),
            (3, 0.30, "Renaissance par la flamme sacrée", "Sacred Flame Rebirth", 1.65, 1.32, "embrasement_du_ciel", ["soleil_des_karsts", "plume_immortelle"]),
        ],
    ),
    (
        "l_eveil_du_visage",
        "L'Éveil du Visage",
        "Awakening of the Face",
        "lumiere",
        13200.0, 156.0, 36.0, 168000, 4000,
        [
            (1, 1.00, "Sourire imperturbable du Nord", "Unfaltering Smile of the North", 1.00, 0.95, "rotation_du_labyrinthe", ["regard_de_pierre", "sceau_de_silence"]),
            (2, 0.65, "Courroux du Visage du Sud", "Wrath of the Southern Face", 1.32, 1.10, "miroirs_de_lumiere", ["faisceau_quadruple", "illusion_des_galeries"]),
            (3, 0.30, "Harmonie des quatre horizons", "Harmony of Four Horizons", 1.66, 1.22, "convergence_astrale", ["sentence_millenaire", "lumiere_omnisciente"]),
        ],
    ),
    (
        "garuda_de_la_tempete",
        "Garuda de la Tempête",
        "Storm Garuda",
        "air",
        16800.0, 184.0, 35.0, 235000, 5200,
        [
            (1, 1.00, "Seigneur des îles flottantes", "Lord of the Floating Isles", 1.00, 1.12, "courants_ascendants", ["serres_de_foudre", "bourrasque_royale"]),
            (2, 0.65, "Cyclone de l'archipel céleste", "Sky Archipelago Cyclone", 1.34, 1.24, "foudre_en_chaine", ["lance_d_orage", "ailes_ouragan"]),
            (3, 0.30, "Souverain du tonnerre primordial", "Primordial Thunder Sovereign", 1.68, 1.36, "oeil_de_la_tempete", ["jugement_de_garuda", "plongee_supersonique"]),
        ],
    ),
    (
        "le_gardien_de_l_equilibre",
        "Le Gardien de l'Équilibre",
        "Guardian of Balance",
        "vie",
        22000.0, 215.0, 42.0, 350000, 7500,
        [
            (1, 1.00, "Épreuve de l'Eau et de la Terre", "Ordeal of Water and Earth", 1.00, 1.05, "bassins_de_lotus", ["onde_sacree", "piliers_du_sanctuaire"]),
            (2, 0.65, "Épreuve du Feu et du Vent", "Ordeal of Fire and Wind", 1.25, 1.15, "souffle_des_sept_elements", ["flamme_du_lotus", "tourbillon_celeste"]),
            (3, 0.35, "Confrontation de l'Ombre et de la Lumière", "Clash of Shadow and Light", 1.50, 1.25, "dualite_du_bol", ["eclipse_sacree", "rayon_d_harmonie"]),
            (4, 0.12, "La Leçon du Bol Sacré", "Lesson of the Sacred Bowl", 1.78, 1.35, "resonance_absolue_du_nen", ["jugement_de_l_equilibre", "plenitude_des_ancetres"]),
        ],
    ),
    (
        "l_ombre_premiere",
        "L'Ombre Première",
        "The First Shadow",
        "ombre",
        30000.0, 260.0, 48.0, 600000, 12000,
        [
            (1, 1.00, "Murmure né avant l'Aube", "Whisper Born Before Dawn", 1.00, 1.10, "brume_du_royaume_des_esprits", ["lame_du_neant", "reflet_obscur"]),
            (2, 0.65, "Fracture des Sept Éléments", "Shattering of Seven Elements", 1.30, 1.20, "inversion_elementaire", ["chaine_du_vide", "pluie_d_etoiles_noires"]),
            (3, 0.35, "Miroir de la Renaissance", "Mirror of Rebirth", 1.58, 1.30, "duel_interieur", ["spectre_du_joueur", "dechirure_spirituelle"]),
            (4, 0.12, "Transcendance de l'Ombre et du Lotus", "Shadow and Lotus Transcendence", 1.88, 1.42, "apotheose_du_royaume_des_esprits", ["abime_primordial", "chant_de_l_eternite"]),
        ],
    ),
]


def generate() -> None:
    # 1. Pouvoirs (84)
    powers_dir = CAMBO_DIR / "powers"
    for spec in POWERS_SPEC:
        write_json(powers_dir / f"{spec[0]}.json", build_power_json(spec))

    # 2. Synergies (34)
    syn_dir = CAMBO_DIR / "synergies"
    for sid, fr, en, pa, pb, eff, mult, dur in SYNERGIES_SPEC:
        write_json(syn_dir / f"{sid}.json", {
            "nom": {"fr": fr, "en": en},
            "pouvoirs": [pa, pb],
            "effet": eff,
            "multiplicateur": mult,
            "duree_ticks": dur,
            "description": {
                "fr": f"Synergie active lorsque {pa} et {pb} sont équipés ensemble : {fr} (×{mult:.2f}).",
                "en": f"Active synergy when {pa} and {pb} are equipped together: {en} (×{mult:.2f}).",
            },
        })

    # 3. Ennemis de zone et ninjas (44) + Boss (12) = 56
    enemies_dir = CAMBO_DIR / "enemies"
    for eid, fr, en, cat, rang, elem, pv, atk, arm, z_ord in ZONE_ENEMIES_SPEC:
        caps = ["attaque_de_base", f"affinite_{elem}"]
        if cat == "ninja":
            caps.extend(["transformation_5_etapes", "pas_de_l_ombre", "salve_shuriken"])
        write_json(enemies_dir / f"{eid}.json", {
            "nom": {"fr": fr, "en": en},
            "categorie": cat,
            "rang": rang,
            "element": elem,
            "pv_base": float(pv),
            "attaque_base": float(atk),
            "armure_base": float(arm),
            "vitesse": round(0.26 + 0.01 * min(z_ord, 8), 2),
            "xp": z_ord * 85,
            "pieces": z_ord * 14,
            "capacites": caps,
            "phases": [],
        })

    for bid, fr, en, elem, pv, atk, arm, xp, pieces, phases in BOSSES_SPEC:
        phases_json = []
        for pnum, seuil, pfr, pen, matk, mspd, arene, pcaps in phases:
            phases_json.append({
                "phase": pnum,
                "seuil_pv": seuil,
                "nom": {"fr": pfr, "en": pen},
                "mult_attaque": matk,
                "mult_vitesse": mspd,
                "mecanique_arene": arene,
                "capacites": pcaps,
            })
        write_json(enemies_dir / f"{bid}.json", {
            "nom": {"fr": fr, "en": en},
            "categorie": "boss",
            "rang": "seigneur_boss",
            "element": elem,
            "pv_base": pv,
            "attaque_base": atk,
            "armure_base": arm,
            "vitesse": 0.32,
            "xp": xp,
            "pieces": pieces,
            "capacites": ["presence_de_boss", "transition_invulnerable", f"maitrise_{elem}"],
            "phases": phases_json,
        })

    # 4. Événements saisonniers (4, dont la Nuit des Ombres)
    events_dir = CAMBO_DIR / "events"
    events = [
        (
            "nuit_des_ombres",
            "La Nuit des Ombres",
            "Night of Shadows",
            "toutes",
            5,
            13000,
            10000,
            ["infiltration_clan_ombre", "lune_pourpre", "butin_ombre_double"],
            ["cambocraft:eclat_d_ombre", "cambocraft:bol_de_riz_beni", "cambocraft:piece_du_lotus_or"],
            "Une fois par saison, au crépuscule du sixième jour, les infiltrés du Clan Ombre se dévoilent en trois vagues coordonnées.",
            "Once per season, at dusk on the sixth day, Shadow Clan infiltrators reveal themselves in three coordinated waves.",
        ),
        (
            "fete_des_eaux_bon_om_touk",
            "Fête des Eaux (Bon Om Touk)",
            "Water Festival (Bon Om Touk)",
            "saison_douce",
            2,
            1000,
            14000,
            ["vitesse_pirogue_accrue", "lanternes_flottantes", "reputation_tonle_bonus"],
            ["cambocraft:piece_du_lotus_argent", "cambocraft:offrande_de_fleurs", "cambocraft:amok"],
            "Les berges du Tonlé s'illuminent pour célébrer l'inversion du cours des eaux et les régates de pirogues.",
            "The banks of Tonle light up to celebrate the reversal of the waters and the dugout canoe regattas.",
        ),
        (
            "floraison_sacree_du_lotus",
            "Floraison Sacrée du Lotus",
            "Sacred Lotus Blooming",
            "saison_chaude",
            3,
            2000,
            12000,
            ["regeneration_nen_accrue", "abondance_fleurs_de_lotus"],
            ["cambocraft:fleur_de_lotus", "cambocraft:graine_de_lotus", "cambocraft:bol_de_riz_beni"],
            "Les bassins sacrés s'ouvrent à l'unisson et amplifient la résonance spirituelle du Nen.",
            "Sacred ponds bloom in unison and amplify the spiritual resonance of Nen.",
        ),
        (
            "moisson_d_or_de_ly",
            "Moisson d'Or de Ly",
            "Golden Harvest of Ly",
            "mousson",
            6,
            1000,
            12000,
            ["recolte_riz_abondante", "baisse_inflation_riz"],
            ["cambocraft:riz_brut", "cambocraft:riz_parfume_jasmin", "cambocraft:bol_de_riz_beni"],
            "Les terrasses de Ly arrivent à maturité : l'abondance de riz apaise les prix dans tout le royaume.",
            "The terraces of Ly reach maturity: abundant rice lowers prices across the entire realm.",
        ),
    ]
    for evid, fr, en, saison, jour, debut, duree, effs, recs, dfr, den in events:
        write_json(events_dir / f"{evid}.json", {
            "nom": {"fr": fr, "en": en},
            "description": {"fr": dfr, "en": den},
            "saison": saison,
            "jour_saison": jour,
            "heure_debut_tick": debut,
            "duree_ticks": duree,
            "effets": effs,
            "recompenses": recs,
        })

    # 5. Modèles hiérarchiques d'entités et courbes d'animation (D-02)
    models_dir = ASSETS_DIR / "models" / "entity"
    for archetype in ("faune_quadrupede", "faune_oiseau", "faune_aquatique", "villageois_khmer", "ninja_clan_ombre", "boss_monumental"):
        write_json(models_dir / f"{archetype}.json", {
            "format_version": "1.21.1-cambocraft-native",
            "archetype": archetype,
            "texture_width": 64,
            "texture_height": 64,
            "bones": [
                {"name": "root", "pivot": [0, 0, 0]},
                {"name": "body", "parent": "root", "pivot": [0, 12, 0]},
                {"name": "head", "parent": "body", "pivot": [0, 24, 0]},
                {"name": "right_arm", "parent": "body", "pivot": [-5, 22, 0]},
                {"name": "left_arm", "parent": "body", "pivot": [5, 22, 0]},
                {"name": "right_leg", "parent": "root", "pivot": [-2, 12, 0]},
                {"name": "left_leg", "parent": "root", "pivot": [2, 12, 0]},
            ],
        })

    anim_dir = ASSETS_DIR / "animations"
    write_json(anim_dir / "entites_cambocraft.json", {
        "format_version": "1.21.1-cambocraft-native",
        "animations": {
            "respiration_repos": {"loop": True, "length_ticks": 40, "bones": {"body": {"scale": [1.0, 1.02, 1.0]}}},
            "marche_standard": {"loop": True, "length_ticks": 20, "bones": {"left_leg": {"rot_x": 30}, "right_leg": {"rot_x": -30}}},
            "salut_khmer_sampeah": {"loop": False, "length_ticks": 30, "bones": {"left_arm": {"rot_x": -65}, "right_arm": {"rot_x": -65}, "head": {"rot_x": 15}}},
            "transformation_ninja_5_etapes": {
                "loop": False,
                "length_ticks": 42,
                "stages": [
                    {"etape": 1, "nom": "dissimule_villageois", "tick_debut": 0},
                    {"etape": 2, "nom": "suspicion_regard", "tick_debut": 1},
                    {"etape": 3, "nom": "sceau_de_fumigene", "tick_debut": 16},
                    {"etape": 4, "nom": "revelation_tenue_ombre", "tick_debut": 28},
                    {"etape": 5, "nom": "combat_actif", "tick_debut": 42},
                ],
            },
            "transition_phase_boss": {"loop": False, "length_ticks": 30, "invulnerable": True},
        },
    })

    print(
        f"Phase 6 générée : {len(POWERS_SPEC)} pouvoirs, {len(SYNERGIES_SPEC)} synergies, "
        f"{len(ZONE_ENEMIES_SPEC) + len(BOSSES_SPEC)} ennemis/boss ({len(BOSSES_SPEC)} boss), "
        f"{len(events)} événements."
    )


if __name__ == "__main__":
    generate()
