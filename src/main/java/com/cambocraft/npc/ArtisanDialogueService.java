package com.cambocraft.npc;

import com.cambocraft.entity.ArtisanEntity;
import com.cambocraft.util.Localized;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Ouverture d'un dialogue de PNJ, rendue dans le chat natif avec des liens cliquables.
 *
 * <p>Fabric ne fournit pas d'écran de dialogue clé en main : plutôt que d'écrire un
 * {@code ScreenHandler} complet, chaque choix du nœud est publié sous forme de texte cliquable
 * ({@link ClickEvent.RunCommand}). C'est la solution la plus robuste en survie comme en créatif,
 * et elle fonctionne sans aucun paquet réseau personnalisé.</p>
 */
public final class ArtisanDialogueService {
    private ArtisanDialogueService() {}

    /** Publie le nœud courant d'un dialogue pour un joueur. */
    public static void ouvrir(ServerPlayerEntity joueur, ServerWorld monde, ArtisanEntity artisan, String profil) {
        NpcDefinition definition = NpcRegistry.pnj(profil);
        if (definition == null) {
            joueur.sendMessage(Text.literal("§7[" + profil + "] §fBonjour, voyageur."), false);
            return;
        }
        String idDialogue = definition.dialogueInitial();
        DialogueNode.Arbre arbre = NpcRegistry.dialogue(idDialogue);
        if (arbre == null) {
            joueur.sendMessage(Text.literal("§e" + profil + "§f n'a rien à dire."), false);
            return;
        }
        publier(joueur, arbre, arbre.noeudInitial(), 0, java.util.Set.of());
    }

    /** Publie un nœud précis et ses choix disponibles. */
    public static void publier(ServerPlayerEntity joueur, DialogueNode.Arbre arbre, String idNoeud,
                               int reputation, java.util.Set<String> quetes) {
        DialogueNode noeud = arbre.noeuds().get(idNoeud);
        if (noeud == null) {
            joueur.sendMessage(Text.literal("§7Fin du dialogue."), false);
            return;
        }
        joueur.sendMessage(choisir(noeud.texte()), false);
        List<DialogueNode.Choix> dispo = new ArrayList<>();
        for (DialogueNode.Choix choix : noeud.choix()) {
            if (choix.disponible(reputation, quetes)) {
                dispo.add(choix);
            }
        }
        if (dispo.isEmpty()) {
            joueur.sendMessage(Text.literal("§7…"), false);
            return;
        }
        for (int i = 0; i < dispo.size(); i++) {
            DialogueNode.Choix choix = dispo.get(i);
            MutableText ligne = Text.literal("  [" + (i + 1) + "] ")
                    .formatted(Formatting.GOLD)
                    .append(choisir(choix.reponse()).copy().formatted(Formatting.YELLOW));
            if (choix.suivant() != null && !choix.suivant().isEmpty()) {
                Style style = Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/cambocraft dialogue " + arbre.id() + " " + choix.suivant()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Cliquer pour répondre")));
                ligne.setStyle(style);
            }
            joueur.sendMessage(ligne, false);
        }
    }

    private static Text choisir(Localized texte) {
        if (texte == null) return Text.literal("…");
        return Text.literal(texte.get("fr_fr"));
    }
}
