package com.cambocraft.ecology;

/**
 * Les trois saisons du Cambodge.
 *
 * <p>Elles suivent le rythme réel du pays : la saison douce et sèche (novembre à février), la
 * saison chaude (mars à mai) et la mousson (juin à octobre). Chaque saison change la pluie, la
 * brume, la hauteur du Grand Lac, la pousse du riz et les espèces de poissons actives.</p>
 */
public enum Season {
    /** Saison douce et sèche : ciel clair, matins frais, Grand Lac au plus bas. */
    SAISON_DOUCE("saison_douce", 0.15f, 0.0f, 0.35f, 0.6f),
    /** Saison chaude : chaleur sèche, orages rares en fin de journée, rizières en préparation. */
    SAISON_CHAUDE("saison_chaude", 0.10f, 0.35f, 0.5f, 1.0f),
    /** Mousson : pluies longues, brume épaisse, Grand Lac au plus haut, temples engloutis. */
    MOUSSON("mousson", 0.75f, 0.15f, 1.0f, 1.25f);

    /** Identifiant utilisé dans les données JSON. */
    public final String id;
    /** Probabilité de pluie à chaque changement de météo vanilla (0..1). */
    public final float probaPluie;
    /** Allongement de la nuit (heures de jeu) : la mousson assombrit les journées. */
    public final float assombrissement;
    /** Hauteur relative du Grand Lac (0 = étiage, 1 = crue maximale). */
    public final float niveauLac;
    /** Multiplicateur de vitesse de pousse du riz. */
    public final float pousseRiz;

    Season(String id, float probaPluie, float assombrissement, float niveauLac, float pousseRiz) {
        this.id = id;
        this.probaPluie = probaPluie;
        this.assombrissement = assombrissement;
        this.niveauLac = niveauLac;
        this.pousseRiz = pousseRiz;
    }

    public String cleTraduction() {
        return "cambocraft.saison." + id;
    }

    /** Saison suivante dans le cycle. */
    public Season suivante() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static Season parId(String id) {
        for (Season s : values()) if (s.id.equals(id)) return s;
        return SAISON_DOUCE;
    }
}
