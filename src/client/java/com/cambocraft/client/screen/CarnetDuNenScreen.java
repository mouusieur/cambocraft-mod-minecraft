package com.cambocraft.client.screen;

import com.cambocraft.power.NenNotebook;
import com.cambocraft.server.CamboPlayerData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Écran du Carnet du Nen : collection complète, emplacements actifs et synergies.
 *
 * <p>Avant cet écran, le Carnet n'existait que sous forme d'une classe Java : le joueur ne pouvait
 * ni consulter sa collection ni choisir ses trois pouvoirs actifs. L'écran lit l'état synchronisé
 * par le serveur ({@code SyncProfilNenS2C}) et présente trois colonnes : collection, emplacements,
 * synergies. Les boutons d'emplacement envoient un paquet {@code EquiperPouvoirC2S} ; le serveur
 * revalide la possession avant d'accepter.</p>
 */
public class CarnetDuNenScreen extends Screen {
    private static final int COULEUR_TITRE = 0xFFF2C14E;
    private static final int COULEUR_TEXTE = 0xFFEDE7D9;
    private static final int COULEUR_DOUX = 0xFF9FB8AD;
    private static final int COULEUR_FOND = 0xF01B2A33;
    private static final int COULEUR_BORDURE = 0xFF3E6B58;
    private static final int COULEUR_VERT = 0xFF7FC8A9;
    private static final int COULEUR_OR = 0xFFE0B84C;

    private final CamboPlayerData donnees;
    /** Pouvoir sélectionné pour être équipé (index dans la liste triée de la collection). */
    private int selection;

    public CarnetDuNenScreen(CamboPlayerData donnees) {
        super(Text.translatable("cambocraft.ecran.carnet.titre"));
        this.donnees = donnees;
    }

    private List<Map.Entry<String, NenNotebook.PowerProgress>> collectionTriee() {
        List<Map.Entry<String, NenNotebook.PowerProgress>> liste =
                new ArrayList<>(donnees.carnet().collection().entrySet());
        liste.sort(Map.Entry.comparingByKey());
        return liste;
    }

    @Override
    public void init() {
        // Trois boutons d'emplacement : chacun équipe le pouvoir sélectionné.
        int largeurBouton = 96;
        int x = this.width / 2 - 60;
        for (int slot = 0; slot < NenNotebook.NB_EMPLACEMENTS_ACTIFS; slot++) {
            final int emplacement = slot;
            String libelle = donnees.carnet().pouvoirEquipe(slot) == null
                    ? "§7vide"
                    : "§a" + raccourcir(donnees.carnet().pouvoirEquipe(slot));
            addDrawableChild(ButtonWidget.builder(Text.literal("§8[" + (slot + 1) + "] " + libelle),
                            bouton -> equiper(emplacement))
                    .dimensions(x, this.height / 2 + 4 + slot * 22, largeurBouton, 20)
                    .build());
        }
        addDrawableChild(ButtonWidget.builder(Text.translatable("cambocraft.ecran.fermer"),
                        bouton -> close())
                .dimensions(this.width / 2 - 50, this.height - 32, 100, 20)
                .build());
    }

    private void equiper(int slot) {
        List<Map.Entry<String, NenNotebook.PowerProgress>> liste = collectionTriee();
        if (liste.isEmpty()) {
            return;
        }
        String pouvoir = liste.get(Math.floorMod(selection, liste.size())).getKey();
        donnees.carnet().equiper(slot, pouvoir);
        com.cambocraft.client.ClientNetworking.envoyerEquipement(slot, pouvoir);
        clearAndInit();
    }

    @Override
    public void render(DrawContext contexte, int sourisX, int sourisY, float delta) {
        renderBackground(contexte, sourisX, sourisY, delta);
        int largeurPanneau = Math.min(420, this.width - 40);
        int hauteurPanneau = Math.min(230, this.height - 60);
        int x0 = (this.width - largeurPanneau) / 2;
        int y0 = (this.height - hauteurPanneau) / 2 - 10;
        contexte.fill(x0, y0, x0 + largeurPanneau, y0 + hauteurPanneau, COULEUR_FOND);
        contexte.drawBorder(x0, y0, largeurPanneau, hauteurPanneau, COULEUR_BORDURE);
        contexte.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, y0 + 8, COULEUR_TITRE);

