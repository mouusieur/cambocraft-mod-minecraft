package com.cambocraft.power;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Définition d'une synergie entre deux pouvoirs équipés simultanément dans le Carnet du Nen
 * ({@code data/cambocraft/cambo/synergies/<id>.json}).
 */
public final class SynergyDefinition {
    private final String id;
    private final Localized nom;
    private final String pouvoirA;
    private final String pouvoirB;
    private final String effet;
    private final double multiplicateur;
    private final int dureeTicks;
    private final Localized description;

    public SynergyDefinition(
            String id,
            Localized nom,
            String pouvoirA,
            String pouvoirB,
            String effet,
            double multiplicateur,
            int dureeTicks,
            Localized description
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.nom = Objects.requireNonNull(nom, "nom");
        this.pouvoirA = Objects.requireNonNull(pouvoirA, "pouvoirA");
        this.pouvoirB = Objects.requireNonNull(pouvoirB, "pouvoirB");
        if (pouvoirA.equals(pouvoirB)) {
            throw new IllegalArgumentException("Une synergie doit relier deux pouvoirs distincts: " + id);
        }
        this.effet = Objects.requireNonNull(effet, "effet");
        this.multiplicateur = multiplicateur;
        this.dureeTicks = Math.max(0, dureeTicks);
        this.description = Objects.requireNonNull(description, "description");
    }

    public String id() { return id; }
    public Localized nom() { return nom; }
    public String pouvoirA() { return pouvoirA; }
    public String pouvoirB() { return pouvoirB; }
    public String effet() { return effet; }
    public double multiplicateur() { return multiplicateur; }
    public int dureeTicks() { return dureeTicks; }
    public Localized description() { return description; }

    /**
     * Indique si cette synergie est active pour l'ensemble de pouvoirs équipés fourni.
     */
    public boolean estActivePour(Set<String> pouvoirsEquipes) {
        return pouvoirsEquipes != null
                && pouvoirsEquipes.contains(pouvoirA)
                && pouvoirsEquipes.contains(pouvoirB);
    }

    public static SynergyDefinition fromJson(String id, JsonObject obj) {
        Localized nom = Localized.fromJson(obj.get("nom"), id);
        List<String> paire = JsonUtil.strings(obj, "pouvoirs");
        if (paire.size() < 2) {
            throw new IllegalArgumentException("La synergie " + id + " doit lister 2 pouvoirs");
        }
        String effet = JsonUtil.str(obj, "effet", "resonance_spirituelle");
        double mult = JsonUtil.real(obj, "multiplicateur", 1.20, 1.0, 5.0);
        int duree = JsonUtil.integer(obj, "duree_ticks", 200, 0, 72000);
        Localized desc = Localized.fromJson(obj.get("description"), id);
        return new SynergyDefinition(id, nom, paire.get(0), paire.get(1), effet, mult, duree, desc);
    }
}
