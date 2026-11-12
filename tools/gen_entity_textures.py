#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Génère les textures des entités du mod (artisan, gardien, Neak Ta).

Aucune dépendance externe : encodeur PNG pur Python (struct + zlib), comme ``gen_textures.py``.

Les textures sont dessinées à la main, région par région, selon le découpage UV des modèles
déclarés dans ``ModelesCambocraft`` :

  artisan 64×64  :
    (0,0)   tête                (0,16)  corps
    (0,32)  jambes              (24,16) sarong
    (40,16) bras                (48,32) chapeau conique
    (56,40) ombrelle            (32,0)  visage / détails

  gardien 128×128 :
    (0,0)   tête                (0,40)  torse
    (52,40) ceinture            (88,40) bras
    (0,72)  pagne               (56,72) jambes
    (84,20) cornes              (48,0)  casque

  Neak Ta 64×64 :
    (0,0)   corps               (0,16)  socle
    (28,0)  tête                (28,12) couronne

Sortie : ``src/main/resources/assets/cambocraft/textures/entity/{artisan,gardien,neak_ta}.png``
et le manifeste ``assets_src/textures/manifest_entites.json``.
"""
import json
import os
import struct
import zlib

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SORTIE = os.path.join(RACINE, 'src', 'main', 'resources', 'assets', 'cambocraft', 'textures', 'entity')
MANIFESTE = os.path.join(RACINE, 'assets_src', 'textures', 'manifest_entites.json')

# Palettes culturellement ancrées : soie safran, latérite, grès khmer, or de temple.
SAFRAN = (233, 168, 52, 255)
SAFRAN_OMBRE = (186, 129, 33, 255)
PEAU = (198, 143, 96, 255)
PEAU_OMBRE = (160, 110, 72, 255)
CHEVEUX = (38, 30, 26, 255)
SOIE_ROUGE = (168, 52, 44, 255)
SOIE_ROUGE_OMBRE = (128, 36, 32, 255)
OR = (222, 184, 88, 255)
OR_OMBRE = (170, 136, 56, 255)
GRES = (166, 154, 130, 255)
GRES_OMBRE = (124, 114, 94, 255)
LATERITE = (152, 92, 60, 255)
LATERITE_OMBRE = (116, 68, 44, 255)
NEN = (86, 208, 190, 255)
NEN_SOMBRE = (46, 138, 128, 255)
OS = (226, 216, 196, 255)


class Toile:
    """Grille RGBA manipulable, avec primitives simples."""

    def __init__(self, largeur, hauteur, fond=(0, 0, 0, 0)):
        self.l = largeur
        self.h = hauteur
        self.pixels = [list(fond) for _ in range(largeur * hauteur)]

    def poser(self, x, y, couleur):
        if 0 <= x < self.l and 0 <= y < self.h:
            self.pixels[y * self.l + x] = list(couleur)

    def rect(self, x, y, largeur, hauteur, couleur):
        for dy in range(hauteur):
            for dx in range(largeur):
                self.poser(x + dx, y + dy, couleur)

    def bordure(self, x, y, largeur, hauteur, couleur):
        for dx in range(largeur):
            self.poser(x + dx, y, couleur)
            self.poser(x + dx, y + hauteur - 1, couleur)
        for dy in range(hauteur):
            self.poser(x, y + dy, couleur)
            self.poser(x + largeur - 1, y + dy, couleur)

    def degrade_vertical(self, x, y, largeur, hauteur, haut, bas):
        for dy in range(hauteur):
            t = dy / max(1, hauteur - 1)
            couleur = tuple(int(round(haut[c] * (1 - t) + bas[c] * t)) for c in range(4))
            for dx in range(largeur):
                self.poser(x + dx, y + dy, couleur)

    def moucheture(self, x, y, largeur, hauteur, couleur, pas=3, decalage=0):
        for dy in range(hauteur):
            for dx in range(largeur):
                if (dx + dy * 2 + decalage) % pas == 0:
                    self.poser(x + dx, y + dy, couleur)

    def png(self):
        brut = bytearray()
        for y in range(self.h):
            brut.append(0)
            for x in range(self.l):
                brut.extend(self.pixels[y * self.l + x])

        def morceau(nom, donnees):
            bloc = nom + donnees
            return (struct.pack('>I', len(donnees)) + bloc
                    + struct.pack('>I', zlib.crc32(bloc) & 0xFFFFFFFF))

        entete = struct.pack('>IIBBBBB', self.l, self.h, 8, 6, 0, 0, 0)
        return (b'\x89PNG\r\n\x1a\n' + morceau(b'IHDR', entete)
                + morceau(b'IDAT', zlib.compress(bytes(brut), 9)) + morceau(b'IEND', b''))


def texture_artisan():
    """PNJ pacifique : sarong safran, chemise de soie rouge, chapeau conique, ombrelle."""
    t = Toile(64, 64)

    # --- tête (0,0) 8×8 → échelle ×1 sur la zone UV ---
    t.rect(8, 8, 8, 8, PEAU)
    t.rect(8, 8, 8, 2, CHEVEUX)
    t.poser(10, 12, (30, 30, 30, 255))
    t.poser(13, 12, (30, 30, 30, 255))
    t.rect(10, 15, 4, 1, PEAU_OMBRE)
    t.rect(16, 8, 8, 8, PEAU_OMBRE)   # arrière du crâne
    t.rect(8, 16, 8, 2, PEAU_OMBRE)   # dessous du visage

    # --- corps (0,16) : chemise de soie rouge à liseré or ---
    t.degrade_vertical(20, 20, 8, 12, SOIE_ROUGE, SOIE_ROUGE_OMBRE)
    t.rect(20, 20, 8, 1, OR)
    t.rect(22, 20, 1, 12, OR_OMBRE)
    t.rect(26, 20, 1, 12, OR_OMBRE)
    t.rect(16, 20, 4, 12, SOIE_ROUGE_OMBRE)
    t.rect(28, 20, 4, 12, SOIE_ROUGE_OMBRE)

    # --- sarong (24,16) : safran à bandes ---
    t.degrade_vertical(24, 30, 9, 4, SAFRAN, SAFRAN_OMBRE)
    t.rect(24, 31, 9, 1, OR)
    t.rect(24, 33, 9, 1, SOIE_ROUGE)

    # --- bras (40,16) : manches rouges puis peau ---
    t.rect(44, 20, 3, 6, SOIE_ROUGE)
    t.rect(44, 26, 3, 5, PEAU)

    # --- jambes (0,32) et (16,32) ---
    t.rect(4, 48, 4, 10, PEAU)
    t.rect(20, 48, 4, 10, PEAU)
    t.rect(4, 56, 4, 2, (92, 64, 44, 255))    # sandales
    t.rect(20, 56, 4, 2, (92, 64, 44, 255))

    # --- chapeau conique (48,32) : trois degrés, or et safran ---
    t.rect(48, 32, 10, 10, SAFRAN_OMBRE)
    t.rect(49, 34, 8, 2, OR)
    t.rect(48, 43, 7, 7, SAFRAN)
    t.rect(50, 46, 3, 2, OR_OMBRE)
    t.rect(48, 52, 4, 4, SAFRAN_OMBRE)

    # --- ombrelle (56,40) : manche et disque de soie ---
    t.rect(56, 46, 1, 12, (96, 68, 40, 255))
    t.rect(56, 58, 8, 1, SOIE_ROUGE)
    t.rect(57, 59, 6, 1, SOIE_ROUGE_OMBRE)

    return t


def texture_gardien():
    """Gardien colossal : grès et latérite, cornes de naga, yeux de Nen."""
    t = Toile(128, 128)

    # --- tête (0,0) 12×12 ---
    t.degrade_vertical(6, 6, 12, 12, GRES, GRES_OMBRE)
    t.rect(6, 6, 12, 3, GRES_OMBRE)
    t.rect(9, 11, 2, 2, NEN)
    t.rect(13, 11, 2, 2, NEN)
    t.rect(9, 16, 6, 1, LATERITE_OMBRE)
    t.rect(18, 6, 12, 12, GRES_OMBRE)

    # --- casque (48,0) ---
    t.rect(48, 1, 10, 1, OR)
    t.rect(49, 0, 8, 1, OR_OMBRE)

    # --- cornes (84,20) ---
    t.rect(84, 22, 2, 6, OS)
    t.rect(84, 21, 2, 1, (200, 190, 170, 255))

    # --- torse (0,40) 18×22 ---
    t.degrade_vertical(0, 58, 18, 22, GRES, LATERITE)
    t.moucheture(0, 58, 18, 22, GRES_OMBRE, pas=5)
    t.rect(4, 62, 10, 2, LATERITE_OMBRE)   # plastron
    t.rect(6, 64, 6, 6, NEN_SOMBRE)
    t.rect(7, 65, 4, 4, NEN)
    t.rect(36, 58, 18, 22, GRES_OMBRE)
    t.moucheture(36, 58, 18, 22, LATERITE_OMBRE, pas=7, decalage=2)

    # --- ceinture (52,40) ---
    t.rect(52, 42, 20, 6, OR_OMBRE)
    t.rect(52, 44, 20, 2, OR)

    # --- bras (88,40) 6×20 ---
    t.degrade_vertical(90, 58, 6, 20, LATERITE, LATERITE_OMBRE)
    t.rect(90, 74, 6, 3, GRES)

    # --- pagne (0,72) ---
    t.degrade_vertical(0, 86, 18, 14, SOIE_ROUGE_OMBRE, (96, 28, 24, 255))
    for x in range(0, 18, 4):
        t.rect(x, 86, 1, 14, OR_OMBRE)

    # --- jambes (56,72) ---
    t.rect(58, 90, 6, 12, LATERITE)
    t.rect(58, 100, 6, 2, GRES_OMBRE)

    return t


def texture_neak_ta():
    """Compagnon spirituel : petite stèle de grès veillée par un halo de Nen."""
    t = Toile(64, 64)
    t.rect(12, 10, 8, 10, GRES)
    t.rect(12, 10, 8, 2, GRES_OMBRE)
    t.rect(14, 14, 1, 1, NEN)
    t.rect(17, 14, 1, 1, NEN)
    t.rect(14, 17, 4, 1, GRES_OMBRE)
    t.rect(0, 26, 10, 3, OR_OMBRE)
    t.rect(28, 10, 6, 6, GRES)
    t.rect(28, 22, 8, 1, OR)
    return t


def main():
    os.makedirs(SORTIE, exist_ok=True)
    os.makedirs(os.path.dirname(MANIFESTE), exist_ok=True)
    manifeste = {}
    for nom, fabrique in (('artisan', texture_artisan), ('gardien', texture_gardien),
                          ('neak_ta', texture_neak_ta)):
        toile = fabrique()
        chemin = os.path.join(SORTIE, nom + '.png')
        with open(chemin, 'wb') as f:
            f.write(toile.png())
        manifeste[nom] = {
            'fichier': os.path.relpath(chemin, RACINE),
            'dimensions': [toile.l, toile.h],
            'octets': os.path.getsize(chemin),
        }
        print(f'  {nom}.png  {toile.l}×{toile.h}  {os.path.getsize(chemin)} octets')
    with open(MANIFESTE, 'w', encoding='utf-8') as f:
        json.dump(manifeste, f, ensure_ascii=False, indent=2, sort_keys=True)
        f.write('\n')
    print(f'manifeste : {MANIFESTE}')


if __name__ == '__main__':
    main()
