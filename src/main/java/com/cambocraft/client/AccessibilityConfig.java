package com.cambocraft.client;

import com.cambocraft.power.PowerRarity;
import java.util.Objects;

/**
 * Options d'accessibilité côté client (§2 de DESIGN.md) :
 * palettes adaptées au daltonisme, réduction des flashs/particules d'orage et de Nen,
 * échelle de police ajustable ({@code 0.85..1.50}) et sous-titres directionnels bilingues.
 */
public final class AccessibilityConfig {
    public enum ColorblindMode {
        AUCUN,
        PROTANOPIE,
        DEUTERANOPIE,
        TRITANOPIE
    }

    private ColorblindMode modeDaltonisme = ColorblindMode.AUCUN;
    private boolean effetsVisuelsReduits = false;
    private double echelleTexte = 1.00;
    private boolean sousTitresActifs = true;

    public ColorblindMode modeDaltonisme() { return modeDaltonisme; }
    public boolean effetsVisuelsReduits() { return effetsVisuelsReduits; }
    public double echelleTexte() { return echelleTexte; }
    public boolean sousTitresActifs() { return sousTitresActifs; }

    public void definirModeDaltonisme(ColorblindMode mode) {
        this.modeDaltonisme = Objects.requireNonNull(mode, "mode");
    }

    public void definirEffetsVisuelsReduits(boolean reduits) {
        this.effetsVisuelsReduits = reduits;
    }

    public void definirEchelleTexte(double echelle) {
        this.echelleTexte = Math.max(0.85, Math.min(1.50, echelle));
    }

    public void definirSousTitresActifs(boolean actifs) {
        this.sousTitresActifs = actifs;
    }

    /**
     * Retourne une couleur RGB contrastée pour la rareté de pouvoir selon le profil daltonien actif.
     */
    public int couleurRareteAccessible(PowerRarity rarete) {
        Objects.requireNonNull(rarete, "rarete");
        if (modeDaltonisme == ColorblindMode.AUCUN) {
            return rarete.couleurRgb();
        }
        return switch (rarete) {
            case COMMUN -> 0xE0E0E0;
            case RARE -> 0x0072B2;       // Bleu Okabe-Ito
            case EPIQUE -> 0xCC79A7;     // Magenta contrasté
            case LEGENDAIRE -> 0xF0E442; // Jaune haute luminance
            case MYTHIQUE -> 0xD55E00;   // Vermillon distinct
        };
    }

    /**
     * Densité de particules autorisée ({@code 0.25} en mode effets réduits, {@code 1.0} sinon).
     */
    public double multiplicateurParticules() {
        return effetsVisuelsReduits ? 0.25 : 1.00;
    }
}
