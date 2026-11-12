package com.cambocraft.power;

import java.util.Locale;

/**
 * Les 8 archétypes fonctionnels de pouvoirs du Nen (§4 de DESIGN.md).
 */
public enum PowerType {
    OFFENSIF("offensif"),
    DEFENSIF("defensif"),
    MOBILITE("mobilite"),
    SOIN("soin"),
    CONTROLE("controle"),
    UTILITAIRE("utilitaire"),
    PASSIF("passif"),
    INVOCATION("invocation");

    private final String id;

    PowerType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static PowerType fromId(String raw) {
        if (raw == null) {
            return OFFENSIF;
        }
        String norm = raw.trim().toLowerCase(Locale.ROOT);
        for (PowerType t : values()) {
            if (t.id.equals(norm)) {
                return t;
            }
        }
        return OFFENSIF;
    }
}
