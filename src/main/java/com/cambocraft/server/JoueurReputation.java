package com.cambocraft.server;

import com.cambocraft.npc.Faction;
import com.cambocraft.npc.ReputationTier;
import java.util.EnumMap;
import java.util.Map;

/**
 * Réputation d'un joueur auprès des quatre factions régionales (§6.2).
 *
 * <p>La réputation n'existait jusqu'ici que sous forme de formule ({@code Economy.palier},
 * {@code ReputationTier}) sans aucun endroit où la stocker : ce registre la rend concrète. Elle
 * pilote les prix marchands, le déblocage des spécialités culinaires et les choix de dialogue
 * conditionnels ({@code reputation_min}).</p>
 *
 * <p>Les gains et pertes passent par des helpers nommés plutôt que par des additions dispersées,
 * ce qui rend les conséquences auditables : une quête terminée, une offrande au Neak Ta ou un
 * mauvais choix de dialogue se traduisent par un même point d'entrée.</p>
 */
public final class JoueurReputation {
    /** Bornes de sécurité : une faction ne descend jamais sous -10 000 ni ne dépasse 10 000. */
    public static final int MINIMUM = -10_000;
    public static final int MAXIMUM = 10_000;

    private final Map<Faction, Integer> points = new EnumMap<>(Faction.class);

    public JoueurReputation() {
        for (Faction f : Faction.values()) {
            points.put(f, 0);
        }
    }

    /** Points de réputation auprès d'une faction. */
    public int points(Faction faction) {
        return points.getOrDefault(faction, 0);
    }

    /** Palier de réputation correspondant ({@code inconnu} … {@code légendaire}). */
    public ReputationTier palier(Faction faction) {
        return ReputationTier.pourPoints(points(faction));
    }

    /** Ajoute (ou retire, si négatif) des points et renvoie le nouveau total. */
    public int ajouter(Faction faction, int delta) {
        int total = Math.max(MINIMUM, Math.min(MAXIMUM, points(faction) + delta));
        points.put(faction, total);
        return total;
    }

    /** Réputation maximale toutes factions confondues : sert à l'affichage du HUD. */
    public int meilleure() {
        int max = Integer.MIN_VALUE;
        for (Faction f : Faction.values()) {
            max = Math.max(max, points(f));
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    /** Copie immuable, pour la synchronisation réseau et les sauvegardes. */
    public Map<Faction, Integer> tout() {
        return Map.copyOf(points);
    }

    /** Restaure une valeur lue d'une sauvegarde. */
    public void restaurer(Faction faction, int valeur) {
        points.put(faction, Math.max(MINIMUM, Math.min(MAXIMUM, valeur)));
    }
}
