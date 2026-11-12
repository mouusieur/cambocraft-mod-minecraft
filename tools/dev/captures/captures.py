#!/usr/bin/env python3
"""Lance un vrai client Minecraft avec Cambocraft et capture de veritables ecrans.

Le client est demarre sans launcher officiel (voir client.py), en mode hors-ligne,
avec un rendu logiciel (Mesa/llvmpipe). Un monde mono-joueur est cree, les scenes sont
construites par commandes, puis des captures sont prises via la touche F2 de Minecraft.

Usage : python3 captures.py --client <dir> --jar <mod.jar> --sortie <dir>
        [--scenes portail,cite,interieur,vegetation] [--largeur 1280] [--hauteur 720]
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

# ---------------------------------------------------------------------------
# Construction des scenes (commandes envoyees au serveur integre)
# ---------------------------------------------------------------------------


def bloc(x, y, z, nom):
    return f"setblock {x} {y} {z} cambocraft:{nom}"


def cube(x0, y0, z0, x1, y1, z1, nom):
    return [f"fill {x0} {y0} {z0} {x1} {y1} {z1} cambocraft:{nom}"]


def scene_portail():
    """Le Portail du Lotus complet : socle, anneau 3x3, cadre, coeur, jardins, piliers."""
    c = []
    c += cube(-9, 59, -9, 9, 59, 9, "pierre_du_lotus_polie")
    c += [f"fill -10 59 -10 10 59 -10 cambocraft:pierre_du_lotus_sculptee",
          f"fill -10 59 10 10 59 10 cambocraft:pierre_du_lotus_sculptee",
          f"fill -10 59 -10 -10 59 10 cambocraft:pierre_du_lotus_sculptee",
          f"fill 10 59 -10 10 59 10 cambocraft:pierre_du_lotus_sculptee"]
    # anneau horizontal 3x3 + coeur
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1):
            c += [bloc(dx, 60, dz, "cadre_du_portail")]
    c += [bloc(0, 60, 0, "coeur_du_portail")]
    # quatre piliers de lotus surmontes d'un cristal
    for dx, dz in ((-3, -3), (3, -3), (-3, 3), (3, 3)):
        c += [f"fill {dx} 60 {dz} {dx} 63 {dz} cambocraft:pilier_de_lotus"]
        c += [bloc(dx, 64, dz, "cristal_de_lotus")]
    # jardins de lotus
    for dx, dz in ((0, -4), (0, 4), (-4, 0), (4, 0), (-5, -5), (5, 5), (-5, 5), (5, -5)):
        c += [bloc(dx, 60, dz, "lotus_rose" if (dx + dz) % 2 == 0 else "lotus_blanc")]
    for dx in range(-7, 8, 2):
        c += [bloc(dx, 60, 7, "fleur_d_hibiscus")]
    c += [bloc(0, 64, -6, "lanterne_de_bambou"), bloc(0, 64, 6, "lanterne_de_bambou")]
    c += [bloc(-6, 60, -6, "teleporteur_de_lotus"), bloc(6, 60, 6, "autel"),
          bloc(-6, 61, -6, "socle_d_offrande")]
    return c


def scene_cite():
    """Une place khmere : prasat central, piliers, toit de chaume, marche, tambour, lions."""
    c = []
    c += cube(-14, 59, -14, 14, 59, 14, "pierre_du_lotus_polie")
    c += cube(-15, 59, -15, 15, 59, -15, "gres_khmer_poli")
    c += cube(-15, 59, 15, 15, 59, 15, "gres_khmer_poli")
    c += cube(-15, 59, -15, -15, 59, 15, "gres_khmer_poli")
    c += cube(15, 59, -15, 15, 59, 15, "gres_khmer_poli")
    # prasat a sept niveaux
    for etage in range(7):
        d = 8 - etage
        c += cube(-d, 60 + etage * 2, -d, d, 60 + etage * 2, d, "brique_de_temple")
        c += cube(-d, 61 + etage * 2, -d, d, 61 + etage * 2, d, "brique_de_temple")
    c += [bloc(0, 74, 0, "cristal_de_lotus"), bloc(0, 75, 0, "coeur_du_portail")]
    for dx, dz in ((-11, -11), (11, -11), (-11, 11), (11, 11)):
        c += [f"fill {dx} 60 {dz} {dx} 66 {dz} cambocraft:pilier_de_lotus"]
        c += [bloc(dx, 67, dz, "encens")]
    # etals de marche et toit de chaume
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
    """Un atelier : metier a tisser, cuve de soie, forge, four, poterie, vitraux."""
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
    """Rizieres en terrasses : riz a differents stades, eau, lotus, fleurs, arbres."""
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
    "portail": (scene_portail, (0, 62.5, 15), (0.0, 12.0), "portail-du-lotus"),
    "cite": (scene_cite, (0, 65.0, 24), (0.0, 16.0), "cite-khmere"),
    "interieur": (scene_interieur, (0, 62.0, 6), (0.0, 8.0), "atelier"),
    "vegetation": (scene_vegetation, (0, 67.0, 18), (0.0, 18.0), "rizieres"),
}

MOTIF_PRET = re.compile(r"Done \([0-9.]+s\)!")


class Client:
    """Pilote un client Minecraft : demarrage, commandes, captures."""

    def __init__(self, args):
        self.args = args
        self.capture_dir = os.path.join(args.client, "screenshots")
        os.makedirs(self.capture_dir, exist_ok=True)
        self.proc = None
        self.lignes = []
        self.verrou = threading.Lock()
        self.fil = None
        self.pret = threading.Event()

    def demarrer(self):
        infos = json.load(open(os.path.join(self.args.client, "infos.json"), encoding="utf-8"))
        classpath = open(os.path.join(self.args.client, "classpath.txt"), encoding="utf-8").read().strip()
        cmd = [
            "java",
            f"-Xmx{self.args.memoire}",
            "-Djava.library.path=" + infos["natives"],
            "-Dorg.lwjgl.librarypath=" + infos["natives"],
            "-Dfile.encoding=UTF-8",
            "-Dfabric.debug.disableClassPathIsolation=true",
            "-cp", classpath,
            infos["main_class"],
            "--username", "Cambocraft",
            "--uuid", "00000000000040008000000000000001",
            "--accessToken", "0",
            "--version", infos["version"],
            "--gameDir", self.args.client,
            "--assetsDir", os.path.join(self.args.client, "assets"),
            "--assetIndex", infos["asset_index"],
            "--width", str(self.args.largeur),
            "--height", str(self.args.hauteur),
            "--userType", "legacy",
        ]
        self.proc = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            stdin=subprocess.PIPE,
            bufsize=0,
            cwd=self.args.client,
        )
        self.fil = threading.Thread(target=self.lire, daemon=True)
        self.fil.start()

    def lire(self):
        tampon = b""
        while True:
            bloc = self.proc.stdout.read(4096)
            if not bloc:
                break
            tampon += bloc
            while b"\n" in tampon:
                ligne, tampon = tampon.split(b"\n", 1)
                texte = ligne.decode("utf-8", "replace").rstrip()
                with self.verrou:
                    self.lignes.append(texte)
                if MOTIF_PRET.search(texte):
                    self.pret.set()

    def dire(self, commande):
        self.proc.stdin.write((commande + "\n").encode("utf-8"))
        self.proc.stdin.flush()

    def attendre(self, condition, delai=90.0, description=""):
        fin = time.time() + delai
        while time.time() < fin:
            with self.verrou:
                if any(condition(l) for l in self.lignes):
                    return True
            time.sleep(0.4)
        print(f"   [delai depasse] {description}")
        return False

    def photographier(self, nom):
        self.dire("screenshot")
        if not self.attendre(lambda l: "Saved screenshot" in l, 60, f"capture {nom}"):
            return None
        with self.verrou:
            for ligne in reversed(self.lignes):
                if "Saved screenshot" in ligne:
                    chemin = ligne.split("as ", 1)[-1].strip()
                    self.lignes.remove(ligne)
                    break
            else:
                chemin = None
        if chemin and os.path.exists(chemin):
            cible = os.path.join(self.args.sortie, nom + ".png")
            shutil.copy(chemin, cible)
            return cible
        return None

    def arreter(self):
        try:
            self.dire("stop")
        except Exception:
            pass
        for _ in range(40):
            if self.proc.poll() is not None:
                return
            time.sleep(0.5)
        self.proc.kill()


def main() -> int:
    analyseur = argparse.ArgumentParser()
    analyseur.add_argument("--client", required=True)
    analyseur.add_argument("--jar", required=True)
    analyseur.add_argument("--sortie", required=True)
    analyseur.add_argument("--scenes", default="portail")
    analyseur.add_argument("--largeur", type=int, default=1280)
    analyseur.add_argument("--hauteur", type=int, default=720)
    analyseur.add_argument("--memoire", default="2G")
    analyseur.add_argument("--minutes", type=float, default=20.0)
    args = analyseur.parse_args()
    os.makedirs(args.sortie, exist_ok=True)

    mods = os.path.join(args.client, "mods")
    print("== mods installes ==")
    for nom in sorted(os.listdir(mods)) if os.path.isdir(mods) else []:
        print("   " + nom)

    client = Client(args)
    print("== demarrage du client ==")
    client.demarrer()

    if not client.pret.wait(timeout=args.minutes * 60):
        print("!! Le monde n'a jamais ete pret. Dernieres lignes :")
        with client.verrou:
            for ligne in client.lignes[-80:]:
                print("   " + ligne)
        client.arreter()
        with open(os.path.join(args.sortie, "journal-client.txt"), "w", encoding="utf-8") as f:
            f.write("\n".join(client.lignes))
        return 1
    print("== monde pret ==")
    time.sleep(5)

    for commande in (
        "gamerule doDaylightCycle false",
        "gamerule doWeatherCycle false",
        "gamerule doMobSpawning false",
        "gamerule sendCommandFeedback false",
        "time set 6000",
        "weather clear",
        "gamemode creative @a",
    ):
        client.dire(commande)
        time.sleep(0.4)

    resultats = []
    for nom in [s.strip() for s in args.scenes.split(",") if s.strip()]:
        if nom not in SCENES:
            print(f"   scene inconnue : {nom}")
            continue
        constructeur, position, rotation, fichier = SCENES[nom]
        print(f"== scene {nom} ({fichier}) ==")
        client.dire("execute in cambocraft:cambodia run forceload add -48 -48 48 48")
        time.sleep(3)
        for commande in constructeur():
            client.dire(f"execute in cambocraft:cambodia run {commande}")
        time.sleep(2)
        x, y, z = position
        client.dire(f"execute in cambocraft:cambodia run tp @a {x} {y} {z} {rotation[0]} {rotation[1]}")
        time.sleep(4)
        chemin = client.photographier(fichier)
        if chemin:
            resultats.append(chemin)
            print(f"   capture : {chemin}")

    client.arreter()

    print("== fin ==")
    for chemin in resultats:
        print("   " + chemin)
    with open(os.path.join(args.sortie, "journal-client.txt"), "w", encoding="utf-8") as f:
        f.write("\n".join(client.lignes))
    return 0 if resultats else 1


if __name__ == "__main__":
    raise SystemExit(main())
