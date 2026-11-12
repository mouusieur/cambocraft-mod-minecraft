#!/usr/bin/env python3
"""Genere les illustrations du README a partir des vraies captures du jeu.

Entree  : captures/*.png  (produites par captures.py et rapatriees depuis la CI)
Sortie  : docs/captures/  (bandeau, image de tete et galerie 2x2)

Usage : python3 tools/dev/captures/illustrations.py   (depuis la racine du depot)
"""
import subprocess, os

SRC, DST = "captures", "docs/captures"
NUIT, OR, CREME = "#0E1A16", "#C9A227", "#F5EFE0"
PAD = 10
LARGE, HAUT_IMG, HAUT_LEG = 640, 360, 56

def run(*a):
    subprocess.check_call(["convert", *a])

def vignette(src, legende, sortie):
    """Image 640x416 : photo 640x360 en haut, bande de legende 56 px en bas."""
    run(f"{SRC}/{src}", "-resize", f"{LARGE}x{HAUT_IMG}^",
        "-gravity", "center", "-extent", f"{LARGE}x{HAUT_IMG}",
        "-background", NUIT, "-gravity", "north", "-splice", f"0x{HAUT_LEG}",
        "-font", "DejaVu-Sans-Bold", "-pointsize", "25", "-fill", OR,
        "-gravity", "south", "-annotate", "+0+14", legende, sortie)

def case_texte(titre, lignes, sortie):
    """Case 640x416 de texte, pour la 4e position de la grille."""
    args = ["-size", f"{LARGE}x{HAUT_IMG+HAUT_LEG}", f"xc:{NUIT}",
            "-fill", OR, "-draw", f"rectangle 60,150 66,270",
            "-font", "DejaVu-Sans-Bold", "-pointsize", "52", "-fill", CREME,
            "-gravity", "northwest", "-annotate", "+100+150", titre]
    y = 226
    for ligne in lignes:
        args += ["-font", "DejaVu-Sans", "-pointsize", "23", "-fill", OR,
                 "-gravity", "northwest", "-annotate", f"+102+{y}", ligne]
        y += 38
    args.append(sortie)
    run(*args)

vignette("cite-khmere.png", "Cité khmère — prasat à 7 gradins", "/tmp/v1.png")
vignette("rizieres.png", "Rizières en terrasses", "/tmp/v2.png")
vignette("atelier.png", "Atelier — soie, poterie, forge", "/tmp/v3.png")
case_texte("220 blocs", ["199 objets autonomes",
                         "360 textures 16×16",
                         "15 biomes"], "/tmp/v4.png")

subprocess.check_call(["montage", "/tmp/v1.png", "/tmp/v2.png", "/tmp/v3.png", "/tmp/v4.png",
                       "-tile", "2x2", "-geometry", "+10+10", "-background", NUIT,
                       f"{DST}/galerie.png"])
print("galerie OK")

