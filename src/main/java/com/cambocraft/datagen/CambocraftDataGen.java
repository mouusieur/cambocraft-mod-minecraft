package com.cambocraft.datagen;

import com.cambocraft.combat.CombatRegistry;
import com.cambocraft.craft.CookingRegistry;
import com.cambocraft.data.DataManager;
import com.cambocraft.event.ShadowNightManager;
import com.cambocraft.fishing.FishingRegistry;
import com.cambocraft.npc.NpcRegistry;
import com.cambocraft.power.PowerRegistry;
import com.cambocraft.progression.ZoneRegistry;
import com.cambocraft.quest.QuestRegistry;
import java.nio.file.Path;

/**
 * Utilitaire de synchronisation et de chargement hors ligne de l'ensemble des registres de données
 * de Cambocraft pour la génération de données et les tests d'intégration.
 */
public final class CambocraftDataGen {
    private CambocraftDataGen() {}

    public static void initialiserEtChargerTout(Path dataRoot) throws Exception {
        FishingRegistry.init();
        NpcRegistry.init();
        CookingRegistry.init();
        ZoneRegistry.init();
        QuestRegistry.init();
        PowerRegistry.init();
        CombatRegistry.init();
        ShadowNightManager.init();
        DataManager.chargerDepuisDossier(dataRoot);
    }
}
