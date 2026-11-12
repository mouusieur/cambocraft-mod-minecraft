# Documentation technique et chaîne de compilation — Cambocraft

Version : `1.0.0` · Minecraft `1.21.1` · Yarn `1.21.1+build.3` · Fabric Loader `0.16.14` · Fabric API `0.116.18+1.21.1` · Java `21`

---

## 1. Architecture en 18 paquets (`src/main/java/com/cambocraft/`)

| Paquet | Classes principales | Rôle technique |
|---|---|---|
| `com.cambocraft` | `Cambocraft` | Point d'entrée `ModInitializer` ordonnant l'enregistrement des blocs, objets, registres et événements. |
| `registry` | `Registrar`, `CamboBlocks`, `CamboItems`, `CamboItemGroup` | Enregistrement déclaratif de **220 blocs** et **199 objets autonomes** (419 items au total). |
| `data` | `DataManager`, `DataConsumer` | Écouteur `ResourceManagerHelper` (`SERVER_DATA`) et chargeur hors ligne `chargerDepuisDossier(Path)`. |
| `world` | `CambodiaDimension`, `LotusPortal`, `LotusTeleporter` | Dimension `cambocraft:cambodia`, 15 biomes, portail horizontal 3×3 et téléporteurs inter-zones. |
| `ecology` | `Season`, `Seasons`, `RiceCycle`, `WorldAtmosphere` | Cycle de 3 saisons de 8 jours, crue du Grand Lac (`Y = 62 / 64 / 68`), temples engloutis et riziculture. |
| `npc` | `Faction`, `ReputationTier`, `Economy`, `NpcDefinition`, `DialogueNode`, `MerchantOfferSpec`, `NpcRegistry` | 4 factions, 5 paliers de réputation, inflation du riz, 16 PNJ à routine horaire, dialogues et 8 marchands. |
| `craft` | `CamboFoods`, `CamboWeapons`, `CamboWeaponItem`, `CamboHealingItem`, `CookingRecipeSpec`, `CookingRegistry`, `SilkChain`, `StoneSculpture`, `ForgeUpgrade` | 16 plats khmers à buffs, chaîne de la soie, 9 sculptures sur pierre et forge `+1..+5` non destructive. |
| `fishing` | `FishRarity`, `FishSpecies`, `FishingTable`, `FishingContext`, `FishingCatch`, `FishingMinigame`, `FishingRegistry`, `CamboFishingRodItem` | 16 espèces de poissons, mini-jeu à fenêtre de réflexe et marquage de taille/prix. |
| `progression` | `ProgressionCurve`, `ZoneDefinition`, `ZoneRegistry` | Courbe d'XP des niveaux `1` à `200` (`round(120 × n^1.8)`), Renaissance `1–5` et verrouillage des 12 zones. |
| `quest` | `QuestDefinition`, `QuestEngine`, `QuestRegistry` | Moteur d'états des 85 quêtes (intro, 12 chapitres principaux non abandonnables, 64 secondaires, 8 répétables, 3 fins). |
| `combat` | `WildlifeSpecies`, `ShadowClanRank`, `ShadowNinjaAi`, `BossPhaseSpec`, `EnemyDefinition`, `BossEncounterEngine`, `CombatEngine`, `CombatRegistry` | 12 espèces de faune, transformation en 5 étapes des ninjas de l'Ombre, 12 boss multi-phases et calcul serveur des dégâts + *i-frames*. |
| `event` | `SeasonalEventDefinition`, `ShadowNightManager` | Événements saisonniers dynamiques et **Nuit des Ombres** (3 vagues au 6e jour de chaque saison). |
| `power` | `PowerRarity`, `PowerElement`, `PowerType`, `PowerDefinition`, `SynergyDefinition`, `RiceBowlGacha`, `NenNotebook`, `PowerRegistry` | 84 pouvoirs du Nen (niveaux 1–5 + Maîtrise), 34 synergies, gacha avec *pity* (30 / 80 / 250) et Carnet du Nen. |
| `companion` | `NeakTaCompanion`, `MountSystem` | Esprit protecteur Neak Ta à 3 stades d'évolution, monture Buffle d'eau et Pirogue khmère. |
| `minigame` | `PradalSereyArena`, `PirogueRaceCourse`, `ApsaraDanceGame`, `NightMarketManager` | Arène Pradal Serey (3 paliers), courses de pirogues (3 parcours), danse Apsara et marché de nuit. |
| `network` | `CamboPayloads`, `ServerPacketValidator` | Paquets C2S/S2C et validation serveur systématique (portée `<= 6,0` blocs, anti-spam, permissions). |
| `client` | `CambocraftClient`, `CamboHudModel`, `AccessibilityConfig` | Point d'entrée client, modèle du HUD et accessibilité (daltonisme Okabe-Ito, effets réduits, taille du texte, sous-titres). |
| `datagen` / `util` | `CambocraftDataGen`, `Ids`, `Localized`, `JsonUtil`, `DataBundle`, `Log`, `Tick`, `Validate`, `Effects` | Utilitaires transversaux et synchronisation des générateurs. |

