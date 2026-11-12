# Document de conception générale — Cambocraft (Phase 1)

Date : 2026-11-11  
Version : 1.0 · Minecraft 1.21.1 (Fabric)

---

## 1. Vision et piliers de jeu

Cambocraft transporte le joueur dans la dimension **`cambocraft:cambodia`**, un monde vivant inspiré du Cambodge rural et historique. Quatre piliers guident chaque système :

1. **Progression lisible et grisante** : niveaux 1 à 200 (`XP(n) = round(120 × n^1.8)`), 12 zones étagées dotées chacune de leur boss multi-phases et de téléporteurs de Pierre du Lotus, Renaissance post-200 et Royaume des Esprits.
2. **Culture vivante et respectueuse** : cycle de 3 saisons (8 jours configurables), crue du Grand Lac, riziculture influençant l'économie en *Pièces du Lotus*, cuisine khmère à buffs, sériciculture, sculpture sur grès, Pradal Serey, courses de pirogues et danse Apsara.
3. **Exploration et surprise** : plus de 60 structures NBT (avec au moins 2 variantes chacune), temples engloutis révélés à la saison sèche, PNJ à routine quotidienne et ninjas du clan Ombre dissimulés pouvant se dévoiler selon le contexte.
4. **Maîtrise des pouvoirs du Nen** : plus de 80 pouvoirs répartis en 5 raretés, 7 éléments et 8 types, obtenus en dégustant un bol de riz béni auprès d'un apprenti moine (avec système de *pity* garanti), stockés sans limite dans le **Carnet du Nen** (3 actifs simultanément) et enrichis par plus de 30 synergies et la maîtrise.

---

## 2. Architecture logicielle (`com.cambocraft.*`)

| Paquet | Responsabilité principale |
|---|---|
| `registry` | Enregistrement déclaratif des 220 blocs (`CamboBlocks`), 199 objets autonomes (`CamboItems`) et de l'onglet créatif (`CamboItemGroup`). |
| `data` | Gestionnaire `DataManager` et interface `DataConsumer` : chargement de `data/cambocraft/cambo/<categorie>/*.json` et rechargement à chaud via `/reload`. |
| `world` | Dimension `cambocraft:cambodia`, portail de Pierre du Lotus activé par un Bol de Riz Béni, 15 biomes, routeur de bruit karst/terrasses/Grand Lac, structures et téléporteurs. |
| `ecology` | Cycle des 3 saisons (`Season`, `Seasons`), crue du Grand Lac, cycle du riz à 4 stades, brume matinale, mousson et faune/flore. |
| `npc` | 15 archétypes de PNJ à routine horaire, dialogues français à embranchements, réputation auprès des 4 factions (5 paliers), économie en Pièces du Lotus et inflation du riz (`Economy`). |
| `craft` | Cuisine khmère à buffs temporaires, chaîne de la soie, sculpture sur pierre, forge `+1` à `+5`, armes et soins. |
| `fishing` | Mini-jeu de pêche à fenêtre de réflexe (`FishingMinigame`), 16 espèces JSON (`FishingRegistry`, `FishingTable`), marquage NBT de taille et prix (`FishingCatch`). |
| `quest` | Moteur de quêtes JSON (étapes, conditions, branches, abandon), quête d'introduction, quête principale en 12 chapitres (*La Leçon du Bol*, 3 fins), 60+ quêtes secondaires et répétables. |
| `progression` | Niveaux 1–200, courbe d'XP, verrouillage des 12 zones, statistiques par zone et système de **Renaissance** post-200. |
| `combat` | Fenêtres d'invulnérabilité (*i-frames*), coups critiques, calcul de dégâts serveur, hiérarchie du clan Ombre (transformation en 5 étapes, IA variée), 12 boss à ≥3 phases. |
| `event` | Événements saisonniers dynamiques, dont la **Nuit des Ombres** (une fois par saison) et la Fête des Eaux / Récolte du Lotus. |
| `power` | 80+ pouvoirs JSON (niveaux 1–5), gacha du bol de riz avec *pity* (30 / 80 / 250), Carnet du Nen, 3 emplacements actifs, 30+ synergies, maîtrise et énergie/aura serveur. |
| `companion` | Esprit protecteur **Neak Ta** à 3 stades d'évolution, monture Buffle d'eau et Pirogue à capitaine. |
| `minigame` | Arène de **Pradal Serey** (3 paliers), **Course de pirogues** (3 parcours et classement), **Danse Apsara** et Marché de nuit. |
| `network` | Paquets `CustomPayload` C2S/S2C compacts, validation serveur systématique (distance, temps de recharge, permissions). |
| `client` | HUD d'énergie/aura, suivi de quête, Carnet du Nen, journal, dialogues, notifications, sous-titres et options d'accessibilité (daltonisme, effets réduits, taille du texte). |
| `datagen` | Points d'entrée Fabric Data Generation et synchronisation avec les générateurs Python de `tools/`. |
| `util` | Identifiants (`Ids`), JSON tolérant (`JsonUtil`, `DataBundle`), textes localisés (`Localized`), horloge (`Tick`), validation (`Validate`) et effets (`Effects`). |

