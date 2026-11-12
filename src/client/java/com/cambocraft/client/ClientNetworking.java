package com.cambocraft.client;

import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.PowerRegistry;
import com.cambocraft.quest.QuestRegistry;
import com.cambocraft.server.CamboPlayerData;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * État client : miroir de l'état serveur reçu par les paquets S2C.
 *
 * <p>Le client ne calcule jamais sa progression lui-même : il affiche ce que le serveur lui a
 * envoyé ({@code SyncProfilNenS2C}, {@code SyncQuetesS2C}, {@code SyncSaisonEtLacS2C}). Cette
 * classe est le point unique où arrivent ces paquets, ce qui évite de disperser l'état dans les
 * écrans.</p>
 */
public final class ClientNetworking {
    private static volatile int niveau = 1;
    private static volatile int renaissance;
    private static volatile int energie;
    private static volatile int energieMax = 100;
    private static volatile List<String> emplacements = List.of("", "", "");
    private static volatile List<String> pouvoirs = List.of();
    private static volatile List<String> quetesActives = List.of();
    private static volatile List<String> quetesTerminees = List.of();
    private static volatile String saison = "saison_douce";
    private static volatile int jourDansSaison;
    private static volatile int niveauLac = 62;
    private static volatile String derniereNotification = "";

    private ClientNetworking() {}

    /** Enregistre les récepteurs S2C (appelé depuis l'initialiseur client). */
    public static void enregistrerRecepteurs() {
        ClientPlayNetworking.registerGlobalReceiver(com.cambocraft.network.CamboPayloads.SyncProfilNenS2C.ID,
                (charge, contexte) -> {
                    niveau = charge.niveauJoueur();
                    renaissance = charge.rangRenaissance();
                    energie = charge.energieActuelle();
                    energieMax = charge.energieMax();
                    emplacements = List.copyOf(charge.emplacementsActifs());
                    pouvoirs = List.copyOf(charge.pouvoirsDebloques().stream().sorted().toList());
                });
        ClientPlayNetworking.registerGlobalReceiver(com.cambocraft.network.CamboPayloads.SyncQuetesS2C.ID,
                (charge, contexte) -> {
                    quetesActives = List.copyOf(charge.actives());
                    quetesTerminees = List.copyOf(charge.terminees());
                });
        ClientPlayNetworking.registerGlobalReceiver(com.cambocraft.network.CamboPayloads.SyncSaisonEtLacS2C.ID,
                (charge, contexte) -> {
                    saison = charge.saisonId();
                    jourDansSaison = charge.jourDansSaison();
                    niveauLac = charge.niveauEauGrandLacY();
                });
        ClientPlayNetworking.registerGlobalReceiver(com.cambocraft.network.CamboPayloads.NotificationRituelS2C.ID,
                (charge, contexte) -> {
                    derniereNotification = charge.cle();
                    if (contexte.player() != null) {
                        contexte.player().sendMessage(Text.translatable(charge.cle()), true);
                    }
                });
    }

    // --- état exposé aux écrans et au HUD -------------------------------------

    public static int niveau() {
        return niveau;
    }

    public static int renaissance() {
        return renaissance;
    }

    public static int energie() {
        return energie;
    }

    public static int energieMax() {
        return energieMax;
    }

    public static List<String> emplacements() {
        return emplacements;
    }

    public static List<String> pouvoirs() {
        return pouvoirs;
    }

    public static List<String> quetesActives() {
        return quetesActives;
    }

    public static List<String> quetesTerminees() {
        return quetesTerminees;
    }

    public static String saison() {
        return saison;
    }

    public static int jourDansSaison() {
        return jourDansSaison;
    }

    public static int niveauLac() {
        return niveauLac;
    }

    public static String derniereNotification() {
        return derniereNotification;
    }

    /** Construit un état de joueur local à partir du miroir, pour les écrans qui en dépendent. */
    public static CamboPlayerData instantaneLocal() {
        CamboPlayerData local = CamboPlayerData.pourAffichage();
        local.carnet().definirEnergieMax(energieMax);
        local.carnet().definirEnergie(energie);
        for (int i = 0; i < Math.min(emplacements.size(), NenNotebook.NB_EMPLACEMENTS_ACTIFS); i++) {
            String id = emplacements.get(i);
            if (id != null && !id.isEmpty()) {
                local.carnet().restaurerPouvoir(id, 1, 0, false);
                local.carnet().restaurerEmplacement(i, id);
            }
        }
        for (String id : pouvoirs) {
            if (PowerRegistry.get(id) != null) {
                local.carnet().restaurerPouvoir(id, 1, 0, false);
            }
        }
        for (String id : quetesActives) {
            local.quetes().restaurerActive(id, 0, 0, null);
        }
        for (String id : quetesTerminees) {
            local.quetes().restaurerTerminee(id);
        }
        local.definirNiveauPourAffichage(niveau, renaissance);
        return local;
    }

    /** Ouvre un écran si le client est prêt (jamais depuis un thread réseau hors jeu). */
    public static void ouvrirEcran(net.minecraft.client.gui.screen.Screen ecran) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(ecran);
        }
    }

    /** Ouvre le Carnet du Nen (touche de raccourci ou objet en main). */
    public static void ouvrirCarnet() {
        ouvrirEcran(new com.cambocraft.client.screen.CarnetDuNenScreen(instantaneLocal()));
        ClientPlayNetworking.send(new com.cambocraft.network.CamboPayloads.OuvrirCarnetC2S());
    }

    /** Ouvre le journal de quêtes. */
    public static void ouvrirJournal() {
        ouvrirEcran(new com.cambocraft.client.screen.JournalDeQuetesScreen(instantaneLocal()));
    }

    /** Ouvre la table khmère (cuisine, soie, sculpture, forge). */
    public static void ouvrirTableKhmere() {
        ouvrirEcran(new com.cambocraft.client.screen.TableKhmereScreen());
    }

    // --- envois C2S -----------------------------------------------------------

    public static void envoyerActivation(int slot) {
        ClientPlayNetworking.send(new com.cambocraft.network.CamboPayloads.ActiverPouvoirC2S(slot, 0L));
    }

    public static void envoyerEquipement(int slot, String pouvoir) {
        ClientPlayNetworking.send(new com.cambocraft.network.CamboPayloads.EquiperPouvoirC2S(slot, pouvoir));
    }

    public static void envoyerEpingle(String queteId) {
        ClientPlayNetworking.send(new com.cambocraft.network.CamboPayloads.ActionMiniJeuC2S(
                "journal", "epingler:" + queteId, 0));
    }

    public static void envoyerAbandon(String queteId) {
        ClientPlayNetworking.send(new com.cambocraft.network.CamboPayloads.ActionMiniJeuC2S(
                "journal", "abandonner:" + queteId, 0));
    }

    /** Réinitialise le miroir (déconnexion). */
    public static void reinitialiser() {
        niveau = 1;
        renaissance = 0;
        energie = 0;
        energieMax = 100;
        emplacements = List.of("", "", "");
        pouvoirs = List.of();
        quetesActives = List.of();
        quetesTerminees = List.of();
        saison = "saison_douce";
        jourDansSaison = 0;
        niveauLac = 62;
        derniereNotification = "";
        if (PowerRegistry.tous().isEmpty()) {
            QuestRegistry.quetes().size();
        }
    }
}
