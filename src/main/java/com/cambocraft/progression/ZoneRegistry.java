package com.cambocraft.progression;

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
 * Registre rechargeable des 12 zones de progression ({@code data/cambocraft/cambo/zones/}).
 */
public final class ZoneRegistry implements DataConsumer {
    private static final ZoneRegistry INSTANCE = new ZoneRegistry();
    private static volatile List<ZoneDefinition> ordonnees = List.of();
    private static volatile Map<String, ZoneDefinition> parId = Map.of();

    private ZoneRegistry() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    public static List<ZoneDefinition> zones() {
        return ordonnees;
    }

    public static ZoneDefinition zone(String id) {
        return parId.get(id);
    }

    public static ZoneDefinition parOrdre(int ordre) {
        for (ZoneDefinition z : ordonnees) {
            if (z.ordre() == ordre) return z;
        }
        return null;
    }

    @Override
    public String nom() {
        return "zones_progression";
    }

    @Override
    public void charger(DataBundle bundle) {
        List<ZoneDefinition> liste = new ArrayList<>();
        for (Map.Entry<String, JsonObject> e : bundle.category("zones").entrySet()) {
            ZoneDefinition z = ZoneDefinition.fromJson(e.getKey(), e.getValue());
            if (z.ordre() < 1 || z.ordre() > 12 || z.ennemis().size() < 4) {
                bundle.error("zones/" + e.getKey() + " : ordre hors 1..12 ou moins de 4 types d'ennemis");
                continue;
            }
            liste.add(z);
        }
        liste.sort(Comparator.comparingInt(ZoneDefinition::ordre));
        Map<String, ZoneDefinition> index = new LinkedHashMap<>();
        for (ZoneDefinition z : liste) index.put(z.id(), z);
        ordonnees = List.copyOf(liste);
        parId = Map.copyOf(index);
        Log.info("Zones : {} zone(s) chargée(s)", ordonnees.size());
    }
}
