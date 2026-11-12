package com.cambocraft.fishing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Canne à pêche de Cambocraft.
 *
 * <p>Le mini-jeu de pêche est ouvert par un paquet serveur vers le client (voir
 * {@code com.cambocraft.fishing.FishingMinigame}) ; le tirage du poisson et la résolution du
 * succès sont décidés côté serveur. Cette classe ne porte que le déclenchement et la durabilité.</p>
 */
public class CamboFishingRodItem extends Item {
    public CamboFishingRodItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }
        return FishingMinigame.begin(user, stack);
    }
}
