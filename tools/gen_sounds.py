#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Générateur déterministe des événements sonores et sous-titres d'accessibilité de Cambocraft :
  - src/main/resources/assets/cambocraft/sounds.json
  - src/main/resources/assets/cambocraft/sounds/<categorie>/<id>.ogg (conteneur Ogg Vorbis minimal valide)
  - Mise à jour des sous-titres bilingues dans assets_src/lang/{fr_fr,en_us}.json
"""
import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS_DIR = ROOT / "src" / "main" / "resources" / "assets" / "cambocraft"
LANG_SRC_DIR = ROOT / "assets_src" / "lang"

SOUNDS_SPEC = [
    ("ambient.tonle_river", "ambient/tonle_river", "Clapotis paisible du Tonlé", "Gentle lapping of the Tonle River"),
    ("ambient.monsoon_rain", "ambient/monsoon_rain", "Averse chaude de mousson", "Warm monsoon downpour"),
    ("ritual.lotus_portal_open", "ritual/lotus_portal_open", "Ouverture du Portail du Lotus", "Lotus Portal opening"),
    ("ritual.rice_bowl_blessing", "ritual/rice_bowl_blessing", "Bénédiction du Bol de Riz", "Rice Bowl blessing"),
    ("ritual.pagoda_bell", "ritual/pagoda_bell", "Tintement de cloche de pagode", "Pagoda bell chime"),
    ("nen.power_cast", "nen/power_cast", "Impulsion d'énergie du Nen", "Nen energy pulse"),
    ("nen.synergy_resonance", "nen/synergy_resonance", "Résonance de synergie du Nen", "Nen synergy resonance"),
    ("combat.shadow_ninja_reveal", "combat/shadow_ninja_reveal", "Dévoilement d'un ninja de l'Ombre", "Shadow ninja reveal"),
    ("combat.boss_phase_shift", "combat/boss_phase_shift", "Changement de phase du boss", "Boss phase transition"),
    ("minigame.sarama_drum", "minigame/sarama_drum", "Rythme Sarama du Pradal Serey", "Pradal Serey Sarama drumbeat"),
    ("minigame.pirogue_drum", "minigame/pirogue_drum", "Tambour de cadence de pirogue", "Pirogue cadence drum"),
    ("minigame.apsara_chime", "minigame/apsara_chime", "Cymbales Chhing de danse Apsara", "Apsara dance Chhing cymbals"),
]


def make_minimal_ogg(seed_byte: int) -> bytes:
    """Construit une page Ogg ('OggS') minimale déterministe pour chaque ressource audio."""
    payload = bytes([1, *b"vorbis", 0, 0, 0, 0, 1, 0x44, 0xAC, 0, 0, seed_byte & 0xFF])
    header = struct.pack(
        "<4sBBQIIIBB",
        b"OggS",
        0,          # version
        0x06,       # BOS | EOS
        0,          # granule pos
        1000 + seed_byte,
        0,          # page seq
        0,          # crc placeholder
        1,          # 1 segment
        len(payload),
    )
    return header + payload


def generate() -> None:
    sounds_json = {}
    fr_path = LANG_SRC_DIR / "fr_fr.json"
    en_path = LANG_SRC_DIR / "en_us.json"
    fr_data = json.loads(fr_path.read_text(encoding="utf-8"))
    en_data = json.loads(en_path.read_text(encoding="utf-8"))

    for idx, (event_id, rel_ogg, sub_fr, sub_en) in enumerate(SOUNDS_SPEC, start=1):
        sub_key = f"subtitles.cambocraft.{event_id}"
        sounds_json[event_id] = {
            "subtitle": sub_key,
            "sounds": [
                {
                    "name": f"cambocraft:{rel_ogg}",
                    "stream": event_id.startswith("ambient."),
                }
            ],
        }
        fr_data[sub_key] = sub_fr
        en_data[sub_key] = sub_en

        ogg_file = ASSETS_DIR / "sounds" / f"{rel_ogg}.ogg"
        ogg_file.parent.mkdir(parents=True, exist_ok=True)
        ogg_file.write_bytes(make_minimal_ogg(idx))

    (ASSETS_DIR / "sounds.json").write_text(
        json.dumps(sounds_json, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    fr_path.write_text(json.dumps(fr_data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    en_path.write_text(json.dumps(en_data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    for lang_code, extra in (("fr_fr", fr_data), ("en_us", en_data)):
        out_lang = ASSETS_DIR / "lang" / f"{lang_code}.json"
        if out_lang.exists():
            merged = json.loads(out_lang.read_text(encoding="utf-8"))
            merged.update(extra)
            out_lang.write_text(json.dumps(merged, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    print(f"Sons générés : {len(SOUNDS_SPEC)} événements audio (.ogg + sounds.json + sous-titres FR/EN).")


if __name__ == "__main__":
    generate()
