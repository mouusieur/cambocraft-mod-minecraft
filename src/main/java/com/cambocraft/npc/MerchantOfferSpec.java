package com.cambocraft.npc;

import com.cambocraft.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Catalogue d'un marchand PNJ (§6.3) : offres d'achat et de rachat libellées en Pièces du Lotus,
 * soumises au palier de réputation et à l'indice d'inflation du riz.
 */
public record MerchantOfferSpec(
        String item,
        int quantite,
        int prixBase,
        ReputationTier palierMin,
        boolean sensibleAuRiz) {

    public boolean debloquee(int reputationPoints) {
        return ReputationTier.pourPoints(reputationPoints).indice >= palierMin.indice;
    }

    public int prixAchatEffectif(int reputationPoints, double indiceRiz) {
        double idx = sensibleAuRiz ? indiceRiz : 1.0;
        return Economy.prixAchat(prixBase, reputationPoints, idx);
    }

    public int prixVenteEffectif(int reputationPoints, double indiceRiz) {
        double idx = sensibleAuRiz ? indiceRiz : 1.0;
        return Economy.prixVente(prixBase, reputationPoints, idx);
    }

    public record Catalogue(String id, String pnj, Faction faction, List<MerchantOfferSpec> offres) {
        public static Catalogue fromJson(String id, JsonObject o) {
            String pnj = JsonUtil.str(o, "pnj", id);
            Faction faction = Faction.parId(JsonUtil.str(o, "faction", "tonle"));
            List<MerchantOfferSpec> liste = new ArrayList<>();
            JsonArray arr = o.getAsJsonArray("offres");
            if (arr != null) {
                for (JsonElement el : arr) {
                    if (!el.isJsonObject()) continue;
                    JsonObject of = el.getAsJsonObject();
                    liste.add(new MerchantOfferSpec(
                            JsonUtil.str(of, "item", "cambocraft:riz_brut"),
                            Math.max(1, JsonUtil.integer(of, "quantite", 1)),
                            Math.max(1, JsonUtil.integer(of, "prix_base", 5)),
                            ReputationTier.parId(JsonUtil.str(of, "palier_min", "connu")),
                            JsonUtil.bool(of, "sensible_au_riz", false)));
                }
            }
            return new Catalogue(id, pnj, faction, List.copyOf(liste));
        }
    }
}
