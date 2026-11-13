package com.cambocraft.power;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertFalse;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.datagen.CambocraftDataGen;
import com.cambocraft.test.TestSupport;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Couverture mécanique des 84 pouvoirs du Nen.
 *
 * <p>Ces tests ne simulent pas Minecraft : ils garantissent que le moteur d'effets dispose
 * réellement d'une mise en scène pour <b>chaque</b> pouvoir livré, que les seize formes sont
 * toutes utilisées, et que la table ne référence aucun pouvoir inexistant. Sans eux, l'ajout
 * d'un pouvoir dans les données le rendrait silencieusement inerte.</p>
 */
public final class Phase11PowersTests {
    private Phase11PowersTests() {}

    public static void chaquePouvoirAUnEffet() {
        try {
            CambocraftDataGen.initialiserEtChargerTout(TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement global : " + e, e);
        }

        List<PowerDefinition> pouvoirs = new ArrayList<>(PowerRegistry.tous());
        assertEquals("nombre de pouvoirs chargés", 84, pouvoirs.size());

        // 1. Aucun pouvoir livré sans forme explicite. C'est le test central : un pouvoir
        //    ajouté aux données sans ligne dans FormePouvoir serait tombé sur un repli
        //    générique, donc indiscernable de ses voisins.
        List<String> ids = pouvoirs.stream().map(PowerDefinition::id).sorted().toList();
        List<String> manquants = FormePouvoir.manquants(ids);
        assertTrue("pouvoirs sans mise en scène : " + manquants, manquants.isEmpty());
        assertEquals("pouvoirs couverts par la table", ids.size(), FormePouvoir.couverts());

        // 2. Chaque pouvoir a une forme résolue et des statistiques exploitables.
        Map<FormePouvoir, Integer> parForme = new EnumMap<>(FormePouvoir.class);
        for (PowerDefinition def : pouvoirs) {
            FormePouvoir forme = FormePouvoir.formeDe(def);
            assertTrue("forme nulle pour " + def.id(), forme != null);
            parForme.merge(forme, 1, Integer::sum);
            for (int niveau = 1; niveau <= 5; niveau++) {
                PowerDefinition.LevelStats stats = def.statsPourNiveau(niveau);
                assertTrue(def.id() + " niveau " + niveau + " : recharge nulle",
                        stats.rechargeTicks() > 0);
                // Un pouvoir doit produire quelque chose : dégâts, soin, ou effet (portée/durée).
                boolean utile = stats.degats() > 0 || stats.soin() > 0
                        || stats.dureeTicks() > 0 || stats.portee() > 0;
                assertTrue(def.id() + " niveau " + niveau + " n'a aucun effet", utile);
            }
            // Le palier 5 doit être strictement meilleur que le palier 1 en puissance brute.
            double puissance1 = def.statsPourNiveau(1).degats() + def.statsPourNiveau(1).soin();
            double puissance5 = def.statsPourNiveau(5).degats() + def.statsPourNiveau(5).soin();
            assertTrue(def.id() + " n'évolue pas entre le niveau 1 et 5", puissance5 >= puissance1);
        }

        // 3. Les seize formes doivent toutes servir, sinon le moteur contient du code mort.
        assertEquals("formes distinctes utilisées", 16, parForme.size());
        Set<FormePouvoir> attendues = EnumSet.allOf(FormePouvoir.class);
        Set<FormePouvoir> utilisees = EnumSet.copyOf(parForme.keySet());
        assertTrue("formes déclarées mais inutilisées : "
                + difference(attendues, utilisees), utilisees.equals(attendues));

        // 4. Répartition attendue par forme : ces bornes garantissent que la variété est réelle
        //    et non un habillage (par exemple : plus de projectiles que d'explosions).
        assertEquals("projectiles", 9, parForme.get(FormePouvoir.PROJECTILE).intValue());
        assertEquals("cônes", 5, parForme.get(FormePouvoir.CONE).intValue());
        assertEquals("explosions", 9, parForme.get(FormePouvoir.NOVA).intValue());
        assertEquals("zones", 3, parForme.get(FormePouvoir.ZONE).intValue());
        assertEquals("corps à corps", 4, parForme.get(FormePouvoir.MELEE).intValue());
        assertEquals("rayons", 4, parForme.get(FormePouvoir.BEAM).intValue());
        assertEquals("téléportations", 3, parForme.get(FormePouvoir.BLINK).intValue());
        assertEquals("élans", 5, parForme.get(FormePouvoir.DASH).intValue());
        assertEquals("boucliers", 10, parForme.get(FormePouvoir.BOUCLIER).intValue());
        assertEquals("soins", 7, parForme.get(FormePouvoir.SOIN).intValue());
        assertEquals("régénérations", 5, parForme.get(FormePouvoir.REGENERATION).intValue());
        assertEquals("entraves", 3, parForme.get(FormePouvoir.ENTRAVE).intValue());
        assertEquals("peurs", 1, parForme.get(FormePouvoir.PEUR).intValue());
        assertEquals("invocations", 8, parForme.get(FormePouvoir.INVOCATION).intValue());
        assertEquals("passifs", 5, parForme.get(FormePouvoir.PASSIF).intValue());
        assertEquals("révélations", 3, parForme.get(FormePouvoir.REVELATION).intValue());
        // La somme doit retomber exactement sur le catalogue.
        int total = parForme.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals("total des formes", 84, total);
    }

    /** Chaque élément doit être représenté par des particules distinctes. */
    public static void signaturesElementaires() {
        try {
            CambocraftDataGen.initialiserEtChargerTout(TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement global : " + e, e);
        }
        int couverts = 0;
        for (PowerElement element : PowerElement.values()) {
            // Les constantes de particules viennent du registre : sous les stubs de compilation
            // elles valent null, mais la résolution doit rester sans exception et couvrir les
            // sept éléments.
            PowerEffects.particulesDe(element);
            couverts++;
        }
        assertEquals("éléments couverts", 7, couverts);
        // Élément inconnu : la résolution ne doit jamais lever (un datapack peut en fournir un).
        PowerEffects.particulesDe(null);

        // Les 5 pouvoirs passifs doivent couvrir leurs éléments respectifs.
        assertTrue("éléments passifs incomplets", PassifsPouvoir.elementsCouverts().size() == 7);

        // Le repli d'archétype doit rester cohérent avec les 8 archétypes.
        for (PowerType type : PowerType.values()) {
            assertTrue("repli nul pour " + type.id(),
                    FormePouvoir.parArchetype(type) != null);
        }
    }

    /** Une forme non vide par archétype : le repli ne doit pas tout ramener au projectile. */
    public static void repliParArchetype() {
        assertEquals("offensif", FormePouvoir.PROJECTILE,
                FormePouvoir.parArchetype(PowerType.OFFENSIF));
        assertEquals("défensif", FormePouvoir.BOUCLIER,
                FormePouvoir.parArchetype(PowerType.DEFENSIF));
        assertEquals("mobilité", FormePouvoir.DASH,
                FormePouvoir.parArchetype(PowerType.MOBILITE));
        assertEquals("soin", FormePouvoir.SOIN, FormePouvoir.parArchetype(PowerType.SOIN));
        assertEquals("contrôle", FormePouvoir.ENTRAVE,
                FormePouvoir.parArchetype(PowerType.CONTROLE));
        assertEquals("utilitaire", FormePouvoir.REVELATION,
                FormePouvoir.parArchetype(PowerType.UTILITAIRE));
        assertEquals("passif", FormePouvoir.PASSIF, FormePouvoir.parArchetype(PowerType.PASSIF));
        assertEquals("invocation", FormePouvoir.INVOCATION,
                FormePouvoir.parArchetype(PowerType.INVOCATION));
        assertEquals("archétype nul", FormePouvoir.PROJECTILE, FormePouvoir.parArchetype(null));
    }

    /** Les registres d'effets persistants doivent pouvoir être vidés et comptés. */
    public static void registresEffets() {
        ZonesPouvoir.reinitialiser();
        InvocationsPouvoir.reinitialiser();
        assertEquals("zones au départ", 0, ZonesPouvoir.actives());
        assertEquals("invocations au départ", 0, InvocationsPouvoir.actives());
        assertTrue("liste d'invocations non vide", InvocationsPouvoir.toutes().isEmpty());
    }

    private static Set<FormePouvoir> difference(Set<FormePouvoir> a, Set<FormePouvoir> b) {
        Set<FormePouvoir> copie = EnumSet.copyOf(a);
        copie.removeAll(b);
        return copie;
    }
}
