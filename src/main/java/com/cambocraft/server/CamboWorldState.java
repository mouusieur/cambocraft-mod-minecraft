package com.cambocraft.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

/**
 * Sauvegarde du monde Cambocraft : état des joueurs, index des PNJ et état des rencontres de boss.
 *
 * <p>Stockée avec le monde via {@link PersistentState}, elle survit au redémarrage du serveur.
 * C'est ce qui distingue une vraie progression d'un simple cache mémoire.</p>
 */
public class CamboWorldState extends PersistentState {
    /** Nom du fichier de sauvegarde dans {@code world/data/}. */
    public static final String ID = "cambocraft";

    private final Map<UUID, NbtCompound> joueurs = new HashMap<>();

    public static CamboWorldState obtenir(MinecraftServer serveur) {
        @SuppressWarnings("unchecked")
        java.util.function.Supplier<PersistentState> fabrique = CamboWorldState::new;
        @SuppressWarnings("unchecked")
        java.util.function.BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, PersistentState> lecture =
                (nbt, registres) -> CamboWorldState.lire(nbt, registres);
        PersistentState.Type<CamboWorldState> type = new PersistentState.Type(fabrique, lecture, null);
        return (CamboWorldState) serveur.getOverworld().getPersistentStateManager().getOrCreate(type, ID);
    }

    /** NBT sauvegardé d'un joueur, ou {@code null} s'il n'a jamais joué. */
    public NbtCompound joueur(UUID uuid) {
        return joueurs.get(uuid);
    }

    /** Enregistre le NBT d'un joueur et marque la sauvegarde comme modifiée. */
    public void ecrire(UUID uuid, NbtCompound nbt) {
        joueurs.put(uuid, nbt);
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registres) {
        NbtList liste = new NbtList();
        for (Map.Entry<UUID, NbtCompound> e : joueurs.entrySet()) {
            NbtCompound entree = new NbtCompound();
            entree.putString("uuid", e.getKey().toString());
            entree.put("donnees", (net.minecraft.nbt.NbtElement) e.getValue());
            liste.add(entree);
        }
        nbt.put("joueurs", liste);
        return nbt;
    }

    /** Lit le NBT du monde (appelé par le gestionnaire de sauvegarde). */
    public static CamboWorldState lire(NbtCompound nbt, RegistryWrapper.WrapperLookup registres) {
        CamboWorldState etat = new CamboWorldState();
        if (nbt.contains("joueurs")) {
            NbtList liste = nbt.getList("joueurs", 10);
            for (int i = 0; i < liste.size(); i++) {
                NbtCompound entree = liste.getCompound(i);
                try {
                    etat.joueurs.put(UUID.fromString(entree.getString("uuid")),
                            entree.getCompound("donnees"));
                } catch (IllegalArgumentException ignore) {
                    // UUID corrompu : on ignore l'entrée plutôt que de refuser la sauvegarde.
                }
            }
        }
        return etat;
    }
}
