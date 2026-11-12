package com.cambocraft.game;

import com.cambocraft.entity.ArtisanEntity;
import com.cambocraft.entity.CamboEntities;
import com.cambocraft.entity.CamboEntitiesFabrique;
import com.cambocraft.util.Log;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

/**
 * Apparition naturelle des PNJ près des joueurs, dans la dimension {@code cambocraft:cambodia}.
 *
 * <p>Le mod possède 16 profils de PNJ et 128 structures générées, mais aucun mécanisme ne les
 * faisait se rencontrer : les PNJ n'apparaissaient que par {@code /summon}. Ce service comble ce
 * manque avec une règle simple et économe : toutes les 30 secondes, si le joueur est au Cambodge et
 * qu'aucun artisan n'est déjà dans un rayon de 48 blocs, un artisan portant un profil aléatoire
 * apparaît sur le sol, à 12–24 blocs, en terrain dégagé.</p>
 *
 * <p>La règle évite deux écueils : peupler la surface d'une foule d'entités (le plafond par
 * joueur est de 3) et apparaître dans l'eau ou dans un mur (le sol est testé avant l'apparition).</p>
 */
public final class SpawnNaturel {
    /** Intervalle d'essai, en ticks (30 secondes). */
    private static final int INTERVALLE_TICKS = 20 * 30;
    /** Distance minimale d'apparition, en blocs. */
    private static final double DISTANCE_MIN = 12.0;
    /** Distance maximale d'apparition, en blocs. */
    private static final double DISTANCE_MAX = 24.0;
    /** Rayon de recherche pour compter les artisans déjà présents. */
    private static final double RAYON_ANTI_DOUBLON = 48.0;
    /** Nombre maximal d'artisans autour d'un même joueur. */
    private static final int MAX_ARTISANS_PAR_JOUEUR = 3;

    private static int compteur;

    private SpawnNaturel() {}

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(SpawnNaturel::surTickMonde);
        Log.info("Cambocraft : apparition naturelle des PNJ armée (intervalle 30 s, 3 par joueur)");
    }

    private static void surTickMonde(ServerWorld monde) {
        if (monde == null || !monde.getRegistryKey().equals(com.cambocraft.world.CambodiaDimension.WORLD_KEY)) {
            return;
        }
        if (compteur++ % INTERVALLE_TICKS != 0) {
            return;
        }
        MinecraftServer serveur = monde.getServer();
        if (serveur == null) {
            return;
        }
        for (ServerPlayerEntity joueur : serveur.getPlayerManager().getPlayerList()) {
            if (joueur.getWorld() != monde) {
                continue;
            }
            essayerApparition(monde, joueur);
        }
    }

    private static void essayerApparition(ServerWorld monde, ServerPlayerEntity joueur) {
        double x = joueur.getX();
        double z = joueur.getZ();
        List<ArtisanEntity> proches = monde.getEntitiesByClass(ArtisanEntity.class,
                new net.minecraft.util.math.Box(x - RAYON_ANTI_DOUBLON, joueur.getY() - 32,
                        z - RAYON_ANTI_DOUBLON, x + RAYON_ANTI_DOUBLON, joueur.getY() + 32,
                        z + RAYON_ANTI_DOUBLON),
                entite -> true);
        if (proches.size() >= MAX_ARTISANS_PAR_JOUEUR) {
            return;
        }
        Random aleatoire = monde.getRandom();
        double angle = aleatoire.nextDouble() * Math.PI * 2.0;
        double distance = DISTANCE_MIN + aleatoire.nextDouble() * (DISTANCE_MAX - DISTANCE_MIN);
        int bx = (int) Math.floor(x + Math.cos(angle) * distance);
        int bz = (int) Math.floor(z + Math.sin(angle) * distance);
        int by = monde.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, bx, bz);
        BlockPos pos = new BlockPos(bx, by, bz);
        if (by <= monde.getBottomY() + 2 || by >= monde.getTopY()) {
            return;
        }
        if (!monde.getBlockState(pos).isAir() || !monde.getBlockState(pos.up()).isAir()) {
            return;
        }
        ArtisanEntity artisan = CamboEntitiesFabrique.artisan(monde, pos, aleatoire);
        if (artisan != null) {
            Log.info("PNJ {} apparu près de {} en {}",
                    artisan.profil(), joueur.getName().getString(), pos);
        }
    }
}
