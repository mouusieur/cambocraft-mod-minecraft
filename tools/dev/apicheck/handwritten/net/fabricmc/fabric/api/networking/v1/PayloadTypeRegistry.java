package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** Enregistrement des types de paquets personnalisés (stub de compilation). */
public interface PayloadTypeRegistry<B> {
	<T extends CustomPayload> CustomPayload.Type<B, T> register(CustomPayload.Id<T> id, PacketCodec<? super B, T> codec);

	static PayloadTypeRegistry<RegistryByteBuf> playC2S() {
		throw new RuntimeException();
	}

	static PayloadTypeRegistry<RegistryByteBuf> playS2C() {
		throw new RuntimeException();
	}
}
