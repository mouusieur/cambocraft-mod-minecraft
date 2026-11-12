package com.cambocraft.npc;

/**
 * Les quatre factions régionales de Cambocraft (§6.2).
 */
public enum Faction {
    TONLE("tonle", "Province Fluviale de Tonlé", "Tonle River Province"),
    LY("ly", "Terrasses Rizicoles de Ly", "Ly Rice Terraces"),
    SROK("srok", "Marches Forestières de Srok", "Srok Forest Marches"),
    KOMPONG("kompong", "Cités Sacrées de Kompong", "Kompong Sacred Cities");

    public final String id;
    public final String nomFr;
    public final String nomEn;

    Faction(String id, String nomFr, String nomEn) {
        this.id = id;
        this.nomFr = nomFr;
        this.nomEn = nomEn;
    }

    public static Faction parId(String id) {
        if (id == null) return TONLE;
        for (Faction f : values()) {
            if (f.id.equalsIgnoreCase(id)) return f;
        }
        return TONLE;
    }
}
