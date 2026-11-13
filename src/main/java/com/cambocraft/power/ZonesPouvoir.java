package com.cambocraft.power;

import com.cambocraft.combat.CombatEngine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Zones persistantes créées par les pouvoirs de type {@link FormePouvoir#ZONE}.
 *
 * <p>Une zone survit au lancement : elle occupe une sphère au sol et frappe <b>à chaque tick</b>
 * les entités qui s'y trouvent, tant que sa durée n'est pas écoulée. C'est ce qui distingue un
 * « Tourbillon du Naga » d'une simple explosion : on peut y entrer, en sortir, y être retenu.</p>
 *
 * <p>Les zones sont la propriété du serveur : elles ne sont pas sauvegardées en NBT (une zone
 * survit au maximum quelques secondes) et disparaissent au redémarrage, ce qui est sans
 * conséquence.</p>
 */
public final class ZonesPouvoir {

    /** Une zone active, avec son compte à rebours et sa cadence de frappe. */
    public static final class Zone {
        private final ServerWorld monde;
        private final Vec3d centre;
        private final String pouvoirId;
        private final PowerElement element;
        private final double rayon;
        private final double degatsParFrappe;
        private final double bonusMaitrise;
        private final java.util.UUID lanceur;
        private final int dureeTicks;
        private int ticksRestants;
        private int ticksDepuisFrappe;

        Zone(ServerWorld monde, Vec3d centre, String pouvoirId, PowerElement element, double rayon,
             double degatsParFrappe, double bonusMaitrise, java.util.UUID lanceur, int dureeTicks) {
            this.monde = monde;
            this.centre = centre;
            this.pouvoirId = pouvoirId;
            this.element = element;
            this.rayon = rayon;
            this.degatsParFrappe = degatsParFrappe;
            this.bonusMaitrise = bonusMaitrise;
            this.lanceur = lanceur;
            this.dureeTicks = Math.max(20, dureeTicks);
            this.ticksRestants = this.dureeTicks;
        }

        public Vec3d centre() { return centre; }
        public double rayon() { return rayon; }
        public String pouvoirId() { return pouvoirId; }
        public int ticksRestants() { return ticksRestants; }
        public boolean active() { return ticksRestants > 0; }

        /** Avance d'un tick : frappe périodiquement, émet des particules, décompte la durée. */
        void avancer(List<LivingEntity> cibles, Runnable emission) {
            ticksRestants--;
            ticksDepuisFrappe++;
            emission.run();
            // Une frappe toutes les 10 ticks : assez pour être menaçant, assez lent pour laisser
            // le temps de sortir de la zone.
            if (ticksDepuisFrappe < 10 || degatsParFrappe <= 0.0) {
                return;
            }
            ticksDepuisFrappe = 0;
            for (LivingEntity cible : cibles) {
                if (cible.getUuid().equals(lanceur) || cible instanceof PlayerEntity || cible.isDead()) {
                    continue;
                }
                double degats = CombatEngine.calculerDegatsServeur(degatsParFrappe, 0, element,
                        PowerElement.TERRE, 1.0, bonusMaitrise, false, cible.getArmor());
                cible.damage(monde.getDamageSources().magic(), (float) degats);
                cible.setOnFireFor(element == PowerElement.FEU ? 2 : 0);
            }
        }
    }

    private static final List<Zone> ZONES = new ArrayList<>();

    private ZonesPouvoir() {}

    /** Crée une zone centrée sur le lanceur, d'après les statistiques du pouvoir. */
    public static Zone creer(ServerPlayerEntity joueur, PowerDefinition def,
                             PowerDefinition.LevelStats stats, double bonusMaitrise) {
        if (!(joueur.getWorld() instanceof ServerWorld monde)) {
            return null;
        }
        Zone zone = new Zone(monde, joueur.getPos(), def.id(), def.element(),
                Math.max(2.5, stats.portee() / 2.0),
                stats.degats() * 0.35,
                bonusMaitrise,
                joueur.getUuid(),
                stats.dureeTicks() > 0 ? stats.dureeTicks() : 120);
        ZONES.add(zone);
        return zone;
    }

    /** Avance toutes les zones d'un monde : appelé à chaque tick serveur. */
    public static void avancer(ServerWorld monde) {
        Iterator<Zone> it = ZONES.iterator();
        while (it.hasNext()) {
            Zone zone = it.next();
            if (zone.monde != monde) {
                continue;
            }
            if (!zone.active()) {
                it.remove();
                continue;
            }
            Box boite = Box.of(zone.centre, zone.rayon * 2, zone.rayon * 2, zone.rayon * 2);
            @SuppressWarnings("unchecked")
            List<LivingEntity> cibles = monde.getEntitiesByClass(LivingEntity.class, boite,
                    e -> ((LivingEntity) e).getPos().squaredDistanceTo(zone.centre) <= zone.rayon * zone.rayon);
            ParticleEffect particules = PowerEffects.particulesDe(zone.element);
            zone.avancer(cibles, () -> monde.spawnParticles(particules,
                    zone.centre.x, zone.centre.y + 0.4, zone.centre.z,
                    4, zone.rayon / 2.0, 0.3, zone.rayon / 2.0, 0.01));
        }
    }

    /** Nombre de zones actives (diagnostic et tests). */
    public static int actives() {
        return ZONES.size();
    }

    /** Vide le registre : utilisé par les tests pour repartir d'un état propre. */
    public static void reinitialiser() {
        ZONES.clear();
    }
}
