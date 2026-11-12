package com.cambocraft.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;

/** Rendu de l'artisan : modèle villageois vanilla, sans modèle 3D dédié. */
public class RenduArtisan extends VillagerEntityRenderer {
    public RenduArtisan(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
}
