package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Attaque d'une entité (stub de compilation). */
public interface AttackEntityCallback {
	Event<AttackEntityCallback> EVENT = null;

	ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, Vec3d hitPos);
}
