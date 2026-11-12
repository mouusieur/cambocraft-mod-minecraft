# Rapport d'équilibrage et simulations Monte-Carlo — Cambocraft

Ce document consigne les formules mathématiques d'équilibrage et les résultats
empiriques de `tools/sim_gacha.py` sur **5,000,000 tirages sans pity** et **5,000,000 tirages avec pity**.

---

## 1. Simulation du Gacha au Bol de Riz Béni (`5 000 000` de tirages)

| Rareté | Taux de base théorique | Taux observé sans Pity (`N = 5 000 000`) | Taux effectif avec Pity (`30 / 80 / 250`) | Pouvoirs au catalogue |
|---|---:|---:|---:|---:|
| **Commun** | `55,00 %` | `54.975 %` (`2,748,744`) | `54.819 %` (`2,740,965`) | 22 (`>= 20`) |
| **Rare** | `28,00 %` | `28.031 %` (`1,401,552`) | `27.896 %` (`1,394,788`) | 20 (`>= 18`) |
| **Épique** | `12,00 %` | `12.007 %` (`600,361`) | `12.015 %` (`600,743`) | 18 (`>= 18`) |
| **Légendaire** | `4,50 %` | `4.493 %` (`224,652`) | `4.569 %` (`228,437`) | 14 (`>= 14`) |
| **Mythique** | `0,50 %` | `0.494 %` (`24,691`) | `0.701 %` (`35,067`) | 10 (`>= 10`) |

### Garanties de *Hard Pity* vérifiées sur 5 000 000 de tirages
- **Épique ou mieux (seuil 30)** : écart maximal observé = **`30`** tirages (`2,993` garanties déclenchées).
- **Légendaire ou mieux (seuil 80)** : écart maximal observé = **`80`** tirages (`3,568` garanties déclenchées).
- **Mythique (seuil 250)** : écart maximal observé = **`250`** tirages (`9,975` garanties déclenchées).

---

## 2. Courbe d'expérience des niveaux `1` à `200` et Renaissance `1–5`

- Formule par palier : $\text{XP}(n \to n+1) = \text{round}(120 \times n^{1,8})$.
- Niveau `1 → 2` : `120 XP` · Niveau `50 → 51` : `136 870 XP` · Niveau `100 → 101` : `477 729 XP` · Niveau `199 → 200` : `1 654 210 XP`.
- Cumul total du niveau 1 au niveau 200 : **`118 073 398 XP`** (~35 à 45 heures de jeu à travers les 11 zones principales et les 85 quêtes).
- **Renaissance** (rangs `1` à `5`) : accessible au niveau 200, confère `+6 %` de multiplicateur global permanent par étoile (`×1,30` au rang 5) et déverrouille la Zone 12 (*Royaume des Esprits*).

---

## 3. Échelonnement des 12 boss multi-phases

| Zone | Boss | Élément | PV de base | Attaque | Armure | Phases |
|---:|---|---|---:|---:|---:|---:|
| 1 | Chef des bandits de la rivière | `eau` | 420 | 16 | 6 | 3 (`100% / 65% / 30%`) |
| 2 | Le Buffle d'Or | `terre` | 850 | 25 | 10 | 3 (`100% / 65% / 30%`) |
| 3 | Maître des lames du clan Ombre | `ombre` | 1 550 | 38 | 13 | 3 (`100% / 65% / 30%`) |
| 4 | Le Crocodile des Profondeurs | `eau` | 2 600 | 52 | 17 | 3 (`100% / 65% / 30%`) |
| 5 | Le Serpent de la forêt | `vie` | 3 900 | 68 | 20 | 3 (`100% / 65% / 30%`) |
| 6 | Gardien de pierre | `terre` | 5 600 | 86 | 28 | 3 (`100% / 65% / 30%`) |
| 7 | Généralissime du clan Ombre | `ombre` | 7 800 | 108 | 27 | 3 (`100% / 65% / 30%`) |
| 8 | Le Phénix de latérite | `feu` | 10 200 | 132 | 30 | 3 (`100% / 65% / 30%`) |
| 9 | L'Éveil du Visage | `lumiere` | 13 200 | 156 | 36 | 3 (`100% / 65% / 30%`) |
| 10 | Garuda de la Tempête | `air` | 16 800 | 184 | 35 | 3 (`100% / 65% / 30%`) |
| 11 | Le Gardien de l'Équilibre | `vie` | 22 000 | 215 | 42 | 4 (`100% / 65% / 35% / 12%`) |
| 12 | L'Ombre Première | `ombre` | 30 000 | 260 | 48 | 4 (`100% / 65% / 35% / 12%`) |
