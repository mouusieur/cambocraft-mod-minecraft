package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** Écouteur de rechargement de ressources (stub de compilation). */
public interface ResourceReloadListener {
	default CompletableFuture<?> reload(ResourceManager manager, Object profiler, Executor prepareExecutor, Executor applyExecutor) {
		throw new RuntimeException();
	}
}
