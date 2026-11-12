package com.cambocraft.fishing;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Espèces de poissons disponibles, rechargées depuis {@code data/cambocraft/cambo/fishing/}.
 *
 * <p>Les données sont validées au chargement : identifiant d'objet présent, poids positif,
 * intervalle de taille cohérent, zone connue. Une espèce invalide est écartée avec un message
 * explicite plutôt que de provoquer une erreur en jeu.</p>
 */
public final class FishingRegistry implements DataConsumer {
    /** Zones de pêche reconnues (voir docs/DESIGN.md). */
    public static final List<String> ZONES = List.of(
            "riviere", "grand_lac", "mangrove", "riziere", "etang",
            "abysses", "sanctuaire", "montagne_riviere");

    private static final FishingRegistry INSTANCE = new FishingRegistry();
    private static volatile FishingTable table = new FishingTable(List.of());
    private static volatile Map<String, FishSpecies> parId = Map.of();

    private FishingRegistry() {}

    public static void init() {
        com.cambocraft.data.DataManager.enregistrer(INSTANCE);
    }

    public static FishingTable table() {
        return table;
    }

    public static FishSpecies parId(String id) {
        return parId.get(id);
    }

    @Override
    public String nom() {
        return "pêche";
    }

    @Override
    public void charger(DataBundle bundle) {
        List<FishSpecies> especes = new ArrayList<>();
        Map<String, FishSpecies> index = new LinkedHashMap<>();
        for (Map.Entry<String, JsonObject> entree : bundle.category("fishing").entrySet()) {
            String id = entree.getKey();
            FishSpecies espece = FishSpecies.fromJson(id, entree.getValue());
            String erreur = valider(espece);
            if (erreur != null) {
                bundle.error("fishing/" + id + " : " + erreur);
                continue;
            }
            especes.add(espece);
            index.put(id, espece);
        }
        table = new FishingTable(especes);
        parId = Map.copyOf(index);
        Log.info("Pêche : {} espèce(s) chargée(s)", especes.size());
    }

    /** Renvoie un message d'erreur, ou {@code null} si l'espèce est valide. */
    static String valider(FishSpecies e) {
        if (e.weight() <= 0) return "poids nul ou négatif";
        if (e.minCm() <= 0 || e.maxCm() < e.minCm()) return "intervalle de taille incohérent";
        if (e.difficulty() < 0 || e.difficulty() > 1) return "difficulté hors de 0..1";
        if (e.price() <= 0) return "prix nul ou négatif";
        for (String zone : e.zones()) {
            if (!zone.equals("*") && !ZONES.contains(zone)) return "zone inconnue : " + zone;
        }
        return null;
    }
}