---

## 3. Les 15 biomes et les 12 zones de progression

### 3.1 Les 15 biomes de `cambocraft:cambodia`
1. `rizieres_en_terrasses` — Rizières en terrasses bordées de palmiers à sucre et de diguettes de boue.
2. `foret_tropicale` — Forêt dense de tecks, manguiers et jacquiers.
3. `bambouseraie` — Forêt serrée de bambous géants et brume matinale.
4. `savane_claire` — Savane herbeuse sèche parsemée de palmiers et d'affleurements de latérite.
5. `mangrove_et_marais` — Palétuviers sur racines échasses, chenaux vaseux et eaux saumâtres.
6. `riviere_du_tonle` — Grands cours d'eau navigables en pirogue et berges de sable.
7. `grand_lac` — Immense étendue lacustre soumise à la crue de mousson et aux temples engloutis.
8. `jungle_en_ruines` — Jungle ancienne envahie de racines aériennes de banyan et de sanctuaires moussus.
9. `montagnes_karstiques` — Pitons calcaires abrupts, grottes et falaises embrumées.
10. `plateau_de_laterite` — Hautes terres de terre rouge ferrugineuse et gorges rocheuses.
11. `sanctuaire_de_lotus` — Bassins sacrés de lotus roses et blancs à haute résonance spirituelle.
12. `pinedes_seches` — Crêtes fraîches plantées de pins du Cambodge.
13. `plaine_inondable` — Basses terres fertiles recouvertes d'eau pendant la mousson.
14. `foret_de_kapokiers` — Forêt de kapokiers géants à contreforts entourant les vieux temples.
15. `iles_flottantes_des_garudas` — Archipel céleste balayé par les vents au-dessus des nuages.

### 3.2 Les 12 zones et leurs boss

| Zone | Identifiant | Niveaux | Faction liée | Boss de fin de zone |
|---:|---|---|---|---|
| 1 | `village_de_tonle` | 1 – 15 | Tonlé | Chef des bandits de la rivière |
| 2 | `rizieres_de_ly` | 15 – 30 | Ly | Le Buffle d'Or |
| 3 | `bambouseraie_de_srok` | 30 – 50 | Srok | Maître des lames du clan Ombre |
| 4 | `grand_lac` | 50 – 70 | Tonlé | Le Crocodile des Profondeurs |
| 5 | `foret_de_kompong` | 70 – 90 | Kompong | Le Serpent de la forêt |
| 6 | `temples_de_prasat_sovann` | 90 – 110 | Kompong | Gardien de pierre |
| 7 | `forteresse_ombre` | 110 – 130 | Srok | Généralissime du clan Ombre |
| 8 | `montagnes_karstiques` | 130 – 150 | Ly | Le Phénix de latérite |
| 9 | `labyrinthe_des_visages` | 150 – 165 | Kompong | L'Éveil du Visage |
| 10 | `iles_des_garudas` | 165 – 185 | Srok | Garuda de la Tempête |
| 11 | `sanctuaire_du_lotus` | 185 – 200 | Toutes | Le Gardien de l'Équilibre |
| 12 | `royaume_des_esprits` | Post-200 | Renaissance | L'Ombre Première |

---

## 4. Schémas JSON des données rechargeables (`data/cambocraft/cambo/`)

Tous les fichiers JSON sous `data/cambocraft/cambo/<categorie>/<id>.json` sont validés par `tools/validate.py` et rechargés en jeu par `/reload` :

