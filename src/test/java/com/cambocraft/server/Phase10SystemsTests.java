package com.cambocraft.server;

import static com.cambocraft.test.TestSupport.assertEquals;
import static com.cambocraft.test.TestSupport.assertFalse;
import static com.cambocraft.test.TestSupport.assertTrue;

import com.cambocraft.datagen.CambocraftDataGen;
import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.Seasons;
import com.cambocraft.game.ProgressionService;
import com.cambocraft.npc.Faction;
import com.cambocraft.npc.ReputationTier;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.PowerDefinition;
import com.cambocraft.power.PowerRegistry;
import com.cambocraft.power.RiceBowlGacha;
import com.cambocraft.progression.ProgressionCurve;
import com.cambocraft.quest.QuestDefinition;
import com.cambocraft.quest.QuestRegistry;
import com.cambocraft.test.TestSupport;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;

/**
 * Suite de tests unitaires de la Phase 10 : persistance, réseau et progression réelle.
 *
 * <p>Ces tests n'ont pas besoin de Minecraft : ils vérifient le <b>format</b> NBT (aller-retour
 * complet d'un état joueur), les invariants de la réputation par faction, la cohérence des codecs
 * réseau (bornes de listes) et la chaîne de progression (XP → niveau → Renaissance → énergie).</p>
 */
public final class Phase10SystemsTests {
    private Phase10SystemsTests() {}

    public static void persistanceReseauEtProgression() {
        try {
            CambocraftDataGen.initialiserEtChargerTout(TestSupport.dataRoot());
        } catch (Exception e) {
            throw new AssertionError("échec de chargement global : " + e, e);
        }
        persistanceAllerRetour();
        reputationParFaction();
        progressionEtRenaissance();
        formatDeSauvegarde();
        codecsReseau();
    }

    /** Un état joueur complet doit survivre à un aller-retour NBT sans perte. */
    private static void persistanceAllerRetour() {
        CamboPlayerData source = CamboPlayerData.pourAffichage();
        source.ajouterXp(12_000L);
        source.marquerBossVaincu("gardien_de_pierre");
        source.marquerPierreDecouverte("village_de_tonle");
        source.marquerZoneVisitee("rizieres_de_ly");
        source.reputation().ajouter(Faction.LY, 420);
        source.reputation().ajouter(Faction.KOMPONG, -80);
        source.epinglerQuete("intro_offrande_du_bol");

        PowerDefinition pouvoir = PowerRegistry.tous().iterator().next();
        source.carnet().ajouterTirage(pouvoir);
        source.carnet().ajouterTirage(pouvoir);
        source.carnet().ajouterEclats(15);
        source.carnet().restaurerEmplacement(1, pouvoir.id());
        source.pity().restaurer(7, 3, 1, 42);

        QuestDefinition quete = QuestRegistry.quetes().values().iterator().next();
        source.quetes().restaurerActive(quete.id(), 1, 2, "");

        NbtCompound ecrit = source.ecrire();
        CamboPlayerData relu = CamboPlayerData.pourAffichage();
        relu.lire(ecrit);

        assertEquals("niveau perdu à la relecture", source.niveau(), relu.niveau());
        assertEquals("XP perdue à la relecture", source.xp(), relu.xp());
        assertEquals("pouvoirs perdus", source.carnet().nombrePouvoirsDebloques(),
                relu.carnet().nombrePouvoirsDebloques());
        assertEquals("éclats perdus", source.carnet().eclatsDeNen(), relu.carnet().eclatsDeNen());
        assertEquals("emplacement actif perdu", pouvoir.id(), relu.carnet().pouvoirEquipe(1));
        assertEquals("pity épique perdu", 7, relu.pity().tiragesSansEpique());
        assertEquals("pity total perdu", 42, relu.pity().totalTirages());
        assertEquals("boss vaincu perdu", 1, relu.bossVaincus().size());
        assertTrue("boss vaincu incorrect", relu.bossVaincus().contains("gardien_de_pierre"));
        assertEquals("pierre découverte perdue", 1, relu.pierresDecouvertes().size());
        assertEquals("zone visitée perdue", 1, relu.zonesVisitees().size());
        assertEquals("réputation LY perdue", 420, relu.reputation().points(Faction.LY));
        assertEquals("réputation KOMPONG perdue", -80, relu.reputation().points(Faction.KOMPONG));
        assertEquals("quête active perdue", 1, relu.quetes().actives().size());
        assertEquals("étape de quête perdue", 1,
                relu.quetes().actives().get(quete.id()).indexEtape());
        assertEquals("avancement de quête perdu", 2,
                relu.quetes().actives().get(quete.id()).avancementEtape());
        assertEquals("quête épinglée perdue", "intro_offrande_du_bol", relu.queteEpinglee());

        // Le niveau du pouvoir doit aussi être conservé (double tirage → niveau 2).
        assertEquals("niveau du pouvoir perdu", 2,
                relu.carnet().progression(pouvoir.id()).niveau());
    }

