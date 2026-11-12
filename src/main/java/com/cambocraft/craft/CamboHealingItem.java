package com.cambocraft.craft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Objet de soin à utilisation directe (bandage, onguent).
 *
 * <p>Le soin est appliqué côté serveur uniquement : le client ne fait qu'afficher l'animation.
 * Un temps de recharge évite d'enchaîner les soins instantanés, et le soin ne dépasse jamais le
 * maximum de vie du joueur.</p>
 */
public class CamboHealingItem extends Item {
    private final float amount;
    private final int cooldownTicks;

    public CamboHealingItem(Settings settings, float amount, int cooldownTicks) {
        super(settings);
        this.amount = amount;
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getHealth() >= user.getMaxHealth()) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            user.heal(amount);
            stack.decrement(1);
            user.getItemCooldownManager().set(this, cooldownTicks);
            world.sendEntityStatus(user, (byte) 9);
        }
        return TypedActionResult.success(stack);
    }
}
