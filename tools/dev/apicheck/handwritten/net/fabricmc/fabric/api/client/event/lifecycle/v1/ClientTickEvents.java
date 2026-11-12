package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;

/** Ticks du client (stub de compilation, signatures de Fabric API 1.21.1). */
public final class ClientTickEvents {
	private ClientTickEvents() {
	}

	public static final Event<EndTick> END_CLIENT_TICK = null;

	public interface EndTick {
		void onEndTick(MinecraftClient client);
	}
}
