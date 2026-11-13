package com.cambocraft.power;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Suivi des alliés créés par les pouvoirs {@link FormePouvoir#INVOCATION}.
 *
 * <p>Les créatures invoquées sont de vraies entités du monde (des loups apprivoisés) : elles
 * combattent, suivent leur maître et meurent normalement. Ce gestionnaire se contente de tenir
 * le compte à rebours : à l'expiration, l'allié disparaît en fumée, pour qu'un « Légion des
 * Ombres Silencieuses » reste un pouvoir à durée limitée et non une armée permanente.</p>
 */
public final class InvocationsPouvoir {

    /** Un allié suivi, avec son délai d'expiration. */
    public static final class Invocation {
        private final MobEntity entite;
        private final java.util.UUID maitre;
        private final String pouvoirId;
        private final int dureeTicks;
        private int ticksRestants;

        Invocation(MobEntity entite, java.util.UUID maitre, String pouvoirId, int dureeTicks) {
            this.entite = entite;
            this.maitre = maitre;
            this.pouvoirId = pouvoirId;
            this.dureeTicks = dureeTicks;
            this.ticksRestants = dureeTicks;
        }

        public MobEntity entite() { return entite; }
        public String pouvoirId() { return pouvoirId; }
        public int ticksRestants() { return ticksRestants; }
        public int dureeTicks() { return dureeTicks; }
        public java.util.UUID maitre() { return maitre; }
    }

    private static final List<Invocation> INVOCATIONS = new ArrayList<>();

    private InvocationsPouvoir() {}

    /** Enregistre un allié venant d'apparaître. */
    public static Invocation enregistrer(MobEntity entite, ServerPlayerEntity maitre,
                                         String pouvoirId, int dureeTicks) {
        Invocation invocation = new Invocation(entite, maitre.getUuid(), pouvoirId,
                Math.max(20, dureeTicks));
        INVOCATIONS.add(invocation);
        return invocation;
    }

    /**
     * Avance les compteurs et retire les alliés expirés.
     *
     * <p>Un allié qui meurt, ou dont le maître se déconnecte, est simplement oublié : il n'y a
     * rien à nettoyer dans le monde.</p>
     */
    public static void avancer(MinecraftServer serveur) {
        Iterator<Invocation> it = INVOCATIONS.iterator();
        while (it.hasNext()) {
            Invocation invocation = it.next();
            MobEntity entite = invocation.entite;
            if (entite == null || entite.isDead()) {
                it.remove();
                continue;
            }
            if (serveur.getPlayerManager().getPlayer(invocation.maitre) == null) {
                it.remove();
                continue;
            }
            invocation.ticksRestants--;
            if (invocation.ticksRestants > 0) {
                continue;
            }
            // Expiration : l'allié disparaît sans butin, en laissant une trace de particules.
            if (entite.getWorld() instanceof net.minecraft.server.world.ServerWorld monde) {
                monde.spawnParticles(PowerEffects.particulesDe(PowerElement.OMBRE),
                        entite.getX(), entite.getY() + 0.5, entite.getZ(), 25, 0.4, 0.6, 0.4, 0.05);
            }
            entite.discard();
            it.remove();
        }
    }

    /** Nombre d'alliés actuellement suivis. */
    public static int actives() {
        return INVOCATIONS.size();
    }

    /** Alliés d'un joueur donné (diagnostic). */
    public static long activesPour(java.util.UUID maitre) {
        return INVOCATIONS.stream().filter(i -> i.maitre.equals(maitre)).count();
    }

    /** Vide le registre : utilisé par les tests. */
    public static void reinitialiser() {
        INVOCATIONS.clear();
    }

    /** Vue en lecture seule, pour les tests et les commandes de diagnostic. */
    public static List<Invocation> toutes() {
        return List.copyOf(INVOCATIONS);
    }

    /** Petite aide : une entité vivante encore suivie ? */
    static boolean suit(LivingEntity entite) {
        return INVOCATIONS.stream().anyMatch(i -> i.entite == entite);
    }
}
