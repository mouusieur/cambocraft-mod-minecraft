package com.cambocraft.quest;

import com.cambocraft.npc.Faction;
import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Définition déclarative d'une quête JSON (§8), chargée depuis
 * {@code data/cambocraft/cambo/quests/<id>.json}.
 */
public record QuestDefinition(
        String id,
        int chapitre,
        TypeQuete type,
        Localized titre,
        Localized resume,
        int niveauMin,
        String queteRequise,
        String saisonRequise,
        Faction faction,
        List<Etape> etapes,
        List<Branche> branches,
        Recompense recompenses) {

    public enum TypeQuete {
        INTRO("intro", false),
        PRINCIPALE("principale", false),
        SECONDAIRE("secondaire", true),
        SAISONNIERE("saisonniere", true),
        QUOTIDIENNE("quotidienne", true),
        HEBDOMADAIRE("hebdomadaire", true);

        public final String id;
        public final boolean abandonnable;

        TypeQuete(String id, boolean abandonnable) {
            this.id = id;
            this.abandonnable = abandonnable;
        }

        public static TypeQuete parId(String raw) {
            if (raw == null) return SECONDAIRE;
            for (TypeQuete t : values()) {
                if (t.id.equalsIgnoreCase(raw)) return t;
            }
            return SECONDAIRE;
        }
    }

    public record Etape(
            String id,
            String objectif,
            String cible,
            int quantite,
            Localized description) {}

    public record Branche(
            String id,
            Localized titre,
            Localized consequence,
            String pouvoirDebloque,
            int bonusReputation) {}

    public record Recompense(
            long xp,
            int pieces,
            int reputation,
            List<String> objets,
            String pouvoir) {}

    public static QuestDefinition fromJson(String id, JsonObject o) {
        int chap = JsonUtil.integer(o, "chapitre", 0);
        TypeQuete type = TypeQuete.parId(JsonUtil.str(o, "type", "secondaire"));
        Localized titre = Localized.fromJson(o.get("titre"), id);
        Localized resume = Localized.fromJson(o.get("resume"), id);
        int nivMin = Math.max(1, JsonUtil.integer(o, "niveau_min", 1));
        String req = JsonUtil.str(o, "quete_requise", "");
        String saison = JsonUtil.str(o, "saison_requise", "*");
        Faction fac = Faction.parId(JsonUtil.str(o, "faction", "tonle"));

        List<Etape> etapes = new ArrayList<>();
        JsonArray arrE = o.getAsJsonArray("etapes");
        if (arrE != null) {
            for (JsonElement el : arrE) {
                if (!el.isJsonObject()) continue;
                JsonObject eo = el.getAsJsonObject();
                etapes.add(new Etape(
                        JsonUtil.str(eo, "id", "etape_" + (etapes.size() + 1)),
                        JsonUtil.str(eo, "objectif", "parler"),
                        JsonUtil.str(eo, "cible", ""),
                        Math.max(1, JsonUtil.integer(eo, "quantite", 1)),
                        Localized.fromJson(eo.get("description"), "")));
            }
        }

        List<Branche> branches = new ArrayList<>();
        JsonArray arrB = o.getAsJsonArray("branches");
        if (arrB != null) {
            for (JsonElement el : arrB) {
                if (!el.isJsonObject()) continue;
                JsonObject bo = el.getAsJsonObject();
                branches.add(new Branche(
                        JsonUtil.str(bo, "id", "equilibre"),
                        Localized.fromJson(bo.get("titre"), ""),
                        Localized.fromJson(bo.get("consequence"), ""),
                        JsonUtil.str(bo, "pouvoir_debloque", ""),
                        JsonUtil.integer(bo, "bonus_reputation", 0)));
            }
        }

        JsonObject rec = JsonUtil.obj(o, "recompenses");
        Recompense recompense = new Recompense(
                Math.max(0L, JsonUtil.longValue(rec, "xp", 100L)),
                Math.max(0, JsonUtil.integer(rec, "pieces", 10)),
                JsonUtil.integer(rec, "reputation", 25),
                JsonUtil.strings(rec, "objets"),
                JsonUtil.str(rec, "pouvoir", ""));

        return new QuestDefinition(
                id, chap, type, titre, resume, nivMin, req, saison, fac,
                List.copyOf(etapes), List.copyOf(branches), recompense);
    }
}
