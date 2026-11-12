package com.cambocraft.world;

import com.cambocraft.registry.CamboBlocks;
import com.cambocraft.registry.CamboItems;
import com.cambocraft.util.Log;
import java.util.function.BiPredicate;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Portail de Pierre du Lotus reliant le monde normal à la dimension {@code cambocraft:cambodia}.
 *
 * <p>Construction sans commande (§4.1) : le joueur dispose un anneau horizontal 3×3 de
 * {@link CamboBlocks#CADRE_DU_PORTAIL} (ou de {@link CamboBlocks#PIERRE_DU_LOTUS_SCULPTEE}) autour
 * d'un bloc central ({@link CamboBlocks#SOCLE_D_OFFRANDE} ou {@link CamboBlocks#PIERRE_DU_LOTUS_POLIE}),
 * puis dépose un {@link CamboItems#BOL_DE_RIZ_BENI} au centre par clic droit. Le bol est consommé,
 * le centre devient un {@link CamboBlocks#COEUR_DU_PORTAIL} lumineux et tout clic droit sur ce cœur
 * transporte le joueur vers le sanctuaire d'accueil du Village de Tonlé (et inversement).</p>
 */
public final class LotusPortal {
    private LotusPortal() {}

    public static void init() {
        UseBlockCallback.EVENT.register(LotusPortal::surClicBloc);
    }

    /**
     * Vérifie en géométrie pure qu'un anneau 3×3 horizontal autour de (0, 0) est entièrement formé
     * de blocs de cadre valides.
     */
    public static boolean verifierAnneauHorizontal(BiPredicate<Integer, Integer> estCadre) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (!estCadre.test(dx, dz)) return false;
            }
        }
        return true;
    }

    public static boolean estBlocCadre(Block bloc) {
        return bloc == CamboBlocks.CADRE_DU_PORTAIL
                || bloc == CamboBlocks.PIERRE_DU_LOTUS_SCULPTEE
                || bloc == CamboBlocks.PIERRE_DU_LOTUS;
    }

    public static boolean estBlocCentreInitial(Block bloc) {
        return bloc == CamboBlocks.SOCLE_D_OFFRANDE
                || bloc == CamboBlocks.PIERRE_DU_LOTUS_POLIE
                || bloc == CamboBlocks.CADRE_DU_PORTAIL;
    }

    public static boolean cadreValideAutour(World monde, BlockPos centre) {
        return verifierAnneauHorizontal((dx, dz) -> {
            BlockState etat = monde.getBlockState(centre.add(dx, 0, dz));
            return etat != null && estBlocCadre(etat.getBlock());
        });
    }

    private static ActionResult surClicBloc(PlayerEntity joueur, World monde, Hand main, BlockHitResult cible) {
        if (monde == null || cible == null) return ActionResult.PASS;
        BlockPos pos = cible.getBlockPos();
        BlockState etat = monde.getBlockState(pos);
        if (etat == null) return ActionResult.PASS;
        Block bloc = etat.getBlock();

        // 1. Traversée d'un portail déjà actif
        if (bloc == CamboBlocks.COEUR_DU_PORTAIL) {
            if (!monde.isClient && joueur instanceof ServerPlayerEntity serveurJoueur) {
                traverserPortail(serveurJoueur);
            }
            return ActionResult.SUCCESS;
        }

        // 2. Activation d'un cadre par le Bol de Riz Béni posé au centre
        ItemStack enMain = joueur.getStackInHand(main);
        if (enMain == null || enMain.getItem() != CamboItems.BOL_DE_RIZ_BENI) {
            return ActionResult.PASS;
        }
        if (!estBlocCentreInitial(bloc) || !cadreValideAutour(monde, pos)) {
            return ActionResult.PASS;
        }
        if (!monde.isClient && joueur instanceof ServerPlayerEntity serveurJoueur) {
            if (!serveurJoueur.isCreative()) {
                enMain.decrement(1);
            }
            monde.setBlockState(pos, CamboBlocks.COEUR_DU_PORTAIL.getDefaultState());
            serveurJoueur.sendMessage(Text.translatable("cambocraft.portail.ouverture"), true);
            Log.info("Portail du Cambodge ouvert par {} en {}", serveurJoueur.getUuidAsString(), pos);
            traverserPortail(serveurJoueur);
        }
        return ActionResult.SUCCESS;
    }

    /** Transporte le joueur entre le monde normal et {@code cambocraft:cambodia}. */
    public static void traverserPortail(ServerPlayerEntity joueur) {
        ServerWorld origine = joueur.getServerWorld();
        if (origine == null) return;
        MinecraftServer serveur = origine.getServer();
        if (serveur == null) return;

        boolean versCambodge = !CambodiaDimension.estCambodge(origine);
        ServerWorld destination = serveur.getWorld(
                versCambodge ? CambodiaDimension.WORLD_KEY : World.OVERWORLD);
        if (destination == null) {
            joueur.sendMessage(Text.translatable("cambocraft.portail.indisponible"), true);
            return;
        }

        BlockPos arrivee = new BlockPos(
                CambodiaDimension.SPAWN_X,
                CambodiaDimension.SPAWN_Y,
                CambodiaDimension.SPAWN_Z);
        preparerPlateformeArrivee(destination, arrivee);
        joueur.teleport(
                destination,
                arrivee.getX() + 0.5,
                arrivee.getY() + 1.0,
                arrivee.getZ() + 1.5,
                joueur.getYaw(),
                joueur.getPitch());
        joueur.sendMessage(
                Text.translatable(versCambodge ? "cambocraft.portail.arrivee" : "cambocraft.portail.retour"),
                true);
    }

    /** Construit un parvis de Pierre du Lotus avec un cœur de retour au point d'arrivée. */
    private static void preparerPlateformeArrivee(ServerWorld monde, BlockPos centre) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos sol = centre.add(dx, 0, dz);
                if (dx == 0 && dz == 0) {
                    monde.setBlockState(sol, CamboBlocks.COEUR_DU_PORTAIL.getDefaultState());
                } else if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) {
                    monde.setBlockState(sol, CamboBlocks.CADRE_DU_PORTAIL.getDefaultState());
                } else {
                    monde.setBlockState(sol, CamboBlocks.PIERRE_DU_LOTUS_POLIE.getDefaultState());
                }
            }
        }
        for (int coinX : new int[]{-2, 2}) {
            for (int coinZ : new int[]{-2, 2}) {
                monde.setBlockState(centre.add(coinX, 1, coinZ), CamboBlocks.PILIER_DE_LOTUS.getDefaultState());
                monde.setBlockState(centre.add(coinX, 2, coinZ), CamboBlocks.LANTERNE_DE_BAMBOU.getDefaultState());
            }
        }
    }
}
