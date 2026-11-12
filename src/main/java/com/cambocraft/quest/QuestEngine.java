package com.cambocraft.quest;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Moteur d'exécution serveur des quêtes d'un joueur (§8.1) :
 * vérification des conditions de déclenchement, progression étape par étape, choix de branche
 * finale (les 3 voies de La Leçon du Bol : Lumière, Ombre, Équilibre), abandon et répétabilité.
 */
public final class QuestEngine {
    public record ProgressionActive(
            String queteId,
            int indexEtape,
            int avancementEtape,
            String brancheChoisie) {}

    private final Map<String, ProgressionActive> actives = new LinkedHashMap<>();
    private final Set<String> terminees = new LinkedHashSet<>();
    private final Map<String, String> branchesFinales = new LinkedHashMap<>();

    public Map<String, ProgressionActive> actives() {
        return Map.copyOf(actives);
    }

    public Set<String> terminees() {
        return Set.copyOf(terminees);
    }

    /**
     * Restaure une quête terminée depuis une sauvegarde.
     *
     * <p>Utilisé au chargement de l'état joueur ; ne vérifie volontairement aucune condition de
     * déblocage, l'état sauvegardé faisant foi.</p>
     */
    public void restaurerTerminee(String queteId) {
        terminees.add(queteId);
    }

    /** Restaure une quête en cours depuis une sauvegarde (étape, avancement, branche). */
    public void restaurerActive(String queteId, int indexEtape, int avancement, String branche) {
        actives.put(queteId, new ProgressionActive(queteId, indexEtape, avancement, branche));
        if (branche != null) {
            branchesFinales.put(queteId, branche);
        }
    }

    public String brancheChoisie(String queteId) {
        return branchesFinales.get(queteId);
    }

    public boolean peutDemarrer(QuestDefinition q, int niveauJoueur, String saisonCourante) {
        if (q == null) return false;
        if (actives.containsKey(q.id())) return false;
        boolean repetable = q.type() == QuestDefinition.TypeQuete.QUOTIDIENNE
                || q.type() == QuestDefinition.TypeQuete.HEBDOMADAIRE
                || q.type() == QuestDefinition.TypeQuete.SAISONNIERE;
        if (terminees.contains(q.id()) && !repetable) return false;
        if (niveauJoueur < q.niveauMin()) return false;
        if (q.queteRequise() != null && !q.queteRequise().isEmpty() && !terminees.contains(q.queteRequise())) {
            return false;
        }
        if (q.saisonRequise() != null && !q.saisonRequise().isEmpty() && !"*".equals(q.saisonRequise())) {
            if (!q.saisonRequise().equalsIgnoreCase(saisonCourante)) return false;
        }
        return true;
    }

    public boolean demarrer(QuestDefinition q, int niveauJoueur, String saisonCourante) {
        if (!peutDemarrer(q, niveauJoueur, saisonCourante)) return false;
        actives.put(q.id(), new ProgressionActive(q.id(), 0, 0, ""));
        return true;
    }

    /**
     * Notifie un événement de jeu (ex. {@code parler}, {@code vaincre}, {@code recolter},
     * {@code livrer}, {@code visiter}) et fait progresser la quête.
     *
     * @return vrai si la quête vient d'être intégralement terminée
     */
    public boolean notifierObjectif(QuestDefinition q, String objectif, String cible, int quantite) {
        if (q == null) return false;
        ProgressionActive prog = actives.get(q.id());
        if (prog == null || prog.indexEtape() >= q.etapes().size()) return false;
        QuestDefinition.Etape etape = q.etapes().get(prog.indexEtape());
        if (!etape.objectif().equalsIgnoreCase(objectif)) return false;
        if (!etape.cible().isEmpty() && !"*".equals(etape.cible()) && !etape.cible().equalsIgnoreCase(cible)) {
            return false;
        }
        int nouvelAvancement = prog.avancementEtape() + Math.max(1, quantite);
        if (nouvelAvancement < etape.quantite()) {
            actives.put(q.id(), new ProgressionActive(q.id(), prog.indexEtape(), nouvelAvancement, prog.brancheChoisie()));
            return false;
        }
        int prochainIndex = prog.indexEtape() + 1;
        if (prochainIndex < q.etapes().size()) {
            actives.put(q.id(), new ProgressionActive(q.id(), prochainIndex, 0, prog.brancheChoisie()));
            return false;
        }
        // Toutes les étapes sont accomplies : si la quête comporte des branches et qu'aucune n'a été choisie,
        // la branche par défaut (première) est retenue à moins d'un choix explicite préalable.
        String branche = prog.brancheChoisie();
        if ((branche == null || branche.isEmpty()) && !q.branches().isEmpty()) {
            branche = q.branches().get(0).id();
        }
        if (branche != null && !branche.isEmpty()) {
            branchesFinales.put(q.id(), branche);
        }
        actives.remove(q.id());
        terminees.add(q.id());
        return true;
    }

    /** Enregistre le choix d'une branche narrative (ex. Lumière / Ombre / Équilibre au chapitre 12). */
    public boolean choisirBranche(QuestDefinition q, String idBranche) {
        if (q == null || q.branches().isEmpty()) return false;
        boolean existe = false;
        for (QuestDefinition.Branche b : q.branches()) {
            if (b.id().equalsIgnoreCase(idBranche)) {
                existe = true;
                break;
            }
        }
        if (!existe) return false;
        ProgressionActive prog = actives.get(q.id());
        if (prog != null) {
            actives.put(q.id(), new ProgressionActive(q.id(), prog.indexEtape(), prog.avancementEtape(), idBranche));
        }
        branchesFinales.put(q.id(), idBranche);
        return true;
    }

    /** Abandonne une quête active si son type l'autorise (les quêtes principales ne se perdent jamais). */
    public boolean abandonner(QuestDefinition q) {
        if (q == null || !q.type().abandonnable) return false;
        return actives.remove(q.id()) != null;
    }
}
