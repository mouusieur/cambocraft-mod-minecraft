# Palettes de couleurs et motifs de textures — Cambocraft

Ce document recense les palettes chromatiques 16×16 utilisées par `tools/gen_textures.py`
pour générer de manière déterministe les **160 textures de blocs** et les **199 icônes d'objets**.

## 1. Familles chromatiques directrices

| Famille | Teinte de base | Ombre / Accent | Lumière / Reflet | Usage principal |
|---|---|---|---|---|
| **Pierre du Lotus** | `#8C9089` | `#6E736C` | `#A3A79F` | Portail, sanctuaires, téléporteurs, piliers sacrés |
| **Latérite ferrugineuse** | `#9C4A2A` | `#7C371E` | `#B35C36` | Soubassements, enceintes, pyramides, sols rouges |
| **Grès khmer doré** | `#C9AE7F` | `#B2966A` | `#DBBE8C` | Prasats, bas-reliefs, tours-visages, statues |
| **Ardoise des Ombres** | `#3B3F46` | `#2A2D33` | `#4B5058` | Forteresse du clan Ombre, pierres runiques (`#8A5AA8`) |
| **Bois tropicaux (Teck, Manguier, Palmier à sucre)** | `#A87A4A` | `#8B6238` | `#BE9059` | Maisons sur pilotis, ponts, charpentes, pirogues |
| **Bambou & Chaume de riz** | `#C9B260` | `#A99244` | `#DCC66F` | Bambouseraie, nattes, paniers, toits de chaume |
| **Soie khmère & Krama** | `#C84C44` | `#7E2824` | `#F0D8B8` | Soieries, bannières de temple, kramas, bandages |
| **Cristal & Eau du Lotus** | `#7FC8C0` | `#3F7A78` | `#D8F6F0` | Minerai de lotus, cœur du portail, reliques spirituelles |

## 2. Catalogue complet des 160 textures de blocs (`textures/block/*.png`)

