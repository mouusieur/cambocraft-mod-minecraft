package com.cambocraft.client;

import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.Seasons;
import com.cambocraft.ecology.WorldAtmosphere;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.progression.ProgressionCurve;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modèle de présentation du HUD et du Carnet du Nen côté client :
 * synthétise les barres de niveau/Renaissance, d'énergie spirituelle, les 3 emplacements actifs,
 * les synergies, la saison et le suivi de quête dans un état immuable directement testable.
 */
public final class CamboHudModel {
    public record HudSnapshot(
            int niveau,
            int etoilesRenaissance,
            double ratioXpPalier,
            int energieActuelle,
            int energieMax,
            double ratioEnergie,
            List<String> emplacementsActifs,
            int nombreSynergiesActives,
            Season saison,
            int jourDansSaison,
            int niveauEauGrandLacY,
            String queteEpingleeTitre
    ) {}

    private CamboHudModel() {}

    public static HudSnapshot construire(
            int niveau,
            int etoilesRenaissance,
            long xpDansNiveauActuel,
            NenNotebook carnet,
            long worldTick,
            String queteEpingleeTitre
    ) {
        Objects.requireNonNull(carnet, "carnet");
        long xpRequis = ProgressionCurve.xpPourNiveauSuivant(Math.min(199, Math.max(1, niveau)));
        double ratioXp = niveau >= ProgressionCurve.NIVEAU_MAX
                ? 1.0
                : Math.max(0.0, Math.min(1.0, (double) xpDansNiveauActuel / Math.max(1L, xpRequis)));
        double ratioEn = (double) carnet.energieActuelle() / Math.max(1, carnet.energieMax());

        List<String> slots = new ArrayList<>(NenNotebook.NB_EMPLACEMENTS_ACTIFS);
        for (int i = 0; i < NenNotebook.NB_EMPLACEMENTS_ACTIFS; i++) {
            String p = carnet.pouvoirEquipe(i);
            slots.add(p == null ? "" : p);
        }

        Season s = Seasons.saison(worldTick);
        int jour = Seasons.jourDansSaison(worldTick);
        int yLac = WorldAtmosphere.hauteurGrandLac(s);

        return new HudSnapshot(
                niveau,
                etoilesRenaissance,
                ratioXp,
                carnet.energieActuelle(),
                carnet.energieMax(),
                ratioEn,
                List.copyOf(slots),
                carnet.synergiesActives().size(),
                s,
                jour,
                yLac,
                queteEpingleeTitre == null ? "" : queteEpingleeTitre
        );
    }
}
