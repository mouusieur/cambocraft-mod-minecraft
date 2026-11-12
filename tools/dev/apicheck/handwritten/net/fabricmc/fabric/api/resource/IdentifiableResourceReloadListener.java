package net.fabricmc.fabric.api.resource;

import java.util.Collection;
import java.util.List;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

/** Écouteur de rechargement identifié (stub de compilation). */
public interface IdentifiableResourceReloadListener extends ResourceReloadListener {
	Identifier getFabricId();

	default Collection<Identifier> getFabricDependencies() {
		return List.of();
	}
}
