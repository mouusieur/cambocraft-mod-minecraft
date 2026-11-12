package com.cambocraft.data;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.fishing.FishingRegistry;
import com.cambocraft.test.TestSupport;
import com.cambocraft.util.DataBundle;

/** Tests du chargement des données JSON sur disque et de la tolérance aux erreurs. */
public final class DataManagerTests {
    private DataManagerTests() {}

    public static void chargementEtToleranceAuxErreurs() {
        FishingRegistry.init();
        int v0 = DataManager.version();
        try {
            DataBundle bundle = DataManager.chargerDepuisDossier(TestSupport.dataRoot());
            assertTrue(DataManager.version() > v0, "la version doit s'incrémenter au rechargement");
            assertEquals(0, bundle.errors().size());
            assertTrue(FishingRegistry.table().especes().size() >= 12,
                    "au moins 12 espèces de poissons doivent être chargées");
        } catch (Exception e) {
            throw new AssertionError("échec de lecture des données : " + e, e);
        }

        // Vérifie qu'un JSON cassé est signalé mais n'interrompt pas le chargement
        DataBundle partiel = new DataBundle();
        partiel.putText("fishing", "casse", "{pas du json");
        assertEquals(1, partiel.errors().size());
    }
}
