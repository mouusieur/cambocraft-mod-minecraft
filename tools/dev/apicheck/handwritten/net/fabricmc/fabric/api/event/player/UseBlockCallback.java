package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

/** Clic droit sur un bloc (stub de compilation). */
public interface UseBlockCallback {
	Event<UseBlockCallback> EVENT = null;

	ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult);

	/** Variante moderne (1.21) : reçoit la pile d'objets. */
	interface WithStack {
		ActionResult interact(PlayerEntity player, World world, Hand hand, ItemStack stack, BlockHitResult hitResult);
	}
}
