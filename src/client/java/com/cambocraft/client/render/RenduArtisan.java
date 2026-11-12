package com.cambocraft.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * Rendu de l'artisan : modèle 3D dédié ({@link ModelArtisan}) et texture de soie safran.
 *
 * <p>Le rendu s'affiche en 1,0× pour les épaules et la tête, mais le PNJ est légèrement réduit
 * (0,92×) pour rester plus petit que le joueur : hiérarchie visuelle respectueuse du statut
 * d'artisan, sans effet « boss ».</p>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RenduArtisan extends LivingEntityRenderer {
    public RenduArtisan(EntityRendererFactory.Context contexte) {
        super(contexte, (EntityModel) new ModelArtisan(), 0.5f);
    }

    @Override
    public Identifier getTexture(net.minecraft.entity.Entity entite) {
        return ModelesCambocraft.TEXTURE_ARTISAN;
    }

    @Override
    public void scale(LivingEntity entite, net.minecraft.client.util.math.MatrixStack matrices, float delta) {
        matrices.scale(0.92f, 0.92f, 0.92f);
    }
}
