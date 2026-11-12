package com.cambocraft.power;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.data.DataManager;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registre rechargeable des 80+ pouvoirs du Nen ({@code powers/*.json})
 * et des 30+ synergies ({@code synergies/*.json}).
 */
public final class PowerRegistry implements DataConsumer {
    private static final PowerRegistry INSTANCE = new PowerRegistry();

    private static volatile Map<String, PowerDefinition> parId = Map.of();
    private static volatile Map<PowerRarity, List<PowerDefinition>> parRarete = Map.of();
    private static volatile Map<String, SynergyDefinition> synergiesParId = Map.of();

    private PowerRegistry() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    @Override
    public String nom() {
        return "PowerRegistry";
    }

    @Override
    public void charger(DataBundle bundle) {
        Map<String, PowerDefinition> mapP = new LinkedHashMap<>();
        Map<PowerRarity, List<PowerDefinition>> mapR = new EnumMap<>(PowerRarity.class);
        for (PowerRarity r : PowerRarity.values()) {
            mapR.put(r, new ArrayList<>());
        }

        for (Map.Entry<String, com.google.gson.JsonObject> e : bundle.category("powers").entrySet()) {
            String key = e.getKey();
            try {
                PowerDefinition def = PowerDefinition.fromJson(key, e.getValue());
                mapP.put(def.id(), def);
                mapR.get(def.rarete()).add(def);
            } catch (RuntimeException ex) {
                bundle.error("powers/" + key + ".json : " + ex.getMessage());
            }
        }

        Map<PowerRarity, List<PowerDefinition>> figeeR = new EnumMap<>(PowerRarity.class);
        for (Map.Entry<PowerRarity, List<PowerDefinition>> entry : mapR.entrySet()) {
            figeeR.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }

        Map<String, SynergyDefinition> mapS = new LinkedHashMap<>();
        for (Map.Entry<String, com.google.gson.JsonObject> e : bundle.category("synergies").entrySet()) {
            String key = e.getKey();
            try {
                SynergyDefinition syn = SynergyDefinition.fromJson(key, e.getValue());
                mapS.put(syn.id(), syn);
            } catch (RuntimeException ex) {
                bundle.error("synergies/" + key + ".json : " + ex.getMessage());
            }
        }

        parId = Collections.unmodifiableMap(mapP);
        parRarete = Collections.unmodifiableMap(figeeR);
        synergiesParId = Collections.unmodifiableMap(mapS);
        Log.info("Pouvoirs du Nen chargés : {} pouvoirs, {} synergies", parId.size(), synergiesParId.size());
    }

    public static PowerDefinition get(String id) {
        return parId.get(id);
    }

    public static Collection<PowerDefinition> tous() {
        return parId.values();
    }

    public static List<PowerDefinition> parRarete(PowerRarity rarete) {
        return parRarete.getOrDefault(rarete, List.of());
    }

    public static SynergyDefinition getSynergie(String id) {
        return synergiesParId.get(id);
    }

    public static Collection<SynergyDefinition> toutesSynergies() {
        return synergiesParId.values();
    }
}
