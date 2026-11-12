package com.cambocraft.combat;

import com.cambocraft.craft.ForgeUpgrade;
import com.cambocraft.power.PowerElement;

/**
 * Moteur de calcul des dégâts côté serveur, coups critiques et fenêtres d'invulnérabilité (*i-frames*).
 */
public final class CombatEngine {
    public static final int I_FRAMES_TICKS = 10;
    public static final double MULTIPLICATEUR_CRITIQUE = 1.50;

    private CombatEngine() {}

    /**
     * Indique si une entité ayant subi son dernier coup au tick {@code dernierCoupTick}
     * est encore dans sa fenêtre d'invulnérabilité au tick {@code tickActuel}.
     */
    public static boolean estEnIframe(long dernierCoupTick, long tickActuel) {
        return dernierCoupTick >= 0 && (tickActuel - dernierCoupTick) < I_FRAMES_TICKS;
    }

    /**
     * Calcule les dégâts finaux côté serveur en combinant :
     * <ul>
     *   <li>les dégâts de base (arme ou pouvoir du Nen)</li>
     *   <li>le niveau de forge {@code +0..+5} de l'arme ({@link ForgeUpgrade#multiplicateur(int)})</li>
     *   <li>l'affinité élémentaire entre l'élément d'attaque et celui de la cible</li>
     *   <li>le multiplicateur des synergies actives du Carnet du Nen</li>
     *   <li>le bonus de Maîtrise du pouvoir (+20 % à +35 %)</li>
     *   <li>le coup critique ({@code ×1.50})</li>
     *   <li>la réduction d'armure hyperbolique {@code 100 / (100 + armure * 4)}</li>
     * </ul>
     */
    public static double calculerDegatsServeur(
            double degatsBase,
            int niveauForge,
            PowerElement elementAttaque,
            PowerElement elementDefenseur,
            double multiplicateurSynergie,
            double bonusMaitrise,
            boolean critique,
            double armureCible
    ) {
        if (degatsBase <= 0.0) {
            return 0.0;
        }
        double multForge = ForgeUpgrade.multiplicateur(niveauForge);
        double multElem = elementAttaque != null
                ? elementAttaque.multiplicateurContre(elementDefenseur)
                : 1.0;
        double multSyn = Math.max(1.0, multiplicateurSynergie);
        double multMaitrise = 1.0 + Math.max(0.0, bonusMaitrise);
        double multCrit = critique ? MULTIPLICATEUR_CRITIQUE : 1.0;
        double reductionArmure = 100.0 / (100.0 + Math.max(0.0, armureCible) * 4.0);

        double brut = degatsBase * multForge * multElem * multSyn * multMaitrise * multCrit;
        return Math.max(0.5, brut * reductionArmure);
    }
}
