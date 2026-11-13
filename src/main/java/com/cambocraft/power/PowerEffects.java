package com.cambocraft.power;

import com.cambocraft.combat.CombatEngine;
import com.cambocraft.util.Effects;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Moteur d'exécution des pouvoirs du Nen : <b>chaque</b> pouvoir produit un effet réel.
 *
 * <p>Le pouvoir est déjà validé en amont (possédé, équipé, rechargé, énergie suffisante) par
 * {@link NenNotebook#activerPouvoir(int, long)}. Ce moteur ne décide plus de la permission : il
 * met en scène l'effet, et renvoie un {@link Resultat} décrivant ce qui s'est passé pour que
 * l'appelant puisse journaliser et notifier le joueur.</p>
 *
 * <p>La mise en scène dépend de trois choses : la <b>forme</b> du pouvoir (voir
 * {@link FormePouvoir}), ses <b>statistiques</b> au palier courant, et son <b>élément</b>
 * (particules, son, effet de statut principal, comportement secondaire). Les dégâts passent par
 * {@link CombatEngine#calculerDegatsServeur} : affinité élémentaire, forge, synergies et maîtrise
 * s'appliquent donc vraiment, comme pour un coup d'épée.</p>
 */
public final class PowerEffects {

    /** Multiplicateur appliqué aux statistiques quand la Maîtrise est débloquée. */
    public static final double BONUS_MAITRISE_ACTIF = 0.20;
    /** Les alliés invoqués disparaissent après ce délai (en ticks). */
    public static final int DUREE_INVOCATION_TICKS = 30 * 20;
    /** Nombre maximal d'alliés invoqués simultanément par pouvoir. */
    public static final int INVOCATIONS_MAX = 3;

    private PowerEffects() {}

    /** Décompte de ce qu'un lancement a réellement produit. */
    public record Resultat(
            String pouvoirId,
            FormePouvoir forme,
            int ciblesTouchees,
            double degatsTotaux,
            double soinApplique,
            int alliésInvoques,
            boolean effetVisible
    ) {
        public boolean sansEffet() {
            return ciblesTouchees == 0 && soinApplique <= 0.0 && !effetVisible;
        }

        /** Ligne courte pour le journal serveur et le test de couverture. */
        public String resume() {
            return String.format("%s (%s) : %d cible(s), %.1f dégâts, %.1f soin, %d allié(s)",
                    pouvoirId, forme.name().toLowerCase(java.util.Locale.ROOT),
                    ciblesTouchees, degatsTotaux, soinApplique, alliésInvoques);
        }
    }

    /**
     * Exécute le pouvoir du joueur au palier donné.
     *
     * @param joueur lanceur (serveur uniquement)
     * @param def    définition du pouvoir
     * @param stats  statistiques du palier courant
     * @param maitrise vrai si la Maîtrise est débloquée (bonus appliqué)
     */
    public static Resultat executer(ServerPlayerEntity joueur, PowerDefinition def,
                                    PowerDefinition.LevelStats stats, boolean maitrise) {
        if (joueur == null || def == null || stats == null) {
            return new Resultat("inconnu", FormePouvoir.PROJECTILE, 0, 0, 0, 0, false);
        }
        FormePouvoir forme = FormePouvoir.formeDe(def);
        if (!(joueur.getWorld() instanceof ServerWorld monde)) {
            return new Resultat(def.id(), forme, 0, 0, 0, 0, false);
        }
        double bonus = (maitrise ? BONUS_MAITRISE_ACTIF : 0.0) + def.maitrise().bonusMultiplicateur();
        Contexte ctx = new Contexte(joueur, monde, def, stats, bonus);

        // Mise en scène : toujours au moins les particules et le son du pouvoir. C'est ce qui
        // rend un lancement lisible même sans cible à portée.
        jouerEffets(ctx);

        return switch (forme) {
            case PROJECTILE -> projectile(ctx);
            case CONE -> cone(ctx);
            case NOVA -> nova(ctx);
            case ZONE -> zone(ctx);
            case MELEE -> melee(ctx);
            case BEAM -> rayon(ctx);
            case BLINK -> teleportation(ctx);
            case DASH -> elan(ctx);
            case BOUCLIER -> bouclier(ctx);
            case SOIN -> soin(ctx);
            case REGENERATION -> regeneration(ctx);
            case ENTRAVE -> entrave(ctx);
            case PEUR -> peur(ctx);
            case INVOCATION -> invocation(ctx);
            case PASSIF -> passif(ctx);
            case REVELATION -> revelation(ctx);
        };
    }

    // ------------------------------------------------------------------ formes

    private static Resultat projectile(Contexte c) {
        // Un trait visible part de l'œil du lanceur vers le point visé : il traverse la
        // distance réelle et frappe la première entité sur le trajet, comme une flèche.
        double portee = Math.max(2.0, c.stats.portee());
        Vec3d depart = c.joueur.getEyePos();
        Vec3d direction = c.joueur.getRotationVector().normalize();
        Vec3d arrivee = depart.add(direction.multiply(portee));

        c.monde.spawnParticles(c.particules(), arrivee.x, arrivee.y, arrivee.z,
                12, 0.4, 0.4, 0.4, 0.0);
        double accumule = 0.0;
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, Box.of(arrivee, portee, portee / 2.0, portee))) {
            accumule += frapper(c, cible, 1.0);
            touches++;
        }
        return new Resultat(c.def.id(), FormePouvoir.PROJECTILE, touches, accumule, 0.0, 0, true);
    }

    private static Resultat cone(Contexte c) {
        // Cône frontal de 90° : on retient les entités dont la direction est alignée avec le
        // regard du lanceur, dans la portée du pouvoir.
        Vec3d regard = c.joueur.getRotationVector().normalize();
        Vec3d centre = c.joueur.getEyePos().add(regard.multiply(c.stats.portee() / 2.0));
        double accumule = 0.0;
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, Box.of(centre, c.stats.portee() * 2.0,
                c.stats.portee() * 2.0, c.stats.portee() * 2.0))) {
            Vec3d vers = cible.getPos().subtract(c.joueur.getPos()).normalize();
            if (vers.dotProduct(regard) < 0.5) {
                continue;   // hors du cône
            }
            accumule += frapper(c, cible, 0.9);
            reculer(cible, regard, 0.6);
            touches++;
        }
        particulesEnCone(c);
        return new Resultat(c.def.id(), FormePouvoir.CONE, touches, accumule, 0.0, 0, true);
    }

    private static Resultat nova(Contexte c) {
        // Explosion sphérique centrée sur le lanceur : tout ce qui est à portée est frappé.
        double accumule = 0.0;
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, Box.of(c.joueur.getPos(),
                c.stats.portee() * 2.0, c.stats.portee() * 2.0, c.stats.portee() * 2.0))) {
            accumule += frapper(c, cible, 1.1);
            Vec3d pousse = cible.getPos().subtract(c.joueur.getPos()).normalize();
            reculer(cible, pousse, 0.9);
            touches++;
        }
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 0.2,
                c.joueur.getZ(), 80, c.stats.portee() / 2.0, 0.6, c.stats.portee() / 2.0, 0.15);
        // Son d'explosion : un pouvoir de zone doit s'entendre, pas seulement se voir.
        son(c, "minecraft:entity.generic.explode", 1.2f);
        return new Resultat(c.def.id(), FormePouvoir.NOVA, touches, accumule, 0.0, 0, true);
    }

    private static Resultat zone(Contexte c) {
        // Zone persistante : elle est enregistrée dans le gestionnaire, qui applique les dégâts
        // à chaque tick aux entités qui s'y trouvent (voir ZonesPouvoir).
        ZonesPouvoir.creer(c.joueur, c.def, c.stats, c.bonus);
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 0.1,
                c.joueur.getZ(), 60, c.stats.portee() / 2.0, 0.3, c.stats.portee() / 2.0, 0.02);
        son(c, "minecraft:block.beacon.ambient", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.ZONE, 0, 0.0, 0.0, 0, true);
    }

    private static Resultat melee(Contexte c) {
        // Frappe au corps à corps : courte portée, dégâts élevés, fort recul.
        double portee = Math.max(2.5, c.stats.portee() * 0.5);
        Vec3d centre = c.joueur.getPos().add(c.joueur.getRotationVector().normalize()
                .multiply(portee / 2.0));
        double accumule = 0.0;
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, Box.of(centre, portee, portee, portee))) {
            accumule += frapper(c, cible, 1.3);
            reculer(cible, c.joueur.getRotationVector().normalize(), 1.0);
            touches++;
        }
        son(c, "minecraft:entity.player.attack.strong", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.MELEE, touches, accumule, 0.0, 0, true);
    }

    private static Resultat rayon(Contexte c) {
        // Rayon continu : trait fin entre l'œil et la cible visée, avec dégâts et effet d'élément.
        LivingEntity cible = cibleVisee(c, c.stats.portee());
        double accumule = 0.0;
        if (cible != null) {
            accumule += frapper(c, cible, 1.4);
        }
        Vec3d depart = c.joueur.getEyePos();
        Vec3d direction = c.joueur.getRotationVector().normalize();
        for (int i = 1; i <= 12; i++) {
            Vec3d point = depart.add(direction.multiply(c.stats.portee() * i / 12.0));
            c.monde.spawnParticles(c.particules(), point.x, point.y, point.z, 2, 0.05, 0.05, 0.05, 0.0);
        }
        son(c, "minecraft:block.beacon.power_select", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.BEAM, cible != null ? 1 : 0, accumule, 0.0, 0, true);
    }

    private static Resultat teleportation(Contexte c) {
        // Téléportation vers le point visé : on projette un rayon sur 32 blocs et on s'arrête
        // devant le premier bloc rencontré, pour ne jamais finir dans la roche.
        HitResult hit = c.joueur.raycast(32.0, 0.0f, false);
        Vec3d destination;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bloc = (BlockHitResult) hit;
            destination = Vec3d.ofCenter(bloc.getBlockPos()).add(0, 1, 0);
        } else {
            destination = c.joueur.getEyePos()
                    .add(c.joueur.getRotationVector().normalize().multiply(c.stats.portee()));
        }
        Vec3d depart = c.joueur.getPos();
        c.monde.spawnParticles(c.particules(), depart.x, depart.y + 1.0, depart.z, 40, 0.4, 0.9, 0.4, 0.05);
        double accumule = 0.0;
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, Box.of(destination, 3.0, 3.0, 3.0))) {
            if (c.stats.degats() > 0) {
                accumule += frapper(c, cible, 0.8);
                touches++;
            }
        }
        c.joueur.teleport(c.monde, destination.x, destination.y, destination.z,
                c.joueur.getYaw(), c.joueur.getPitch());
        c.monde.spawnParticles(c.particules(), destination.x, destination.y + 1.0, destination.z,
                40, 0.4, 0.9, 0.4, 0.05);
        son(c, "minecraft:entity.enderman.teleport", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.BLINK, touches, accumule, 0.0, 0, true);
    }

    private static Resultat elan(Contexte c) {
        // Élan : propulsion dans la direction du regard, proportionnelle à la portée. Le recul
        // est plafonné pour ne pas projeter le joueur hors du monde.
        Vec3d direction = c.joueur.getRotationVector().normalize();
        double force = Math.min(3.2, 0.6 + c.stats.portee() * 0.08);
        c.joueur.setVelocity(direction.x * force, Math.max(0.35, direction.y * force + 0.25),
                direction.z * force);
        c.joueur.velocityModified = true;
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 0.3,
                c.joueur.getZ(), 30, 0.5, 0.4, 0.5, 0.25);
        son(c, "minecraft:entity.breeze.jump", 1.0f);
        // Les pouvoirs d'élan qui infligent des dégâts (danse des flammes) brûlent sur la route.
        double accumule = 0.0;
        int touches = 0;
        if (c.stats.degats() > 0) {
            for (LivingEntity cible : ciblesDans(c, Box.of(c.joueur.getPos(), 6.0, 4.0, 6.0))) {
                accumule += frapper(c, cible, 0.7);
                touches++;
            }
        }
        return new Resultat(c.def.id(), FormePouvoir.DASH, touches, accumule, 0.0, 0, true);
    }

    private static Resultat bouclier(Contexte c) {
        // Bouclier : absorption proportionnelle au soin déclaré, résistance et lenteur de
        // l'attaquant adverse. La durée vient de la définition du pouvoir.
        int duree = Math.max(60, c.stats.dureeTicks());
        appliquer(c, c.joueur, "absorption", duree, 1);
        appliquer(c, c.joueur, "resistance", duree, 1);
        if (c.def.element() == PowerElement.FEU) {
            appliquer(c, c.joueur, "fire_resistance", duree, 0);
        }
        double soigne = soinProportionnel(c, 0.6);
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 1.0,
                c.joueur.getZ(), 50, 0.8, 1.0, 0.8, 0.02);
        son(c, "minecraft:block.anvil.land", 0.7f);
        return new Resultat(c.def.id(), FormePouvoir.BOUCLIER, 0, 0.0, soigne, 0, true);
    }

    private static Resultat soin(Contexte c) {
        double soigne = soigner(c, c.joueur, c.stats.soin());
        int soignes = soigne > 0 ? 1 : 0;
        // Les pouvoirs de soin de haute rareté soignent aussi les alliés proches : c'est ce qui
        // distingue une « Aube Sacrée » d'une simple « Goutte Apaisante ».
        if (c.def.rarete() == PowerRarity.EPIQUE || c.def.rarete() == PowerRarity.LEGENDAIRE
                || c.def.rarete() == PowerRarity.MYTHIQUE) {
            for (LivingEntity allie : alliesDans(c, c.stats.portee())) {
                soigne += soigner(c, allie, c.stats.soin() * 0.5);
                soignes++;
            }
        }
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 1.0,
                c.joueur.getZ(), 40, 0.8, 0.8, 0.8, 0.05);
        son(c, "minecraft:block.beacon.activate", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.SOIN, 0, 0.0, soigne, 0, soignes > 0);
    }

    private static Resultat regeneration(Contexte c) {
        int duree = Math.max(80, c.stats.dureeTicks());
        appliquer(c, c.joueur, "regeneration", duree, niveauDepuisSoin(c.stats.soin()));
        appliquer(c, c.joueur, "saturation", Math.min(duree, 200), 0);
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 1.0,
                c.joueur.getZ(), 30, 0.9, 1.0, 0.9, 0.01);
        son(c, "minecraft:block.enchantment_table.use", 0.8f);
        double soigne = soinProportionnel(c, 0.25);
        return new Resultat(c.def.id(), FormePouvoir.REGENERATION, 0, 0.0, soigne, 0, true);
    }

    private static Resultat entrave(Contexte c) {
        int duree = Math.max(40, c.stats.dureeTicks() / 2);
        double accumule = 0.0;
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, zoneAutour(c))) {
            accumule += frapper(c, cible, 0.5);
            appliquer(c, cible, "slowness", duree, 3);
            appliquer(c, cible, "weakness", duree, 1);
            if (c.def.element() == PowerElement.TERRE) {
                appliquer(c, cible, "jump_boost", duree, 250);   // amplification extrême : saut bloqué
            }
            touches++;
        }
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 0.1,
                c.joueur.getZ(), 55, c.stats.portee() / 2.0, 0.3, c.stats.portee() / 2.0, 0.01);
        son(c, "minecraft:block.chain.break", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.ENTRAVE, touches, accumule, 0.0, 0, true);
    }

    private static Resultat peur(Contexte c) {
        int duree = Math.max(40, c.stats.dureeTicks() / 2);
        int touches = 0;
        for (LivingEntity cible : ciblesDans(c, zoneAutour(c))) {
            appliquer(c, cible, "darkness", duree, 0);
            appliquer(c, cible, "slowness", duree, 1);
            Vec3d fuite = cible.getPos().subtract(c.joueur.getPos()).normalize();
            reculer(cible, fuite, 1.4);
            touches++;
        }
        son(c, "minecraft:entity.warden.heartbeat", 1.2f);
        return new Resultat(c.def.id(), FormePouvoir.PEUR, touches, 0.0, 0.0, 0, true);
    }

    private static Resultat invocation(Contexte c) {
        // Invocation : des alliés apparaissent autour du lanceur et le suivent. Ils sont
        // enregistrés dans InvocationsPouvoir, qui les fera disparaître à l'expiration.
        int nombre = Math.min(INVOCATIONS_MAX, 1 + c.def.rarete().ordinal() / 2);
        int crees = 0;
        for (int i = 0; i < nombre; i++) {
            double angle = (Math.PI * 2.0 / nombre) * i;
            double x = c.joueur.getX() + Math.cos(angle) * 1.8;
            double z = c.joueur.getZ() + Math.sin(angle) * 1.8;
            // `EntityType.create(World)` construit l'entité avec son type réel : c'est le seul
            // constructeur utilisable, `new WolfEntity(World)` n'existe pas en 1.21.1.
            WolfEntity allie = EntityType.WOLF.create(c.monde);
            if (allie == null) {
                continue;
            }
            allie.refreshPositionAndAngles(x, c.joueur.getY(), z, c.joueur.getYaw(), 0.0f);
            allie.setOwner(c.joueur);
            allie.setTamed(true, true);
            allie.setPersistent();
            if (c.monde.spawnEntity(allie)) {
                InvocationsPouvoir.enregistrer(allie, c.joueur, c.def.id(), DUREE_INVOCATION_TICKS);
                crees++;
            }
        }
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 0.5,
                c.joueur.getZ(), 60, 1.5, 0.6, 1.5, 0.05);
        son(c, "minecraft:entity.wolf.howl", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.INVOCATION, 0, 0.0, 0.0, crees, crees > 0);
    }

    private static Resultat passif(Contexte c) {
        // Les pouvoirs passifs n'ont pas de geste : ils agissent tant qu'ils sont équipés
        // (voir PassifsPouvoir). L'activation volontaire redonne un peu d'énergie, ce qui rend
        // le bouton utile sans trahir leur nature.
        c.joueur.getHungerManager();
        appliquer(c, c.joueur, "regeneration", Math.max(60, c.stats.dureeTicks() / 2), 0);
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 1.2,
                c.joueur.getZ(), 25, 0.6, 0.6, 0.6, 0.03);
        son(c, "minecraft:block.amethyst_block.chime", 1.0f);
        return new Resultat(c.def.id(), FormePouvoir.PASSIF, 0, 0.0, 0.0, 0, true);
    }

    private static Resultat revelation(Contexte c) {
        // Révélation : toutes les entités vivantes autour du lanceur reçoivent l'effet
        // « glowing » : elles deviennent visibles à travers les murs pour toute l'équipe.
        int duree = Math.max(100, c.stats.dureeTicks());
        int touches = 0;
        @SuppressWarnings("unchecked")
        java.util.List<LivingEntity> proches = c.monde.getEntitiesByClass(LivingEntity.class,
                Box.of(c.joueur.getPos(), c.stats.portee() * 2, c.stats.portee() * 2,
                        c.stats.portee() * 2),
                e -> e != c.joueur);
        for (LivingEntity cible : proches) {
            appliquer(c, cible, "glowing", duree, 0);
            touches++;
        }
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 1.5,
                c.joueur.getZ(), 45, c.stats.portee() / 2.0, 0.8, c.stats.portee() / 2.0, 0.01);
        son(c, "minecraft:block.beacon.power_select", 1.2f);
        return new Resultat(c.def.id(), FormePouvoir.REVELATION, touches, 0.0, 0.0, 0, true);
    }

    // ------------------------------------------------------------------ outils

    /** État immuable d'un lancement, partagé par toutes les formes. */
    private record Contexte(
            ServerPlayerEntity joueur,
            ServerWorld monde,
            PowerDefinition def,
            PowerDefinition.LevelStats stats,
            double bonus
    ) {
        /** Particules de l'élément du pouvoir : sa signature visuelle. */
        ParticleEffect particules() {
            return particulesDe(def.element());
        }
    }

    /** Dégâts réels d'un pouvoir sur une cible, en passant par le moteur de combat. */
    static double frapper(Contexte c, LivingEntity cible, double facteur) {
        if (cible == c.joueur || cible.isDead() || c.stats.degats() <= 0.0) {
            return 0.0;
        }
        double degats = CombatEngine.calculerDegatsServeur(
                c.stats.degats() * facteur,
                0,                                  // la forge ne concerne que les armes tenues
                c.def.element(),
                elementDe(cible),
                1.0,                                // synergies : appliquées par le carnet, pas ici
                c.bonus,
                false,
                cible.getArmor());
        cible.damage(c.monde.getDamageSources().magic(), (float) degats);
        appliquerEffetElementaire(c, cible);
        return degats;
    }

    private static void appliquerEffetElementaire(Contexte c, LivingEntity cible) {
        int duree = Math.max(60, Math.min(200, c.stats.dureeTicks()));
        switch (c.def.element()) {
            case FEU -> {
                cible.setOnFireFor(3);
                appliquer(c, cible, "slowness", duree / 2, 1);
            }
            case EAU -> appliquer(c, cible, "slowness", duree / 2, 2);
            case AIR -> appliquer(c, cible, "levitation", 40, 1);
            case TERRE -> appliquer(c, cible, "mining_fatigue", duree / 2, 1);
            case OMBRE -> {
                appliquer(c, cible, "darkness", duree / 2, 0);
                appliquer(c, cible, "weakness", duree / 2, 0);
            }
            case LUMIERE -> appliquer(c, cible, "glowing", duree, 0);
            case VIE -> appliquer(c, cible, "poison", duree / 2, 0);
        }
    }

    private static PowerElement elementDe(LivingEntity cible) {
        // Les entités du mod ont leur élément ; pour le reste, l'élément dominant dépend de la
        // nature de la créature, ce qui donne un cycle élémentaire lisible en jeu.
        if (cible instanceof com.cambocraft.entity.BossGuardianEntity) {
            return PowerElement.OMBRE;
        }
        if (cible instanceof com.cambocraft.entity.ArtisanEntity) {
            return PowerElement.VIE;
        }
        // En 1.21.1, « mort-vivant » se lit `hasInvertedHealingAndHarm()` : soigner un
        // squelette le blesse, et c'est exactement la population concernée.
        return cible.hasInvertedHealingAndHarm() ? PowerElement.OMBRE : PowerElement.TERRE;
    }

    private static void appliquer(Contexte c, LivingEntity cible, String effet, int duree, int amplification) {
        var entry = Effects.vanilla(effet);
        if (entry == null) {
            return;
        }
        cible.addStatusEffect(new StatusEffectInstance(entry, Math.max(1, duree),
                Math.max(0, Math.min(255, amplification)), false, true));
    }

    private static double soigner(Contexte c, LivingEntity cible, double montant) {
        if (montant <= 0.0) {
            return 0.0;
        }
        double avant = cible.getHealth();
        double apres = Math.min(cible.getMaxHealth(), avant + montant);
        cible.heal((float) (apres - avant));
        return Math.max(0.0, apres - avant);
    }

    private static double soinProportionnel(Contexte c, double fraction) {
        return soigner(c, c.joueur, c.stats.soin() * fraction);
    }

    private static int niveauDepuisSoin(double soin) {
        if (soin >= 40) return 2;
        if (soin >= 20) return 1;
        return 0;
    }

    private static void reculer(LivingEntity cible, Vec3d direction, double force) {
        Vec3d pousse = direction.normalize().multiply(force);
        cible.setVelocity(cible.getVelocity().x + pousse.x, cible.getVelocity().y + 0.25,
                cible.getVelocity().z + pousse.z);
        cible.velocityModified = true;
    }

    private static List<LivingEntity> ciblesDans(Contexte c, Box zone) {
        @SuppressWarnings("unchecked")
        java.util.List<LivingEntity> brut = c.monde.getEntitiesByClass(LivingEntity.class, zone,
                e -> e != c.joueur && !(e instanceof PlayerEntity) && !((LivingEntity) e).isDead()
                        && !(e instanceof TameableEntity tame && tame.isTamed()));
        return brut;
    }

    private static List<LivingEntity> alliesDans(Contexte c, double portee) {
        @SuppressWarnings("unchecked")
        java.util.List<LivingEntity> brut = c.monde.getEntitiesByClass(LivingEntity.class,
                Box.of(c.joueur.getPos(), portee * 2, portee * 2, portee * 2),
                e -> e != c.joueur && ((LivingEntity) e).isAlive()
                        && !(e instanceof net.minecraft.entity.mob.HostileEntity));
        return brut;
    }

    private static Box zoneAutour(Contexte c) {
        double r = Math.max(2.0, c.stats.portee());
        return Box.of(c.joueur.getPos(), r * 2, r, r * 2);
    }

    private static LivingEntity cibleVisee(Contexte c, double portee) {
        Vec3d regard = c.joueur.getRotationVector().normalize();
        LivingEntity meilleure = null;
        double meilleureDistance = Double.MAX_VALUE;
        for (LivingEntity cible : ciblesDans(c, Box.of(c.joueur.getPos(),
                portee * 2, portee * 2, portee * 2))) {
            Vec3d vers = cible.getEyePos().subtract(c.joueur.getEyePos());
            double distance = vers.length();
            if (distance > portee || vers.normalize().dotProduct(regard) < 0.9) {
                continue;
            }
            if (distance < meilleureDistance) {
                meilleureDistance = distance;
                meilleure = cible;
            }
        }
        return meilleure;
    }

    // ------------------------------------------------------------------ FX

    /**
     * Mise en scène d'ouverture, commune à toutes les formes : un halo de particules autour du
     * lanceur et le son de lancement. C'est ce qui rend un pouvoir lisible même sans cible.
     */
    private static void jouerEffets(Contexte c) {
        c.monde.spawnParticles(c.particules(), c.joueur.getX(), c.joueur.getY() + 1.0,
                c.joueur.getZ(), 24, 0.5, 0.8, 0.5, 0.05);
        son(c, "minecraft:entity.player.attack.sweep", 0.6f);
    }

    private static void particulesEnCone(Contexte c) {
        Vec3d regard = c.joueur.getRotationVector().normalize();
        Vec3d base = c.joueur.getEyePos();
        for (int i = 1; i <= 10; i++) {
            Vec3d point = base.add(regard.multiply(c.stats.portee() * i / 10.0));
            c.monde.spawnParticles(c.particules(), point.x, point.y, point.z, 3, 0.3, 0.3, 0.3, 0.02);
        }
    }

    private static void son(Contexte c, String id, float volume) {
        try {
            SoundEvent evenement = Registries.SOUND_EVENT.get(Identifier.of(id));
            if (evenement != null) {
                c.monde.playSound(null, c.joueur.getX(), c.joueur.getY(), c.joueur.getZ(),
                        evenement, net.minecraft.sound.SoundCategory.PLAYERS, volume, 1.0f);
            }
        } catch (RuntimeException ignore) {
            // Un sound event absent ne doit jamais interrompre un pouvoir.
        }
    }

    /**
     * Particules associées à un élément : c'est la signature visuelle du pouvoir. Elles sont
     * résolues par le registre pour éviter de figer une constante qui changerait de version.
     */
    public static ParticleEffect particulesDe(PowerElement element) {
        if (element == null) {
            return ParticleTypes.END_ROD;
        }
        return switch (element) {
            case FEU -> ParticleTypes.FLAME;
            case EAU -> ParticleTypes.SPLASH;
            case AIR -> ParticleTypes.CLOUD;
            case TERRE -> ParticleTypes.CRIT;
            case OMBRE -> ParticleTypes.SQUID_INK;
            case LUMIERE -> ParticleTypes.END_ROD;
            case VIE -> ParticleTypes.HAPPY_VILLAGER;
        };
    }

    /** Résout un type de particule par identifiant, pour les pouvoirs développés en datapack. */
    public static ParticleEffect particuleParId(String id) {
        try {
            Object type = Registries.PARTICLE_TYPE.get(Identifier.of(id));
            if (type instanceof ParticleEffect effet) {
                return effet;
            }
        } catch (RuntimeException ignore) {
            // identifiant inconnu : on retombe sur la particule neutre
        }
        return ParticleTypes.END_ROD;
    }
}
