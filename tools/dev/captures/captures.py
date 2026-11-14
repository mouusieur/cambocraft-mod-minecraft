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
# Reponse de /list : « There are 1 of a max of 20 players online: Cambocraft ».
MOTIF_LISTE = re.compile(r'players online:')


# ---------------------------------------------------------------------------
# Scenes
# ---------------------------------------------------------------------------


def bloc(x, y, z, nom):
    # On passe par /fill (1x1x1) plutot que /setblock : fill est plus tolerant et
    # s'execute de facon fiable dans une dimension personnalisee via /execute.
    return f"fill {x} {y} {z} {x} {y} {z} cambocraft:{nom}"


def cube(x0, y0, z0, x1, y1, z1, nom):
    return [f"fill {x0} {y0} {z0} {x1} {y1} {z1} cambocraft:{nom}"]


# Altitude du studio flottant : au-dessus du terrain naturel de cambodia (le monde
# monte jusqu'a y=319), ce qui supprime les parois de terre qui encombraient le fond.
Y = 200


def scene_portail():
    c = []
    # Terrasse : 19x19 de pierre de lotus polie, bordee de pierre sculptee.
    c += cube(-9, Y, -9, 9, Y, 9, "pierre_du_lotus_polie")
    c += [f"fill -10 {Y} -10 10 {Y} -10 cambocraft:pierre_du_lotus_sculptee",
          f"fill -10 {Y} 10 10 {Y} 10 cambocraft:pierre_du_lotus_sculptee",
          f"fill -10 {Y} -10 -10 {Y} 10 cambocraft:pierre_du_lotus_sculptee",
          f"fill 10 {Y} -10 10 {Y} 10 cambocraft:pierre_du_lotus_sculptee"]
    # Portail : cadre 3x3 en anneau (le coeur occupe le centre) surmonte de montants.
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1):
            if (dx, dz) != (0, 0):
                c += [bloc(dx, Y + 1, dz, "cadre_du_portail")]
    c += [bloc(0, Y + 1, 0, "coeur_du_portail")]
    for dx, dz in ((-2, -2), (2, -2), (-2, 2), (2, 2)):
        c += [f"fill {dx} {Y+1} {dz} {dx} {Y+3} {dz} cambocraft:cadre_du_portail"]
    # Piliers d'angle de lotus couronnes de cristaux.
    for dx, dz in ((-3, -3), (3, -3), (-3, 3), (3, 3)):
        c += [f"fill {dx} {Y+1} {dz} {dx} {Y+4} {dz} cambocraft:pilier_de_lotus"]
        c += [bloc(dx, Y + 5, dz, "cristal_de_lotus")]
    # Parterre de lotus, rangee d'hibiscus, lanternes, autel et teleporteur.
    for dx, dz in ((0, -4), (0, 4), (-4, 0), (4, 0), (-5, -5), (5, 5), (-5, 5), (5, -5)):
        c += [bloc(dx, Y + 1, dz, "lotus_rose" if (dx + dz) % 2 == 0 else "lotus_blanc")]
    for dx in range(-7, 8, 2):
        c += [bloc(dx, Y + 1, 7, "fleur_d_hibiscus")]
    # Lanternes en fond de scene uniquement : placees cote camera (z positif), elles
    # se retrouvaient au premier plan face a l'objectif.
    c += [bloc(-3, Y + 5, -6, "lanterne_de_bambou"), bloc(3, Y + 5, -6, "lanterne_de_bambou")]
    c += [bloc(-6, Y + 1, -6, "teleporteur_de_lotus"), bloc(6, Y + 1, 6, "autel")]
    return c


