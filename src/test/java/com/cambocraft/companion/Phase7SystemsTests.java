package com.cambocraft.companion;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertNear;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.client.AccessibilityConfig;
import com.cambocraft.client.CamboHudModel;
import com.cambocraft.datagen.CambocraftDataGen;
import com.cambocraft.ecology.Season;
import com.cambocraft.minigame.ApsaraDanceGame;
import com.cambocraft.minigame.NightMarketManager;
import com.cambocraft.minigame.PirogueRaceCourse;
import com.cambocraft.minigame.PradalSereyArena;
import com.cambocraft.network.CamboPayloads;
import com.cambocraft.network.ServerPacketValidator;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.PowerRarity;
import com.cambocraft.power.PowerRegistry;
import com.cambocraft.test.TestSupport;
import java.util.Set;

/**
 * Suite de tests unitaires de la Phase 7 :
 * compagnon Neak Ta (3 stades), montures (Buffle d'eau, Pirogue), mini-jeux culturels
 * (Pradal Serey, Course de pirogues, Danse Apsara, Marché de nuit), validation réseau C2S
 * et interface client / accessibilité.
 */
public final class Phase7SystemsTests {
    private Phase7SystemsTests() {}

    public static void compagnonsMiniJeuxReseauEtClient() {
        try {
            CambocraftDataGen.initialiserEtChargerTout(TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement global : " + e, e);
        }

        // 1. Compagnon Neak Ta à 3 stades d'évolution
        NeakTaCompanion neakTa = new NeakTaCompanion();
        assertEquals(NeakTaCompanion.EvolutionStage.STADE_1_LUEUR_DU_SANCTUAIRE, neakTa.stadeActuel());
        assertTrue(!neakTa.tenterSauvetageFatal(1L), "Pas de sauvetage fatal au stade 1");
        for (int i = 0; i < 3; i++) {
            neakTa.presenterOffrande("cambocraft:bol_de_riz_beni"); // +360
        }
        assertEquals(NeakTaCompanion.EvolutionStage.STADE_2_GARDIEN_DE_GRES, neakTa.stadeActuel());
        for (int i = 0; i < 5; i++) {
            neakTa.presenterOffrande("cambocraft:bol_de_riz_beni"); // +600 -> 960
        }
        assertEquals(NeakTaCompanion.EvolutionStage.STADE_3_AVATAR_ANCESTRAL_DU_LOTUS, neakTa.stadeActuel());
        assertTrue(neakTa.tenterSauvetageFatal(5L), "Sauvetage fatal accordé le jour 5");
        assertTrue(!neakTa.tenterSauvetageFatal(5L), "Un seul sauvetage par jour de jeu");
        assertTrue(neakTa.tenterSauvetageFatal(6L), "Réinitialisé le jour 6");

        // 2. Montures Buffle d'eau et Pirogue
        double vBuffleBoue = MountSystem.vitesseBuffleDEau(true, Season.MOUSSON);
        assertTrue(vBuffleBoue > MountSystem.VITESSE_BASE_BUFFLE, "Buffle plus agile dans la boue de mousson");
        double vPirogueCourse = MountSystem.vitessePirogue(1.0, Season.SAISON_DOUCE, true);
        assertTrue(vPirogueCourse > MountSystem.VITESSE_BASE_PIROGUE * 1.5, "Bonus de cadence et régate");

        // 3. Arène de Pradal Serey (3 paliers et rythme Sarama)
        assertEquals(3, PradalSereyArena.ArenaTier.values().length);
        double degatsCoude = PradalSereyArena.calculerDegatsTechnique(
                PradalSereyArena.StrikeMove.COUP_DE_COUDE_DESCENDANT, 20.0, 5, 1);
        assertNear(20.0 * 1.50 * 1.24 * 1.25, degatsCoude, 1e-6);

        // 4. Course de pirogues (3 parcours, portes ordonnées et médailles)
        PirogueRaceCourse course = new PirogueRaceCourse(
                PirogueRaceCourse.CourseTrack.PARCOURS_1_BOUCLE_DE_TONLE, 1000L);
        assertTrue(!course.franchirPorte(2, 1100L), "Porte hors ordre refusée");
        for (int p = 0; p < 6; p++) {
            assertTrue(course.franchirPorte(p, 1100L + p * 120L), "Porte " + p + " validée");
        }
        assertTrue(course.estTerminee(), "Course terminée après les 6 portes");
        assertEquals(PirogueRaceCourse.Medal.OR, course.medailleObtenue());

        PirogueRaceCourse.Leaderboard lb = new PirogueRaceCourse.Leaderboard();
        assertTrue(lb.enregistrerTemps(PirogueRaceCourse.CourseTrack.PARCOURS_1_BOUCLE_DE_TONLE, 850L), "Record initial");
        assertTrue(!lb.enregistrerTemps(PirogueRaceCourse.CourseTrack.PARCOURS_1_BOUCLE_DE_TONLE, 920L), "Moins bon temps ignoré");
        assertTrue(lb.enregistrerTemps(PirogueRaceCourse.CourseTrack.PARCOURS_1_BOUCLE_DE_TONLE, 780L), "Nouveau record battu");

        // 5. Danse Apsara et Marché de nuit
        ApsaraDanceGame danse = new ApsaraDanceGame();
        for (ApsaraDanceGame.MudraGesture g : ApsaraDanceGame.CHOREGRAPHIE_SACREE) {
            assertTrue(danse.executerGeste(g, 1), "Geste Apsara " + g);
        }
        assertTrue(danse.estComplete(), "Chorégraphie Apsara complète");
        assertTrue(danse.dureeBenedictionTicks() >= 4800, "Bénédiction de grâce active");
        assertTrue(NightMarketManager.estOuvert(16000L), "Marché de nuit ouvert à 16000 ticks");
        assertTrue(!NightMarketManager.estOuvert(6000L), "Marché de nuit fermé à midi");

        // 6. Validation serveur des paquets C2S
        NenNotebook carnet = new NenNotebook(150);
        carnet.ajouterTirage(PowerRegistry.get("onde_du_tonle"));
        carnet.ajouterTirage(PowerRegistry.get("braise_du_foyer"));
        assertEquals(
                ServerPacketValidator.ValidationStatus.AUTORISE,
                ServerPacketValidator.validerActivationPouvoir(
                        new CamboPayloads.ActiverPouvoirC2S(0, 500L), carnet, 500L, 490L));
        assertEquals(
                ServerPacketValidator.ValidationStatus.REFUSE_CADENCE_EXCESSIVE,
                ServerPacketValidator.validerActivationPouvoir(
                        new CamboPayloads.ActiverPouvoirC2S(1, 501L), carnet, 501L, 500L));
        assertEquals(
                ServerPacketValidator.ValidationStatus.REFUSE_TROP_LOIN,
                ServerPacketValidator.validerTeleportationLotus(
                        new CamboPayloads.TeleporterLotusC2S("village_de_tonle", "rizieres_de_ly", 12.5),
                        30, 0, Set.of("chef_des_bandits_de_la_riviere"),
                        Set.of("village_de_tonle", "rizieres_de_ly")));

        // 7. Accessibilité et HUD client
        AccessibilityConfig acc = new AccessibilityConfig();
        acc.definirModeDaltonisme(AccessibilityConfig.ColorblindMode.DEUTERANOPIE);
        acc.definirEffetsVisuelsReduits(true);
        acc.definirEchelleTexte(1.25);
        assertEquals(0x0072B2, acc.couleurRareteAccessible(PowerRarity.RARE));
        assertNear(0.25, acc.multiplicateurParticules(), 1e-6);

        CamboHudModel.HudSnapshot hud = CamboHudModel.construire(
                45, 0, 1000L, carnet, 500L, "L'Offrande du Bol");
        assertEquals(45, hud.niveau());
        assertEquals(1, hud.nombreSynergiesActives());
    }
}
