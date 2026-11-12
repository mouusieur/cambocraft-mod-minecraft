package com.cambocraft.fishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Table de tirage des prises.
 *
 * <p>Le tirage est pondéré : chaque espèce a un poids de base ; la chance du joueur
 * (canne, appât, maîtrise) augmente le poids des espèces rares et diminue celui des communes,
 * sans jamais rendre une espèce impossible. Le calcul exact est décrit dans
 * {@code docs/PROBABILITES.md} et vérifié par {@code src/test/java/.../FishingTableTest}.</p>
 */
public final class FishingTable {
    private final List<FishSpecies> especes;

    public FishingTable(List<FishSpecies> especes) {
        this.especes = List.copyOf(especes);
    }

    public List<FishSpecies> especes() {
        return especes;
    }

    /** Poids effectif d'une espèce selon la chance (0 = neutre, 1 = très chanceux). */
    static double poidsEffectif(FishSpecies espece, double chance) {
        double ajustement = switch (espece.rarity()) {
            case COMMUN -> 1.0 - 0.35 * chance;
            case PEU_COMMUN -> 1.0;
            case RARE -> 1.0 + 1.2 * chance;
            case EPIQUE -> 1.0 + 2.5 * chance;
        };
        return espece.weight() * Math.max(0.05, ajustement);
    }

    public List<FishSpecies> candidates(String zone, String saison, boolean jour) {
        List<FishSpecies> sortie = new ArrayList<>();
        for (FishSpecies e : especes) {
            if (e.activeDans(zone, saison, jour)) sortie.add(e);
        }
        return sortie;
    }

    /** Tire une espèce ; renvoie vide si aucune espèce n'est active dans ce contexte. */
    public Optional<FishSpecies> tirer(String zone, String saison, boolean jour, double chance, Random random) {
        List<FishSpecies> dispo = candidates(zone, saison, jour);
        if (dispo.isEmpty()) return Optional.empty();
        double total = 0;
        for (FishSpecies e : dispo) total += poidsEffectif(e, chance);
        double seuil = random.nextDouble() * total;
        double cumul = 0;
        for (FishSpecies e : dispo) {
            cumul += poidsEffectif(e, chance);
            if (seuil <= cumul) return Optional.of(e);
        }
        return Optional.of(dispo.get(dispo.size() - 1));
    }

    /** Taille tirée dans l'intervalle de l'espèce ; les très grosses prises restent rares. */
    public static int taille(FishSpecies espece, Random random) {
        double t = Math.pow(random.nextDouble(), 1.35); // biais vers les tailles moyennes
        int taille = (int) Math.round(espece.minCm() + t * (espece.maxCm() - espece.minCm()));
        return Math.max(espece.minCm(), Math.min(espece.maxCm(), taille));
    }
}
