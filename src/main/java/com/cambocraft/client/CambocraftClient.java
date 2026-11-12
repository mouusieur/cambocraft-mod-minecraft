package com.cambocraft.client;

import com.cambocraft.util.Log;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Point d'entrée client de Cambocraft (HUD, Carnet du Nen, journal de quêtes, sous-titres et accessibilité).
 */
@Environment(EnvType.CLIENT)
public final class CambocraftClient implements ClientModInitializer {
    private static final AccessibilityConfig ACCESSIBILITY = new AccessibilityConfig();

    @Override
    public void onInitializeClient() {
        Log.info("Cambocraft Client : HUD du Nen, Carnet, suivi de quêtes et accessibilité initialisés");
    }

    public static AccessibilityConfig accessibility() {
        return ACCESSIBILITY;
    }
}
