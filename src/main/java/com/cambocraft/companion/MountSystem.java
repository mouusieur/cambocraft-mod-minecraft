package com.cambocraft.companion;

import com.cambocraft.ecology.Season;

/**
 * Système de montures traditionnelles de Cambocraft :
 * <ul>
 *   <li>**Buffle d'eau** : insensible au ralentissement de la boue de rizière et de la mousson,
 *       capable de labourer les casiers rizicoles et équipé de sacoches à 12 emplacements</li>
 *   <li>**Pirogue khmère** : embarcation fluviale rapide sur le Tonlé et le Grand Lac,
 *       avec bonus de cadence pendant la Fête des Eaux</li>
 * </ul>
 */
public final class MountSystem {
    public static final int EMPLACEMENTS_SACOCHES_BUFFLE = 12;
    public static final double VITESSE_BASE_BUFFLE = 0.24;
    public static final double VITESSE_BASE_PIROGUE = 0.42;

    private MountSystem() {}

    /**
     * Calcule la vitesse effective du Buffle d'eau selon le terrain et la saison.
     * Contrairement à la marche à pied, le buffle gagne un léger bonus dans l'eau peu profonde
     * et la boue de rizière.
     */
    public static double vitesseBuffleDEau(boolean dansBoueDeRiziere, Season saison) {
        double mult = dansBoueDeRiziere ? 1.15 : 1.00;
        if (saison == Season.MOUSSON) {
            mult *= 1.05;
        }
        return VITESSE_BASE_BUFFLE * mult;
    }

    /**
     * Calcule la vitesse effective d'une pirogue selon la cadence de pagaie ({@code 0..1})
     * et l'état de crue du Grand Lac.
     */
    public static double vitessePirogue(double synchronisationCadence, Season saison, boolean pirogueDeCourse) {
        double sync = Math.max(0.0, Math.min(1.0, synchronisationCadence));
        double base = pirogueDeCourse ? VITESSE_BASE_PIROGUE * 1.25 : VITESSE_BASE_PIROGUE;
        double bonusCadence = 0.85 + 0.45 * sync;
        double bonusSaison = saison == Season.SAISON_DOUCE ? 1.10 : 1.00;
        return base * bonusCadence * bonusSaison;
    }
}
