package com.cambocraft.client.screen;

import com.cambocraft.craft.CookingRecipeSpec;
import com.cambocraft.craft.CookingRegistry;
import com.cambocraft.craft.ForgeUpgrade;
import com.cambocraft.craft.SilkChain;
import com.cambocraft.craft.StoneSculpture;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Table khmère : les quatre métiers d'artisanat cambodgien sur un seul écran.
 *
 * <p>Quatre onglets — <b>cuisine</b> (16 recettes, ingrédients et palier de réputation),
 * <b>soie</b> (chaîne complète du cocon au tissu teint), <b>sculpture</b> (9 œuvres),
 * <b>forge</b> (renforcement +1 → +5 avec chances de réussite). Chaque ligne affiche les
 * exigences réelles lues dans les registres, de sorte que l'écran ne peut pas mentir sur une
 * recette : il lit les mêmes objets que le serveur.</p>
 */
public class TableKhmereScreen extends Screen {
    private static final String[] ONGLETS = {"Cuisine", "Soie", "Sculpture", "Forge"};
    private static final int COULEUR_FOND = 0xF01F1B14;
    private static final int COULEUR_BORDURE = 0xFF8A6A3A;
    private static final int COULEUR_TITRE = 0xFFE8C877;
    private static final int COULEUR_TEXTE = 0xFFEFE6D2;
    private static final int COULEUR_DOUX = 0xFFA99C82;

    private int onglet;

    public TableKhmereScreen() {
        super(Text.translatable("cambocraft.ecran.table.titre"));
    }

    @Override
    public void init() {
        int largeurOnglet = 84;
        int depart = this.width / 2 - (largeurOnglet * ONGLETS.length + 4 * (ONGLETS.length - 1)) / 2;
        for (int i = 0; i < ONGLETS.length; i++) {
            final int index = i;
            addDrawableChild(ButtonWidget.builder(
                            Text.literal((i == onglet ? "§6" : "§7") + ONGLETS[i]),
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

        int x0 = this.width / 2 - 195;
        int y0 = 56;
        int largeur = 390;
        int hauteur = this.height - 96;
        contexte.fill(x0, y0, x0 + largeur, y0 + hauteur, COULEUR_FOND);
        contexte.drawBorder(x0, y0, largeur, hauteur, COULEUR_BORDURE);

        switch (onglet) {
            case 0 -> rendreCuisine(contexte, x0, y0, largeur, hauteur);
            case 1 -> rendreSoie(contexte, x0, y0, largeur, hauteur);
            case 2 -> rendreSculpture(contexte, x0, y0, largeur, hauteur);
            default -> rendreForge(contexte, x0, y0, largeur, hauteur);
        }
        super.render(contexte, sourisX, sourisY, delta);
    }

    private void rendreCuisine(DrawContext ctx, int x0, int y0, int largeur, int hauteur) {
        List<CookingRecipeSpec> recettes = new ArrayList<>(CookingRegistry.recettes().values());
        recettes.sort((a, b) -> a.id().compareTo(b.id()));
        int y = y0 + 8;
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§6" + recettes.size() + " recettes · palier de réputation requis"),
                x0 + 10, y, COULEUR_TITRE);
        y += 14;
        int max = Math.max(1, (hauteur - 30) / 22);
        for (int i = 0; i < Math.min(max, recettes.size()); i++) {
            CookingRecipeSpec r = recettes.get(i);
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.literal("§f" + r.nom().get("fr_fr") + " §8→ §7" + r.resultat()),
                    x0 + 12, y, COULEUR_TEXTE);
            y += 9;
            String ingredients = String.join(", ", r.ingredients());
            if (ingredients.length() > 70) {
                ingredients = ingredients.substring(0, 68) + "…";
            }
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.literal("§8" + r.palierMin().id + " · " + ingredients),
                    x0 + 16, y, COULEUR_DOUX);
            y += 11;
        }
    }

    private void rendreSoie(DrawContext ctx, int x0, int y0, int largeur, int hauteur) {
        int y = y0 + 8;
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§6Chaîne de la soie khmère"), x0 + 10, y, COULEUR_TITRE);
        y += 16;
        for (SilkChain.EtapeTransformation etape : SilkChain.CHAINE) {
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.literal("§e" + etape.quantiteEntree() + "× §f" + etape.entree()
                            + " §8[" + etape.poste() + "] §7→ §a" + etape.quantiteSortie() + "× " + etape.sortie()),
                    x0 + 12, y, COULEUR_TEXTE);
            y += 12;
        }
        y += 8;
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§6Teintures naturelles"), x0 + 10, y, COULEUR_TITRE);
        y += 14;
        for (var e : SilkChain.TEINTURES.entrySet()) {
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.literal("§7" + e.getKey() + " §8→ §d" + e.getValue()), x0 + 12, y, COULEUR_TEXTE);
            y += 12;
        }
    }

    private void rendreSculpture(DrawContext ctx, int x0, int y0, int largeur, int hauteur) {
        int y = y0 + 8;
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§6" + StoneSculpture.OEUVRES.size() + " œuvres sculptables"),
                x0 + 10, y, COULEUR_TITRE);
        y += 16;
        for (StoneSculpture.OeuvreSculptee oeuvre : StoneSculpture.OEUVRES) {
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.literal("§f" + oeuvre.id() + " §8← §7" + oeuvre.materiauBrut()
                            + " §8(" + oeuvre.coupsDeCiseau() + " coups, +"
                            + oeuvre.xpArtisanat() + " XP)"),
                    x0 + 12, y, COULEUR_TEXTE);
            y += 12;
        }
    }

    private void rendreForge(DrawContext ctx, int x0, int y0, int largeur, int hauteur) {
        int y = y0 + 8;
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§6Renforcement non destructif : +1 → +" + ForgeUpgrade.NIVEAU_MAX),
                x0 + 10, y, COULEUR_TITRE);
        y += 16;
        for (int cible = 1; cible <= ForgeUpgrade.NIVEAU_MAX; cible++) {
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.literal("§e+" + cible + " §8· réussite "
                            + Math.round(ForgeUpgrade.chanceSucces(cible) * 100) + "%"
                            + " · " + ForgeUpgrade.coutPieces(cible) + " pièces"
                            + " · " + ForgeUpgrade.cristalRequis(cible)
                            + " · ×" + String.format("%.2f", ForgeUpgrade.multiplicateur(cible))),
                    x0 + 12, y, COULEUR_TEXTE);
            y += 13;
        }
        y += 8;
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§8L'enclume accepte aussi les recettes d'établi du catalogue"), x0 + 10, y, COULEUR_DOUX);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
