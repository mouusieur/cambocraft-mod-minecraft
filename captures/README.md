# Captures de référence

Images produites par un vrai client Minecraft 1.21.1 + Fabric sous Xvfb
(`.github/workflows/captures.yml`, rendu logiciel Mesa/llvmpipe). Elles servent de
référence visuelle pour la documentation et sont **régénérées**, pas retouchées.

| Fichier | Contenu |
| --- | --- |
| `00-ecran-titre.png` | écran titre avec le mod chargé |
| `01-multijoueur.png` | liste des serveurs |
| `02-serveur-selectionne.png` | serveur local sélectionné |
| `sanite.png` | témoin de rendu : HUD, saison et niveau du lac |
| `portail-du-lotus.png` | portail du Lotus (scène `portail`) |
| `cite-khmere.png` | cité khmère (scène `cite`) |
| `atelier.png` | atelier d'artisan (scène `interieur`) |
| `rizieres.png` | rizières et végétation (scène `vegetation`) |

Les captures d'interface (HUD, carnet du Nen, table de craft) et la vue des entités
sont produites par la même passe mais **ne sont pas versionnées** : sous rendu
logiciel, le framebuffer peut revenir noir selon la charge de la machine, et une
image noire publiée vaudrait moins que pas d'image du tout. Le script refuse
d'ailleurs d'écraser une capture valide par une image uniformément noire.

Pour en produire, lancer le workflow `captures` : le journal du client
(`captures-journal.txt`) et les images sont joints au check run
`cambocraft-captures`, et l'artefact `captures-cambocraft` contient tout le dossier.
