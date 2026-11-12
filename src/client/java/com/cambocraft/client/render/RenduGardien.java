package com.cambocraft.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * Rendu du gardien de boss : modèle 3D dédié ({@link ModelGardien}) mis à l'échelle 1,85×.
 *
 * <p>L'échelle distingue immédiatement une rencontre de boss d'un simple mob : hauteur effective
 * d'environ 4,4 blocs, largeur 2,6 blocs. Elle est appliquée au rendu uniquement — la boîte de
 * collision reste celle de l'entité, ce qui évite de coincer le joueur dans un mur.</p>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RenduGardien extends LivingEntityRenderer {
    public RenduGardien(EntityRendererFactory.Context contexte) {
        super(contexte, (EntityModel) new ModelGardien(), 0.9f);
    }

    @Override
    public Identifier getTexture(net.minecraft.entity.Entity entite) {
        return ModelesCambocraft.TEXTURE_GARDIEN;
    }

    @Override
    public void scale(LivingEntity entite, net.minecraft.client.util.math.MatrixStack matrices, float delta) {
        matrices.scale(1.85f, 1.85f, 1.85f);
    }
}
