package com.cambocraft.network;

import com.cambocraft.util.Ids;
import java.util.List;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Définition des paquets réseau C2S (Client → Serveur) et S2C (Serveur → Client) de Cambocraft (§2 de DESIGN.md).
 *
 * <p>Chaque paquet C2S passe obligatoirement par {@link ServerPacketValidator} côté serveur avant
 * toute modification d'état.</p>
 *
 * <p>Depuis la phase 10, chaque paquet <b>est</b> un {@link CustomPayload} : les enregistrements
 * ci-dessous portent leur propre identifiant et leur codec de lecture/écriture, ce qui les rend
 * réellement transmissibles (et non plus seulement décrits).</p>
 */
public final class CamboPayloads {
    public static final Identifier ID_ACTIVER_POUVOIR_C2S = Ids.id("activer_pouvoir_c2s");
    public static final Identifier ID_EQUIPER_POUVOIR_C2S = Ids.id("equiper_pouvoir_c2s");
    public static final Identifier ID_TELEPORTER_LOTUS_C2S = Ids.id("teleporter_lotus_c2s");
    public static final Identifier ID_CHOISIR_DIALOGUE_C2S = Ids.id("choisir_dialogue_c2s");
    public static final Identifier ID_ACTION_MINI_JEU_C2S = Ids.id("action_mini_jeu_c2s");
    public static final Identifier ID_OUVRIR_CARNET_C2S = Ids.id("ouvrir_carnet_c2s");

    public static final Identifier ID_SYNC_PROFIL_NEN_S2C = Ids.id("sync_profil_nen_s2c");
    public static final Identifier ID_SYNC_QUETES_S2C = Ids.id("sync_quetes_s2c");
    public static final Identifier ID_SYNC_SAISON_ET_LAC_S2C = Ids.id("sync_saison_et_lac_s2c");
    public static final Identifier ID_NOTIFICATION_RITUEL_S2C = Ids.id("notification_rituel_s2c");

    private static final int TAILLE_CHAINE = 256;

    private CamboPayloads() {}

    /** Demande client d'activation d'un emplacement actif du Carnet du Nen ({@code 0..2}). */
    public record ActiverPouvoirC2S(int slot, long tickClient) implements CustomPayload {
        public static final CustomPayload.Id<ActiverPouvoirC2S> ID = new CustomPayload.Id<>(ID_ACTIVER_POUVOIR_C2S);

        public ActiverPouvoirC2S(PacketByteBuf buf) {
            this(buf.readVarInt(), buf.readVarLong());
        }

        public static final PacketCodec<RegistryByteBuf, ActiverPouvoirC2S> CODEC = codecTerrain();

        private static PacketCodec<RegistryByteBuf, ActiverPouvoirC2S> codecTerrain() {
            return PacketCodec.of((valeur, buf) -> {
                if (buf != null) {
                    buf.writeVarInt(valeur.slot());
                    buf.writeVarLong(valeur.tickClient());
                }
            }, ActiverPouvoirC2S::new);
        }

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Demande client d'équipement d'un pouvoir débloqué dans un emplacement actif ({@code 0..2}). */
    public record EquiperPouvoirC2S(int slot, String powerId) implements CustomPayload {
        public static final CustomPayload.Id<EquiperPouvoirC2S> ID = new CustomPayload.Id<>(ID_EQUIPER_POUVOIR_C2S);

        public EquiperPouvoirC2S(PacketByteBuf buf) {
            this(buf.readVarInt(), buf.readString(TAILLE_CHAINE));
        }

