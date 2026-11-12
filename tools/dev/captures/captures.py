#!/usr/bin/env python3
"""Capture de veritables ecrans du mod avec un vrai client Minecraft.

Architecture :
  1. un serveur dedie Fabric 1.21.1 charge Cambocraft (il genere la dimension) ;
  2. le client Minecraft demarre sous Xvfb et se connecte directement au serveur
     (--quickPlayMultiplayer), sans passer par les menus ;
  3. les scenes sont construites par commandes envoyees a la console du serveur ;
  4. les captures sont declenchees par la vraie touche F2 (xdotool), et recuperees
     dans le dossier screenshots du client.

Usage :
  python3 captures.py --client <dir> --serveur <dir> --sortie <dir>
                      [--scenes portail,cite,interieur,vegetation]
"""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import threading
import time

PORT = 25565
MOTIF_PRET_SERVEUR = re.compile(r'Done \([0-9.]+s\)!')
MOTIF_CAPTURE = re.compile(r'Saved screenshot as (\S+)')
MOTIF_CONNEXION = re.compile(r'joined the game')


# ---------------------------------------------------------------------------
# Scenes
# ---------------------------------------------------------------------------


def bloc(x, y, z, nom):
    # On passe par /fill (1x1x1) plutot que /setblock : fill est plus tolerant et
    # s'execute de facon fiable dans une dimension personnalisee via /execute.
    return f"fill {x} {y} {z} {x} {y} {z} cambocraft:{nom}"


def cube(x0, y0, z0, x1, y1, z1, nom):
    return [f"fill {x0} {y0} {z0} {x1} {y1} {z1} cambocraft:{nom}"]


def scene_portail():
    c = []
    c += cube(-9, 59, -9, 9, 59, 9, "pierre_du_lotus_polie")
    for cote in (
        f"fill -10 59 -10 10 59 -10 cambocraft:pierre_du_lotus_sculptee",
        f"fill -10 59 10 10 59 10 cambocraft:pierre_du_lotus_sculptee",
        f"fill -10 59 -10 -10 59 10 cambocraft:pierre_du_lotus_sculptee",
        f"fill 10 59 -10 10 59 10 cambocraft:pierre_du_lotus_sculptee",
    ):
        c += [cote]
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1):
            c += [bloc(dx, 60, dz, "cadre_du_portail")]
    c += [bloc(0, 60, 0, "coeur_du_portail")]
    for dx, dz in ((-3, -3), (3, -3), (-3, 3), (3, 3)):
        c += [f"fill {dx} 60 {dz} {dx} 63 {dz} cambocraft:pilier_de_lotus"]
        c += [bloc(dx, 64, dz, "cristal_de_lotus")]
    for dx, dz in ((0, -4), (0, 4), (-4, 0), (4, 0), (-5, -5), (5, 5), (-5, 5), (5, -5)):
        c += [bloc(dx, 60, dz, "lotus_rose" if (dx + dz) % 2 == 0 else "lotus_blanc")]
    for dx in range(-7, 8, 2):
        c += [bloc(dx, 60, 7, "fleur_d_hibiscus")]
    c += [bloc(0, 64, -6, "lanterne_de_bambou"), bloc(0, 64, 6, "lanterne_de_bambou")]
    c += [bloc(-6, 60, -6, "teleporteur_de_lotus"), bloc(6, 60, 6, "autel")]
    return c


