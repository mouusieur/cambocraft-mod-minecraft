package com.cambocraft.event;

import com.cambocraft.ecology.Season;
import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;

/**
 * Définition d'un événement saisonnier dynamique chargée depuis
 * {@code data/cambocraft/cambo/events/<id>.json}.
 */
public record SeasonalEventDefinition(
        String id,
        Localized nom,
        Localized description,
        Season saisonCible, // null = toutes les saisons (ex. Nuit des Ombres une fois par saison)
        int jourDansSaison, // 0..7
        int heureDebutTick,
        int dureeTicks,
        List<String> effets,
        List<String> recompenses
) {
    public SeasonalEventDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(nom, "nom");
        Objects.requireNonNull(description, "description");
        effets = effets == null ? List.of() : List.copyOf(effets);
        recompenses = recompenses == null ? List.of() : List.copyOf(recompenses);
    }

    public static SeasonalEventDefinition fromJson(String id, JsonObject obj) {
        Localized nom = Localized.fromJson(obj.get("nom"), id);
        Localized desc = Localized.fromJson(obj.get("description"), id);
        String sRaw = JsonUtil.str(obj, "saison", "toutes");
        Season saison = switch (sRaw) {
            case "saison_douce" -> Season.SAISON_DOUCE;
            case "saison_chaude" -> Season.SAISON_CHAUDE;
            case "mousson" -> Season.MOUSSON;
            default -> null;
        };
        int jour = JsonUtil.integer(obj, "jour_saison", 5, 0, 7);
        int debut = JsonUtil.integer(obj, "heure_debut_tick", 13000, 0, 23999);
        int duree = JsonUtil.integer(obj, "duree_ticks", 10000, 200, 192000);
        List<String> effets = JsonUtil.strings(obj, "effets");
        List<String> rec = JsonUtil.strings(obj, "recompenses");
        return new SeasonalEventDefinition(id, nom, desc, saison, jour, debut, duree, effets, rec);
    }
}
