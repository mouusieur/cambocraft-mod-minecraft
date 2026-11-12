package com.cambocraft.ecology;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertNear;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.util.Tick;

/** Tests du cycle des trois saisons cambodgiennes (saison douce, saison chaude, mousson). */
public final class SeasonsTests {
    private SeasonsTests() {}

    public static void cycleEtProgression() {
        long unJour = Tick.UN_DAY;
        long uneSaison = Seasons.DUREE_DEFAUT * unJour;

        assertEquals(Season.SAISON_DOUCE, Seasons.saison(0));
        assertEquals(Season.SAISON_DOUCE, Seasons.saison(uneSaison - 1));
        assertEquals(Season.SAISON_CHAUDE, Seasons.saison(uneSaison));
        assertEquals(Season.MOUSSON, Seasons.saison(2 * uneSaison));
        assertEquals(Season.SAISON_DOUCE, Seasons.saison(3 * uneSaison));

        assertEquals(1, Seasons.jourDansSaison(0));
        assertEquals(8, Seasons.jourDansSaison(7 * unJour + 5000));
        assertEquals(1, Seasons.jourDansSaison(8 * unJour));

        assertNear(0.0, Seasons.progression(0), 1e-6);
        assertNear(0.5, Seasons.progression(uneSaison / 2), 1e-6);
        assertEquals(uneSaison, Seasons.ticksAvantSaisonSuivante(0));
        assertEquals(1L, Seasons.ticksAvantSaisonSuivante(uneSaison - 1));
        assertEquals(1L, Seasons.annee(0));
        assertEquals(2L, Seasons.annee(3 * uneSaison));

        assertTrue(Season.MOUSSON.niveauLac > Season.SAISON_DOUCE.niveauLac,
                "le Grand Lac doit être plus haut en mousson qu'en saison douce");
        assertTrue(Season.MOUSSON.pousseRiz > Season.SAISON_DOUCE.pousseRiz,
                "le riz doit pousser plus vite en mousson");
    }
}
