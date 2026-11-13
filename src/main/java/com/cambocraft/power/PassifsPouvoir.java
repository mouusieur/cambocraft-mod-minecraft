package com.cambocraft.power;

import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Effets permanents des pouvoirs {@link FormePouvoir#PASSIF} équipés.
 *
 * <p>Un pouvoir passif ne se lance pas : il agit tant qu'il occupe l'un des trois emplacements
 * actifs du Carnet du Nen. Ce gestionnaire réapplique ses effets à chaque seconde, avec une durée
 * courte, ce qui produit un état permanent sans jamais créer d'effet qui survivrait au retrait du
 * pouvoir ou à la déconnexion.</p>
 */
public final class PassifsPouvoir {

    /** Durée réappliquée à chaque tick de rafraîchissement (2 s, rafraîchi chaque seconde). */
    private static final int DUREE = 40;

    private PassifsPouvoir() {}

    /**
     * Applique les effets passifs des pouvoirs équipés du joueur.
     *
     * @param joueur joueur concerné
     * @param carnet son Carnet du Nen
     */
    public static void rafraichir(ServerPlayerEntity joueur, NenNotebook carnet) {
        if (joueur == null || carnet == null) {
            return;
        }
        for (int slot = 0; slot < NenNotebook.NB_EMPLACEMENTS_ACTIFS; slot++) {
            String id = carnet.pouvoirEquipe(slot);
            if (id == null) {
                continue;
            }
            PowerDefinition def = PowerRegistry.get(id);
            if (def == null || FormePouvoir.formeDe(def) != FormePouvoir.PASSIF) {
                continue;
            }
            appliquer(joueur, def);
        }
    }

    /** Effets d'un passif : ils dépendent de son élément, ce qui donne 5 passifs distincts. */
    static void appliquer(ServerPlayerEntity joueur, PowerDefinition def) {
        switch (def.element()) {
            case VIE -> {
                effet(joueur, "regeneration", 0);
                effet(joueur, "luck", 0);
            }
            case TERRE -> {
                effet(joueur, "resistance", 0);
                effet(joueur, "haste", 0);
            }
            case AIR -> {
                effet(joueur, "speed", 1);
                effet(joueur, "slow_falling", 0);
            }
            case FEU -> {
                effet(joueur, "strength", 0);
                effet(joueur, "fire_resistance", 0);
            }
            case EAU -> {
                effet(joueur, "water_breathing", 0);
                effet(joueur, "dolphins_grace", 0);
            }
            case LUMIERE -> {
                effet(joueur, "night_vision", 0);
                effet(joueur, "health_boost", 0);
            }
            case OMBRE -> {
                effet(joueur, "invisibility", 250);   // amplification 250 : effet très atténué
                effet(joueur, "speed", 0);
            }
        }
    }

    /**
     * Réapplique un effet sans écraser un effet plus fort déjà présent : un buff de potion
     * temporaire ne doit pas être dégradé par le rafraîchissement d'un passif.
     */
    private static void effet(ServerPlayerEntity joueur, String nom, int amplification) {
        var entry = com.cambocraft.util.Effects.vanilla(nom);
        if (entry == null) {
            return;
        }
        StatusEffectInstance existant = joueur.getStatusEffect(entry);
        if (existant != null && (existant.getAmplifier() > amplification
                || existant.getDuration() > DUREE + 20)) {
            return;
        }
        joueur.addStatusEffect(new StatusEffectInstance(entry, DUREE, amplification, true, false));
    }

    /** Liste des éléments couverts : contrôle de complétude utilisé par les tests. */
    public static List<PowerElement> elementsCouverts() {
        return List.of(PowerElement.VIE, PowerElement.TERRE, PowerElement.AIR, PowerElement.FEU,
                PowerElement.EAU, PowerElement.LUMIERE, PowerElement.OMBRE);
    }
}
