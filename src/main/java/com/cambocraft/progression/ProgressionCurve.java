package com.cambocraft.progression;

/**
 * Courbe d'expérience des niveaux 1 à 200 (§12.1) et système de Renaissance (§12.4).
 *
 * <p>Formule officielle : {@code XP(n -> n+1) = round(120 * n^1.8)}.</p>
 */
public final class ProgressionCurve {
    public static final int NIVEAU_MIN = 1;
    public static final int NIVEAU_MAX = 200;
    public static final int RENAISSANCE_MAX = 5;

    public record EtatNiveau(int niveau, long xpDansNiveau, int niveauxGagnes) {}

    private ProgressionCurve() {}

    /** XP nécessaire pour passer du niveau {@code n} au niveau {@code n + 1}. */
    public static long xpPourNiveauSuivant(int n) {
        if (n < NIVEAU_MIN) n = NIVEAU_MIN;
        if (n >= NIVEAU_MAX) return 0L;
        return Math.round(120.0 * Math.pow(n, 1.8));
    }

    /** XP cumulée totale depuis le niveau 1 pour atteindre le début du niveau {@code cible}. */
    public static long xpCumuleePourAtteindre(int cible) {
        int borne = Math.max(NIVEAU_MIN, Math.min(NIVEAU_MAX, cible));
        long total = 0L;
        for (int n = 1; n < borne; n++) {
            total += xpPourNiveauSuivant(n);
        }
        return total;
    }

    /** Applique un gain d'XP et fait monter autant de niveaux que nécessaire jusqu'à 200. */
    public static EtatNiveau ajouterXp(int niveauActuel, long xpActuelle, long gain) {
        int niv = Math.max(NIVEAU_MIN, Math.min(NIVEAU_MAX, niveauActuel));
        long xp = Math.max(0L, xpActuelle) + Math.max(0L, gain);
        int gagnes = 0;
        while (niv < NIVEAU_MAX) {
            long requis = xpPourNiveauSuivant(niv);
            if (xp < requis) break;
            xp -= requis;
            niv++;
            gagnes++;
        }
        if (niv >= NIVEAU_MAX) {
            xp = 0L;
        }
        return new EtatNiveau(niv, xp, gagnes);
    }

    /** Points de vie supplémentaires conférés par le niveau (+0,5 cœur tous les 5 niveaux). */
    public static double pvBonus(int niveau, int renaissances) {
        int n = Math.max(NIVEAU_MIN, Math.min(NIVEAU_MAX, niveau));
        int r = Math.max(0, Math.min(RENAISSANCE_MAX, renaissances));
        return (n - 1) * 0.25 + r * 4.0;
    }

    /** Bonus de dégâts de base lié au niveau et au rang de Renaissance. */
    public static double multiplicateurPuissance(int niveau, int renaissances) {
        int n = Math.max(NIVEAU_MIN, Math.min(NIVEAU_MAX, niveau));
        int r = Math.max(0, Math.min(RENAISSANCE_MAX, renaissances));
        return (1.0 + (n - 1) * 0.015) * (1.0 + r * 0.05);
    }

    /** Énergie spirituelle maximale du Nen (base 100 + 2 par niveau + 25 par Renaissance). */
    public static int energieMax(int niveau, int renaissances) {
        int n = Math.max(NIVEAU_MIN, Math.min(NIVEAU_MAX, niveau));
        int r = Math.max(0, Math.min(RENAISSANCE_MAX, renaissances));
        return 100 + (n - 1) * 2 + r * 25;
    }

    /** Indique si le joueur remplit les conditions pour accomplir une Renaissance à la Pyramide. */
    public static boolean peutRenaitre(int niveau, int renaissances, boolean gardienEquilibreVaincu) {
        return niveau >= NIVEAU_MAX && renaissances < RENAISSANCE_MAX && gardienEquilibreVaincu;
    }
}