- **`fishing/<id>.json`** : `nom {fr,en}`, `item`, `rarete` (`commun|peu_commun|rare|epique`), `poids`, `zones[]`, `saisons[]`, `nocturne`, `min_cm`, `max_cm`, `difficulte` (`0..1`), `prix`.
- **`powers/<id>.json`** : `nom {fr,en}`, `description {fr,en}`, `rarete` (`commun|rare|epique|legendaire|mythique`), `element` (`eau|feu|air|terre|ombre|lumiere|vie`), `type` (`offensif|defensif|mobilite|soin|controle|utilitaire|passif|invocation`), `niveaux[5]` (`degats`, `soin`, `portee`, `duree_ticks`, `recharge_ticks`, `cout_energie`), `maitrise {seuil, variante_fr, variante_en, bonus}`.
- **`synergies/<id>.json`** : `nom {fr,en}`, `pouvoirs[2]`, `effet`, `multiplicateur`, `duree_ticks`, `description {fr,en}`.
- **`zones/<id>.json`** : `ordre` (`1..12`), `nom {fr,en}`, `niveau_min`, `niveau_max`, `biomes[]`, `boss_requis`, `boss_id`, `teleporteur {x,y,z}`, `ennemis[]`.
- **`enemies/<id>.json`** : `nom {fr,en}`, `categorie` (`zone|ninja|boss`), `rang`, `pv_base`, `attaque_base`, `armure_base`, `vitesse`, `xp`, `pieces`, `capacites[]`, `phases[]`.
- **`npcs/<id>.json`** : `nom {fr,en}`, `role`, `faction`, `personnalite`, `routine[]` (`heure_debut`, `heure_fin`, `activite`, `lieu`), `dialogue_initial`, `marchand_id`.
- **`dialogues/<id>.json`** : `pnj`, `noeud_initial`, `noeuds` (`texte {fr,en}`, `choix[]` avec `condition`, `effets[]`, `suivant`).
- **`merchants/<id>.json`** : `pnj`, `faction`, `offres[]` (`item`, `quantite`, `prix_base`, `palier_reputation_min`, `sensible_au_riz`).
- **`cooking/<id>.json`** : `nom {fr,en}`, `resultat`, `ingredients[]`, `faction`, `palier_reputation_min`, `buff_effet`, `buff_amplificateur`, `buff_duree_ticks`.
- **`quests/<id>.json`** : `chapitre`, `type` (`intro|principale|secondaire|saisonniere|quotidienne|hebdomadaire`), `titre {fr,en}`, `resume {fr,en}`, `conditions_ouverture`, `etapes[]`, `branches[]`, `recompenses {xp, pieces, reputation, objets[], pouvoir}`.
- **`events/<id>.json`** : `nom {fr,en}`, `saison`, `frequence`, `duree_ticks`, `effets[]`, `recompenses[]`.

---

## 5. Mathématiques d'équilibrage et probabilités

1. **Courbe d'expérience (niveaux 1 à 200)** :
   $$\text{XP}(n \to n+1) = \text{round}\left(120 \times n^{1,8}\right)$$
   - Niveau 1 → 2 : 120 XP ; Niveau 50 → 51 : 136 870 XP ; Niveau 100 → 101 : 477 729 XP ; Niveau 199 → 200 : 1 654 210 XP.
2. **Tirage de pouvoirs (Bol de Riz Béni) et Pity** :
   - Taux de base : **Commun 55,0 %** · **Rare 28,0 %** · **Épique 12,0 %** · **Légendaire 4,5 %** · **Mythique 0,5 %**.
   - Seuils de garantie (*hard pity*) :
     - **Épique ou mieux** garanti au **30e** tirage sans Épique+ ;
     - **Légendaire ou mieux** garanti au **80e** tirage sans Légendaire+ ;
     - **Mythique** garanti au **250e** tirage sans Mythique.
   - Vérifié par `tools/sim_gacha.py` sur $\ge 5\,000\,000$ de tirages simulés avec et sans pity.
3. **Économie et inflation du riz** :
   - Paliers de réputation : `inconnu (<0)` · `connu (0..199)` · `respecté (200..599)` · `honoré (600..1499)` · `légendaire (>=1500)`.
   - Multiplicateurs d'achat : `[1.30, 1.10, 1.00, 0.92, 0.80]` ; de vente : `[0.75, 0.90, 1.00, 1.10, 1.25]`.
   - Indice du riz $i \in [0,5 ; 2,0]$ : $\text{inflation}(i) = 1 + 0,4(i - 1) + 0,2\max(0, i - 1)$ (de $-20\,\%$ en récolte abondante à $+60\,\%$ en disette).
4. **Forge d'armes et d'armures (`+1` à `+5`)** :
   - Bonus de statistiques : $+8\,\%$ par palier ($+1 : +8\,\%, +2 : +16\,\%, +3 : +25\,\%, +4 : +36\,\%, +5 : +50\,\%$).
   - Taux de réussite sans perte d'objet : $+1 : 100\,\%, +2 : 85\,\%, +3 : 70\,\%, +4 : 50\,\%, +5 : 35\,\%$ (en cas d'échec, le cristal est consommé mais l'arme conserve son niveau actuel).
