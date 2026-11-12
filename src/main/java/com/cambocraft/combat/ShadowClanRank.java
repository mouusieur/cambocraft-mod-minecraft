package com.cambocraft.combat;

import java.util.Locale;

/**
 * Hiérarchie du Clan Ombre (§2 de DESIGN.md).
 */
public enum ShadowClanRank {
    INITIE("initie", 1, 1.00),
    LAME("lame", 2, 1.25),
    ARCHER("archer", 2, 1.20),
    MAITRE_SHURIKEN("maitre_shuriken", 3, 1.55),
    OMBRE_SILENCIEUSE("ombre_silencieuse", 4, 1.95),
    ARCHONTE("archonte", 5, 2.50),
    SEIGNEUR_BOSS("seigneur_boss", 6, 3.40);

    private final String id;
    private final int echelon;
    private final double multiplicateurStats;

    ShadowClanRank(String id, int echelon, double multiplicateurStats) {
        this.id = id;
        this.echelon = echelon;
        this.multiplicateurStats = multiplicateurStats;
    }

    public String id() { return id; }
    public int echelon() { return echelon; }
    public double multiplicateurStats() { return multiplicateurStats; }

    public static ShadowClanRank fromId(String raw) {
        if (raw == null) {
            return INITIE;
        }
        String norm = raw.trim().toLowerCase(Locale.ROOT);
        for (ShadowClanRank r : values()) {
            if (r.id.equals(norm)) {
                return r;
            }
        }
        return INITIE;
    }
}
