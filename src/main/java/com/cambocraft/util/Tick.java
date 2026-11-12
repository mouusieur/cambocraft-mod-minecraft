package com.cambocraft.util;

/**
 * Conversions de durées de jeu.
 *
 * <p>Un jour Minecraft dure 24 000 ticks, soit 20 minutes réelles à vitesse normale.
 * UN_DAY est la seule constante à utiliser pour parler de « jours de jeu » : les saisons,
 * les routines des PNJ, la marée du Grand Lac et les quêtes quotidiennes s'appuient dessus.</p>
 */
public final class Tick {
    public static final int PER_SECOND = 20;
    public static final int PER_MINUTE = 1200;
    public static final int UN_DAY = 24000;
    public static final int UN_HOUR = 1000;

    private Tick() {}

    public static int seconds(double s) {
        return (int) Math.round(s * PER_SECOND);
    }

    public static int minutes(double m) {
        return (int) Math.round(m * PER_MINUTE);
    }

    public static int days(double d) {
        return (int) Math.round(d * UN_DAY);
    }
}
