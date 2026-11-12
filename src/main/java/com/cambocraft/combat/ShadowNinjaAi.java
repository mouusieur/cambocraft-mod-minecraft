package com.cambocraft.combat;

/**
 * Machine à états de la transformation en 5 étapes d'un villageois infiltré en ninja du Clan Ombre,
 * et sélection tactique d'action de combat.
 *
 * <p>Les 5 étapes de dévoilement (§2 de DESIGN.md) :
 * <ol>
 *   <li>{@link RevealStage#ETAPE_1_DISSIMULE_VILLAGEOIS} : apparence et routine de villageois</li>
 *   <li>{@link RevealStage#ETAPE_2_SUSPICION_REGARD} : arrêt du geste, regard fixe, main sur la garde</li>
 *   <li>{@link RevealStage#ETAPE_3_SCEAU_DE_FUMIGENE} : mudra rapide et explosion de fumée d'encre</li>
 *   <li>{@link RevealStage#ETAPE_4_REVELATION_TENUE_OMBRE} : apparition de la tenue d'ardoise et dégainage</li>
 *   <li>{@link RevealStage#ETAPE_5_COMBAT_ACTIF} : posture offensive complète du Clan Ombre</li>
 * </ol>
 */
public final class ShadowNinjaAi {
    public enum RevealStage {
        ETAPE_1_DISSIMULE_VILLAGEOIS(1, 0),
        ETAPE_2_SUSPICION_REGARD(2, 15),
        ETAPE_3_SCEAU_DE_FUMIGENE(3, 12),
        ETAPE_4_REVELATION_TENUE_OMBRE(4, 15),
        ETAPE_5_COMBAT_ACTIF(5, 0);

        private final int numero;
        private final int dureeTransitionTicks;

        RevealStage(int numero, int dureeTransitionTicks) {
            this.numero = numero;
            this.dureeTransitionTicks = dureeTransitionTicks;
        }

        public int numero() { return numero; }
        public int dureeTransitionTicks() { return dureeTransitionTicks; }

        public RevealStage suivant() {
            return switch (this) {
                case ETAPE_1_DISSIMULE_VILLAGEOIS -> ETAPE_2_SUSPICION_REGARD;
                case ETAPE_2_SUSPICION_REGARD -> ETAPE_3_SCEAU_DE_FUMIGENE;
                case ETAPE_3_SCEAU_DE_FUMIGENE -> ETAPE_4_REVELATION_TENUE_OMBRE;
                case ETAPE_4_REVELATION_TENUE_OMBRE, ETAPE_5_COMBAT_ACTIF -> ETAPE_5_COMBAT_ACTIF;
            };
        }
    }

    public enum TacticalAction {
        APPROCHE_FURTIVE,
        FRAPPE_DAGUE_OMBRE,
        SALVE_DE_SHURIKENS,
        PAS_DE_L_OMBRE_DOS,
        CONTRE_ATTAQUE_PARADE,
        REPLI_FUMIGENE
    }

    private RevealStage etape = RevealStage.ETAPE_1_DISSIMULE_VILLAGEOIS;
    private int ticksDansEtape = 0;

    public RevealStage etape() {
        return etape;
    }

    /**
     * Déclenche le processus de dévoilement si le joueur s'approche avec une relique,
     * attaque l'infiltré ou si la Nuit des Ombres est active.
     */
    public boolean doitDeclencherRevelation(
            double distanceJoueur,
            boolean joueurPorteReliqueSacree,
            boolean nuitDesOmbresActive,
            boolean aSubiDegats
    ) {
        if (etape != RevealStage.ETAPE_1_DISSIMULE_VILLAGEOIS) {
            return true;
        }
        if (aSubiDegats) {
            return true;
        }
        if (nuitDesOmbresActive && distanceJoueur <= 14.0) {
            return true;
        }
        return joueurPorteReliqueSacree && distanceJoueur <= 6.5;
    }

    /**
     * Fait progresser la transformation d'un tick serveur à travers les 5 étapes.
     */
    public RevealStage tickTransformation(boolean declenchee) {
        if (!declenchee && etape == RevealStage.ETAPE_1_DISSIMULE_VILLAGEOIS) {
            return etape;
        }
        if (etape == RevealStage.ETAPE_1_DISSIMULE_VILLAGEOIS) {
            etape = RevealStage.ETAPE_2_SUSPICION_REGARD;
            ticksDansEtape = 0;
            return etape;
        }
        if (etape == RevealStage.ETAPE_5_COMBAT_ACTIF) {
            return etape;
        }
        ticksDansEtape++;
        if (ticksDansEtape >= etape.dureeTransitionTicks()) {
            etape = etape.suivant();
            ticksDansEtape = 0;
        }
        return etape;
    }

    /**
     * Choisit l'action tactique du ninja en fonction de son rang, de la distance et de ses PV restants.
     */
    public static TacticalAction choisirAction(
            ShadowClanRank rang,
            double distanceCible,
            double ratioPvRestants,
            boolean pasDeLOmbrePret
    ) {
        if (ratioPvRestants <= 0.22 && rang.echelon() >= 2) {
            return TacticalAction.REPLI_FUMIGENE;
        }
        if (pasDeLOmbrePret && distanceCible >= 4.5 && distanceCible <= 12.0 && rang.echelon() >= 3) {
            return TacticalAction.PAS_DE_L_OMBRE_DOS;
        }
        if (distanceCible > 6.0) {
            if (rang == ShadowClanRank.ARCHER || rang == ShadowClanRank.MAITRE_SHURIKEN || rang.echelon() >= 4) {
                return TacticalAction.SALVE_DE_SHURIKENS;
            }
            return TacticalAction.APPROCHE_FURTIVE;
        }
        if (distanceCible <= 2.8 && ratioPvRestants < 0.55 && rang.echelon() >= 3) {
            return TacticalAction.CONTRE_ATTAQUE_PARADE;
        }
        return TacticalAction.FRAPPE_DAGUE_OMBRE;
    }
}
