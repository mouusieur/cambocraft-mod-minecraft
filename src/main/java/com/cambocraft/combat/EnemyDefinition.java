package com.cambocraft.combat;

import com.cambocraft.power.PowerElement;
import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Définition d'un ennemi de zone, d'un membre du Clan Ombre ou d'un boss multi-phases
 * chargée depuis {@code data/cambocraft/cambo/enemies/<id>.json}.
 */
public final class EnemyDefinition {
    private final String id;
    private final Localized nom;
    private final String categorie; // zone | ninja | boss
    private final ShadowClanRank rangOmbre;
    private final PowerElement element;
    private final double pvBase;
    private final double attaqueBase;
    private final double armureBase;
    private final double vitesse;
    private final int xpRecompense;
    private final int piecesRecompense;
    private final List<String> capacites;
    private final List<BossPhaseSpec> phases;

    public EnemyDefinition(
            String id,
            Localized nom,
            String categorie,
            ShadowClanRank rangOmbre,
            PowerElement element,
            double pvBase,
            double attaqueBase,
            double armureBase,
            double vitesse,
            int xpRecompense,
            int piecesRecompense,
            List<String> capacites,
            List<BossPhaseSpec> phases
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.nom = Objects.requireNonNull(nom, "nom");
        this.categorie = Objects.requireNonNull(categorie, "categorie");
        this.rangOmbre = Objects.requireNonNull(rangOmbre, "rangOmbre");
        this.element = Objects.requireNonNull(element, "element");
        this.pvBase = Math.max(1.0, pvBase);
        this.attaqueBase = Math.max(0.5, attaqueBase);
        this.armureBase = Math.max(0.0, armureBase);
        this.vitesse = Math.max(0.05, vitesse);
        this.xpRecompense = Math.max(0, xpRecompense);
        this.piecesRecompense = Math.max(0, piecesRecompense);
        this.capacites = List.copyOf(capacites);
        this.phases = List.copyOf(phases);
        if ("boss".equals(categorie) && this.phases.size() < 3) {
            throw new IllegalArgumentException("Un boss doit posséder au moins 3 phases: " + id);
        }
    }

    public String id() { return id; }
    public Localized nom() { return nom; }
    public String categorie() { return categorie; }
    public boolean estBoss() { return "boss".equals(categorie); }
    public boolean estNinja() { return "ninja".equals(categorie); }
    public ShadowClanRank rangOmbre() { return rangOmbre; }
    public PowerElement element() { return element; }
    public double pvBase() { return pvBase; }
    public double attaqueBase() { return attaqueBase; }
    public double armureBase() { return armureBase; }
    public double vitesse() { return vitesse; }
    public int xpRecompense() { return xpRecompense; }
    public int piecesRecompense() { return piecesRecompense; }
    public List<String> capacites() { return capacites; }
    public List<BossPhaseSpec> phases() { return phases; }

    public static EnemyDefinition fromJson(String id, JsonObject obj) {
        Localized nom = Localized.fromJson(obj.get("nom"), id);
        String categorie = JsonUtil.str(obj, "categorie", "zone");
        ShadowClanRank rang = ShadowClanRank.fromId(JsonUtil.str(obj, "rang", "initie"));
        PowerElement element = PowerElement.fromId(JsonUtil.str(obj, "element", "terre"));
        double pv = JsonUtil.real(obj, "pv_base", 40.0, 1.0, 1_000_000.0);
        double atk = JsonUtil.real(obj, "attaque_base", 6.0, 0.5, 100_000.0);
        double arm = JsonUtil.real(obj, "armure_base", 2.0, 0.0, 1000.0);
        double spd = JsonUtil.real(obj, "vitesse", 0.28, 0.05, 2.0);
        int xp = JsonUtil.integer(obj, "xp", 50, 0, 10_000_000);
        int pieces = JsonUtil.integer(obj, "pieces", 10, 0, 1_000_000);
        List<String> caps = JsonUtil.strings(obj, "capacites");

        List<BossPhaseSpec> phases = new ArrayList<>();
        JsonArray arr = JsonUtil.arr(obj, "phases");
        if (arr != null) {
            int idx = 1;
            for (JsonElement el : arr) {
                if (el.isJsonObject()) {
                    phases.add(BossPhaseSpec.fromJson(idx, el.getAsJsonObject()));
                    idx++;
                }
            }
        }
        return new EnemyDefinition(id, nom, categorie, rang, element, pv, atk, arm, spd, xp, pieces, caps, phases);
    }
}
