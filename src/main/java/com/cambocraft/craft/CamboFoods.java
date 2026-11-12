package com.cambocraft.craft;

import net.minecraft.component.type.FoodComponent;
import com.cambocraft.util.Effects;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * Valeurs nutritives des plats khmers.
 *
 * <p>Chaque plat a été calibré sur la nourriture vanilla : le pain (5 demi-cuillères, 1,2 de
 * saturation) sert d'étalon. Les plats de Cambocraft donnent un peu moins de saturation brute que
 * le pain pour leur coût, mais apportent un effet temporaire : la cuisine reste un choix
 * tactique, pas une simple source de nourriture. Le détail des recettes et des durées est dans
 * {@code docs/RECETTES.md}, les calculs de valeur dans {@code docs/PROBABILITES.md}.</p>
 */
public final class CamboFoods {
    private CamboFoods() {}

    /** Riz nature : équivalent du pain, sans effet. */
    public static final FoodComponent RICE = new FoodComponent.Builder()
            .nutrition(4).saturationModifier(0.8f).build();

    /** Amok : régénération — le plat des jours de fête. */
    public static final FoodComponent AMOK = new FoodComponent.Builder()
            .nutrition(8).saturationModifier(1.1f)
            .statusEffect(new StatusEffectInstance(Effects.vanilla("regeneration"), 200, 0), 1.0f)
            .build();

    /** Nom Banh Chok : vitesse et énergie (effet « énergie » = saturation rapide). */
    public static final FoodComponent NOM_BANH_CHOK = new FoodComponent.Builder()
            .nutrition(7).saturationModifier(0.9f)
            .statusEffect(new StatusEffectInstance(Effects.vanilla("speed"), 600, 0), 1.0f)
            .build();

    /** Bai Sach Chrouk : force. */
    public static final FoodComponent BAI_SACH_CHROUK = new FoodComponent.Builder()
            .nutrition(9).saturationModifier(1.0f)
            .statusEffect(new StatusEffectInstance(Effects.vanilla("strength"), 300, 0), 1.0f)
            .build();

    /** Num Pang : en-cas rapide, avalé deux fois plus vite. */
    public static final FoodComponent NUM_PANG = new FoodComponent.Builder()
            .nutrition(3).saturationModifier(0.6f).snack().build();

    /** Riz gluant : nourriture de route, longue conservation. */
    public static final FoodComponent STICKY_RICE = new FoodComponent.Builder()
            .nutrition(5).saturationModifier(0.9f).build();

    /** Dessert au coco : réserve de saturation élevée. */
    public static final FoodComponent COCONUT_DESSERT = new FoodComponent.Builder()
            .nutrition(4).saturationModifier(1.4f)
            .statusEffect(new StatusEffectInstance(Effects.vanilla("saturation"), 100, 0), 0.6f)
            .build();

    /** Thé glacé : touche de hâte, agréable en exploration. */
    public static final FoodComponent ICED_TEA = new FoodComponent.Builder()
            .nutrition(1).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(Effects.vanilla("haste"), 900, 0), 1.0f)
            .build();

    /** Poisson grillé : valeur sûre des pêcheurs. */
    public static final FoodComponent GRILLED_FISH = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.9f).build();

    /** Poisson cru : nourrit peu et peut rendre malade, comme dans l'eau douce réelle. */
    public static final FoodComponent RAW_FISH = new FoodComponent.Builder()
            .nutrition(2).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(Effects.vanilla("nausea"), 100, 0), 0.25f)
            .build();

    /** Crevettes et crabes : petits mais nourrissants. */
    public static final FoodComponent SHRIMP = new FoodComponent.Builder()
            .nutrition(2).saturationModifier(0.4f).build();

    /** Bol de riz : base de tous les repas khmers. */
    public static final FoodComponent BOWL_OF_RICE = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(1.0f).build();
}