def scene_cite():
    c = []
    c += cube(-14, 59, -14, 14, 59, 14, "pierre_du_lotus_polie")
    c += cube(-15, 59, -15, 15, 59, -15, "gres_khmer_poli")
    c += cube(-15, 59, 15, 15, 59, 15, "gres_khmer_poli")
    c += cube(-15, 59, -15, -15, 59, 15, "gres_khmer_poli")
    c += cube(15, 59, -15, 15, 59, 15, "gres_khmer_poli")
    for etage in range(7):
        d = 8 - etage
        c += cube(-d, 60 + etage * 2, -d, d, 60 + etage * 2, d, "brique_de_temple")
        c += cube(-d, 61 + etage * 2, -d, d, 61 + etage * 2, d, "brique_de_temple")
    c += [bloc(0, 74, 0, "cristal_de_lotus"), bloc(0, 75, 0, "coeur_du_portail")]
    for dx, dz in ((-11, -11), (11, -11), (-11, 11), (11, 11)):
        c += [f"fill {dx} 60 {dz} {dx} 66 {dz} cambocraft:pilier_de_lotus"]
        c += [bloc(dx, 67, dz, "encens")]
    c += [f"fill -9 66 9 -3 66 13 cambocraft:toit_de_chaume",
          f"fill -9 65 9 -9 65 13 cambocraft:toit_de_chaume",
          f"fill -3 65 9 -3 65 13 cambocraft:toit_de_chaume"]
    c += [bloc(-7, 60, 11, "etal_de_marche"), bloc(-5, 60, 11, "jarre"),
          bloc(-6, 60, 12, "panier"), bloc(-4, 60, 10, "marmite"),
          bloc(4, 60, -9, "tambour"), bloc(6, 60, -9, "gong"),
          bloc(-4, 60, -9, "lion_de_pierre"), bloc(4, 60, -11, "visage_de_pierre"),
          bloc(0, 60, 12, "statue_de_pierre"), bloc(0, 60, -12, "bas_relief")]
    for i, b in enumerate(("banniere_rouge", "banniere_or", "banniere_bleue")):
        c += [bloc(-9 + i * 6, 63, 14, b)]
    c += [bloc(-2, 64, 12, "tissu_suspendu"), bloc(2, 64, 12, "tissu_suspendu")]
    c += [f"fill -3 60 3 3 60 5 cambocraft:tapis_de_soie_krama",
          f"fill -3 60 -5 3 60 -3 cambocraft:natte_de_jonc"]
    return c


def scene_interieur():
    c = []
    c += cube(-8, 59, -8, 8, 59, 8, "teck_planks")
    c += [f"fill -8 60 -8 8 64 -8 cambocraft:brique_de_temple",
          f"fill -8 60 8 8 64 8 cambocraft:brique_de_temple",
          f"fill -8 60 -8 -8 64 8 cambocraft:brique_de_temple",
          f"fill 8 60 -8 8 64 8 cambocraft:brique_de_temple",
          f"fill -8 65 -8 8 65 8 cambocraft:teck_planks"]
    for x in (-3, 3):
        c += [f"fill {x} 61 -8 {x} 63 -8 cambocraft:vitrail_de_temple"]
        c += [f"fill {x} 61 8 {x} 63 8 cambocraft:vitrail_de_temple"]
    for z in (-3, 3):
        c += [f"fill 8 61 {z} 8 63 {z} cambocraft:vitrail_de_temple"]
    c += [bloc(-5, 60, -5, "metier_a_tisser"), bloc(-5, 61, -5, "tissu_suspendu"),
          bloc(-3, 60, -5, "cuve_de_soie"), bloc(-1, 60, -5, "tour_de_poterie"),
          bloc(1, 60, -5, "table_de_forge"), bloc(3, 60, -5, "four_de_forge"),
          bloc(5, 60, -5, "creuset_de_fusion"), bloc(3, 60, -3, "atelier_de_sculpture"),
          bloc(-5, 60, 5, "jarre"), bloc(-4, 60, 5, "panier"),
          bloc(5, 60, 5, "marmite"), bloc(5, 60, 3, "encens"),
          bloc(0, 60, 6, "tapis_de_soie_krama"), bloc(2, 60, 6, "natte_de_jonc")]
    for x in (-6, 0, 6):
        c += [bloc(x, 63, 7, "lanterne_de_bambou")]
    return c


