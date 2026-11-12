package com.cambocraft.combat;

import java.util.List;
import java.util.Objects;

/**
 * Moteur de rencontre d'un boss multi-phases :
 * gère les transitions de phases aux seuils de PV ({@code 100 % -> 65 % -> 30 % [-> 12 %]}),
 * la fenêtre d'invulnérabilité de transition entre deux phases (30 ticks) et les multiplicateurs actifs.
 */
public final class BossEncounterEngine {
    public static final int TICKS_INVULNERABILITE_TRANSITION = 30;

    private final EnemyDefinition boss;
    private double pvActuels;
    private int indexPhaseActuelle = 0;
    private long finTransitionTick = -1L;

    public BossEncounterEngine(EnemyDefinition boss) {
        this.boss = Objects.requireNonNull(boss, "boss");
        if (!boss.estBoss() || boss.phases().size() < 3) {
            throw new IllegalArgumentException("BossEncounterEngine requiert un boss à >= 3 phases: " + boss.id());
        }
        this.pvActuels = boss.pvBase();
    }

    public EnemyDefinition boss() { return boss; }
    public double pvActuels() { return pvActuels; }
    public double pvMax() { return boss.pvBase(); }
    public double ratioPv() { return pvActuels / boss.pvBase(); }
    public boolean estVaincu() { return pvActuels <= 0.0; }

    public BossPhaseSpec phaseActuelle() {
        return boss.phases().get(indexPhaseActuelle);
    }

    public int numeroPhaseActuelle() {
        return phaseActuelle().numero();
    }

    public boolean enTransitionInvulnerable(long tickActuel) {
        return tickActuel < finTransitionTick;
    }

    /**
     * Applique des dégâts au boss au tick {@code tickActuel}.
     * Si un seuil de phase est franchi, plafonne les PV au seuil de la nouvelle phase
     * et ouvre une fenêtre d'invulnérabilité de transition de 30 ticks.
     *
     * @return {@code true} si une nouvelle phase vient de se déclencher
     */
    public boolean appliquerDegats(double degats, long tickActuel) {
        if (degats <= 0.0 || estVaincu() || enTransitionInvulnerable(tickActuel)) {
            return false;
        }
        pvActuels = Math.max(0.0, pvActuels - degats);
        List<BossPhaseSpec> phases = boss.phases();
        if (indexPhaseActuelle + 1 < phases.size()) {
            BossPhaseSpec prochaine = phases.get(indexPhaseActuelle + 1);
            double pvSeuil = boss.pvBase() * prochaine.seuilPvRatio();
            if (pvActuels <= pvSeuil) {
                pvActuels = Math.max(pvActuels, pvSeuil);
                indexPhaseActuelle++;
                finTransitionTick = tickActuel + TICKS_INVULNERABILITE_TRANSITION;
                return true;
            }
        }
        return false;
    }

    public double attaqueEffective() {
        return boss.attaqueBase() * phaseActuelle().multiplicateurAttaque();
    }

    public double vitesseEffective() {
        return boss.vitesse() * phaseActuelle().multiplicateurVitesse();
    }
}
