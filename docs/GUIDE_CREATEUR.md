# Guide du créateur de datapacks — Cambocraft

Toutes les données de gameplay de Cambocraft sont pilotées par des fichiers JSON situés sous `data/cambocraft/cambo/<categorie>/<id>.json` et peuvent être surchargées ou étendues par n'importe quel datapack Minecraft 1.21.1, puis rechargées à chaud en jeu via la commande **`/reload`**.

---

## 1. Arborescence des catégories rechargeables (`data/cambocraft/cambo/`)

| Dossier | Nombre livré | Registre Java | Rôle |
|---|---:|---|---|
| `fishing/*.json` | 16 | `FishingRegistry` | Espèces de poissons, saisons, tailles et difficulté du mini-jeu |
| `zones/*.json` | 12 | `ZoneRegistry` | Paliers de niveaux (1–200 + post-200), boss requis et coordonnées des téléporteurs |
| `npcs/*.json` | 16 | `NpcRegistry` | Profils de PNJ, faction, routine horaire à 4 créneaux et dialogue initial |
| `dialogues/*.json` | 16 | `NpcRegistry` | Arbres de dialogues bilingues (`fr`/`en`) à embranchements et conditions |
| `merchants/*.json` | 8 | `NpcRegistry` | Catalogues marchands en Pièces du Lotus, paliers de réputation et sensibilité au riz |
| `cooking/*.json` | 16 | `CookingRegistry` | Recettes de cuisine khmère, ingrédients et buffs temporaires |
| `quests/*.json` | 85 | `QuestRegistry` | Quête d'intro, 12 chapitres principaux, 64 quêtes secondaires et 8 répétables |
| `powers/*.json` | 84 | `PowerRegistry` | Pouvoirs du Nen (5 raretés, 7 éléments, 8 types, 5 niveaux et Maîtrise) |
| `synergies/*.json` | 34 | `PowerRegistry` | Synergies actives entre paires de pouvoirs équipés dans le Carnet du Nen |
| `enemies/*.json` | 56 | `CombatRegistry` | 44 ennemis de zone/ninjas de l'Ombre et 12 boss multi-phases (3 à 4 phases) |
| `events/*.json` | 4 | `ShadowNightManager` | Événements saisonniers dynamiques (dont la *Nuit des Ombres*) |

---

## 2. Exemples de schémas JSON

### 2.1 Ajouter un pouvoir du Nen (`data/cambocraft/cambo/powers/mon_pouvoir.json`)
```json
{
  "nom": { "fr": "Souffle du Lotus Blanc", "en": "White Lotus Breath" },
  "description": {
    "fr": "Libère une onde purificatrice de lumière et de vie.",
    "en": "Unleashes a purifying wave of light and life."
  },
  "rarete": "epique",
  "element": "lumiere",
  "type": "soin",
  "niveaux": [
    { "niveau": 1, "degats": 8.0, "soin": 14.0, "portee": 7.0, "duree_ticks": 100, "recharge_ticks": 60, "cout_energie": 26 },
    { "niveau": 2, "degats": 10.0, "soin": 17.0, "portee": 7.5, "duree_ticks": 115, "recharge_ticks": 57, "cout_energie": 26 },
    { "niveau": 3, "degats": 12.0, "soin": 20.0, "portee": 8.0, "duree_ticks": 130, "recharge_ticks": 54, "cout_energie": 26 },
    { "niveau": 4, "degats": 14.5, "soin": 23.5, "portee": 8.5, "duree_ticks": 145, "recharge_ticks": 51, "cout_energie": 26 },
    { "niveau": 5, "degats": 17.5, "soin": 28.0, "portee": 9.5, "duree_ticks": 160, "recharge_ticks": 48, "cout_energie": 26 }
  ],
  "maitrise": {
    "seuil": 130,
    "variante_fr": "Souffle du Lotus Blanc — Éveil du Maître",
    "variante_en": "White Lotus Breath — Master Awakening",
    "bonus": 0.26
  }
}
```

### 2.2 Ajouter une quête secondaire (`data/cambocraft/cambo/quests/ma_quete.json`)
```json
{
  "chapitre": 0,
  "type": "secondaire",
  "faction": "tonle",
  "zone": "village_de_tonle",
  "titre": { "fr": "Les Lanternes du Soir", "en": "Evening Lanterns" },
  "resume": {
    "fr": "Aider le conteur à allumer les lanternes flottantes sur la berge.",
    "en": "Help the storyteller light the floating lanterns along the riverbank."
  },
  "conditions_ouverture": {
    "niveau_min": 5,
    "quetes_requises": ["intro_offrande_du_bol"],
    "faction": "tonle",
    "reputation_min": 0
  },
  "etapes": [
    {
      "id": "parler_conteur",
      "type": "parler_pnj",
      "cible": "conteur",
      "quantite": 1,
      "description": { "fr": "Parler au conteur du village", "en": "Speak with the village storyteller" }
    }
  ],
  "branches": [],
  "recompenses": {
    "xp": 450,
    "pieces": 35,
    "faction": "tonle",
    "reputation": 50,
    "objets": ["cambocraft:bol_de_riz_beni"],
    "pouvoir": ""
  }
}
```

---

## 3. Tolérance aux erreurs et validation

- **En jeu (`/reload`)** : si un fichier JSON d'un datapack contient une erreur de syntaxe ou une valeur hors bornes, `DataManager` et `JsonUtil` journalisent un avertissement précis (`Log.warn`) et ignorent uniquement l'entrée fautive sans jamais faire planter le serveur ni interrompre le chargement des autres fichiers.
- **Hors jeu** : exécutez `python3 tools/validate.py` pour contrôler en moins de 2 secondes l'intégralité des schémas JSON, des structures `.nbt`, des textures `.png`, des sons `.ogg` et des clés de traduction `fr_fr.json` / `en_us.json`.
