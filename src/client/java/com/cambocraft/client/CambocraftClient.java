package com.cambocraft.client;

import com.cambocraft.entity.CamboEntities;
import com.cambocraft.registry.CamboBlocks;
import com.cambocraft.util.Log;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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
        // Rendu des entités : modèles 3D dédiés (artisan, gardien, Neak Ta) et textures
        // procédurales générées par tools/gen_entity_textures.py.
        enregistrerRendus();
        // Réseau client : miroir de l'état serveur, puis raccourcis clavier des trois écrans.
        ClientNetworking.enregistrerRecepteurs();
        CambocraftKeys.enregistrer();
        // Captures pilotees par fichier : indispensable dans la CI sans gestionnaire de
        // fenetres, ou les appuis de touche se perdent.
        CapturesAutomatiques.enregistrer();
        com.cambocraft.client.hud.HudCambocraft.enregistrer();
        Log.info("Cambocraft Client : HUD du Nen, Carnet, suivi de quêtes et accessibilité initialisés");
    }

    private static void enregistrerRendus() {
        com.cambocraft.client.render.RendusEntites.enregistrer();
    }

    public static AccessibilityConfig accessibility() {
        return ACCESSIBILITY;
    }
}
