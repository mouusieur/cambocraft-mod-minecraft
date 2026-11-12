package com.cambocraft.entity;

import com.cambocraft.npc.NpcRegistry;
import com.cambocraft.util.Ids;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * Apparition des entités du mod dans le monde.
 *
 * <p>Les PNJ naissent près des structures générées (les 128 `.nbt` du catalogue) et tirent leur
 * profil dans {@code NpcRegistry} : les 16 PNJ sont donc réellement rencontrés en jeu, pas seulement
 * déclarés. Les gardiens de boss sont invoqués par {@code BossEncounterService}.</p>
 */
public final class CamboEntitiesFabrique {
    private CamboEntitiesFabrique() {}

    /** Fait apparaître un artisan portant l'un des 16 profils, au hasard. */
    public static ArtisanEntity artisan(ServerWorld monde, BlockPos pos, Random aleatoire) {
        ArtisanEntity entite = CamboEntities.ARTISAN.create(monde);
        if (entite == null) {
            return null;
        }
        List<String> profils = List.copyOf(NpcRegistry.pnjs().keySet());
        String profil = profils.isEmpty()
                ? "marchand_de_riz"
                : profils.get(aleatoire.nextInt(profils.size()));
        entite.definirProfil(profil);
        entite.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
        monde.spawnEntity(entite);
        return entite;
    }

    /** Invoque un gardien de boss rattaché à un profil de {@code CombatRegistry}. */
    public static BossGuardianEntity gardien(ServerWorld monde, BlockPos pos, String profil) {
        BossGuardianEntity entite = CamboEntities.GARDIEN.create(monde);
        if (entite == null) {
            return null;
        }
        entite.definirProfil(profil, 1.0);
        entite.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
        monde.spawnEntity(entite);
        return entite;
    }
}