def scene_vegetation():
    c = []
    for i, niveau in enumerate((59, 61, 63)):
        z0 = i * 6 - 6
        c += [f"fill -12 {niveau} {z0} 12 {niveau} {z0 + 4} cambocraft:boue_de_riziaire"]
        c += [f"fill -12 {niveau + 1} {z0} 12 {niveau + 1} {z0} cambocraft:riz_sauvage"]
        c += [f"fill -12 {niveau + 1} {z0 + 4} 12 {niveau + 1} {z0 + 4} cambocraft:riz_empile"]
        c += [f"fill -12 {niveau} {z0 + 5} 12 {niveau} {z0 + 5} water"]
    c += [bloc(-6, 65, 8, "lotus_rose"), bloc(-4, 65, 8, "lotus_blanc"),
          bloc(2, 65, 8, "fleur_de_lotus"), bloc(4, 65, 8, "feuille_de_lotus")]
    for x in range(-12, 13, 3):
        c += [bloc(x, 65, 12, "paille_de_riz" if x % 2 == 0 else "orchidee_khmer")]
    c += [bloc(-10, 65, -8, "paletuvier_sapling"), bloc(10, 65, -8, "palmier_a_sucre_sapling")]
    c += [f"fill -12 64 -9 12 64 -9 cambocraft:laterite"]
    return c


SCENES = {
    # nom : (constructeur, position du joueur, rotation (yaw, pitch), nom de fichier)
    "portail": (scene_portail, (0.5, 63.5, 15.0), (0.0, 18.0), "portail-du-lotus"),
    "cite": (scene_cite, (0.5, 67.0, 26.0), (0.0, 16.0), "cite-khmere"),
    "interieur": (scene_interieur, (1.5, 62.5, 6.5), (14.0, 12.0), "atelier"),
    "vegetation": (scene_vegetation, (0.5, 73.0, 22.0), (0.0, 30.0), "rizieres"),
}


# ---------------------------------------------------------------------------


class Flux:
    """Lit la sortie d'un processus ligne par ligne, dans un fil."""

    def __init__(self, proc, prefixe):
        self.proc = proc
        self.prefixe = prefixe
        self.lignes = []
        self.verrou = threading.Lock()
        self.fil = threading.Thread(target=self._lire, daemon=True)
        self.fil.start()

    def _lire(self):
        tampon = b""
        while True:
            bloc = self.proc.stdout.read(4096)
            if not bloc:
                return
            tampon += bloc
            while b"\n" in tampon:
                ligne, tampon = tampon.split(b"\n", 1)
                texte = ligne.decode("utf-8", "replace").rstrip()
                with self.verrou:
                    self.lignes.append(texte)
                    if len(self.lignes) > 4000:
                        del self.lignes[:2000]

    def attendre(self, motif, delai, description=""):
        fin = time.time() + delai
        vues = 0
        while time.time() < fin:
            with self.verrou:
                nouvelles = self.lignes[vues:]
                for ligne in nouvelles:
                    if motif.search(ligne):
                        return ligne
                vues = len(self.lignes)
            time.sleep(0.30)
        print(f"   [delai depasse] {description}")
        return None


def demarrer_serveur(repertoire):
    """Demarrage du serveur dedie avec une entree standard exploitable."""
    cmd = ["java", "-Xmx2G", "-jar", "fabric-server-launch.jar", "nogui"]
    return subprocess.Popen(
        cmd, cwd=repertoire, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        stdin=subprocess.PIPE, bufsize=0,
    )


def demarrer_client(repertoire, largeur, hauteur, adresse):
    infos = json.load(open(os.path.join(repertoire, "infos.json"), encoding="utf-8"))
    classpath = open(os.path.join(repertoire, "classpath.txt"), encoding="utf-8").read().strip()
    cmd = [
        "java", "-Xmx2G",
        "-Djava.library.path=" + infos["natives"],
        "-Dorg.lwjgl.librarypath=" + infos["natives"],
        "-Dfile.encoding=UTF-8",
        "-cp", classpath, infos["main_class"],
        "--username", "Cambocraft",
        "--uuid", "00000000000040008000000000000001",
        "--accessToken", "0",
        "--version", infos["version"],
        "--gameDir", repertoire,
        "--assetsDir", os.path.join(repertoire, "assets"),
        "--assetIndex", infos["asset_index"],
        "--width", str(largeur), "--height", str(hauteur),
        "--userType", "legacy",
        "--quickPlayMultiplayer", adresse,
    ]
    return subprocess.Popen(
        cmd, cwd=repertoire, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        stdin=subprocess.DEVNULL, bufsize=0,
    )


