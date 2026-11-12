package com.cambocraft.companion;

import com.cambocraft.util.Localized;
import java.util.Objects;

/**
 * Compagnon spirituel **Neak Ta** (esprit gardien tutélaire du terroir cambodgien)
 * doté de **3 stades d'évolution** (§2 de DESIGN.md).
 */
public final class NeakTaCompanion {
    public enum EvolutionStage {
        STADE_1_LUEUR_DU_SANCTUAIRE(
                1,
                new Localized("Lueur du Sanctuaire", "Sanctuary Wisp"),
                0,
                0.15,
                0.00,
                false
        ),
        STADE_2_GARDIEN_DE_GRES(
                2,
                new Localized("Gardien de Grès", "Sandstone Guardian"),
                300,
                0.25,
                0.08,
                false
        ),
        STADE_3_AVATAR_ANCESTRAL_DU_LOTUS(
                3,
                new Localized("Avatar Ancestral du Lotus", "Ancestral Lotus Avatar"),
                900,
                0.40,
                0.15,
                true
        );

        private final int numero;
        private final Localized nom;
        private final int affiniteRequise;
        private final double bonusRegenNen;
        private final double reductionRechargeNen;
        private final boolean sauvetageCoupFatalQuotidien;

        EvolutionStage(
                int numero,
                Localized nom,
                int affiniteRequise,
                double bonusRegenNen,
                double reductionRechargeNen,
                boolean sauvetageCoupFatalQuotidien
        ) {
            this.numero = numero;
            this.nom = nom;
            this.affiniteRequise = affiniteRequise;
            this.bonusRegenNen = bonusRegenNen;
            this.reductionRechargeNen = reductionRechargeNen;
            this.sauvetageCoupFatalQuotidien = sauvetageCoupFatalQuotidien;
        }

        public int numero() { return numero; }
        public Localized nom() { return nom; }
        public int affiniteRequise() { return affiniteRequise; }
        public double bonusRegenNen() { return bonusRegenNen; }
        public double reductionRechargeNen() { return reductionRechargeNen; }
        public boolean sauvetageCoupFatalQuotidien() { return sauvetageCoupFatalQuotidien; }
    }

    private int pointsAffinite;
    private long dernierSauvetageJour = -1L;

    public NeakTaCompanion() {
        this(0);
    }

    public NeakTaCompanion(int pointsAffiniteInitiaux) {
        this.pointsAffinite = Math.max(0, pointsAffiniteInitiaux);
    }

    public int pointsAffinite() {
        return pointsAffinite;
    }

    public EvolutionStage stadeActuel() {
        if (pointsAffinite >= EvolutionStage.STADE_3_AVATAR_ANCESTRAL_DU_LOTUS.affiniteRequise()) {
            return EvolutionStage.STADE_3_AVATAR_ANCESTRAL_DU_LOTUS;
        }
        if (pointsAffinite >= EvolutionStage.STADE_2_GARDIEN_DE_GRES.affiniteRequise()) {
            return EvolutionStage.STADE_2_GARDIEN_DE_GRES;
        }
        return EvolutionStage.STADE_1_LUEUR_DU_SANCTUAIRE;
    }

    /**
     * Présente une offrande rituelle au Neak Ta et retourne le stade atteint après l'offrande.
     */
    public EvolutionStage presenterOffrande(String itemId) {
        Objects.requireNonNull(itemId, "itemId");
        String court = itemId.replace("cambocraft:", "");
        int gain = switch (court) {
            case "bol_de_riz_beni" -> 120;
            case "fleur_de_lotus" -> 60;
            case "encens_sacre" -> 50;
            case "offrande_de_fleurs" -> 45;
            case "riz_parfume_jasmin" -> 30;
            default -> 10;
        };
        pointsAffinite = Math.min(2000, pointsAffinite + gain);
        return stadeActuel();
    }

    /**
     * Tente d'intercepter un coup fatal au jour de jeu {@code jourMonde} (disponible au Stade 3,
     * une fois par jour de jeu).
     */
    public boolean tenterSauvetageFatal(long jourMonde) {
        if (!stadeActuel().sauvetageCoupFatalQuotidien()) {
            return false;
        }
        if (dernierSauvetageJour == jourMonde) {
            return false;
        }
        dernierSauvetageJour = jourMonde;
        return true;
    }
}
