package com.cambocraft.craft;

import java.util.List;
import java.util.Map;

/**
 * Chaîne artisanale de la soie cambodgienne (§7.4) :
 * Mûrier → Feuille de mûrier → Ver à soie → Cocon → Cuve de soie (fil / écheveau) →
 * Teinture naturelle (or, pourpre, indigo, émeraude) → Métier à tisser (tissu, krama, tenues).
 */
public final class SilkChain {
    public record EtapeTransformation(String entree, int quantiteEntree, String poste, String sortie, int quantiteSortie) {}

    public static final List<EtapeTransformation> CHAINE = List.of(
            new EtapeTransformation("cambocraft:feuille_de_murier", 3, "elevage", "cambocraft:cocon_de_soie", 2),
            new EtapeTransformation("cambocraft:cocon_de_soie", 2, "cambocraft:cuve_de_soie", "cambocraft:fil_de_soie", 4),
            new EtapeTransformation("cambocraft:fil_de_soie", 4, "cambocraft:cuve_de_soie", "cambocraft:echeveau_de_soie_ecrue", 1),
            new EtapeTransformation("cambocraft:echeveau_de_soie_ecrue", 2, "cambocraft:metier_a_tisser", "cambocraft:tissu_de_soie", 1),
            new EtapeTransformation("cambocraft:tissu_de_soie", 2, "cambocraft:metier_a_tisser", "cambocraft:krama_traditionnel", 1),
            new EtapeTransformation("cambocraft:soie_teinte_or", 3, "cambocraft:metier_a_tisser", "cambocraft:krama_de_soie_doree", 1));

    /** Correspondance entre pigment naturel cambodgien et soie teinte. */
    public static final Map<String, String> TEINTURES = Map.of(
            "cambocraft:curcuma_racine", "cambocraft:soie_teinte_or",
            "cambocraft:fleur_d_hibiscus", "cambocraft:soie_teinte_pourpre",
            "cambocraft:fleur_de_bougainvillier", "cambocraft:soie_teinte_indigo",
            "cambocraft:feuille_de_lotus", "cambocraft:soie_teinte_emeraude");

    private SilkChain() {}

    public static String teindre(String pigment) {
        return TEINTURES.get(pigment);
    }
}
