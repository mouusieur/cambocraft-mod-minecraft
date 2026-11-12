package net.fabricmc.fabric.api.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

/** Ticks du serveur et des mondes (stub de compilation). */
public final class ServerTickEvents {
	private ServerTickEvents() {
	}

	public static final Event<EndTick> END_SERVER_TICK = null;
	public static final Event<EndWorldTick> END_WORLD_TICK = null;

	public interface EndTick {
		void onEndTick(MinecraftServer server);
	}

	public interface EndWorldTick {
		void onEndTick(ServerWorld world);
	}
}
