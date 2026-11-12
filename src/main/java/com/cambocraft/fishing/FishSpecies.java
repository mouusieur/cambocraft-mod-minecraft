package com.cambocraft.fishing;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonObject;
import java.util.List;

/**
 * Espèce de poisson de Cambocraft, décrite dans {@code data/cambocraft/cambo/fishing/species/*.json}.
 *
 * @param id        identifiant du fichier (sans extension)
 * @param names     noms localisés
 * @param itemId    identifiant d'objet remis au joueur (par exemple {@code cambocraft:tilapia})
 * @param rarity    rareté (poids et prix)
 * @param weight    poids de tirage de base, avant ajustement de rareté
 * @param zones     zones de pêche où l'espèce mord (voir docs/DESIGN.md)
 * @param seasons   saisons où l'espèce est active, {@code *} pour toutes
 * @param nocturnal vrai si l'espèce ne mord que la nuit
 * @param minCm     taille minimale (centimètres, purement descriptive)
 * @param maxCm     taille maximale
 * @param difficulty difficulté de base du mini-jeu (0 = très facile, 1 = très difficile)
 * @param price     prix de vente de base en pièces du lotus
 */
public record FishSpecies(
        String id,
        Localized names,
        String itemId,
        FishRarity rarity,
        int weight,
        List<String> zones,
        List<String> seasons,
        boolean nocturnal,
        int minCm,
        int maxCm,
        double difficulty,
        int price) {

    public static FishSpecies fromJson(String id, JsonObject json) {
        double min = JsonUtil.num(json, "min_cm", 10);
        double max = JsonUtil.num(json, "max_cm", Math.max(min + 1, 20));
        return new FishSpecies(
                id,
                Localized.fromJson(json.get("nom")),
                JsonUtil.str(json, "item", "cambocraft:poisson_tilapia"),
                FishRarity.parse(JsonUtil.str(json, "rarete", "commun"), FishRarity.COMMUN),
                Math.max(1, JsonUtil.integer(json, "poids", 10)),
                JsonUtil.strList(json, "zones"),
                JsonUtil.strList(json, "saisons").isEmpty() ? List.of("*") : JsonUtil.strList(json, "saisons"),
                JsonUtil.bool(json, "nocturne", false),
                (int) Math.round(min),
                (int) Math.round(max),
                Math.max(0, Math.min(1, JsonUtil.num(json, "difficulte", 0.3))),
                Math.max(1, JsonUtil.integer(json, "prix", 4)));
    }

    public boolean activeDans(String zone, String saison, boolean jour) {
        boolean zoneOk = zones.isEmpty() || zones.contains(zone) || zones.contains("*");
        boolean saisonOk = seasons.contains("*") || seasons.contains(saison);
        boolean heureOk = !nocturnal || !jour;
        return zoneOk && saisonOk && heureOk;
    }

    /** Prix de vente : base × rareté × taille relative (une grosse prise vaut plus cher). */
    public int prixDeVente(int tailleCm) {
        double span = Math.max(1, maxCm - minCm);
        double taille = Math.max(0, Math.min(1, (tailleCm - minCm) / span));
        return (int) Math.round(price * rarity.priceMultiplier * (0.8 + 0.6 * taille));
    }
}
