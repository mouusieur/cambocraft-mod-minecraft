package com.cambocraft.client.hud;

import com.cambocraft.client.ClientNetworking;
import com.cambocraft.util.Log;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * HUD du Nen : jauge d'énergie, niveau/Renaissance, saison et quête épinglée.
 *
 * <p>Le HUD est volontairement discret (coin supérieur gauche, semi-transparent) et n'affiche
 * que des données reçues du serveur : il ne peut donc pas se désynchroniser de la progression
 * réelle. Il est aussi respectueux de l'accessibilité : la couleur n'est jamais le seul canal
 * d'information (les valeurs sont écrites en clair).</p>
 */
public final class HudCambocraft {
    private static final int MARGE = 6;
    private static final int LARGEUR = 116;
    private static final int HAUTEUR_BARRE = 4;

    private HudCambocraft() {}

    /** Branche le HUD sur le cycle de rendu. */
    public static void enregistrer() {
        HudRenderCallback.EVENT.register(HudCambocraft::rendre);
        Log.info("Cambocraft Client : HUD du Nen branché");
    }

    private static void rendre(DrawContext contexte, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.hudHidden) {
            return;
        }
        int x = MARGE;
        int y = MARGE;

        int energie = ClientNetworking.energie();
        int energieMax = Math.max(1, ClientNetworking.energieMax());
        int largeurBarre = LARGEUR - 2;

        // Fond + bordure : lisibles sur ciel comme sur terre.
        contexte.fill(x, y, x + LARGEUR, y + 42, 0xA0101B22);
        contexte.drawBorder(x, y, LARGEUR, 42, 0xFF3E6B58);

        contexte.drawTextWithShadow(client.textRenderer,
                net.minecraft.text.Text.literal("§bNen §f" + energie + "/" + energieMax),
                x + 4, y + 4, 0xFFFFFFFF);
        int rempli = (int) Math.round((double) energie / energieMax * largeurBarre);
        contexte.fill(x + 1, y + 14, x + 1 + largeurBarre, y + 14 + HAUTEUR_BARRE, 0xFF20323A);
        contexte.fill(x + 1, y + 14, x + 1 + Math.max(0, Math.min(largeurBarre, rempli)),
                y + 14 + HAUTEUR_BARRE, 0xFF4EC9B0);

        String niveau = "§eNiv. " + ClientNetworking.niveau();
        if (ClientNetworking.renaissance() > 0) {
            niveau += " §6★" + ClientNetworking.renaissance();
        }
        contexte.drawTextWithShadow(client.textRenderer, net.minecraft.text.Text.literal(niveau),
                x + 4, y + 21, 0xFFFFFFFF);

        String saison = "§a" + ClientNetworking.saison().replace('_', ' ')
                + " §8j" + ClientNetworking.jourDansSaison()
                + " §7lac y=" + ClientNetworking.niveauLac();
        contexte.drawTextWithShadow(client.textRenderer, net.minecraft.text.Text.literal(saison),
                x + 4, y + 31, 0xFFFFFFFF);

        // Quête épinglée et emplacements actifs, sous le panneau.
        int ySuite = y + 46;
        var actives = ClientNetworking.quetesActives();
        if (!actives.isEmpty()) {
            String quete = actives.get(0);
            if (quete.length() > 22) {
                quete = quete.substring(0, 21) + "…";
            }
            contexte.drawTextWithShadow(client.textRenderer,
                    net.minecraft.text.Text.literal("§d◆ " + quete), x, ySuite, 0xFFFFFFFF);
            ySuite += 10;
        }
        var emplacements = ClientNetworking.emplacements();
        StringBuilder slots = new StringBuilder("§8");
        for (String p : emplacements) {
            slots.append(p == null || p.isEmpty() ? "◇ " : "◆ ");
        }
        if (!emplacements.isEmpty()) {
            contexte.drawTextWithShadow(client.textRenderer,
                    net.minecraft.text.Text.literal(slots.toString() + "§7[C] Carnet"), x, ySuite, 0xFFFFFFFF);
        }
    }
}
