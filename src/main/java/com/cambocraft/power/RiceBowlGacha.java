package com.cambocraft.power;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Moteur de tirage de pouvoirs du Nen par dégustation d'un Bol de Riz Béni auprès d'un
 * Apprenti Moine (§5.2 de DESIGN.md).
 *
 * <p>Taux de base :
 * <ul>
 *   <li>Commun : 55,0 %</li>
 *   <li>Rare : 28,0 %</li>
 *   <li>Épique : 12,0 % (garanti au 30e tirage sans Épique+)</li>
 *   <li>Légendaire : 4,5 % (garanti au 80e tirage sans Légendaire+)</li>
 *   <li>Mythique : 0,5 % (garanti au 250e tirage sans Mythique)</li>
 * </ul>
 */
public final class RiceBowlGacha {
    public static final int PITY_EPIQUE = 30;
    public static final int PITY_LEGENDAIRE = 80;
    public static final int PITY_MYTHIQUE = 250;

    /**
     * État individuel des compteurs de garantie (pity) d'un joueur.
     */
    public static final class PityState {
        private int tiragesSansEpique;
        private int tiragesSansLegendaire;
        private int tiragesSansMythique;
        private int totalTirages;

        public PityState() {}

        public PityState(int sansEpique, int sansLegendaire, int sansMythique, int totalTirages) {
            this.tiragesSansEpique = Math.max(0, sansEpique);
            this.tiragesSansLegendaire = Math.max(0, sansLegendaire);
            this.tiragesSansMythique = Math.max(0, sansMythique);
            this.totalTirages = Math.max(0, totalTirages);
        }

        /** Restaure les compteurs depuis une sauvegarde (NBT ou paquet de synchronisation). */
        public void restaurer(int sansEpique, int sansLegendaire, int sansMythique, int total) {
            this.tiragesSansEpique = Math.max(0, sansEpique);
            this.tiragesSansLegendaire = Math.max(0, sansLegendaire);
            this.tiragesSansMythique = Math.max(0, sansMythique);
            this.totalTirages = Math.max(0, total);
        }

        public int tiragesSansEpique() { return tiragesSansEpique; }
        public int tiragesSansLegendaire() { return tiragesSansLegendaire; }
        public int tiragesSansMythique() { return tiragesSansMythique; }
        public int totalTirages() { return totalTirages; }
    }

    public record GachaRollResult(
            PowerDefinition pouvoir,
            PowerRarity rareteObtenue,
            boolean garantiePityActivee,
            int nouveauNiveauPouvoir,
            boolean convertiEnMaitrise
    ) {}

    private RiceBowlGacha() {}

    /**
     * Tire une rareté pure selon les probabilités de base sans modifier de compteur de pity.
     */
    public static PowerRarity tirerRaretePure(double u) {
        if (u < 0.005) {
            return PowerRarity.MYTHIQUE;
        }
        if (u < 0.005 + 0.045) {
            return PowerRarity.LEGENDAIRE;
        }
        if (u < 0.005 + 0.045 + 0.120) {
            return PowerRarity.EPIQUE;
        }
        if (u < 0.005 + 0.045 + 0.120 + 0.280) {
            return PowerRarity.RARE;
        }
        return PowerRarity.COMMUN;
    }

    /**
     * Tire une rareté avec application et mise à jour des compteurs de garantie (30 / 80 / 250).
     */
    public static PowerRarity tirerRareteAvecPity(PityState state, Random rng) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(rng, "rng");

        state.totalTirages++;
        state.tiragesSansEpique++;
        state.tiragesSansLegendaire++;
        state.tiragesSansMythique++;

        PowerRarity brute = tirerRaretePure(rng.nextDouble());
        PowerRarity finale = brute;

        if (state.tiragesSansMythique >= PITY_MYTHIQUE) {
            finale = PowerRarity.MYTHIQUE;
        } else if (state.tiragesSansLegendaire >= PITY_LEGENDAIRE && !brute.auMoins(PowerRarity.LEGENDAIRE)) {
            finale = PowerRarity.LEGENDAIRE;
        } else if (state.tiragesSansEpique >= PITY_EPIQUE && !brute.auMoins(PowerRarity.EPIQUE)) {
            finale = PowerRarity.EPIQUE;
        }

        if (finale == PowerRarity.MYTHIQUE) {
            state.tiragesSansMythique = 0;
            state.tiragesSansLegendaire = 0;
            state.tiragesSansEpique = 0;
        } else if (finale == PowerRarity.LEGENDAIRE) {
            state.tiragesSansLegendaire = 0;
            state.tiragesSansEpique = 0;
        } else if (finale == PowerRarity.EPIQUE) {
            state.tiragesSansEpique = 0;
        }

        return finale;
    }

    /**
     * Effectue un tirage complet au Bol de Riz Béni et met à jour le Carnet du Nen du joueur.
     */
    public static GachaRollResult degusterBolDeRizBeni(
            PityState pity,
            NenNotebook carnet,
            Random rng
    ) {
        int avantEp = pity.tiragesSansEpique() + 1;
        int avantLeg = pity.tiragesSansLegendaire() + 1;
        int avantMyt = pity.tiragesSansMythique() + 1;

        PowerRarity rarete = tirerRareteAvecPity(pity, rng);
        boolean pityDeclenchee = (avantMyt >= PITY_MYTHIQUE && rarete == PowerRarity.MYTHIQUE)
                || (avantLeg >= PITY_LEGENDAIRE && rarete == PowerRarity.LEGENDAIRE)
                || (avantEp >= PITY_EPIQUE && rarete == PowerRarity.EPIQUE);

        List<PowerDefinition> candidats = PowerRegistry.parRarete(rarete);
        if (candidats.isEmpty()) {
            candidats = List.copyOf(PowerRegistry.tous());
        }
        if (candidats.isEmpty()) {
            throw new IllegalStateException("Aucun pouvoir enregistré dans PowerRegistry");
        }
        PowerDefinition choisi = candidats.get(rng.nextInt(candidats.size()));
        NenNotebook.UnlockOutcome outcome = carnet.ajouterTirage(choisi);
        return new GachaRollResult(
                choisi,
                rarete,
                pityDeclenchee,
                outcome.niveauActuel(),
                outcome.doublonNiveauMaxConverti()
        );
    }
}
