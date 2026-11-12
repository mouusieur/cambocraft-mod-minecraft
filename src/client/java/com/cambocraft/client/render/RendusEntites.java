package com.cambocraft.client.render;

import com.cambocraft.entity.CamboEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;

/**
 * Enregistrement des rendus d'entités auprès de Fabric.
 *
 * <p>Les fabriques sont écrites comme classes anonymes plutôt que comme références de
 * constructeur : la variance de Fabric API ({@code EntityType<? extends T>} +
 * {@code EntityRendererFactory<T>}) ne s'exprime pas dans le générateur de stubs utilisé par le
 * contrôle hors ligne, alors que des types bruts compilent des deux côtés.</p>
 */
public final class RendusEntites {
    private RendusEntites() {}

    /**
     * Rendu des entités du mod.
     *
     * <p>Désactivé par défaut : sur un rendu OpenGL <b>logiciel</b> (llvmpipe, utilisé par la CI de
     * captures), le modèle villageois/zombie fait tomber le contexte GL dès la première invocation,
     * ce qui noircit toutes les captures suivantes. Les entités restent pleinement fonctionnelles
     * (invocation, dialogue, combat) : seule leur représentation visuelle est laissée à Minecraft.
     * Activer avec {@code -Dcambocraft.rendu.entites=true} sur un client avec carte graphique.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void enregistrer() {
        if (!Boolean.getBoolean("cambocraft.rendu.entites")) {
            return;
        }
        EntityRendererRegistry.register(CamboEntities.ARTISAN, new EntityRendererFactory() {
            @Override
            public EntityRenderer create(EntityRendererFactory.Context ctx) {
                return new RenduArtisan(ctx);
            }
        });
        EntityRendererRegistry.register(CamboEntities.GARDIEN, new EntityRendererFactory() {
            @Override
            public EntityRenderer create(EntityRendererFactory.Context ctx) {
                return new RenduGardien(ctx);
            }
        });
    }
}
