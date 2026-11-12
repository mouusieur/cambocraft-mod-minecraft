#!/usr/bin/env python3
"""Génère les recettes de fabrication des 199 objets de Cambocraft.

Chaque objet reçoit une recette cohérente avec sa catégorie sémantique (nourriture khmère,
chaîne de la soie, poterie, forge, reliques du lotus, armes, monnaie, composants). Sans ce
générateur, seuls 5 objets sur 419 étaient fabricables : le mod était un catalogue.

Sortie : src/main/resources/data/cambocraft/recipe/<id>.json (format 1.21.1)
"""
from __future__ import annotations
import json
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ITEMS_YML = os.path.join(ROOT, "assets_src", "registry", "items.yml")
SORTIE = os.path.join(ROOT, "src", "main", "resources", "data", "cambocraft", "recipe")

NS = "cambocraft"


def catalogue():
    """Lit items.yml et renvoie [(id, champs)] dans l'ordre de déclaration."""
    txt = open(ITEMS_YML, encoding="utf-8").read()
    items = []
    for m in re.finditer(r"-\s*\{id:\s*([a-z0-9_]+)(.*?)\}\s*$", txt, re.M):
        ident, reste = m.group(1), m.group(2)
        champs = {k: v.strip('"') for k, v in re.findall(r'(\w+):\s*("[^"]*"|[^,}]*)', reste)}
        items.append((ident, champs))
    return items


def cid(ident: str) -> str:
    return f"{NS}:{ident}"


def objet(ident: str, n: int = 1) -> dict:
    return {"item": cid(ident), "count": n}


def forme(ident: str, motif: list[str], cles: dict[str, dict], n: int = 1) -> dict:
    return {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "pattern": motif,
        "key": {k: v for k, v in cles.items()},
        "result": {"id": cid(ident), "count": n},
    }


def sans_forme(ident: str, ingredients: list[dict], n: int = 1) -> dict:
    return {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": ingredients,
        "result": {"id": cid(ident), "count": n},
    }


def cuisson(ident: str, source: str, xp: float = 0.35) -> dict:
    return {"type": "minecraft:smelting", "category": "food",
            "ingredient": {"item": cid(source)}, "result": {"id": cid(ident)},
            "experience": xp, "cookingtime": 200}


def affinage(ident: str, source: str) -> dict:
    return {"type": "minecraft:blasting", "category": "misc",
            "ingredient": {"item": cid(source)}, "result": {"id": cid(ident)},
            "experience": 0.1, "cookingtime": 100}


# --- matières premières vanilla servant de base aux recettes ---
BAMBOU = {"item": "minecraft:bamboo"}
ARGILE = {"item": "minecraft:clay_ball"}
VERRE = {"item": "minecraft:glass"}
CUIVRE = {"item": "minecraft:copper_ingot"}
FER = {"item": "minecraft:iron_ingot"}
OR = {"item": "minecraft:gold_ingot"}
CUIR = {"item": "minecraft:leather"}
BOIS = {"item": "minecraft:oak_planks"}
PIERRE = {"item": "minecraft:stone"}
CIRE = {"item": "minecraft:honeycomb"}

# Familles détectées par préfixe/suffixe d'identifiant.
NOURRITURE = ("amok", "banh", "num_pang", "riz_gluant", "kralan", "somlor", "prahok", "lort",
              "kuy_teav", "cha_ktieu", "samlor", "baw")
SOIE = ("soie", "cocon", "tissu", "krama", "fil_")
POTERIE = ("poterie", "jarre", "argile", "brique", "tesson", "cruche")
FORGE = ("forge", "lingot", "fer_", "acier", "alliage", "creuset", "enclume")
RELIGIEUX = ("lotus", "autel", "relique", "amulette", "offrande", "bol_de_riz_beni", "encens",
             "benediction", "talisman")
ARMES = ("epee", "arme", "dague", "lance", "arc_", "fleche")
MONNAIE = ("piece_", "monnaie")


def categorie(ident: str, champs: dict) -> str:
    if "food" in champs:
        return "nourriture"
    for n in NOURRITURE:
        if n in ident:
            return "nourriture"
    for n in MONNAIE:
        if ident.startswith(n):
            return "monnaie"
    for n in ARMES:
        if n in ident:
            return "arme"
    for n in RELIGIEUX:
        if n in ident:
            return "relique"
    for n in FORGE:
        if n in ident:
            return "forge"
    for n in SOIE:
        if n in ident:
            return "soie"
    for n in POTERIE:
        if n in ident:
            return "poterie"
    return "composant"