def scene_cite():
    c = []
    c += cube(-14, Y, -14, 14, Y, 14, "pierre_du_lotus_polie")
    c += [f"fill -15 {Y} -15 15 {Y} -15 cambocraft:gres_khmer_poli",
          f"fill -15 {Y} 15 15 {Y} 15 cambocraft:gres_khmer_poli",
          f"fill -15 {Y} -15 -15 {Y} 15 cambocraft:gres_khmer_poli",
          f"fill 15 {Y} -15 15 {Y} 15 cambocraft:gres_khmer_poli"]
    # Prasat a 7 gradins, 2 blocs de haut chacun.
    for etage in range(7):
        d = 8 - etage
        c += cube(-d, Y + 1 + etage * 2, -d, d, Y + 2 + etage * 2, d, "brique_de_temple")
    c += [bloc(0, Y + 15, 0, "cristal_de_lotus"), bloc(0, Y + 16, 0, "coeur_du_portail")]
    for dx, dz in ((-11, -11), (11, -11), (-11, 11), (11, 11)):
        c += [f"fill {dx} {Y+1} {dz} {dx} {Y+7} {dz} cambocraft:pilier_de_lotus"]
        c += [bloc(dx, Y + 8, dz, "encens")]
    # Halle de marche au toit de chaume.
    c += [f"fill -9 {Y+7} 9 -3 {Y+7} 13 cambocraft:toit_de_chaume",
          f"fill -9 {Y+6} 9 -9 {Y+6} 13 cambocraft:toit_de_chaume",
          f"fill -3 {Y+6} 9 -3 {Y+6} 13 cambocraft:toit_de_chaume"]
    c += [bloc(-7, Y + 1, 11, "etal_de_marche"), bloc(-5, Y + 1, 11, "jarre"),
          bloc(-6, Y + 1, 12, "panier"), bloc(-4, Y + 1, 10, "marmite"),
          bloc(4, Y + 1, -9, "tambour"), bloc(6, Y + 1, -9, "gong"),
          bloc(-4, Y + 1, -9, "lion_de_pierre"), bloc(4, Y + 1, -11, "visage_de_pierre"),
          bloc(0, Y + 1, 12, "statue_de_pierre"), bloc(0, Y + 1, -12, "bas_relief")]
    for i, b in enumerate(("banniere_rouge", "banniere_or", "banniere_bleue")):
        c += [bloc(-9 + i * 6, Y + 4, 14, b)]
    c += [bloc(-2, Y + 5, 12, "tissu_suspendu"), bloc(2, Y + 5, 12, "tissu_suspendu")]
    c += [f"fill -3 {Y+1} 3 3 {Y+1} 5 cambocraft:tapis_de_soie_krama",
          f"fill -3 {Y+1} -5 3 {Y+1} -3 cambocraft:natte_de_jonc"]
    return c


def scene_interieur():
    c = []
    c += cube(-8, Y, -8, 8, Y, 8, "teck_planks")
    c += [f"fill -8 {Y+1} -8 8 {Y+5} -8 cambocraft:brique_de_temple",
          f"fill -8 {Y+1} 8 8 {Y+5} 8 cambocraft:brique_de_temple",
          f"fill -8 {Y+1} -8 -8 {Y+5} 8 cambocraft:brique_de_temple",
          f"fill 8 {Y+1} -8 8 {Y+5} 8 cambocraft:brique_de_temple",
          f"fill -8 {Y+6} -8 8 {Y+6} 8 cambocraft:teck_planks"]
    for x in (-3, 3):
        c += [f"fill {x} {Y+2} -8 {x} {Y+4} -8 cambocraft:vitrail_de_temple",
              f"fill {x} {Y+2} 8 {x} {Y+4} 8 cambocraft:vitrail_de_temple"]
    for z in (-3, 3):
        c += [f"fill 8 {Y+2} {z} 8 {Y+4} {z} cambocraft:vitrail_de_temple"]
    c += [bloc(-5, Y + 1, -5, "metier_a_tisser"), bloc(-5, Y + 2, -5, "tissu_suspendu"),
          bloc(-3, Y + 1, -5, "cuve_de_soie"), bloc(-1, Y + 1, -5, "tour_de_poterie"),
          bloc(1, Y + 1, -5, "table_de_forge"), bloc(3, Y + 1, -5, "four_de_forge"),
          bloc(5, Y + 1, -5, "creuset_de_fusion"), bloc(3, Y + 1, -3, "atelier_de_sculpture"),
          bloc(-5, Y + 1, 5, "jarre"), bloc(-4, Y + 1, 5, "panier"),
          bloc(5, Y + 1, 5, "marmite"), bloc(5, Y + 1, 3, "encens"),
          bloc(0, Y + 1, 6, "tapis_de_soie_krama"), bloc(2, Y + 1, 6, "natte_de_jonc")]
    # Eclairage : les lanternes sont au plafond, devant la camera, pour eclairer
    # les etablis (sans elles, l'interieur est tres sombre).
    for x in (-4, 0, 4):
        c += [bloc(x, Y + 5, -3, "lanterne_de_bambou"), bloc(x, Y + 5, 3, "lanterne_de_bambou")]
    # Deux etablis supplementaires pour remplir le champ.
    c += [bloc(-2, Y + 1, 0, "marmite"), bloc(2, Y + 1, 0, "jarre"),
          bloc(0, Y + 1, -2, "panier")]
    return c


