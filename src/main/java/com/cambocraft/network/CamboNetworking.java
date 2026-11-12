package com.cambocraft.network;

import com.cambocraft.ecology.Season;
import com.cambocraft.ecology.Seasons;
import com.cambocraft.ecology.WorldAtmosphere;
import com.cambocraft.npc.Faction;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.server.CamboPlayerData;
import com.cambocraft.util.Log;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Couche réseau serveur : enregistrement des paquets, validation et synchronisation.
 *
 * <p>Chaque paquet C2S est d'abord confronté à {@link ServerPacketValidator} (cadence, portée,
 * possession du pouvoir), puis appliqué à l'état persistant du joueur. Aucun client ne peut donc
 * modifier un état qu'il ne possède pas.</p>
 *
 * <p>Les paquets S2C poussent vers le client le profil du Nen, le journal de quêtes et la saison :
 * c'est ce qui alimente le HUD, le Carnet du Nen et l'écran de quêtes, sans que le client n'ait à
 * interroger le serveur par commande.</p>
 */
public final class CamboNetworking {
    /** Dernier tick d'activation par joueur : sert à l'anti-spam du validateur. */
    private static final java.util.Map<java.util.UUID, Long> DERNIER_PAQUET = new java.util.HashMap<>();

    private CamboNetworking() {}

