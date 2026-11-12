package com.cambocraft.client;

import com.cambocraft.registry.CamboBlocks;
import com.cambocraft.util.Log;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

/**
 * Point d'entrée client de Cambocraft (HUD, Carnet du Nen, journal de quêtes, sous-titres et accessibilité).
 */
@Environment(EnvType.CLIENT)
public final class CambocraftClient implements ClientModInitializer {
    private static final AccessibilityConfig ACCESSIBILITY = new AccessibilityConfig();

    @Override
    public void onInitializeClient() {
        // Sans cette declaration, les pixels transparents des textures de vegetaux
        // sont rendus en NOIR (les fleurs apparaissent sur un carre noir).
        for (Block bloc : CamboBlocks.CUTOUT) {
            BlockRenderLayerMap.INSTANCE.putBlock(bloc, RenderLayer.getCutout());
        }
        Log.info("Cambocraft Client : " + CamboBlocks.CUTOUT.size() + " blocs en rendu decoupe");
        Log.info("Cambocraft Client : HUD du Nen, Carnet, suivi de quêtes et accessibilité initialisés");
    }

    public static AccessibilityConfig accessibility() {
        return ACCESSIBILITY;
    }
}
