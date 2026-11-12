package com.cambocraft.world;

import com.cambocraft.util.Ids;
import java.util.List;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

/**
 * Clés de registre et constantes de la dimension {@code cambocraft:cambodia} et de ses 15 biomes.
 *
 * <p>La dimension possède son propre cycle solaire, sa météo saisonnière, ses 15 biomes et son
 * routeur de densité ({@code data/cambocraft/worldgen/noise_settings/cambodia_noise.json}).</p>
 */
public final class CambodiaDimension {
    /** Identifiant court de la dimension. */
    public static final String ID = "cambodia";

    public static final RegistryKey<DimensionOptions> DIMENSION_KEY =
            RegistryKey.of(RegistryKeys.DIMENSION, Ids.id(ID));
    public static final RegistryKey<World> WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, Ids.id(ID));
    public static final RegistryKey<DimensionType> DIMENSION_TYPE_KEY =
            RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Ids.id(ID));

    // --- Les 15 biomes de la dimension (§4.2) ---
    public static final RegistryKey<Biome> RIZIERES_EN_TERRASSES = Ids.biomeKey("rizieres_en_terrasses");
    public static final RegistryKey<Biome> FORET_TROPICALE = Ids.biomeKey("foret_tropicale");
    public static final RegistryKey<Biome> BAMBOUSERAIE = Ids.biomeKey("bambouseraie");
    public static final RegistryKey<Biome> SAVANE_CLAIRE = Ids.biomeKey("savane_claire");
    public static final RegistryKey<Biome> MANGROVE_ET_MARAIS = Ids.biomeKey("mangrove_et_marais");
    public static final RegistryKey<Biome> RIVIERE_DU_TONLE = Ids.biomeKey("riviere_du_tonle");
    public static final RegistryKey<Biome> GRAND_LAC = Ids.biomeKey("grand_lac");
    public static final RegistryKey<Biome> JUNGLE_EN_RUINES = Ids.biomeKey("jungle_en_ruines");
    public static final RegistryKey<Biome> MONTAGNES_KARSTIQUES = Ids.biomeKey("montagnes_karstiques");
    public static final RegistryKey<Biome> PLATEAU_DE_LATERITE = Ids.biomeKey("plateau_de_laterite");
    public static final RegistryKey<Biome> SANCTUAIRE_DE_LOTUS = Ids.biomeKey("sanctuaire_de_lotus");
    public static final RegistryKey<Biome> PINEDES_SECHES = Ids.biomeKey("pinedes_seches");
    public static final RegistryKey<Biome> PLAINE_INONDABLE = Ids.biomeKey("plaine_inondable");
    public static final RegistryKey<Biome> FORET_DE_KAPOKIERS = Ids.biomeKey("foret_de_kapokiers");
    public static final RegistryKey<Biome> ILES_FLOTTANTES_DES_GARUDAS = Ids.biomeKey("iles_flottantes_des_garudas");

    /** Liste immuable des 15 biomes déclarés dans la dimension. */
    public static final List<RegistryKey<Biome>> BIOMES = List.of(
            RIZIERES_EN_TERRASSES,
            FORET_TROPICALE,
            BAMBOUSERAIE,
            SAVANE_CLAIRE,
            MANGROVE_ET_MARAIS,
            RIVIERE_DU_TONLE,
            GRAND_LAC,
            JUNGLE_EN_RUINES,
            MONTAGNES_KARSTIQUES,
            PLATEAU_DE_LATERITE,
            SANCTUAIRE_DE_LOTUS,
            PINEDES_SECHES,
            PLAINE_INONDABLE,
            FORET_DE_KAPOKIERS,
            ILES_FLOTTANTES_DES_GARUDAS);

    /** Coordonnées du sanctuaire d'arrivée initial dans la dimension (Village de Tonlé, zone 1). */
    public static final int SPAWN_X = 0;
    public static final int SPAWN_Y = 72;
    public static final int SPAWN_Z = 0;

    private CambodiaDimension() {}

    /** Indique si le monde fourni est la dimension {@code cambocraft:cambodia}. */
    public static boolean estCambodge(World monde) {
        return monde != null && WORLD_KEY.equals(monde.getRegistryKey());
    }
}
