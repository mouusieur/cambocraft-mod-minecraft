package com.cambocraft.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;

/** Rendu du gardien de boss : modèle zombie vanilla, sans modèle 3D dédié. */
public class RenduGardien extends ZombieEntityRenderer {
    public RenduGardien(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
}
