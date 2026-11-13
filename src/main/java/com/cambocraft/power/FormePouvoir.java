package com.cambocraft.power;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Les seize <b>formes mécaniques</b> d'un pouvoir du Nen, et la table qui assigne chaque
 * pouvoir livré à l'une d'elles.
 *
 * <p>Un pouvoir n'est pas seulement un nom et des dégâts : il doit produire un effet réel,
 * différent des 83 autres. Deux pouvoirs peuvent partager une forme (un projectile, une
 * explosion) sans se ressembler, parce que la forme est paramétrée par les statistiques du
 * pouvoir (dégâts, portée, durée, soin) et par son élément (particules, son, effets appliqués).</p>
 *
 * <p>La table est explicite, pouvoir par pouvoir : un pouvoir mal classé se voit immédiatement,
 * alors qu'une heuristique par mots-clés produirait des résultats incohérents. Le classement de
 * secours ({@link #parArchetype}) ne sert qu'aux pouvoirs ajoutés par un datapack.</p>
 */
public enum FormePouvoir {
    /** Trait qui frappe une cible unique à distance. */
    PROJECTILE,
    /** Cône de souffle qui touche tout ce qui est devant le lanceur. */
    CONE,
    /** Explosion sphérique centrée sur le lanceur. */
    NOVA,
    /** Zone persistante au sol : dégâts continus sur les entités qui s'y trouvent. */
    ZONE,
    /** Frappe au corps à corps : courte portée, dégâts élevés, recul. */
    MELEE,
    /** Rayon continu d'une cible vers le lanceur. */
    BEAM,
    /** Téléportation vers le point visé, avec dégâts d'arrivée optionnels. */
    BLINK,
    /** Élan : propulsion et/ou vol temporaire. */
    DASH,
    /** Bouclier : absorption et résistance pendant une durée. */
    BOUCLIER,
    /** Soin immédiat (soi-même ou alliés proches). */
    SOIN,
    /** Régénération progressive sur la durée. */
    REGENERATION,
    /** Entrave : les cibles perdent leur mobilité. */
    ENTRAVE,
    /** Peur : les cibles fuient le lanceur. */
    PEUR,
    /** Invocation : alliés temporaires combattent pour le lanceur. */
    INVOCATION,
    /** Marque : bonus permanent tant que le pouvoir est équipé. */
    PASSIF,
    /** Révélation : met en évidence les entités proches à travers les murs. */
    REVELATION;

    /** Forme explicite de chaque pouvoir livré, par identifiant. */
    private static final java.util.Map<String, FormePouvoir> TABLE = java.util.Map.ofEntries(
            // --- Ombre ---
            java.util.Map.entry("abime_de_l_ombre_premiere", FormePouvoir.NOVA),
            java.util.Map.entry("chaine_d_ardoise", FormePouvoir.ENTRAVE),
            java.util.Map.entry("eclipse_du_generalissime", FormePouvoir.NOVA),
            java.util.Map.entry("griffe_nocturne", FormePouvoir.MELEE),
            java.util.Map.entry("lame_du_crepuscule", FormePouvoir.MELEE),
            java.util.Map.entry("lames_jumelles_de_l_ombre", FormePouvoir.MELEE),
            java.util.Map.entry("legion_des_ombres_silencieuses", FormePouvoir.INVOCATION),
            java.util.Map.entry("mirage_du_clan_ombre", FormePouvoir.INVOCATION),
            java.util.Map.entry("pas_feutre", FormePouvoir.BLINK),
            java.util.Map.entry("prison_de_nuit", FormePouvoir.PEUR),
            java.util.Map.entry("saut_de_l_ombre", FormePouvoir.BLINK),
            java.util.Map.entry("voile_absolu_du_neant", FormePouvoir.ZONE),
            java.util.Map.entry("voile_d_encre", FormePouvoir.REVELATION),
            // --- Air ---
            java.util.Map.entry("ailes_du_vent_karstique", FormePouvoir.DASH),
            java.util.Map.entry("ascension_des_iles_celestes", FormePouvoir.DASH),
            java.util.Map.entry("bond_du_bambou", FormePouvoir.DASH),
            java.util.Map.entry("elan_du_gibbon", FormePouvoir.DASH),
            java.util.Map.entry("lance_de_foudre_celeste", FormePouvoir.PROJECTILE),
            java.util.Map.entry("oeil_du_cyclone", FormePouvoir.BEAM),
            java.util.Map.entry("plume_legere", FormePouvoir.PASSIF),
            java.util.Map.entry("rafale_des_falaises", FormePouvoir.CONE),
            java.util.Map.entry("souffle_des_palmes", FormePouvoir.CONE),
            java.util.Map.entry("souverain_des_cieux_garuda", FormePouvoir.INVOCATION),
            java.util.Map.entry("tempete_du_roi_garuda", FormePouvoir.NOVA),
            java.util.Map.entry("tourbillon_de_feuilles", FormePouvoir.BOUCLIER),
            // --- Feu ---
            java.util.Map.entry("ardeur_du_pradal", FormePouvoir.PASSIF),
            java.util.Map.entry("armure_de_braise_sacree", FormePouvoir.BOUCLIER),
            java.util.Map.entry("braise_du_foyer", FormePouvoir.PROJECTILE),
            java.util.Map.entry("colere_du_phenix", FormePouvoir.NOVA),
            java.util.Map.entry("danse_des_flammes_khmeres", FormePouvoir.DASH),
            java.util.Map.entry("flamme_de_forge", FormePouvoir.PROJECTILE),
            java.util.Map.entry("flamme_eternelle_du_lotus_d_or", FormePouvoir.BEAM),
            java.util.Map.entry("garde_de_cendre", FormePouvoir.BOUCLIER),
            java.util.Map.entry("renaissance_du_phenix_de_laterite", FormePouvoir.REGENERATION),
            java.util.Map.entry("soleil_ardent_de_sovann", FormePouvoir.NOVA),
            java.util.Map.entry("souffle_de_braise", FormePouvoir.CONE),
            java.util.Map.entry("tison_ardent", FormePouvoir.PROJECTILE),
            // --- Eau ---
            java.util.Map.entry("barattage_de_la_mer_de_lait", FormePouvoir.NOVA),
            java.util.Map.entry("brume_du_grand_lac", FormePouvoir.ZONE),
            java.util.Map.entry("carapace_du_crabe", FormePouvoir.BOUCLIER),
            java.util.Map.entry("courant_du_mekong", FormePouvoir.PROJECTILE),
            java.util.Map.entry("crue_de_mousson", FormePouvoir.CONE),
            java.util.Map.entry("fureur_du_naga_a_sept_tetes", FormePouvoir.CONE),
            java.util.Map.entry("goutte_apaisante", FormePouvoir.SOIN),
            java.util.Map.entry("onde_du_tonle", FormePouvoir.NOVA),
            java.util.Map.entry("pas_de_brume", FormePouvoir.BLINK),
            java.util.Map.entry("sanctuaire_des_eaux_eternelles", FormePouvoir.REGENERATION),
            java.util.Map.entry("source_du_temple_englouti", FormePouvoir.SOIN),
            java.util.Map.entry("tourbillon_du_naga", FormePouvoir.ZONE),
            // --- Terre ---
            java.util.Map.entry("ancrage_des_digues", FormePouvoir.ENTRAVE),
            java.util.Map.entry("bastion_d_angkor", FormePouvoir.BOUCLIER),
            java.util.Map.entry("citadelle_vivante_de_gres", FormePouvoir.BOUCLIER),
            java.util.Map.entry("eclat_de_laterite", FormePouvoir.PROJECTILE),
            java.util.Map.entry("eveil_du_temple_millenaire", FormePouvoir.BOUCLIER),
            java.util.Map.entry("jugement_des_quatre_visages", FormePouvoir.NOVA),
            java.util.Map.entry("peau_d_argile", FormePouvoir.BOUCLIER),
            java.util.Map.entry("pilier_de_gres", FormePouvoir.PROJECTILE),
            java.util.Map.entry("poing_du_colosse_de_pierre", FormePouvoir.MELEE),
            java.util.Map.entry("racine_tenace", FormePouvoir.ENTRAVE),
            java.util.Map.entry("rempart_de_laterite", FormePouvoir.BOUCLIER),
            java.util.Map.entry("secousse_des_terrasses", FormePouvoir.NOVA),
            // --- Lumière ---
            java.util.Map.entry("aube_sacree_du_cambodge", FormePouvoir.SOIN),
            java.util.Map.entry("benediction_du_moine", FormePouvoir.SOIN),
            java.util.Map.entry("choeur_des_apsaras", FormePouvoir.REGENERATION),
            java.util.Map.entry("couronne_de_lumiere_d_angkor", FormePouvoir.BOUCLIER),
            java.util.Map.entry("etincelle_du_wat", FormePouvoir.PROJECTILE),
            java.util.Map.entry("grace_celeste_de_l_apsara", FormePouvoir.SOIN),
            java.util.Map.entry("halo_de_sagesse", FormePouvoir.REVELATION),
            java.util.Map.entry("jugement_radieux_du_lotus", FormePouvoir.BEAM),
            java.util.Map.entry("lance_solaire_du_sanctuaire", FormePouvoir.PROJECTILE),
            java.util.Map.entry("lueur_d_encens", FormePouvoir.REGENERATION),
            java.util.Map.entry("rayon_du_prasat", FormePouvoir.BEAM),
            java.util.Map.entry("regard_clair", FormePouvoir.REVELATION),
            // --- Vie ---
            java.util.Map.entry("arbre_monde_du_tonle", FormePouvoir.INVOCATION),
            java.util.Map.entry("avatar_du_neak_ta_ancestral", FormePouvoir.INVOCATION),
            java.util.Map.entry("bourgeon_de_riz", FormePouvoir.SOIN),
            java.util.Map.entry("chant_de_la_foret_sacree", FormePouvoir.REGENERATION),
            java.util.Map.entry("esprit_du_banyan", FormePouvoir.INVOCATION),
            java.util.Map.entry("fleur_de_lotus_rose", FormePouvoir.SOIN),
            java.util.Map.entry("gardien_de_kapokier", FormePouvoir.INVOCATION),
            java.util.Map.entry("harmonie_du_bol_sacree", FormePouvoir.PASSIF),
            java.util.Map.entry("lucioles_du_marais", FormePouvoir.INVOCATION),
            java.util.Map.entry("seve_vivifiante", FormePouvoir.PASSIF),
            java.util.Map.entry("souffle_primordial_des_rizieres", FormePouvoir.PASSIF)
    );

    /**
     * Forme du pouvoir : sa ligne explicite, sinon une forme cohérente avec son archétype.
     */
    public static FormePouvoir formeDe(PowerDefinition def) {
        FormePouvoir explicite = TABLE.get(def.id());
        return explicite != null ? explicite : parArchetype(def.type());
    }

    /** Repli pour les pouvoirs ajoutés par un datapack (non listés dans la table). */
    public static FormePouvoir parArchetype(PowerType type) {
        if (type == null) {
            return FormePouvoir.PROJECTILE;
        }
        return switch (type) {
            case OFFENSIF -> FormePouvoir.PROJECTILE;
            case DEFENSIF -> FormePouvoir.BOUCLIER;
            case MOBILITE -> FormePouvoir.DASH;
            case SOIN -> FormePouvoir.SOIN;
            case CONTROLE -> FormePouvoir.ENTRAVE;
            case UTILITAIRE -> FormePouvoir.REVELATION;
            case PASSIF -> FormePouvoir.PASSIF;
            case INVOCATION -> FormePouvoir.INVOCATION;
        };
    }

    /**
     * Nombre de pouvoirs couverts par une forme explicite : sert au contrôle de complétude
     * (les 84 pouvoirs livrés doivent tous avoir leur mise en scène écrite à la main).
     */
    public static int couverts() {
        return TABLE.size();
    }

    /** Identifiants sans forme explicite, triés : vide tant que la table est complète. */
    public static List<String> manquants(List<String> idsLivres) {
        return idsLivres.stream()
                .filter(id -> !TABLE.containsKey(id))
                .sorted()
                .toList();
    }

    /** Normalisation identique à celle des identifiants de données. */
    static String normaliser(String brut) {
        return brut == null ? "" : brut.trim().toLowerCase(Locale.ROOT);
    }
}
