package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/** Réseau serveur vers client (stub de compilation). */
public final class ServerPlayNetworking {
	private ServerPlayNetworking() {
	}

	public static <T extends CustomPayload> boolean registerGlobalReceiver(CustomPayload.Id<T> type, PlayPayloadHandler<T> handler) {
		throw new RuntimeException();
	}

	public static void send(ServerPlayerEntity player, CustomPayload payload) {
		throw new RuntimeException();
	}

	public static PacketSender getSender(ServerPlayerEntity player) {
		throw new RuntimeException();
	}

	public interface PlayPayloadHandler<T extends CustomPayload> {
		void receive(T payload, Context context);
	}

	public interface Context {
		ServerPlayerEntity player();

		ServerPlayNetworkHandler handler();
	}
}
