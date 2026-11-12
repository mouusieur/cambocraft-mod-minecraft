package com.cambocraft.craft;

import java.util.Random;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Système de forge et d'amélioration d'équipement de {@code +1} à {@code +5} (§7.6).
 *
 * <p>En cas d'échec, le cristal de forge est consommé mais l'arme ou l'armure n'est jamais
 * détruite ni rétrogradée : son niveau actuel est intégralement préservé.</p>
 */
public final class ForgeUpgrade {
    public static final int NIVEAU_MAX = 5;
    public static final String CLE_NIVEAU_FORGE = "cambocraft_forge_niveau";

    /** Multiplicateurs de dégâts / défense pour les niveaux 0 à +5. */
    private static final double[] MULTIPLICATEURS = {1.00, 1.08, 1.16, 1.25, 1.36, 1.50};
    /** Probabilités de succès pour passer au niveau +1, +2, +3, +4, +5. */
    private static final double[] CHANCES_SUCCES = {0.0, 1.00, 0.85, 0.70, 0.50, 0.35};
    /** Coût en Pièces du Lotus par palier. */
    private static final int[] COUTS_PIECES = {0, 15, 35, 75, 150, 300};

    private ForgeUpgrade() {}

    public static double multiplicateur(int niveau) {
        int n = Math.max(0, Math.min(NIVEAU_MAX, niveau));
        return MULTIPLICATEURS[n];
    }

    public static double chanceSucces(int niveauCible) {
        if (niveauCible < 1 || niveauCible > NIVEAU_MAX) return 0.0;
        return CHANCES_SUCCES[niveauCible];
    }

    public static int coutPieces(int niveauCible) {
        if (niveauCible < 1 || niveauCible > NIVEAU_MAX) return 0;
        return COUTS_PIECES[niveauCible];
    }

    public static String cristalRequis(int niveauCible) {
        if (niveauCible <= 2) return "cambocraft:cristal_de_forge_mineur";
        if (niveauCible <= 4) return "cambocraft:cristal_de_forge_majeur";
        return "cambocraft:cristal_de_forge_parfait";
    }

    /** Tente une amélioration du niveau actuel vers {@code niveauActuel + 1}. */
    public static int tenterAmelioration(int niveauActuel, Random random) {
        if (niveauActuel < 0 || niveauActuel >= NIVEAU_MAX) return Math.min(NIVEAU_MAX, Math.max(0, niveauActuel));
        int cible = niveauActuel + 1;
        if (random.nextDouble() < chanceSucces(cible)) {
            return cible;
        }
        return niveauActuel;
    }

    public static int lireNiveau(ItemStack stack) {
        if (stack == null || !stack.contains(DataComponentTypes.CUSTOM_DATA)) return 0;
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 0;
        NbtCompound nbt = comp.copyNbt();
        return Math.max(0, Math.min(NIVEAU_MAX, nbt.getInt(CLE_NIVEAU_FORGE)));
    }

    public static void ecrireNiveau(ItemStack stack, int niveau) {
        if (stack == null) return;
        int n = Math.max(0, Math.min(NIVEAU_MAX, niveau));
        NbtComponent existant = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = existant != null ? existant.copyNbt() : new NbtCompound();
        nbt.putInt(CLE_NIVEAU_FORGE, n);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}
