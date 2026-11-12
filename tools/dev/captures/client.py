#!/usr/bin/env python3
"""Prepare un client Minecraft 1.21.1 + Fabric executable sans launcher officiel.

Telecharge le jar client, toutes les bibliotheques (avec les natives Linux), l'index
d'assets et ses objets, puis ecrit dans le repertoire cible :
  - `classpath.txt` : la liste des jars a passer a -cp ;
  - `natives/`      : les bibliotheques natives de LWJGL ;
  - `assets/`       : les ressources (textures, sons, polices) ;
  - `infos.json`    : identifiant d'index d'assets et classe principale.

Usage : python3 client.py <repertoire_cible> [version_minecraft]
"""

from __future__ import annotations

import json
import os
import sys
import urllib.request
import zipfile
from concurrent.futures import ThreadPoolExecutor

MANIFESTE = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
VERSION_DEFAUT = "1.21.1"


def telecharger(url: str, destination: str) -> str:
    """Telecharge `url` vers `destination` (idempotent : ne refait rien si deja present)."""
    if os.path.exists(destination) and os.path.getsize(destination) > 0:
        return destination
    os.makedirs(os.path.dirname(destination) or ".", exist_ok=True)
    temporaire = destination + ".part"
    with urllib.request.urlopen(url, timeout=60) as reponse, open(temporaire, "wb") as sortie:
        while True:
            bloc = reponse.read(1 << 20)
            if not bloc:
                break
            sortie.write(bloc)
    os.replace(temporaire, destination)
    return destination


def regles_acceptees(regles) -> bool:
    """Applique les `rules` du manifeste Mojang pour la plateforme courante."""
    if not regles:
        return True
    autorise = False
    for regle in regles:
        action = regle.get("action")
        conditions = regle.get("os", {})
        if "name" in conditions and conditions["name"] != "linux":
            continue
        if "arch" in conditions and conditions["arch"] not in ("x86_64", "amd64", "x64"):
            continue
        if "version" in conditions:  # motif regex sur la version du systeme
            continue
        autorise = action == "allow"
    return autorise


def main() -> int:
    cible = os.path.abspath(sys.argv[1] if len(sys.argv) > 1 else "client")
    version = sys.argv[2] if len(sys.argv) > 2 else VERSION_DEFAUT
    os.makedirs(cible, exist_ok=True)

    print(f"== Manifeste Mojang ({version}) ==")
    manifeste = json.load(urllib.request.urlopen(MANIFESTE, timeout=60))
    entree = next(v for v in manifeste["versions"] if v["id"] == version)
    meta = json.load(urllib.request.urlopen(entree["url"], timeout=60))
    print(f"   version publiee le {entree['releaseTime']}")

    # 1. jar client
    jar_client = os.path.join(cible, "client.jar")
    telecharger(meta["downloads"]["client"]["url"], jar_client)
    print(f"   client.jar : {os.path.getsize(jar_client) // 1024 // 1024} Mio")

    # 2. bibliotheques + natives
    jars, natives = [], []
    for lib in meta["libraries"]:
        if not regles_acceptees(lib.get("rules")):
            continue
        telechargements = lib.get("downloads", {})
        artefact = telechargements.get("artifact")
        if artefact:
            chemin = os.path.join(cible, "libraries", artefact["path"])
            telecharger(artefact["url"], chemin)
            jars.append(chemin)
        classify = telechargements.get("classifiers", {})
        natif = classify.get("natives-linux")
        if natif:
            chemin = os.path.join(cible, "libraries", natif["path"])
            telecharger(natif["url"], chemin)
            with zipfile.ZipFile(chemin) as archive:
                archive.extractall(os.path.join(cible, "natives"))
            natives.append(chemin)
    print(f"   {len(jars)} jars, {len(natives)} paquets de natives")

    # 3. assets
    index = meta["assetIndex"]
    chemin_index = os.path.join(cible, "assets", "indexes", index["id"] + ".json")
    telecharger(index["url"], chemin_index)
    with open(chemin_index, encoding="utf-8") as fichier:
        objets = json.load(fichier)["objects"]
    print(f"   {len(objets)} objets d'assets a verifier")

    def recuperer(objet: dict) -> None:
        hache = objet["hash"]
        chemin = os.path.join(cible, "assets", "objects", hache[:2], hache)
        telecharger(f"https://resources.download.minecraft.net/{hache[:2]}/{hache}", chemin)

    with ThreadPoolExecutor(max_workers=16) as pool:
        list(pool.map(recuperer, objets.values()))
    print("   assets termines")

    # 4. fichiers de synthese
    with open(os.path.join(cible, "classpath.txt"), "w", encoding="utf-8") as fichier:
        fichier.write(":".join(jars + [jar_client]))
    with open(os.path.join(cible, "infos.json"), "w", encoding="utf-8") as fichier:
        json.dump(
            {
                "version": version,
                "asset_index": index["id"],
                "main_class": meta["mainClass"],
                "natives": os.path.join(cible, "natives"),
            },
            fichier,
        )
    print("== pret ==")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