---

## 2. Chaîne de génération et de validation (`tools/`)

- `python3 tools/gen_registry.py` : génère `CamboBlocks.java`, `CamboItems.java`, blockstates, modèles, tables de butin, tags et traductions `fr_fr.json` / `en_us.json` depuis `assets_src/registry/*.yml`.
- `python3 tools/gen_textures.py` : génère de façon déterministe les 160 textures de blocs 16×16, les 199 icônes d'objets 16×16 et `icon.png` 64×64.
- `python3 tools/gen_worldgen.py` : génère la dimension `cambocraft:cambodia`, le routeur de densité `cambodia_noise.json`, les 15 biomes, les 16 features et les 12 zones.
- `python3 tools/gen_structures.py` : génère les **128 fichiers `.nbt`** compressés GZip (`DataVersion: 3953`) des 64 familles de structures (2 variantes chacune) et leurs `template_pool` Jigsaw.
- `python3 tools/gen_npc_craft_data.py` : génère les 16 PNJ, 16 dialogues, 8 marchands, 16 plats khmers et `docs/RECETTES.md`.
- `python3 tools/gen_quests.py` : génère les 85 quêtes JSON (intro, 12 chapitres principaux, 64 secondaires, 8 répétables).
- `python3 tools/gen_combat_power_data.py` : génère les 84 pouvoirs, 34 synergies, 56 ennemis/boss, 4 événements et modèles d'entités.
- `python3 tools/gen_sounds.py` : génère `sounds.json`, les 12 fichiers `.ogg` et les sous-titres bilingues.
- `python3 tools/sim_gacha.py` : exécute `5 000 000` de tirages sans pity + `5 000 000` de tirages avec pity et génère `docs/EQUILIBRAGE.md`.
- `python3 tools/validate.py` : contrôle l'intégralité des **1 889 fichiers JSON**, **128 fichiers NBT**, **360 PNG** et **12 fichiers OGG** (**0 erreur**).
- `bash tools/dev/apicheck/check.sh` : auto-amorce la chaîne Java 21 / JDT + stubs Yarn `1.21.1+build.3`, compile les **124 classes** du mod et exécute les **10 suites de tests unitaires** (**0 échec**).

---

## 3. Chaîne de compilation Fabric/Loom (production)

Le contrôle hors ligne (`check.sh`) valide les noms et les types contre des stubs Yarn ; il ne
produit pas d'artefact exécutable. Le jar réel est construit par **Gradle + Fabric Loom**, qui
télécharge Minecraft 1.21.1, applique les mappings Yarn puis remappe en intermédiaire.

### Commandes locales

```bash
./gradlew build        # -> build/libs/cambocraft-1.0.0.jar (jar jouable, remappé)
./gradlew camboTest    # -> exécute com.cambocraft.test.TestMain (noyau de règles, sans JUnit)
./gradlew runServer    # -> serveur de développement avec le mod chargé
```

