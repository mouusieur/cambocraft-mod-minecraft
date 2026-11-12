package com.cambocraft.npc;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertFalse;
import static com.cambocraft.test.TestSupport.assertNear;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.craft.CookingRecipeSpec;
import com.cambocraft.craft.CookingRegistry;
import com.cambocraft.craft.ForgeUpgrade;
import com.cambocraft.craft.SilkChain;
import com.cambocraft.craft.StoneSculpture;
import com.cambocraft.data.DataManager;
import com.cambocraft.test.TestSupport;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Tests des 16 PNJ, dialogues, marchands, réputation, cuisine khmère, soie, sculpture et forge. */
public final class NpcAndCraftTests {
    private NpcAndCraftTests() {}

    public static void pnjReputationEtArtisanat() {
        NpcRegistry.init();
        CookingRegistry.init();
        try {
            DataManager.chargerDepuisDossier(TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement : " + e, e);
        }

        // 1. >= 15 PNJ, dialogues et marchands (§6.1)
        assertTrue(NpcRegistry.pnjs().size() >= 15, "au moins 15 PNJ doivent être enregistrés");
        assertTrue(NpcRegistry.dialogues().size() >= 15, "au moins 15 arbres de dialogues requis");
        assertTrue(NpcRegistry.marchands().size() >= 8, "au moins 8 catalogues marchands requis");

        NpcDefinition vendeurRiz = NpcRegistry.pnj("vendeur_de_riz");
        assertEquals("salut_matinal_et_offrande", vendeurRiz.activiteCourante(1000L, false));
        assertEquals("travail_principal", vendeurRiz.activiteCourante(5000L, false));
        assertEquals("abri_mousson", vendeurRiz.activiteCourante(5000L, true));

        // 2. Dialogues à embranchements et filtre de réputation
        DialogueNode.Arbre dlg = NpcRegistry.dialogue("vendeur_de_riz");
        DialogueNode accueil = dlg.initial();
        assertEquals(3, accueil.choix().size());
        DialogueNode.Choix choixConfiance = accueil.choix().get(1);
        assertFalse("le choix de confiance requiert 200 de réputation",
                choixConfiance.disponible(50, Set.of()));
        assertTrue(choixConfiance.disponible(250, Set.of()),
                "le choix de confiance doit s'ouvrir à 250 de réputation");

        // 3. Offres marchandes modulées par la réputation et le riz
        MerchantOfferSpec.Catalogue catRiz = NpcRegistry.marchand("vendeur_de_riz");
        MerchantOfferSpec offreBolBeni = catRiz.offres().get(catRiz.offres().size() - 1);
        assertFalse("le bol béni marchand requiert le palier honoré", offreBolBeni.debloquee(100));
        assertTrue(offreBolBeni.debloquee(800), "le bol béni marchand est débloqué au palier honoré");
        assertTrue(offreBolBeni.prixAchatEffectif(800, 1.5) < offreBolBeni.prixAchatEffectif(0, 1.5),
                "un meilleur palier de réputation réduit le prix chez le marchand");

        // 4. Cuisine khmère débloquée par réputation (§7.3)
        assertTrue(CookingRegistry.recettes().size() >= 16, "au moins 16 recettes khmères attendues");
        CookingRecipeSpec amok = CookingRegistry.recette("amok");
        List<String> panier = List.of(
                "cambocraft:poisson_serpent",
                "cambocraft:lait_de_coco",
                "cambocraft:pate_de_kroeung_jaune");
        assertFalse("l'amok requiert le palier respecté (200)", amok.peutCuisiner(panier, 50));
        assertTrue(amok.peutCuisiner(panier, 300), "l'amok est cuisinable au palier respecté");

        // 5. Soie, sculpture sur pierre et forge +1..+5 (§7.4–7.6)
        assertEquals("cambocraft:soie_teinte_or", SilkChain.teindre("cambocraft:curcuma_racine"));
        assertTrue(StoneSculpture.OEUVRES.size() >= 8, "au moins 8 œuvres sculptées attendues");
        assertNear(1.08, ForgeUpgrade.multiplicateur(1), 1e-6);
        assertNear(1.50, ForgeUpgrade.multiplicateur(5), 1e-6);
        assertEquals(1, ForgeUpgrade.tenterAmelioration(0, new Random(42)));
    }
}
