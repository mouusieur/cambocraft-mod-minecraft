package com.cambocraft.world;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertFalse;
import static com.cambocraft.test.TestSupport.assertNear;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.ecology.RiceCycle;
import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.WorldAtmosphere;

/** Tests du portail du Lotus, des 15 biomes, du cycle du riz et de la crue du Grand Lac. */
public final class WorldAndEcologyTests {
    private WorldAndEcologyTests() {}

    public static void portailBiomesRizEtLac() {
        // 1. Les 15 biomes déclarés (§4.2 : >= 14 biomes requis)
        assertTrue(CambodiaDimension.BIOMES.size() >= 15,
                "la dimension doit déclarer au moins 15 biomes");

        // 2. Anneau 3×3 du portail de Pierre du Lotus
        assertTrue(LotusPortal.verifierAnneauHorizontal((dx, dz) -> true),
                "un anneau 3x3 complet doit être valide");
        assertFalse("un coin manquant doit invalider le portail",
                LotusPortal.verifierAnneauHorizontal((dx, dz) -> !(dx == 1 && dz == 1)));

        // 3. Cycle du riz à 4 stades et couplage économique
        assertEquals(RiceCycle.Stade.REPIQUAGE, RiceCycle.Stade.SEMIS.suivant());
        assertEquals(RiceCycle.Stade.CROISSANCE, RiceCycle.Stade.REPIQUAGE.suivant());
        assertEquals(RiceCycle.Stade.RECOLTE, RiceCycle.Stade.CROISSANCE.suivant());
        assertTrue(RiceCycle.Stade.RECOLTE.estMur(), "le stade RECOLTE doit être mûr");

        double pMousson = RiceCycle.probabiliteCroissance(Season.MOUSSON, true, true);
        double pSeche = RiceCycle.probabiliteCroissance(Season.SAISON_DOUCE, false, false);
        assertTrue(pMousson > pSeche, "le riz irrigué en mousson doit pousser plus vite");

        double rendMousson = RiceCycle.calculerRendementSaisonnier(Season.MOUSSON, 1.0);
        double indiceMousson = RiceCycle.indiceRizCourant();
        double rendChaud = RiceCycle.calculerRendementSaisonnier(Season.SAISON_CHAUDE, 0.2);
        double indiceChaud = RiceCycle.indiceRizCourant();
        assertTrue(rendMousson > rendChaud && indiceMousson < indiceChaud,
                "une bonne récolte de mousson doit abaisser l'indice d'inflation du riz");

        // 4. Grand Lac et temples engloutis (entrée à Y=64)
        assertTrue(WorldAtmosphere.templeEngloutiAccessible(Season.SAISON_DOUCE, 64),
                "un temple à Y=64 doit être émergé en saison douce (lac à 62)");
        assertFalse("un temple à Y=64 doit être submergé en mousson (lac à 68)",
                WorldAtmosphere.templeEngloutiAccessible(Season.MOUSSON, 64));

        // 5. Brume matinale et ralentissement sous la pluie de mousson
        assertTrue(WorldAtmosphere.estBrumeMatinale(Season.SAISON_DOUCE, 1000L),
                "brume matinale attendue à l'aube en saison douce");
        assertNear(0.90, WorldAtmosphere.multiplicateurMarchePluie(Season.MOUSSON, true, false), 1e-6);
        assertNear(1.00, WorldAtmosphere.multiplicateurMarchePluie(Season.MOUSSON, true, true), 1e-6);
    }
}
