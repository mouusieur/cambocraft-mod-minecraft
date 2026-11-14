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
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ScreenshotRecorder;

/**
 * Déclencheur de captures d'écran piloté par fichiers, pour l'automatisation.
 *
 * <p>Dans la CI (client OpenGL logiciel sous Xvfb, sans gestionnaire de fenêtres),
 * les appuis de touche sont peu fiables : le focus clavier suit le pointeur et se
 * perd après les clics de menu. On surveille donc un fichier
 * {@code <repertoire de jeu>/capture.demande} : dès qu'il apparaît, le client écrit
 * une capture de son framebuffer sous le nom indiqué, puis supprime la demande.</p>
 *
 * <p>Le déclenchement se fait dans le callback de rendu du HUD, <b>après</b> le dessin
 * de la scène : la lire pendant un tick ramènerait un framebuffer vide (image noire).</p>
 */
@Environment(EnvType.CLIENT)
public final class CapturesAutomatiques {
    private static final String DEMANDE = "capture.demande";
    private static final String JOURNAL = "captures-journal.txt";
    // Battement de journal : a 10-20 fps (rendu logiciel), 400 images font 20 a 40 s de
    // silence, ce qui fait passer un client vivant pour fige. 120 images donnent un
    // battement de 6 a 12 s, assez fin pour distinguer les deux cas.
    private static final int BATTEMENT = 120;
    /** Dossier de sortie, relatif au repertoire de jeu : chemin deterministe. */
    private static final String SORTIE = "captures-auto";

    private static Path repertoire;
    private static long images;

    private CapturesAutomatiques() {}

    public static void enregistrer() {
        // Deux chemins valent mieux qu'un : le rendu du HUD n'est pas toujours appele
        // (ecran de chargement, interface masquee), alors que le tick continue.
        HudRenderCallback.EVENT.register(CapturesAutomatiques::apresRendu);
        ClientTickEvents.END_CLIENT_TICK.register(CapturesAutomatiques::finDeTick);
    }

    private static void finDeTick(MinecraftClient client) {
        if (client != null && client.runDirectory != null) {
            traiter(client, "tick");
        }
    }

    private static void apresRendu(DrawContext contexte, RenderTickCounter compteur) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) {
            return;
        }
        images++;
        if (images % BATTEMENT == 0) {
            journal("rendu #" + images + " ; fps " + client.getCurrentFps());
        }
        traiter(client, "rendu");
    }

    private static void traiter(MinecraftClient client, String origine) {
        if (repertoire == null) {
            repertoire = client.runDirectory.toPath();
            journal("arme ; repertoire=" + repertoire);
        }
        Path demande = repertoire.resolve(DEMANDE);
        if (!Files.exists(demande)) {
            return;
        }
        String nom = "capture-" + System.currentTimeMillis() + ".png";
        try {
            String contenu = Files.readString(demande, StandardCharsets.UTF_8).trim();
            if (!contenu.isEmpty()) {
                // Nom simple uniquement : pas de chemin, pas de remontee de repertoire.
                String propre = contenu.replaceAll("[^A-Za-z0-9._-]", "");
                if (!propre.isEmpty()) {
                    nom = propre;
                }
            }
            Files.deleteIfExists(demande);
        } catch (IOException e) {
            journal("demande illisible : " + e.getMessage());
            return;
        }
        journal("capture demandee (" + origine + ") : " + nom);
        final String fichier = nom;
        try {
            ScreenshotRecorder.saveScreenshot(
                    client.runDirectory, nom, client.getFramebuffer(),
                    texte -> publier(fichier));
        } catch (RuntimeException e) {
            journal("echec de la capture : " + e);
        }
    }

    /**
     * Deplace l'image produite vers {@code <repertoire de jeu>/captures-auto/}.
     *
     * <p>ScreenshotRecorder ecrit dans {@code screenshots/} et n'annonce son chemin que
     * dans un message d'interface : plutot que de le reconstruire ou de l'analyser,
     * on publie l'image sous un chemin fixe et connu de l'automatisation.</p>
     */
    private static void publier(String fichier) {
        Path source = repertoire.resolve("screenshots").resolve(fichier);
        Path cible = repertoire.resolve(SORTIE).resolve(fichier);
        try {
            Files.createDirectories(cible.getParent());
            if (Files.exists(source)) {
                Files.move(source, cible, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                journal("image publiee : " + cible);
            } else {
                journal("image annoncee introuvable : " + source);
            }
        } catch (IOException e) {
            journal("publication impossible : " + e.getMessage());
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
