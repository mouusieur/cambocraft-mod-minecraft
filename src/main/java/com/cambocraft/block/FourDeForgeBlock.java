package com.cambocraft.block;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Four de forge : four réellement fonctionnel, ouvert d'un clic droit.
 *
 * <p>Le mod comptait 220 blocs purement décoratifs, sans aucune block-entity : la table de forge,
 * le métier à tisser ou la marmite ne faisaient rien. Ce bloc est le premier à ouvrir une interface
 * réelle et à cuire des objets — il réutilise la block-entity du four vanilla, ce qui évite de
 * dupliquer toute la logique de cuisson et de sauvegarde.</p>
 */
public class FourDeForgeBlock extends AbstractFurnaceBlock {
    public FourDeForgeBlock(net.minecraft.block.AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FurnaceBlockEntity four) {
            player.openHandledScreen(four);
            player.incrementStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
}