        public static final PacketCodec<RegistryByteBuf, EquiperPouvoirC2S> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeVarInt(valeur.slot());
                        buf.writeString(valeur.powerId() == null ? "" : valeur.powerId());
                    }
                },
                EquiperPouvoirC2S::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Demande client de voyage rapide via une Pierre de Téléportation du Lotus. */
    public record TeleporterLotusC2S(String zoneOrigineId, String zoneDestinationId, double distancePierreBlocs)
            implements CustomPayload {
        public static final CustomPayload.Id<TeleporterLotusC2S> ID = new CustomPayload.Id<>(ID_TELEPORTER_LOTUS_C2S);

        public TeleporterLotusC2S(PacketByteBuf buf) {
            this(buf.readString(TAILLE_CHAINE), buf.readString(TAILLE_CHAINE), buf.readDouble());
        }

        public static final PacketCodec<RegistryByteBuf, TeleporterLotusC2S> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeString(valeur.zoneOrigineId());
                        buf.writeString(valeur.zoneDestinationId());
                        buf.writeDouble(valeur.distancePierreBlocs());
                    }
                },
                TeleporterLotusC2S::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Demande client de sélection d'une branche de dialogue auprès d'un PNJ. */
    public record ChoisirDialogueC2S(String npcId, String noeudId, int indexChoix, double distancePnjBlocs)
            implements CustomPayload {
        public static final CustomPayload.Id<ChoisirDialogueC2S> ID = new CustomPayload.Id<>(ID_CHOISIR_DIALOGUE_C2S);

        public ChoisirDialogueC2S(PacketByteBuf buf) {
            this(buf.readString(TAILLE_CHAINE), buf.readString(TAILLE_CHAINE), buf.readVarInt(), buf.readDouble());
        }

        public static final PacketCodec<RegistryByteBuf, ChoisirDialogueC2S> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeString(valeur.npcId());
                        buf.writeString(valeur.noeudId());
                        buf.writeVarInt(valeur.indexChoix());
                        buf.writeDouble(valeur.distancePnjBlocs());
                    }
                },
                ChoisirDialogueC2S::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Demande client d'action dans un mini-jeu (Pradal Serey, Course de pirogues, Danse Apsara). */
    public record ActionMiniJeuC2S(String miniJeuId, String actionId, int valeurPrecisionTicks)
            implements CustomPayload {
        public static final CustomPayload.Id<ActionMiniJeuC2S> ID = new CustomPayload.Id<>(ID_ACTION_MINI_JEU_C2S);

        public ActionMiniJeuC2S(PacketByteBuf buf) {
            this(buf.readString(TAILLE_CHAINE), buf.readString(TAILLE_CHAINE), buf.readVarInt());
        }

        public static final PacketCodec<RegistryByteBuf, ActionMiniJeuC2S> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeString(valeur.miniJeuId());
                        buf.writeString(valeur.actionId());
                        buf.writeVarInt(valeur.valeurPrecisionTicks());
                    }
                },
                ActionMiniJeuC2S::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Demande client d'ouverture du Carnet du Nen (touche de raccourci ou objet en main). */
    public record OuvrirCarnetC2S() implements CustomPayload {
        public static final CustomPayload.Id<OuvrirCarnetC2S> ID = new CustomPayload.Id<>(ID_OUVRIR_CARNET_C2S);
        public static final PacketCodec<RegistryByteBuf, OuvrirCarnetC2S> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    // Aucun champ : l'ouverture ne transporte aucune donnée.
                },
                buf -> new OuvrirCarnetC2S());

        public OuvrirCarnetC2S(PacketByteBuf buf) {
            this();
        }

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Synchronisation serveur → client du Carnet du Nen et de la jauge d'énergie. */
    public record SyncProfilNenS2C(
            int niveauJoueur,
            int rangRenaissance,
            int energieActuelle,
            int energieMax,
            List<String> emplacementsActifs,
            Set<String> pouvoirsDebloques
    ) implements CustomPayload {
        public static final CustomPayload.Id<SyncProfilNenS2C> ID = new CustomPayload.Id<>(ID_SYNC_PROFIL_NEN_S2C);

        /** Reconstruction depuis le réseau, champ par champ. */
        private SyncProfilNenS2C(PacketByteBuf buf, int marqueur) {
            this(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                    NetworkLists.lireListe(buf), new java.util.LinkedHashSet<>(NetworkLists.lireListe(buf)));
        }

        /** Point d'entree du codec : la fabrique ne peut pas viser directement un constructeur. */
        public static SyncProfilNenS2C depuisReseau(PacketByteBuf buf) {
            return new SyncProfilNenS2C(buf, 0);
        }

        public static final PacketCodec<RegistryByteBuf, SyncProfilNenS2C> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeVarInt(valeur.niveauJoueur());
                        buf.writeVarInt(valeur.rangRenaissance());
                        buf.writeVarInt(valeur.energieActuelle());
                        buf.writeVarInt(valeur.energieMax());
                        NetworkLists.ecrireListe(buf, valeur.emplacementsActifs());
                        NetworkLists.ecrireListe(buf, valeur.pouvoirsDebloques().stream().sorted().toList());
                    }
                },
                SyncProfilNenS2C::depuisReseau);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Synchronisation serveur → client du journal de quêtes (actives et terminées). */
    public record SyncQuetesS2C(List<String> actives, List<String> terminees) implements CustomPayload {
        public static final CustomPayload.Id<SyncQuetesS2C> ID = new CustomPayload.Id<>(ID_SYNC_QUETES_S2C);

        public SyncQuetesS2C(PacketByteBuf buf) {
            this(NetworkLists.lireListe(buf), NetworkLists.lireListe(buf));
        }

        public static final PacketCodec<RegistryByteBuf, SyncQuetesS2C> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        NetworkLists.ecrireListe(buf, valeur.actives());
                        NetworkLists.ecrireListe(buf, valeur.terminees());
                    }
                },
                SyncQuetesS2C::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Synchronisation serveur → client de la saison et du niveau de crue du Grand Lac. */
    public record SyncSaisonEtLacS2C(String saisonId, int jourDansSaison, int niveauEauGrandLacY, double indiceRiz)
            implements CustomPayload {
        public static final CustomPayload.Id<SyncSaisonEtLacS2C> ID = new CustomPayload.Id<>(ID_SYNC_SAISON_ET_LAC_S2C);

        public SyncSaisonEtLacS2C(PacketByteBuf buf) {
            this(buf.readString(64), buf.readVarInt(), buf.readVarInt(), buf.readDouble());
        }

        public static final PacketCodec<RegistryByteBuf, SyncSaisonEtLacS2C> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeString(valeur.saisonId());
                        buf.writeVarInt(valeur.jourDansSaison());
                        buf.writeVarInt(valeur.niveauEauGrandLacY());
                        buf.writeDouble(valeur.indiceRiz());
                    }
                },
                SyncSaisonEtLacS2C::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** Notification serveur → client d'un rite, d'une bénédiction ou d'une montée de niveau. */
    public record NotificationRituelS2C(String cle, int valeur) implements CustomPayload {
        public static final CustomPayload.Id<NotificationRituelS2C> ID =
                new CustomPayload.Id<>(ID_NOTIFICATION_RITUEL_S2C);

        public NotificationRituelS2C(PacketByteBuf buf) {
            this(buf.readString(TAILLE_CHAINE), buf.readVarInt());
        }

        public static final PacketCodec<RegistryByteBuf, NotificationRituelS2C> CODEC = PacketCodec.of(
                (valeur, buf) -> {
                    if (buf != null) {
                        buf.writeString(valeur.cle());
                        buf.writeVarInt(valeur.valeur());
                    }
                },
                NotificationRituelS2C::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