| Texture | Motif (`pattern`) | Base | Accent | Reflet (`alt`) |
|---|---|---|---|---|
| `ardoise_des_ombres` | `stone` | `#3B3F46` | `#2A2D33` | `#4B5058` |
| `atelier_de_sculpture` | `carving` | `#A9A594` | `#8A8677` | `#BDBAAB` |
| `aubergine` | `crop` | `#4A6E32` | `#5A3F7A` | `#6B8A46` |
| `autel` | `carving` | `#8F8B7C` | `#6E6A5D` | `#A6A292` |
| `bambou_bloc` | `bamboo_block` | `#CBB466` | `#A79246` | `#DFCA7C` |
| `bambou_leaves` | `leaves` | `#5E963C` | `#457429` | `#74AD4E` |
| `bambou_planches` | `planks` | `#C9B260` | `#A99244` | `#DCC66F` |
| `bambou_sapling` | `sapling` | `#5E963C` | `#74AD4E` | `#457429` |
| `bambou_tresse` | `mosaic` | `#BFA75C` | `#9E8741` | `#D2BB6E` |
| `banniere_blanche` | `banner` | `#E7E2D4` | `#A8332E` | `#F5F1E6` |
| `banniere_bleue` | `banner` | `#2F4E7A` | `#D9B45E` | `#3C6296` |
| `banniere_or` | `banner` | `#C9A24A` | `#8C6A2E` | `#E0BC63` |
| `banniere_rouge` | `banner` | `#A8332E` | `#D9B45E` | `#C2443E` |
| `banniere_verte` | `banner` | `#2F6B45` | `#D9B45E` | `#3C8455` |
| `banniere_violette` | `banner` | `#5A3F7A` | `#D9B45E` | `#6E4E96` |
| `bas_relief` | `relief` | `#B3AB94` | `#8E8770` | `#C8C1AB` |
| `basalte_colonnaire_side` | `column` | `#4A4E55` | `#363A40` | `#5B6068` |
| `basalte_colonnaire_top` | `stone` | `#4A4E55` | `#363A40` | `#5B6068` |
| `bloc_de_lotus` | `crystal` | `#7FC0A8` | `#4E8A72` | `#AEE0CA` |
| `boue_de_riziaire` | `mud` | `#5E4A33` | `#4A3A27` | `#71593E` |
| `brique_de_laterite` | `bricks` | `#A04F2C` | `#7B3A1F` | `#BC6039` |
| `brique_de_lotus` | `bricks` | `#7E827B` | `#5F635D` | `#93978F` |
| `brique_de_temple` | `bricks` | `#7A6A52` | `#5A4C39` | `#8E7C61` |
| `brique_des_ombres` | `bricks` | `#353941` | `#23262C` | `#464B55` |
| `cadre_du_portail` | `carving` | `#8C9089` | `#D9B45E` | `#A3A79F` |
| `calcaire_karstique` | `stone` | `#B9B6A6` | `#9A9788` | `#CFCCBB` |
| `citronnelle` | `crop` | `#9BB04A` | `#7A8C35` | `#B5C964` |
| `cloche_de_temple` | `metal` | `#8A7A4A` | `#6A5C34` | `#A99A62` |
| `cocotier_leaves` | `palm_leaves` | `#3C7A38` | `#2B5A28` | `#4E9146` |
| `cocotier_log_side` | `bark` | `#7A6244` | `#5C4830` | `#917757` |
| `cocotier_log_top` | `wood_end` | `#BFA171` | `#A08355` | `#D2B787` |
| `cocotier_planks` | `planks` | `#B98F5C` | `#9A7447` | `#CBA271` |
| `cocotier_sapling` | `sapling` | `#3C7A38` | `#5A8E4A` | `#2A5A26` |
| `cocotier_stripped_log_side` | `planks` | `#B98F5C` | `#9A7447` | `#CBA271` |
| `cocotier_stripped_log_top` | `planks` | `#B98F5C` | `#9A7447` | `#CBA271` |
| `coeur_du_portail` | `crystal` | `#7FB7C9` | `#4E8A9E` | `#B5DEE8` |
| `concombre` | `crop` | `#4E7A34` | `#7FA83C` | `#6E9A4A` |
| `creuset_de_fusion` | `metal` | `#4A3A50` | `#2E2434` | `#5E4A66` |
| `cristal_de_lotus` | `crystal` | `#9AD1E0` | `#5E9EB0` | `#C6EAF4` |
| `cuve_de_soie` | `jar` | `#D9C27A` | `#A89352` | `#EBD68E` |
| `dalle_de_pression_ancienne` | `carving` | `#8A7A5E` | `#D9B45E` | `#A39173` |
| `eau_du_lotus` | `water` | `#2F6B8A` | `#24526B` | `#3F88A8` |
| `encens` | `incense` | `#6B5A44` | `#C8B79A` | `#8A7659` |
| `etal_de_marche` | `planks` | `#8C6A44` | `#6E5030` | `#A07C52` |
| `feuille_de_lotus` | `lotus_leaf` | `#2F6E3A` | `#1F5028` | `#3F8A4A` |
| `fleur_d_hibiscus` | `flower` | `#D9473F` | `#AE332C` | `#EE6B5F` |
| `fleur_de_bougainvillier` | `flower` | `#C4498C` | `#9B346C` | `#DE6CA8` |
| `fleur_de_frangipanier` | `flower` | `#F5E7C8` | `#D8C79F` | `#FFF6E0` |
| `fleur_de_lotus` | `flower` | `#E7B7C8` | `#C98CA5` | `#F4D6E0` |
| `fougere_tropicale` | `sapling` | `#347233` | `#245424` | `#468A45` |
| `four_de_forge` | `stone` | `#6E6A60` | `#4A4640` | `#8A8678` |
| `garuda` | `carving` | `#B89A5E` | `#96793F` | `#CBAE74` |
| `gong` | `metal` | `#A98F4A` | `#86702F` | `#C4AC64` |
| `gravier_de_riviere` | `gravel` | `#8E8779` | `#6F695E` | `#A59E8F` |
| `gres_khmer_poli` | `polished` | `#D2B785` | `#B89C6D` | `#E4CA98` |
| `gres_khmer_side` | `sandstone` | `#C9AE7F` | `#B2966A` | `#DBBE8C` |
| `gres_khmer_top` | `sandstone_top` | `#CDB282` | `#B89C6D` | `#DEC292` |
| `gres_sculpte` | `carving` | `#C4A878` | `#9F855A` | `#D8BC8B` |
| `herbe_de_savane` | `crop` | `#B59E4A` | `#8F7B34` | `#C9B25E` |
| `jacquier_leaves` | `leaves` | `#356B33` | `#274F26` | `#458044` |
| `jacquier_log_side` | `bark` | `#5E4A34` | `#46351F` | `#775E42` |
| `jacquier_log_top` | `wood_end` | `#B99B6A` | `#9A7F51` | `#CBAE7C` |
| `jacquier_planks` | `planks` | `#A4855A` | `#856A43` | `#B89A6D` |
| `jacquier_sapling` | `sapling` | `#356B33` | `#4F8A46` | `#245223` |
| `jacquier_stripped_log_side` | `planks` | `#A4855A` | `#856A43` | `#B89A6D` |
| `jacquier_stripped_log_top` | `planks` | `#A4855A` | `#856A43` | `#B89A6D` |
| `jarre` | `jar` | `#A8643C` | `#7C462A` | `#C07A4E` |
| `kapokier_leaves` | `leaves` | `#4E8A44` | `#3A6C32` | `#5F9E52` |
| `kapokier_log_side` | `bark` | `#8A7F72` | `#6C6256` | `#A09584` |
| `kapokier_log_top` | `wood_end` | `#C9B99B` | `#A99A7E` | `#D9CBB0` |
| `kapokier_planks` | `planks` | `#C2B49A` | `#A6977E` | `#D4C7AE` |
| `kapokier_sapling` | `sapling` | `#4E8A44` | `#6FA05A` | `#376A2E` |
| `kapokier_stripped_log_side` | `planks` | `#C2B49A` | `#A6977E` | `#D4C7AE` |
| `kapokier_stripped_log_top` | `planks` | `#C2B49A` | `#A6977E` | `#D4C7AE` |
| `lanterne_de_bambou` | `lantern` | `#D9B45E` | `#8C6A2E` | `#FFF3C4` |
| `laterite` | `stone` | `#9C4A2A` | `#7C371E` | `#B35C36` |
| `lion_de_pierre` | `carving` | `#9C9684` | `#7A7464` | `#B4AE9B` |
| `lotus_blanc` | `flower` | `#F2EFE6` | `#D3CEC2` | `#FFFFFF` |
| `lotus_rose` | `flower` | `#DC8FA8` | `#B56C86` | `#F0B5C8` |
| `manguier_leaves` | `leaves_fruit` | `#2F6B31` | `#1F4E22` | `#3E8040` |
| `manguier_log_side` | `bark` | `#6B5340` | `#4F3C2C` | `#83674F` |
| `manguier_log_top` | `wood_end` | `#C0A470` | `#A1885A` | `#D2B886` |
| `manguier_planks` | `planks` | `#B08A5C` | `#916F46` | `#C49E70` |
| `manguier_sapling` | `sapling` | `#2F6B31` | `#4C8A46` | `#1D4A1F` |
| `manguier_stripped_log_side` | `planks` | `#B08A5C` | `#916F46` | `#C49E70` |
| `manguier_stripped_log_top` | `planks` | `#B08A5C` | `#916F46` | `#C49E70` |
| `marbre_blanc` | `polished` | `#E6E2D8` | `#CFCABE` | `#F3F0E8` |
| `marmite` | `metal` | `#4A4A50` | `#33333A` | `#5E5E66` |
| `metier_a_tisser` | `planks` | `#9A7748` | `#7A5C34` | `#B08C5A` |
| `minerai_de_lotus` | `ore` | `#8C9089` | `#9AD1E0` | `#A3A79F` |
| `mortier` | `carving` | `#9A9483` | `#7A7566` | `#B0AA98` |
| `mousson_vase` | `mud` | `#4F4638` | `#3B3429` | `#655A49` |
| `mur_de_roseaux` | `thatch` | `#A98F4B` | `#8A7238` | `#C0A75E` |
| `murier_buisson` | `leaves_fruit` | `#3C7536` | `#5A2D4E` | `#4E8C46` |
| `naga_balustrade` | `carving` | `#9A9686` | `#7A7767` | `#B0AC9A` |
| `natte_de_jonc` | `thatch` | `#C2AA6B` | `#9E874D` | `#D4BE80` |
| `orchidee_khmer` | `flower` | `#D46AAE` | `#A84887` | `#E88EC6` |
| `paille_de_riz` | `thatch` | `#C7A95E` | `#A98D47` | `#D9C079` |
| `paletuvier_leaves` | `leaves` | `#3B6E3A` | `#295228` | `#4C844B` |
| `paletuvier_log_side` | `bark` | `#584339` | `#3F2E27` | `#6E5549` |
| `paletuvier_log_top` | `wood_end` | `#9E6A4E` | `#7F523B` | `#B27C5F` |
| `paletuvier_planks` | `planks` | `#945E44` | `#784932` | `#A87055` |
| `paletuvier_sapling` | `sapling` | `#3B6E3A` | `#548A4E` | `#295228` |
| `paletuvier_stripped_log_side` | `planks` | `#945E44` | `#784932` | `#A87055` |
| `paletuvier_stripped_log_top` | `planks` | `#945E44` | `#784932` | `#A87055` |
| `palmier_a_sucre_leaves` | `palm_leaves` | `#437A36` | `#2F5B25` | `#569147` |
| `palmier_a_sucre_log_side` | `bark` | `#5C4938` | `#423325` | `#735C47` |
| `palmier_a_sucre_log_top` | `wood_end` | `#B89A6C` | `#967B50` | `#CAAE80` |
| `palmier_a_sucre_planks` | `planks` | `#AE8855` | `#8E6C3F` | `#C29C68` |
| `palmier_a_sucre_sapling` | `sapling` | `#437A36` | `#62944B` | `#2F5B25` |
| `palmier_a_sucre_stripped_log_side` | `planks` | `#AE8855` | `#8E6C3F` | `#C29C68` |
| `palmier_a_sucre_stripped_log_top` | `planks` | `#AE8855` | `#8E6C3F` | `#C29C68` |
| `panier` | `basket` | `#B59A5E` | `#8F7742` | `#C9B075` |
| `pierre_des_ombres_runique` | `runes` | `#3B3F46` | `#8A5AA8` | `#4B5058` |
| `pierre_du_lotus` | `stone` | `#8C9089` | `#6E736C` | `#A3A79F` |
| `pierre_du_lotus_polie` | `polished` | `#8C9089` | `#7C8078` | `#9BA095` |
| `pierre_du_lotus_sculptee` | `carving` | `#868A83` | `#64685F` | `#A8AC9F` |
| `pierre_erodee` | `roughstone` | `#8A8577` | `#6B675C` | `#A19B8C` |
| `pierre_moussue_de_temple` | `bricks` | `#6E6B4E` | `#4A5D36` | `#857E5D` |
| `pilier_de_laterite` | `column` | `#9C4A2A` | `#7C371E` | `#B35C36` |
| `pilier_de_lotus` | `column` | `#8C9089` | `#6E736C` | `#A3A79F` |
| `piment` | `crop` | `#4E7A32` | `#C43A2A` | `#6B9A46` |
| `pin_du_cambodge_leaves` | `leaves` | `#2F5E3A` | `#204428` | `#3E734A` |
| `pin_du_cambodge_log_side` | `bark` | `#6B4E37` | `#4E3725` | `#826146` |
| `pin_du_cambodge_log_top` | `wood_end` | `#C4A372` | `#A38457` | `#D6B786` |
| `pin_du_cambodge_planks` | `planks` | `#BC9866` | `#9C7B4E` | `#CEA978` |
| `pin_du_cambodge_sapling` | `sapling` | `#2F5E3A` | `#487A4E` | `#204428` |
| `pin_du_cambodge_stripped_log_side` | `planks` | `#BC9866` | `#9C7B4E` | `#CEA978` |
| `pin_du_cambodge_stripped_log_top` | `planks` | `#BC9866` | `#9C7B4E` | `#CEA978` |
| `planche_de_toit` | `planks` | `#7A5C3A` | `#5E462C` | `#916E48` |
| `racine_de_banyan` | `bark` | `#6B5844` | `#4E3F30` | `#826C55` |
| `riz_culture` | `crop` | `#8FA83C` | `#6E8A2A` | `#B2C255` |
| `riz_empile` | `thatch` | `#D2B96E` | `#B29A54` | `#E4CE86` |
| `riz_paille` | `thatch` | `#C9AE63` | `#A98F4B` | `#DCC57A` |
| `riz_sauvage` | `crop` | `#7E9E3A` | `#5F7A28` | `#98B84E` |
| `sable_de_riviere` | `sand` | `#D8C79A` | `#C4B183` | `#E5D6AC` |
| `socle_d_offrande` | `carving` | `#9E9A8B` | `#C9A24A` | `#B5B0A0` |
| `sol_de_temple` | `polished` | `#9A9686` | `#7C7868` | `#B2AE9E` |
| `statue_de_pierre` | `carving` | `#A9A594` | `#8A8677` | `#BDBAAB` |
| `table_de_forge` | `metal` | `#5A5A60` | `#3E3E44` | `#74747C` |
| `tambour` | `drum` | `#8A5A34` | `#C9AE7A` | `#A06E42` |
| `tapis_de_soie_krama` | `fabric` | `#A8332E` | `#E7E2D4` | `#C2443E` |
| `teck_leaves` | `leaves` | `#3F7A34` | `#2F5E27` | `#4F9042` |
| `teck_log_side` | `bark` | `#6E4A2E` | `#523419` | `#845C3B` |
| `teck_log_top` | `wood_end` | `#B08A5A` | `#8E6C41` | `#C4A170` |
| `teck_planks` | `planks` | `#A87A4A` | `#8B6238` | `#BE9059` |
| `teck_sapling` | `sapling` | `#3F7A34` | `#5A8A3A` | `#2C5A24` |
| `teck_stripped_log_side` | `planks` | `#A87A4A` | `#8B6238` | `#BE9059` |
| `teck_stripped_log_top` | `planks` | `#A87A4A` | `#8B6238` | `#BE9059` |
| `teleporteur_de_lotus` | `crystal` | `#8CC9B0` | `#4E8A72` | `#BDE6D2` |
| `terre_cuite` | `bricks` | `#A85B37` | `#83452A` | `#C06C43` |
| `terre_lateritique` | `soil` | `#7E4327` | `#65341C` | `#955238` |
| `tissu_suspendu` | `fabric` | `#C98A3E` | `#A66B2A` | `#DEA25A` |
| `toit_de_chaume` | `thatch` | `#B59A5E` | `#8F7742` | `#C9B075` |
| `torche_de_resine` | `torch` | `#8A5A2E` | `#FFD27A` | `#C98A3E` |
| `tour_de_poterie` | `clay` | `#A8613C` | `#84492C` | `#C07950` |
| `tuile_de_terre_cuite` | `tiles` | `#9E4E2E` | `#7C3A20` | `#B85F3A` |
| `tuile_vernissee` | `tiles` | `#2F5E4E` | `#244A3D` | `#3B7260` |
| `visage_de_pierre` | `faces` | `#8F8B7C` | `#6E6A5D` | `#A6A292` |
| `vitrail_de_temple` | `stained_glass` | `#C9A24A` | `#8C6A2E` | `#E0BC63` |
