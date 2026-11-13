package com.cambocraft.client;

import com.cambocraft.util.Log;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Déclencheur de pouvoirs piloté par fichiers, pour l'automatisation.
 *
 * <p>Sous Xvfb (client OpenGL logiciel de la CI), les appuis de touche sont peu fiables :
 * le focus clavier suit le pointeur et se perd après les clics de menu. Ce déclencheur lit donc
 * le fichier {@code <répertoire de jeu>/pouvoir.demande} : son contenu est l'index de
 * l'emplacement actif à lancer (0, 1 ou 2). L'activation emprunte exactement le même chemin
 * qu'une vraie touche — {@link CambocraftKeys#lancerEmplacement(int)} → paquet C2S → serveur —
 * si bien qu'une preuve obtenue par ce biais vaut preuve du chemin clavier.</p>
 *
 * <p>Le résultat est journalisé dans {@code pouvoirs-journal.txt} : la sortie standard du client
 * n'est pas lisible depuis la CI, on y écrit donc ce qui a été demandé et quand.</p>
 */
@Environment(EnvType.CLIENT)
public final class ActivationAutomatique {
    private static final String DEMANDE = "pouvoir.demande";
    private static final String JOURNAL = "pouvoirs-journal.txt";

    private static Path repertoire;

    private ActivationAutomatique() {}

    public static void enregistrer() {
        ClientTickEvents.END_CLIENT_TICK.register(ActivationAutomatique::aChaqueTick);
    }

    private static void aChaqueTick(MinecraftClient client) {
        if (client == null || client.runDirectory == null || client.player == null) {
            return;
        }
        if (repertoire == null) {
            repertoire = client.runDirectory.toPath();
        }
        Path demande = repertoire.resolve(DEMANDE);
        if (!Files.exists(demande)) {
            return;
        }
        int emplacement;
        try {
            String contenu = Files.readString(demande, StandardCharsets.UTF_8).trim();
            emplacement = Integer.parseInt(contenu);
            Files.deleteIfExists(demande);
        } catch (IOException | NumberFormatException e) {
            journal("demande illisible : " + e.getMessage());
            return;
        }
        if (emplacement < 0 || emplacement >= 3) {
            journal("emplacement hors bornes ignoré : " + emplacement);
            return;
        }
        journal("activation demandée : emplacement " + emplacement);
        CambocraftKeys.lancerEmplacement(emplacement);
        if (client.player != null) {
            journal("état au déclenchement : " + CambocraftKeys.etatPouvoirs());
        }
    }

    /** Journal fichier : la sortie standard du client n'est pas lisible depuis la CI. */
    private static void journal(String ligne) {
        Log.info("Activation automatique : " + ligne);
        if (repertoire == null) {
            return;
        }
        try {
            Files.writeString(repertoire.resolve(JOURNAL), ligne + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Log.info("Activation automatique : journal indisponible (" + e.getMessage() + ")");
        }
    }
}
