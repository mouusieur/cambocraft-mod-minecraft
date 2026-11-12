package net.fabricmc.fabric.api.command.v2;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;

/** Enregistrement des commandes (stub de compilation). */
public interface CommandRegistrationCallback {
	Event<CommandRegistrationCallback> EVENT = null;

	void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
			CommandManager.RegistrationEnvironment environment);
}
