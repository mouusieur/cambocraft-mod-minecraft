package com.cambocraft.registry;

import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WoodType;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;

/**
 * Blocs de Cambocraft.
 *
 * <p>Fichier GÉNÉRÉ par {@code tools/gen_registry.py} à partir de
 * {@code assets_src/registry/blocks.yml} : ne pas modifier à la main, régénérer.</p>
 */
public final class CamboBlocks {
    private CamboBlocks() {}

    public static final Block PIERRE_DU_LOTUS = Registrar.blockWithItem("pierre_du_lotus", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_SLAB = Registrar.blockWithItem("pierre_du_lotus_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_STAIRS = Registrar.blockWithItem("pierre_du_lotus_stairs", new StairsBlock(PIERRE_DU_LOTUS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_WALL = Registrar.blockWithItem("pierre_du_lotus_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_POLIE = Registrar.blockWithItem("pierre_du_lotus_polie", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_POLIE_SLAB = Registrar.blockWithItem("pierre_du_lotus_polie_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_POLIE_STAIRS = Registrar.blockWithItem("pierre_du_lotus_polie_stairs", new StairsBlock(PIERRE_DU_LOTUS_POLIE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_POLIE_WALL = Registrar.blockWithItem("pierre_du_lotus_polie_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_LOTUS = Registrar.blockWithItem("brique_de_lotus", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block BRIQUE_DE_LOTUS_SLAB = Registrar.blockWithItem("brique_de_lotus_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_LOTUS_STAIRS = Registrar.blockWithItem("brique_de_lotus_stairs", new StairsBlock(BRIQUE_DE_LOTUS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_LOTUS_WALL = Registrar.blockWithItem("brique_de_lotus_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block LATERITE = Registrar.blockWithItem("laterite", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 3f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block LATERITE_SLAB = Registrar.blockWithItem("laterite_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block LATERITE_STAIRS = Registrar.blockWithItem("laterite_stairs", new StairsBlock(LATERITE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block LATERITE_WALL = Registrar.blockWithItem("laterite_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_LATERITE = Registrar.blockWithItem("brique_de_laterite", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.4f, 4f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block BRIQUE_DE_LATERITE_SLAB = Registrar.blockWithItem("brique_de_laterite_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.4f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_LATERITE_STAIRS = Registrar.blockWithItem("brique_de_laterite_stairs", new StairsBlock(BRIQUE_DE_LATERITE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.4f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_LATERITE_WALL = Registrar.blockWithItem("brique_de_laterite_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.4f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER = Registrar.blockWithItem("gres_khmer", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.8f, 0.8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block GRES_KHMER_SLAB = Registrar.blockWithItem("gres_khmer_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.8f, 0.8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER_STAIRS = Registrar.blockWithItem("gres_khmer_stairs", new StairsBlock(GRES_KHMER.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.8f, 0.8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER_WALL = Registrar.blockWithItem("gres_khmer_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.8f, 0.8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block ARDOISE_DES_OMBRES = Registrar.blockWithItem("ardoise_des_ombres", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE), new Item.Settings());
    public static final Block ARDOISE_DES_OMBRES_SLAB = Registrar.blockWithItem("ardoise_des_ombres_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)), new Item.Settings());
    public static final Block ARDOISE_DES_OMBRES_STAIRS = Registrar.blockWithItem("ardoise_des_ombres_stairs", new StairsBlock(ARDOISE_DES_OMBRES.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)), new Item.Settings());
    public static final Block ARDOISE_DES_OMBRES_WALL = Registrar.blockWithItem("ardoise_des_ombres_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)), new Item.Settings());
    public static final Block CALCAIRE_KARSTIQUE = Registrar.blockWithItem("calcaire_karstique", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block CALCAIRE_KARSTIQUE_SLAB = Registrar.blockWithItem("calcaire_karstique_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block CALCAIRE_KARSTIQUE_STAIRS = Registrar.blockWithItem("calcaire_karstique_stairs", new StairsBlock(CALCAIRE_KARSTIQUE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block CALCAIRE_KARSTIQUE_WALL = Registrar.blockWithItem("calcaire_karstique_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block MARBRE_BLANC = Registrar.blockWithItem("marbre_blanc", AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block MARBRE_BLANC_SLAB = Registrar.blockWithItem("marbre_blanc_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block MARBRE_BLANC_STAIRS = Registrar.blockWithItem("marbre_blanc_stairs", new StairsBlock(MARBRE_BLANC.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block MARBRE_BLANC_WALL = Registrar.blockWithItem("marbre_blanc_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_TEMPLE = Registrar.blockWithItem("brique_de_temple", AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block BRIQUE_DE_TEMPLE_SLAB = Registrar.blockWithItem("brique_de_temple_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_TEMPLE_STAIRS = Registrar.blockWithItem("brique_de_temple_stairs", new StairsBlock(BRIQUE_DE_TEMPLE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_TEMPLE_WALL = Registrar.blockWithItem("brique_de_temple_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DE_TEMPLE_PILLAR = Registrar.blockWithItem("brique_de_temple_pillar", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block TUILE_DE_TERRE_CUITE = Registrar.blockWithItem("tuile_de_terre_cuite", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 4f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block TUILE_DE_TERRE_CUITE_SLAB = Registrar.blockWithItem("tuile_de_terre_cuite_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block TUILE_DE_TERRE_CUITE_STAIRS = Registrar.blockWithItem("tuile_de_terre_cuite_stairs", new StairsBlock(TUILE_DE_TERRE_CUITE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER_POLI = Registrar.blockWithItem("gres_khmer_poli", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(1f, 2f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block GRES_KHMER_POLI_SLAB = Registrar.blockWithItem("gres_khmer_poli_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(1f, 2f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER_POLI_STAIRS = Registrar.blockWithItem("gres_khmer_poli_stairs", new StairsBlock(GRES_KHMER_POLI.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(1f, 2f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER_POLI_WALL = Registrar.blockWithItem("gres_khmer_poli_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(1f, 2f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block GRES_KHMER_POLI_PILLAR = Registrar.blockWithItem("gres_khmer_poli_pillar", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(1f, 2f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block BRIQUE_DES_OMBRES = Registrar.blockWithItem("brique_des_ombres", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2.2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE), new Item.Settings());
    public static final Block BRIQUE_DES_OMBRES_SLAB = Registrar.blockWithItem("brique_des_ombres_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2.2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)), new Item.Settings());
    public static final Block BRIQUE_DES_OMBRES_STAIRS = Registrar.blockWithItem("brique_des_ombres_stairs", new StairsBlock(BRIQUE_DES_OMBRES.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2.2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)), new Item.Settings());
    public static final Block BRIQUE_DES_OMBRES_WALL = Registrar.blockWithItem("brique_des_ombres_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2.2f, 9f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)), new Item.Settings());
    public static final Block PIERRE_MOUSSUE_DE_TEMPLE = Registrar.blockWithItem("pierre_moussue_de_temple", AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block PIERRE_MOUSSUE_DE_TEMPLE_SLAB = Registrar.blockWithItem("pierre_moussue_de_temple_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_MOUSSUE_DE_TEMPLE_STAIRS = Registrar.blockWithItem("pierre_moussue_de_temple_stairs", new StairsBlock(PIERRE_MOUSSUE_DE_TEMPLE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_MOUSSUE_DE_TEMPLE_WALL = Registrar.blockWithItem("pierre_moussue_de_temple_wall", new WallBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(1.8f, 7f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block TECK_LOG = Registrar.blockWithItem("teck_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block TECK_STRIPPED_LOG = Registrar.blockWithItem("teck_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block TECK_PLANKS = Registrar.blockWithItem("teck_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block TECK_STAIRS = Registrar.blockWithItem("teck_stairs", new StairsBlock(TECK_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block TECK_SLAB = Registrar.blockWithItem("teck_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block TECK_FENCE = Registrar.blockWithItem("teck_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block TECK_FENCE_GATE = Registrar.blockWithItem("teck_fence_gate", new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block TECK_LEAVES = Registrar.blockWithItem("teck_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block TECK_SAPLING = Registrar.blockWithItem("teck_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block KAPOKIER_LOG = Registrar.blockWithItem("kapokier_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block KAPOKIER_STRIPPED_LOG = Registrar.blockWithItem("kapokier_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block KAPOKIER_PLANKS = Registrar.blockWithItem("kapokier_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block KAPOKIER_STAIRS = Registrar.blockWithItem("kapokier_stairs", new StairsBlock(KAPOKIER_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block KAPOKIER_SLAB = Registrar.blockWithItem("kapokier_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block KAPOKIER_FENCE = Registrar.blockWithItem("kapokier_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block KAPOKIER_FENCE_GATE = Registrar.blockWithItem("kapokier_fence_gate", new FenceGateBlock(WoodType.BIRCH, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block KAPOKIER_LEAVES = Registrar.blockWithItem("kapokier_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block KAPOKIER_SAPLING = Registrar.blockWithItem("kapokier_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block COCOTIER_LOG = Registrar.blockWithItem("cocotier_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block COCOTIER_STRIPPED_LOG = Registrar.blockWithItem("cocotier_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block COCOTIER_PLANKS = Registrar.blockWithItem("cocotier_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block COCOTIER_STAIRS = Registrar.blockWithItem("cocotier_stairs", new StairsBlock(COCOTIER_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block COCOTIER_SLAB = Registrar.blockWithItem("cocotier_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block COCOTIER_FENCE = Registrar.blockWithItem("cocotier_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block COCOTIER_FENCE_GATE = Registrar.blockWithItem("cocotier_fence_gate", new FenceGateBlock(WoodType.JUNGLE, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.6f, 1.6f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block COCOTIER_LEAVES = Registrar.blockWithItem("cocotier_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block COCOTIER_SAPLING = Registrar.blockWithItem("cocotier_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block MANGUIER_LOG = Registrar.blockWithItem("manguier_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block MANGUIER_STRIPPED_LOG = Registrar.blockWithItem("manguier_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block MANGUIER_PLANKS = Registrar.blockWithItem("manguier_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block MANGUIER_STAIRS = Registrar.blockWithItem("manguier_stairs", new StairsBlock(MANGUIER_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block MANGUIER_SLAB = Registrar.blockWithItem("manguier_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block MANGUIER_FENCE = Registrar.blockWithItem("manguier_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block MANGUIER_FENCE_GATE = Registrar.blockWithItem("manguier_fence_gate", new FenceGateBlock(WoodType.JUNGLE, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block MANGUIER_LEAVES = Registrar.blockWithItem("manguier_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block MANGUIER_SAPLING = Registrar.blockWithItem("manguier_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block JACQUIER_LOG = Registrar.blockWithItem("jacquier_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block JACQUIER_STRIPPED_LOG = Registrar.blockWithItem("jacquier_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block JACQUIER_PLANKS = Registrar.blockWithItem("jacquier_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block JACQUIER_STAIRS = Registrar.blockWithItem("jacquier_stairs", new StairsBlock(JACQUIER_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block JACQUIER_SLAB = Registrar.blockWithItem("jacquier_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block JACQUIER_FENCE = Registrar.blockWithItem("jacquier_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block JACQUIER_FENCE_GATE = Registrar.blockWithItem("jacquier_fence_gate", new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(2f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block JACQUIER_LEAVES = Registrar.blockWithItem("jacquier_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block JACQUIER_SAPLING = Registrar.blockWithItem("jacquier_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block BAMBOU_LEAVES = Registrar.blockWithItem("bambou_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block BAMBOU_SAPLING = Registrar.blockWithItem("bambou_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_LOG = Registrar.blockWithItem("palmier_a_sucre_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_STRIPPED_LOG = Registrar.blockWithItem("palmier_a_sucre_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_PLANKS = Registrar.blockWithItem("palmier_a_sucre_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_STAIRS = Registrar.blockWithItem("palmier_a_sucre_stairs", new StairsBlock(PALMIER_A_SUCRE_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_SLAB = Registrar.blockWithItem("palmier_a_sucre_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_FENCE = Registrar.blockWithItem("palmier_a_sucre_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_FENCE_GATE = Registrar.blockWithItem("palmier_a_sucre_fence_gate", new FenceGateBlock(WoodType.JUNGLE, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_LEAVES = Registrar.blockWithItem("palmier_a_sucre_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PALMIER_A_SUCRE_SAPLING = Registrar.blockWithItem("palmier_a_sucre_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PALETUVIER_LOG = Registrar.blockWithItem("paletuvier_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALETUVIER_STRIPPED_LOG = Registrar.blockWithItem("paletuvier_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALETUVIER_PLANKS = Registrar.blockWithItem("paletuvier_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block PALETUVIER_STAIRS = Registrar.blockWithItem("paletuvier_stairs", new StairsBlock(PALETUVIER_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALETUVIER_SLAB = Registrar.blockWithItem("paletuvier_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALETUVIER_FENCE = Registrar.blockWithItem("paletuvier_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALETUVIER_FENCE_GATE = Registrar.blockWithItem("paletuvier_fence_gate", new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PALETUVIER_LEAVES = Registrar.blockWithItem("paletuvier_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PALETUVIER_SAPLING = Registrar.blockWithItem("paletuvier_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_LOG = Registrar.blockWithItem("pin_du_cambodge_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_STRIPPED_LOG = Registrar.blockWithItem("pin_du_cambodge_stripped_log", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_PLANKS = Registrar.blockWithItem("pin_du_cambodge_planks", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_STAIRS = Registrar.blockWithItem("pin_du_cambodge_stairs", new StairsBlock(PIN_DU_CAMBODGE_PLANKS.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_SLAB = Registrar.blockWithItem("pin_du_cambodge_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_FENCE = Registrar.blockWithItem("pin_du_cambodge_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_FENCE_GATE = Registrar.blockWithItem("pin_du_cambodge_fence_gate", new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.8f, 1.8f).requiresTool().sounds(BlockSoundGroup.WOOD)), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_LEAVES = Registrar.blockWithItem("pin_du_cambodge_leaves", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.3f, 0.3f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PIN_DU_CAMBODGE_SAPLING = Registrar.blockWithItem("pin_du_cambodge_sapling", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f, 0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PIERRE_DU_LOTUS_SCULPTEE = Registrar.blockWithItem("pierre_du_lotus_sculptee", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block GRES_SCULPTE = Registrar.blockWithItem("gres_sculpte", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.8f, 0.8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block BASALTE_COLONNAIRE = Registrar.blockWithItem("basalte_colonnaire", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PIERRE_ERODEE = Registrar.blockWithItem("pierre_erodee", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.3f, 5f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block SOL_DE_TEMPLE = Registrar.blockWithItem("sol_de_temple", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block PIERRE_DES_OMBRES_RUNIQUE = Registrar.blockWithItem("pierre_des_ombres_runique", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(2.5f, 10f).luminance(state -> 3).requiresTool().sounds(BlockSoundGroup.DEEPSLATE), new Item.Settings());
    public static final Block MINERAI_DE_LOTUS = Registrar.blockWithItem("minerai_de_lotus", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block TERRE_LATERITIQUE = Registrar.blockWithItem("terre_lateritique", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.6f, 0.6f).requiresTool().sounds(BlockSoundGroup.GRAVEL), new Item.Settings());
    public static final Block BOUE_DE_RIZIAIRE = Registrar.blockWithItem("boue_de_riziaire", AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.7f, 0.7f).requiresTool().sounds(BlockSoundGroup.MUD), new Item.Settings());
    public static final Block SABLE_DE_RIVIERE = Registrar.blockWithItem("sable_de_riviere", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.6f, 0.6f).requiresTool().sounds(BlockSoundGroup.SAND), new Item.Settings());
    public static final Block GRAVIER_DE_RIVIERE = Registrar.blockWithItem("gravier_de_riviere", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(0.7f, 0.7f).requiresTool().sounds(BlockSoundGroup.GRAVEL), new Item.Settings());
    public static final Block TERRE_CUITE = Registrar.blockWithItem("terre_cuite", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.2f, 3f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block BAMBOU_PLANCHES_BLOC = Registrar.blockWithItem("bambou_planches_bloc", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.4f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD), new Item.Settings());
    public static final Block BAMBOU_BLOC = Registrar.blockWithItem("bambou_bloc", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.5f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD)), new Item.Settings());
    public static final Block BAMBOU_TRESSE = Registrar.blockWithItem("bambou_tresse", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.4f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD), new Item.Settings());
    public static final Block BAMBOU_PLANCHES_STAIRS = Registrar.blockWithItem("bambou_planches_stairs", new StairsBlock(BAMBOU_PLANCHES_BLOC.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.4f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD)), new Item.Settings());
    public static final Block BAMBOU_PLANCHES_SLAB = Registrar.blockWithItem("bambou_planches_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.4f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD)), new Item.Settings());
    public static final Block BAMBOU_FENCE = Registrar.blockWithItem("bambou_fence", new FenceBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.4f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD)), new Item.Settings());
    public static final Block BAMBOU_FENCE_GATE = Registrar.blockWithItem("bambou_fence_gate", new FenceGateBlock(WoodType.BAMBOO, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.4f, 3f).requiresTool().sounds(BlockSoundGroup.BAMBOO_WOOD)), new Item.Settings());
    public static final Block PAILLE_DE_RIZ = Registrar.blockWithItem("paille_de_riz", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.4f, 0.4f).requiresTool().sounds(BlockSoundGroup.CROP), new Item.Settings());
    public static final Block TOIT_DE_CHAUME = Registrar.blockWithItem("toit_de_chaume", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.5f, 0.5f).requiresTool().sounds(BlockSoundGroup.CROP), new Item.Settings());
    public static final Block PLANCHE_DE_TOIT = Registrar.blockWithItem("planche_de_toit", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(0.8f, 1.5f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block TUILE_VERNISSEE = Registrar.blockWithItem("tuile_vernissee", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.2f, 4f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block RIZ_EMPILE = Registrar.blockWithItem("riz_empile", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.4f, 0.4f).sounds(BlockSoundGroup.CROP), new Item.Settings());
    public static final Block MUR_DE_ROSEAUX = Registrar.blockWithItem("mur_de_roseaux", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.4f, 0.4f).requiresTool().sounds(BlockSoundGroup.CROP), new Item.Settings());
    public static final Block FEUILLE_DE_LOTUS = Registrar.blockWithItem("feuille_de_lotus", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.1f, 0.1f).sounds(BlockSoundGroup.CROP)), new Item.Settings());
    public static final Block FLEUR_DE_LOTUS = Registrar.blockWithItem("fleur_de_lotus", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block LOTUS_ROSE = Registrar.blockWithItem("lotus_rose", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block LOTUS_BLANC = Registrar.blockWithItem("lotus_blanc", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block FLEUR_DE_FRANGIPANIER = Registrar.blockWithItem("fleur_de_frangipanier", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block FLEUR_D_HIBISCUS = Registrar.blockWithItem("fleur_d_hibiscus", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block FLEUR_DE_BOUGAINVILLIER = Registrar.blockWithItem("fleur_de_bougainvillier", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block CITRONNELLE = Registrar.blockWithItem("citronnelle", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block PIMENT = Registrar.blockWithItem("piment", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block AUBERGINE = Registrar.blockWithItem("aubergine", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block CONCOMBRE = Registrar.blockWithItem("concombre", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block LANTERNE_DE_BAMBOU = Registrar.blockWithItem("lanterne_de_bambou", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(0.3f).luminance(state -> 14).requiresTool().sounds(BlockSoundGroup.LANTERN), new Item.Settings());
    public static final Block TORCHE_DE_RESINE = Registrar.blockWithItem("torche_de_resine", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(0f).noCollision().nonOpaque().luminance(state -> 12).sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block ENCENS = Registrar.blockWithItem("encens", AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(0.2f).noCollision().nonOpaque().luminance(state -> 5).sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block BANNIERE_ROUGE = Registrar.blockWithItem("banniere_rouge", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_RED).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block BANNIERE_OR = Registrar.blockWithItem("banniere_or", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block BANNIERE_BLEUE = Registrar.blockWithItem("banniere_bleue", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block BANNIERE_VERTE = Registrar.blockWithItem("banniere_verte", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block BANNIERE_BLANCHE = Registrar.blockWithItem("banniere_blanche", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block BANNIERE_VIOLETTE = Registrar.blockWithItem("banniere_violette", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block TISSU_SUSPENDU = Registrar.blockWithItem("tissu_suspendu", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.2f).noCollision().nonOpaque().sounds(BlockSoundGroup.WOOL), new Item.Settings());
    public static final Block ETAL_DE_MARCHE = Registrar.blockWithItem("etal_de_marche", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(0.9f, 1.5f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block JARRE = Registrar.blockWithItem("jarre", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.5f, 0.5f).sounds(BlockSoundGroup.DECORATED_POT), new Item.Settings());
    public static final Block PANIER = Registrar.blockWithItem("panier", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.4f, 0.4f).sounds(BlockSoundGroup.CROP), new Item.Settings());
    public static final Block STATUE_DE_PIERRE = Registrar.blockWithItem("statue_de_pierre", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2.5f, 9f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    /** Balustrade ornementale : le naga est un protecteur bienveillant, jamais un ennemi. */
    public static final Block NAGA_BALUSTRADE = Registrar.blockWithItem("naga_balustrade", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2.5f, 9f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block NAGA_BALUSTRADE_STAIRS = Registrar.blockWithItem("naga_balustrade_stairs", new StairsBlock(NAGA_BALUSTRADE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2.5f, 9f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block NAGA_BALUSTRADE_SLAB = Registrar.blockWithItem("naga_balustrade_slab", new SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2.5f, 9f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    /** Décor de galerie : éléments d'architecture traités avec respect, jamais moqués. */
    public static final Block BAS_RELIEF = Registrar.blockWithItem("bas_relief", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    /** Tour-visage : élément de décor et de donjon, traité avec respect. */
    public static final Block VISAGE_DE_PIERRE = Registrar.blockWithItem("visage_de_pierre", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3f, 10f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block GARUDA = Registrar.blockWithItem("garuda", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(2.5f, 9f).requiresTool().sounds(BlockSoundGroup.METAL), new Item.Settings());
    public static final Block AUTEL = Registrar.blockWithItem("autel", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block CLOCHE_DE_TEMPLE = Registrar.blockWithItem("cloche_de_temple", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.METAL), new Item.Settings());
    public static final Block GONG = Registrar.blockWithItem("gong", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.METAL), new Item.Settings());
    public static final Block TAMBOUR = Registrar.blockWithItem("tambour", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(0.8f, 1f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block VITRAIL_DE_TEMPLE = Registrar.blockWithItem("vitrail_de_temple", AbstractBlock.Settings.create().mapColor(MapColor.GOLD).strength(0.4f, 0.4f).nonOpaque().sounds(BlockSoundGroup.GLASS), new Item.Settings());
    public static final Block FOUR_DE_FORGE = Registrar.blockWithItem("four_de_forge", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3.5f, 10f).luminance(state -> 6).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block TABLE_DE_FORGE = Registrar.blockWithItem("table_de_forge", AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).strength(3f, 9f).requiresTool().sounds(BlockSoundGroup.METAL), new Item.Settings());
    public static final Block METIER_A_TISSER = Registrar.blockWithItem("metier_a_tisser", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1f, 2f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block CUVE_DE_SOIE = Registrar.blockWithItem("cuve_de_soie", AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.8f, 1.5f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block MARMITE = Registrar.blockWithItem("marmite", AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).strength(1.2f, 3f).requiresTool().sounds(BlockSoundGroup.METAL), new Item.Settings());
    public static final Block MORTIER = Registrar.blockWithItem("mortier", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1f, 3f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block TOUR_DE_POTERIE = Registrar.blockWithItem("tour_de_poterie", AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(0.8f, 1.5f).requiresTool().sounds(BlockSoundGroup.WOOD), new Item.Settings());
    public static final Block ATELIER_DE_SCULPTURE = Registrar.blockWithItem("atelier_de_sculpture", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2f, 6f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block CREUSET_DE_FUSION = Registrar.blockWithItem("creuset_de_fusion", AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(3f, 9f).luminance(state -> 4).requiresTool().sounds(BlockSoundGroup.METAL), new Item.Settings());
    /** Cadre du portail du Cambodge : 12 blocs entourant un bol de riz béni. */
    public static final Block CADRE_DU_PORTAIL = Registrar.blockWithItem("cadre_du_portail", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3f, 12f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block COEUR_DU_PORTAIL = Registrar.blockWithItem("coeur_du_portail", AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(3f, 12f).luminance(state -> 11).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK), new Item.Settings());
    public static final Block TELEPORTEUR_DE_LOTUS = Registrar.blockWithItem("teleporteur_de_lotus", AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(2.5f, 9f).luminance(state -> 7).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK), new Item.Settings());
    public static final Block CRISTAL_DE_LOTUS = Registrar.blockWithItem("cristal_de_lotus", AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(1f, 3f).luminance(state -> 9).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK), new Item.Settings());
    public static final Block BLOC_DE_LOTUS = Registrar.blockWithItem("bloc_de_lotus", AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(1f, 3f).luminance(state -> 4).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK), new Item.Settings());
    public static final Block MOUSSON_VASE = Registrar.blockWithItem("mousson_vase", AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.6f, 0.6f).requiresTool().sounds(BlockSoundGroup.MUD), new Item.Settings());
    /** Dalle à mécanisme des temples-pyramides. */
    public static final Block DALLE_DE_PRESSION_ANCIENNE = Registrar.blockWithItem("dalle_de_pression_ancienne", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block SOCLE_D_OFFRANDE = Registrar.blockWithItem("socle_d_offrande", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2f, 8f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block LION_DE_PIERRE = Registrar.blockWithItem("lion_de_pierre", AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(2.8f, 10f).requiresTool().sounds(BlockSoundGroup.STONE), new Item.Settings());
    public static final Block PILIER_DE_LATERITE = Registrar.blockWithItem("pilier_de_laterite", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1.4f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block PILIER_DE_LOTUS = Registrar.blockWithItem("pilier_de_lotus", new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(1.5f, 6f).requiresTool().sounds(BlockSoundGroup.STONE)), new Item.Settings());
    public static final Block NATTE_DE_JONC = Registrar.blockWithItem("natte_de_jonc", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(0.2f).sounds(BlockSoundGroup.CROP)), new Item.Settings());
    public static final Block TAPIS_DE_SOIE_KRAMA = Registrar.blockWithItem("tapis_de_soie_krama", new CarpetBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_RED).strength(0.2f).sounds(BlockSoundGroup.WOOL)), new Item.Settings());
    public static final Block MURIER_BUISSON = Registrar.blockWithItem("murier_buisson", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0.4f, 0.4f).requiresTool().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block RIZ_SAUVAGE = Registrar.blockWithItem("riz_sauvage", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block HERBE_DE_SAVANE = Registrar.blockWithItem("herbe_de_savane", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block FOUGERE_TROPICALE = Registrar.blockWithItem("fougere_tropicale", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block ORCHIDEE_KHMER = Registrar.blockWithItem("orchidee_khmer", AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0f).noCollision().nonOpaque().sounds(BlockSoundGroup.GRASS), new Item.Settings());
    public static final Block RACINE_DE_BANYAN = Registrar.blockWithItem("racine_de_banyan", AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(0.3f).noCollision().nonOpaque().sounds(BlockSoundGroup.WOOD), new Item.Settings());

    /** Tous les blocs du catalogue, dans leur ordre de déclaration. */
    public static final List<Block> ALL = List.of(
            PIERRE_DU_LOTUS,
            PIERRE_DU_LOTUS_SLAB,
            PIERRE_DU_LOTUS_STAIRS,
            PIERRE_DU_LOTUS_WALL,
            PIERRE_DU_LOTUS_POLIE,
            PIERRE_DU_LOTUS_POLIE_SLAB,
            PIERRE_DU_LOTUS_POLIE_STAIRS,
            PIERRE_DU_LOTUS_POLIE_WALL,
            BRIQUE_DE_LOTUS,
            BRIQUE_DE_LOTUS_SLAB,
            BRIQUE_DE_LOTUS_STAIRS,
            BRIQUE_DE_LOTUS_WALL,
            LATERITE,
            LATERITE_SLAB,
            LATERITE_STAIRS,
            LATERITE_WALL,
            BRIQUE_DE_LATERITE,
            BRIQUE_DE_LATERITE_SLAB,
            BRIQUE_DE_LATERITE_STAIRS,
            BRIQUE_DE_LATERITE_WALL,
            GRES_KHMER,
            GRES_KHMER_SLAB,
            GRES_KHMER_STAIRS,
            GRES_KHMER_WALL,
            ARDOISE_DES_OMBRES,
            ARDOISE_DES_OMBRES_SLAB,
            ARDOISE_DES_OMBRES_STAIRS,
            ARDOISE_DES_OMBRES_WALL,
            CALCAIRE_KARSTIQUE,
            CALCAIRE_KARSTIQUE_SLAB,
            CALCAIRE_KARSTIQUE_STAIRS,
            CALCAIRE_KARSTIQUE_WALL,
            MARBRE_BLANC,
            MARBRE_BLANC_SLAB,
            MARBRE_BLANC_STAIRS,
            MARBRE_BLANC_WALL,
            BRIQUE_DE_TEMPLE,
            BRIQUE_DE_TEMPLE_SLAB,
            BRIQUE_DE_TEMPLE_STAIRS,
            BRIQUE_DE_TEMPLE_WALL,
            BRIQUE_DE_TEMPLE_PILLAR,
            TUILE_DE_TERRE_CUITE,
            TUILE_DE_TERRE_CUITE_SLAB,
            TUILE_DE_TERRE_CUITE_STAIRS,
            GRES_KHMER_POLI,
            GRES_KHMER_POLI_SLAB,
            GRES_KHMER_POLI_STAIRS,
            GRES_KHMER_POLI_WALL,
            GRES_KHMER_POLI_PILLAR,
            BRIQUE_DES_OMBRES,
            BRIQUE_DES_OMBRES_SLAB,
            BRIQUE_DES_OMBRES_STAIRS,
            BRIQUE_DES_OMBRES_WALL,
            PIERRE_MOUSSUE_DE_TEMPLE,
            PIERRE_MOUSSUE_DE_TEMPLE_SLAB,
            PIERRE_MOUSSUE_DE_TEMPLE_STAIRS,
            PIERRE_MOUSSUE_DE_TEMPLE_WALL,
            TECK_LOG,
            TECK_STRIPPED_LOG,
            TECK_PLANKS,
            TECK_STAIRS,
            TECK_SLAB,
            TECK_FENCE,
            TECK_FENCE_GATE,
            TECK_LEAVES,
            TECK_SAPLING,
            KAPOKIER_LOG,
            KAPOKIER_STRIPPED_LOG,
            KAPOKIER_PLANKS,
            KAPOKIER_STAIRS,
            KAPOKIER_SLAB,
            KAPOKIER_FENCE,
            KAPOKIER_FENCE_GATE,
            KAPOKIER_LEAVES,
            KAPOKIER_SAPLING,
            COCOTIER_LOG,
            COCOTIER_STRIPPED_LOG,
            COCOTIER_PLANKS,
            COCOTIER_STAIRS,
            COCOTIER_SLAB,
            COCOTIER_FENCE,
            COCOTIER_FENCE_GATE,
            COCOTIER_LEAVES,
            COCOTIER_SAPLING,
            MANGUIER_LOG,
            MANGUIER_STRIPPED_LOG,
            MANGUIER_PLANKS,
            MANGUIER_STAIRS,
            MANGUIER_SLAB,
            MANGUIER_FENCE,
            MANGUIER_FENCE_GATE,
            MANGUIER_LEAVES,
            MANGUIER_SAPLING,
            JACQUIER_LOG,
            JACQUIER_STRIPPED_LOG,
            JACQUIER_PLANKS,
            JACQUIER_STAIRS,
            JACQUIER_SLAB,
            JACQUIER_FENCE,
            JACQUIER_FENCE_GATE,
            JACQUIER_LEAVES,
            JACQUIER_SAPLING,
            BAMBOU_LEAVES,
            BAMBOU_SAPLING,
            PALMIER_A_SUCRE_LOG,
            PALMIER_A_SUCRE_STRIPPED_LOG,
            PALMIER_A_SUCRE_PLANKS,
            PALMIER_A_SUCRE_STAIRS,
            PALMIER_A_SUCRE_SLAB,
            PALMIER_A_SUCRE_FENCE,
            PALMIER_A_SUCRE_FENCE_GATE,
            PALMIER_A_SUCRE_LEAVES,
            PALMIER_A_SUCRE_SAPLING,
            PALETUVIER_LOG,
            PALETUVIER_STRIPPED_LOG,
            PALETUVIER_PLANKS,
            PALETUVIER_STAIRS,
            PALETUVIER_SLAB,
            PALETUVIER_FENCE,
            PALETUVIER_FENCE_GATE,
            PALETUVIER_LEAVES,
            PALETUVIER_SAPLING,
            PIN_DU_CAMBODGE_LOG,
            PIN_DU_CAMBODGE_STRIPPED_LOG,
            PIN_DU_CAMBODGE_PLANKS,
            PIN_DU_CAMBODGE_STAIRS,
            PIN_DU_CAMBODGE_SLAB,
            PIN_DU_CAMBODGE_FENCE,
            PIN_DU_CAMBODGE_FENCE_GATE,
            PIN_DU_CAMBODGE_LEAVES,
            PIN_DU_CAMBODGE_SAPLING,
            PIERRE_DU_LOTUS_SCULPTEE,
            GRES_SCULPTE,
            BASALTE_COLONNAIRE,
            PIERRE_ERODEE,
            SOL_DE_TEMPLE,
            PIERRE_DES_OMBRES_RUNIQUE,
            MINERAI_DE_LOTUS,
            TERRE_LATERITIQUE,
            BOUE_DE_RIZIAIRE,
            SABLE_DE_RIVIERE,
            GRAVIER_DE_RIVIERE,
            TERRE_CUITE,
            BAMBOU_PLANCHES_BLOC,
            BAMBOU_BLOC,
            BAMBOU_TRESSE,
            BAMBOU_PLANCHES_STAIRS,
            BAMBOU_PLANCHES_SLAB,
            BAMBOU_FENCE,
            BAMBOU_FENCE_GATE,
            PAILLE_DE_RIZ,
            TOIT_DE_CHAUME,
            PLANCHE_DE_TOIT,
            TUILE_VERNISSEE,
            RIZ_EMPILE,
            MUR_DE_ROSEAUX,
            FEUILLE_DE_LOTUS,
            FLEUR_DE_LOTUS,
            LOTUS_ROSE,
            LOTUS_BLANC,
            FLEUR_DE_FRANGIPANIER,
            FLEUR_D_HIBISCUS,
            FLEUR_DE_BOUGAINVILLIER,
            CITRONNELLE,
            PIMENT,
            AUBERGINE,
            CONCOMBRE,
            LANTERNE_DE_BAMBOU,
            TORCHE_DE_RESINE,
            ENCENS,
            BANNIERE_ROUGE,
            BANNIERE_OR,
            BANNIERE_BLEUE,
            BANNIERE_VERTE,
            BANNIERE_BLANCHE,
            BANNIERE_VIOLETTE,
            TISSU_SUSPENDU,
            ETAL_DE_MARCHE,
            JARRE,
            PANIER,
            STATUE_DE_PIERRE,
            NAGA_BALUSTRADE,
            NAGA_BALUSTRADE_STAIRS,
            NAGA_BALUSTRADE_SLAB,
            BAS_RELIEF,
            VISAGE_DE_PIERRE,
            GARUDA,
            AUTEL,
            CLOCHE_DE_TEMPLE,
            GONG,
            TAMBOUR,
            VITRAIL_DE_TEMPLE,
            FOUR_DE_FORGE,
            TABLE_DE_FORGE,
            METIER_A_TISSER,
            CUVE_DE_SOIE,
            MARMITE,
            MORTIER,
            TOUR_DE_POTERIE,
            ATELIER_DE_SCULPTURE,
            CREUSET_DE_FUSION,
            CADRE_DU_PORTAIL,
            COEUR_DU_PORTAIL,
            TELEPORTEUR_DE_LOTUS,
            CRISTAL_DE_LOTUS,
            BLOC_DE_LOTUS,
            MOUSSON_VASE,
            DALLE_DE_PRESSION_ANCIENNE,
            SOCLE_D_OFFRANDE,
            LION_DE_PIERRE,
            PILIER_DE_LATERITE,
            PILIER_DE_LOTUS,
            NATTE_DE_JONC,
            TAPIS_DE_SOIE_KRAMA,
            MURIER_BUISSON,
            RIZ_SAUVAGE,
            HERBE_DE_SAVANE,
            FOUGERE_TROPICALE,
            ORCHIDEE_KHMER,
            RACINE_DE_BANYAN
    );

    /** Blocs à rendre en mode découpé (végétaux, fleurs, cultures) — utilisé par le client. */
    public static final List<Block> CUTOUT = List.of(
            TECK_SAPLING,
            KAPOKIER_SAPLING,
            COCOTIER_SAPLING,
            MANGUIER_SAPLING,
            JACQUIER_SAPLING,
            BAMBOU_SAPLING,
            PALMIER_A_SUCRE_SAPLING,
            PALETUVIER_SAPLING,
            PIN_DU_CAMBODGE_SAPLING,
            FLEUR_DE_LOTUS,
            LOTUS_ROSE,
            LOTUS_BLANC,
            FLEUR_DE_FRANGIPANIER,
            FLEUR_D_HIBISCUS,
            FLEUR_DE_BOUGAINVILLIER,
            CITRONNELLE,
            PIMENT,
            AUBERGINE,
            CONCOMBRE,
            TORCHE_DE_RESINE,
            ENCENS,
            TISSU_SUSPENDU,
            VITRAIL_DE_TEMPLE,
            RIZ_SAUVAGE,
            HERBE_DE_SAVANE,
            FOUGERE_TROPICALE,
            ORCHIDEE_KHMER,
            RACINE_DE_BANYAN
    );
}

