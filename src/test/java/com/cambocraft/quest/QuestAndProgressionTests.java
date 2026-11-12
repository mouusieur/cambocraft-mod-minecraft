package com.cambocraft.quest;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertFalse;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.data.DataManager;
import com.cambocraft.progression.ProgressionCurve;
import com.cambocraft.progression.ZoneDefinition;
import com.cambocraft.progression.ZoneRegistry;
import com.cambocraft.test.TestSupport;
import com.cambocraft.world.LotusTeleporter;
import java.util.Set;

/** Tests de la courbe d'XP (1–200), des 12 zones, des téléporteurs et du moteur de quêtes (85 quêtes, 3 fins). */
public final class QuestAndProgressionTests {
    private QuestAndProgressionTests() {}

    public static void progressionZonesEtQuetes() {
        ZoneRegistry.init();
        QuestRegistry.init();
        try {
            DataManager.chargerDepuisDossier(TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement : " + e, e);
        }

        // 1. Courbe d'expérience 120 * n^1.8 (§12.1)
        assertEquals(120L, ProgressionCurve.xpPourNiveauSuivant(1));
        assertEquals(Math.round(120.0 * Math.pow(50, 1.8)), ProgressionCurve.xpPourNiveauSuivant(50));
        assertEquals(0L, ProgressionCurve.xpPourNiveauSuivant(200));

        ProgressionCurve.EtatNiveau etat = ProgressionCurve.ajouterXp(1, 0L, 150L);
        assertEquals(2, etat.niveau());
        assertEquals(30L, etat.xpDansNiveau());
        assertEquals(1, etat.niveauxGagnes());

        assertFalse("impossible de renaître avant le niveau 200", ProgressionCurve.peutRenaitre(199, 0, true));
        assertTrue(ProgressionCurve.peutRenaitre(200, 0, true), "Renaissance disponible au niveau 200 après le Gardien");

        // 2. Les 12 zones et le verrouillage des téléporteurs de Pierre du Lotus (§12.2)
        assertEquals(12, ZoneRegistry.zones().size());
        ZoneDefinition z1 = ZoneRegistry.parOrdre(1);
        ZoneDefinition z2 = ZoneRegistry.parOrdre(2);
        ZoneDefinition z12 = ZoneRegistry.parOrdre(12);
        assertTrue(z1.estDebloquee(1, 0, Set.of()), "la zone 1 est ouverte dès le niveau 1");
        assertFalse("la zone 2 exige le boss de la zone 1", z2.estDebloquee(20, 0, Set.of()));
        assertTrue(z2.estDebloquee(20, 0, Set.of("chef_des_bandits_de_la_riviere")),
                "la zone 2 s'ouvre au niveau 15+ après le boss de la zone 1");
        assertFalse("le Royaume des Esprits (zone 12) est verrouillé avant 200/Renaissance",
                z12.estDebloquee(150, 0, Set.of("le_gardien_de_l_equilibre")));
        assertTrue(z12.estDebloquee(1, 1, Set.of("le_gardien_de_l_equilibre")),
                "le Royaume des Esprits s'ouvre après une Renaissance");
        assertEquals(2, LotusTeleporter.zonesAccessibles(20, 0, Set.of("chef_des_bandits_de_la_riviere")).size());

        // 3. Quêtes : 12 chapitres principaux, >= 60 secondaires et 3 fins au chapitre 12 (§8)
        assertEquals(12, QuestRegistry.chapitresPrincipaux().size());
        long nbSecondaires = QuestRegistry.quetes().values().stream()
                .filter(q -> q.type() == QuestDefinition.TypeQuete.SECONDAIRE)
                .count();
        assertTrue(nbSecondaires >= 60, "au moins 60 quêtes secondaires sont requises");

        QuestDefinition chap12 = QuestRegistry.quete("chapitre_12_la_lecon_du_bol");
        assertEquals(3, chap12.branches().size());

        // Simulation d'exécution de la quête d'intro puis du choix de fin du chapitre 12
        QuestEngine moteur = new QuestEngine();
        QuestDefinition intro = QuestRegistry.quete("intro_premier_bol_du_tonle");
        assertTrue(moteur.demarrer(intro, 1, "saison_douce"), "la quête d'intro doit démarrer");
        assertFalse("une quête d'intro ne peut pas être abandonnée", moteur.abandonner(intro));
        assertFalse("première étape seulement", moteur.notifierObjectif(intro, "parler", "maitre_de_quete", 1));
        assertTrue(moteur.notifierObjectif(intro, "parler", "apprenti_moine", 1), "la quête d'intro doit se terminer");
        assertTrue(moteur.terminees().contains("intro_premier_bol_du_tonle"), "intro marquée terminée");

        // Choix des 3 fins sur La Leçon du Bol
        for (String voie : new String[]{"lumiere", "ombre", "equilibre"}) {
            assertTrue(moteur.choisirBranche(chap12, voie), "la voie " + voie + " doit être sélectionnable");
            assertEquals(voie, moteur.brancheChoisie(chap12.id()));
        }
    }
}
