package com.cambocraft.npc;

/**
 * Calculs économiques : prix d'achat, prix de vente, inflation du riz.
 *
 * <p>Toutes les formules sont des fonctions pures, testées dans
 * {@code src/test/java/com/cambocraft/npc/EconomyTests.java} et documentées dans
 * {@code docs/RECETTES.md} (table des prix) et {@code docs/PROBABILITES.md} (inflation).</p>
 *
 * <p>Principes :</p>
 * <ul>
 *   <li>la réputation fait baisser les prix d'achat jusqu'à 20 % au niveau « légendaire » et
 *       monter les prix de vente jusqu'à 25 % ;</li>
 *   <li>une mauvaise réputation renchérit les achats (+30 % au niveau « inconnu ») ;</li>
 *   <li>l'indice du riz (0,5 à 2,0) module les prix des denrées : une bonne récolte fait baisser
 *       les prix, une mauvaise les fait monter.</li>
 * </ul>
 */
public final class Economy {
    /** Multiplicateurs d'achat par palier de réputation (inconnu → légendaire). */
    public static final double[] MULT_ACHAT = {1.30, 1.10, 1.00, 0.92, 0.80};
    /** Multiplicateurs de vente par palier de réputation. */
    public static final double[] MULT_VENTE = {0.75, 0.90, 1.00, 1.10, 1.25};
    /** Bornes de l'indice du riz. */
    public static final double INDICE_RIZ_MIN = 0.5;
    public static final double INDICE_RIZ_MAX = 2.0;

    private Economy() {}

    public static int palier(int reputation) {
        if (reputation < 0) return 0;
        if (reputation < 200) return 1;
        if (reputation < 600) return 2;
        if (reputation < 1500) return 3;
        return 4;
    }

    /** Prix d'achat : base × palier × inflation du riz (arrondi au supérieur). */
    public static int prixAchat(int prixBase, int reputation, double indiceRiz) {
        double prix = prixBase * MULT_ACHAT[palier(reputation)] * inflation(indiceRiz);
        return Math.max(1, (int) Math.ceil(prix));
    }

    /** Prix de vente : base × palier × inflation, arrondi à l'inférieur mais jamais nul. */
    public static int prixVente(int prixBase, int reputation, double indiceRiz) {
        double prix = prixBase * MULT_VENTE[palier(reputation)] * inflation(indiceRiz);
        return Math.max(1, (int) Math.floor(prix));
    }

    /**
     * Facteur d'inflation lié à l'indice du riz : indice 1 = neutre ; un indice de 0,5 (récolte
     * exceptionnelle) baisse les prix de 20 %, un indice de 2,0 (famine) les monte de 60 %.
     */
    public static double inflation(double indiceRiz) {
        double indice = Math.max(INDICE_RIZ_MIN, Math.min(INDICE_RIZ_MAX, indiceRiz));
        return 1.0 + 0.4 * (indice - 1.0) + 0.2 * Math.max(0, indice - 1.0);
    }

    /**
     * Indice du riz déduit du rendement de la dernière récolte.
     *
     * @param rendement récolte observée par hectare de rizière (base 100)
     */
    public static double indiceRiz(double rendement) {
        double ratio = Math.max(0.25, rendement / 100.0);
        return Math.max(INDICE_RIZ_MIN, Math.min(INDICE_RIZ_MAX, 1.0 / ratio));
    }

    /** Somme d'une liste de pièces, plafonnée (évite les débordements d'inventaire). */
    public static long ajouter(long solde, long montant) {
        long total = solde + montant;
        return Math.max(0, Math.min(9_000_000_000L, total));
    }
}
