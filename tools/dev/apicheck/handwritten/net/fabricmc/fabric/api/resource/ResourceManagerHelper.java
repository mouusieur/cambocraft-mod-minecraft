package net.fabricmc.fabric.api.resource;

import net.minecraft.resource.ResourceType;

/** Accès au gestionnaire de ressources (stub de compilation). */
public interface ResourceManagerHelper {
	static ResourceManagerHelper get(ResourceType type) {
		throw new RuntimeException();
	}

	void registerReloadListener(IdentifiableResourceReloadListener listener);
}
