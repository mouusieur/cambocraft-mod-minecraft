package com.cambocraft.power;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertNear;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.combat.BossEncounterEngine;
import com.cambocraft.combat.CombatEngine;
import com.cambocraft.combat.CombatRegistry;
import com.cambocraft.combat.EnemyDefinition;
import com.cambocraft.combat.ShadowClanRank;
import com.cambocraft.combat.ShadowNinjaAi;
import com.cambocraft.combat.WildlifeSpecies;
import com.cambocraft.data.DataManager;
import com.cambocraft.event.ShadowNightManager;
import com.cambocraft.util.DataBundle;
import java.nio.file.Path;
import java.util.Random;

/**
 * Suite de tests unitaires de la Phase 6 :
 * 84 pouvoirs du Nen (5 raretés, 7 éléments, 8 types), 34 synergies, tirage Gacha avec Pity
 * (30 / 80 / 250), Carnet du Nen, faune cambodgienne, transformation en 5 étapes du Clan Ombre,
 * 12 boss multi-phases et Nuit des Ombres.
 */
public final class CombatAndPowerTests {
    private CombatAndPowerTests() {}

    public static void bestiaireBossPouvoirsEtGacha() {
        PowerRegistry.init();
        CombatRegistry.init();
        ShadowNightManager.init();
        try {
            DataManager.chargerDepuisDossier(com.cambocraft.test.TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement : " + e, e);
        }

        // 1. Catalogue de 84 pouvoirs et minimums par rareté (20 / 18 / 18 / 14 / 10)
        assertTrue(PowerRegistry.tous().size() >= 80, "Au moins 80 pouvoirs attendus");
        assertTrue(PowerRegistry.parRarete(PowerRarity.COMMUN).size() >= 20, ">= 20 communs");
        assertTrue(PowerRegistry.parRarete(PowerRarity.RARE).size() >= 18, ">= 18 rares");
        assertTrue(PowerRegistry.parRarete(PowerRarity.EPIQUE).size() >= 18, ">= 18 épiques");
        assertTrue(PowerRegistry.parRarete(PowerRarity.LEGENDAIRE).size() >= 14, ">= 14 légendaires");
        assertTrue(PowerRegistry.parRarete(PowerRarity.MYTHIQUE).size() >= 10, ">= 10 mythiques");
        assertTrue(PowerRegistry.toutesSynergies().size() >= 30, ">= 30 synergies attendues");

        // 2. Tirage Gacha et garanties de Pity (30 / 80 / 250)
        RiceBowlGacha.PityState pity = new RiceBowlGacha.PityState(29, 0, 0, 29);
        NenNotebook carnet = new NenNotebook(200);
        RiceBowlGacha.GachaRollResult r30 = RiceBowlGacha.degusterBolDeRizBeni(pity, carnet, new Random(123));
        assertTrue(r30.rareteObtenue().auMoins(PowerRarity.EPIQUE), "30e tirage garantit Épique+");
        assertEquals(0, pity.tiragesSansEpique());

        RiceBowlGacha.PityState pity80 = new RiceBowlGacha.PityState(10, 79, 79, 79);
        RiceBowlGacha.GachaRollResult r80 = RiceBowlGacha.degusterBolDeRizBeni(pity80, carnet, new Random(456));
        assertTrue(r80.rareteObtenue().auMoins(PowerRarity.LEGENDAIRE), "80e tirage garantit Légendaire+");
        assertEquals(0, pity80.tiragesSansLegendaire());

        RiceBowlGacha.PityState pity250 = new RiceBowlGacha.PityState(5, 20, 249, 249);
        RiceBowlGacha.GachaRollResult r250 = RiceBowlGacha.degusterBolDeRizBeni(pity250, carnet, new Random(789));
        assertEquals(PowerRarity.MYTHIQUE, r250.rareteObtenue());
        assertEquals(0, pity250.tiragesSansMythique());

        // 3. Carnet du Nen, synergies actives et Maîtrise
        PowerDefinition pOnde = PowerRegistry.get("onde_du_tonle");
        PowerDefinition pBraise = PowerRegistry.get("braise_du_foyer");
        for (int i = 0; i < 6; i++) {
            carnet.ajouterTirage(pOnde);
        }
        carnet.ajouterTirage(pBraise);
        assertEquals(5, carnet.progression("onde_du_tonle").niveau());
        assertTrue(carnet.equiper(0, "onde_du_tonle"), "Équipement slot 0");
        assertTrue(carnet.equiper(1, "braise_du_foyer"), "Équipement slot 1");
        assertTrue(!carnet.synergiesActives().isEmpty(), "Synergie onde_du_tonle + braise_du_foyer active");
        assertTrue(carnet.multiplicateurSynergieTotal() > 1.20, "Multiplicateur de synergie > 1.20");
        assertTrue(carnet.activerPouvoir(0, 100L), "Activation pouvoir slot 0");
        assertTrue(!carnet.activerPouvoir(0, 105L), "En temps de recharge au tick 105");

        // 4. Faune cambodgienne et transformation en 5 étapes des ninjas de l'Ombre
        assertEquals(12, WildlifeSpecies.values().length);
        assertTrue(WildlifeSpecies.BUFFLE_D_EAU.montureApprivoisable(), "Buffle d'eau apprivoisable");

        ShadowNinjaAi ai = new ShadowNinjaAi();
        assertEquals(ShadowNinjaAi.RevealStage.ETAPE_1_DISSIMULE_VILLAGEOIS, ai.etape());
        assertTrue(ai.doitDeclencherRevelation(5.0, true, false, false), "Révélation face à relique");
        for (int t = 0; t < 60; t++) {
            ai.tickTransformation(true);
        }
        assertEquals(ShadowNinjaAi.RevealStage.ETAPE_5_COMBAT_ACTIF, ai.etape());
        assertEquals(
                ShadowNinjaAi.TacticalAction.REPLI_FUMIGENE,
                ShadowNinjaAi.choisirAction(ShadowClanRank.LAME, 4.0, 0.15, true));

        // 5. Bestiaire (56 ennemis dont 12 boss multi-phases) et moteur de rencontre de boss
        assertEquals(12, CombatRegistry.boss().size());
        assertTrue(CombatRegistry.tous().size() >= 56, "56 profils d'ennemis/boss chargés");
        EnemyDefinition gardien = CombatRegistry.get("le_gardien_de_l_equilibre");
        assertEquals(4, gardien.phases().size());
        BossEncounterEngine encounter = new BossEncounterEngine(gardien);
        assertEquals(1, encounter.numeroPhaseActuelle());
        boolean transition = encounter.appliquerDegats(gardien.pvBase() * 0.40, 200L);
        assertTrue(transition, "Passage en phase 2 à 65% PV");
        assertEquals(2, encounter.numeroPhaseActuelle());
        assertTrue(encounter.enTransitionInvulnerable(210L), "Invulnérable pendant la transition");

        // 6. Calcul serveur des dégâts, i-frames et Nuit des Ombres
        assertTrue(CombatEngine.estEnIframe(100L, 105L), "En i-frame à +5 ticks");
        assertTrue(!CombatEngine.estEnIframe(100L, 112L), "Hors i-frame à +12 ticks");
        double degatsEauSurFeu = CombatEngine.calculerDegatsServeur(
                20.0, 3, PowerElement.EAU, PowerElement.FEU, 1.22, 0.20, true, 10.0);
        assertNear(49.018, degatsEauSurFeu, 0.01);

        long tickNuitOmbres = 5L * 24000L + 15000L; // Jour 5 de la saison, 15000 ticks
        assertTrue(ShadowNightManager.estNuitDesOmbres(tickNuitOmbres), "Nuit des Ombres active");
        assertEquals(1, ShadowNightManager.vagueNuitDesOmbres(tickNuitOmbres));
    }
}
