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

    private CambocraftKeys() {}

    /** Enregistre les raccourcis et leur boucle de lecture. */
    public static void enregistrer() {
        carnet = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.carnet", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_C, CATEGORIE));
        journal = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.journal", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_J, CATEGORIE));
        table = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cambocraft.table", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_K, CATEGORIE));

        ClientTickEvents.END_CLIENT_TICK.register(CambocraftKeys::aChaqueTick);
        Log.info("Cambocraft Client : raccourcis C (Carnet), J (journal), K (table khmère)");
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
}