        NenNotebook carnet = donnees.carnet();
        String entete = "Niveau " + donnees.niveau()
                + " · Renaissance " + donnees.renaissances()
                + " · Énergie " + carnet.energieActuelle() + "/" + carnet.energieMax();
        contexte.drawCenteredTextWithShadow(this.textRenderer, Text.literal(entete),
                this.width / 2, y0 + 22, COULEUR_DOUX);

        int ligneY = y0 + 40;
        contexte.drawTextWithShadow(this.textRenderer,
                Text.literal("§6Collection (" + carnet.nombrePouvoirsDebloques() + ")"),
                x0 + 10, ligneY, COULEUR_OR);
        ligneY += 12;
        List<Map.Entry<String, NenNotebook.PowerProgress>> liste = collectionTriee();
        int maxLignes = Math.min(8, Math.max(0, (hauteurPanneau - 70) / 11));
        for (int i = 0; i < Math.min(maxLignes, liste.size()); i++) {
            Map.Entry<String, NenNotebook.PowerProgress> e = liste.get(i);
            boolean choisi = i == Math.floorMod(selection, Math.max(1, liste.size()));
            String prefixe = choisi ? "§e▸ " : "§7· ";
            String ligne = prefixe + e.getKey() + " §8niv. " + e.getValue().niveau()
                    + (e.getValue().maitriseDebloquee() ? " §d✦" : "");
            contexte.drawTextWithShadow(this.textRenderer, Text.literal(ligne), x0 + 12, ligneY, COULEUR_TEXTE);
            ligneY += 11;
        }

        // Colonne de droite : synergies actives calculées par le carnet.
        int xDroite = x0 + largeurPanneau / 2 + 6;
        int yDroite = y0 + 40;
        contexte.drawTextWithShadow(this.textRenderer, Text.literal("§6Synergies actives"), xDroite, yDroite, COULEUR_OR);
        yDroite += 12;
        var synergies = carnet.synergiesActives();
        if (synergies.isEmpty()) {
            contexte.drawTextWithShadow(this.textRenderer, Text.literal("§8aucune (2 pouvoirs minimum)"),
                    xDroite, yDroite, COULEUR_DOUX);
        } else {
            for (var syn : synergies) {
                contexte.drawTextWithShadow(this.textRenderer,
                        Text.literal("§a✦ " + syn.id() + " §8×" + String.format("%.2f", syn.multiplicateur())),
                        xDroite, yDroite, COULEUR_VERT);
                yDroite += 11;
            }
        }
        yDroite += 8;
        contexte.drawTextWithShadow(this.textRenderer,
                Text.literal("§6Multiplicateur total §f×" + String.format("%.2f", carnet.multiplicateurSynergieTotal())),
                xDroite, yDroite, COULEUR_TEXTE);
        yDroite += 14;
        contexte.drawTextWithShadow(this.textRenderer,
                Text.literal("§7Éclats de Nen §f" + carnet.eclatsDeNen()), xDroite, yDroite, COULEUR_TEXTE);
        yDroite += 14;
        contexte.drawTextWithShadow(this.textRenderer,
                Text.literal("§8↓ clic : défiler · boutons : équiper"),
                xDroite, yDroite, COULEUR_DOUX);

        super.render(contexte, sourisX, sourisY, delta);
    }

    @Override
    public boolean mouseScrolled(double sourisX, double sourisY, double horizontal, double vertical) {
        List<Map.Entry<String, NenNotebook.PowerProgress>> liste = collectionTriee();
        if (!liste.isEmpty()) {
            selection = Math.floorMod(selection + (vertical > 0 ? -1 : 1), liste.size());
        }
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static String raccourcir(String id) {
        if (id == null) {
            return "—";
        }
        return id.length() <= 12 ? id : id.substring(0, 11) + "…";
    }

    /** Couleur d'accent utilisée par le HUD pour rappeler l'écran. */
    public static Formatting accent() {
        return Formatting.GOLD;
    }
}
