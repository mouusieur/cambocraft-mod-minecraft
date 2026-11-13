package com.cambocraft.client;

import com.cambocraft.util.Log;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Raccourcis clavier du mod.
 *
 * <p>Trois touches seulement, choisies pour ne pas entrer en conflit avec les commandes vanilla :
 * <b>C</b> ouvre le Carnet du Nen, <b>J</b> le journal de quêtes, <b>K</b> la table khmère. Les
 * écrans sont ouverts sur le thread de rendu, dans le tick client, ce qui évite tout accès
 * concurrent à l'interface.</p>
 */
public final class CambocraftKeys {
    private static final String CATEGORIE = "key.cambocraft.categorie";

    private static KeyBinding carnet;
    private static KeyBinding journal;
    private static KeyBinding table;
    /** Les trois emplacements actifs du Carnet du Nen, lancés par les touches 1, 2 et 3 du Nen. */
    private static KeyBinding pouvoir1;
    private static KeyBinding pouvoir2;
    private static KeyBinding pouvoir3;

    private CambocraftKeys() {}

    /** Enregistre les raccourcis et leur boucle de lecture. */
    public static void enregistrer() {
        carnet = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.carnet", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_C, CATEGORIE));
        journal = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.journal", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_J, CATEGORIE));
        table = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.table", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_K, CATEGORIE));

        // Touches d'activation des pouvoirs. Elles sont volontairement distinctes des 1-2-3
        // vanilla (barre d'objets) : « V », « B » et « N » n'entrent en conflit avec rien.
        pouvoir1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.pouvoir1", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_V, CATEGORIE));
        pouvoir2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.pouvoir2", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_B, CATEGORIE));
        pouvoir3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.pouvoir3", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_N, CATEGORIE));

        ClientTickEvents.END_CLIENT_TICK.register(CambocraftKeys::aChaqueTick);
        Log.info("Cambocraft Client : C (Carnet), J (journal), K (table), V/B/N (pouvoirs 1-3)");
    }

    private static void aChaqueTick(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        while (carnet.wasPressed()) {
            ClientNetworking.ouvrirCarnet();
        }
        while (journal.wasPressed()) {
            ClientNetworking.ouvrirJournal();
        }
        while (table.wasPressed()) {
            ClientNetworking.ouvrirTableKhmere();
        }
        while (pouvoir1.wasPressed()) {
            ClientNetworking.envoyerActivation(0);
        }
        while (pouvoir2.wasPressed()) {
            ClientNetworking.envoyerActivation(1);
        }
        while (pouvoir3.wasPressed()) {
            ClientNetworking.envoyerActivation(2);
        }
    }

    public static KeyBinding carnet() {
        return carnet;
    }

    public static KeyBinding journal() {
        return journal;
    }

    public static KeyBinding table() {
        return table;
    }

    public static KeyBinding pouvoir1() {
        return pouvoir1;
    }

    public static KeyBinding pouvoir2() {
        return pouvoir2;
    }

    public static KeyBinding pouvoir3() {
        return pouvoir3;
    }
}
