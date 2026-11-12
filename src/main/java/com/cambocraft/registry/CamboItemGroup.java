package com.cambocraft.registry;

import com.cambocraft.util.Ids;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Groupe d'objets créatif de Cambocraft.
 *
 * <p>Le contenu est rempli automatiquement avec tous les objets de l'espace de noms
 * {@code cambocraft} présents dans le registre : aucun oubli possible quand un objet est ajouté
 * au catalogue.</p>
 */
public final class CamboItemGroup {
    private CamboItemGroup() {}

    public static final RegistryKey<ItemGroup> KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Ids.id("principal"));

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, KEY,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.cambocraft.principal"))
                        .icon(() -> new ItemStack(CamboItems.PIECE_DU_LOTUS))
                        .entries((context, entries) -> {
                            for (var item : Registries.ITEM) {
                                Identifier id = Registries.ITEM.getId(item);
                                if (Ids.NAMESPACE.equals(id.getNamespace())) {
                                    entries.add(item);
                                }
                            }
                        })
                        .build());
    }
}
