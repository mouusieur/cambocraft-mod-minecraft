package net.fabricmc.fabric.api.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

/** Chargement et déchargement des mondes (stub de compilation). */
public final class ServerWorldEvents {
	private ServerWorldEvents() {
	}

	public static final Event<Load> LOAD = null;
	public static final Event<Unload> UNLOAD = null;

	public interface Load {
		void onWorldLoad(MinecraftServer server, ServerWorld world);
	}

	public interface Unload {
		void onWorldUnload(MinecraftServer server, ServerWorld world);
	}
}
