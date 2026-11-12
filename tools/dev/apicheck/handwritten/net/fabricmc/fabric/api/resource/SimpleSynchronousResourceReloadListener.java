package net.fabricmc.fabric.api.resource;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

/** Écouteur simple, exécuté sur le fil principal (stub de compilation). */
public interface SimpleSynchronousResourceReloadListener extends IdentifiableResourceReloadListener, SynchronousResourceReloader {
	@Override
	void reload(ResourceManager manager);
}
