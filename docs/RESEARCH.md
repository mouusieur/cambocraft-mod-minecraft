# Rapport de recherche préalable — Cambocraft (Phase 0)

Date : 2026-11-11  
Cible : Minecraft **1.21.1** · Fabric Loader **0.16.14+** · Fabric API **0.116.18+1.21.1** · Mappings **Yarn 1.21.1+build.3** · Java **21**

---

## 1. Étude technique — Fabric 1.21.1, Data Components, Dimensions et Jigsaw

### 1.1 Changements majeurs de Minecraft 1.21.1 (Yarn `1.21.1+build.3`)
L'examen direct des mappings officiels Yarn `1.21.1+build.3` et du dépôt `fabricmc/fabric` (branche `1.21.1`) met en évidence plusieurs évolutions structurantes par rapport à 1.20.x :

1. **Identifiants (`net.minecraft.util.Identifier`)** :
   - Le constructeur `new Identifier(namespace, path)` est supprimé au profit de la fabrique statique `Identifier.of(namespace, path)` et `Identifier.tryParse(raw)`.
2. **Composants de données d'objets (`net.minecraft.component.DataComponentTypes`)** :
   - Le NBT libre sur `ItemStack` (`getOrCreateNbt()`) est remplacé par des composants typés (`DataComponentTypes.CUSTOM_DATA`, `CUSTOM_NAME`, `LORE`, `ATTRIBUTE_MODIFIERS`, `DAMAGE`, `MAX_DAMAGE`).
   - Les métadonnées métier (prise de pêche : espèce, taille en cm, prix, niveau de forge `+1..+5`) passent par `DataComponentTypes.CUSTOM_DATA` via `NbtComponent.of(NbtCompound)` et `stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt()`.
3. **Modificateurs d'attributs (`AttributeModifiersComponent`)** :
   - `EntityAttributeModifier` prend désormais un `Identifier` (et non plus un `UUID`) : `new EntityAttributeModifier(id, valeur, Operation.ADD_VALUE)` associé à un `AttributeModifierSlot` (`MAINHAND`, `ARMOR`, etc.).
4. **Réseau (`PayloadTypeRegistry` & `CustomPayload`)** :
   - Les paquets réseau C2S et S2C utilisent `CustomPayload` typé, enregistré dans `PayloadTypeRegistry.playC2S()` et `PayloadTypeRegistry.playS2C()` avec un `PacketCodec`, puis écouté côté serveur via `ServerPlayNetworking.registerGlobalReceiver(CustomPayload.Id<T>, PlayPayloadHandler<T>)`.
5. **Rechargement de données (`ResourceManagerHelper`)** :
   - Les ressources serveur `data/cambocraft/cambo/<categorie>/*.json` sont lues par `ResourceManager.findAllResources("cambo", predicate)` à travers un `SimpleSynchronousResourceReloadListener` inscrit sur `ResourceManagerHelper.get(ResourceType.SERVER_DATA)`. La première entrée de la liste `List<Resource>` correspond au pack de plus haute priorité, permettant aux datapacks externes de surcharger proprement n'importe quel fichier du mod et à `/reload` de tout rafraîchir à chaud.

### 1.2 Dimensions personnalisées, densité de bruit et structures Jigsaw
L'analyse des schémas JSON vanilla 1.21.1 (`misode/mcmeta`) confirme la structure déclarative complète d'une dimension :
- `data/cambocraft/dimension_type/cambodia.json` : cycle solaire naturel (`fixed_time` absent), `ambient_light: 0.05`, `min_y: -64`, `height: 384`, `logical_height: 384`, `has_skylight: true`, `ultrawarm: false`.
- `data/cambocraft/dimension/cambodia.json` : générateur `minecraft:noise` utilisant `cambocraft:cambodia_noise` et une source de biomes `minecraft:multi_noise` couvrant les **15 biomes** du mod.
- `data/cambocraft/worldgen/noise_settings/cambodia_noise.json` : routeur de densité combinant :
  - des plaines basses inondables autour du Grand Lac (`continentalness` négative à neutre, faible `erosion`) ;
  - des terrasses étagées dans les collines rizicoles ;
  - des pitons abrupts à forte pente (`weirdness` et `ridges` amplifiés) pour les montagnes karstiques et le plateau de latérite.
- **Structures et villages Jigsaw** :
  - Chaque structure est décrite par son descripteur `data/cambocraft/worldgen/structure/<id>.json`, son ensemble d'espacement `worldgen/structure_set/<id>.json` (`spacing`, `separation`, `salt`), sa `template_pool/<id>.json` et ses fichiers NBT binaires compressés GZip sous `data/cambocraft/structure/<categorie>/<variante>.nbt`.

