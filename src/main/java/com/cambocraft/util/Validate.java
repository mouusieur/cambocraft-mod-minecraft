package com.cambocraft.util;

/** Groupe les vérifications d'arguments ; un message explicite vaut mieux qu'une exception générique. */
public final class Validate {
    private Validate() {}

    public static int between(String name, int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " hors bornes : " + value + " (attendu " + min + ".." + max + ")");
        }
        return value;
    }

    public static double between(String name, double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " hors bornes : " + value + " (attendu " + min + ".." + max + ")");
        }
        return value;
    }

    public static <T> T notNull(String name, T value) {
        if (value == null) throw new IllegalArgumentException(name + " ne peut pas être nul");
        return value;
    }

    public static String text(String name, String value) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException(name + " ne peut pas être vide");
        return value;
    }
}
