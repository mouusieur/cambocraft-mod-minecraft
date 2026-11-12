package com.cambocraft.power;

import java.util.Locale;

/**
 * Les 5 raretés de pouvoirs du Nen (§5.2 de DESIGN.md).
 *
 * <ul>
 *   <li>Commun : 55,0 % (minimum 20 pouvoirs au catalogue)</li>
 *   <li>Rare : 28,0 % (minimum 18 pouvoirs)</li>
 *   <li>Épique : 12,0 % (minimum 18 pouvoirs, garanti au 30e tirage)</li>
 *   <li>Légendaire : 4,5 % (minimum 14 pouvoirs, garanti au 80e tirage)</li>
 *   <li>Mythique : 0,5 % (minimum 10 pouvoirs, garanti au 250e tirage)</li>
 * </ul>
 */
public enum PowerRarity {
    COMMUN("commun", 0.550, 1.00, 0xD8D3C8),
    RARE("rare", 0.280, 1.25, 0x3A8FD9),
    EPIQUE("epique", 0.120, 1.60, 0x9B4DCA),
    LEGENDAIRE("legendaire", 0.045, 2.10, 0xE6A817),
    MYTHIQUE("mythique", 0.005, 2.85, 0xE83B46);

    private final String id;
    private final double probabiliteBase;
    private final double multiplicateurPuissance;
    private final int couleurRgb;

    PowerRarity(String id, double probabiliteBase, double multiplicateurPuissance, int couleurRgb) {
        this.id = id;
        this.probabiliteBase = probabiliteBase;
        this.multiplicateurPuissance = multiplicateurPuissance;
        this.couleurRgb = couleurRgb;
    }

    public String id() {
        return id;
    }

    public double probabiliteBase() {
        return probabiliteBase;
    }

    public double multiplicateurPuissance() {
        return multiplicateurPuissance;
    }

    public int couleurRgb() {
        return couleurRgb;
    }

    public boolean auMoins(PowerRarity autre) {
        return this.ordinal() >= autre.ordinal();
    }

    public static PowerRarity fromId(String raw) {
        if (raw == null) {
            return COMMUN;
        }
        String norm = raw.trim().toLowerCase(Locale.ROOT);
        for (PowerRarity r : values()) {
            if (r.id.equals(norm)) {
                return r;
            }
        }
        return COMMUN;
    }
}
