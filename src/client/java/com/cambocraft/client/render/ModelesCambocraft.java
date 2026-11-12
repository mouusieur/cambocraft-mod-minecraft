package com.cambocraft.client.render;

import com.cambocraft.util.Ids;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.util.Identifier;

/**
 * Modèles 3D construits à la main pour les entités du mod.
 *
 * <p>Le mod utilise jusqu'ici les modèles villageois et zombie de Minecraft, ce qui rendait ses
 * PNJ interchangeables avec les villageois vanilla. Les deux modèles ci-dessous sont décrits
 * cubique par cubique, dans l'esprit de l'artisanat khmer :</p>
 * <ul>
 *   <li><b>artisan</b> — silhouette trapue au sarong asymétrique, épaules larges, chapeau conique
 *       (<i>sbai</i>) et ombrelle de soie tenue à la main ;</li>
 *   <li><b>gardien</b> — colossal, épaules en pente, casque à cornes de naga, bras surdimensionnés
 *       et pagne en lambeaux.</li>
 * </ul>
 *
 * <p>Les parties sont nommées en français et exposées pour l'animation (marche, attaque) :
 * l'animateur n'a pas à connaître la hiérarchie interne.</p>
 */
public final class ModelesCambocraft {
    /** Texture du PNJ artisan (64×64). */
    public static final Identifier TEXTURE_ARTISAN = Ids.id("textures/entity/artisan.png");
    /** Texture du gardien de boss (128×128). */
    public static final Identifier TEXTURE_GARDIEN = Ids.id("textures/entity/gardien.png");
    /** Texture du compagnon Neak Ta (64×64). */
    public static final Identifier TEXTURE_NEAK_TA = Ids.id("textures/entity/neak_ta.png");

    private ModelesCambocraft() {}

