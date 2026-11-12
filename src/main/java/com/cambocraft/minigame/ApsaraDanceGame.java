package com.cambocraft.minigame;

import java.util.List;
import java.util.Objects;

/**
 * Mini-jeu culturel de **Danse Apsara** :
 * le joueur reproduit en rythme une séquence de 4 mudras et postures classiques khmères
 * devant le pavillon du sanctuaire pour recevoir la *Bénédiction de Grâce Céleste*.
 */
public final class ApsaraDanceGame {
    public enum MudraGesture {
        MUDRA_BOUTON_DE_LOTUS,
        MUDRA_FLEUR_EPANOUIE,
        POSTURE_ARC_CELESTE,
        SALUT_ROYAL_SAMPEAH
    }

    public static final List<MudraGesture> CHOREGRAPHIE_SACREE = List.of(
            MudraGesture.MUDRA_BOUTON_DE_LOTUS,
            MudraGesture.MUDRA_FLEUR_EPANOUIE,
            MudraGesture.POSTURE_ARC_CELESTE,
            MudraGesture.SALUT_ROYAL_SAMPEAH
    );

    private int etapeActuelle = 0;
    private int scorePrecision = 0;

    public int etapeActuelle() {
        return etapeActuelle;
    }

    public int scorePrecision() {
        return scorePrecision;
    }

    public boolean estComplete() {
        return etapeActuelle >= CHOREGRAPHIE_SACREE.size();
    }

    /**
     * Exécute un geste de la chorégraphie avec un écart rythmique en ticks ({@code ecartTicks}).
     */
    public boolean executerGeste(MudraGesture geste, int ecartTicks) {
        Objects.requireNonNull(geste, "geste");
        if (estComplete()) {
            return false;
        }
        MudraGesture attendu = CHOREGRAPHIE_SACREE.get(etapeActuelle);
        if (geste != attendu || Math.abs(ecartTicks) > 8) {
            etapeActuelle = 0;
            scorePrecision = 0;
            return false;
        }
        scorePrecision += Math.abs(ecartTicks) <= 2 ? 30 : 20;
        etapeActuelle++;
        return true;
    }

    /**
     * Durée en ticks du buff de grâce obtenu à l'issue de la danse complète.
     */
    public int dureeBenedictionTicks() {
        if (!estComplete()) {
            return 0;
        }
        return 2400 + scorePrecision * 20;
    }
}