`./gradlew test` est volontairement désactivé : le dépôt ne contient pas de tests JUnit, la
campagne de tests est pilotée par `TestMain` et exposée sous le nom `camboTest`.

### Contenu du jar produit

| Élément | Valeur |
| --- | --- |
| Taille | 1 219 725 octets (~1,2 Mo) |
| Entrées totales | 2 592 |
| Classes `com/cambocraft` | 128 (124 fichiers sources + 4 classes internes générées par le compilateur, toutes remappées en intermédiaire) |
| Assets | 1 393 (360 PNG, 12 OGG, 779 modèles, 220 blockstates) |
| Données | 1 047 (recipes, loot tables, tags, worldgen, structures `.nbt`) |
| `fabric.mod.json` | `id = cambocraft`, `version = 1.0.0` (variable `${version}` expansée par Loom) |
| Version de classe Java | 65 (Java 21) |

### Intégration continue (`.github/workflows/build.yml`)

Deux jobs s'exécutent à chaque `push` :

1. **`build`** — `./gradlew build`, puis vérification programmatique du jar : intégrité zip,
   nombre de classes, présence des points d'entrée, **absence de référence Yarn résiduelle**
   (contrôle sur les constantes `CONSTANT_Class` et les descripteurs de linkage uniquement, en
   excluant les 14 classes non obfusquées de Minecraft comme `MinecraftServer`), version de
   classe Java 21. Le jar est publié en artefact et attaché à la release `v1.0.0`.
2. **`serveur`** — installe Minecraft 1.21.1 + Fabric Loader via l'installeur officiel, dépose
   le jar et Fabric API dans `mods/`, démarre le serveur 4 minutes et vérifie dans le journal
   la présence de `Cambocraft : initialisation du contenu de base`, de la ligne
   `Cambocraft : 220 blocs, 199 objets et 15 biomes initialisés` et de `Done (…)`, sans
   `ModResolutionException`.

Le journal complet de chaque job est republié dans un *check run* (`cambocraft-build-log`,
`cambocraft-serveur-test`), lisible via l'API GitHub même quand les artefacts de logs sont
inaccessibles.

### Résultats du test d'intégration en jeu

Le job `serveur` démarre un vrai serveur Fabric 1.21.1 avec le jar publié, puis envoie des
commandes sur `stdin`. Relevé d'exécution :

| Vérification | Commande | Résultat observé |
| --- | --- | --- |
| Chargement du mod | — | `Loading 43 mods: … - cambocraft 1.0.0` |
| Initialisation | — | `Cambocraft : initialisation du contenu de base` |
| Enregistrement | — | `Cambocraft : 220 blocs, 199 objets et 15 biomes initialisés` |
| Données JSON | — | `Données : 347 fichier(s) chargé(s), 0 erreur(s)` |
| Sous-systèmes | — | 16 poissons, 16 PNJ, 8 marchands, 16 recettes, 12 zones, 85 quêtes, 84 pouvoirs, 34 synergies, 56 ennemis (12 boss) |
| Serveur prêt | — | `Done (9.457s)! For help, type "help"` |
| Dimension | `execute in cambocraft:cambodia run forceload add -32 -32 32 32` | `Marked 25 chunks in cambocraft:cambodia` |
| Bloc du mod | `… setblock 0 110 0 cambocraft:pierre_du_lotus` | `Changed the block at 0, 110, 0` |
| Bloc du mod | `… setblock 1 110 0 cambocraft:lotus_rose` | `Changed the block at 1, 110, 0` |
| Objet + traduction | `… summon minecraft:item … cambocraft:bol_de_riz_beni` | `Summoned new Blessed Bowl of Rice` |
| Biome | `… locate biome cambocraft:grand_lac` | `The nearest cambocraft:grand_lac is at [-1536, 91, 416]` |
| Sauvegarde | `stop` | `All chunks are saved` pour `cambodia` |

Le `fabric.mod.json` du jar publié porte bien `version = 1.0.0` (variable `${version}` expansée
par Loom) : plus aucun avertissement de version au démarrage.

