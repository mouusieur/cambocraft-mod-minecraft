package net.fabricmc.fabric.api.object.builder.v1.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

/** Enregistrement des attributs par défaut d'un type d'entité (stub de compilation). */
public interface FabricDefaultAttributeRegistry {
	static void register(EntityType<? extends Entity> type, DefaultAttributeContainer.Builder builder) {
		throw new UnsupportedOperationException();
	}
}
