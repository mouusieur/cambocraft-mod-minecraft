package com.cambocraft.network;

import com.cambocraft.util.Ids;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Identifier;

/**
 * Définition des paquets réseau C2S (Client → Serveur) et S2C (Serveur → Client) de Cambocraft (§2 de DESIGN.md).
 *
 * <p>Chaque paquet C2S passe obligatoirement par {@link ServerPacketValidator} côté serveur avant
 * toute modification d'état.
 */
public final class CamboPayloads {
    public static final Identifier ID_ACTIVER_POUVOIR_C2S = Ids.id("activer_pouvoir_c2s");
    public static final Identifier ID_EQUIPER_POUVOIR_C2S = Ids.id("equiper_pouvoir_c2s");
    public static final Identifier ID_TELEPORTER_LOTUS_C2S = Ids.id("teleporter_lotus_c2s");
    public static final Identifier ID_CHOISIR_DIALOGUE_C2S = Ids.id("choisir_dialogue_c2s");
    public static final Identifier ID_ACTION_MINI_JEU_C2S = Ids.id("action_mini_jeu_c2s");

    public static final Identifier ID_SYNC_PROFIL_NEN_S2C = Ids.id("sync_profil_nen_s2c");
    public static final Identifier ID_SYNC_QUETES_S2C = Ids.id("sync_quetes_s2c");
    public static final Identifier ID_SYNC_SAISON_ET_LAC_S2C = Ids.id("sync_saison_et_lac_s2c");
    public static final Identifier ID_NOTIFICATION_RITUEL_S2C = Ids.id("notification_rituel_s2c");

    private CamboPayloads() {}

    /** Demande client d'activation d'un emplacement actif du Carnet du Nen ({@code 0..2}). */
    public record ActiverPouvoirC2S(int slot, long tickClient) {}

    /** Demande client d'équipement d'un pouvoir débloqué dans un emplacement actif ({@code 0..2}). */
    public record EquiperPouvoirC2S(int slot, String powerId) {}

    /** Demande client de voyage rapide via une Pierre de Téléportation du Lotus. */
    public record TeleporterLotusC2S(String zoneOrigineId, String zoneDestinationId, double distancePierreBlocs) {}

    /** Demande client de sélection d'une branche de dialogue auprès d'un PNJ. */
    public record ChoisirDialogueC2S(String npcId, String noeudId, int indexChoix, double distancePnjBlocs) {}

    /** Demande client d'action dans un mini-jeu (Pradal Serey, Course de pirogues, Danse Apsara). */
    public record ActionMiniJeuC2S(String miniJeuId, String actionId, int valeurPrecisionTicks) {}

    /** Synchronisation serveur → client du Carnet du Nen et de la jauge d'énergie. */
    public record SyncProfilNenS2C(
            int niveauJoueur,
            int rangRenaissance,
            int energieActuelle,
            int energieMax,
            List<String> emplacementsActifs,
            Set<String> pouvoirsDebloques
    ) {}

    /** Synchronisation serveur → client de la saison et du niveau de crue du Grand Lac. */
    public record SyncSaisonEtLacS2C(String saisonId, int jourDansSaison, int niveauEauGrandLacY, double indiceRiz) {}
}
