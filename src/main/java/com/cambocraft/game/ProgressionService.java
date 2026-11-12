package com.cambocraft.game;

import com.cambocraft.combat.CombatRegistry;
import com.cambocraft.combat.EnemyDefinition;
import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.Seasons;
import com.cambocraft.entity.BossGuardianEntity;
import com.cambocraft.npc.Faction;
import com.cambocraft.network.CamboNetworking;
import com.cambocraft.quest.QuestDefinition;
import com.cambocraft.quest.QuestRegistry;
import com.cambocraft.server.CamboPlayerData;
import com.cambocraft.util.Log;
import java.util.List;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Progression réelle : expérience gagnée en combat, montée de niveau, boss vaincus, réputation,
 * et avancement automatique des quêtes.
 *
 * <p>C'est le maillon qui manquait entre les formules ({@code ProgressionCurve}, {@code QuestEngine})
 * et le jeu : sans lui, tuer un gardien ne donnait rien, les 85 quêtes ne progressaient jamais
 * d'elles-mêmes et la Renaissance restait théorique.</p>
 */
public final class ProgressionService {
    /** XP accordée par point de vie d'un ennemi vaincu. */
    public static final long XP_PAR_PV = 2L;
    /** XP plancher d'une rencontre, pour qu'un petit ennemi reste intéressant. */
    public static final long XP_MINIMUM = 30L;
    /** Réputation gagnée par boss vaincu. */
    public static final int REPUTATION_PAR_BOSS = 250;
    /** Réputation gagnée par ennemi ordinaire vaincu. */
    public static final int REPUTATION_PAR_ENNEMI = 4;

    private ProgressionService() {}

    /** Branche l'écouteur de mort d'entité. */
    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register(ProgressionService::apresMort);
        Log.info("Cambocraft : progression de combat branchée (XP, quêtes, réputation, boss)");
    }

    private static void apresMort(LivingEntity entite, DamageSource source) {
        if (entite.getWorld() == null) {
            return;
        }
        if (!(entite.getWorld() instanceof ServerWorld monde)) {
            return;
        }
        ServerPlayerEntity tueur = auteur(source);
        if (tueur == null) {
            return;
        }
        CamboPlayerData donnees = CamboPlayerData.pour(tueur);

        boolean estGardien = entite instanceof BossGuardianEntity;
        String profil = estGardien ? ((BossGuardianEntity) entite).profil() : "";
        EnemyDefinition definition = estGardien ? CombatRegistry.get(profil) : null;

        long xp = Math.max(XP_MINIMUM, (long) entite.getMaxHealth() * XP_PAR_PV);
        var etat = donnees.ajouterXp(xp);
        if (etat.niveauxGagnes() > 0) {
            tueur.sendMessage(Text.literal("§6▲ Niveau " + etat.niveau() + " !")
                    .formatted(Formatting.GOLD), true);
            CamboNetworking.notifierRituel(tueur, "cambocraft.riveau.niveau", etat.niveau());
            CamboNetworking.synchroniserProfil(tueur, donnees);
        }

        if (estGardien && !profil.isEmpty()) {
            donnees.marquerBossVaincu(profil);
            Faction faction = factionDe(definition);
            donnees.reputation().ajouter(faction, REPUTATION_PAR_BOSS);
            tueur.sendMessage(Text.literal("§d✦ Boss vaincu : " + profil
                    + " §7(réputation " + faction.id + " +" + REPUTATION_PAR_BOSS + ")"), false);
            CamboNetworking.notifierRituel(tueur, "cambocraft.riveau.boss", 1);
            // Les quêtes dont l'objectif est « vaincre » progressent automatiquement.
            notifierQuetes(donnees, tueur, "vaincre", profil);
        } else {
            donnees.reputation().ajouter(Faction.TONLE, REPUTATION_PAR_ENNEMI);
            notifierQuetes(donnees, tueur, "vaincre", entite.getType().toString());
        }
        CamboNetworking.synchroniserQuetes(tueur, donnees);
    }

    /** Identifie le joueur responsable de la mort (attaque directe ou projectile). */
    private static ServerPlayerEntity auteur(DamageSource source) {
        if (source == null) {
            return null;
        }
        if (source.getAttacker() instanceof ServerPlayerEntity joueur) {
            return joueur;
        }
        if (source.getSource() instanceof ServerPlayerEntity joueur) {
            return joueur;
        }
        return null;
    }

    private static Faction factionDe(EnemyDefinition definition) {
        if (definition == null) {
            return Faction.KOMPONG;
        }
        String zone = definition.id() == null ? "" : definition.id().toLowerCase(java.util.Locale.ROOT);
        if (zone.contains("ly")) {
            return Faction.LY;
        }
        if (zone.contains("srok")) {
            return Faction.SROK;
        }
        if (zone.contains("kompong") || zone.contains("temple") || zone.contains("pyramide")) {
            return Faction.KOMPONG;
        }
        return Faction.TONLE;
    }

    /**
     * Fait avancer toutes les quêtes actives dont une étape correspond à l'objectif et à la cible.
     * Une quête terminée déclenche la récompense d'XP et de réputation.
     */
    public static void notifierQuetes(CamboPlayerData donnees, ServerPlayerEntity joueur,
                                      String objectif, String cible) {
        for (QuestDefinition q : List.copyOf(QuestRegistry.quetes().values())) {
            if (!donnees.quetes().actives().containsKey(q.id())) {
                continue;
            }
            boolean terminee = donnees.quetes().notifierObjectif(q, objectif, cible, 1);
            if (!terminee) {
                continue;
            }
            QuestDefinition.Recompense recompense = q.recompenses();
            if (recompense.xp() > 0) {
                donnees.ajouterXp(recompense.xp());
            }
            if (recompense.reputation() != 0) {
                donnees.reputation().ajouter(q.faction(), recompense.reputation());
            }
            joueur.sendMessage(Text.literal("§a✔ Quête terminée : §f" + q.titre().get("fr_fr")
                    + " §7(+" + recompense.xp() + " XP)"), false);
            CamboNetworking.notifierRituel(joueur, "cambocraft.riveau.quete", 1);
        }
    }

    /** Saison courante du monde d'un joueur, pratique pour les quêtes saisonnières. */
    public static String saisonCourante(ServerPlayerEntity joueur) {
        Season saison = Seasons.saison(joueur.getServerWorld().getTimeOfDay());
        return saison.id;
    }
}
