package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/** Événements joueur côté serveur (stub de compilation). */
public final class ServerPlayerEvents {
	private ServerPlayerEvents() {
	}

	public static final Event<AfterRespawn> AFTER_RESPAWN = null;
	public static final Event<Join> JOIN = null;
	public static final Event<Leave> LEAVE = null;

	public interface AfterRespawn {
		void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}

	public interface Join {
		void onJoin(ServerPlayerEntity player);
	}

	public interface Leave {
		void onLeave(ServerPlayerEntity player);
	}

	/** Compatibilité : certains exemples importent la version entity.event. */
	public static final class WorldView {
		private WorldView() {
		}
	}
}
