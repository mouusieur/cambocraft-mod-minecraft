package com.cambocraft.combat;

import com.cambocraft.util.Localized;
import java.util.List;

/**
 * Faune cambodgienne authentique présente dans {@code cambocraft:cambodia}.
 *
 * <p>Toutes les espèces endémiques et menacées sont modélisées de manière respectueuse :
 * leur observation, leur protection ou une offrande de nourriture leur est préférée à la chasse.
 */
public enum WildlifeSpecies {
    BUFFLE_D_EAU(
            "buffle_d_eau",
            new Localized("Buffle d'eau", "Water Buffalo"),
            Behavior.MONTURE_PAISIBLE,
            List.of("rizieres_en_terrasses", "plaine_inondable"),
            40.0,
            true
    ),
    ELEPHANT_D_ASIE(
            "elephant_d_asie",
            new Localized("Éléphant d'Asie", "Asian Elephant"),
            Behavior.DEFENSIF_MAJESTUEUX,
            List.of("foret_tropicale", "foret_de_kapokiers", "plateau_de_laterite"),
            120.0,
            false
    ),
    GIBBON_A_MAINS_BLANCHES(
            "gibbon_a_mains_blanches",
            new Localized("Gibbon à mains blanches", "Lar Gibbon"),
            Behavior.ACROBATE_PACIFIQUE,
            List.of("foret_tropicale", "jungle_en_ruines", "foret_de_kapokiers"),
            18.0,
            false
    ),
    IBIS_GEANT(
            "ibis_geant",
            new Localized("Ibis géant (oiseau national)", "Giant Ibis"),
            Behavior.PACIFIQUE_PROTEGE,
            List.of("mangrove_et_marais", "plaine_inondable", "savane_claire"),
            16.0,
            false
    ),
    DAUPHIN_DE_L_IRRAWADDY(
            "dauphin_de_l_irrawaddy",
            new Localized("Dauphin de l'Irrawaddy", "Irrawaddy Dolphin"),
            Behavior.AQUATIQUE_BIENVEILLANT,
            List.of("riviere_du_tonle", "grand_lac"),
            34.0,
            false
    ),
    PAON_VERT(
            "paon_vert",
            new Localized("Paon spicifère", "Green Peafowl"),
            Behavior.PACIFIQUE_PROTEGE,
            List.of("savane_claire", "bambouseraie", "sanctuaire_de_lotus"),
            14.0,
            false
    ),
    MACAQUE_CRABIER(
            "macaque_crabier",
            new Localized("Macaque crabier", "Long-tailed Macaque"),
            Behavior.CURIEUX_CHAPARDEUR,
            List.of("jungle_en_ruines", "mangrove_et_marais"),
            16.0,
            false
    ),
    CERF_D_ELD(
            "cerf_d_eld",
            new Localized("Cerf d'Eld", "Eld's Deer"),
            Behavior.FUYARD_VIGILANT,
            List.of("savane_claire", "pinedes_seches"),
            24.0,
            false
    ),
    TORTUE_ROYALE(
            "tortue_royale",
            new Localized("Tortue royale du Cambodge", "Southern River Terrapin"),
            Behavior.AQUATIQUE_BIENVEILLANT,
            List.of("riviere_du_tonle", "mangrove_et_marais", "sanctuaire_de_lotus"),
            28.0,
            false
    ),
    VARAN_D_EAU(
            "varan_d_eau",
            new Localized("Varan malais", "Water Monitor"),
            Behavior.DEFENSIF_MAJESTUEUX,
            List.of("mangrove_et_marais", "riviere_du_tonle"),
            22.0,
            false
    ),
    MARTIN_PECHEUR_DU_TONLE(
            "martin_pecheur_du_tonle",
            new Localized("Martin-pêcheur du Tonlé", "Tonle Kingfisher"),
            Behavior.ACROBATE_PACIFIQUE,
            List.of("riviere_du_tonle", "grand_lac"),
            10.0,
            false
    ),
    GRUE_ANTIGONE(
            "grue_antigone",
            new Localized("Grue antigone", "Sarus Crane"),
            Behavior.PACIFIQUE_PROTEGE,
            List.of("plaine_inondable", "rizieres_en_terrasses", "sanctuaire_de_lotus"),
            18.0,
            false
    );

    public enum Behavior {
        MONTURE_PAISIBLE,
        DEFENSIF_MAJESTUEUX,
        ACROBATE_PACIFIQUE,
        PACIFIQUE_PROTEGE,
        AQUATIQUE_BIENVEILLANT,
        CURIEUX_CHAPARDEUR,
        FUYARD_VIGILANT
    }

    private final String id;
    private final Localized nom;
    private final Behavior comportement;
    private final List<String> biomes;
    private final double pvBase;
    private final boolean montureApprivoisable;

    WildlifeSpecies(
            String id,
            Localized nom,
            Behavior comportement,
            List<String> biomes,
            double pvBase,
            boolean montureApprivoisable
    ) {
        this.id = id;
        this.nom = nom;
        this.comportement = comportement;
        this.biomes = List.copyOf(biomes);
        this.pvBase = pvBase;
        this.montureApprivoisable = montureApprivoisable;
    }

    public String id() { return id; }
    public Localized nom() { return nom; }
    public Behavior comportement() { return comportement; }
    public List<String> biomes() { return biomes; }
    public double pvBase() { return pvBase; }
    public boolean montureApprivoisable() { return montureApprivoisable; }
}
