package com.cambocraft.fishing;

/** Rareté d'une prise : pilote le poids de tirage, la valeur de vente et la difficulté du mini-jeu. */
public enum FishRarity {
    COMMUN(1.00, 1.0),
    PEU_COMMUN(1.35, 1.15),
    RARE(2.20, 1.35),
    EPIQUE(4.00, 1.60);

    /** Multiplicateur de prix de vente (voir docs/RECETTES.md, table des prisées). */
    public final double priceMultiplier;
    /** Multiplicateur de difficulté du mini-jeu. */
    public final double difficultyMultiplier;

    FishRarity(double priceMultiplier, double difficultyMultiplier) {
        this.priceMultiplier = priceMultiplier;
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public static FishRarity parse(String value, FishRarity def) {
        if (value == null) return def;
        return switch (value.toLowerCase(java.util.Locale.ROOT)) {
            case "commun", "common" -> COMMUN;
            case "peu_commun", "uncommon" -> PEU_COMMUN;
            case "rare" -> RARE;
            case "epique", "epic" -> EPIQUE;
            default -> def;
        };
    }
}
