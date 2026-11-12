package com.cambocraft.registry;

import com.cambocraft.util.Ids;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

/**
 * Enregistrement des objets de jeu.
 *
 * <p>Toutes les classes {@code Cambo*} de ce paquet appellent uniquement ces méthodes.
 * Elles sont générées à partir de {@code assets_src/registry/*.yml} par
 * {@code tools/gen_registry.py} : le catalogue YAML est la source de vérité, le Java en est
 * le reflet vérifiable.</p>
 */
public final class Registrar {
    private Registrar() {}

    /** Enregistre une valeur dans un registre du jeu sous l'espace de noms du mod. */
    public static <T> T register(Registry<T> registry, String path, T value) {
        return Registry.register(registry, Ids.id(path), value);
    }

    /** Bloc simple : la fabrique reçoit les réglages (dureté, son, lumière…). */
    public static <T extends Block> T block(String path, Function<AbstractBlock.Settings, T> factory, AbstractBlock.Settings settings) {
        T bloc = factory.apply(settings);
        Registry.register(Registries.BLOCK, Ids.id(path), bloc);
        return bloc;
    }

    /** Bloc dont le constructeur standard {@code Block(AbstractBlock.Settings)} suffit. */
    public static Block block(String path, AbstractBlock.Settings settings) {
        return register(Registries.BLOCK, path, new Block(settings));
    }

    /**
     * Variante pour les blocs à constructeur spécifique (dalles, escaliers, murets, barrières,
     * piliers à axe…).
     *
     * <p>Elle est déclarée <b>avant</b> la surcharge {@code (String, Settings, Item.Settings)} :
     * les deux acceptent trois arguments, et le compilateur doit d'abord essayer la forme
     * générique — sinon {@code new PillarBlock(settings)} est rejeté.</p>
     */
    public static <B extends Block> B blockWithItem(String path, B block, Item.Settings itemSettings) {
        register(Registries.BLOCK, path, block);
        register(Registries.ITEM, path, new BlockItem(block, itemSettings));
        return block;
    }

    /** Bloc associé à son objet d'inventaire (la très grande majorité des blocs). */
    public static Block blockWithItem(String path, AbstractBlock.Settings settings, Item.Settings itemSettings) {
        return blockWithItem(path, new Block(settings), itemSettings);
    }

    /** Objet simple. */
    public static <T extends Item> T item(String path, Function<Item.Settings, T> factory, Item.Settings settings) {
        T objet = factory.apply(settings);
        Registry.register(Registries.ITEM, Ids.id(path), objet);
        return objet;
    }

    /** Objet avec constructeur standard {@code Item(Item.Settings)}. */
    public static Item item(String path, Item.Settings settings) {
        return register(Registries.ITEM, path, new Item(settings));
    }
}
