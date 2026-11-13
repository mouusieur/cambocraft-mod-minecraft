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
import net.minecraft.client.util.ScreenshotRecorder;

/**
 * Déclencheur de captures d'écran piloté par fichiers, pour l'automatisation.
 *
 * <p>Dans la CI (client OpenGL logiciel sous Xvfb, sans gestionnaire de fenêtres),
 * les appuis de touche sont peu fiables : le focus clavier suit le pointeur et se
 * perd après les clics de menu. Plutôt que de simuler F2, on surveille un fichier
 * {@code <repertoire de jeu>/capture.demande} : dès qu'il apparaît, le client écrit
 * une capture de son framebuffer dans le fichier indiqué (ou {@value #DEFAUT}).
 * La demande est supprimée, ce qui vaut acquittement.</p>
 *
 * <p>Le mécanisme est inerte en jeu normal : sans fichier de demande, il ne fait
 * qu'un test d'existence par tick (une fraction de milliseconde).</p>
 */
@Environment(EnvType.CLIENT)
public final class CapturesAutomatiques {
    private static final String DEMANDE = "capture.demande";
    private static final String JOURNAL = "captures-journal.txt";
    private static final String DEFAUT = "capture-active.png";
    private static final int DELAI_MINIMAL = 10;
    private static final int BATTEMENT = 200;

    private static Path repertoire;
    private static int tics;

    private CapturesAutomatiques() {}

    public static void enregistrer() {
        ClientTickEvents.END_CLIENT_TICK.register(CapturesAutomatiques::tick);
    }

    private static void tick(MinecraftClient client) {
        tics++;
        if (client.runDirectory == null) {
            return;
        }
        if (repertoire == null) {
            repertoire = client.runDirectory.toPath();
            journal("arme ; repertoire=" + repertoire);
        }
        if (tics % BATTEMENT == 0) {
            journal("battement tic " + tics + " ; fps " + client.getCurrentFps());
        }
        // On laisse passer quelques tics : au demarrage le framebuffer n'est pas pret.
        if (tics < DELAI_MINIMAL) {
            return;
        }
        Path demande = repertoire.resolve(DEMANDE);
        if (!Files.exists(demande)) {
            return;
        }
        String nom = DEFAUT;
        try {
            String contenu = Files.readString(demande, StandardCharsets.UTF_8).trim();
            if (!contenu.isEmpty()) {
                // Nom simple uniquement : pas de chemin, pas de remontee de repertoire.
                nom = contenu.replaceAll("[^A-Za-z0-9._-]", "").replace("..", "");
                if (nom.isEmpty()) {
                    nom = DEFAUT;
                }
            }
            Files.deleteIfExists(demande);
        } catch (IOException e) {
            journal("demande illisible : " + e.getMessage());
            return;
        }
        journal("demande vue au tic " + tics + " -> " + nom);
        try {
            // Le message de retour dit si l'ecriture a reussi : on le journalise tel quel,
            // sans quoi un echec silencieux ressemble a un succes.
            ScreenshotRecorder.saveScreenshot(
                    client.runDirectory, nom, client.getFramebuffer(),
                    texte -> journal("resultat : " + texte));
        } catch (RuntimeException e) {
            journal("echec de la capture : " + e);
        }
    }

    /** Journal fichier : la sortie standard du client n'est pas lisible depuis la CI. */
    private static void journal(String ligne) {
        Log.info("Captures automatiques : " + ligne);
        if (repertoire == null) {
            return;
        }
        try {
            Files.writeString(repertoire.resolve(JOURNAL), ligne + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Log.info("Captures automatiques : journal indisponible (" + e.getMessage() + ")");
        }
    }
}
