package com.cambocraft.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.PacketByteBuf;

/**
 * Petites primitives d'écriture de listes de chaînes dans un paquet.
 *
 * <p>Elles évitent de dupliquer la même boucle dans chacun des paquets de {@link CamboPayloads} et
 * bornent la taille lue côté client : un paquet malformé ne peut pas provoquer d'allocation
 * démesurée, puisque chaque entrée est tronquée à {@link #TAILLE_MAX_ENTREE} caractères et la
 * liste à {@link #MAX_ENTREES} éléments.</p>
 */
public final class NetworkLists {
    /** Nombre maximal d'entrées d'une liste réseau. */
    public static final int MAX_ENTREES = 512;
    /** Longueur maximale d'une chaîne transmise. */
    public static final int TAILLE_MAX_ENTREE = 256;

    private NetworkLists() {}

    /** Écrit une liste de chaînes (taille puis entrées). */
    public static void ecrireListe(PacketByteBuf buf, List<String> valeurs) {
        List<String> copie = valeurs == null ? List.of() : valeurs;
        int n = Math.min(copie.size(), MAX_ENTREES);
        buf.writeVarInt(n);
        for (int i = 0; i < n; i++) {
            String valeur = copie.get(i) == null ? "" : copie.get(i);
            if (valeur.length() > TAILLE_MAX_ENTREE) {
                valeur = valeur.substring(0, TAILLE_MAX_ENTREE);
            }
            buf.writeString(valeur);
        }
    }

    /** Lit une liste de chaînes écrite par {@link #ecrireListe}. */
    public static List<String> lireListe(PacketByteBuf buf) {
        int n = Math.max(0, Math.min(buf.readVarInt(), MAX_ENTREES));
        List<String> valeurs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            valeurs.add(buf.readString(TAILLE_MAX_ENTREE));
        }
        return valeurs;
    }
}
