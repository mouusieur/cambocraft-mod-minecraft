package com.cambocraft.combat;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.data.DataManager;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registre rechargeable du bestiaire (ennemis de zone, ninjas du Clan Ombre et 12 boss multi-phases)
 * depuis {@code data/cambocraft/cambo/enemies/*.json}.
 */
public final class CombatRegistry implements DataConsumer {
    private static final CombatRegistry INSTANCE = new CombatRegistry();

    private static volatile Map<String, EnemyDefinition> parId = Map.of();
    private static volatile List<EnemyDefinition> bossList = List.of();

    private CombatRegistry() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    @Override
    public String nom() {
        return "CombatRegistry";
    }

    @Override
    public void charger(DataBundle bundle) {
        Map<String, EnemyDefinition> map = new LinkedHashMap<>();
        List<EnemyDefinition> bosses = new ArrayList<>();
        for (Map.Entry<String, com.google.gson.JsonObject> e : bundle.category("enemies").entrySet()) {
            String key = e.getKey();
            try {
                EnemyDefinition def = EnemyDefinition.fromJson(key, e.getValue());
                map.put(def.id(), def);
                if (def.estBoss()) {
                    bosses.add(def);
                }
            } catch (RuntimeException ex) {
                bundle.error("enemies/" + key + ".json : " + ex.getMessage());
            }
        }
        parId = Collections.unmodifiableMap(map);
        bossList = Collections.unmodifiableList(bosses);
        Log.info("Bestiaire chargé : {} ennemis dont {} boss multi-phases", parId.size(), bossList.size());
    }

    public static EnemyDefinition get(String id) {
        return parId.get(id);
    }

    public static Collection<EnemyDefinition> tous() {
        return parId.values();
    }

    public static List<EnemyDefinition> boss() {
        return bossList;
    }
}
