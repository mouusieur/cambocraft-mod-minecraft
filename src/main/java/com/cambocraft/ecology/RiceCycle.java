package com.cambocraft.ecology;

import com.cambocraft.npc.Economy;

/**
 * Cycle rizicole cambodgien à quatre stades (§4.4) et couplage direct avec l'économie des villages.
 *
 * <p>Les quatre étapes suivent la riziculture traditionnelle en rizière inondée :</p>
 * <ol>
 *   <li>{@link Stade#SEMIS} : semis en pépinière sur boue humide ;</li>
 *   <li>{@link Stade#REPIQUAGE} : repiquage des jeunes plants en casiers inondés ;</li>
 *   <li>{@link Stade#CROISSANCE} : tallage et montaison sous les pluies de mousson ;</li>
 *   <li>{@link Stade#RECOLTE} : épis dorés prêts à être coupés à la faucille.</li>
 * </ol>
 */
public final class RiceCycle {
    public enum Stade {
        SEMIS(0, "semis"),
        REPIQUAGE(1, "repiquage"),
        CROISSANCE(2, "croissance"),
        RECOLTE(3, "recolte");

        public final int indice;
        public final String id;

        Stade(int indice, String id) {
            this.indice = indice;
            this.id = id;
        }

        public boolean estMur() {
            return this == RECOLTE;
        }

        public Stade suivant() {
            return switch (this) {
                case SEMIS -> REPIQUAGE;
                case REPIQUAGE -> CROISSANCE;
                case CROISSANCE, RECOLTE -> RECOLTE;
            };
        }

        public static Stade parIndice(int indice) {
            for (Stade s : values()) if (s.indice == indice) return s;
            return SEMIS;
        }
    }

    /** Probabilité de base d'avancer d'un stade lors d'un tick aléatoire sur boue irriguée. */
    public static final double PROBA_BASE = 0.18;

    private static volatile double dernierRendement = 100.0;

    private RiceCycle() {}

    /**
     * Calcule la probabilité effective de croissance d'un plant de riz.
     *
     * @param saison    saison courante (module par {@link Season#pousseRiz})
     * @param irrigue   vrai si le plant pousse sur {@code boue_de_riziaire} ou près de l'eau
     * @param pluie     vrai s'il pleut actuellement sur la rizière
     */
    public static double probabiliteCroissance(Season saison, boolean irrigue, boolean pluie) {
        double multIrrig = irrigue ? 1.35 : 0.65;
        double multPluie = pluie ? 1.20 : 1.00;
        double p = PROBA_BASE * saison.pousseRiz * multIrrig * multPluie;
        return Math.max(0.01, Math.min(0.95, p));
    }

    /** Nombre de grains/épis de riz brut obtenus à la récolte selon le stade et la saison. */
    public static int quantiteRecolte(Stade stade, Season saison, boolean avecFaucille) {
        if (!stade.estMur()) return 1;
        int base = saison == Season.MOUSSON ? 4 : (saison == Season.SAISON_DOUCE ? 3 : 2);
        return avecFaucille ? base + 1 : base;
    }

    /**
     * Rendement régional moyen (base 100) selon la saison et la part de rizières irriguées (0..1).
     */
    public static double calculerRendementSaisonnier(Season saison, double partIrriguee) {
        double clampIrrig = Math.max(0.0, Math.min(1.0, partIrriguee));
        double facteurSaison = switch (saison) {
            case MOUSSON -> 1.25;
            case SAISON_DOUCE -> 1.05;
            case SAISON_CHAUDE -> 0.72;
        };
        double rendement = 100.0 * facteurSaison * (0.65 + 0.55 * clampIrrig);
        dernierRendement = rendement;
        return rendement;
    }

    /** Indice d'inflation du riz courant déduit de la dernière récolte régionale. */
    public static double indiceRizCourant() {
        return Economy.indiceRiz(dernierRendement);
    }

    public static double dernierRendement() {
        return dernierRendement;
    }
}
