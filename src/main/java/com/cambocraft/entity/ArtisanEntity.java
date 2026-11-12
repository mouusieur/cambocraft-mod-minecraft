package com.cambocraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;

/**
 * Artisan de village : villageois parlant rattaché à un profil de {@code NpcRegistry}.
 *
 * <p>Le nom du profil est stocké en données persistantes ({@code profils} NBT) : un même type
 * d'entité porte donc les 16 PNJ du catalogue. Un clic droit ouvre le dialogue du profil.</p>
 */
public class ArtisanEntity extends VillagerEntity {
    /** Identifiant du profil PNJ (clé de {@code data/cambocraft/cambo/npcs/}). */
    private String profil = "marchand_de_riz";

    public ArtisanEntity(EntityType<? extends ArtisanEntity> type, World world) {
        super(type, world, VillagerType.PLAINS);
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
            com.cambocraft.npc.ArtisanDialogueService.ouvrir(serveur, monde, this, profil);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
