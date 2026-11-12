package com.cambocraft.combat;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;

/**
 * Spécification d'une phase de boss multi-phases (§4 de DESIGN.md).
 *
 * <p>Chacun des 12 boss de Cambocraft possède au minimum 3 phases distinctes
 * (4 phases pour les boss finaux de la zone 11 et de la zone 12).
 */
public record BossPhaseSpec(
        int numero,
        double seuilPvRatio,
        Localized titrePhase,
        double multiplicateurAttaque,
        double multiplicateurVitesse,
        String mecaniqueArene,
        List<String> capacitesPhase
) {
    public BossPhaseSpec {
        Objects.requireNonNull(titrePhase, "titrePhase");
        Objects.requireNonNull(mecaniqueArene, "mecaniqueArene");
        capacitesPhase = capacitesPhase == null ? List.of() : List.copyOf(capacitesPhase);
    }

    public static BossPhaseSpec fromJson(int defaultNum, JsonObject obj) {
        int num = JsonUtil.integer(obj, "phase", defaultNum, 1, 10);
        double seuil = JsonUtil.real(obj, "seuil_pv", 1.0, 0.01, 1.0);
        Localized titre = Localized.fromJson(obj.get("nom"), "Phase " + num);
        double multAtk = JsonUtil.real(obj, "mult_attaque", 1.0 + (num - 1) * 0.20, 0.5, 5.0);
        double multSpd = JsonUtil.real(obj, "mult_vitesse", 1.0 + (num - 1) * 0.10, 0.5, 3.0);
        String arene = JsonUtil.str(obj, "mecanique_arene", "standard");
        List<String> caps = JsonUtil.strings(obj, "capacites");
        return new BossPhaseSpec(num, seuil, titre, multAtk, multSpd, arene, caps);
    }
}