def scene_vegetation():
    c = []
    # Trois terrasses de riziere. Chaque terrasse est un socle plein (jamais une dalle
    # flottante vue par-dessous) surmonte de boue et de riz, avec un bassin d'irrigation
    # clos de toutes parts : sans rebord, l'eau s'ecoule dans le vide en cascade.
    for i, niveau in enumerate((Y, Y + 2, Y + 4)):
        z0 = i * 9 - 9
        c += [f"fill -12 {Y-6} {z0} 12 {niveau-1} {z0+8} cambocraft:laterite"]
        c += [f"fill -12 {niveau} {z0} 12 {niveau} {z0+4} cambocraft:boue_de_riziaire"]
        c += [f"fill -12 {niveau+1} {z0} 12 {niveau+1} {z0} cambocraft:riz_sauvage",
              f"fill -12 {niveau+1} {z0+4} 12 {niveau+1} {z0+4} cambocraft:riz_empile"]
        # Bassin : eau au niveau de la terrasse, bordee de boue sur les quatre cotes.
        c += [f"fill -11 {niveau} {z0+5} 11 {niveau} {z0+7} water",
              f"fill -12 {niveau} {z0+5} -12 {niveau} {z0+8} cambocraft:boue_de_riziaire",
              f"fill 12 {niveau} {z0+5} 12 {niveau} {z0+8} cambocraft:boue_de_riziaire",
              f"fill -12 {niveau} {z0+8} 12 {niveau} {z0+8} cambocraft:boue_de_riziaire"]
        # Fleurs aquatiques a la surface du bassin.
        c += [bloc(-6, niveau + 1, z0 + 6, "lotus_rose"),
              bloc(-3, niveau + 1, z0 + 6, "lotus_blanc"),
              bloc(4, niveau + 1, z0 + 6, "feuille_de_lotus"),
              bloc(7, niveau + 1, z0 + 6, "fleur_de_lotus")]
    # Bande de laterite et vegetaux de berge.
    c += [f"fill -12 {Y-6} 17 12 {Y+4} 17 cambocraft:laterite"]
    for x in range(-12, 13, 3):
        c += [bloc(x, Y + 5, 18, "paille_de_riz" if x % 2 == 0 else "orchidee_khmer")]
    c += [bloc(-10, Y + 5, 19, "paletuvier_sapling"),
          bloc(10, Y + 5, 19, "palmier_a_sucre_sapling")]
    return c


