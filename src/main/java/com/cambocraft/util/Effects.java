package com.cambocraft.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Accès aux effets de statut vanilla par identifiant.
 *
 * <p>Les effets sont résolus depuis le registre ({@code minecraft:regeneration}…) plutôt que par
 * des constantes de classe : la lecture est explicite dans les tables de données, et une faute de
 * frappe est signalée par une exception claire au chargement au lieu d'un comportement silencieux.</p>
 */
public final class Effects {
    private static final Map<String, RegistryEntry<StatusEffect>> CACHE = new HashMap<>();

    private Effects() {}

    /** Effet vanilla, par exemple {@code "regeneration"} ou {@code "minecraft:speed"}. */
    public static RegistryEntry<StatusEffect> vanilla(String path) {
        RegistryEntry<StatusEffect> cached = CACHE.get(path);
        if (cached != null) return cached;
        Identifier id = path.indexOf(':') >= 0 ? Identifier.of(path) : Identifier.ofVanilla(path);
        RegistryEntry<StatusEffect> entry = Registries.STATUS_EFFECT
                .getEntry(id)
                .orElseThrow(() -> new IllegalStateException("Effet de statut inconnu : " + id));
        CACHE.put(path, entry);
        return entry;
    }

    /** Effet du mod, par exemple {@code "resonance"} (voir {@code cambocraft:resonance}). */
    public static RegistryEntry<StatusEffect> cambocraft(String path) {
        return vanilla(Ids.NAMESPACE + ":" + path);
    }
}