    /** Déclare les codecs des deux sens. À appeler une fois, au démarrage du mod. */
    public static void enregistrerTypes() {
        PayloadTypeRegistry.playC2S().register(CamboPayloads.ActiverPouvoirC2S.ID,
                CamboPayloads.ActiverPouvoirC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(CamboPayloads.EquiperPouvoirC2S.ID,
                CamboPayloads.EquiperPouvoirC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(CamboPayloads.TeleporterLotusC2S.ID,
                CamboPayloads.TeleporterLotusC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(CamboPayloads.ChoisirDialogueC2S.ID,
                CamboPayloads.ChoisirDialogueC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(CamboPayloads.ActionMiniJeuC2S.ID,
                CamboPayloads.ActionMiniJeuC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(CamboPayloads.OuvrirCarnetC2S.ID,
                CamboPayloads.OuvrirCarnetC2S.CODEC);

        PayloadTypeRegistry.playS2C().register(CamboPayloads.SyncProfilNenS2C.ID,
                CamboPayloads.SyncProfilNenS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(CamboPayloads.SyncQuetesS2C.ID,
                CamboPayloads.SyncQuetesS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(CamboPayloads.SyncSaisonEtLacS2C.ID,
                CamboPayloads.SyncSaisonEtLacS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(CamboPayloads.NotificationRituelS2C.ID,
                CamboPayloads.NotificationRituelS2C.CODEC);
        Log.info("Cambocraft : 10 paquets réseau enregistrés (6 C2S, 4 S2C)");
    }

    /** Branche les récepteurs serveur des paquets C2S. */
    public static void enregistrerRecepteurs() {
        ServerPlayNetworking.registerGlobalReceiver(CamboPayloads.ActiverPouvoirC2S.ID,
                (charge, contexte) -> activerPouvoir(contexte.player(), charge, contexte));
        ServerPlayNetworking.registerGlobalReceiver(CamboPayloads.EquiperPouvoirC2S.ID,
                (charge, contexte) -> equiperPouvoir(contexte.player(), charge));
        ServerPlayNetworking.registerGlobalReceiver(CamboPayloads.TeleporterLotusC2S.ID,
                (charge, contexte) -> teleporter(contexte.player(), charge));
        ServerPlayNetworking.registerGlobalReceiver(CamboPayloads.ChoisirDialogueC2S.ID,
                (charge, contexte) -> choisirDialogue(contexte.player(), charge));
        ServerPlayNetworking.registerGlobalReceiver(CamboPayloads.ActionMiniJeuC2S.ID,
                (charge, contexte) -> actionMiniJeu(contexte.player(), charge));
        ServerPlayNetworking.registerGlobalReceiver(CamboPayloads.OuvrirCarnetC2S.ID,
                (charge, contexte) -> synchroniserTout(contexte.player(),
                        CamboPlayerData.pour(contexte.player())));
        Log.info("Cambocraft : récepteurs réseau C2S branchés");
    }

    // --- C2S -----------------------------------------------------------------

    private static void activerPouvoir(ServerPlayerEntity joueur, CamboPayloads.ActiverPouvoirC2S requete,
                                       ServerPlayNetworking.Context contexte) {
        CamboPlayerData donnees = CamboPlayerData.pour(joueur);
        long tick = joueur.getServerWorld().getTime();
        long dernier = DERNIER_PAQUET.getOrDefault(joueur.getUuid(), -1L);
        ServerPacketValidator.ValidationStatus statut = ServerPacketValidator.validerActivationPouvoir(
                requete, donnees.carnet(), tick, dernier);
        DERNIER_PAQUET.put(joueur.getUuid(), tick);
        if (statut != ServerPacketValidator.ValidationStatus.AUTORISE) {
            refuser(joueur, statut);
            return;
        }
        String pouvoir = donnees.carnet().pouvoirEquipe(requete.slot());
        joueur.sendMessage(Text.translatable("cambocraft.pouvoir.active", String.valueOf(pouvoir)), true);
        synchroniserProfil(joueur, donnees);
    }

    private static void equiperPouvoir(ServerPlayerEntity joueur, CamboPayloads.EquiperPouvoirC2S requete) {
        CamboPlayerData donnees = CamboPlayerData.pour(joueur);
        ServerPacketValidator.ValidationStatus statut = ServerPacketValidator.validerEquipementPouvoir(
                requete, donnees.carnet());
        if (statut != ServerPacketValidator.ValidationStatus.AUTORISE) {
            refuser(joueur, statut);
            return;
        }
        synchroniserProfil(joueur, donnees);
    }

    private static void teleporter(ServerPlayerEntity joueur, CamboPayloads.TeleporterLotusC2S requete) {
        CamboPlayerData donnees = CamboPlayerData.pour(joueur);
        ServerPacketValidator.ValidationStatus statut = ServerPacketValidator.validerTeleportationLotus(
                requete, donnees.niveau(), donnees.renaissances(), donnees.bossVaincus(),
                donnees.pierresDecouvertes());
        if (statut != ServerPacketValidator.ValidationStatus.AUTORISE) {
            refuser(joueur, statut);
            return;
        }
        donnees.marquerZoneVisitee(requete.zoneDestinationId());
        synchroniserSaison(joueur);
    }

    private static void choisirDialogue(ServerPlayerEntity joueur, CamboPayloads.ChoisirDialogueC2S requete) {
        if (ServerPacketValidator.validerInteractionPnj(requete)
                != ServerPacketValidator.ValidationStatus.AUTORISE) {
            refuser(joueur, ServerPacketValidator.ValidationStatus.REFUSE_TROP_LOIN);
            return;
        }
        var arbre = com.cambocraft.npc.NpcRegistry.dialogue(requete.npcId());
        if (arbre == null) {
            return;
        }
        com.cambocraft.npc.ArtisanDialogueService.publier(joueur, arbre, requete.noeudId(), 0,
                CamboPlayerData.pour(joueur).quetes().terminees());
    }

    private static void actionMiniJeu(ServerPlayerEntity joueur, CamboPayloads.ActionMiniJeuC2S requete) {
        // Les mini-jeux valident la précision des entrées : une valeur hors bornes est rejetée
        // plutôt que propagée au moteur.
        if (requete.valeurPrecisionTicks() < 0 || requete.valeurPrecisionTicks() > 72000) {
            refuser(joueur, ServerPacketValidator.ValidationStatus.REFUSE_CADENCE_EXCESSIVE);
            return;
        }
        if (!requete.miniJeuId().equals("pradal_serey")
                && !requete.miniJeuId().equals("course_pirogue")
                && !requete.miniJeuId().equals("danse_apsara")) {
            return;
        }
        joueur.sendMessage(Text.translatable("cambocraft.minijeu.action", requete.actionId()), true);
    }

    private static void refuser(ServerPlayerEntity joueur, ServerPacketValidator.ValidationStatus statut) {
        joueur.sendMessage(Text.translatable("cambocraft.reseau.refuse." + statut.name().toLowerCase()), true);
    }

    // --- S2C -----------------------------------------------------------------

    /** Envoie au joueur l'ensemble de son état (connexion, ouverture du carnet). */
    public static void synchroniserTout(ServerPlayerEntity joueur, CamboPlayerData donnees) {
        if (donnees == null) {
            return;
        }
        synchroniserProfil(joueur, donnees);
        synchroniserQuetes(joueur, donnees);
        synchroniserSaison(joueur);
    }

    /** Pousse le profil du Nen : niveau, Renaissance, énergie, emplacements, collection. */
    public static void synchroniserProfil(ServerPlayerEntity joueur, CamboPlayerData donnees) {
        NenNotebook carnet = donnees.carnet();
        List<String> emplacements = new ArrayList<>(NenNotebook.NB_EMPLACEMENTS_ACTIFS);
        for (int i = 0; i < NenNotebook.NB_EMPLACEMENTS_ACTIFS; i++) {
            String p = carnet.pouvoirEquipe(i);
            emplacements.add(p == null ? "" : p);
        }
        Set<String> pouvoirs = new LinkedHashSet<>(carnet.collection().keySet());
        ServerPlayNetworking.send(joueur, new CamboPayloads.SyncProfilNenS2C(
                donnees.niveau(), donnees.renaissances(), carnet.energieActuelle(), carnet.energieMax(),
                List.copyOf(emplacements), Set.copyOf(pouvoirs)));
    }

    /** Pousse le journal de quêtes : quêtes actives et quêtes terminées. */
    public static void synchroniserQuetes(ServerPlayerEntity joueur, CamboPlayerData donnees) {
        ServerPlayNetworking.send(joueur, new CamboPayloads.SyncQuetesS2C(
                new ArrayList<>(donnees.quetes().actives().keySet()),
                new ArrayList<>(donnees.quetes().terminees())));
    }

    /** Pousse la saison courante et le niveau de crue du Grand Lac. */
    public static void synchroniserSaison(ServerPlayerEntity joueur) {
        long temps = joueur.getServerWorld().getTimeOfDay();
        Season saison = Seasons.saison(temps);
        ServerPlayNetworking.send(joueur, new CamboPayloads.SyncSaisonEtLacS2C(
                saison.id, Seasons.jourDansSaison(temps), WorldAtmosphere.hauteurGrandLac(saison), 1.0));
    }

    /** Notifie un rite (montée de niveau, boss vaincu, bénédiction) au client. */
    public static void notifierRituel(ServerPlayerEntity joueur, String cle, int valeur) {
        ServerPlayNetworking.send(joueur, new CamboPayloads.NotificationRituelS2C(cle, valeur));
    }

    /** Réputation du joueur par faction, sous forme texte (affichage du HUD). */
    public static String resumeReputation(CamboPlayerData donnees) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Faction, Integer> e : donnees.reputation().tout().entrySet()) {
            if (sb.length() > 0) {
                sb.append(" · ");
            }
            sb.append(e.getKey().id).append(' ').append(e.getValue());
        }
        return sb.toString();
    }

    /** Envoi groupé à tous les joueurs connectés (annonce globale). */
    public static void annoncer(MinecraftServer serveur, String cle, int valeur) {
        for (ServerPlayerEntity joueur : serveur.getPlayerManager().getPlayerList()) {
            notifierRituel(joueur, cle, valeur);
        }
    }

    /** Récepteur utilitaire conservé pour la signature Fabric (connexion réseau). */
    public static void surConnexion(ServerPlayNetworkHandler gestionnaire, PacketSender expediteur,
                                   MinecraftServer serveur) {
        ServerPlayerEntity joueur = gestionnaire.player;
        if (joueur != null) {
            synchroniserTout(joueur, CamboPlayerData.pour(joueur));
        }
    }
}
