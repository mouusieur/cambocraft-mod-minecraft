package com.cambocraft.minigame;

import com.cambocraft.util.Localized;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Courses de pirogues traditionnelles (*Bon Om Touk*) sur **3 parcours** homologués,
 * avec validation ordonnée des portes de bouées et classement des records (§2 & D-06).
 */
public final class PirogueRaceCourse {
    public enum CourseTrack {
        PARCOURS_1_BOUCLE_DE_TONLE(
                "boucle_de_tonle",
                new Localized("Boucle du Village de Tonlé", "Tonle Village Loop"),
                6,
                1200,
                900,
                100
        ),
        PARCOURS_2_TRAVERSEE_DU_GRAND_LAC(
                "traversee_du_grand_lac",
                new Localized("Traversée du Grand Lac", "Great Lake Crossing"),
                10,
                2200,
                1650,
                250
        ),
        PARCOURS_3_REGATE_ROYALE_DES_TROIS_RIVIERES(
                "regate_royale_des_trois_rivieres",
                new Localized("Régate Royale des Trois Rivières", "Royal Three Rivers Regatta"),
                14,
                3200,
                2400,
                600
        );

        private final String id;
        private final Localized nom;
        private final int nombrePortes;
        private final int tempsBronzeTicks;
        private final int tempsOrTicks;
        private final int piecesRecompenseOr;

        CourseTrack(
                String id,
                Localized nom,
                int nombrePortes,
                int tempsBronzeTicks,
                int tempsOrTicks,
                int piecesRecompenseOr
        ) {
            this.id = id;
            this.nom = nom;
            this.nombrePortes = nombrePortes;
            this.tempsBronzeTicks = tempsBronzeTicks;
            this.tempsOrTicks = tempsOrTicks;
            this.piecesRecompenseOr = piecesRecompenseOr;
        }

        public String id() { return id; }
        public Localized nom() { return nom; }
        public int nombrePortes() { return nombrePortes; }
        public int tempsBronzeTicks() { return tempsBronzeTicks; }
        public int tempsOrTicks() { return tempsOrTicks; }
        public int piecesRecompenseOr() { return piecesRecompenseOr; }
    }

    public enum Medal {
        AUCUNE,
        BRONZE,
        ARGENT,
        OR
    }

    private final CourseTrack parcours;
    private final long tickDepart;
    private int prochainePorte = 0;
    private long tickArrivee = -1L;

    public PirogueRaceCourse(CourseTrack parcours, long tickDepart) {
        this.parcours = Objects.requireNonNull(parcours, "parcours");
        this.tickDepart = tickDepart;
    }

    public CourseTrack parcours() { return parcours; }
    public int portesFranchies() { return prochainePorte; }
    public boolean estTerminee() { return tickArrivee >= 0L; }

    /**
     * Valide le franchissement de la porte d'indice {@code indexPorte} dans l'ordre strict {@code 0..N-1}.
     */
    public boolean franchirPorte(int indexPorte, long tickActuel) {
        if (estTerminee() || indexPorte != prochainePorte) {
            return false;
        }
        prochainePorte++;
        if (prochainePorte >= parcours.nombrePortes()) {
            tickArrivee = Math.max(tickDepart + 1L, tickActuel);
        }
        return true;
    }

    public long dureeCourseTicks() {
        if (!estTerminee()) {
            return -1L;
        }
        return tickArrivee - tickDepart;
    }

    public Medal medailleObtenue() {
        long d = dureeCourseTicks();
        if (d < 0L) {
            return Medal.AUCUNE;
        }
        if (d <= parcours.tempsOrTicks()) {
            return Medal.OR;
        }
        long argent = (parcours.tempsOrTicks() + parcours.tempsBronzeTicks()) / 2L;
        if (d <= argent) {
            return Medal.ARGENT;
        }
        if (d <= parcours.tempsBronzeTicks()) {
            return Medal.BRONZE;
        }
        return Medal.AUCUNE;
    }

    /**
     * Tableau des records personnels d'un joueur sur les 3 parcours.
     */
    public static final class Leaderboard {
        private final Map<CourseTrack, Long> meilleursTempsTicks = new EnumMap<>(CourseTrack.class);

        public boolean enregistrerTemps(CourseTrack track, long dureeTicks) {
            if (dureeTicks <= 0L) {
                return false;
            }
            Long precedent = meilleursTempsTicks.get(track);
            if (precedent == null || dureeTicks < precedent) {
                meilleursTempsTicks.put(track, dureeTicks);
                return true;
            }
            return false;
        }

        public Long meilleurTemps(CourseTrack track) {
            return meilleursTempsTicks.get(track);
        }

        public Map<CourseTrack, Long> tous() {
            return Collections.unmodifiableMap(meilleursTempsTicks);
        }
    }
}
