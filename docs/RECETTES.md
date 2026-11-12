# Catalogue des recettes, de l'économie et de l'artisanat — Cambocraft

Ce document regroupe les recettes d'établi essentielles, les **16 recettes de cuisine khmère**
(débloquées par réputation), la **chaîne de la soie**, la **sculpture sur grès**, le système
de **forge +1 à +5** et la grille tarifaire des **8 marchands** en *Pièces du Lotus*.

## 1. Recettes fondamentales (Accès à la dimension et Nen)

| Objet | Type | Ingrédients | Résultat |
|---|---|---|---|
| **Cadre du Portail** (`cadre_du_portail`) | Établi 3×3 | 4× `pierre_du_lotus`, 4× `lingot_de_lotus`, 1× `cristal_de_lotus` | 4× Cadre du Portail |
| **Bol de Riz Béni** (`bol_de_riz_beni`) | Sans forme | 1× `bol_de_riz` + 1× `fleur_de_lotus` + 1× `poudre_de_lotus` (ou via Apprenti Moine) | 1× Bol de Riz Béni |
| **Carnet du Nen** (`carnet_du_nen`) | Sans forme | 1× `minecraft:book` + 1× `fil_de_soie` + 1× `fleur_de_lotus` | 1× Carnet du Nen |
| **Canne à Pêcher** (`canne_a_pecher`) | Établi 3×3 | 3× `bambou_brut` + 2× `fil_de_soie` | 1× Canne à Pêcher |
| **Épée Khmère** (`epee_khmer`) | Établi 3×3 | 2× `lingot_de_lotus` + 1× `teck_planks` | 1× Épée Khmère |

## 2. Les 16 recettes de cuisine khmère (`data/cambocraft/cambo/cooking/`)

