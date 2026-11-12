package com.cambocraft.fishing;

import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.Seasons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Contexte d'une partie de pêche : zone, saison, heure et chance du joueur.
 *
 * <p>La zone est déduite du biome (table {@link #ZONE_PAR_BIOME}) et de la hauteur d'eau ; la
 * chance combine la canne utilisée, la pluie et la maîtrise de pêche du joueur (voir
 * {@code docs/DESIGN.md}). Toutes les valeurs sont des fonctions pures du contexte : elles sont
 * testables sans lancer le jeu.</p>
 */
public final class FishingContext {
    /** Chance de base d'un joueur sans maîtrise. */
    public static final double CHANCE_BASE = 0.10;
    /** Bonus de chance par point de maîtrise de pêche (0,03 par niveau, plafonné). */
    public static final double CHANCE_PAR_MAITRISE = 0.03;
    public static final double MAITRISE_MAX = 5;

    private FishingContext() {}

    /** Zone de pêche déduite du biome ; « riviere » par défaut. */
    public static String zone(PlayerEntity joueur) {
        String biome = joueur.getWorld().getBiome(joueur.getBlockPos()).getKey()
                .map(cle -> cle.getValue().getPath()).orElse("");
        if (biome.contains("grand_lac") || biome.contains("lac")) return "grand_lac";
        if (biome.contains("mangrove") || biome.contains("marais")) return "mangrove";
        if (biome.contains("riziaire") || biome.contains("riz")) return "riziere";
        if (biome.contains("sanctuaire")) return "sanctuaire";
        if (biome.contains("karst") || biome.contains("montagne")) return "montagne_riviere";
        return "riviere";
    }

    public static String saison(World monde) {
        return Seasons.saison(monde).id;
    }

    /** Chance effective : base + maîtrise + pluie (la pluie excite les poissons). */
    public static double chance(PlayerEntity joueur, ItemStack canne) {
        double chance = CHANCE_BASE;
        chance += Math.min(MAITRISE_MAX, maitrise(joueur)) * CHANCE_PAR_MAITRISE;
        if (joueur.getWorld().isRaining()) chance += 0.08;
        if (estCanneDeLotus(canne)) chance += 0.10;
        return Math.min(1.0, chance);
    }

    /** Maîtrise de pêche du joueur (0 par défaut, branchée sur la progression en phase 5). */
    public static double maitrise(PlayerEntity joueur) {
        return 0.0;
    }

    /** Vrai si la canne est celle du mod (bonus de chance dédié). */
    public static boolean estCanneDeLotus(ItemStack canne) {
        Identifier id = Registries.ITEM.getId(canne.getItem());
        return "cambocraft".equals(id.getNamespace()) && id.getPath().equals("canne_a_pecher");
    }

    /** Nombre de saisons écoulées depuis le début du monde (utile aux événements saisonniers). */
    public static Season saisonEnum(World monde) {
        return Seasons.saison(monde);
    }
}
