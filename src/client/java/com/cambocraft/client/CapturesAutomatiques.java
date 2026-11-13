package com.cambocraft.client;

import com.cambocraft.util.Log;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * {@code <repertoire de jeu>/capture.demande} : dès qu'il apparaît, le client prend
 * lui-même une capture de son framebuffer et l'écrit dans {@code screenshots/}.
 * Le fichier est ensuite supprimé, ce qui vaut acquittement.</p>
 *
 * <p>Le mécanisme est inerte en jeu normal : sans fichier de demande, il ne fait
 * qu'un test d'existence par tick (une fraction de milliseconde).</p>
 */
@Environment(EnvType.CLIENT)
public final class CapturesAutomatiques {
    private static final String DEMANDE = "capture.demande";
    private static final int DELAI_MINIMAL = 10;

    private static Path repertoire;
    private static int tics;

    private CapturesAutomatiques() {}

    public static void enregistrer() {
        ClientTickEvents.END_CLIENT_TICK.register(CapturesAutomatiques::tick);
    }

    private static void tick(MinecraftClient client) {
        tics++;
        // On laisse passer quelques tics : au demarrage le framebuffer n'est pas pret.
        if (tics < DELAI_MINIMAL || client.runDirectory == null) {
            return;
        }
        if (repertoire == null) {
            repertoire = client.runDirectory.toPath();
        }
        Path demande = repertoire.resolve(DEMANDE);
        if (!Files.exists(demande)) {
            return;
        }
        // Une capture juste apres un changement de scene peut tomber sur une image
        // encore vide : on laisse un delai d'un demi-seconde avant de declencher.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            Files.deleteIfExists(demande);
        } catch (IOException e) {
            Log.info("Capture automatique : demande illisible (" + e.getMessage() + ")");
            return;
        }
        Log.info("Capture automatique : declenchement");
        // Le consomateur recoit le message de confirmation vanilla : on journalise
        // simplement qu'une image a ete ecrite, le contenu du message n'est pas utile.
        ScreenshotRecorder.saveScreenshot(
                client.runDirectory, client.getFramebuffer(),
                texte -> Log.info("Capture automatique : image ecrite"));
    }
}
