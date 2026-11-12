package com.cambocraft.npc;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Arbre de dialogue à embranchements d'un PNJ (§6.1), chargé depuis
 * {@code data/cambocraft/cambo/dialogues/<id>.json}.
 */
public record DialogueNode(
        String id,
        Localized texte,
        List<Choix> choix) {

    public record Choix(
            Localized reponse,
            String suivant,
            int reputationMin,
            String queteRequise,
            String action) {

        public boolean disponible(int reputationJoueur, java.util.Set<String> quetesActivesOuTerminees) {
            if (reputationJoueur < reputationMin) return false;
            if (queteRequise != null && !queteRequise.isEmpty() && !quetesActivesOuTerminees.contains(queteRequise)) {
                return false;
            }
            return true;
        }
    }

    public record Arbre(String id, String pnj, String noeudInitial, Map<String, DialogueNode> noeuds) {
        public DialogueNode initial() {
            return noeuds.get(noeudInitial);
        }

        public static Arbre fromJson(String id, JsonObject o) {
            String pnj = JsonUtil.str(o, "pnj", id);
            String init = JsonUtil.str(o, "noeud_initial", "accueil");
            Map<String, DialogueNode> map = new LinkedHashMap<>();
            JsonObject nObj = JsonUtil.obj(o, "noeuds");
            for (Map.Entry<String, JsonElement> e : nObj.entrySet()) {
                if (!e.getValue().isJsonObject()) continue;
                JsonObject nd = e.getValue().getAsJsonObject();
                Localized txt = Localized.fromJson(nd.get("texte"), e.getKey());
                List<Choix> choix = new ArrayList<>();
                JsonArray arr = nd.getAsJsonArray("choix");
                if (arr != null) {
                    for (JsonElement ce : arr) {
                        if (!ce.isJsonObject()) continue;
                        JsonObject co = ce.getAsJsonObject();
                        choix.add(new Choix(
                                Localized.fromJson(co.get("reponse"), "..."),
                                JsonUtil.str(co, "suivant", "fin"),
                                JsonUtil.integer(co, "reputation_min", -10000),
                                JsonUtil.str(co, "quete_requise", ""),
                                JsonUtil.str(co, "action", "")));
                    }
                }
                map.put(e.getKey(), new DialogueNode(e.getKey(), txt, List.copyOf(choix)));
            }
            return new Arbre(id, pnj, init, Map.copyOf(map));
        }
    }
}
