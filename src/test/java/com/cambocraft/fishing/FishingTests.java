package com.cambocraft.fishing;

import com.cambocraft.test.TestSupport;
import com.cambocraft.util.Localized;
import java.util.List;
import java.util.Random;

/** Tests du mini-jeu de pêche : table de tirage, poids, prix et tailles. */
public final class FishingTests {
    private FishingTests() {}

    private static FishSpecies espece(String id, FishRarity rarete, int poids, int prix, List<String> zones, boolean nocturne) {
        return new FishSpecies(id, new Localized(id, id), "cambocraft:" + id, rarete, poids, zones,
                List.of("*"), nocturne, 10, 20, 0.3, prix);
    }

    private static FishingTable table() {
        return new FishingTable(List.of(
                espece("a", FishRarity.COMMUN, 50, 3, List.of("riviere"), false),
                espece("b", FishRarity.PEU_COMMUN, 25, 6, List.of("riviere"), false),
                espece("c", FishRarity.RARE, 10, 20, List.of("riviere"), false),
                espece("d", FishRarity.EPIQUE, 2, 90, List.of("riviere"), true),
                espece("e", FishRarity.COMMUN, 40, 3, List.of("lac"), false)));
    }

    public static void tableDeTirage() {
        FishingTable t = table();
        TestSupport.assertEquals("espèces actives de jour en rivière", 3, t.candidates("riviere", "saison_douce", true).size());
        TestSupport.assertEquals("espèces actives de nuit en rivière", 4, t.candidates("riviere", "saison_douce", false).size());
        TestSupport.assertEquals("aucune espèce active dans une zone vide", 0, t.candidates("desert", "saison_douce", true).size());
        TestSupport.assertTrue("tirage sans candidat = vide",
                t.tirer("desert", "saison_douce", true, 0, new Random(1)).isEmpty());

        // Aucune espèce nocturne ne doit sortir de jour, sur 200 000 tirages.
        Random r = new Random(42);
        for (int i = 0; i < 200_000; i++) {
            FishSpecies e = t.tirer("riviere", "saison_douce", true, 0.5, r).orElseThrow();
            if (e.id().equals("d")) TestSupport.fail("une espèce nocturne a mordu de jour");
        }
    }

    public static void poidsEtTailles() {
        // Les fréquences observées doivent suivre les poids annoncés (tolérance relative de 3 %).
        FishingTable t = table();
        Random r = new Random(7);
        int n = 400_000;
        int[] compte = new int[3];
        for (int i = 0; i < n; i++) {
            String id = t.tirer("riviere", "saison_douce", true, 0, r).orElseThrow().id();
            switch (id) {
                case "a" -> compte[0]++;
                case "b" -> compte[1]++;
                default -> compte[2]++;
            }
        }
        double total = 50 + 25 + 10;
        TestSupport.assertNear("proportion de communes", 50 / total, compte[0] / (double) n, 0.03);
        TestSupport.assertNear("proportion de peu communes", 25 / total, compte[1] / (double) n, 0.03);
        TestSupport.assertNear("proportion de rares", 10 / total, compte[2] / (double) n, 0.05);

        // Avec une chance maximale, les rares doivent être nettement plus fréquentes.
        FishingTable t2 = table();
        Random r2 = new Random(11);
        int rares = 0;
        for (int i = 0; i < n; i++) {
            if (t2.tirer("riviere", "saison_douce", true, 1.0, r2).orElseThrow().rarity() == FishRarity.RARE) rares++;
        }
        TestSupport.assertTrue("la chance augmente la part des rares", rares / (double) n > 0.15);

        // Prix : une prise de taille maximale vaut plus qu'une prise minimale, à espèce égale.
        FishSpecies s = espece("x", FishRarity.RARE, 10, 20, List.of("*"), false);
        TestSupport.assertTrue("prix croissant avec la taille", s.prixDeVente(20) > s.prixDeVente(10));

        // Tailles toujours dans l'intervalle annoncé.
        Random r3 = new Random(3);
        for (int i = 0; i < 10_000; i++) {
            int taille = FishingTable.taille(s, r3);
            TestSupport.assertTrue("taille dans l'intervalle", taille >= s.minCm() && taille <= s.maxCm());
        }
    }
}
