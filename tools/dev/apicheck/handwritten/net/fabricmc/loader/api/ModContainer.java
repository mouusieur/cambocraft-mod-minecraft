package net.fabricmc.loader.api;

import java.nio.file.Path;
import java.util.List;

public interface ModContainer {
	List<Path> getRootPaths();

	Path getRootPath();
}