def ecrire_servers_dat(repertoire):
    """Ecrit servers.dat (NBT non compresse) avec le serveur local deja enregistre.

    Le client affiche alors ce serveur dans sa liste et la touche Entree suffit a
    le rejoindre : aucune saisie d'adresse n'est necessaire.
    """
    import struct

    def texte(valeur):
        donnees = valeur.encode("utf-8")
        return struct.pack(">H", len(donnees)) + donnees

    def chaine(nom, valeur):
        return b"\x08" + texte(nom) + texte(valeur)

    def octet(nom, valeur):
        return b"\x01" + texte(nom) + struct.pack(">b", valeur)

    serveur = (
        chaine("name", "Cambocraft")
        + chaine("ip", f"127.0.0.1:{PORT}")
        + octet("hidden", 0)
        + octet("acceptTextures", 1)
        + b"\x00"
    )
    racine = (
        b"\x0a" + texte("")
        + b"\x09" + texte("servers") + b"\x0a" + struct.pack(">i", 1) + serveur
        + b"\x00"
    )
    with open(os.path.join(repertoire, "servers.dat"), "wb") as fichier:
        fichier.write(racine)


def capture_ecran(destination):
    """Capture l'ecran X courant (ce que le joueur voit reellement)."""
    resultat = subprocess.run(
        ["import", "-window", "root", destination],
        capture_output=True, timeout=60,
    )
    if resultat.returncode != 0:
        print("   echec de la capture X :", resultat.stderr.decode("utf-8", "replace")[:200])
        return False
    return os.path.exists(destination) and os.path.getsize(destination) > 0


def cliquer(x, y, double=False):
    """Clique a des coordonnees physiques de la fenetre X."""
    subprocess.run(["xdotool", "mousemove", str(x), str(y)], capture_output=True, timeout=30)
    time.sleep(0.3)
    subprocess.run(["xdotool", "click", "--repeat", "2" if double else "1", "--delay", "120", "1"],
                   capture_output=True, timeout=30)


def appuyer(*touches):
    for combinaison in touches:
        subprocess.run(["xdotool", "key", "--clearmodifiers", combinaison],
                       capture_output=True, timeout=30)


def ecrire_options(repertoire):
    """Options ecrites avant le lancement : HUD masque, FOV large, qualite maximale."""
    options = {
        "version": "3955",
        "guiScale": "2",
        "fov": "0.5",
        "renderDistance": "10",
        "graphicsMode": "1",
        "ao": "2",
        "particles": "0",
        "clouds": "true",
        "bobView": "false",
        "hideGui": "true",
        "gamma": "1.0",
        "soundCategory_master": "0.0",
        "narrator": "0",
        "pauseOnLostFocus": "false",
        "fullscreen": "false",
    }
    with open(os.path.join(repertoire, "options.txt"), "w", encoding="utf-8") as fichier:
        for cle, valeur in options.items():
            fichier.write(f"{cle}:{valeur}\n")


def touche_f2():
    """Envoie la vraie touche F2 (capture d'ecran) au client sous Xvfb."""
    subprocess.run(["xdotool", "key", "--clearmodifiers", "F2"], check=False,
                   env=os.environ.copy(), timeout=30)


