package com.cambocraft.registry;

import com.cambocraft.craft.CamboFoods;
import com.cambocraft.craft.CamboWeapons;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * Objets de Cambocraft (hors blocs).
 *
 * <p>Fichier GÉNÉRÉ par {@code tools/gen_registry.py} à partir de
 * {@code assets_src/registry/items.yml} : ne pas modifier à la main, régénérer.</p>
 */
public final class CamboItems {
    private CamboItems() {}
    public static final Item PIECE_DU_LOTUS = Registrar.item("piece_du_lotus", new Item.Settings().maxCount(64));
    public static final Item GRAINE_DE_RIZ = Registrar.item("graine_de_riz", new Item.Settings());
    public static final Item RIZ_BRUT = Registrar.item("riz_brut", new Item.Settings());
    public static final Item RIZ_CUIT = Registrar.item("riz_cuit", new Item.Settings().food(CamboFoods.RICE));
    public static final Item BOL_DE_RIZ = Registrar.item("bol_de_riz", new Item.Settings().maxCount(16));
    public static final Item BOL_DE_RIZ_BENI = Registrar.item("bol_de_riz_beni", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item CARNET_DU_NEN = Registrar.item("carnet_du_nen", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item PIERRE_DU_LOTUS_BRUTE = Registrar.item("pierre_du_lotus_brute", new Item.Settings());
    public static final Item LOTUS_BRUT = Registrar.item("lotus_brut", new Item.Settings());
    public static final Item FIL_DE_SOIE = Registrar.item("fil_de_soie", new Item.Settings());
    public static final Item COCON_DE_SOIE = Registrar.item("cocon_de_soie", new Item.Settings());
    public static final Item TISSU_DE_SOIE = Registrar.item("tissu_de_soie", new Item.Settings());
    public static final Item BAMBOU_BRUT = Registrar.item("bambou_brut", new Item.Settings());
    public static final Item RESINE = Registrar.item("resine", new Item.Settings());
    public static final Item CHARBON_DE_BOIS = Registrar.item("charbon_de_bois", new Item.Settings());
    public static final Item ARGILE_BRUTE = Registrar.item("argile_brute", new Item.Settings());
    public static final Item BRIQUE_D_ARGILE = Registrar.item("brique_d_argile", new Item.Settings());
    public static final Item POUDRE_DE_LOTUS = Registrar.item("poudre_de_lotus", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item AMOK = Registrar.item("amok", new Item.Settings().maxCount(16).food(CamboFoods.AMOK));
    public static final Item NOM_BANH_CHOK = Registrar.item("nom_banh_chok", new Item.Settings().maxCount(16).food(CamboFoods.NOM_BANH_CHOK));
    public static final Item BAI_SACH_CHROUK = Registrar.item("bai_sach_chrouk", new Item.Settings().maxCount(16).food(CamboFoods.BAI_SACH_CHROUK));
    public static final Item NUM_PANG = Registrar.item("num_pang", new Item.Settings().maxCount(16).food(CamboFoods.NUM_PANG));
    public static final Item RIZ_GLUANT = Registrar.item("riz_gluant", new Item.Settings().maxCount(16).food(CamboFoods.STICKY_RICE));
    public static final Item DESSERT_AU_COCO = Registrar.item("dessert_au_coco", new Item.Settings().maxCount(16).food(CamboFoods.COCONUT_DESSERT));
    public static final Item THE_GLACE = Registrar.item("the_glace", new Item.Settings().maxCount(16).food(CamboFoods.ICED_TEA));
    public static final Item POISSON_GRILLE = Registrar.item("poisson_grille", new Item.Settings().maxCount(16).food(CamboFoods.GRILLED_FISH));
    public static final Item NOUILLES_DE_RIZ = Registrar.item("nouilles_de_riz", new Item.Settings().maxCount(16));
    public static final Item LAIT_DE_COCO = Registrar.item("lait_de_coco", new Item.Settings().maxCount(16));
    public static final Item CITRONNELLE_COUPEE = Registrar.item("citronnelle_coupee", new Item.Settings());
    public static final Item PIMENT_ROUGE = Registrar.item("piment_rouge", new Item.Settings());
    public static final Item CREVETTE = Registrar.item("crevette", new Item.Settings().food(CamboFoods.SHRIMP));
    public static final Item LINGOT_DE_LOTUS = Registrar.item("lingot_de_lotus", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item EPEE_KHMER = Registrar.item("epee_khmer", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(6f, -2.2f)));
    public static final Item PIQUE_EN_BAMBOU = Registrar.item("pique_en_bambou", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(6f, -2.2f)));
    public static final Item FLECHE_EN_BAMBOU = Registrar.item("fleche_en_bambou", new Item.Settings().maxCount(64));
    public static final Item BOUCLIER_DE_ROTIN = Registrar.item("bouclier_de_rotin", new Item.Settings().maxCount(1));
    public static final Item BANDAGE_DE_SOIE = Registrar.item("bandage_de_soie", s -> new com.cambocraft.craft.CamboHealingItem(s, 4.0F, 200), new Item.Settings().maxCount(16));
    public static final Item ONGUENT_DE_LOTUS = Registrar.item("onguent_de_lotus", s -> new com.cambocraft.craft.CamboHealingItem(s, 8.0F, 400), new Item.Settings().maxCount(8).rarity(Rarity.UNCOMMON));
    public static final Item ENCENS_BENI = Registrar.item("encens_beni", new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));
    public static final Item MAILLET_DE_SCULPTEUR = Registrar.item("maillet_de_sculpteur", new Item.Settings().maxCount(1));
    public static final Item CISEAU_DE_SCULPTEUR = Registrar.item("ciseau_de_sculpteur", new Item.Settings().maxCount(1));
    public static final Item CANNE_A_PECHER = Registrar.item("canne_a_pecher", com.cambocraft.fishing.CamboFishingRodItem::new, new Item.Settings().maxCount(1));
    public static final Item FILET_DE_PECHE = Registrar.item("filet_de_peche", new Item.Settings().maxCount(1));
    public static final Item HOUE_DE_RIZ = Registrar.item("houe_de_riz", new Item.Settings().maxCount(1));
    public static final Item FAUCILLE = Registrar.item("faucille", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(6f, -2.2f)));
    public static final Item POISSON_CHAT = Registrar.item("poisson_chat", new Item.Settings().food(CamboFoods.RAW_FISH));
    public static final Item POISSON_TILAPIA = Registrar.item("poisson_tilapia", new Item.Settings().food(CamboFoods.RAW_FISH));
    public static final Item POISSON_SERPENT = Registrar.item("poisson_serpent", new Item.Settings().food(CamboFoods.RAW_FISH));
    public static final Item CARPE_GEANTE = Registrar.item("carpe_geante", new Item.Settings().rarity(Rarity.UNCOMMON).food(CamboFoods.RAW_FISH));
    public static final Item POISSON_COMBATTANT = Registrar.item("poisson_combattant", new Item.Settings());
    public static final Item CREVETTE_DE_RIVIERE = Registrar.item("crevette_de_riviere", new Item.Settings().food(CamboFoods.SHRIMP));
    public static final Item CRABE_DE_RIZIAIRE = Registrar.item("crabe_de_riziaire", new Item.Settings().food(CamboFoods.SHRIMP));
    public static final Item ANGUILLE = Registrar.item("anguille", new Item.Settings().food(CamboFoods.RAW_FISH));
    public static final Item POISSON_LUNE = Registrar.item("poisson_lune", new Item.Settings().rarity(Rarity.RARE));
    public static final Item RAIE_D_EAU_DOUCE = Registrar.item("raie_d_eau_douce", new Item.Settings().rarity(Rarity.RARE));
    public static final Item CROCODILE_DORE = Registrar.item("crocodile_dore", new Item.Settings().rarity(Rarity.EPIC));
    public static final Item POISSON_DE_LOTUS = Registrar.item("poisson_de_lotus", new Item.Settings().rarity(Rarity.RARE).food(CamboFoods.RAW_FISH));
    public static final Item SILURE_GEANT = Registrar.item("silure_geant", new Item.Settings().rarity(Rarity.RARE).food(CamboFoods.RAW_FISH));
    public static final Item POISSON_D_ARGENT = Registrar.item("poisson_d_argent", new Item.Settings().food(CamboFoods.RAW_FISH));
    public static final Item POISSON_ARC_EN_CIEL = Registrar.item("poisson_arc_en_ciel", new Item.Settings().food(CamboFoods.RAW_FISH));
    public static final Item GRENOUILLE_DE_RIZIAIRE = Registrar.item("grenouille_de_riziaire", new Item.Settings().food(CamboFoods.SHRIMP));
    public static final Item GRAINE_DE_PIMENT = Registrar.item("graine_de_piment", new Item.Settings());
    public static final Item GRAINE_D_AUBERGINE = Registrar.item("graine_d_aubergine", new Item.Settings());
    public static final Item GRAINE_DE_CONCOMBRE = Registrar.item("graine_de_concombre", new Item.Settings());
    public static final Item BOUTURE_DE_CITRONNELLE = Registrar.item("bouture_de_citronnelle", new Item.Settings());
    public static final Item POUSSE_DE_TECK = Registrar.item("pousse_de_teck", new Item.Settings());
    public static final Item POUSSE_DE_KAPOKIER = Registrar.item("pousse_de_kapokier", new Item.Settings());
    public static final Item POUSSE_DE_COCOTIER = Registrar.item("pousse_de_cocotier", new Item.Settings());
    public static final Item POUSSE_DE_MANGUIER = Registrar.item("pousse_de_manguier", new Item.Settings());
    public static final Item POUSSE_DE_JACQUIER = Registrar.item("pousse_de_jacquier", new Item.Settings());
    public static final Item RHIZOME_DE_LOTUS = Registrar.item("rhizome_de_lotus", new Item.Settings());
    public static final Item ECAILLE_DE_NAGA = Registrar.item("ecaille_de_naga", new Item.Settings().rarity(Rarity.RARE));
    public static final Item PLUME_DE_GARUDA = Registrar.item("plume_de_garuda", new Item.Settings().rarity(Rarity.RARE));
    public static final Item COEUR_DU_LOTUS = Registrar.item("coeur_du_lotus", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item ESSENCE_D_OMBRE = Registrar.item("essence_d_ombre", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item CLE_DE_PRASAT = Registrar.item("cle_de_prasat", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item CARTE_DE_LA_RENAISSANCE = Registrar.item("carte_de_la_renaissance", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item PARCHEMIN_DE_QUETE = Registrar.item("parchemin_de_quete", new Item.Settings().maxCount(1));
    public static final Item JOURNAL_DE_VOYAGE = Registrar.item("journal_de_voyage", new Item.Settings().maxCount(1));
    public static final Item OFFRANDE_DE_FLEURS = Registrar.item("offrande_de_fleurs", new Item.Settings().maxCount(16));
    public static final Item BOL_D_OFFRANDE = Registrar.item("bol_d_offrande", new Item.Settings().maxCount(1));
    public static final Item MEDAILLON_DE_FACTION = Registrar.item("medaillon_de_faction", new Item.Settings().maxCount(1));
    public static final Item PIERRE_DE_TELEPORTATION = Registrar.item("pierre_de_teleportation", new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));
    public static final Item SCEAU_D_OMBRE = Registrar.item("sceau_d_ombre", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item LANTERNE_DE_BRUME = Registrar.item("lanterne_de_brume", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item TAMBOUR_DE_GUERRE = Registrar.item("tambour_de_guerre", new Item.Settings().maxCount(1));
    public static final Item MASQUE_DE_PRADAL = Registrar.item("masque_de_pradal", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item PIECE_DU_LOTUS_ARGENT = Registrar.item("piece_du_lotus_argent", new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON));
    public static final Item PIECE_DU_LOTUS_OR = Registrar.item("piece_du_lotus_or", new Item.Settings().maxCount(64).rarity(Rarity.RARE));
    public static final Item BOURSE_DE_SOIE = Registrar.item("bourse_de_soie", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item LINGOT_D_OR_KHMER = Registrar.item("lingot_d_or_khmer", new Item.Settings().rarity(Rarity.RARE));
    public static final Item LOK_LAK = Registrar.item("lok_lak", new Item.Settings().maxCount(16).food(CamboFoods.AMOK));
    public static final Item KUY_TEAV = Registrar.item("kuy_teav", new Item.Settings().maxCount(16).food(CamboFoods.NOM_BANH_CHOK));
    public static final Item SAMLOR_KORKO = Registrar.item("samlor_korko", new Item.Settings().maxCount(16).food(CamboFoods.AMOK));
    public static final Item SAMLOR_MACHU = Registrar.item("samlor_machu", new Item.Settings().maxCount(16).food(CamboFoods.NOM_BANH_CHOK));
    public static final Item PRAHOK_KTIS = Registrar.item("prahok_ktis", new Item.Settings().maxCount(16).food(CamboFoods.BAI_SACH_CHROUK));
    public static final Item PLEAH_SACH_KO = Registrar.item("pleah_sach_ko", new Item.Settings().maxCount(16).food(CamboFoods.BAI_SACH_CHROUK));
    public static final Item BANH_CHAO = Registrar.item("banh_chao", new Item.Settings().maxCount(16).food(CamboFoods.NUM_PANG));
    public static final Item KRALAN = Registrar.item("kralan", new Item.Settings().maxCount(16).food(CamboFoods.STICKY_RICE));
    public static final Item NUM_ANSORM = Registrar.item("num_ansorm", new Item.Settings().maxCount(16).food(CamboFoods.STICKY_RICE));
    public static final Item NUM_KOM = Registrar.item("num_kom", new Item.Settings().maxCount(16).food(CamboFoods.COCONUT_DESSERT));
    public static final Item NUM_CHAK_KCHAN = Registrar.item("num_chak_kchan", new Item.Settings().maxCount(16).food(CamboFoods.COCONUT_DESSERT));
    public static final Item BROCHETTE_DE_POISSON = Registrar.item("brochette_de_poisson", new Item.Settings().maxCount(16).food(CamboFoods.GRILLED_FISH));
    public static final Item BROCHETTE_DE_BOEUF = Registrar.item("brochette_de_boeuf", new Item.Settings().maxCount(16).food(CamboFoods.BAI_SACH_CHROUK));
    public static final Item CRABE_AU_POIVRE_VERT = Registrar.item("crabe_au_poivre_vert", new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).food(CamboFoods.AMOK));
    public static final Item SALADE_DE_PAPAYE = Registrar.item("salade_de_papaye", new Item.Settings().maxCount(16).food(CamboFoods.NOM_BANH_CHOK));
    public static final Item SOUPE_DE_BAMBOU = Registrar.item("soupe_de_bambou", new Item.Settings().maxCount(16).food(CamboFoods.RICE));
    public static final Item JUS_DE_CANNE_A_SUCRE = Registrar.item("jus_de_canne_a_sucre", new Item.Settings().maxCount(16).food(CamboFoods.ICED_TEA));
    public static final Item JUS_DE_COCO_FRAIS = Registrar.item("jus_de_coco_frais", new Item.Settings().maxCount(16).food(CamboFoods.ICED_TEA));
    public static final Item INFUSION_DE_LOTUS = Registrar.item("infusion_de_lotus", new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).food(CamboFoods.ICED_TEA));
    public static final Item INFUSION_DE_CITRONNELLE = Registrar.item("infusion_de_citronnelle", new Item.Settings().maxCount(16).food(CamboFoods.ICED_TEA));
    public static final Item PATE_DE_PRAHOK = Registrar.item("pate_de_prahok", new Item.Settings());
    public static final Item PATE_DE_KROEUNG_JAUNE = Registrar.item("pate_de_kroeung_jaune", new Item.Settings());
    public static final Item PATE_DE_KROEUNG_VERT = Registrar.item("pate_de_kroeung_vert", new Item.Settings());
    public static final Item PATE_DE_KROEUNG_ROUGE = Registrar.item("pate_de_kroeung_rouge", new Item.Settings());
    public static final Item POIVRE_NOIR_DE_KAMPOT = Registrar.item("poivre_noir_de_kampot", new Item.Settings());
    public static final Item POIVRE_ROUGE_DE_KAMPOT = Registrar.item("poivre_rouge_de_kampot", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item POIVRE_VERT_FRAIS = Registrar.item("poivre_vert_frais", new Item.Settings());
    public static final Item SUCRE_DE_PALME = Registrar.item("sucre_de_palme", new Item.Settings());
    public static final Item GALANGA_RACINE = Registrar.item("galanga_racine", new Item.Settings());
    public static final Item CURCUMA_RACINE = Registrar.item("curcuma_racine", new Item.Settings());
    public static final Item COMBAVA_ZESTE = Registrar.item("combava_zeste", new Item.Settings());
    public static final Item TAMARIN_GOUSSE = Registrar.item("tamarin_gousse", new Item.Settings());
    public static final Item AIL_GRILLE = Registrar.item("ail_grille", new Item.Settings());
    public static final Item NOIX_DE_COCO = Registrar.item("noix_de_coco", new Item.Settings().food(CamboFoods.RICE));
    public static final Item MANGUE_MURE = Registrar.item("mangue_mure", new Item.Settings().food(CamboFoods.COCONUT_DESSERT));
    public static final Item MANGUE_VERTE = Registrar.item("mangue_verte", new Item.Settings().food(CamboFoods.RICE));
    public static final Item JACQUE_QUARTIER = Registrar.item("jacque_quartier", new Item.Settings().food(CamboFoods.COCONUT_DESSERT));
    public static final Item BANANE_PLANTAIN = Registrar.item("banane_plantain", new Item.Settings().food(CamboFoods.RICE));
    public static final Item PAPAYE_VERTE = Registrar.item("papaye_verte", new Item.Settings().food(CamboFoods.RICE));
    public static final Item POUSSE_DE_BAMBOU_COMESTIBLE = Registrar.item("pousse_de_bambou_comestible", new Item.Settings().food(CamboFoods.RICE));
    public static final Item FARINE_DE_RIZ = Registrar.item("farine_de_riz", new Item.Settings());
    public static final Item GRAINE_DE_LOTUS = Registrar.item("graine_de_lotus", new Item.Settings().food(CamboFoods.RICE));
    public static final Item FEUILLE_DE_MURIER = Registrar.item("feuille_de_murier", new Item.Settings());
    public static final Item VER_A_SOIE = Registrar.item("ver_a_soie", new Item.Settings());
    public static final Item ECHEVEAU_DE_SOIE_ECRUE = Registrar.item("echeveau_de_soie_ecrue", new Item.Settings());
    public static final Item SOIE_TEINTE_OR = Registrar.item("soie_teinte_or", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item SOIE_TEINTE_POURPRE = Registrar.item("soie_teinte_pourpre", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item SOIE_TEINTE_INDIGO = Registrar.item("soie_teinte_indigo", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item SOIE_TEINTE_EMERAUDE = Registrar.item("soie_teinte_emeraude", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item KRAMA_TRADITIONNEL = Registrar.item("krama_traditionnel", new Item.Settings().maxCount(1));
    public static final Item KRAMA_DE_SOIE_DOREE = Registrar.item("krama_de_soie_doree", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item BLOC_DE_GRES_BRUT = Registrar.item("bloc_de_gres_brut", new Item.Settings());
    public static final Item STATUETTE_DE_NAGA = Registrar.item("statuette_de_naga", new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));
    public static final Item STATUETTE_D_APSARA = Registrar.item("statuette_d_apsara", new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));
    public static final Item DAGUE_KHMER = Registrar.item("dague_khmer", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(4.5f, -1.6f)));
    public static final Item SABRE_DE_GARDE_ROYAL = Registrar.item("sabre_de_garde_royal", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).attributeModifiers(CamboWeapons.attack(7.5f, -2.2f)));
    public static final Item LANCE_DE_PRASAT = Registrar.item("lance_de_prasat", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).attributeModifiers(CamboWeapons.attack(7f, -2.5f)));
    public static final Item HALLEBARDE_DE_GARUDA = Registrar.item("hallebarde_de_garuda", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).attributeModifiers(CamboWeapons.attack(8.5f, -2.7f)));
    public static final Item BATON_DE_MOINE_EN_TECK = Registrar.item("baton_de_moine_en_teck", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(5f, -2f)));
    public static final Item ARC_COMPOSITE_EN_BAMBOU = Registrar.item("arc_composite_en_bambou", new Item.Settings().maxCount(1));
    public static final Item ARBALETE_DE_ROTIN = Registrar.item("arbalete_de_rotin", new Item.Settings().maxCount(1));
    public static final Item KUNAI_D_OMBRE = Registrar.item("kunai_d_ombre", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(16).attributeModifiers(CamboWeapons.attack(5.5f, -1.5f)));
    public static final Item SHURIKEN_D_OMBRE = Registrar.item("shuriken_d_ombre", new Item.Settings().maxCount(64));
    public static final Item LAME_COURBE_D_OMBRE = Registrar.item("lame_courbe_d_ombre", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE).attributeModifiers(CamboWeapons.attack(8f, -2.1f)));
    public static final Item MASSE_DE_LATERITE = Registrar.item("masse_de_laterite", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(9f, -3f)));
    public static final Item EPEE_DU_LOTUS_SACREE = Registrar.item("epee_du_lotus_sacree", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof().attributeModifiers(CamboWeapons.attack(9.5f, -2.2f)));
    public static final Item GANTS_DE_PRADAL_SEREY = Registrar.item("gants_de_pradal_serey", com.cambocraft.craft.CamboWeaponItem::new, new Item.Settings().maxCount(1).attributeModifiers(CamboWeapons.attack(5f, -1.2f)));
    public static final Item PIERRE_D_AFFUTAGE = Registrar.item("pierre_d_affutage", new Item.Settings());
    public static final Item CRISTAL_DE_FORGE_MINEUR = Registrar.item("cristal_de_forge_mineur", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item CRISTAL_DE_FORGE_MAJEUR = Registrar.item("cristal_de_forge_majeur", new Item.Settings().rarity(Rarity.RARE));
    public static final Item CRISTAL_DE_FORGE_PARFAIT = Registrar.item("cristal_de_forge_parfait", new Item.Settings().rarity(Rarity.EPIC));
    public static final Item LINGOT_D_ACIER_TREMPE = Registrar.item("lingot_d_acier_trempe", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item PIOCHE_DE_LOTUS = Registrar.item("pioche_de_lotus", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item HACHE_DE_TECK = Registrar.item("hache_de_teck", new Item.Settings().maxCount(1));
    public static final Item PELLE_DE_RIZIAIRE = Registrar.item("pelle_de_riziaire", new Item.Settings().maxCount(1));
    public static final Item CISEAUX_A_SOIE = Registrar.item("ciseaux_a_soie", new Item.Settings().maxCount(1));
    public static final Item CANNE_A_PECHER_EN_BAMBOU_RENFORCE = Registrar.item("canne_a_pecher_en_bambou_renforce", com.cambocraft.fishing.CamboFishingRodItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item CANNE_A_PECHER_DU_MAITRE = Registrar.item("canne_a_pecher_du_maitre", com.cambocraft.fishing.CamboFishingRodItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item APPAT_DE_RIZ_FERMENTE = Registrar.item("appat_de_riz_fermente", new Item.Settings());
    public static final Item APPAT_DE_CREVETTE_DOREE = Registrar.item("appat_de_crevette_doree", new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item CASQUE_DE_ROTIN = Registrar.item("casque_de_rotin", new Item.Settings().maxCount(1));
    public static final Item PLASTRON_DE_ROTIN = Registrar.item("plastron_de_rotin", new Item.Settings().maxCount(1));
    public static final Item JAMBIERES_DE_ROTIN = Registrar.item("jambieres_de_rotin", new Item.Settings().maxCount(1));
    public static final Item BOTTES_DE_ROTIN = Registrar.item("bottes_de_rotin", new Item.Settings().maxCount(1));
    public static final Item COURONNE_DE_SOIE_ET_LOTUS = Registrar.item("couronne_de_soie_et_lotus", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item TUNIQUE_DE_SOIE_ET_LOTUS = Registrar.item("tunique_de_soie_et_lotus", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item SAMPOT_DE_SOIE_ET_LOTUS = Registrar.item("sampot_de_soie_et_lotus", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item SANDALES_DE_SOIE_ET_LOTUS = Registrar.item("sandales_de_soie_et_lotus", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item HEAUME_DE_PRASAT = Registrar.item("heaume_de_prasat", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item CUIRASSE_DE_PRASAT = Registrar.item("cuirasse_de_prasat", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item GREVES_DE_PRASAT = Registrar.item("greves_de_prasat", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item SOLERETS_DE_PRASAT = Registrar.item("solerets_de_prasat", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item INSIGNE_DU_CHEF_BANDIT = Registrar.item("insigne_du_chef_bandit", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item CORNE_DU_BUFFLE_D_OR = Registrar.item("corne_du_buffle_d_or", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item FOURREAU_DU_MAITRE_DES_LAMES = Registrar.item("fourreau_du_maitre_des_lames", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item CROC_DU_CROCODILE_DES_PROFONDEURS = Registrar.item("croc_du_crocodile_des_profondeurs", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item ECAILLE_DU_SERPENT_SYLVESTRE = Registrar.item("ecaille_du_serpent_sylvestre", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item COEUR_DU_GARDIEN_DE_PIERRE = Registrar.item("coeur_du_gardien_de_pierre", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item MASQUE_DU_GENERALISSIME = Registrar.item("masque_du_generalissime", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item PLUME_ARDENTE_DU_PHENIX = Registrar.item("plume_ardente_du_phenix", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item FRAGMENT_DU_VISAGE_EVEILLE = Registrar.item("fragment_du_visage_eveille", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item SERRE_DU_GARUDA_DE_TEMPETE = Registrar.item("serre_du_garuda_de_tempete", new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item SCEAU_DU_GARDIEN_DE_L_EQUILIBRE = Registrar.item("sceau_du_gardien_de_l_equilibre", new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item CRISTAL_DE_L_OMBRE_PREMIERE = Registrar.item("cristal_de_l_ombre_premiere", new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item BOL_DU_MAITRE_DU_NEN = Registrar.item("bol_du_maitre_du_nen", new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
    public static final Item PERLE_D_ESPRIT_NEAK_TA = Registrar.item("perle_d_esprit_neak_ta", new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    public static final Item CORNE_D_APPEL_DU_BUFFLE = Registrar.item("corne_d_appel_du_buffle", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item PAGAIE_DE_COURSE = Registrar.item("pagaie_de_course", new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    /** Tous les objets du catalogue, pratique pour la vérification et les commandes. */
    public static final java.util.List<Item> ALL = java.util.List.of(
            PIECE_DU_LOTUS,
            GRAINE_DE_RIZ,
            RIZ_BRUT,
            RIZ_CUIT,
            BOL_DE_RIZ,
            BOL_DE_RIZ_BENI,
            CARNET_DU_NEN,
            PIERRE_DU_LOTUS_BRUTE,
            LOTUS_BRUT,
            FIL_DE_SOIE,
            COCON_DE_SOIE,
            TISSU_DE_SOIE,
            BAMBOU_BRUT,
            RESINE,
            CHARBON_DE_BOIS,
            ARGILE_BRUTE,
            BRIQUE_D_ARGILE,
            POUDRE_DE_LOTUS,
            AMOK,
            NOM_BANH_CHOK,
            BAI_SACH_CHROUK,
            NUM_PANG,
            RIZ_GLUANT,
            DESSERT_AU_COCO,
            THE_GLACE,
            POISSON_GRILLE,
            NOUILLES_DE_RIZ,
            LAIT_DE_COCO,
            CITRONNELLE_COUPEE,
            PIMENT_ROUGE,
            CREVETTE,
            LINGOT_DE_LOTUS,
            EPEE_KHMER,
            PIQUE_EN_BAMBOU,
            FLECHE_EN_BAMBOU,
            BOUCLIER_DE_ROTIN,
            BANDAGE_DE_SOIE,
            ONGUENT_DE_LOTUS,
            ENCENS_BENI,
            MAILLET_DE_SCULPTEUR,
            CISEAU_DE_SCULPTEUR,
            CANNE_A_PECHER,
            FILET_DE_PECHE,
            HOUE_DE_RIZ,
            FAUCILLE,
            POISSON_CHAT,
            POISSON_TILAPIA,
            POISSON_SERPENT,
            CARPE_GEANTE,
            POISSON_COMBATTANT,
            CREVETTE_DE_RIVIERE,
            CRABE_DE_RIZIAIRE,
            ANGUILLE,
            POISSON_LUNE,
            RAIE_D_EAU_DOUCE,
            CROCODILE_DORE,
            POISSON_DE_LOTUS,
            SILURE_GEANT,
            POISSON_D_ARGENT,
            POISSON_ARC_EN_CIEL,
            GRENOUILLE_DE_RIZIAIRE,
            GRAINE_DE_PIMENT,
            GRAINE_D_AUBERGINE,
            GRAINE_DE_CONCOMBRE,
            BOUTURE_DE_CITRONNELLE,
            POUSSE_DE_TECK,
            POUSSE_DE_KAPOKIER,
            POUSSE_DE_COCOTIER,
            POUSSE_DE_MANGUIER,
            POUSSE_DE_JACQUIER,
            RHIZOME_DE_LOTUS,
            ECAILLE_DE_NAGA,
            PLUME_DE_GARUDA,
            COEUR_DU_LOTUS,
            ESSENCE_D_OMBRE,
            CLE_DE_PRASAT,
            CARTE_DE_LA_RENAISSANCE,
            PARCHEMIN_DE_QUETE,
            JOURNAL_DE_VOYAGE,
            OFFRANDE_DE_FLEURS,
            BOL_D_OFFRANDE,
            MEDAILLON_DE_FACTION,
            PIERRE_DE_TELEPORTATION,
            SCEAU_D_OMBRE,
            LANTERNE_DE_BRUME,
            TAMBOUR_DE_GUERRE,
            MASQUE_DE_PRADAL,
            PIECE_DU_LOTUS_ARGENT,
            PIECE_DU_LOTUS_OR,
            BOURSE_DE_SOIE,
            LINGOT_D_OR_KHMER,
            LOK_LAK,
            KUY_TEAV,
            SAMLOR_KORKO,
            SAMLOR_MACHU,
            PRAHOK_KTIS,
            PLEAH_SACH_KO,
            BANH_CHAO,
            KRALAN,
            NUM_ANSORM,
            NUM_KOM,
            NUM_CHAK_KCHAN,
            BROCHETTE_DE_POISSON,
            BROCHETTE_DE_BOEUF,
            CRABE_AU_POIVRE_VERT,
            SALADE_DE_PAPAYE,
            SOUPE_DE_BAMBOU,
            JUS_DE_CANNE_A_SUCRE,
            JUS_DE_COCO_FRAIS,
            INFUSION_DE_LOTUS,
            INFUSION_DE_CITRONNELLE,
            PATE_DE_PRAHOK,
            PATE_DE_KROEUNG_JAUNE,
            PATE_DE_KROEUNG_VERT,
            PATE_DE_KROEUNG_ROUGE,
            POIVRE_NOIR_DE_KAMPOT,
            POIVRE_ROUGE_DE_KAMPOT,
            POIVRE_VERT_FRAIS,
            SUCRE_DE_PALME,
            GALANGA_RACINE,
            CURCUMA_RACINE,
            COMBAVA_ZESTE,
            TAMARIN_GOUSSE,
            AIL_GRILLE,
            NOIX_DE_COCO,
            MANGUE_MURE,
            MANGUE_VERTE,
            JACQUE_QUARTIER,
            BANANE_PLANTAIN,
            PAPAYE_VERTE,
            POUSSE_DE_BAMBOU_COMESTIBLE,
            FARINE_DE_RIZ,
            GRAINE_DE_LOTUS,
            FEUILLE_DE_MURIER,
            VER_A_SOIE,
            ECHEVEAU_DE_SOIE_ECRUE,
            SOIE_TEINTE_OR,
            SOIE_TEINTE_POURPRE,
            SOIE_TEINTE_INDIGO,
            SOIE_TEINTE_EMERAUDE,
            KRAMA_TRADITIONNEL,
            KRAMA_DE_SOIE_DOREE,
            BLOC_DE_GRES_BRUT,
            STATUETTE_DE_NAGA,
            STATUETTE_D_APSARA,
            DAGUE_KHMER,
            SABRE_DE_GARDE_ROYAL,
            LANCE_DE_PRASAT,
            HALLEBARDE_DE_GARUDA,
            BATON_DE_MOINE_EN_TECK,
            ARC_COMPOSITE_EN_BAMBOU,
            ARBALETE_DE_ROTIN,
            KUNAI_D_OMBRE,
            SHURIKEN_D_OMBRE,
            LAME_COURBE_D_OMBRE,
            MASSE_DE_LATERITE,
            EPEE_DU_LOTUS_SACREE,
            GANTS_DE_PRADAL_SEREY,
            PIERRE_D_AFFUTAGE,
            CRISTAL_DE_FORGE_MINEUR,
            CRISTAL_DE_FORGE_MAJEUR,
            CRISTAL_DE_FORGE_PARFAIT,
            LINGOT_D_ACIER_TREMPE,
            PIOCHE_DE_LOTUS,
            HACHE_DE_TECK,
            PELLE_DE_RIZIAIRE,
            CISEAUX_A_SOIE,
            CANNE_A_PECHER_EN_BAMBOU_RENFORCE,
            CANNE_A_PECHER_DU_MAITRE,
            APPAT_DE_RIZ_FERMENTE,
            APPAT_DE_CREVETTE_DOREE,
            CASQUE_DE_ROTIN,
            PLASTRON_DE_ROTIN,
            JAMBIERES_DE_ROTIN,
            BOTTES_DE_ROTIN,
            COURONNE_DE_SOIE_ET_LOTUS,
            TUNIQUE_DE_SOIE_ET_LOTUS,
            SAMPOT_DE_SOIE_ET_LOTUS,
            SANDALES_DE_SOIE_ET_LOTUS,
            HEAUME_DE_PRASAT,
            CUIRASSE_DE_PRASAT,
            GREVES_DE_PRASAT,
            SOLERETS_DE_PRASAT,
            INSIGNE_DU_CHEF_BANDIT,
            CORNE_DU_BUFFLE_D_OR,
            FOURREAU_DU_MAITRE_DES_LAMES,
            CROC_DU_CROCODILE_DES_PROFONDEURS,
            ECAILLE_DU_SERPENT_SYLVESTRE,
            COEUR_DU_GARDIEN_DE_PIERRE,
            MASQUE_DU_GENERALISSIME,
            PLUME_ARDENTE_DU_PHENIX,
            FRAGMENT_DU_VISAGE_EVEILLE,
            SERRE_DU_GARUDA_DE_TEMPETE,
            SCEAU_DU_GARDIEN_DE_L_EQUILIBRE,
            CRISTAL_DE_L_OMBRE_PREMIERE,
            BOL_DU_MAITRE_DU_NEN,
            PERLE_D_ESPRIT_NEAK_TA,
            CORNE_D_APPEL_DU_BUFFLE,
            PAGAIE_DE_COURSE
    );
}
