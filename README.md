# Cambocraft — Mod Minecraft 1.21.1 (Fabric)

**Cambocraft** est un mod d'aventure, d'exploration et de maîtrise spirituelle pour **Minecraft 1.21.1 (Fabric, Java 21)** qui ouvre les portes de la dimension **`cambocraft:cambodia`**, un monde vivant inspiré des paysages, de l'architecture khmère et des traditions culturelles du Cambodge.

---

## Points forts du contenu livré

- **220 blocs et 199 objets autonomes (419 objets enregistrés)** : latérite, grès khmer ciselé, pierre du lotus, bois de teck, de manguier, de cocotier et de bambou, métiers à tisser, ateliers de sculpture, tables de forge, plats khmers, étoffes de soie, armes et reliques sacrées, accompagnés de **360 textures pixel-art 16×16** originales (`docs/PALETTES.md`).
- **Dimension `cambocraft:cambodia` et 15 biomes** : accessible à 100 % en survie via le **Portail du Lotus** (anneau horizontal 3×3 activé par un *Bol de Riz Béni*), comprenant les *Rizières en terrasses*, le *Grand Lac*, la *Jungle en ruines*, les *Montagnes karstiques*, le *Sanctuaire de lotus* et les *Îles flottantes des Garudas*.
- **Écologie à 3 saisons et 128 structures `.nbt` (64 familles × 2 variantes)** : cycle déterministe de 8 jours par saison (*Saison douce*, *Saison chaude*, *Mousson*), crue saisonnière du Grand Lac découvrant les **temples engloutis** à la saison sèche, cycle rizicole à 4 stades couplé à l'inflation économique, maisons sur pilotis, prasats, ponts Naga, arènes de Pradal Serey et embarcadères de pirogues.
- **16 PNJ à routine horaire, 4 factions, 5 paliers de réputation et artisanat complet** : arbres de dialogues bilingues (`fr_fr` / `en_us`), économie en *Pièces du Lotus*, **16 plats de cuisine khmère** à buffs, chaîne de la soie, sculpture sur grès et **forge non destructive `+1` à `+5`** (`docs/RECETTES.md`).
- **Progression des niveaux 1 à 200, Renaissance (1–5), 12 zones et 85 quêtes** : courbe d'XP `round(120 × n^1.8)`, réseau de **Pierres de Téléportation du Lotus**, quête d'introduction, quête principale en **12 chapitres** (*La Leçon du Bol* avec **3 fins** au choix : *Lumière*, *Ombre*, *Équilibre*), **64 quêtes secondaires** et **8 quêtes répétables**.
- **84 pouvoirs du Nen, 34 synergies, Gacha au Bol de Riz Béni et 12 boss multi-phases** : 5 raretés (*Commun 55 %*, *Rare 28 %*, *Épique 12 %*, *Légendaire 4,5 %*, *Mythique 0,5 %*) avec *hard pity* garanti à **30 / 80 / 250** tirages (vérifié sur 10 millions de tirages dans `docs/EQUILIBRAGE.md`), **Carnet du Nen** à 3 emplacements actifs, faune cambodgienne respectueuse, ninjas du Clan Ombre à transformation en **5 étapes**, **Nuit des Ombres** et **12 boss à 3–4 phases**.
- **Compagnon Neak Ta (3 stades), montures et mini-jeux culturels** : esprit protecteur **Neak Ta**, **Buffle d'eau**, **Pirogue khmère**, arène de **Pradal Serey** (3 paliers), **Courses de pirogues** (3 parcours et classement), **Danse Apsara**, **Marché de nuit** et options complètes d'**accessibilité** (daltonisme, effets réduits, taille du texte, sous-titres FR/EN).

---

## Vérification rapide et compilation

```bash
# 1. Valider les 1 889 fichiers JSON, 128 NBT, 360 PNG, 12 OGG et 478 clés FR/EN
python3 tools/validate.py

# 2. Compiler les 124 classes Java 21 contre les stubs Yarn 1.21.1 et lancer les 10 suites de tests
bash tools/dev/apicheck/check.sh

# 3. Lancer la simulation Monte-Carlo du Gacha (5 000 000 de tirages sans pity + 5 000 000 avec pity)
python3 tools/sim_gacha.py

# 4. Build Gradle/Loom standard (dans un environnement connecté à maven.fabricmc.net)
./gradlew build
```

---

## Documentation complète (`docs/`)

- [`docs/RESEARCH.md`](docs/RESEARCH.md) — Recherche préalable technique (Yarn 1.21.1, Fabric API) et culturelle cambodgienne.
- [`docs/DESIGN.md`](docs/DESIGN.md) — Architecture générale en 18 paquets, schémas JSON et formules mathématiques.
- [`docs/DECISIONS.md`](docs/DECISIONS.md) — Journal des 12 décisions d'architecture (`D-01` à `D-12`).
- [`docs/PALETTES.md`](docs/PALETTES.md) — Palettes chromatiques des 160 textures de blocs et 199 icônes d'objets.
- [`docs/RECETTES.md`](docs/RECETTES.md) — Catalogue des recettes de cuisine khmère, soie, sculpture, forge et marchands.
- [`docs/EQUILIBRAGE.md`](docs/EQUILIBRAGE.md) — Résultats Monte-Carlo (`tools/sim_gacha.py`), courbe d'XP 1–200 et boss.
- [`docs/GUIDE_JOUEUR.md`](docs/GUIDE_JOUEUR.md) — Manuel complet du joueur en survie.
- [`docs/GUIDE_CREATEUR.md`](docs/GUIDE_CREATEUR.md) — Guide de création de datapacks rechargeables via `/reload`.
- [`docs/TECHNIQUE.md`](docs/TECHNIQUE.md) — Documentation technique des paquets Java et des générateurs Python.
- [`docs/CHANGELOG.md`](docs/CHANGELOG.md) — Journal détaillé des phases 0 à 7.
