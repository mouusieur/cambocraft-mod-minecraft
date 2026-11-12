package com.cambocraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Gardien de boss : créature hostile liée à un profil de {@code CombatRegistry}.
 *
 * <p>Le profil (points de vie, phases, dégâts) est appliqué à l'apparition par
 * {@code BossEncounterService}, qui lit les 12 boss du catalogue. La classe reste volontairement
 * générique : un seul type d'entité sert les 12 rencontres.</p>
 */
public class BossGuardianEntity extends PathAwareEntity {
    private String profil = "gardien_du_seuil";
    private double multiplicateurVie = 1.0;

    public BossGuardianEntity(EntityType<? extends BossGuardianEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder attributs() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 120.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.26)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    public String profil() {
        return profil;
    }

    public void definirProfil(String profil, double multiplicateurVie) {
        this.profil = profil;
        this.multiplicateurVie = multiplicateurVie;
        setCustomName(net.minecraft.text.Text.literal(profil));
        setCustomNameVisible(true);
    }

    @Override
    public void initGoals() {
        // Poursuite simple : suffisant pour valider la boucle de combat sans IA lourde.
        goalSelector.add(1, new net.minecraft.entity.ai.goal.MeleeAttackGoal(this, 1.0, true));
        goalSelector.add(2, new net.minecraft.entity.ai.goal.WanderAroundFarGoal(this, 0.8, 0.001f));
        goalSelector.add(3, new net.minecraft.entity.ai.goal.LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        goalSelector.add(4, new net.minecraft.entity.ai.goal.LookAroundGoal(this));
        targetSelector.add(1, new net.minecraft.entity.ai.goal.ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
}
