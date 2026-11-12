package com.cambocraft.craft;

import com.cambocraft.util.Ids;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;

/**
 * Fabrication des caractéristiques d'arme.
 *
 * <p>Une arme de Cambocraft déclare ses dégâts et sa vitesse ; la vitesse est exprimée comme
 * la pénalité appliquée à la vitesse d'attaque de base (valeur négative, comme les armes
 * vanilla). La valeur affichée dans l'infobulle et la valeur utilisée en combat viennent donc
 * de la même source : aucun risque d'écart entre la documentation et le jeu.</p>
 */
public final class CamboWeapons {
    private CamboWeapons() {}

    /** Modificateurs d'une arme : dégâts bonus et pénalité de vitesse d'attaque. */
    public static AttributeModifiersComponent attack(double damage, double speed) {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Ids.id("base_attack_damage"), damage,
                                EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(Ids.id("base_attack_speed"), speed,
                                EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .build();
    }
}
