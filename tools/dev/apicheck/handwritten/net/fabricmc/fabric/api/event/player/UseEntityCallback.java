package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/** Interaction avec une entité (stub de compilation). */
public interface UseEntityCallback {
	Event<UseEntityCallback> EVENT = null;

	ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, Object hitResult);
}