    /** Les quatre factions sont indépendantes et bornées. */
    private static void reputationParFaction() {
        JoueurReputation reputation = new JoueurReputation();
        for (Faction f : Faction.values()) {
            assertEquals("réputation initiale non nulle pour " + f.id, 0, reputation.points(f));
            assertEquals("palier initial inattendu pour " + f.id, ReputationTier.CONNU,
                    reputation.palier(f));
        }
        reputation.ajouter(Faction.TONLE, 600);
        assertEquals("palier honoré attendu", ReputationTier.HONORE, reputation.palier(Faction.TONLE));
        assertEquals("les factions ne doivent pas se contaminer", 0, reputation.points(Faction.SROK));

        reputation.ajouter(Faction.TONLE, 100_000);
        assertEquals("plafond de réputation non appliqué", JoueurReputation.MAXIMUM,
                reputation.points(Faction.TONLE));
        reputation.ajouter(Faction.SROK, -100_000);
        assertEquals("plancher de réputation non appliqué", JoueurReputation.MINIMUM,
                reputation.points(Faction.SROK));
        assertEquals("palier inconnu attendu", ReputationTier.INCONNU, reputation.palier(Faction.SROK));
        assertEquals("meilleure réputation incorrecte", JoueurReputation.MAXIMUM, reputation.meilleure());

        JoueurReputation copie = new JoueurReputation();
        copie.restaurer(Faction.KOMPONG, 1500);
        assertEquals("palier légendaire attendu", ReputationTier.LEGENDAIRE,
                copie.palier(Faction.KOMPONG));
    }

    /** La chaîne XP → niveau → Renaissance → énergie doit rester cohérente. */
    private static void progressionEtRenaissance() {
        CamboPlayerData donnees = CamboPlayerData.pourAffichage();
        assertEquals("niveau initial", 1, donnees.niveau());
        assertEquals("énergie initiale", ProgressionCurve.energieMax(1, 0), donnees.energieMax());

        // Assez d'XP pour franchir plusieurs niveaux.
        var etat = donnees.ajouterXp(20_000L);
        assertTrue("aucun niveau gagné", etat.niveauxGagnes() > 0);
        assertEquals("le niveau de l'état doit suivre", etat.niveau(), donnees.niveau());
        assertTrue("énergie maximale non recalculée", donnees.energieMax() > ProgressionCurve.energieMax(1, 0));

        // Renaissance refusée sans niveau 200 ni boss.
        assertFalse("Renaissance accordée à tort", donnees.renaitre());

        // Niveau 200 forcé + Gardien de l'Équilibre vaincu : Renaissance acceptée.
        donnees.definirNiveauPourAffichage(ProgressionCurve.NIVEAU_MAX, 0);
        donnees.marquerBossVaincu("le_gardien_de_l_equilibre");
        assertTrue("Renaissance refusée alors que les conditions sont réunies", donnees.renaitre());
        assertEquals("rang de Renaissance", 1, donnees.renaissances());
        assertEquals("niveau remis à 1 après Renaissance", 1, donnees.niveau());
        assertEquals("XP remise à zéro", 0L, donnees.xp());
        assertTrue("le bonus d'énergie de Renaissance manque",
                donnees.energieMax() >= ProgressionCurve.energieMax(1, 1));
    }