### 1.3 Animation d'entités : modèles hiérarchiques natifs vs GeckoLib
- L'étude de `bernie-g/geckolib` (`4.9.3`, licence MIT) montre que GeckoLib 1.21.1 exige Fabric Loader `>= 0.17` et ajoute une dépendance externe lourde.
- Minecraft 1.21.1 offre nativement `SinglePartEntityModel`, `ModelPart`, `AnimationHelper` et les interpolations d'angles pilotées par des courbes JSON/Java sans dépendance externe.
- **Conclusion retenue** (consignée dans `docs/DECISIONS.md`) : utiliser le système hiérarchique natif piloté par nos descripteurs JSON (`assets/cambocraft/models/entity/*.json` et `animations/*.json`), garantissant zéro dépendance fragile et une compatibilité immédiate avec Fabric Loader 0.16+.

---

## 2. Étude de mods de référence (architecture et bonnes pratiques)

L'analyse en lecture seule de mods open source de référence (notamment `fabricmc/fabric-example-mod` pour la configuration Loom 1.7 split-sources, ainsi que l'organisation des données de mods de dimensions/RPG tels que *The Twilight Forest* et *Botania*) dégage quatre règles d'architecture appliquées dans Cambocraft :

1. **Séparation stricte entre noyau de règles et adaptateurs Minecraft** :
   - Les tables de tirage (pêche, gacha avec et sans pity), les courbes d'XP (`120 × n^1.8`), les formules d'inflation du riz, l'arbre de dialogues, le moteur de quêtes et la résolution des synergies sont écrits en Java pur sans dépendance aux classes serveur, ce qui permet de les tester en quelques millisecondes et de simuler 5 000 000 de tirages de gacha hors jeu.
2. **Source de vérité déclarative** :
   - Les catalogues `assets_src/registry/{blocks,items}.yml` génèrent `CamboBlocks.java`, `CamboItems.java`, les `blockstates`, les modèles, les tables de butin, les tags d'outils et les fichiers de langue `fr_fr.json` / `en_us.json`, éliminant tout risque de désynchronisation entre code et ressources.
3. **Validation automatique en amont** :
   - Un outil unique (`tools/validate.py`) vérifie l'intégralité de la chaîne : JSON, références de modèles, dimensions PNG 16×16, parité des traductions FR/EN et schémas métier.
4. **Autorité serveur systématique** :
   - Aucune requête client ne modifie directement l'XP, la monnaie, la réputation, l'énergie spirituelle ou le Carnet du Nen ; le serveur valide distance, temps de recharge, coût et état avant d'appliquer l'effet et de renvoyer un paquet d'état compact.

---

## 3. Recherche culturelle, géographique et historique sur le Cambodge

### 3.1 Géographie physique et hydrologie saisonnière
- **Le bassin du Tonlé Sap (Grand Lac)** : cœur hydrologique du Cambodge, relié au Mékong par la rivière Tonlé Sap. En saison sèche (novembre à mai), le lac se vide vers le Mékong et découvre de vastes plaines alluviales et vasières fertiles ; en mousson (juin à octobre), la crue du Mékong inverse le cours de la rivière et multiplie par quatre à cinq la surface du lac, inondant forêts galerie, mangroves d'eau douce et sanctuaires bas.
  - *Traduction en jeu* : cycle de 3 saisons (`saison_douce`, `saison_chaude`, `mousson`, 8 jours chacune par défaut), variation de hauteur d'eau du Grand Lac, bloc `mousson_vase` découvert à l'étiage et accès saisonnier aux sanctuaires engloutis.
- **Les reliefs périphériques** :
  - *Monts des Cardamomes et pitons karstiques (Kampot / Battambang)* : reliefs calcaires abrupts percés de grottes, rivières encaissées et brumes matinales.
  - *Plateaux de terres rouges et latérite (Ratanakiri / Mondulkiri / Siem Reap)* : sols ferrugineux rouge brique (`terre_lateritique`, `laterite`), forêts claires et pinèdes sèches d'altitude.

### 3.2 Architecture khmère classique et vernaculaire
- **Matériaux historiques** :
  - *La latérite* (`laterite`, `brique_de_laterite`) : pierre ferrugineuse vacuolaire rouge-brun, utilisée pour les soubassements, enceintes, chaussées et corps de pyramides.
  - *Le grès khmer* (`gres_khmer`, `gres_sculpte`) : grès beige-doré à gris-rose, réservé aux sanctuaires, linteaux ciselés, galeries de bas-reliefs et tours-visages.
  - *La brique de temple ancienne* (`brique_de_temple`) : employée sur les tours pré-angkoriennes avec liant végétal.