    /** Données du modèle d'artisan : tête, chapeau, corps, sarong, bras, main d'ombrelle. */
    public static TexturedModelData donneesArtisan() {
        ModelData donnees = new ModelData();
        ModelPartData racine = donnees.getRoot();

        ModelPartData corps = racine.addChild("corps",
                ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f)
                        .uv(24, 16).cuboid(-4.5f, 8.0f, -2.5f, 9.0f, 4.0f, 5.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        corps.addChild("tete",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                        .uv(32, 0).cuboid(-3.5f, -9.0f, -3.5f, 7.0f, 1.0f, 7.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        // Chapeau conique à trois degrés : lecture immédiate de la silhouette cambodgienne.
        corps.addChild("chapeau",
                ModelPartBuilder.create()
                        .uv(48, 32).cuboid(-5.0f, -10.0f, -5.0f, 10.0f, 1.0f, 10.0f)
                        .uv(48, 43).cuboid(-3.5f, -12.0f, -3.5f, 7.0f, 2.0f, 7.0f)
                        .uv(48, 52).cuboid(-2.0f, -14.0f, -2.0f, 4.0f, 2.0f, 4.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        corps.addChild("bras_droit",
                ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-2.0f, -1.0f, -2.0f, 3.0f, 11.0f, 3.0f),
                ModelTransform.of(-4.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.12f));

        ModelPartData brasGauche = corps.addChild("bras_gauche",
                ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-1.0f, -1.0f, -2.0f, 3.0f, 11.0f, 3.0f),
                ModelTransform.of(4.5f, 1.0f, 0.0f, 0.0f, 0.0f, -0.12f));

        // Ombrelle de soie : manche + disque, tenue dans la main gauche.
        brasGauche.addChild("ombrelle",
                ModelPartBuilder.create()
                        .uv(56, 40).cuboid(-0.5f, 6.0f, -0.5f, 1.0f, 12.0f, 1.0f)
                        .uv(56, 54).cuboid(-4.0f, 18.0f, -4.0f, 8.0f, 1.0f, 8.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        corps.addChild("jambe_droite",
                ModelPartBuilder.create()
                        .uv(0, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
                ModelTransform.of(-2.0f, 12.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        corps.addChild("jambe_gauche",
                ModelPartBuilder.create()
                        .uv(16, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
                ModelTransform.of(2.0f, 12.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        return TexturedModelData.of(donnees, 64, 64);
    }

    /** Données du modèle de gardien : masse, casque à cornes, bras lourds, pagne. */
    public static TexturedModelData donneesGardien() {
        ModelData donnees = new ModelData();
        ModelPartData racine = donnees.getRoot();

        ModelPartData corps = racine.addChild("corps",
                ModelPartBuilder.create()
                        .uv(0, 40).cuboid(-9.0f, -18.0f, -4.0f, 18.0f, 22.0f, 8.0f)
                        .uv(52, 40).cuboid(-10.0f, -2.0f, -5.0f, 20.0f, 6.0f, 10.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        ModelPartData tete = corps.addChild("tete",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f)
                        .uv(48, 0).cuboid(-5.0f, -13.0f, -5.0f, 10.0f, 1.0f, 10.0f),
                ModelTransform.of(0.0f, -18.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        tete.addChild("corne_droite",
                ModelPartBuilder.create().uv(84, 20).cuboid(-1.0f, -6.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                ModelTransform.of(-6.0f, -10.0f, 0.0f, 0.0f, 0.0f, 0.5f));
        tete.addChild("corne_gauche",
                ModelPartBuilder.create().uv(84, 20).cuboid(-1.0f, -6.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                ModelTransform.of(6.0f, -10.0f, 0.0f, 0.0f, 0.0f, -0.5f));

        corps.addChild("bras_droit",
                ModelPartBuilder.create().uv(88, 40).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 20.0f, 6.0f),
                ModelTransform.of(-11.0f, -16.0f, 0.0f, 0.0f, 0.0f, 0.08f));
        corps.addChild("bras_gauche",
                ModelPartBuilder.create().uv(88, 40).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 20.0f, 6.0f),
                ModelTransform.of(11.0f, -16.0f, 0.0f, 0.0f, 0.0f, -0.08f));

        corps.addChild("pagne",
                ModelPartBuilder.create().uv(0, 72).cuboid(-9.0f, 0.0f, -5.0f, 18.0f, 14.0f, 10.0f),
                ModelTransform.of(0.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        corps.addChild("jambe_droite",
                ModelPartBuilder.create().uv(56, 72).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 12.0f, 6.0f),
                ModelTransform.of(-4.5f, 18.0f, 0.0f, 0.0f, 0.0f, 0.0f));
        corps.addChild("jambe_gauche",
                ModelPartBuilder.create().uv(56, 72).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 12.0f, 6.0f),
                ModelTransform.of(4.5f, 18.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        return TexturedModelData.of(donnees, 128, 128);
    }

    /** Données du compagnon Neak Ta : petit gardien de sanctuaire de pierre. */
    public static TexturedModelData donneesNeakTa() {
        ModelData donnees = new ModelData();
        ModelPartData racine = donnees.getRoot();
        ModelPartData corps = racine.addChild("corps",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 10.0f, 6.0f)
                        .uv(0, 16).cuboid(-5.0f, 1.0f, -4.0f, 10.0f, 3.0f, 8.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
        corps.addChild("tete",
                ModelPartBuilder.create()
                        .uv(28, 0).cuboid(-3.0f, -6.0f, -3.0f, 6.0f, 6.0f, 6.0f)
                        .uv(28, 12).cuboid(-4.0f, -7.0f, -4.0f, 8.0f, 1.0f, 8.0f),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(donnees, 64, 64);
    }

    /** Crée la partie racine de l'artisan à partir des données (appelée par le modèle). */
    public static ModelPart partieArtisan() {
        return donneesArtisan().createModel();
    }

    /** Crée la partie racine du gardien. */
    public static ModelPart partieGardien() {
        return donneesGardien().createModel();
    }
}
