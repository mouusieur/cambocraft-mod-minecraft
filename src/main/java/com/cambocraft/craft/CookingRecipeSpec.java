package com.cambocraft.craft;

import com.cambocraft.npc.Faction;
import com.cambocraft.npc.ReputationTier;
import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonObject;
import java.util.List;

/**
 * Recette de cuisine khmère (§7.3) : ingrédients requis, plat obtenu, palier de réputation
 * minimal auprès de la faction locale et buff temporaire conféré à la dégustation.
 */
public record CookingRecipeSpec(
        String id,
        Localized nom,
        String resultat,
        List<String> ingredients,
        Faction faction,
        ReputationTier palierMin,
        String buffEffet,
        int buffAmplificateur,
        int buffDureeTicks) {

    public boolean debloquee(int reputationPoints) {
        return ReputationTier.pourPoints(reputationPoints).indice >= palierMin.indice;
    }

    public boolean peutCuisiner(List<String> inventaireIngredients, int reputationPoints) {
        if (!debloquee(reputationPoints)) return false;
        for (String req : ingredients) {
            if (!inventaireIngredients.contains(req)) return false;
        }
        return true;
    }

    public static CookingRecipeSpec fromJson(String id, JsonObject o) {
        return new CookingRecipeSpec(
                id,
                Localized.fromJson(o.get("nom"), id),
                JsonUtil.str(o, "resultat", "cambocraft:" + id),
                JsonUtil.strings(o, "ingredients"),
                Faction.parId(JsonUtil.str(o, "faction", "tonle")),
                ReputationTier.parId(JsonUtil.str(o, "palier_min", "connu")),
                JsonUtil.str(o, "buff_effet", "minecraft:regeneration"),
                Math.max(0, JsonUtil.integer(o, "buff_amplificateur", 0)),
                Math.max(20, JsonUtil.integer(o, "buff_duree_ticks", 2400)));
    }
}
