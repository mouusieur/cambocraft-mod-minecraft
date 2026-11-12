package com.cambocraft.client.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;

/**
 * Modèle animé de l'artisan : marche balancée, oscillation du chapeau et balancement de l'ombrelle.
 *
 * <p>Les amplitudes sont volontairement modestes : le PNJ est pacifique et doit inspirer le calme,
 * pas la menace. L'animation ne dépend que du temps de marche fourni par Minecraft, ce qui la rend
 * déterministe et économe.</p>
 */
public class ModelArtisan extends SinglePartEntityModel<Entity> {
    private final ModelPart racine;
    private final ModelPart corps;
    private final ModelPart tete;
    private final ModelPart brasDroit;
    private final ModelPart brasGauche;
    private final ModelPart jambeDroite;
    private final ModelPart jambeGauche;

    @SuppressWarnings("unchecked")
    public ModelArtisan() {
        this.racine = ModelesCambocraft.partieArtisan();
        this.corps = racine.getChild("corps");
        this.tete = corps.getChild("tete");
        this.brasDroit = corps.getChild("bras_droit");
        this.brasGauche = corps.getChild("bras_gauche");
        this.jambeDroite = corps.getChild("jambe_droite");
        this.jambeGauche = corps.getChild("jambe_gauche");
    }

    @Override
    public ModelPart getPart() {
        return racine;
    }

    @Override
    public void setAngles(Entity entite, float membre, float intensite, float age, float lacet, float tangage) {
        // Repos de base : bras le long du corps, tête droite.
        this.tete.yaw = lacet * 0.017453292f / 2.0f;
        this.tete.pitch = tangage * 0.017453292f / 2.0f;

        float balancement = membre * 0.5f;
        this.jambeDroite.pitch = (float) Math.sin(balancement) * 0.55f * Math.min(1.0f, intensite);
        this.jambeGauche.pitch = (float) Math.sin(balancement + (float) Math.PI) * 0.55f * Math.min(1.0f, intensite);
        this.brasDroit.pitch = (float) Math.sin(balancement + (float) Math.PI) * 0.35f * Math.min(1.0f, intensite);
        // Le bras qui tient l'ombrelle reste haut et oscille doucement.
        this.brasGauche.pitch = -0.6f + (float) Math.sin(age * 0.12f) * 0.06f;
        this.brasGauche.roll = -0.12f;

        this.corps.yaw = 0.0f;
        this.racine.pivotY = 24.0f;
    }
}
