package com.cambocraft.test;

import com.cambocraft.companion.Phase7SystemsTests;
import com.cambocraft.data.DataManagerTests;
import com.cambocraft.ecology.SeasonsTests;
import com.cambocraft.fishing.FishingTests;
import com.cambocraft.npc.EconomyTests;
import com.cambocraft.npc.NpcAndCraftTests;
import com.cambocraft.power.CombatAndPowerTests;
import com.cambocraft.quest.QuestAndProgressionTests;
import com.cambocraft.world.WorldAndEcologyTests;
import java.util.List;

/**
 * Exécuteur des tests du noyau de règles, sans Minecraft.
 *
 * <p>Lancement hors ligne : {@code tools/dev/apicheck/check.sh} (qui compile puis exécute cette
 * classe). Lancement en jeu de développement : {@code ./gradlew camboTest}. Chaque cas est isolé :
 * une exception est comptée comme un échec, jamais comme un arrêt de la campagne.</p>
 */
public final class TestMain {
    private TestMain() {}

    /** Tous les cas de test, dans l'ordre d'exécution. */
    static List<Cas> cas() {
        return List.of(
                new Cas("pêche : table de tirage", FishingTests::tableDeTirage),
                new Cas("pêche : poids, chances et tailles", FishingTests::poidsEtTailles),
                new Cas("saisons : cycle de 8 jours et mousson", SeasonsTests::cycleEtProgression),
                new Cas("économie : paliers de réputation et inflation du riz", EconomyTests::paliersEtInflation),
                new Cas("données : chargement JSON et tolérance aux erreurs", DataManagerTests::chargementEtToleranceAuxErreurs),
                new Cas("monde : portail du lotus, 15 biomes, riz et Grand Lac", WorldAndEcologyTests::portailBiomesRizEtLac),
                new Cas("pnj & artisanat : 16 PNJ, dialogues, marchands, cuisine, soie et forge", NpcAndCraftTests::pnjReputationEtArtisanat),
                new Cas("progression & quêtes : courbe 1-200, 12 zones, 85 quêtes et 3 fins", QuestAndProgressionTests::progressionZonesEtQuetes),
                new Cas("combat & pouvoirs : 84 pouvoirs, 34 synergies, gacha pity, 12 boss et Nuit des Ombres", CombatAndPowerTests::bestiaireBossPouvoirsEtGacha),
                new Cas("compagnons, mini-jeux, réseau & client : Neak Ta, Pradal Serey, pirogues, C2S et HUD", Phase7SystemsTests::compagnonsMiniJeuxReseauEtClient),
                new Cas("persistance, réseau & progression : aller-retour NBT, réputation, Renaissance, codecs", com.cambocraft.server.Phase10SystemsTests::persistanceReseauEtProgression),
                new Cas("saisons & boss : adressage par identifiant et 12 rencontres", com.cambocraft.server.Phase10SystemsTests::saisonDesQuetes));
    }

    public static void main(String[] args) {
        int echecs = 0;
        List<Cas> tous = cas();
        for (Cas c : tous) {
            try {
                c.corps.run();
                System.out.println("  [OK]    " + c.nom);
            } catch (AssertionError e) {
                echecs++;
                System.out.println("  [ÉCHEC] " + c.nom + " → " + e.getMessage());
            } catch (RuntimeException e) {
                echecs++;
                System.out.println("  [ERREUR] " + c.nom + " → " + e);
            }
        }
        System.out.printf("%d test(s), %d échec(s)%n", tous.size(), echecs);
        if (echecs > 0) System.exit(1);
    }

    /** Un cas de test : un nom lisible et un corps. */
    public record Cas(String nom, Runnable corps) {}
}
