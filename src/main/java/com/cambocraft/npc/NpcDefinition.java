package com.cambocraft.npc;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Définition JSON d'un archétype de PNJ (§6.1) : identité, faction, personnalité, routine
 * quotidienne sur les 24 000 ticks d'une journée et comportement en cas de pluie ou de danger.
 */
public record NpcDefinition(
        String id,
        Localized nom,
        String role,
        Faction faction,
        String personnalite,
        String dialogueInitial,
        String marchandId,
        boolean peutEtreNinjaDeguise,
        List<CreneauRoutine> routine) {

    public record CreneauRoutine(int debutTick, int finTick, String activite, String lieu) {
        public boolean actifA(long tickJour) {
            int t = (int) Math.floorMod(tickJour, 24000L);
            if (debutTick <= finTick) {
                return t >= debutTick && t < finTick;
            }
            return t >= debutTick || t < finTick;
        }
    }

    /** Activité courante du PNJ selon l'heure et la météo (mise à l'abri sous l'orage). */
    public String activiteCourante(long tickJour, boolean pluieForte) {
        if (pluieForte && !"gardien_du_temple".equals(role)) {
            return "abri_mousson";
        }
        for (CreneauRoutine c : routine) {
            if (c.actifA(tickJour)) return c.activite();
        }
        return "repos";
    }

    public static NpcDefinition fromJson(String id, JsonObject o) {
        Localized nom = Localized.fromJson(o.get("nom"), id);
        String role = JsonUtil.str(o, "role", id);
        Faction faction = Faction.parId(JsonUtil.str(o, "faction", "tonle"));
        String personnalite = JsonUtil.str(o, "personnalite", "serein");
        String dialogue = JsonUtil.str(o, "dialogue_initial", id);
        String marchand = JsonUtil.str(o, "marchand_id", "");
        boolean deguise = JsonUtil.bool(o, "peut_etre_ninja_deguise", false);
        List<CreneauRoutine> creneaux = new ArrayList<>();
        JsonArray arr = o.getAsJsonArray("routine");
        if (arr != null) {
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject c = el.getAsJsonObject();
                creneaux.add(new CreneauRoutine(
                        JsonUtil.integer(c, "debut_tick", 0),
                        JsonUtil.integer(c, "fin_tick", 6000),
                        JsonUtil.str(c, "activite", "travail"),
                        JsonUtil.str(c, "lieu", "village")));
            }
        }
        return new NpcDefinition(id, nom, role, faction, personnalite, dialogue, marchand, deguise, List.copyOf(creneaux));
    }
}
