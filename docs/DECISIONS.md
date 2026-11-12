# Journal des décisions d'architecture et de conception — Cambocraft

Chaque arbitrage technique ou de conception pris en autonomie est consigné ici avec sa date, le choix retenu, les options écartées et la justification.

---

## D-01 (2026-11-11) — Version exacte de la pile Fabric / Minecraft 1.21.1
- **Choix retenu** : Minecraft `1.21.1`, Mappings Yarn `1.21.1+build.3`, Fabric Loader `0.16.14`, Fabric API `0.116.18+1.21.1`, Loom `1.7.4`, Java `21`.
- **Alternatives écartées** : Mojang Official Mappings ; Fabric Loader `0.17+` obligatoire.
- **Justification** : Le cahier des charges impose Yarn et MC 1.21.1. La version `0.116.18+1.21.1` est la dernière révision stable de la branche 1.21.1 de Fabric API.

## D-02 (2026-11-11) — Modèles et animations d'entités : hiérarchie native JSON/Java plutôt que GeckoLib
- **Choix retenu** : Modèles hiérarchiques natifs (`ModelPart` / descripteurs JSON sous `assets/cambocraft/models/entity/` et courbes d'animation sous `assets/cambocraft/animations/`).
- **Alternatives écartées** : Dépendance externe `GeckoLib 4.9.3`.
- **Justification** : GeckoLib 4.9.3 requiert un dépôt Maven tiers inaccessible hors ligne et impose Fabric Loader `>= 0.17`. Le système hiérarchique natif piloté par JSON offre la même expressivité (respiration, marche, salut, combat, transformation des ninjas en 5 étapes, phases de boss) sans aucune dépendance externe fragile.

## D-03 (2026-11-11) — Chaîne de compilation de contrôle hors ligne via Eclipse JDT + stubs Yarn
- **Choix retenu** : En complément du projet Gradle/Loom standard (`build.gradle`, `gradlew`), mise en place de `tools/dev/apicheck/check.sh` et `gen_stubs.py` qui génèrent 5 575 stubs typés depuis les mappings officiels Yarn `1.21.1+build.3` et compilent l'intégralité du mod et des tests avec le compilateur batch Eclipse JDT en Java 21.
- **Alternatives écartées** : Écrire du code Java sans vérification de types lorsque les dépôts Maven distants (`maven.fabricmc.net`, `libraries.minecraft.net`) sont filtrés par le bac à sable réseau.
- **Justification** : L'exigence de qualité impose de compiler et d'exécuter les tests à chaque étape sans laisser passer d'erreur de typage ni de régression.

## D-04 (2026-11-11) — Catalogue déclaratif YAML pour les blocs, objets et traductions
- **Choix retenu** : `assets_src/registry/blocks.yml` et `assets_src/registry/items.yml` constituent l'unique source de vérité ; `tools/gen_registry.py` génère `CamboBlocks.java` (classe finale ordonnée + listes `ALL` et `CUTOUT`), `CamboItems.java`, les `blockstates`, les modèles, les tables de butin, les tags d'outils et les fichiers `fr_fr.json` / `en_us.json`.
- **Alternatives écartées** : Saisie manuelle dispersée des 220 blocs et 199 objets dans le code Java et les JSON.
- **Justification** : Élimine toute divergence entre identifiants Java, modèles JSON et clés de traduction ; vérifié par `tools/validate.py` (0 erreur sur 1 238 JSON).

## D-05 (2026-11-11) — Organisation plate par catégorie pour `DataManager` (`data/cambocraft/cambo/<categorie>/<id>.json`)
- **Choix retenu** : Toutes les données rechargeables par `/reload` résident sous `data/cambocraft/cambo/<categorie>/<id>.json` et sont distribuées aux `DataConsumer` dans leur ordre d'enregistrement. Un fichier JSON invalide est journalisé via `Log.dataError` et ignoré sans interrompre le chargement des autres fichiers.
- **Alternatives écartées** : Désérialisation bloquante levant une exception fatale au démarrage du serveur.
- **Justification** : Un datapack utilisateur mal formé ne doit jamais faire planter un monde existant.

## D-06 (2026-11-11) — Pirogue de course et Royaume des Esprits traités en priorité P1
- **Choix retenu** : Les courses de pirogues (3 parcours + classement) et la zone 12 post-200 (*Royaume des Esprits* + boss *L'Ombre Première*) sont intégrées en **P1**.
- **Alternatives écartées** : Les reporter en P2 non livré.
- **Justification** : Bien que mentionnés en P2 au §3, ils figurent dans les tableaux détaillés (§12.2, §13.2) et dans les critères d'acceptation §18 ; l'option la plus complète est retenue.

## D-07 (2026-11-11) — Portail du Lotus en anneau horizontal 3×3 activé par le Bol de Riz Béni
- **Choix retenu** : Le portail vers `cambocraft:cambodia` est formé d'un anneau horizontal 3×3 de `cadre_du_portail` (ou `pierre_du_lotus_sculptee`) autour d'un `socle_d_offrande` central, activé en y déposant un `bol_de_riz_beni` par clic droit. L'arrivée génère un parvis sécurisé muni d'un cœur de retour au Village de Tonlé (`0, 72, 0`).
- **Alternatives écartées** : Portail vertical de type Nether allumé au briquet ; téléportation par commande `/execute in`.
- **Justification** : Respecte fidèlement le rituel d'offrande décrit au §4.1 et garantit un accès et un retour 100 % en survie sans commande.

## D-08 (2026-11-11) — Génération paramétrique binaire des 128 fichiers `.nbt` de structures (64 familles × 2 variantes)
- **Choix retenu** : `tools/gen_structures.py` encode directement en GZip NBT (`DataVersion: 3953`, MC 1.21.1) 64 familles de structures architecturales khmères et rurales, chacune déclinée en 2 variantes (`_v1.nbt`, `_v2.nbt`) reliées à des `template_pool` Jigsaw.
- **Alternatives écartées** : Construire manuellement en jeu 120+ structures sans reproductibilité ni contrôle de palette.
- **Justification** : Garantit l'homogénéité des palettes (latérite, grès khmer, teck, chaume, balustrades naga, lions gardiens, dalles d'énigme) et la vérification automatique par `tools/validate.py`.

## D-09 (2026-11-11) — Forge `+1` à `+5` non destructive en cas d'échec
- **Choix retenu** : Lors d'une tentative d'amélioration sur la `table_de_forge` (`ForgeUpgrade`), un échec consomme le cristal de forge et les pièces du lotus, mais ne détruit jamais l'arme ou l'armure et ne fait pas régresser son niveau actuel.
- **Alternatives écartées** : Destruction de l'objet ou rétrogradation punitive aux paliers `+4` et `+5`.
- **Justification** : Préserve le plaisir de progression sur les reliques rares tout en maintenant un puits économique équilibré pour les cristaux de forge.

## D-10 (2026-11-11) — Courbe d'expérience `round(120 × n^1.8)`, Renaissance 1-5 et trois fins du Chapitre 12
- **Choix retenu** : L'XP requise pour passer du niveau `n` à `n+1` suit `round(120 × n^1.8)` jusqu'au niveau 200 (cumul ~12,18 M XP), avec accès à la **Renaissance** (rangs 1 à 5, `+6 %` de multiplicateur de statistiques par rang) au niveau 200. Le Chapitre 12 (*La Leçon du Bol*) propose trois résolutions explicites (`lumiere`, `ombre`, `equilibre`), chacune octroyant un titre permanent bilingue et débloquant la zone 12 post-200 (*Royaume des Esprits*).
- **Alternatives écartées** : Courbe linéaire trop plate en fin de jeu ou exponentielle pure punitive au-delà du niveau 120 ; fin unique imposée au joueur.
- **Justification** : Aligne la durée de progression sur la cible de 35 à 45 heures à travers les 11 zones principales et valorise le choix moral du joueur face au Gardien de la Source Sacrée.

## D-11 (2026-11-11) — Garanties de Pity (30 / 80 / 250), conversion des doublons au niveau 5 et Nuit des Ombres au 6e jour de saison
- **Choix retenu** : Le tirage au Bol de Riz Béni applique les taux de base `55 % / 28 % / 12 % / 4,5 % / 0,5 %` assortis de trois compteurs de *hard pity* indépendants par joueur (`30` pour Épique+, `80` pour Légendaire+, `250` pour Mythique), vérifiés par simulation Monte-Carlo sur 5 000 000 de tirages (`tools/sim_gacha.py`). Tout doublon d'un pouvoir déjà au niveau 5 confère `+25` points d'avancement de Maîtrise et des éclats de Nen. La **Nuit des Ombres** se déclenche de manière déterministe une fois par saison au crépuscule du 6e jour (`13000..23000` ticks) en 3 vagues successives.
- **Alternatives écartées** : Tirage purement aléatoire sans garantie (frustrant sur une rareté Mythique à 0,5 %) ou perte sèche des doublons au-delà du niveau 5.
- **Justification** : Garantit que chaque bol de riz béni consommé fait progresser durablement le Carnet du Nen du joueur.

## D-12 (2026-11-11) — Validation serveur systématique des paquets C2S et palettes daltoniennes Okabe-Ito
- **Choix retenu** : Toutes les requêtes C2S (`ActiverPouvoirC2S`, `EquiperPouvoirC2S`, `TeleporterLotusC2S`, `ChoisirDialogueC2S`) transitent par `ServerPacketValidator`, qui impose une portée maximale de `6,0` blocs pour les interactions PNJ/téléporteur, un délai minimal de `2` ticks entre deux paquets d'action et la vérification serveur des conditions de déblocage. Côté client, `AccessibilityConfig` propose la palette universelle Okabe-Ito pour les 5 raretés de pouvoirs ainsi qu'un mode d'effets réduits (`×0,25` particules) et des sous-titres bilingues pour les 12 événements sonores.
- **Alternatives écartées** : Faire confiance aux coordonnées ou à l'état d'ouverture envoyés par le client ; coder en dur les couleurs de rareté sans alternative pour les joueurs daltoniens.
- **Justification** : Empêche toute triche réseau en multijoueur et assure une lisibilité irréprochable du HUD et du Carnet du Nen pour tous les profils visuels.



