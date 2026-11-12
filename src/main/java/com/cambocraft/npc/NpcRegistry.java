package com.cambocraft.npc;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.data.DataManager;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registre rechargeable des PNJ, de leurs arbres de dialogues et de leurs catalogues marchands (§6).
 */
public final class NpcRegistry implements DataConsumer {
    private static final NpcRegistry INSTANCE = new NpcRegistry();
    private static volatile Map<String, NpcDefinition> pnjs = Map.of();
    private static volatile Map<String, DialogueNode.Arbre> dialogues = Map.of();
    private static volatile Map<String, MerchantOfferSpec.Catalogue> marchands = Map.of();

    private NpcRegistry() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    public static Map<String, NpcDefinition> pnjs() {
        return pnjs;
    }

    public static NpcDefinition pnj(String id) {
        return pnjs.get(id);
    }

    public static Map<String, DialogueNode.Arbre> dialogues() {
        return dialogues;
    }

    public static DialogueNode.Arbre dialogue(String id) {
        return dialogues.get(id);
    }

    public static Map<String, MerchantOfferSpec.Catalogue> marchands() {
        return marchands;
    }

    public static MerchantOfferSpec.Catalogue marchand(String id) {
        return marchands.get(id);
    }

    @Override
    public String nom() {
        return "pnj_dialogues_marchands";
    }

    @Override
    public void charger(DataBundle bundle) {
        Map<String, NpcDefinition> mapPnj = new LinkedHashMap<>();
        for (Map.Entry<String, JsonObject> e : bundle.category("npcs").entrySet()) {
            NpcDefinition def = NpcDefinition.fromJson(e.getKey(), e.getValue());
            if (def.routine().isEmpty()) {
                bundle.error("npcs/" + e.getKey() + " : routine quotidienne vide");
                continue;
            }
            mapPnj.put(e.getKey(), def);
        }

        Map<String, DialogueNode.Arbre> mapDlg = new LinkedHashMap<>();
        for (Map.Entry<String, JsonObject> e : bundle.category("dialogues").entrySet()) {
            DialogueNode.Arbre arbre = DialogueNode.Arbre.fromJson(e.getKey(), e.getValue());
            if (arbre.initial() == null) {
                bundle.error("dialogues/" + e.getKey() + " : noeud_initial introuvable");
                continue;
            }
            mapDlg.put(e.getKey(), arbre);
        }

        Map<String, MerchantOfferSpec.Catalogue> mapMch = new LinkedHashMap<>();
        for (Map.Entry<String, JsonObject> e : bundle.category("merchants").entrySet()) {
            MerchantOfferSpec.Catalogue cat = MerchantOfferSpec.Catalogue.fromJson(e.getKey(), e.getValue());
            if (cat.offres().isEmpty()) {
                bundle.error("merchants/" + e.getKey() + " : catalogue sans offre");
                continue;
            }
            mapMch.put(e.getKey(), cat);
        }

        pnjs = Map.copyOf(mapPnj);
        dialogues = Map.copyOf(mapDlg);
        marchands = Map.copyOf(mapMch);
        Log.info("PNJ : {} profil(s), {} dialogue(s), {} marchand(s)", pnjs.size(), dialogues.size(), marchands.size());
    }
}
