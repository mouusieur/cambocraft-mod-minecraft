package com.cambocraft.server;

import com.cambocraft.network.CamboNetworking;
import com.cambocraft.util.Log;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Branchement des évènements serveur : chargement et sauvegarde de l'état des joueurs, envoi des
 * synchronisations et sauvegarde périodique automatique.
 *
 * <p>Trois moments comptent pour la persistance :</p>
 * <ul>
 *   <li><b>connexion</b> ({@code JOIN}) : l'état est (re)chargé depuis la sauvegarde du monde et le
 *       client reçoit son profil Nen, son journal de quêtes et l'état saisonnier ;</li>
 *   <li><b>déconnexion</b> ({@code LEAVE}) : l'état est écrit dans le monde avant que le joueur ne
 *       soit retiré, puis le cache mémoire est purgé ;</li>
 *   <li><b>arrêt du serveur</b> ({@code SERVER_STOPPING}) : toutes les données encore en cache sont
 *       vidées, ce qui couvre l'arrêt brutal de la session et le passage en veille d'un monde solo.</li>
 * </ul>
 *
 * <p>Une sauvegarde périodique toutes les 5 minutes évite en outre de perdre la progression en cas
 * de plantage du serveur : sans elle, seule la déconnexion propre garantirait la persistance.</p>
 */
public final class CamboServerEvents {
    /** Intervalle de sauvegarde de sécurité, en ticks (5 minutes). */
    private static final int INTERVALLE_SAUVEGARDE_TICKS = 20 * 60 * 5;
    /** Intervalle de régénération de l'énergie spirituelle, en ticks (1 seconde). */
    private static final int INTERVALLE_REGENERATION_TICKS = 20;
    /** Points d'énergie spirituelle récupérés par seconde et par joueur. */
    private static final int REGENERATION_PAR_SECONDE = 2;

    private static int compteurTicks;

    private CamboServerEvents() {}

    /** Enregistre tous les écouteurs serveur du mod (appelé une seule fois à l'initialisation). */
    public static void enregistrer() {
        ServerPlayerEvents.JOIN.register(CamboServerEvents::aLaConnexion);
        ServerPlayerEvents.LEAVE.register(CamboServerEvents::aLaDeconnexion);
        ServerLifecycleEvents.SERVER_STOPPING.register(CamboServerEvents::aLArret);
        ServerTickEvents.END_SERVER_TICK.register(CamboServerEvents::aChaqueTick);
        Log.info("Cambocraft : persistance des joueurs branchée (connexion, déconnexion, arrêt)");
    }

    private static void aLaConnexion(ServerPlayerEntity joueur) {
        MinecraftServer serveur = joueur.getServer();
        if (serveur == null) {
            return;
        }
        // Force le chargement depuis la sauvegarde : le cache est vide au premier accès.
        CamboPlayerData donnees = CamboPlayerData.pour(joueur);
        Log.info("Cambocraft : état joueur chargé pour {} (niveau {}, {} pouvoir(s), {} quête(s) active(s))",
                joueur.getName().getString(), donnees.niveau(), donnees.carnet().nombrePouvoirsDebloques(),
                donnees.quetes().actives().size());
        CamboNetworking.synchroniserTout(joueur, donnees);
    }

    private static void aLaDeconnexion(ServerPlayerEntity joueur) {
        MinecraftServer serveur = joueur.getServer();
        if (serveur == null) {
            return;
        }
        CamboPlayerData.sauvegarderJoueur(joueur, serveur);
        Log.info("Cambocraft : état de {} sauvegardé", joueur.getName().getString());
    }

    private static void aLArret(MinecraftServer serveur) {
        int n = CamboPlayerData.sauvegarderTout(serveur);
        // Dernière sauvegarde des états persistants : le monde est encore ouvert à ce stade.
        try {
            CamboWorldState.obtenir(serveur).markDirty();
        } catch (RuntimeException e) {
            Log.warn("Cambocraft : sauvegarde du monde impossible à l'arrêt : " + e.getMessage());
        }
        Log.info("Cambocraft : {} état(s) joueur sauvegardé(s) avant l'arrêt du serveur", n);
    }

    /**
     * Battement périodique : régénération de l'énergie du Nen chaque seconde et sauvegarde de
     * sécurité toutes les 5 minutes (protège la progression d'un plantage du serveur).
     */
    private static void aChaqueTick(MinecraftServer serveur) {
        int tick = compteurTicks++;
        boolean regenerer = tick % INTERVALLE_REGENERATION_TICKS == 0;
        boolean sauvegarder = tick % INTERVALLE_SAUVEGARDE_TICKS == 0;
        if (!regenerer && !sauvegarder) {
            return;
        }
        for (ServerPlayerEntity joueur : serveur.getPlayerManager().getPlayerList()) {
            CamboPlayerData donnees = CamboPlayerData.pour(joueur);
            if (regenerer) {
                donnees.regenererEnergie(REGENERATION_PAR_SECONDE);
            }
            if (sauvegarder) {
                donnees.sauvegarderSansPurger(serveur);
            }
        }
    }
}