- **Vocabulaire architectural** :
  - *Prasat* : tour-sanctuaire couronnée en boutons de lotus étagés.
  - *Temple-montagne à degrés* : pyramide à gradins symbolisant le mont Meru, dotée d'escaliers axiaux gardés par des lions de pierre (`lion_de_pierre`).
  - *Balustrades naga et garudas* : chaussées d'accès bordées du serpent protecteur à capuchon multiple (*naga*, symbole bienveillant des eaux et de la fertilité, jamais assimilé à une menace religieuse) et d'aigles mythiques (*garuda*).
  - *Habitat rural sur pilotis* : maisons en bois de teck, manguier ou bambou tressé, surélevées sur pilotis de 2 à 3 blocs pour résister à la crue de mousson, coiffées de chaume de paille de riz (`toit_de_chaume`) ou de tuiles de terre cuite, avec jarres d'eau de pluie (`jarre`), nattes de jonc (`natte_de_jonc`) et métier à tisser sous la maison.

### 3.3 Agriculture, artisanat, gastronomie et arts vivants
- **Riziculture et économie** : le cycle du riz (*semis → repiquage → croissance → récolte*) rythme l'année paysanne. Les diguettes des rizières sont plantées de palmiers à sucre (*Borassus flabellifer*, `palmier_a_sucre`), dont la sève donne le sucre de palme (`sucre_de_palme`).
- **Sériciculture et Krama** : culture du mûrier (`murier_buisson`, `feuille_de_murier`), élevage du ver à soie (`ver_a_soie`), dévidage des cocons dorés en cuve (`cuve_de_soie`), teinture naturelle (or, indigo, pourpre, émeraude) et tissage au métier (`metier_a_tisser`) du *krama* traditionnel.
- **Gastronomie khmère** : structurée autour des pâtes aromatiques *kroeung* (citronnelle, galanga, curcuma, combava, ail), du *prahok*, du lait de coco et du poivre de Kampot. Plats emblématiques intégrés : *Amok* de poisson, *Nom Banh Chok*, *Lok Lak* au poivre de Kampot, *Bai Sach Chrouk*, *Samlor Korko*, *Samlor Machu*, *Kuy Teav*, *Kralan* (riz gluant cuit dans le bambou), *Num Ansorm* et *Num Kom*.
- **Pradal Serey, courses de pirogues et danse Apsara** :
  - *Pradal Serey* : art martial traditionnel à mains bandées (`gants_de_pradal_serey`), pratiqué en arène au rythme du tambour et du hautbois.
  - *Course de pirogues* : inspirée de la fête des eaux (*Bon Om Touk*) marquant la fin de la mousson et l'inversion du Tonlé Sap.
  - *Danse Apsara* : gestuelle codifiée de grâce et de maîtrise du souffle, traduite en jeu par un défi de rythme et le pouvoir mythique *Danse de l'apsara*.

### 3.4 Respect culturel et garde-fous éditoriaux
Conformément à la charte culturelle du projet (§17) :
- Le maître moine et les apprentis moines sont représentés avec calme, bienveillance et dignité ; aucun symbole spirituel ou architectural (tours-visages, bas-reliefs, autels, encens) n'est détourné à des fins comiques ou caricaturales.
- Le *naga* est systématiquement présenté comme un gardien bienveillant des eaux et des chaussées de temple ; les serpents sauvages de la forêt (`Forêt de Kompong`) sont de simples créatures faunistiques sans connotation culturelle ou religieuse.
- Le **clan Ombre** est une confrérie martiale entièrement fictive, détachée de toute nationalité, ethnie ou groupe historique réel ; ses membres agissent par philosophie de maîtrise martiale et certains cherchent la rédemption.

---

## 4. Sources documentaires consultées
1. UNESCO World Heritage Centre — *Site d'Angkor : architecture hydraulique, grès et latérite, bas-reliefs et tours-visages*.
2. FAO / Mekong River Commission — *Hydrologie du bassin du Tonlé Sap, inversion saisonnière des crues, écosystèmes de forêts inondées et pêcheries d'eau douce du Cambodge*.
3. UNESCO Intangible Cultural Heritage — *Le Ballet royal du Cambodge (danse Apsara)*, *Le Bokator / arts martiaux khmers*, *Tissage traditionnel de la soie et du krama*.
4. Documentation officielle Fabric (`fabricmc/fabric` 1.21.1), Mappings Yarn (`fabricmc/yarn` `1.21.1+build.3`) et schémas de données Minecraft 1.21.1 (`misode/mcmeta`).
