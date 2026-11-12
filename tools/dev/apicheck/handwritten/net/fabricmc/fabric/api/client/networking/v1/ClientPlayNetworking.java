package net.fabricmc.fabric.api.client.networking.v1;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.CustomPayload;

/** Réseau client (stub de compilation, signatures de Fabric API 1.21.1). */
public final class ClientPlayNetworking {
	private ClientPlayNetworking() {
	}

	public static <T extends CustomPayload> boolean registerGlobalReceiver(CustomPayload.Id<T> type, PlayPayloadHandler<T> handler) {
		throw new RuntimeException();
	}

	public static void send(CustomPayload payload) {
		throw new RuntimeException();
	}

	public static PacketSender getSender() {
		throw new RuntimeException();
	}

	public static boolean canSend(CustomPayload.Id<?> type) {
		throw new RuntimeException();
	}

	public interface PlayPayloadHandler<T extends CustomPayload> {
		void receive(T payload, Context context);
	}

	public interface Context {
		MinecraftClient client();

		ClientPlayerEntity player();

		PacketSender responseSender();
	}
}
