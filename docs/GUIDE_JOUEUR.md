# Guide du joueur — Cambocraft

Bienvenue dans **Cambocraft**, une aventure complète au cœur de la dimension **`cambocraft:cambodia`**, inspirée des paysages, de l'architecture et des traditions vivantes du Cambodge.

---

## 1. Ouvrir le Portail du Lotus (100 % en survie)

1. Façonnez **8 blocs de `cadre_du_portail`** (ou `pierre_du_lotus_sculptee`) et **1 `socle_d_offrande`**.
2. Disposez l'anneau horizontal **3×3** au sol en plaçant le **`socle_d_offrande`** au centre.
3. Préparez un **`bol_de_riz_beni`** et faites un **clic droit** sur le socle central : l'anneau s'illumine en **`coeur_du_portail`**.
4. Traversez le cœur lumineux pour arriver sur le parvis sécurisé du **Village de Tonlé** (`0, 72, 0`), où un portail de retour permanent vous permet de regagner l'Overworld à tout moment.

---

## 2. Les 3 saisons, le Grand Lac et la riziculture

Le monde suit un cycle déterministe de **3 saisons de 8 jours de jeu** chacune (configurable dans `config/cambocraft.json`) :

| Saison | Niveau du Grand Lac | Effets sur le monde et l'économie |
|---|---|---|
| **Saison douce** (*Khe Roneang*) | `Y = 62` (étiage) | Brume matinale dorée ; **les entrées des temples engloutis du Grand Lac (`Y = 63..66`) émergent** et deviennent accessibles à pied sec ; fête des Eaux. |
| **Saison chaude** (*Khe Kadeuk*) | `Y = 64` | Floraison sacrée des bassins de lotus (+régénération spirituelle du Nen). |
| **Mousson** (*Khe Vassa*) | `Y = 68` (crue `+6` blocs) | Averses chaudes et orages tropicaux ; marche à pied ralentie (`×0,90`) sauf sur un **Buffle d'eau** ; croissance accélérée des rizières. |

- **Cycle du riz à 4 stades** : *Semis → Repiquage → Croissance → Récolte*. Une récolte abondante abaisse l'indice du riz jusqu'à `-20 %` chez tous les marchands, tandis qu'une pénurie renchérit les denrées jusqu'à `+60 %`.

---

## 3. Les 4 factions, la réputation et l'artisanat khmer

Vous développez votre réputation auprès de **4 factions régionales** (**Tonlé**, **Ly**, **Srok**, **Kompong**) à travers **5 paliers** :
- `Inconnu (< 0)` : prix d'achat `×1,30`, prix de vente `×0,75`
- `Connu (0..199)` : prix d'achat `×1,10`, prix de vente `×0,90`
- `Respecté (200..599)` : prix d'achat `×1,00`, prix de vente `×1,00` (débloque les recettes régionales)
- `Honoré (600..1499)` : prix d'achat `×0,92`, prix de vente `×1,10` (débloque les plats royaux et la soie d'or)
- `Légendaire (>= 1500)` : prix d'achat `×0,80`, prix de vente `×1,25`

### Chaînes d'artisanat
- **Cuisine khmère (16 plats)** : préparez l'*Amok de poisson*, le *Lok Lak au poivre de Kampot*, le *Nom Banh Chok* ou le *Kralan* en bambou sur le foyer de cuisine pour obtenir de puissants buffs.
- **Sériciculture et tissage** : élevez les vers à soie sur les feuilles de mûrier, dévidez les cocons au rouet, teignez les fils (indigo, curcuma, garance, lotus) et tissez sur le métier à tisser.
- **Sculpture sur grès et latérite** : utilisez le maillet et le ciseau sur l'`atelier_de_sculpture` pour tailler bas-reliefs, balustrades Naga et lions sacrés.
- **Forge non destructive (`+1` à `+5`)** : améliorez vos armes et armures khmères sur la `table_de_forge` (`+8 %` à `+50 %` de statistiques). En cas d'échec, votre arme ne se brise jamais et conserve son niveau actuel.

---

## 4. Les 84 pouvoirs du Nen, le Bol de Riz Béni et le Carnet du Nen

- **Dégustation auprès d'un Apprenti Moine** : offrez un `bol_de_riz_beni` pour éveiller un pouvoir parmi **84 pouvoirs** répartis en **5 raretés** (*Commun 55 %*, *Rare 28 %*, *Épique 12 %*, *Légendaire 4,5 %*, *Mythique 0,5 %*) et **7 éléments** (*Eau*, *Feu*, *Air*, *Terre*, *Ombre*, *Lumière*, *Vie*).
- **Garanties de *Pity*** : un pouvoir **Épique+** est garanti au **30e** tirage, **Légendaire+** au **80e** tirage et **Mythique** au **250e** tirage.
- **Carnet du Nen** : conservez tous vos pouvoirs sans limite, équipez-en **3 simultanément** et combinez-les pour activer **34 synergies** élémentaires et débloquer leur **Maîtrise**.

---

## 5. Les 12 zones (niveaux 1 à 200), la Renaissance et les 3 fins

1. **Zones 1 à 11 (niveaux 1 à 200)** : du *Village de Tonlé* au *Sanctuaire du Lotus*, chaque zone est gardée par un **boss à 3 ou 4 phases** et reliée au réseau des **Pierres de Téléportation du Lotus**.
2. **Chapitre 12 — *La Leçon du Bol*** : au terme de la quête principale, choisissez librement entre les **3 fins** (*Voie de la Lumière*, *Voie de l'Ombre*, *Voie de l'Équilibre*).
3. **Renaissance (rangs 1 à 5) & Zone 12 (*Royaume des Esprits*)** : au niveau 200, accomplissez la Renaissance à la Pyramide sacrée (`+6 %` de statistiques permanentes par étoile dorée) et affrontez **L'Ombre Première**.

---

## 6. Compagnon Neak Ta, montures et traditions culturelles

- **Esprit protecteur Neak Ta (3 stades)** : présentez des offrandes (*bol de riz béni*, *fleur de lotus*, *encens sacré*) pour le faire évoluer de *Lueur du Sanctuaire* à *Avatar Ancestral du Lotus* (sauvetage quotidien contre un coup fatal).
- **Montures** : apprivoisez le **Buffle d'eau** (agile dans la boue de rizière, sacoches à 12 emplacements) et naviguez en **Pirogue khmère**.
- **Activités et événements** : mesurez-vous aux **3 paliers de l'arène de Pradal Serey** au rythme de l'orchestre *Sarama*, participez aux **3 parcours de courses de pirogues**, accomplissez la **Danse Apsara**, visitez le **Marché de nuit aux lanternes** et défendez les villages lors de la **Nuit des Ombres** (au 6e jour de chaque saison).
