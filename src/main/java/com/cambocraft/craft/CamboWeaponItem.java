package com.cambocraft.craft;

import net.minecraft.item.Item;

/**
 * Arme de Cambocraft.
 *
 * <p>Les dégâts et la vitesse sont fournis par les modificateurs d'attributs posés dans les
 * réglages de l'objet (voir {@link CamboWeapons}). Cette classe existe pour accueillir le
 * comportement propre aux armes : portée de la pique, récolte accélérée de la faucille, et
 * application des effets de maîtrise des pouvoirs côté serveur.</p>
 */
public class CamboWeaponItem extends Item {
    public CamboWeaponItem(Settings settings) {
        super(settings);
    }
}
