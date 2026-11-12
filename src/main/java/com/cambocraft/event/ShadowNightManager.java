package com.cambocraft.event;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.data.DataManager;
import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.Seasons;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.cambocraft.util.Tick;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire des événements saisonniers dynamiques et de la **Nuit des Ombres**
 * (une fois par saison au 6e jour de la saison, de la tombée de la nuit à l'aube).
 */
public final class ShadowNightManager implements DataConsumer {
    public static final String ID_NUIT_DES_OMBRES = "nuit_des_ombres";
    public static final int JOUR_NUIT_DES_OMBRES = 6; // 6e jour (1..8) de chaque saison de 8 jours
    public static final long TICK_DEBUT_NUIT = 13000L;
    public static final long TICK_FIN_NUIT = 23000L;

    private static final ShadowNightManager INSTANCE = new ShadowNightManager();
    private static volatile Map<String, SeasonalEventDefinition> parId = Map.of();

    private ShadowNightManager() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    @Override
    public String nom() {
        return "ShadowNightManager";
    }

    @Override
    public void charger(DataBundle bundle) {
        Map<String, SeasonalEventDefinition> map = new LinkedHashMap<>();
        for (Map.Entry<String, com.google.gson.JsonObject> e : bundle.category("events").entrySet()) {
            String key = e.getKey();
            try {
                SeasonalEventDefinition def = SeasonalEventDefinition.fromJson(key, e.getValue());
                map.put(def.id(), def);
            } catch (RuntimeException ex) {
                bundle.error("events/" + key + ".json : " + ex.getMessage());
            }
        }
        parId = Collections.unmodifiableMap(map);
        Log.info("Événements saisonniers chargés : {}", parId.size());
    }

    /**
     * Indique si la Nuit des Ombres est active au tick monde {@code worldTick}.
     * Elle survient exactement une fois par saison (au 6e jour de la saison,
     * entre {@code 13000} et {@code 23000} ticks).
     */
    public static boolean estNuitDesOmbres(long worldTick) {
        int jourSaison = Seasons.jourDansSaison(worldTick);
        long heure = Math.floorMod(worldTick, (long) Tick.UN_DAY);
        return jourSaison == JOUR_NUIT_DES_OMBRES && heure >= TICK_DEBUT_NUIT && heure <= TICK_FIN_NUIT;
    }

    /**
     * Calcule le numéro de vague d'infiltrés (1 à 3) pendant la Nuit des Ombres.
     */
    public static int vagueNuitDesOmbres(long worldTick) {
        if (!estNuitDesOmbres(worldTick)) {
            return 0;
        }
        long heure = Math.floorMod(worldTick, (long) Tick.UN_DAY);
        long ecoule = heure - TICK_DEBUT_NUIT;
        if (ecoule < 3300L) {
            return 1;
        }
        if (ecoule < 6600L) {
            return 2;
        }
        return 3;
    }

    /**
     * Retourne la liste des événements saisonniers actifs au tick monde {@code worldTick}.
     */
    public static List<SeasonalEventDefinition> evenementsActifs(long worldTick) {
        Season s = Seasons.saison(worldTick);
        int jour = Seasons.jourDansSaison(worldTick);
        long heure = Math.floorMod(worldTick, (long) Tick.UN_DAY);
        List<SeasonalEventDefinition> actifs = new ArrayList<>();
        for (SeasonalEventDefinition def : parId.values()) {
            if (def.saisonCible() != null && def.saisonCible() != s) {
                continue;
            }
            if (def.jourDansSaison() != jour) {
                continue;
            }
            long fin = def.heureDebutTick() + def.dureeTicks();
            if (heure >= def.heureDebutTick() && heure <= fin) {
                actifs.add(def);
            }
        }
        return actifs;
    }

    public static SeasonalEventDefinition get(String id) {
        return parId.get(id);
    }

    public static Collection<SeasonalEventDefinition> tous() {
        return parId.values();
    }
}
