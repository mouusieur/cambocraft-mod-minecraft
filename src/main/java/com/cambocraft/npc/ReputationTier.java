package com.cambocraft.npc;

/**
 * Les cinq paliers de réputation auprès d'une faction (§6.2) et leurs conséquences.
 */
public enum ReputationTier {
    INCONNU(0, "inconnu", -10000, -1, true, false),
    CONNU(1, "connu", 0, 199, false, false),
    RESPECTE(2, "respecte", 200, 599, false, true),
    HONORE(3, "honore", 600, 1499, false, true),
    LEGENDAIRE(4, "legendaire", 1500, Integer.MAX_VALUE, false, true);

    public final int indice;
    public final String id;
    public final int seuilMin;
    public final int seuilMax;
    /** Vrai si les gardes se méfient du joueur et si les recettes avancées sont refusées. */
    public final boolean mefiance;
    /** Vrai si le joueur a accès aux spécialités culinaires et aux remises marchandes. */
    public final boolean accesSpecialites;

    ReputationTier(int indice, String id, int seuilMin, int seuilMax, boolean mefiance, boolean accesSpecialites) {
        this.indice = indice;
        this.id = id;
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
        this.mefiance = mefiance;
        this.accesSpecialites = accesSpecialites;
    }

    public static ReputationTier pourPoints(int points) {
        return values()[Economy.palier(points)];
    }

    public static ReputationTier parId(String id) {
        if (id == null) return CONNU;
        for (ReputationTier p : values()) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        return CONNU;
    }
}
