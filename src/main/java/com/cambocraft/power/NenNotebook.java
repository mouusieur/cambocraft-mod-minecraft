package com.cambocraft.power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Carnet du Nen d'un joueur :
 * <ul>
 *   <li>Stockage sans limite de tous les pouvoirs débloqués (niveaux 1 à 5 + compteur de maîtrise)</li>
 *   <li>3 emplacements actifs simultanés ({@code slots 0..2})</li>
 *   <li>Calcul des synergies actives entre les pouvoirs équipés</li>
 *   <li>Gestion serveur de l'énergie spirituelle (aura) et des temps de recharge</li>
 * </ul>
 */
public final class NenNotebook {
    public static final int NB_EMPLACEMENTS_ACTIFS = 3;
    public static final int NIVEAU_MAX_POUVOIR = 5;

    public record PowerProgress(int niveau, int utilisations, boolean maitriseDebloquee) {}

    public record UnlockOutcome(int niveauActuel, boolean doublonNiveauMaxConverti) {}

    private final Map<String, PowerProgress> collection = new LinkedHashMap<>();
    private final String[] emplacementsActifs = new String[NB_EMPLACEMENTS_ACTIFS];
    private final long[] finRechargeTick = new long[NB_EMPLACEMENTS_ACTIFS];
    private int energieMax = 100;
    private int energieActuelle = 100;
    private int eclatsDeNen = 0;

    public NenNotebook() {}

    public NenNotebook(int energieMax) {
        this.energieMax = Math.max(20, energieMax);
        this.energieActuelle = this.energieMax;
    }

    /**
     * Ajoute un tirage de pouvoir au Carnet :
     * débloque au niveau 1, ou incrémente le niveau jusqu'à 5 ; au-delà du niveau 5,
     * convertit le doublon en progression de maîtrise (+25 utilisations) et éclats de Nen.
     */
    public UnlockOutcome ajouterTirage(PowerDefinition def) {
        Objects.requireNonNull(def, "def");
        PowerProgress actuel = collection.get(def.id());
        if (actuel == null) {
            PowerProgress init = new PowerProgress(1, 0, false);
            collection.put(def.id(), init);
            // Équipe automatiquement dans le premier emplacement libre
            for (int i = 0; i < NB_EMPLACEMENTS_ACTIFS; i++) {
                if (emplacementsActifs[i] == null) {
                    emplacementsActifs[i] = def.id();
                    break;
                }
            }
            return new UnlockOutcome(1, false);
        }
        if (actuel.niveau() < NIVEAU_MAX_POUVOIR) {
            int nv = actuel.niveau() + 1;
            collection.put(def.id(), new PowerProgress(nv, actuel.utilisations(), actuel.maitriseDebloquee()));
            return new UnlockOutcome(nv, false);
        }
        // Doublon d'un pouvoir déjà niveau 5 : accélère la maîtrise et octroie des éclats
        int nouvUses = actuel.utilisations() + 25;
        boolean maitrise = actuel.maitriseDebloquee() || nouvUses >= def.maitrise().seuilUtilisations();
        collection.put(def.id(), new PowerProgress(NIVEAU_MAX_POUVOIR, nouvUses, maitrise));
        eclatsDeNen += (def.rarete().ordinal() + 1) * 5;
        return new UnlockOutcome(NIVEAU_MAX_POUVOIR, true);
    }

    public boolean possede(String powerId) {
        return collection.containsKey(powerId);
    }

    public PowerProgress progression(String powerId) {
        return collection.get(powerId);
    }

    public Map<String, PowerProgress> collection() {
        return Collections.unmodifiableMap(collection);
    }

    public int nombrePouvoirsDebloques() {
        return collection.size();
    }

    public int eclatsDeNen() {
        return eclatsDeNen;
    }

    /**
     * Équipe un pouvoir débloqué dans l'emplacement {@code 0..2}.
     */
    public boolean equiper(int slot, String powerId) {
        if (slot < 0 || slot >= NB_EMPLACEMENTS_ACTIFS) {
            return false;
        }
        if (powerId == null) {
            emplacementsActifs[slot] = null;
            return true;
        }
        if (!collection.containsKey(powerId)) {
            return false;
        }
        // Évite d'équiper deux fois le même pouvoir dans deux emplacements différents
        for (int i = 0; i < NB_EMPLACEMENTS_ACTIFS; i++) {
            if (i != slot && powerId.equals(emplacementsActifs[i])) {
                emplacementsActifs[i] = null;
            }
        }
        emplacementsActifs[slot] = powerId;
        return true;
    }

