# Journal des modifications (Changelog) — Cambocraft

Toutes les évolutions notables du mod sont consignées par phase de développement.

---

## [Phase 0 — Recherche préalable] — 2026-11-11
- Étude complète des mappings officiels **Yarn `1.21.1+build.3`** et de **Fabric API `0.116.18+1.21.1`** (`DataComponentTypes`, `CustomPayload`, `ResourceManagerHelper`, `AttributeModifiersComponent`).
- Analyse des schémas JSON vanilla 1.21.1 pour les dimensions personnalisées, routeurs de densité (`noise_settings`) et structures Jigsaw.
- Recherche culturelle, géographique et architecturale sur le Cambodge (bassin du Tonlé Sap, latérite et grès khmer, riziculture, sériciculture, cuisine khmère, Pradal Serey, courses de pirogues, danse Apsara, respect culturel).
- Rédaction de `docs/RESEARCH.md`.

## [Phase 1 — Conception générale] — 2026-11-11
- Définition de l'architecture en 18 paquets Java sous `com.cambocraft.*` avec séparation stricte entre noyau de règles testable hors jeu et adaptateurs Minecraft/Fabric.
- Spécification des 15 biomes, des 12 zones (niveaux 1 à 200 + Royaume des Esprits), des 80+ pouvoirs et du système de gacha au Bol de Riz Béni avec *pity* (30 / 80 / 250).
- Définition des schémas JSON rechargeables via `/reload` sous `data/cambocraft/cambo/` et des formules mathématiques d'équilibrage.
- Rédaction de `docs/DESIGN.md` et `docs/DECISIONS.md`.

## [Phase 2 — Socle technique, registres, textures et validation] — 2026-11-11
- Configuration du projet Gradle/Loom 1.7.4 (`build.gradle`, `gradle.properties`, `settings.gradle`, `fabric.mod.json`) et de la chaîne de compilation hors ligne Java 21 (`tools/dev/apicheck/check.sh`, `gen_stubs.py`).
- Implémentation du socle `util/` (`Ids`, `Localized`, `JsonUtil`, `DataBundle`, `Log`, `Tick`, `Validate`, `Effects`) et du gestionnaire de données rechargeable `data/DataManager` (`DataConsumer`, écouteur `ResourceManagerHelper`).
- Catalogue déclaratif et génération (`tools/gen_registry.py`) de **220 blocs** (`CamboBlocks`) et **199 objets autonomes** (`CamboItems`, 419 objets au total avec les blocs) ainsi que des `blockstates`, modèles, tables de butin, tags d'outils et traductions `fr_fr.json` / `en_us.json`.
- Générateur procédural déterministe de textures 16×16 (`tools/gen_textures.py`) : 160 textures de blocs, 199 icônes d'objets et `icon.png` 64×64, avec traçabilité dans `assets_src/textures/manifest_textures.json` et documentation dans `docs/PALETTES.md`.
- Implémentation des fondations écologiques (`Season`, `Seasons`), économiques (`Economy`) et du système de pêche (`FishSpecies`, `FishingTable`, `FishingRegistry`, `FishingMinigame`, `FishingCatch`, `CamboFishingRodItem`, 16 espèces JSON).
- Validateur global `tools/validate.py` : 1 238 JSON vérifiés, **0 erreur** ; 5 suites de tests unitaires passantes (`check.sh`).

## [Phase 3 — Monde, dimension, 15 biomes, écologie et 128 structures NBT] — 2026-11-11
- Implémentation de `world/CambodiaDimension` (clés de registre de la dimension `cambocraft:cambodia` et des **15 biomes**) et de `world/LotusPortal` (anneau 3×3 de Pierre du Lotus activé par un Bol de Riz Béni posé au centre, aller-retour survie sans commande).
- Implémentation du cycle rizicole à 4 stades `ecology/RiceCycle` (*semis → repiquage → croissance → récolte*, couplage direct avec l'indice d'inflation du riz `Economy`) et des phénomènes atmosphériques `ecology/WorldAtmosphere` (crue saisonnière du Grand Lac découvrant les temples engloutis à la saison sèche, brume matinale, ralentissement sous pluie de mousson, orages).
- Générateur `tools/gen_worldgen.py` : dimension `cambocraft:cambodia`, routeur de densité `cambodia_noise.json`, 15 biomes, 16 features végétales et minières, et les 12 zones de progression JSON (`data/cambocraft/cambo/zones/`).
- Générateur paramétrique `tools/gen_structures.py` : **64 familles de structures × 2 variantes = 128 fichiers `.nbt`** compressés GZip (`DataVersion: 3953`), `template_pool` Jigsaw, `structure` et `structure_set`.
- Validation complète (`tools/validate.py` : 1 556 JSON, 128 NBT, 0 erreur) et 6 suites de tests unitaires passantes.

