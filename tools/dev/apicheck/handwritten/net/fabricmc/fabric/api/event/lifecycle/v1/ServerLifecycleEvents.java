package net.fabricmc.fabric.api.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;

/** Événements du cycle de vie du serveur (stub de compilation). */
public final class ServerLifecycleEvents {
	private ServerLifecycleEvents() {
	}

	public static final Event<ServerStarted> SERVER_STARTED = null;
	public static final Event<ServerStopping> SERVER_STOPPING = null;
	public static final Event<EndDataPackReload> END_DATA_PACK_RELOAD = null;

	public interface ServerStarted {
		void onServerStarted(MinecraftServer server);
	}

	public interface ServerStopping {
		void onServerStopping(MinecraftServer server);
	}

	public interface EndDataPackReload {
		void endDataPackReload(MinecraftServer server, Object resourceManager, boolean success);
	}
}