### Défaut corrigé grâce à ce test

La première exécution a levé `java.lang.IllegalStateException: Feature order cycle found`
pendant la génération des chunks de `cambocraft:cambodia` :

```
Feature order cycle found, involved sources:
  [cambocraft:mangrove_et_marais, cambocraft:plaine_inondable]
```

Cause : `flore_lotus` et `flore_riz_sauvage` étaient listés dans des ordres opposés selon le
biome. Minecraft exige un ordre de features identique dans **tous** les biomes. Correction :
l'ordre de l'étape végétale est désormais dérivé d'une liste canonique dans
`tools/gen_worldgen.py`, et `tools/validate.py` reconstruit le graphe d'ordre et échoue sur tout
cycle. Sans ce test, le joueur aurait découvert un monde vide dès le premier chunk.

## Captures d'écran automatiques

Les images du README ne sont pas des rendus schématiques : elles proviennent d'un **vrai client
Minecraft 1.21.1 + Fabric** lancé sans GPU, dans `.github/workflows/captures.yml`.

| Étape | Moyen | Piège rencontré |
| --- | --- | --- |
| Rendu 3D sans carte graphique | `xvfb-run` + Mesa/llvmpipe (`LIBGL_ALWAYS_SOFTWARE=1`) | sans `libgl1-mesa-dri` + `libglx-mesa0`, LWJGL n'obtient aucun contexte OpenGL |
| Connexion au serveur | clics souris (`xdotool`) sur l'interface Multijoueur | `--quickPlayMultiplayer` est inopérant ; il faut cliquer « Multijoueur » puis « Rejoindre » |
| Attente avant capture | boucle sur la disparition de « Loading terrain… » | une capture trop tôt donne un écran noir (`sanite.png` sert de témoin) |
| Construction des scènes | `fill x y z x y z cambocraft:<bloc>` | `setblock` ne s'applique pas dans une dimension personnalisée via `execute in … run` |
| Dégagement du terrain | `fill` par tranches de 6 blocs | `/fill` est limité à 32 768 blocs : au-delà, la commande échoue **sans message** |
| Studio aérien | base à `y=200`, nettoyage jusqu'à `y=319` | le terrain naturel de `cambodia` monte très haut : sans cela, des parois de terre et des blocs flottants entrent dans le champ |
| Cadrage | `tp @a x y z yaw pitch` | `yaw=180` regarde vers −Z ; `yaw=0` tourne le dos à une scène construite en Z négatif |
| Champ de vision | `fov:70.0` dans `options.txt` | `fov:0.5` est **écrêté à 30°** : rendu téléobjectif, scènes écrasées |
| Caméra sans avant-bras | `gamemode spectator` | en créatif, le bras du joueur masque le coin bas-droit |
| Interface masquée | `hideGui:true`, `tutorialStep:none` | l'encart « Move with WASD » reste affiché plusieurs minutes |
| Récupération des PNG | check runs `capture-<fichier>-NNN` en base64 (morceaux de 60 000 car.) | les artefacts d'Actions sont inaccessibles à l'API ; le texte d'un check run est limité à 65 535 caractères |

Régénération : `tools/dev/captures/` contient `captures.py` (scènes et prise de vue),
`client.py` (client Fabric sans launcher), `illustrations.py` (bandeau et galerie) et un `Makefile`
(`make recuperer SHA=…` puis `make illustrations`).

### Deux défauts de rendu trouvés par ces captures

1. **Végétaux sur fond noir** — les modèles `block/cross` n'étaient associés à aucune couche de
   rendu. En Fabric, `render_type` dans le JSON du modèle est une propriété NeoForge : il faut
   `BlockRenderLayerMap.INSTANCE.putBlock(bloc, RenderLayer.getCutout())` côté client.
2. **Classe client dans le mauvais source set** — `splitEnvironmentSourceSets()` place le code
   client dans `src/client/java` ; une classe de `src/main/java` ne voit ni `net.minecraft.client.*`
   ni `fabric-blockrenderlayer-v1`, et `./gradlew build` échoue.
