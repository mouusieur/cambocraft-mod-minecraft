package com.cambocraft.client.screen;

import com.cambocraft.quest.QuestDefinition;
import com.cambocraft.quest.QuestEngine;
import com.cambocraft.quest.QuestRegistry;
import com.cambocraft.server.CamboPlayerData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Journal de quêtes : onglets « en cours », « terminées », « disponibles ».
 *
 * <p>Les 85 quêtes du catalogue n'avaient jusqu'ici aucun affichage : on pouvait démarrer une quête
 * sans jamais savoir où l'on en était. Cet écran présente l'étape courante, l'objectif, la
 * progression chiffrée et permet d'épingler une quête (elle apparaît alors sur le HUD).</p>
 */
public class JournalDeQuetesScreen extends Screen {
    private static final int COULEUR_FOND = 0xF0182027;
    private static final int COULEUR_BORDURE = 0xFF5C4B8A;
    private static final int COULEUR_TITRE = 0xFFC9A7EB;
    private static final int COULEUR_TEXTE = 0xFFEAE4F2;
    private static final int COULEUR_DOUX = 0xFFA79DB8;
    private static final String[] ONGLETS = {"En cours", "Terminées", "Disponibles"};

    private final CamboPlayerData donnees;
    private int onglet;

    public JournalDeQuetesScreen(CamboPlayerData donnees) {
        super(Text.translatable("cambocraft.ecran.quetes.titre"));
        this.donnees = donnees;
    }

    private List<QuestDefinition> quetesDeLOnglet() {
        List<QuestDefinition> resultat = new ArrayList<>();
        QuestEngine moteur = donnees.quetes();
        for (QuestDefinition q : QuestRegistry.quetes().values()) {
            boolean active = moteur.actives().containsKey(q.id());
            boolean terminee = moteur.terminees().contains(q.id());
            if (onglet == 0 && active) {
                resultat.add(q);
            } else if (onglet == 1 && terminee) {
                resultat.add(q);
            } else if (onglet == 2 && !active && !terminee) {
                resultat.add(q);
            }
        }
        resultat.sort((a, b) -> Integer.compare(a.chapitre(), b.chapitre()));
        return resultat;
    }

    @Override
    public void init() {
        int largeurOnglet = 90;
        int depart = this.width / 2 - (largeurOnglet * 3 + 8) / 2;
        for (int i = 0; i < 3; i++) {
            final int index = i;
            addDrawableChild(ButtonWidget.builder(
                            Text.literal((i == onglet ? "§e" : "§7") + ONGLETS[i]),
                            bouton -> {
                                onglet = index;
                                clearAndInit();
                            })
                    .dimensions(depart + i * (largeurOnglet + 4), 28, largeurOnglet, 20)
                    .build());
        }
        addDrawableChild(ButtonWidget.builder(Text.translatable("cambocraft.ecran.fermer"),
                        bouton -> close())
                .dimensions(this.width / 2 - 50, this.height - 32, 100, 20)
                .build());
    }

    @Override
    public void render(DrawContext contexte, int sourisX, int sourisY, float delta) {
        renderBackground(contexte, sourisX, sourisY, delta);
        contexte.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, COULEUR_TITRE);

        int x0 = this.width / 2 - 190;
        int y0 = 58;
        int largeur = 380;
        int hauteur = this.height - 100;
        contexte.fill(x0, y0, x0 + largeur, y0 + hauteur, COULEUR_FOND);
        contexte.drawBorder(x0, y0, largeur, hauteur, COULEUR_BORDURE);

        List<QuestDefinition> liste = quetesDeLOnglet();
        if (liste.isEmpty()) {
            contexte.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("§8Aucune quête dans cet onglet."), this.width / 2, y0 + hauteur / 2, COULEUR_DOUX);
        }
        int y = y0 + 8;
        int lignesParQuete = onglet == 0 ? 4 : 3;
        int maxQuetes = Math.max(1, (hauteur - 12) / (lignesParQuete * 10 + 4));
        for (int i = 0; i < Math.min(maxQuetes, liste.size()); i++) {
            QuestDefinition q = liste.get(i);
            QuestEngine.ProgressionActive prog = donnees.quetes().actives().get(q.id());
            String titre = "§f" + q.titre().get("fr_fr");
            contexte.drawTextWithShadow(this.textRenderer, Text.literal(titre), x0 + 10, y, COULEUR_TEXTE);
            y += 10;
            String chapitre = "§8Chapitre " + q.chapitre() + " · " + q.type().id
                    + (donnees.queteEpinglee().equals(q.id()) ? " §e★ épinglée" : "");
            contexte.drawTextWithShadow(this.textRenderer, Text.literal(chapitre), x0 + 10, y, COULEUR_DOUX);
            y += 10;
            if (onglet == 0 && prog != null && prog.indexEtape() < q.etapes().size()) {
                QuestDefinition.Etape etape = q.etapes().get(prog.indexEtape());
                String objectif = "§b▸ " + etape.objectif() + " " + etape.cible()
                        + " " + prog.avancementEtape() + "/" + etape.quantite();
                contexte.drawTextWithShadow(this.textRenderer, Text.literal(objectif), x0 + 12, y, COULEUR_TEXTE);
                y += 10;
                String[] actions = {"[Épingler]", "[Abandonner]"};
                int bx = x0 + 12;
                for (int a = 0; a < actions.length; a++) {
                    final int action = a;
                    addDrawableChild(ButtonWidget.builder(Text.literal(actions[a]), bouton -> actionQuete(q, action))
                            .dimensions(bx, y, 76, 12)
                            .build());
                    bx += 80;
                }
                y += 14;
            } else if (onglet == 1) {
                String branche = donnees.quetes().brancheChoisie(q.id());
                contexte.drawTextWithShadow(this.textRenderer,
                        Text.literal("§a✔ terminée" + (branche == null ? "" : " §7(voie : " + branche + ")")),
                        x0 + 12, y, COULEUR_TEXTE);
                y += 12;
            } else if (onglet == 2) {
                String raison = donnees.quetes().peutDemarrer(q, donnees.niveau(), "")
                        ? "§a✔ démarrable" : "§c✖ verrouillée (niv. " + q.niveauMin() + ")";
                contexte.drawTextWithShadow(this.textRenderer, Text.literal(raison), x0 + 12, y, COULEUR_TEXTE);
                y += 12;
            }
        }
        super.render(contexte, sourisX, sourisY, delta);
    }

    private void actionQuete(QuestDefinition q, int action) {
        if (action == 0) {
            donnees.epinglerQuete(q.id());
            com.cambocraft.client.ClientNetworking.envoyerEpingle(q.id());
        } else {
            donnees.quetes().abandonner(q);
            com.cambocraft.client.ClientNetworking.envoyerAbandon(q.id());
        }
        clearAndInit();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
