package net.fabricmc.fabric.api.event;

/** Événement Fabric (stub de compilation). */
public abstract class Event<T> {
	public abstract T invoker();

	public void register(T listener) {
		throw new RuntimeException();
	}
}
