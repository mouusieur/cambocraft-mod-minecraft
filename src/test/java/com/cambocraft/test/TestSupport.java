package com.cambocraft.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Petit cadre de test maison.
 *
 * <p>Objectif : pouvoir exécuter les tests du noyau de règles hors ligne, sans Minecraft ni
 * JUnit (l'environnement de développement ne dispose pas toujours des dépôts Maven). La classe
 * {@code CAMBOJUnit} enveloppe ces mêmes cas pour que {@code ./gradlew test} fonctionne quand
 * JUnit 5 est disponible : une seule source de vérité, deux façons de l'exécuter.</p>
 */
public final class TestSupport {
    private TestSupport() {}

    /** Dossier racine des données JSON du mod ({@code src/main/resources/data/cambocraft}). */
    public static Path dataRoot() {
        String prop = System.getProperty("cambocraft.data.dir");
        if (prop != null && !prop.isBlank()) return Paths.get(prop);
        return Paths.get("src", "main", "resources", "data", "cambocraft");
    }

    /** Assertion simple : la condition doit être vraie. */
    public static void assertTrue(String message, boolean condition) {
        if (!condition) throw new AssertionError(message);
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    public static void assertEquals(String message, Object attendu, Object obtenu) {
        boolean egal = attendu == null ? obtenu == null : attendu.equals(obtenu);
        if (!egal) throw new AssertionError(message + " (attendu : " + attendu + ", obtenu : " + obtenu + ")");
    }

    public static void assertEquals(Object attendu, Object obtenu) {
        assertEquals("valeurs distinctes", attendu, obtenu);
    }

    public static void assertEquals(String message, double attendu, double obtenu, double tolerance) {
        if (Math.abs(attendu - obtenu) > tolerance) {
            throw new AssertionError(message + " (attendu : " + attendu + ", obtenu : " + obtenu + ")");
        }
    }

    public static void assertEquals(String message, int attendu, int obtenu) {
        if (attendu != obtenu) {
            throw new AssertionError(message + " (attendu : " + attendu + ", obtenu : " + obtenu + ")");
        }
    }

    public static void assertEquals(int attendu, int obtenu) {
        assertEquals("entiers distincts", attendu, obtenu);
    }

    public static void assertEquals(long attendu, long obtenu) {
        if (attendu != obtenu) {
            throw new AssertionError("longs distincts (attendu : " + attendu + ", obtenu : " + obtenu + ")");
        }
    }

    public static void assertNear(String message, double attendu, double obtenu, double toleranceRelative) {
        double tolerance = Math.max(toleranceRelative, Math.abs(attendu) * toleranceRelative);
        if (Math.abs(attendu - obtenu) > tolerance) {
            throw new AssertionError(message + " (attendu ≈ " + attendu + " ±" + tolerance + ", obtenu : " + obtenu + ")");
        }
    }

    public static void assertNear(double attendu, double obtenu, double toleranceAbsolue) {
        if (Math.abs(attendu - obtenu) > toleranceAbsolue) {
            throw new AssertionError("attendu ≈ " + attendu + " ±" + toleranceAbsolue + ", obtenu : " + obtenu);
        }
    }

    public static <T> void assertContains(String message, List<T> liste, T valeur) {
        if (!liste.contains(valeur)) throw new AssertionError(message + " (liste : " + liste + ")");
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    /** Résultat d'une campagne de tests. */
    public static final class Resultat {
        public final List<String> echecs = new ArrayList<>();
        public int total;
    }
}