def main() -> int:
    analyseur = argparse.ArgumentParser()
    analyseur.add_argument("--client", required=True)
    analyseur.add_argument("--serveur", required=True)
    analyseur.add_argument("--sortie", required=True)
    analyseur.add_argument("--scenes", default="portail")
    analyseur.add_argument("--largeur", type=int, default=1280)
    analyseur.add_argument("--hauteur", type=int, default=720)
    args = analyseur.parse_args()
    os.makedirs(args.sortie, exist_ok=True)
    ecrire_options(args.client)
    ecrire_servers_dat(args.client)

    print("== demarrage du serveur dedie ==")
    serveur = demarrer_serveur(args.serveur)
    sortie_serveur = Flux(serveur, "serveur")
    if not sortie_serveur.attendre(MOTIF_PRET_SERVEUR, 420, "serveur pret"):
        print("!! Le serveur n'a pas demarre.")
        for ligne in sortie_serveur.lignes[-60:]:
            print("   " + ligne)
        serveur.kill()
        return 1
    print("== serveur pret ==")

    def commande(texte):
        serveur.stdin.write((texte + "\n").encode("utf-8"))
        serveur.stdin.flush()

    commande("gamerule doDaylightCycle false")
    commande("gamerule doWeatherCycle false")
    commande("gamerule doMobSpawning false")
    commande("gamerule sendCommandFeedback true")
    commande("time set 6000")
    commande("weather clear")
    commande("difficulty peaceful")
    time.sleep(1)

    print("== demarrage du client (connexion automatique) ==")
    client = demarrer_client(args.client, args.largeur, args.hauteur, f"127.0.0.1:{PORT}")
    sortie_client = Flux(client, "client")

    connecte = sortie_serveur.attendre(MOTIF_CONNEXION, 120, "arrivee du joueur (quickPlay)")
    if not connecte:
        # Secours par l'interface : on capture chaque etape pour diagnostic.
        # Ecran titre 1280x720, guiScale 2 -> zone logique 640x360.
        print("   quickPlay sans effet : connexion par l'interface")
        appuyer("Escape", "Escape")
        time.sleep(3)
        capture_ecran(os.path.join(args.sortie, "00-ecran-titre.png"))
        # Bouton « Multijoueur » : x = 320 logique, y = 90 + 48 + 24 = 162 logique.
        cliquer(args.largeur // 2, int(args.hauteur * 162 / 360))
        time.sleep(4)
        capture_ecran(os.path.join(args.sortie, "01-multijoueur.png"))
        # Premiere entree de la liste de serveurs, puis bouton « Rejoindre ».
        cliquer(args.largeur // 2, int(args.hauteur * 60 / 360))
        time.sleep(1)
        capture_ecran(os.path.join(args.sortie, "02-serveur-selectionne.png"))
        cliquer(int(args.largeur * 241 / 640), int(args.hauteur * 342 / 360))
        connecte = bool(sortie_serveur.attendre(MOTIF_CONNEXION, 120, "joueur (interface)"))
    if not connecte:
        print("!! Le joueur ne s'est jamais connecte. Capture de l'ecran courant :")
        os.makedirs(args.sortie, exist_ok=True)
        capture_ecran(os.path.join(args.sortie, "00-ecran-titre.png"))
        with open(os.path.join(args.sortie, "journal-client.txt"), "w", encoding="utf-8") as f:
            f.write("\n".join(sortie_client.lignes))
        with open(os.path.join(args.sortie, "journal-serveur.txt"), "w", encoding="utf-8") as f:
            f.write("\n".join(sortie_serveur.lignes))
        client.kill()
        serveur.kill()
        return 1
    print("== joueur connecte ==")
    # Le client affiche « Loading terrain... » tant que le monde n'est pas pret :
    # capturer a ce moment-la donne un ecran flou ou noir.
    time.sleep(25)

    print("== joueur connecte ==")
    time.sleep(10)

    commande("gamemode creative @a")
    commande("execute in cambocraft:cambodia run forceload add -64 -64 64 64")
    time.sleep(4)
    # Vol stationnaire : sans cela, le joueur tombe dans le vide pendant la construction
    # et la capture ne montre que du noir.
    commande("execute in cambocraft:cambodia run tp @a 0 100 0 0 0")

    # --- verification de sanite : un bloc d'or de 3x3, camera dessus, regard vers le bas ---
    print("== verification de sanite du rendu ==")
    avant = len(sortie_serveur.lignes)
    commande("execute in cambocraft:cambodia run fill -2 70 -2 2 70 2 minecraft:gold_block")
    commande("execute in cambocraft:cambodia run tp @a 0 76 0 0 85")
    commande("execute in cambocraft:cambodia run data get entity Cambocraft Pos")
    time.sleep(5)
    with sortie_serveur.verrou:
        for ligne in sortie_serveur.lignes[avant:]:
            print("   SERVEUR> " + ligne)
    time.sleep(10)
    for _ in range(20):
        # On attend que le client ait fini de charger (plus de "Loading terrain").
        with sortie_client.verrou:
            derniers = " ".join(sortie_client.lignes[-5:])
        if "Loading terrain" not in derniers:
            break
        time.sleep(3)
    if capture_ecran(os.path.join(args.sortie, "sanite.png")):
        print("   capture : sanite.png")
    commande("execute in cambocraft:cambodia run data get entity Cambocraft Pos")
    time.sleep(2)

    captures = []
    for nom in [s.strip() for s in args.scenes.split(",") if s.strip()]:
        if nom not in SCENES:
            print(f"   scene inconnue : {nom}")
            continue
        constructeur, position, rotation, fichier = SCENES[nom]
        print(f"== scene {nom} ({fichier}) ==")
        # Volume degage : le terrain naturel de cambodia monte au-dessus de la camera,
        # sans cela le joueur se retrouve dans la roche et la capture est noire.
        # /fill est limite a 32 768 blocs par commande : on decoupe en tranches de 8.
        for y0 in range(56, 140, 6):
            commande(f"execute in cambocraft:cambodia run fill -32 {y0} -32 32 {y0 + 5} 32 air")
        time.sleep(3)
        for cmd in constructeur():
            commande(f"execute in cambocraft:cambodia run {cmd}")
        time.sleep(3)
        x, y, z = position
        # Filet de securite : une plateforme sous la camera, pour ne jamais tomber.
        commande(f"execute in cambocraft:cambodia run fill {int(x)-3} {int(y)-3} {int(z)-3} "
                 f"{int(x)+3} {int(y)-3} {int(z)+3} cambocraft:pierre_du_lotus_polie")
        commande(f"execute in cambocraft:cambodia run tp @a {x} {y} {z} {rotation[0]} {rotation[1]}")
        # Vol en creatif maintenu : double saut simule par l'API de vol.
        commande(f"execute in cambocraft:cambodia run tp @a {x} {y} {z} {rotation[0]} {rotation[1]}")
        time.sleep(6)

        # Diagnostic : position et dimension reelles du joueur.
        avant_diag = len(sortie_serveur.lignes)
        commande("execute in cambocraft:cambodia run data get entity Cambocraft Pos")
        sortie_serveur.attendre(re.compile(r"has the following entity data"), 20, "position")
        with sortie_serveur.verrou:
            for ligne in sortie_serveur.lignes[avant_diag:]:
                if "entity data" in ligne or "Pos" in ligne:
                    print("   " + ligne)
        time.sleep(2)
        # On laisse au client le temps de recevoir et de mailler les chunks.
        time.sleep(8)
        cible = os.path.join(args.sortie, fichier + ".png")
        if capture_ecran(cible):
            captures.append(cible)
            print(f"   capture : {cible}")

    print("== arret ==")
    commande("stop")
    time.sleep(6)
    if serveur.poll() is None:
        serveur.kill()
    if client.poll() is None:
        client.kill()

    with open(os.path.join(args.sortie, "journal-serveur.txt"), "w", encoding="utf-8") as fichier:
        fichier.write("\n".join(sortie_serveur.lignes))
    with open(os.path.join(args.sortie, "journal-client.txt"), "w", encoding="utf-8") as fichier:
        fichier.write("\n".join(sortie_client.lignes))

    print("== fin ==")
    for chemin in captures:
        print("   " + chemin)
    return 0 if captures else 1


if __name__ == "__main__":
    raise SystemExit(main())
