package com.cambocraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Four de forge : four réellement fonctionnel, ouvert d'un clic droit.
 *
 * <p>Le mod comptait 220 blocs purement décoratifs, sans aucune block-entity : la table de forge,
 * le métier à tisser ou la marmite ne faisaient rien. Ce bloc est le premier à ouvrir une interface
 * réelle et à cuire des objets — il réutilise la block-entity du four vanilla, ce qui évite de
 * dupliquer toute la logique de cuisson et de sauvegarde.</p>
 *
 * <p>Il hérite de {@code Block} et non d'{@code AbstractFurnaceBlock} : cette dernière exige un
 * {@code MapCodec} qui complexifie inutilement le bloc, alors que le comportement souhaité (ouvrir
 * l'écran du four) tient en une méthode.</p>
 */
public class FourDeForgeBlock extends Block {
    public FourDeForgeBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FurnaceBlockEntity four) {
                player.openHandledScreen(four);
                player.incrementStat(Stats.INTERACT_WITH_FURNACE);
            }
        }
        return ActionResult.SUCCESS;
    }
}
