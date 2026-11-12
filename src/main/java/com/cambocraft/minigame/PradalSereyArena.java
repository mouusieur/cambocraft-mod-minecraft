package com.cambocraft.minigame;

import com.cambocraft.util.Localized;
import java.util.Objects;

/**
 * Arène d'art martial traditionnel khmer **Pradal Serey** à **3 paliers** (§2 de DESIGN.md).
 *
 * <p>Chaque combat se déroule en 5 reprises accompagnées de l'orchestre rituel *Sarama*
 * (hautbois *sralai* et tambours *skor chhayyam*), dont le tempo accélère de reprise en reprise.
 */
public final class PradalSereyArena {
    public enum ArenaTier {
        PALIER_1_NOVICE_DES_RIZIERES(
                1,
                new Localized("Novice des Rizières", "Paddy Novice"),
                15,
                180.0,
                14.0,
                120,
                60,
                "cambocraft:bandages_pradal"
        ),
        PALIER_2_GUERRIER_DE_KOMPONG(
                2,
                new Localized("Guerrier de Kompong", "Warrior of Kompong"),
                65,
                620.0,
                42.0,
                450,
                180,
                "cambocraft:gants_pradal"
        ),
        PALIER_3_GRAND_CHAMPION_ROYAL(
                3,
                new Localized("Grand Champion Royal", "Royal Grand Champion"),
                130,
                1850.0,
                110.0,
                1400,
                450,
                "cambocraft:ceinture_champion_pradal"
        );

        private final int numero;
        private final Localized titre;
        private final int niveauRequis;
        private final double pvAdversaire;
        private final double attaqueAdversaire;
        private final int piecesRecompense;
        private final int reputationRecompense;
        private final String objetTrophee;

        ArenaTier(
                int numero,
                Localized titre,
                int niveauRequis,
                double pvAdversaire,
                double attaqueAdversaire,
                int piecesRecompense,
                int reputationRecompense,
                String objetTrophee
        ) {
            this.numero = numero;
            this.titre = titre;
            this.niveauRequis = niveauRequis;
            this.pvAdversaire = pvAdversaire;
            this.attaqueAdversaire = attaqueAdversaire;
            this.piecesRecompense = piecesRecompense;
            this.reputationRecompense = reputationRecompense;
            this.objetTrophee = objetTrophee;
        }

        public int numero() { return numero; }
        public Localized titre() { return titre; }
        public int niveauRequis() { return niveauRequis; }
        public double pvAdversaire() { return pvAdversaire; }
        public double attaqueAdversaire() { return attaqueAdversaire; }
        public int piecesRecompense() { return piecesRecompense; }
        public int reputationRecompense() { return reputationRecompense; }
        public String objetTrophee() { return objetTrophee; }
    }

    public enum StrikeMove {
        DIRECT_DU_POING(1.00),
        COUP_DE_PIED_CIRCULAIRE(1.20),
        COUP_DE_GENOU_SAUTE(1.38),
        COUP_DE_COUDE_DESCENDANT(1.50),
        GARDE_KHMERE(0.00);

        private final double multiplicateurDegats;

        StrikeMove(double multiplicateurDegats) {
            this.multiplicateurDegats = multiplicateurDegats;
        }

        public double multiplicateurDegats() {
            return multiplicateurDegats;
        }
    }

    private PradalSereyArena() {}

    /**
     * Calcule le multiplicateur rythmique *Sarama* selon la reprise ({@code 1..5})
     * et la précision du timing sur le battement du tambour ({@code ecartTicks}).
     */
    public static double calculerBonusRythmeSarama(int round, int ecartTicks) {
        int r = Math.max(1, Math.min(5, round));
        double bonusRound = 1.0 + (r - 1) * 0.06;
        int abs = Math.abs(ecartTicks);
        double precision = abs <= 2 ? 1.25 : (abs <= 5 ? 1.10 : 0.95);
        return bonusRound * precision;
    }

    public static double calculerDegatsTechnique(
            StrikeMove move,
            double attaqueBaseJoueur,
            int round,
            int ecartTicks
    ) {
        Objects.requireNonNull(move, "move");
        if (move == StrikeMove.GARDE_KHMERE) {
            return 0.0;
        }
        return attaqueBaseJoueur * move.multiplicateurDegats() * calculerBonusRythmeSarama(round, ecartTicks);
    }
}
