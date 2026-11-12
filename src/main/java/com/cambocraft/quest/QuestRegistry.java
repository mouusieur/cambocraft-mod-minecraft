package com.cambocraft.quest;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.data.DataManager;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registre rechargeable de toutes les quêtes ({@code data/cambocraft/cambo/quests/}) :
 * quête d'introduction, 12 chapitres de quête principale, 60+ quêtes secondaires et quêtes répétables.
 */
public final class QuestRegistry implements DataConsumer {
    private static final QuestRegistry INSTANCE = new QuestRegistry();
    private static volatile Map<String, QuestDefinition> parId = Map.of();
    private static volatile List<QuestDefinition> chapitresPrincipaux = List.of();

    private QuestRegistry() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    public static Map<String, QuestDefinition> quetes() {
        return parId;
    }

    public static QuestDefinition quete(String id) {
        return parId.get(id);
    }

    public static List<QuestDefinition> chapitresPrincipaux() {
        return chapitresPrincipaux;
    }

    @Override
    public String nom() {
        return "quetes";
    }

    @Override
    public void charger(DataBundle bundle) {
        Map<String, QuestDefinition> map = new LinkedHashMap<>();
        List<QuestDefinition> principaux = new ArrayList<>();
        for (Map.Entry<String, JsonObject> e : bundle.category("quests").entrySet()) {
            QuestDefinition q = QuestDefinition.fromJson(e.getKey(), e.getValue());
            if (q.etapes().isEmpty()) {
                bundle.error("quests/" + e.getKey() + " : aucune étape définie");
                continue;
            }
            map.put(e.getKey(), q);
            if (q.type() == QuestDefinition.TypeQuete.PRINCIPALE) {
                principaux.add(q);
            }
        }
        principaux.sort(Comparator.comparingInt(QuestDefinition::chapitre));
        parId = Map.copyOf(map);
        chapitresPrincipaux = List.copyOf(principaux);
        Log.info("Quêtes : {} quête(s) chargée(s) dont {} chapitre(s) principaux", parId.size(), chapitresPrincipaux.size());
    }
}
