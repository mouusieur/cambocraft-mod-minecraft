package net.fabricmc.loader.api;

import java.nio.file.Path;
import java.util.Optional;

/** Chargeur Fabric (stub de compilation). */
public interface FabricLoader {
	static FabricLoader getInstance() {
		throw new RuntimeException();
	}

	Optional<ModContainer> getModContainer(String id);

	boolean isDevelopmentEnvironment();

	Path getGameDir();
}
