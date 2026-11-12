package net.fabricmc.fabric.api.event;

import java.util.function.Function;

/** Fabrique d'événements Fabric (stub de compilation). */
public final class EventFactory {
	private EventFactory() {
	}

	public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
		throw new RuntimeException();
	}
}