## [Phase 4 — PNJ, économie, réputation, cuisine khmère, soie, sculpture et forge] — 2026-11-11
- Implémentation des 4 factions régionales (`npc/Faction` : Tonlé, Ly, Srok, Kompong) et des 5 paliers de réputation (`npc/ReputationTier` : inconnu, connu, respecté, honoré, légendaire) avec conséquences mesurables sur les prix, le déblocage des recettes et l'attitude des PNJ.
- Implémentation de `npc/NpcDefinition` (routines horaires à 4 créneaux et mise à l'abri sous l'orage), `npc/DialogueNode` (arbres de dialogues français/anglais à embranchements et conditions), `npc/MerchantOfferSpec` et `npc/NpcRegistry`.
- Implémentation de l'artisanat cambodgien : `craft/CookingRecipeSpec` & `CookingRegistry` (16 plats khmers à buffs temporaires débloqués par réputation), `craft/SilkChain` (chaîne de la soie et teintures naturelles), `craft/StoneSculpture` (9 œuvres sculptées au maillet et ciseau) et `craft/ForgeUpgrade` (renforcement `+1` à `+5` non destructif).
- Générateur `tools/gen_npc_craft_data.py` : 16 profils de PNJ JSON, 16 dialogues à embranchements, 8 catalogues marchands, 16 recettes de cuisine khmère, recettes d'établi et documentation `docs/RECETTES.md`.
- Validation (`tools/validate.py` : 1 617 JSON, 0 erreur) et 7 suites de tests unitaires passantes.

