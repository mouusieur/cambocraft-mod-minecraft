package net.fabricmc.fabric.api.networking.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

/** Connexion des joueurs (stub de compilation). */
public final class ServerPlayConnectionEvents {
	private ServerPlayConnectionEvents() {
	}

	public static final Event<Join> JOIN = null;
	public static final Event<Disconnect> DISCONNECT = null;

	public interface Join {
		void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server);
	}

	public interface Disconnect {
		void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server);
	}
}
