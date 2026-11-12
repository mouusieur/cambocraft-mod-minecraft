package com.cambocraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import com.cambocraft.npc.ArtisanDialogueService;

/**
 * Artisan de village : PNJ pacifique rattaché à un profil de {@code NpcRegistry}.
 *
 * <p>Le nom du profil est porté par l'entité ({@code profil}) : un seul type d'entité sert donc les
 * 16 PNJ du catalogue. Un clic droit ouvre le dialogue du profil, publié dans le chat natif avec
 * des choix cliquables.</p>
 *
 * <p>Elle hérite de {@code PathAwareEntity} et non de {@code VillagerEntity} : cette dernière exige
 * un {@code VillagerType} et une initialisation de cerveau (Brain) que le mod n'a pas à reproduire.</p>
 */
public class ArtisanEntity extends PathAwareEntity {
    /** Identifiant du profil PNJ (clé de {@code data/cambocraft/cambo/npcs/}). */
    private String profil = "marchand_de_riz";

    public ArtisanEntity(EntityType<? extends ArtisanEntity> type, World world) {
        super(type, world);
    }

    /** Attributs : pacifique, lent, robuste. */
    public static DefaultAttributeContainer.Builder attributs() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    public String profil() {
        return profil;
    }

    public void definirProfil(String profil) {
        this.profil = profil;
        setCustomName(Text.literal(profil));
        setCustomNameVisible(true);
    }

    @Override
    public ActionResult interactMob(PlayerEntity joueur, Hand main) {
        if (joueur instanceof ServerPlayerEntity serveur && getWorld() instanceof ServerWorld monde) {
            ArtisanDialogueService.ouvrir(serveur, monde, this, profil);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
