package com.cambocraft.power;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Définition immuable d'un pouvoir du Nen chargée depuis {@code data/cambocraft/cambo/powers/<id>.json}.
 *
 * <p>Chaque pouvoir comporte exactement 5 paliers d'évolution (niveaux 1 à 5) et une
 * spécification de Maîtrise débloquée après un seuil d'utilisations en jeu.
 */
public final class PowerDefinition {
    public record LevelStats(
            int niveau,
            double degats,
            double soin,
            double portee,
            int dureeTicks,
            int rechargeTicks,
            int coutEnergie
    ) {}

    public record MasterySpec(
            int seuilUtilisations,
            Localized variante,
            double bonusMultiplicateur
    ) {}

    private final String id;
    private final Localized nom;
    private final Localized description;
    private final PowerRarity rarete;
    private final PowerElement element;
    private final PowerType type;
    private final List<LevelStats> niveaux;
    private final MasterySpec maitrise;

    public PowerDefinition(
            String id,
            Localized nom,
            Localized description,
            PowerRarity rarete,
            PowerElement element,
            PowerType type,
            List<LevelStats> niveaux,
            MasterySpec maitrise
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.nom = Objects.requireNonNull(nom, "nom");
        this.description = Objects.requireNonNull(description, "description");
        this.rarete = Objects.requireNonNull(rarete, "rarete");
        this.element = Objects.requireNonNull(element, "element");
        this.type = Objects.requireNonNull(type, "type");
        if (niveaux == null || niveaux.size() != 5) {
            throw new IllegalArgumentException("Un pouvoir doit définir exactement 5 niveaux: " + id);
        }
        this.niveaux = List.copyOf(niveaux);
        this.maitrise = Objects.requireNonNull(maitrise, "maitrise");
    }

    public String id() { return id; }
    public Localized nom() { return nom; }
    public Localized description() { return description; }
    public PowerRarity rarete() { return rarete; }
    public PowerElement element() { return element; }
    public PowerType type() { return type; }
    public List<LevelStats> niveaux() { return niveaux; }
    public MasterySpec maitrise() { return maitrise; }

    /**
     * Retourne les statistiques du palier {@code 1..5} (borné automatiquement).
     */
    public LevelStats statsPourNiveau(int niveau) {
        int idx = Math.max(0, Math.min(4, niveau - 1));
        return niveaux.get(idx);
    }

    public static PowerDefinition fromJson(String id, JsonObject obj) {
        Localized nom = Localized.fromJson(obj.get("nom"), id);
        Localized description = Localized.fromJson(obj.get("description"), id);
        PowerRarity rarete = PowerRarity.fromId(JsonUtil.str(obj, "rarete", "commun"));
        PowerElement element = PowerElement.fromId(JsonUtil.str(obj, "element", "eau"));
        PowerType type = PowerType.fromId(JsonUtil.str(obj, "type", "offensif"));

        List<LevelStats> lvls = new ArrayList<>(5);
        JsonArray arr = JsonUtil.arr(obj, "niveaux");
        if (arr != null) {
            int idx = 1;
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject lo = el.getAsJsonObject();
                lvls.add(new LevelStats(
                        JsonUtil.integer(lo, "niveau", idx, 1, 5),
                        JsonUtil.real(lo, "degats", 0.0, 0.0, 10000.0),
                        JsonUtil.real(lo, "soin", 0.0, 0.0, 10000.0),
                        JsonUtil.real(lo, "portee", 4.0, 0.0, 256.0),
                        JsonUtil.integer(lo, "duree_ticks", 0, 0, 72000),
                        JsonUtil.integer(lo, "recharge_ticks", 40, 1, 72000),
                        JsonUtil.integer(lo, "cout_energie", 15, 0, 1000)
                ));
                idx++;
            }
        }
        while (lvls.size() < 5) {
            int n = lvls.size() + 1;
            lvls.add(new LevelStats(n, 6.0 * n, 0.0, 6.0, 60, 60, 20));
        }

        JsonObject mo = JsonUtil.obj(obj, "maitrise");
        int seuil = mo != null ? JsonUtil.integer(mo, "seuil", 100, 1, 100000) : 100;
        String varFr = mo != null ? JsonUtil.str(mo, "variante_fr", nom.fr() + " — Maîtrise") : nom.fr();
        String varEn = mo != null ? JsonUtil.str(mo, "variante_en", nom.en() + " — Mastery") : nom.en();
        double bonus = mo != null ? JsonUtil.real(mo, "bonus", 0.20, 0.0, 5.0) : 0.20;
        MasterySpec mastery = new MasterySpec(seuil, new Localized(varFr, varEn), bonus);

        return new PowerDefinition(id, nom, description, rarete, element, type, lvls.subList(0, 5), mastery);
    }
}