def recette(ident: str, champs: dict):
    """Renvoie la recette, ou None pour les objets volontairement non fabricables."""
    # Objets déjà dotés d'une recette écrite à la main.
    if ident in {"bol_de_riz_beni", "cadre_du_portail", "canne_a_pecher", "carnet_du_nen",
                 "epee_khmer"}:
        return None

    cat = categorie(ident, champs)

    if cat == "nourriture":
        # Plat cuisiné : riz cuit + un ingrédient de la région (prahok, poisson, coco).
        accompagne = ("prahok" if "prahok" in ident else
                      "poisson" if ("amok" in ident or "samlor" in ident) else
                      "lotus_brut")
        return sans_forme(ident, [objet("riz_cuit"), objet(accompagne)])
    if cat == "monnaie":
        return forme(ident, ["GGG", "GLG", "GGG"],
                     {"G": OR, "L": objet("pierre_du_lotus_brute")}, 4)
    if cat == "arme":
        return forme(ident, ["  I", " I ", "S  "],
                     {"I": FER, "S": objet("fil_de_soie")}, 1) if "epee" in ident \
            else forme(ident, [" I ", "ISI", " I "], {"I": FER, "S": BOIS}, 1)
    if cat == "relique":
        return forme(ident, [" L ", "LRL", " L "],
                     {"L": objet("pierre_du_lotus_brute"), "R": objet("poudre_de_lotus")}, 1)
    if cat == "forge":
        return affinage_forge(ident)
    if cat == "soie":
        if ident.startswith("fil_"):
            return sans_forme(ident, [objet("cocon_de_soie")], 2)
        return forme(ident, ["FF", "FF"], {"F": objet("fil_de_soie")}, 1)
    if cat == "poterie":
        return cuisson_generique(ident)
    # Composants : recettes par famille, chacune rattachée à une matière réelle.
    return composant(ident)


COMPOSANTS = {
    # identifiant : (motif, clés, quantité)
    "graine_de_riz": (None, [{"item": "minecraft:wheat_seeds"}], 2),
    "riz_brut": (None, [{"item": "minecraft:wheat"}], 1),
    "riz_cuit": (None, [objet("riz_brut")], 1),
    "bol_de_riz": (["B B", " B "], {"B": BAMBOU}, 1),
    "pierre_du_lotus_brute": (None, [{"item": "minecraft:calcite"}], 2),
    "lotus_brut": (None, [{"item": "minecraft:pink_petals"}], 2),
    "cocon_de_soie": (None, [{"item": "minecraft:string"}], 1),
    "bambou_brut": (None, [BAMBOU], 4),
    "resine": (None, [{"item": "minecraft:slime_ball"}], 1),
    "charbon_de_bois": (None, [{"item": "minecraft:charcoal"}], 1),
    "argile_brute": (None, [ARGILE], 2),
    "brique_d_argile": (None, [objet("argile_brute")], 2),
    "poudre_de_lotus": (None, [objet("lotus_brut"), objet("pierre_du_lotus_brute")], 2),
}


def composant(ident: str):
    """Recette d'un composant : table dédiée, sinon moulage en bambou (matériau du mod)."""
    if ident in COMPOSANTS:
        motif, ingredients, n = COMPOSANTS[ident]
        if motif is None:
            return sans_forme(ident, ingredients, n)
        return forme(ident, motif, ingredients, n)
    if "graine" in ident or "semence" in ident:
        return sans_forme(ident, [objet("graine_de_riz")], 2)
    if "poudre" in ident or "teinture" in ident:
        return sans_forme(ident, [objet("lotus_brut")], 2)
    if "lingot" in ident:
        return affinage(ident, "fer_brut")
    # Par défaut : objet façonné en bambou, matériau abondant du mod.
    return forme(ident, ["BB", "BB"], {"B": BAMBOU}, 1)


def affinage_forge(ident: str) -> dict:
    return forme(ident, ["III", "I I", "III"], {"I": FER}, 1)


def cuisson_generique(ident: str) -> dict:
    return {
        "type": "minecraft:smelting", "category": "misc",
        "ingredient": {"item": "minecraft:clay_ball"},
        "result": {"id": cid(ident)}, "experience": 0.3, "cookingtime": 200,
    }


def main():
    os.makedirs(SORTIE, exist_ok=True)
    ecrites = 0
    par_categorie: dict[str, int] = {}
    for ident, champs in catalogue():
        r = recette(ident, champs)
        if r is None:
            continue
        with open(os.path.join(SORTIE, ident + ".json"), "w", encoding="utf-8") as f:
            json.dump(r, f, ensure_ascii=False, indent=2)
            f.write("\n")
        ecrites += 1
        c = categorie(ident, champs)
        par_categorie[c] = par_categorie.get(c, 0) + 1
    print(f"recettes : {ecrites} fichiers dans {SORTIE}")
    for c, n in sorted(par_categorie.items(), key=lambda x: -x[1]):
        print(f"  {c:<12} {n}")


if __name__ == "__main__":
    main()
