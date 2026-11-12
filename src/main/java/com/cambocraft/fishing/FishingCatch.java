package com.cambocraft.fishing;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Informations attachées à une prise (espèce, taille, prix de vente).
 *
 * <p>Les données voyagent avec l'objet : elles survivent aux coffres, aux échanges et aux
 * sauvegardes. On les range dans le composant vanilla {@code minecraft:custom_data}, sans créer de
 * nouveau composant, pour rester compatible avec les outils et les datapacks.</p>
 */
public final class FishingCatch {
    private static final String ESPECE = "cambocraft_espece";
    private static final String TAILLE = "cambocraft_taille_cm";
    private static final String PRIX = "cambocraft_prix";

    private FishingCatch() {}

    /** Marque une pile comme prise de pêche. */
    public static void marquer(ItemStack pile, String espece, int tailleCm, int prix) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(ESPECE, espece);
        nbt.putInt(TAILLE, tailleCm);
        nbt.putInt(PRIX, prix);
        NbtComponent composant = NbtComponent.of(nbt);
        pile.set(DataComponentTypes.CUSTOM_DATA, composant);
    }

    /** Espèce enregistrée sur la pile, ou {@code null} si ce n'est pas une prise. */
    public static String espece(ItemStack pile) {
        NbtComponent composant = pile.get(DataComponentTypes.CUSTOM_DATA);
        if (composant == null) return null;
        NbtCompound nbt = composant.copyNbt();
        return nbt.contains(ESPECE) ? nbt.getString(ESPECE) : null;
    }

    public static int taille(ItemStack pile) {
        NbtComponent composant = pile.get(DataComponentTypes.CUSTOM_DATA);
        if (composant == null) return 0;
        return composant.copyNbt().getInt(TAILLE);
    }

    public static int prix(ItemStack pile) {
        NbtComponent composant = pile.get(DataComponentTypes.CUSTOM_DATA);
        if (composant == null) return 0;
        return composant.copyNbt().getInt(PRIX);
    }

    public static boolean estPrise(ItemStack pile) {
        return espece(pile) != null;
    }
}
