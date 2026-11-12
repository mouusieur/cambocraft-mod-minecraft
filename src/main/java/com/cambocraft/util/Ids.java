package com.cambocraft.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;

/** Identifiants du mod : tout passe par ici, jamais d'identifiant écrit à la main ailleurs. */
public final class Ids {
    /** Espace de noms commun à tous les identifiants du mod. */
    public static final String NAMESPACE = "cambocraft";

    private Ids() {}

    /** {@code cambocraft:<chemin>}. */
    public static Identifier id(String path) {
        return Identifier.of(NAMESPACE, path);
    }

    /** Identifiant dans un autre espace de noms (utile pour les balises et les tables de butin). */
    public static Identifier id(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    public static RegistryKey<World> dimensionKey(String path) {
        return RegistryKey.of(RegistryKeys.WORLD, id(path));
    }

    public static RegistryKey<Biome> biomeKey(String path) {
        return RegistryKey.of(RegistryKeys.BIOME, id(path));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String path) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, id(path));
    }

    public static RegistryKey<PlacedFeature> placedFeatureKey(String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, id(path));
    }

    public static RegistryKey<Structure> structureKey(String path) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, id(path));
    }

    /** Identifiant d'une balise du mod (par exemple {@code cambocraft:lotus_stone}). */
    public static Identifier tagId(String path) {
        return id(path);
    }

    /** Vérifie qu'un identifiant appartient bien au mod. */
    public static boolean isCambocraft(Identifier id) {
        return id != null && NAMESPACE.equals(id.getNamespace());
    }
}