    public String pouvoirEquipe(int slot) {
        if (slot < 0 || slot >= NB_EMPLACEMENTS_ACTIFS) {
            return null;
        }
        return emplacementsActifs[slot];
    }

    public Set<String> ensemblePouvoirsEquipes() {
        Set<String> set = new LinkedHashSet<>();
        for (String s : emplacementsActifs) {
            if (s != null) {
                set.add(s);
            }
        }
        return set;
    }

    /**
     * Retourne toutes les synergies actives parmi les 3 pouvoirs actuellement équipés.
     */
    public List<SynergyDefinition> synergiesActives() {
        Set<String> equipes = ensemblePouvoirsEquipes();
        if (equipes.size() < 2) {
            return List.of();
        }
        List<SynergyDefinition> actives = new ArrayList<>();
        for (SynergyDefinition syn : PowerRegistry.toutesSynergies()) {
            if (syn.estActivePour(equipes)) {
                actives.add(syn);
            }
        }
        return actives;
    }

    /**
     * Multiplicateur combiné des synergies actives (plafonné à ×2.25 pour préserver l'équilibre).
     */
    public double multiplicateurSynergieTotal() {
        double mult = 1.0;
        for (SynergyDefinition syn : synergiesActives()) {
            mult *= syn.multiplicateur();
        }
        return Math.min(2.25, mult);
    }

    /**
     * Tente d'activer le pouvoir de l'emplacement {@code slot} au tick serveur {@code tickActuel}.
     * Vérifie la présence du pouvoir, le temps de recharge et le coût en énergie spirituelle.
     */
    public boolean activerPouvoir(int slot, long tickActuel) {
        if (slot < 0 || slot >= NB_EMPLACEMENTS_ACTIFS) {
            return false;
        }
        String id = emplacementsActifs[slot];
        if (id == null) {
            return false;
        }
        PowerDefinition def = PowerRegistry.get(id);
        PowerProgress prog = collection.get(id);
        if (def == null || prog == null) {
            return false;
        }
        if (tickActuel < finRechargeTick[slot]) {
            return false;
        }
        PowerDefinition.LevelStats stats = def.statsPourNiveau(prog.niveau());
        if (energieActuelle < stats.coutEnergie()) {
            return false;
        }
        energieActuelle -= stats.coutEnergie();
        finRechargeTick[slot] = tickActuel + stats.rechargeTicks();

        int nouvUses = prog.utilisations() + 1;
        boolean maitrise = prog.maitriseDebloquee() || nouvUses >= def.maitrise().seuilUtilisations();
        collection.put(id, new PowerProgress(prog.niveau(), nouvUses, maitrise));
        return true;
    }

    public void regenererEnergie(int montant) {
        if (montant > 0) {
            energieActuelle = Math.min(energieMax, energieActuelle + montant);
        }
    }

    public void definirEnergieMax(int nouvelleMax) {
        this.energieMax = Math.max(20, nouvelleMax);
        this.energieActuelle = Math.min(this.energieActuelle, this.energieMax);
    }

    public int energieActuelle() { return energieActuelle; }
    public int energieMax() { return energieMax; }

    /** Fixe l'énergie courante en la bornant (utilisé par la sauvegarde et la synchronisation). */
    public void definirEnergie(int energie) {
        this.energieActuelle = Math.max(0, Math.min(this.energieMax, energie));
    }

    /** Crédite des éclats de Nen (doublons convertis, récompenses de quête). */
    public void ajouterEclats(int montant) {
        if (montant > 0) {
            eclatsDeNen += montant;
        }
    }

    /**
     * Restaure un pouvoir depuis une sauvegarde, sans passer par un tirage : le niveau, les
     * utilisations, la maîtrise et l'emplacement actif sont repris tels quels.
     */
    public void restaurerPouvoir(String powerId, int niveau, int utilisations, boolean maitrise) {
        if (powerId == null || powerId.isEmpty()) {
            return;
        }
        int nv = Math.max(1, Math.min(NIVEAU_MAX_POUVOIR, niveau));
        collection.put(powerId, new PowerProgress(nv, Math.max(0, utilisations), maitrise));
    }

    /** Équipe un pouvoir lors d'une restauration, sans vérifier qu'il vient d'être débloqué. */
    public void restaurerEmplacement(int slot, String powerId) {
        if (slot < 0 || slot >= NB_EMPLACEMENTS_ACTIFS) {
            return;
        }
        if (powerId != null && !powerId.isEmpty() && collection.containsKey(powerId)) {
            emplacementsActifs[slot] = powerId;
        }
    }
}
