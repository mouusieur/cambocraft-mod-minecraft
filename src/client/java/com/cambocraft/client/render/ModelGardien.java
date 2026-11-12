package com.cambocraft.client.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;

/**
 * Modèle animé du gardien de boss : démarche lourde, bras qui balaient, tête qui suit la cible.
 *
 * <p>Le gardien compte 12 rencontres distinctes : le modèle est commun, mais l'échelle et la
 * teinte changent selon le profil (voir {@link RenduGardien}). L'animation accentue la masse :
 * pas longs, épaules qui roulent, aucune accélération saccadée.</p>
 */
public class ModelGardien extends SinglePartEntityModel<Entity> {
    private final ModelPart racine;
    private final ModelPart corps;
    private final ModelPart tete;
    private final ModelPart brasDroit;
    private final ModelPart brasGauche;
    private final ModelPart jambeDroite;
    private final ModelPart jambeGauche;

    public ModelGardien() {
        this.racine = ModelesCambocraft.partieGardien();
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
        this.tete.yaw = lacet * 0.017453292f * 0.6f;
        this.tete.pitch = tangage * 0.017453292f * 0.4f;

        float balancement = membre * 0.35f;
        float force = Math.min(1.0f, intensite);
        this.jambeDroite.pitch = (float) Math.sin(balancement) * 0.7f * force;
        this.jambeGauche.pitch = (float) Math.sin(balancement + (float) Math.PI) * 0.7f * force;
        this.brasDroit.pitch = (float) Math.sin(balancement + (float) Math.PI) * 0.45f * force - 0.15f;
        this.brasGauche.pitch = (float) Math.sin(balancement) * 0.45f * force - 0.15f;
        this.corps.roll = (float) Math.sin(balancement * 2.0f) * 0.05f * force;
        this.racine.pivotY = 24.0f;
    }
}