SCENES = {
    # nom : (constructeur, position du joueur, rotation (yaw, pitch), nom de fichier)
    # Les scenes sont baties sur le studio flottant a Y=200 : la camera se place
    # a Y+4 environ, en spectateur (aucune chute possible).
    "portail": (scene_portail, (0.5, Y + 4.0, 12.0), (180.0, 12.0), "portail-du-lotus"),
    "cite": (scene_cite, (0.5, Y + 5.0, 25.0), (180.0, 14.0), "cite-khmere"),
    # Camera a l'interieur de l'atelier (murs en z=+-8) : on cadre les etablis,
    # et non la facade depuis l'exterieur.
    "interieur": (scene_interieur, (0.5, Y + 3.0, 4.5), (180.0, 10.0), "atelier"),
    "vegetation": (scene_vegetation, (0.5, Y + 12.0, 22.0), (180.0, 26.0), "rizieres"),
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

    def attendre_apres(self, motif, depart, delai, description=""):
        """Comme `attendre`, mais en ignorant les lignes anterieures a `depart`."""
        fin = time.time() + delai
        vues = max(0, depart)
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
        # Le rendu d'entite tombe sur llvmpipe (OpenGL logiciel) : desactive pour les captures.
        "-Dcambocraft.rendu.entites=false",
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


def client_vivant(repertoire_client, delai=40):
    """Vrai si le mod a ecrit dans son journal recemment (le client rend encore)."""
    chemin = os.path.join(repertoire_client, "captures-journal.txt")
    if not os.path.exists(chemin):
        return False
    return time.time() - os.path.getmtime(chemin) < delai


def relancer_si_fige(client, repertoire_client, largeur, hauteur, relances=()):
    """Relance le client quand llvmpipe l'a fige.

    Le rendu logiciel met parfois plusieurs minutes a remailler une scene ; le
    client cesse alors de tiquer et n'ecrit plus son journal. Un redemarrage
    (reconnexion automatique) est bien plus rapide que d'attendre.

    `relances` est une liste mutable ou le nombre de relances est cumule : la preuve
    en jeu s'en sert pour attendre que le client soit effectivement reveille, et pas
    seulement redemarre une fois.
    """
    if client_vivant(repertoire_client):
        return client, False
    print("   le client ne repond plus : redemarrage")
    client.kill()
    time.sleep(4)
    nouveau = demarrer_client(repertoire_client, largeur, hauteur, f"127.0.0.1:{PORT}")
    Flux(nouveau, "client")
    return nouveau, True


def _journal_mod(repertoire_client, contexte):
    """Affiche le journal ecrit par le mod : indispensable pour diagnostiquer la CI."""
    chemin = os.path.join(repertoire_client, "captures-journal.txt")
    if not os.path.exists(chemin):
        print(f"   [{contexte}] le mod n'a pas encore ecrit de journal (mecanisme absent ?)")
        return
    with open(chemin, encoding="utf-8", errors="replace") as fichier:
        lignes = [ligne.rstrip() for ligne in fichier if ligne.strip()]
    for ligne in lignes[-3:]:
        print(f"   [{contexte}] MOD> {ligne}")


def _attendre_capture(dossier, avant, delai):
    """Attend qu'un NOUVEAU fichier de capture apparaisse et soit completement ecrit."""
    fin = time.time() + delai
    while time.time() < fin:
        if os.path.isdir(dossier):
            nouveaux = sorted(set(os.listdir(dossier)) - avant)
            if nouveaux:
                produit = os.path.join(dossier, nouveaux[-1])
                precedente = -1
                for _ in range(20):
                    try:
                        actuelle = os.path.getsize(produit)
                    except OSError:
                        break
                    if actuelle == precedente and actuelle > 0:
                        return produit
                    precedente = actuelle
                    time.sleep(0.3)
                if os.path.exists(produit) and os.path.getsize(produit) > 0:
                    return produit
        time.sleep(0.5)
    return None


def _image_noire(chemin):
    """Vrai si l'image est uniformement noire (framebuffer pas encore dessine).

    Un PNG noir de 1280x720 ne pese que quelques centaines d'octets : c'est un
    signal fiable et bien moins couteux que de decoder l'image.
    """
    try:
        taille = os.path.getsize(chemin)
    except OSError:
        return True
    return taille < 5000


def _lignes_journal(repertoire_client):
    chemin = os.path.join(repertoire_client, "captures-journal.txt")
    if not os.path.exists(chemin):
        return []
    with open(chemin, encoding="utf-8", errors="replace") as fichier:
        return [ligne.rstrip() for ligne in fichier]


def _chemin_annonce(lignes):
    """Extrait le chemin absolu que le mod annonce apres une capture reussie.

    ScreenshotRecorder renvoie un message « screenshot.success » contenant le
    chemin complet (clickEvent). C'est la seule source fiable : le nom passe en
    parametre peut etre transforme, et le repertoire depend de gameDir.
    """
    for ligne in reversed(lignes):
        marqueur = "value='"
        if "screenshot.success" in ligne and marqueur in ligne:
            debut = ligne.index(marqueur) + len(marqueur)
            fin = ligne.find("'", debut)
            if fin > debut:
                return ligne[debut:fin]
    return None


def capture_ecran(destination, repertoire_client=None):
    """Capture ce que le joueur voit reellement.

    `import -window root` ne lit que du noir sur un rendu OpenGL logiciel, et les
    appuis de touche se perdent sous Xvfb faute de gestionnaire de fenetres. On
    demande donc la capture au mod (voir CapturesAutomatiques.java) : il publie
    l'image sous `<repertoire de jeu>/captures-auto/<nom>`, chemin fixe.
    """
    if repertoire_client is None:
        return _capture_x(destination)

    dossier = os.path.join(repertoire_client, "captures-auto")
    for tentative in range(5):
        nom = "capture-%d-%d.png" % (int(time.time() * 1000), tentative)
        attendu = os.path.join(dossier, nom)
        try:
            with open(os.path.join(repertoire_client, "capture.demande"), "w",
                      encoding="utf-8") as fichier:
                fichier.write(nom)
        except OSError as e:
            print("   demande de capture impossible :", e)
            break
        fin_attente = time.time() + 30
        while time.time() < fin_attente:
            if os.path.exists(attendu):
                taille = os.path.getsize(attendu)
                if taille > 5000:
                    _publier(attendu, destination)
                    return True
                if taille > 0:
                    # Image noire : le framebuffer n'etait pas pret, on redemande.
                    print("   image noire, nouvelle demande")
                    break
            time.sleep(0.5)
        for ligne in _lignes_journal(repertoire_client)[-2:]:
            print(f"   [capture] MOD> {ligne}")
    print("   la capture pilotee par fichier n'a rien produit, repli sur l'ecran X")
    # Le repli X est noir en rendu logiciel : on ne l'utilise que si la destination
    # n'existe pas encore, pour ne jamais ecraser une capture valide.
    if os.path.exists(destination):
        print(f"   {os.path.basename(destination)} conservee (capture precedente)")
        return True
    return _capture_x(destination)


def _publier(source, destination):
    """Copie l'image en refusant d'ecraser une capture valide par une image noire.

    Une scene mal cadree ne doit pas detruire la capture deja versionnee : si la
    nouvelle image est uniformement noire, l'ancienne est conservee.
    """
    try:
        if _image_noire(source):
            print(f"   image noire ignoree, {os.path.basename(destination)} conservee")
            return False
    except OSError:
        return False
    shutil.copyfile(source, destination)
    return True


def _capture_x(destination):
    """Copie de l'ecran X : ne fonctionne pas en rendu logiciel, ultime recours."""
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
    focaliser_client()
    subprocess.run(["xdotool", "mousemove", str(x), str(y)], capture_output=True, timeout=30)
    time.sleep(0.3)
    subprocess.run(["xdotool", "click", "--repeat", "2" if double else "1", "--delay", "120", "1"],
                   capture_output=True, timeout=30)


def focaliser_client():
    """Ramene le pointeur au centre de l'ecran, sur la fenetre du jeu.

    Sous Xvfb il n'y a aucun gestionnaire de fenetres : la politique de focus
    reste « PointerRoot », donc le clavier va a la fenetre sous le pointeur.
    Inutile (et nuisible) de forcer le focus par fenetre : `xdotool windowfocus`
    peut designer une autre fenetre X que celle du jeu et lui voler le clavier.
    Apres les clics de menu, le pointeur peut sortir de la fenetre ; le recentrer
    suffit a rendre les appuis suivants (F2) au jeu.
    """
    subprocess.run(["xdotool", "mousemove", "640", "360"],
                   capture_output=True, timeout=30)


def appuyer(*touches):
    for combinaison in touches:
        focaliser_client()
        subprocess.run(["xdotool", "key", "--clearmodifiers", combinaison],
                       capture_output=True, timeout=30)


def ecrire_options(repertoire):
    """Options ecrites avant le lancement : HUD masque, FOV large, qualite maximale."""
    options = {
        "version": "3955",
        "guiScale": "2",
        "fov": "70.0",   # 0.5 etait ecrete a 30 deg : cadrage teleobjectif
        "renderDistance": "6",
        "graphicsMode": "1",
        "mipmapLevels": "2",
        "entityShadows": "false",
        "renderClouds": "false",
        "ao": "2",
        "particles": "0",
        "clouds": "false",
        "bobView": "false",
        "hideGui": "true",
        "gamma": "1.0",
        "soundCategory_master": "0.0",
        "narrator": "0",
        "pauseOnLostFocus": "false",
        "fullscreen": "false",
        # Le tutoriel vanilla affiche un encart « Move with WASD » pendant les
        # premieres minutes : on le desactive pour des captures propres.
        "tutorialStep": "none",
        "skipMultiplayerWarning": "true",
        "joinedFirstServer": "true",
        "onboardAccessibility": "false",
    }
    with open(os.path.join(repertoire, "options.txt"), "w", encoding="utf-8") as fichier:
        for cle, valeur in options.items():
            fichier.write(f"{cle}:{valeur}\n")


def touche_f2():
    """Envoie la vraie touche F2 (capture d'ecran) au client sous Xvfb."""
    subprocess.run(["xdotool", "key", "--clearmodifiers", "F2"], check=False,
                   env=os.environ.copy(), timeout=30)


def preuve_pouvoir(commande, sortie_serveur, args, client, largeur, hauteur, sortie_client):
    """Declenche un pouvoir par le chemin des touches et renvoie le resultat.

    On ne peut pas faire confiance aux appuis de touche sous Xvfb (le focus se perd).
    Le mod offre donc un declencheur par fichier qui emprunte exactement le chemin
    d'une touche : CambocraftKeys.lancerEmplacement -> paquet C2S -> PowerEffects.
    Ce qui est prouve ici est le chemin reseau + execution serveur ; l'appui clavier
    lui-meme n'est pas prouve par ce test (il l'est par le meme point d'entree).

    Separee de `main` pour sortir tot : sans joueur connecte, la preuve n'a aucune
    chance d'aboutir et doit le dire clairement.
    """
    print("== preuve en jeu : pouvoir declenche par le chemin clavier ==")
    # Un client fige ne peut pas envoyer de paquet : c'est deja arrive avant la preuve
    # (le journal du mod s'arrete net). On le relance au lieu de conclure a tort que le
    # pouvoir ne marche pas.
    # Le rendu logiciel gele souvent pendant le chargement du monde : un seul
    # redemarrage ne suffit pas. On relance jusqu'a obtenir un client vivant ET
    # reconnecte, en bornant le nombre d'essais.
    vivant = False
    for essai in range(3):
        with sortie_serveur.verrou:
            marque_avant_relance = len(sortie_serveur.lignes)
        client, relance = relancer_si_fige(client, args.client, largeur, hauteur)
        if not relance:
            vivant = True
            break
        print(f"   reveil {essai + 1}/3 : attente de la reconnexion")
        sortie_client = Flux(client, "client")
        # Meme piege que plus bas : l'historique contient deja des `joined the game`.
        if not sortie_serveur.attendre_apres(MOTIF_CONNEXION, marque_avant_relance, 180,
                                            "reconnexion apres reveil"):
            print("   reconnexion impossible apres reveil")
            continue
        time.sleep(25)
        vivant = client_vivant(args.client, delai=20)
    if not vivant:
        print("   client injoignable apres 3 reveils : preuve non tentee")
        return client, "client injoignable : preuve non tentee"
    # Le mode spectateur est pratique pour les captures mais empeche l'activation :
    # un pouvoir se lance en survie ou en creatif. On repasse le joueur en creatif.
    commande("gamemode creative @a")
    time.sleep(2)
    # Sans joueur connecte, toute la preuve est vaine : on le dit au lieu de decouvrir
    # l'echec trois minutes plus tard, quand le client a ete relance.
    # On ne regarde QUE les lignes posterieures : `sortie_serveur.lignes` contient tout
    # l'historique, et un `joined the game` d'une session precedente ferait croire a tort
    # que le joueur est la.
    with sortie_serveur.verrou:
        debut_verif = len(sortie_serveur.lignes)
    commande("list")
    # `list` repond « There are 0 of a max of 20 players online: » : la ligne de
    # reponse dit exactement ce qu'on veut savoir, contrairement a `joined the game`
    # qui peut venir d'une session precedente.
    reponse = sortie_serveur.attendre_apres(MOTIF_LISTE, debut_verif, 20, "liste des joueurs")
    # La reponse est « There are 0 of a max of 20 players online: » — sans espace final
    # quand la liste est vide, d'ou la lecture du nombre plutot qu'une sous-chaine.
    nombre = re.search(r"There are (\d+) of", reponse or "")
    if nombre is None or int(nombre.group(1)) == 0:
        print("   aucun joueur connecte : preuve en jeu non tentee (" + str(reponse) + ")")
        return client, "aucun joueur connecte : preuve non tentee"
    # L'energie du Carnet se recharge de 4 points par seconde : inutile de l'attendre
    # si le cout depasse ce que le joueur possede au demarrage.
    with open(os.path.join(args.client, "pouvoirs-journal.txt"), "w", encoding="utf-8") as f:
        f.write("")
    # `donner_pouvoir` vise le joueur de la source : lancee depuis la console, la
    # commande echoue (« No player was found »). Il faut donc nommer le joueur — et le
    # nom peut differer de `--username` si le client rejoue une session anterieure.
    # `@a` couvre les deux cas.
    commande("execute as @a run cambocraft donner_pouvoir tison_ardent")
    time.sleep(4)
    with sortie_serveur.verrou:
        for ligne in sortie_serveur.lignes[-12:]:
            if "Pouvoir" in ligne or "player" in ligne.lower():
                print("   SERVEUR> " + ligne[:200])
    avant_pouvoir = len(sortie_serveur.lignes)
    with open(os.path.join(args.client, "pouvoir.demande"), "w", encoding="utf-8") as f:
        f.write("0")
    declenche = sortie_serveur.attendre(re.compile(r"Pouvoir lanc"), 60, "pouvoir lance")
    if not declenche:
        # Une deuxieme demande ne coute rien : les refus (recharge, cadence) sont
        # journalises par le serveur et restent la seule facon de diagnostiquer.
        with open(os.path.join(args.client, "pouvoir.demande"), "w", encoding="utf-8") as f:
            f.write("0")
        declenche = sortie_serveur.attendre(re.compile(r"Pouvoir lanc"), 60, "pouvoir lance (2e essai)")
    if not declenche:
        # Etat du joueur vu du serveur : sans lui, un echec ne dit pas s'il vient du
        # client (fichier non lu), du reseau (paquet refuse) ou des preconditions.
        commande("cambocraft carnet")
        commande("execute in cambocraft:cambodia run data get entity Cambocraft Pos")
        time.sleep(3)
    with sortie_serveur.verrou:
        for ligne in sortie_serveur.lignes[avant_pouvoir:]:
            if "Pouvoir" in ligne or "pouvoir" in ligne:
                print("   SERVEUR> " + ligne[:200])
    journal_pouvoirs = os.path.join(args.client, "pouvoirs-journal.txt")
    if os.path.exists(journal_pouvoirs):
        with open(journal_pouvoirs, encoding="utf-8", errors="replace") as f:
            for ligne in [l.rstrip() for l in f if l.strip()][-5:]:
                print("   MOD> " + ligne[:200])
        shutil.copyfile(journal_pouvoirs, os.path.join(args.sortie, "pouvoirs-journal.txt"))
    # Ce diagnostic est repete en toute fin de sortie (voir plus bas) : le milieu de la
    # sortie est tronque dans le resume des check runs.
    resultat = ("pouvoir execute cote serveur" if declenche
                else "AUCUNE ligne 'Pouvoir lance' : non prouve")
    print("   resultat : " + resultat)
    if capture_ecran(os.path.join(args.sortie, "pouvoir.png"), args.client):
        print("   capture : pouvoir.png")
    # Le client a pu etre relance : l'appelant doit continuer avec cette instance.
    return client, resultat


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
        capture_ecran(os.path.join(args.sortie, "00-ecran-titre.png"), args.client)
        # Bouton « Multijoueur » : x = 320 logique, y = 90 + 48 + 24 = 162 logique.
        cliquer(args.largeur // 2, int(args.hauteur * 162 / 360))
        time.sleep(4)
        capture_ecran(os.path.join(args.sortie, "01-multijoueur.png"), args.client)
        # Premiere entree de la liste de serveurs, puis bouton « Rejoindre ».
        cliquer(args.largeur // 2, int(args.hauteur * 60 / 360))
        time.sleep(1)
        capture_ecran(os.path.join(args.sortie, "02-serveur-selectionne.png"), args.client)
        cliquer(int(args.largeur * 241 / 640), int(args.hauteur * 342 / 360))
        connecte = bool(sortie_serveur.attendre(MOTIF_CONNEXION, 120, "joueur (interface)"))
    if not connecte:
        print("!! Le joueur ne s'est jamais connecte. Capture de l'ecran courant :")
        os.makedirs(args.sortie, exist_ok=True)
        capture_ecran(os.path.join(args.sortie, "00-ecran-titre.png"), args.client)
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

    # Mode spectateur : en creatif, le bras du joueur (texture de peau orange)
    # occupe le coin bas-droit de chaque capture. En spectateur il disparait.
    commande("gamemode spectator @a")
    # On masque le message de tutoriel vanilla (« Move with WASD »), qui reste
    # affiche plusieurs minutes apres la connexion et pollue les captures.
    commande("gamerule doDaylightCycle false")
    commande("execute in cambocraft:cambodia run forceload add -64 -64 64 64")
    time.sleep(4)
    # Vol stationnaire : sans cela, le joueur tombe dans le vide pendant la construction
    # et la capture ne montre que du noir.
    commande(f"execute in cambocraft:cambodia run tp @a 0 {Y + 30} 0 0 0")

    # --- verification de sanite : un bloc d'or de 3x3, camera dessus, regard vers le bas ---
    print("== verification de sanite du rendu ==")
    avant = len(sortie_serveur.lignes)
    commande(f"execute in cambocraft:cambodia run fill -2 {Y} -2 2 {Y} 2 minecraft:gold_block")
    commande(f"execute in cambocraft:cambodia run tp @a 0 {Y+3} 0 0 80")
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
    if capture_ecran(os.path.join(args.sortie, "sanite.png"), args.client):
        print("   capture : sanite.png")
    # Preuve en jeu tant que la session est fraiche : le client sera relance ensuite
    # pour les scenes, et chaque relance peut le laisser deconnecte.
    client, resultat_pouvoir = preuve_pouvoir(
        commande, sortie_serveur, args, client, args.largeur, args.hauteur, sortie_client)
    commande("execute in cambocraft:cambodia run data get entity Cambocraft Pos")
    time.sleep(2)

    captures = []
    for nom in [s.strip() for s in args.scenes.split(",") if s.strip()]:
        if nom not in SCENES:
            print(f"   scene inconnue : {nom}")
            continue
        constructeur, position, rotation, fichier = SCENES[nom]
        print(f"== scene {nom} ({fichier}) ==")
        # Le rendu logiciel peut figer le client en pleine scene : on le relance
        # avant de construire, puis on reconstruit la scene sur le client neuf.
        client, relance = relancer_si_fige(
            client, args.client, args.largeur, args.hauteur)
        if relance:
            sortie_client = Flux(client, "client")
            if not sortie_serveur.attendre(MOTIF_CONNEXION, 150, "reconnexion apres relance"):
                print("   reconnexion impossible : scene ignoree")
                continue
            time.sleep(20)
        # Nettoyage du studio : la scene precedente doit disparaitre ET le terrain naturel
        # (qui monte jusqu'a y=319) laissait des blocs flottants au-dessus de la scene.
        # /fill est limite a 32 768 blocs par commande : 65x6x65 = 25 350 blocs par tranche.
        for y0 in range(Y - 2, Y + 30, 6):
            commande(f"execute in cambocraft:cambodia run fill -32 {y0} -32 32 {y0 + 5} 32 air")
        for y0 in range(Y + 30, 320, 6):
            commande(f"execute in cambocraft:cambodia run fill -24 {y0} -24 24 {y0 + 5} 24 air")
        time.sleep(3)
        for cmd in constructeur():
            commande(f"execute in cambocraft:cambodia run {cmd}")
        time.sleep(3)
        x, y, z = position
        # Aucune plateforme de secours : le joueur est en spectateur, il ne tombe pas,
        # et une plateforme au premier plan gachait le cadrage.
        commande(f"execute in cambocraft:cambodia run tp @a {x} {y} {z} {rotation[0]} {rotation[1]}")
        time.sleep(20)

        # Diagnostic : position et dimension reelles du joueur.
        avant_diag = len(sortie_serveur.lignes)
        commande("execute in cambocraft:cambodia run data get entity Cambocraft Pos")
        sortie_serveur.attendre(re.compile(r"has the following entity data"), 20, "position")
        with sortie_serveur.verrou:
            for ligne in sortie_serveur.lignes[avant_diag:]:
                if "entity data" in ligne or "Pos" in ligne:
                    print("   " + ligne)
        # On laisse au client le temps de recevoir et de mailler les chunks.
        time.sleep(12)
        cible = os.path.join(args.sortie, fichier + ".png")
        if capture_ecran(cible, args.client):
            captures.append(cible)
            print(f"   capture : {cible}")

    # --- verification des systemes : entites et commandes, APRES les scenes ---
    # L'invocation d'une entite fait tomber le contexte OpenGL logiciel de la CI : toutes
    # les captures suivantes seraient noires. On la fait donc en dernier, dans un client
    # fraichement relance, avant l'arret.
    print("== verification des systemes (client relance) ==")
    client.kill()
    time.sleep(3)
    client = demarrer_client(args.client, args.largeur, args.hauteur, f"127.0.0.1:{PORT}")
    sortie_client = Flux(client, "client")
    if not sortie_serveur.attendre(MOTIF_CONNEXION, 120, "reconnexion"):
        print("   reconnexion impossible : verification des entites ignoree")
    else:
        time.sleep(25)
        commande("gamemode spectator @a")
        commande(f"execute in cambocraft:cambodia run forceload add -64 -64 64 64")
        commande(f"execute in cambocraft:cambodia run fill -5 {Y} 0 5 {Y} 10 cambocraft:pierre_du_lotus_polie")
        commande(f"execute in cambocraft:cambodia run summon cambocraft:artisan 2 {Y+1} 6")
        commande(f"execute in cambocraft:cambodia run summon cambocraft:gardien -2 {Y+1} 6")
        for sous in ("statut", "boss", "carnet"):
            commande(f"cambocraft {sous}")
        time.sleep(4)
        commande(f"execute in cambocraft:cambodia run tp @a 0 {Y+4} 14 180 6")
        time.sleep(10)
        if capture_ecran(os.path.join(args.sortie, "entites.png"), args.client):
            print("   capture : entites.png")
        commande("execute in cambocraft:cambodia run kill @e[type=cambocraft:artisan]")
        commande("execute in cambocraft:cambodia run kill @e[type=cambocraft:gardien]")

    # --- verification de l'interface : table, carnet et HUD, en jeu ---
    # Le HUD est masque dans les options des captures de paysage : on le reactive
    # (F1) pour la derniere etape, sinon l'interface n'apparait pas a l'ecran.
    print("== verification de l'interface (HUD, carnet, table) ==")
    commande(f"execute in cambocraft:cambodia run tp @a 0 {Y+6} 18 180 8")
    time.sleep(15)

    # Le HUD a ete masque dans options.txt (hideGui) pour les captures de paysage.
    appuyer("F1")
    time.sleep(3)
    if capture_ecran(os.path.join(args.sortie, "hud.png"), args.client):
        print("   capture : hud.png")
    commande("cambocraft carnet")
    time.sleep(4)
    if capture_ecran(os.path.join(args.sortie, "carnet.png"), args.client):
        print("   capture : carnet.png")
    appuyer("Escape")
    time.sleep(2)
    commande("cambocraft table")
    time.sleep(4)
    if capture_ecran(os.path.join(args.sortie, "table.png"), args.client):
        print("   capture : table.png")
    appuyer("Escape")
    client.kill()

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
    # Diagnostic des entites en TOUTE FIN : la fin de la sortie survit a la troncature
    # des check runs (le milieu est coupe a 65 535 caracteres).
    with sortie_serveur.verrou:
        interessantes = [l for l in sortie_serveur.lignes
                         if any(m in l.lower() for m in ("artisan", "gardien", "summon", "unknown"))]
    for ligne in interessantes[-10:]:
        print("   FIN-DIAG> " + ligne[:180])
    print("   FIN-POUVOIR> " + resultat_pouvoir)
    # Diagnostic compact en une seule ligne : la fin de sortie est la seule zone qui
    # survit a la troncature du resume des check runs.
    with sortie_serveur.verrou:
        recentes = [l for l in sortie_serveur.lignes
                    if any(m in l for m in ("Pouvoir donne", "Pouvoir lanc", "Pouvoir inconnu",
                                            "No player", "joined the game", "left the game",
                                            "Pouvoir ", "pouvoir "))]
    for ligne in recentes[-6:]:
        print("   FIN-TRACE> " + ligne[:150])
    return 0 if captures else 1


if __name__ == "__main__":
    raise SystemExit(main())
