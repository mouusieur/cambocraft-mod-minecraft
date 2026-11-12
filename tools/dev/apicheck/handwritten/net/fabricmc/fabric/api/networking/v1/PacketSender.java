package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.packet.CustomPayload;

/** Envoi différé de paquets (stub de compilation). */
public interface PacketSender {
	void sendPacket(CustomPayload payload);
}
