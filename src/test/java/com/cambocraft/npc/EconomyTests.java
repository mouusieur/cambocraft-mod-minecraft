package com.cambocraft.npc;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertNear;
import static com.cambocraft.test.TestSupport.assertTrue;

/** Tests des formules économiques : paliers de réputation, inflation du riz, bornes de solde. */
public final class EconomyTests {
    private EconomyTests() {}

    public static void paliersEtInflation() {
        assertEquals(0, Economy.palier(-10));
        assertEquals(1, Economy.palier(0));
        assertEquals(2, Economy.palier(250));
        assertEquals(3, Economy.palier(800));
        assertEquals(4, Economy.palier(2000));

        assertNear(0.80, Economy.inflation(0.5), 1e-6);
        assertNear(1.00, Economy.inflation(1.0), 1e-6);
        assertNear(1.60, Economy.inflation(2.0), 1e-6);

        assertNear(0.50, Economy.indiceRiz(250.0), 1e-6);
        assertNear(1.00, Economy.indiceRiz(100.0), 1e-6);
        assertNear(2.00, Economy.indiceRiz(40.0), 1e-6);

        int achatHostile = Economy.prixAchat(100, -50, 1.0);
        int achatNeutre = Economy.prixAchat(100, 300, 1.0);
        int achatLegendaire = Economy.prixAchat(100, 2000, 1.0);
        assertTrue(achatHostile > achatNeutre && achatNeutre > achatLegendaire,
                "la réputation doit réduire le prix d'achat");

        int venteHostile = Economy.prixVente(100, -50, 1.0);
        int venteLegendaire = Economy.prixVente(100, 2000, 1.0);
        assertTrue(venteLegendaire > venteHostile,
                "la réputation doit augmenter le prix de revente");

        int achatFamine = Economy.prixAchat(100, 300, 2.0);
        int achatAbondance = Economy.prixAchat(100, 300, 0.5);
        assertTrue(achatFamine > achatAbondance,
                "une mauvaise récolte de riz doit renchérir les denrées");

        assertEquals(0L, Economy.ajouter(50L, -200L));
    }
}
