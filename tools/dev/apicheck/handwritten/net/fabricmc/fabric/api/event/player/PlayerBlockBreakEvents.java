package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Événements de casse de bloc (stub de compilation). */
public final class PlayerBlockBreakEvents {
	private PlayerBlockBreakEvents() {
	}

	public static final Event<After> AFTER = null;
	public static final Event<Before> BEFORE = null;
	public static final Event<Canceled> CANCELED = null;

	public interface After {
		void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity);
	}

	public interface Before {
		boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity);
	}

	public interface Canceled {
		void onBlockBreakCanceled(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity);
	}
}