| Plat | Faction | Palier requis | Ingrédients | Effet (Buff) | Durée |
|---|---|---|---|---|---|
| **Amok de Poisson au Coco** (`amok`) | `tonle` | `respecte` | `cambocraft:poisson_serpent`, `cambocraft:lait_de_coco`, `cambocraft:pate_de_kroeung_jaune` | `minecraft:regeneration` (niv. 2) | 180 s |
| **Nom Banh Chok aux Herbes** (`nom_banh_chok`) | `tonle` | `connu` | `cambocraft:nouilles_de_riz`, `cambocraft:poisson_chat`, `cambocraft:citronnelle_coupee` | `minecraft:speed` (niv. 1) | 120 s |
| **Lok Lak au Poivre de Kampot** (`lok_lak`) | `srok` | `honore` | `cambocraft:riz_cuit`, `cambocraft:poivre_noir_de_kampot`, `cambocraft:ail_grille` | `minecraft:strength` (niv. 2) | 180 s |
| **Bai Sach Chrouk Matinal** (`bai_sach_chrouk`) | `ly` | `connu` | `cambocraft:riz_cuit`, `cambocraft:sucre_de_palme`, `cambocraft:ail_grille` | `minecraft:haste` (niv. 1) | 120 s |
| **Num Pang Croustillant** (`num_pang`) | `tonle` | `connu` | `cambocraft:farine_de_riz`, `cambocraft:papaye_verte`, `cambocraft:piment_rouge` | `minecraft:saturation` (niv. 1) | 60 s |
| **Soupe Kuy Teav du Port** (`kuy_teav`) | `tonle` | `respecte` | `cambocraft:nouilles_de_riz`, `cambocraft:crevette_de_riviere`, `cambocraft:ail_grille` | `minecraft:water_breathing` (niv. 1) | 180 s |
| **Samlor Korko Traditionnel** (`samlor_korko`) | `ly` | `respecte` | `cambocraft:pate_de_prahok`, `cambocraft:papaye_verte`, `cambocraft:pate_de_kroeung_vert` | `minecraft:resistance` (niv. 1) | 180 s |
| **Soupe Aigre Samlor Machu** (`samlor_machu`) | `tonle` | `respecte` | `cambocraft:poisson_tilapia`, `cambocraft:tamarin_gousse`, `cambocraft:citronnelle_coupee` | `minecraft:luck` (niv. 1) | 180 s |
| **Prahok Ktis au Lait de Coco** (`prahok_ktis`) | `ly` | `honore` | `cambocraft:pate_de_prahok`, `cambocraft:lait_de_coco`, `cambocraft:piment_rouge` | `minecraft:resistance` (niv. 2) | 180 s |
| **Salade Citronnée au Combava** (`pleah_sach_ko`) | `srok` | `respecte` | `cambocraft:combava_zeste`, `cambocraft:citronnelle_coupee`, `cambocraft:piment_rouge` | `minecraft:speed` (niv. 2) | 150 s |
| **Crêpe Dorée au Curcuma** (`banh_chao`) | `ly` | `connu` | `cambocraft:farine_de_riz`, `cambocraft:curcuma_racine`, `cambocraft:crevette` | `minecraft:jump_boost` (niv. 1) | 120 s |
| **Kralan de Riz Gluant en Bambou** (`kralan`) | `srok` | `connu` | `cambocraft:riz_gluant`, `cambocraft:noix_de_coco`, `cambocraft:bambou_brut` | `minecraft:absorption` (niv. 1) | 150 s |
| **Gâteau de Fête Num Ansorm** (`num_ansorm`) | `kompong` | `honore` | `cambocraft:riz_gluant`, `cambocraft:banane_plantain`, `cambocraft:lait_de_coco` | `minecraft:health_boost` (niv. 2) | 240 s |
| **Pyramide Sucrée Num Kom** (`num_kom`) | `kompong` | `respecte` | `cambocraft:farine_de_riz`, `cambocraft:sucre_de_palme`, `cambocraft:noix_de_coco` | `minecraft:regeneration` (niv. 1) | 150 s |
| **Crabe au Poivre Vert Frais** (`crabe_au_poivre_vert`) | `tonle` | `legendaire` | `cambocraft:crabe_de_riziaire`, `cambocraft:poivre_vert_frais`, `cambocraft:ail_grille` | `minecraft:strength` (niv. 2) | 240 s |
| **Infusion Sacrée de Lotus** (`infusion_de_lotus`) | `kompong` | `honore` | `cambocraft:lotus_brut`, `cambocraft:graine_de_lotus`, `cambocraft:sucre_de_palme` | `minecraft:regeneration` (niv. 2) | 210 s |

## 3. Chaîne de la soie (`SilkChain`) et teintures naturelles

1. `3× feuille_de_murier` (élevage du `ver_a_soie`) → `2× cocon_de_soie`
2. `2× cocon_de_soie` (`cuve_de_soie`) → `4× fil_de_soie`
3. `4× fil_de_soie` (`cuve_de_soie`) → `1× echeveau_de_soie_ecrue`
4. Teinture : `curcuma_racine` → Or · `fleur_d_hibiscus` → Pourpre · `fleur_de_bougainvillier` → Indigo · `feuille_de_lotus` → Émeraude
5. Tissage (`metier_a_tisser`) : `tissu_de_soie`, `krama_traditionnel`, `krama_de_soie_doree`, tenues de soie et lotus.

## 4. Forge et renforcement d'équipement (`+1` à `+5`, `ForgeUpgrade`)

| Niveau cible | Multiplicateur stats | Chance de succès | Cristal requis | Coût (Pièces du Lotus) |
|---|---|---|---|---|
| **+1** | ×1,08 (+8 %) | 100 % | `cristal_de_forge_mineur` | 15 |
| **+2** | ×1,16 (+16 %) | 85 % | `cristal_de_forge_mineur` | 35 |
| **+3** | ×1,25 (+25 %) | 70 % | `cristal_de_forge_majeur` | 75 |
| **+4** | ×1,36 (+36 %) | 50 % | `cristal_de_forge_majeur` | 150 |
| **+5** | ×1,50 (+50 %) | 35 % | `cristal_de_forge_parfait` | 300 |

