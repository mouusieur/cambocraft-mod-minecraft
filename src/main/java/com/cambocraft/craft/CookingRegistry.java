package com.cambocraft.craft;

import com.cambocraft.data.DataConsumer;
import com.cambocraft.data.DataManager;
import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Log;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registre rechargeable des recettes de cuisine khmère ({@code data/cambocraft/cambo/cooking/}).
 */
public final class CookingRegistry implements DataConsumer {
    private static final CookingRegistry INSTANCE = new CookingRegistry();
    private static volatile Map<String, CookingRecipeSpec> recettes = Map.of();

    private CookingRegistry() {}

    public static void init() {
        DataManager.enregistrer(INSTANCE);
    }

    public static Map<String, CookingRecipeSpec> recettes() {
        return recettes;
    }

    public static CookingRecipeSpec recette(String id) {
        return recettes.get(id);
    }

    @Override
    public String nom() {
        return "cuisine_khmere";
    }

    @Override
    public void charger(DataBundle bundle) {
        Map<String, CookingRecipeSpec> map = new LinkedHashMap<>();
        for (Map.Entry<String, JsonObject> e : bundle.category("cooking").entrySet()) {
            CookingRecipeSpec r = CookingRecipeSpec.fromJson(e.getKey(), e.getValue());
            if (r.ingredients().isEmpty()) {
                bundle.error("cooking/" + e.getKey() + " : liste d'ingrédients vide");
                continue;
            }
            map.put(e.getKey(), r);
        }
        recettes = Map.copyOf(map);
        Log.info("Cuisine khmère : {} recette(s) chargée(s)", recettes.size());
    }
}