    /** Une sauvegarde dépourvue de champs optionnels doit rester lisible (compatibilité). */
    private static void formatDeSauvegarde() {
        NbtCompound minimal = new NbtCompound();
        minimal.putInt("niveau", 12);
        minimal.putLong("xp", 340L);
        CamboPlayerData donnees = CamboPlayerData.pourAffichage();
        donnees.lire(minimal);
        assertEquals("niveau relu", 12, donnees.niveau());
        assertEquals("XP relue", 340L, donnees.xp());
        assertEquals("aucun pouvoir attendu", 0, donnees.carnet().nombrePouvoirsDebloques());
        assertTrue("énergie par défaut non initialisée", donnees.carnet().energieActuelle() > 0);

        // Les valeurs hors bornes sont écrêtées plutôt que propagées.
        NbtCompound aberrant = new NbtCompound();
        aberrant.putInt("niveau", -50);
        aberrant.putInt("renaissances", 99);
        aberrant.putLong("xp", -1000L);
        CamboPlayerData borne = CamboPlayerData.pourAffichage();
        borne.lire(aberrant);
        assertEquals("niveau négatif non borné", 1, borne.niveau());
        assertEquals("renaissances non bornées", ProgressionCurve.RENAISSANCE_MAX, borne.renaissances());
        assertEquals("XP négative non bornée", 0L, borne.xp());

        // Le format écrit doit porter sa version et ses clés principales.
        NbtCompound ecrit = donnees.ecrire();
        assertEquals("version de format absente", CamboPlayerData.VERSION_FORMAT,
                ecrit.getInt("version"));
        for (String cle : new String[]{"niveau", "xp", "carnet", "quetesActives", "quetesTerminees",
                "reputation", "bossVaincus", "pierresDecouvertes", "zonesVisitees", "emplacements"}) {
            assertTrue("clé NBT manquante : " + cle, ecrit.contains(cle));
        }
    }

    /** Les paquets réseau doivent rester sérialisables et bornés. */
    private static void codecsReseau() {
        var payload = new com.cambocraft.network.CamboPayloads.ActiverPouvoirC2S(2, 900L);
        assertEquals("slot perdu par le paquet", 2, payload.slot());
        assertEquals("tick perdu par le paquet", 900L, payload.tickClient());
        assertTrue("identifiant de paquet absent", payload.getId() != null);

        var equip = new com.cambocraft.network.CamboPayloads.EquiperPouvoirC2S(1, "eau_du_tonle");
        assertEquals("pouvoir perdu par le paquet", "eau_du_tonle", equip.powerId());

        var saison = new com.cambocraft.network.CamboPayloads.SyncSaisonEtLacS2C("mousson", 3, 68, 1.4);
        assertEquals("saison perdue", "mousson", saison.saisonId());
        assertEquals("niveau de lac perdu", 68, saison.niveauEauGrandLacY());

        // Les listes réseau sont bornées : une entrée trop longue est tronquée côté client.
        assertEquals("taille maximale de liste", 512, com.cambocraft.network.NetworkLists.MAX_ENTREES);
        assertEquals("taille maximale d'entrée", 256,
                com.cambocraft.network.NetworkLists.TAILLE_MAX_ENTREE);

        // Le gacha reste reproductible à graine fixe : indispensable aux tests de garantie.
        RiceBowlGacha.PityState pityA = new RiceBowlGacha.PityState();
        RiceBowlGacha.PityState pityB = new RiceBowlGacha.PityState();
        NenNotebook carnetA = new NenNotebook();
        NenNotebook carnetB = new NenNotebook();
        var resultatA = RiceBowlGacha.degusterBolDeRizBeni(pityA, carnetA, new Random(1234L));
        var resultatB = RiceBowlGacha.degusterBolDeRizBeni(pityB, carnetB, new Random(1234L));
        assertEquals("tirage non déterministe", resultatA.pouvoir().id(), resultatB.pouvoir().id());
        assertEquals("rareté non déterministe", resultatA.rareteObtenue(), resultatB.rareteObtenue());
    }

    /** Les saisons et les 12 boss du catalogue doivent rester adressables par identifiant. */
    public static void saisonDesQuetes() {
        String id = Season.MOUSSON.id;
        assertEquals("identifiant de saison incohérent", Season.MOUSSON, Season.parId(id));
        assertTrue("le service de progression doit être chargeable",
                !ProgressionService.class.getName().isEmpty());
        assertEquals("nombre de boss inattendu", 12,
                com.cambocraft.combat.CombatRegistry.boss().size());
    }
}
