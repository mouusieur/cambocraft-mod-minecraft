package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;

/** Enregistrement d'un rendu d'entité côté client (stub de compilation). */
public interface EntityRendererRegistry {
	static void register(EntityType<?> type, EntityRendererFactory factory) {
		throw new UnsupportedOperationException();
	}
}