## 5. Catalogues des 8 marchands PNJ (`data/cambocraft/cambo/merchants/`)

| Marchand | Faction | Offres principales (prix de base en Pièces du Lotus) |
|---|---|---|
| `vendeur_de_riz` | `tonle` | `graine_de_riz` ×4 (3 p., `connu`), `riz_brut` ×4 (5 p., `connu`), `riz_cuit` ×2 (7 p., `connu`), `bol_de_riz` ×1 (10 p., `respecte`), `farine_de_riz` ×2 (8 p., `respecte`), `sucre_de_palme` ×2 (9 p., `honore`), `bol_de_riz_beni` ×1 (45 p., `honore`) |
| `vendeur_de_bambou` | `srok` | `bambou_brut` ×8 (4 p., `connu`), `fleche_en_bambou` ×16 (8 p., `connu`), `pique_en_bambou` ×1 (18 p., `respecte`), `arc_composite_en_bambou` ×1 (32 p., `respecte`), `bouclier_de_rotin` ×1 (28 p., `honore`), `arbalete_de_rotin` ×1 (48 p., `legendaire`) |
| `forgeron` | `srok` | `pierre_d_affutage` ×1 (12 p., `connu`), `dague_khmer` ×1 (22 p., `connu`), `epee_khmer` ×1 (35 p., `respecte`), `cristal_de_forge_mineur` ×1 (30 p., `respecte`), `lance_de_prasat` ×1 (60 p., `honore`), `cristal_de_forge_majeur` ×1 (75 p., `honore`), `sabre_de_garde_royal` ×1 (120 p., `legendaire`), `cristal_de_forge_parfait` ×1 (180 p., `legendaire`) |
| `pecheur_marchand` | `tonle` | `canne_a_pecher` ×1 (16 p., `connu`), `appat_de_riz_fermente` ×4 (6 p., `connu`), `filet_de_peche` ×1 (20 p., `respecte`), `canne_a_pecher_en_bambou_renforce` ×1 (45 p., `respecte`), `appat_de_crevette_doree` ×4 (18 p., `honore`), `canne_a_pecher_du_maitre` ×1 (110 p., `legendaire`) |
| `tisserand_de_soie` | `ly` | `feuille_de_murier` ×4 (4 p., `connu`), `fil_de_soie` ×4 (10 p., `connu`), `tissu_de_soie` ×1 (22 p., `respecte`), `krama_traditionnel` ×1 (30 p., `respecte`), `tunique_de_soie_et_lotus` ×1 (85 p., `honore`), `krama_de_soie_doree` ×1 (140 p., `legendaire`) |
| `cuisinier_de_rue` | `tonle` | `poisson_grille` ×2 (8 p., `connu`), `num_pang` ×2 (12 p., `connu`), `nom_banh_chok` ×1 (16 p., `respecte`), `amok` ×1 (24 p., `respecte`), `lok_lak` ×1 (30 p., `honore`), `crabe_au_poivre_vert` ×1 (45 p., `legendaire`) |
| `sculpteur` | `kompong` | `bloc_de_gres_brut` ×4 (8 p., `connu`), `maillet_de_sculpteur` ×1 (18 p., `connu`), `ciseau_de_sculpteur` ×1 (18 p., `connu`), `statuette_de_naga` ×1 (40 p., `respecte`), `statuette_d_apsara` ×1 (55 p., `honore`), `pierre_de_teleportation` ×2 (65 p., `legendaire`) |
| `guerisseur` | `ly` | `bandage_de_soie` ×2 (14 p., `connu`), `infusion_de_citronnelle` ×2 (12 p., `connu`), `onguent_de_lotus` ×1 (28 p., `respecte`), `infusion_de_lotus` ×1 (32 p., `honore`), `encens_beni` ×2 (40 p., `legendaire`) |