## [Phase 5 — Courbe de progression 1-200, 12 zones, téléporteurs du lotus et 85 quêtes] — 2026-11-11
- Implémentation de `progression/ProgressionCurve` : niveaux `1` à `200` suivant la loi `round(120 × n^1.8)` (~35-45 h de jeu complet), système de **Renaissance** (`1` à `5` étoiles dorées avec multiplicateur permanent `+6 %` par rang) et calcul des statistiques dérivées (PV max, puissance d'attaque, énergie spirituelle).
- Implémentation de `progression/ZoneDefinition` & `ZoneRegistry` : 12 zones ordonnées de `village_de_tonle` (niv. 1-10) au `royaume_des_esprits` (post-200 / Renaissance + victoire sur le Gardien de la Source Sacrée), avec verrouillage progressif par niveau et par boss précédent.
- Implémentation de `world/LotusTeleporter` : réseau de téléportation inter-zones validé côté serveur entre les pierres de téléportation du lotus activées dans `cambocraft:cambodia`.
- Implémentation du moteur de quêtes piloté par données `quest/QuestDefinition`, `quest/QuestEngine` et `quest/QuestRegistry` : états `NON_DEBLOQUEE`, `DISPONIBLE`, `EN_COURS`, `TERMINEE`, `REPETABLE`, verrouillage d'abandon sur les quêtes principales et d'introduction, et résolution des **3 fins** du Chapitre 12 (*La Leçon du Bol* : `lumiere`, `ombre`, `equilibre`).
- Générateur `tools/gen_quests.py` : **85 quêtes JSON** (`data/cambocraft/cambo/quests/`), comprenant la quête d'introduction *L'Offrande du Bol*, les 12 chapitres de la quête principale, 64 quêtes secondaires régionales (16 par faction/région) et 8 quêtes répétables (4 saisonnières, 2 quotidiennes, 2 hebdomadaires).
- Auto-amorçage de `tools/dev/apicheck/check.sh` et validation complète (`tools/validate.py` : 1 702 JSON, 85 quêtes, 0 erreur ; `check.sh` : 8 suites de tests unitaires passantes).

## [Phase 6 — Faune cambodgienne, Clan Ombre, 12 boss multi-phases, 84 pouvoirs du Nen, 34 synergies et gacha] — 2026-11-11
- Implémentation du système de pouvoirs du Nen (`power/PowerRarity`, `PowerElement`, `PowerType`, `PowerDefinition`, `SynergyDefinition`, `PowerRegistry`) : **84 pouvoirs JSON** répartis en 22 Communs, 20 Rares, 18 Épiques, 14 Légendaires et 10 Mythiques à travers les 7 éléments (`eau`, `feu`, `air`, `terre`, `ombre`, `lumiere`, `vie`) et les 8 types fonctionnels, chacun doté de 5 niveaux d'évolution et d'une spécification de Maîtrise, ainsi que **34 synergies** (`data/cambocraft/cambo/synergies/`).
- Implémentation du tirage au Bol de Riz Béni (`power/RiceBowlGacha`) avec compteurs de *hard pity* individuels par joueur (**30** pour Épique+, **80** pour Légendaire+, **250** pour Mythique) et simulation Monte-Carlo `tools/sim_gacha.py` validée sur **5 000 000 de tirages sans pity + 5 000 000 de tirages avec pity** (`docs/EQUILIBRAGE.md`).
- Implémentation du **Carnet du Nen** (`power/NenNotebook`) : collection illimitée, 3 emplacements actifs simultanés, détection automatique des synergies actives, conversion des doublons niveau 5 en maîtrise/éclats de Nen, et gestion serveur de l'énergie spirituelle et des temps de recharge.
- Implémentation de la faune cambodgienne respectueuse (`combat/WildlifeSpecies` : 12 espèces dont le Buffle d'eau apprivoisable, l'Éléphant d'Asie, l'Ibis géant et le Dauphin de l'Irrawaddy), de la hiérarchie du Clan Ombre (`combat/ShadowClanRank`, `combat/ShadowNinjaAi` : transformation d'un villageois infiltré en ninja en **5 étapes** et IA tactique), du moteur de rencontre des **12 boss multi-phases** (`combat/BossPhaseSpec`, `combat/BossEncounterEngine`, `combat/EnemyDefinition`, `combat/CombatRegistry` : 56 profils JSON dont 12 boss à 3–4 phases) et du calcul serveur des dégâts avec *i-frames* (`combat/CombatEngine`).
- Implémentation des événements saisonniers dynamiques (`event/SeasonalEventDefinition`, `event/ShadowNightManager`) dont la **Nuit des Ombres** (une fois par saison en 3 vagues).
- Validation complète (`tools/validate.py` : 1 887 JSON, 84 pouvoirs, 34 synergies, 56 ennemis/boss, 0 erreur ; `check.sh` : 94 classes compilées, 9 suites de tests unitaires passantes).

## [Phase 7 — Compagnon Neak Ta, montures, mini-jeux culturels, réseau C2S/S2C, client, audio et documentation finale] — 2026-11-11
- Implémentation du compagnon spirituel **Neak Ta** à **3 stades d'évolution** (`companion/NeakTaCompanion` : *Lueur du Sanctuaire*, *Gardien de Grès*, *Avatar Ancestral du Lotus* avec sauvetage quotidien contre un coup fatal) et des montures traditionnelles (`companion/MountSystem` : **Buffle d'eau** agile dans la boue de rizière et sous la mousson + **Pirogue khmère**).
- Implémentation des mini-jeux culturels (`minigame/PradalSereyArena` à 3 paliers et rythme *Sarama*, `minigame/PirogueRaceCourse` sur 3 parcours avec validation ordonnée des portes et classement des records, `minigame/ApsaraDanceGame` à 4 mudras sacrés et `minigame/NightMarketManager`).
- Implémentation de la couche réseau C2S/S2C (`network/CamboPayloads`, `network/ServerPacketValidator` : validation serveur systématique de la distance `<= 6,0` blocs, anti-spam, possession des pouvoirs et déblocage des zones).
- Implémentation de l'interface client et de l'accessibilité (`client/CambocraftClient`, `client/CamboHudModel`, `client/AccessibilityConfig` : palettes daltoniennes Okabe-Ito, réduction des effets visuels, échelle de texte `0,85..1,50` et sous-titres FR/EN).
- Générateur audio `tools/gen_sounds.py` : `sounds.json`, 12 fichiers `.ogg` Vorbis valides et sous-titres bilingues (`478` clés dans `fr_fr.json` et `en_us.json`), ainsi que `src/main/resources/fabric.mod.json`.
- Rédaction de la documentation finale : `README.md`, `docs/GUIDE_JOUEUR.md`, `docs/GUIDE_CREATEUR.md`, `docs/TECHNIQUE.md` et `docs/EQUILIBRAGE.md`.
- Validation finale : `tools/validate.py` (**1 889 JSON**, 220 blocs, 199 icônes d'objets, 478 clés FR/EN, 15 biomes, 64 structures / 128 NBT, 12 zones, 16 PNJ, 16 plats, 85 quêtes, 84 pouvoirs, 34 synergies, 56 ennemis/boss, 16 poissons, 12 sons `.ogg`, **0 erreur**) et `check.sh` (**124 classes compilées**, **10 suites de tests unitaires passantes**, **0 échec**).



