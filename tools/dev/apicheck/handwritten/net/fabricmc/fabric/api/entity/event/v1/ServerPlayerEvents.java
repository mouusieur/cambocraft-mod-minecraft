package net.fabricmc.fabric.api.entity.event.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Évènements joueur côté serveur (stub de compilation).
 *
 * <p>Reproduit les signatures réelles de Fabric API 1.21.1 : {@code JOIN} et {@code LEAVE}
 * reçoivent directement le joueur, sans passer par le gestionnaire réseau.</p>
 */
public final class ServerPlayerEvents {
	private ServerPlayerEvents() {
	}

	public static final Event<Join> JOIN = null;
	public static final Event<Leave> LEAVE = null;
	public static final Event<AfterRespawn> AFTER_RESPAWN = null;
	public static final Event<CopyFrom> COPY_FROM = null;

	public interface Join {
		void onJoin(ServerPlayerEntity player);
	}

	public interface Leave {
		void onLeave(ServerPlayerEntity player);
	}

	public interface AfterRespawn {
		void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}

	public interface CopyFrom {
		void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}
}
