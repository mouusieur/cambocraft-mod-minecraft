package com.cambocraft.ecology;

import com.cambocraft.util.Tick;
import net.minecraft.world.World;

/**
 * Cycle des saisons, calculé à partir du temps du monde.
 *
 * <p>Un jour de jeu dure 24 000 ticks (20 minutes). Par défaut une saison dure 8 jours de jeu
 * (2 h 40 réelles), soit un cycle complet de 24 jours ; la durée est configurable dans
 * {@code config/cambocraft.json} ({@code saisons.duree_jours}). Le calcul est déterministe : tous
 * les joueurs et le serveur voient la même saison sans synchronisation réseau.</p>
 */
public final class Seasons {
    /** Durée par défaut d'une saison, en jours de jeu. */
    public static final int DUREE_DEFAUT = 8;
    private static int dureeJours = DUREE_DEFAUT;

    private Seasons() {}

    public static void configurer(int jours) {
        dureeJours = Math.max(1, Math.min(365, jours));
    }

    public static int dureeJours() {
        return dureeJours;
    }

    /** Numéro du jour de jeu depuis le début du monde. */
    public static long jour(long tempsMonde) {
        return Math.floorDiv(tempsMonde, Tick.UN_DAY);
    }

    /** Saison en cours d'après le temps du monde. */
    public static Season saison(long tempsMonde) {
        long cycle = Math.max(1, dureeJours);
        long index = Math.floorDiv(jour(tempsMonde), cycle) % Season.values().length;
        return Season.values()[(int) index];
    }

    public static Season saison(World monde) {
        return saison(monde.getTime());
    }

    /** Jour dans la saison, de 1 à la durée configurée. */
    public static int jourDansSaison(long tempsMonde) {
        return (int) Math.floorMod(jour(tempsMonde), Math.max(1, dureeJours)) + 1;
    }

    /** Progression dans la saison, de 0 (début) à 1 (fin). */
    public static double progression(long tempsMonde) {
        double dansJour = (double) Math.floorMod(tempsMonde, (long) Tick.UN_DAY) / Tick.UN_DAY;
        return (jourDansSaison(tempsMonde) - 1 + dansJour) / dureeJours;
    }

    /** Numéro du cycle saisonnier complet (1 pour la première année du monde). */
    public static long annee(long tempsMonde) {
        return Math.floorDiv(jour(tempsMonde), (long) Math.max(1, dureeJours) * Season.values().length) + 1;
    }

    /** Nombre de ticks restants avant la saison suivante. */
    public static long ticksAvantSaisonSuivante(long tempsMonde) {
        long duree = (long) Math.max(1, dureeJours) * Tick.UN_DAY;
        return duree - Math.floorMod(tempsMonde, duree);
    }
}
