package com.cambocraft.power;

import java.util.Locale;

/**
 * Les 7 éléments spirituels du Nen dans Cambocraft.
 *
 * <p>Cycle élémentaire :
 * <ul>
 *   <li>Eau bat Feu (+25 %)</li>
 *   <li>Feu bat Air (+25 %)</li>
 *   <li>Air bat Terre (+25 %)</li>
 *   <li>Terre bat Eau (+25 %)</li>
 *   <li>Vie soutient Eau/Terre et purifie Ombre (+20 % contre Ombre)</li>
 *   <li>Ombre et Lumière s'opposent mutuellement (+20 % dans les deux sens)</li>
 * </ul>
 */
public enum PowerElement {
    EAU("eau"),
    FEU("feu"),
    AIR("air"),
    TERRE("terre"),
    OMBRE("ombre"),
    LUMIERE("lumiere"),
    VIE("vie");

    private final String id;

    PowerElement(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    /**
     * Multiplicateur d'affinité élémentaire de {@code attaquant} contre {@code defenseur}.
     */
    public double multiplicateurContre(PowerElement defenseur) {
        if (defenseur == null || defenseur == this) {
            return 1.0;
        }
        return switch (this) {
            case EAU -> defenseur == FEU ? 1.25 : (defenseur == TERRE ? 0.85 : 1.0);
            case FEU -> defenseur == AIR ? 1.25 : (defenseur == EAU ? 0.85 : 1.0);
            case AIR -> defenseur == TERRE ? 1.25 : (defenseur == FEU ? 0.85 : 1.0);
            case TERRE -> defenseur == EAU ? 1.25 : (defenseur == AIR ? 0.85 : 1.0);
            case OMBRE -> defenseur == LUMIERE ? 1.20 : (defenseur == VIE ? 0.90 : 1.0);
            case LUMIERE -> defenseur == OMBRE ? 1.20 : 1.0;
            case VIE -> defenseur == OMBRE ? 1.20 : 1.0;
        };
    }

    public static PowerElement fromId(String raw) {
        if (raw == null) {
            return EAU;
        }
        String norm = raw.trim().toLowerCase(Locale.ROOT);
        for (PowerElement e : values()) {
            if (e.id.equals(norm)) {
                return e;
            }
        }
        return EAU;
    }
}
