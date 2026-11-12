package com.cambocraft.network;

import com.cambocraft.power.NenNotebook;
import com.cambocraft.world.LotusTeleporter;
import java.util.Objects;
import java.util.Set;

/**
 * Validateur serveur systématique des requêtes réseau C2S (§2 de DESIGN.md) :
 * vérifie la portée maximale d'interaction ({@code <= 6.0} blocs), la cadence anti-spam,
 * la possession effective des pouvoirs et les conditions d'accès aux zones.
 */
public final class ServerPacketValidator {
    public static final double DISTANCE_MAX_INTERACTION_BLOCS = 6.0;
    public static final long DELAI_MIN_ENTRE_PAQUETS_TICKS = 2L;

    public enum ValidationStatus {
        AUTORISE,
        REFUSE_TROP_LOIN,
        REFUSE_CADENCE_EXCESSIVE,
        REFUSE_EMPLACEMENT_INVALIDE,
        REFUSE_POUVOIR_NON_POSSEDE,
        REFUSE_TEMPS_DE_RECHARGE_OU_ENERGIE,
        REFUSE_ZONE_VERROUILLEE
    }

    private ServerPacketValidator() {}

    public static ValidationStatus validerActivationPouvoir(
            CamboPayloads.ActiverPouvoirC2S req,
            NenNotebook carnet,
            long tickServeur,
            long dernierPaquetTick
    ) {
        Objects.requireNonNull(req, "req");
        Objects.requireNonNull(carnet, "carnet");
        if (dernierPaquetTick >= 0 && (tickServeur - dernierPaquetTick) < DELAI_MIN_ENTRE_PAQUETS_TICKS) {
            return ValidationStatus.REFUSE_CADENCE_EXCESSIVE;
        }
        if (req.slot() < 0 || req.slot() >= NenNotebook.NB_EMPLACEMENTS_ACTIFS) {
            return ValidationStatus.REFUSE_EMPLACEMENT_INVALIDE;
        }
        if (carnet.pouvoirEquipe(req.slot()) == null) {
            return ValidationStatus.REFUSE_POUVOIR_NON_POSSEDE;
        }
        return carnet.activerPouvoir(req.slot(), tickServeur)
                ? ValidationStatus.AUTORISE
                : ValidationStatus.REFUSE_TEMPS_DE_RECHARGE_OU_ENERGIE;
    }

    public static ValidationStatus validerEquipementPouvoir(
            CamboPayloads.EquiperPouvoirC2S req,
            NenNotebook carnet
    ) {
        Objects.requireNonNull(req, "req");
        Objects.requireNonNull(carnet, "carnet");
        if (req.slot() < 0 || req.slot() >= NenNotebook.NB_EMPLACEMENTS_ACTIFS) {
            return ValidationStatus.REFUSE_EMPLACEMENT_INVALIDE;
        }
        if (req.powerId() != null && !carnet.possede(req.powerId())) {
            return ValidationStatus.REFUSE_POUVOIR_NON_POSSEDE;
        }
        return carnet.equiper(req.slot(), req.powerId())
                ? ValidationStatus.AUTORISE
                : ValidationStatus.REFUSE_EMPLACEMENT_INVALIDE;
    }

    public static ValidationStatus validerTeleportationLotus(
            CamboPayloads.TeleporterLotusC2S req,
            int niveauJoueur,
            int rangRenaissance,
            Set<String> bossVaincus,
            Set<String> pierresDecouvertes
    ) {
        Objects.requireNonNull(req, "req");
        if (req.distancePierreBlocs() < 0.0 || req.distancePierreBlocs() > DISTANCE_MAX_INTERACTION_BLOCS) {
            return ValidationStatus.REFUSE_TROP_LOIN;
        }
        if (pierresDecouvertes == null
                || !pierresDecouvertes.contains(req.zoneOrigineId())
                || !pierresDecouvertes.contains(req.zoneDestinationId())) {
            return ValidationStatus.REFUSE_ZONE_VERROUILLEE;
        }
        boolean ok = LotusTeleporter.teleporterVersZone(
                null,
                req.zoneDestinationId(),
                niveauJoueur,
                rangRenaissance,
                bossVaincus);
        return ok ? ValidationStatus.AUTORISE : ValidationStatus.REFUSE_ZONE_VERROUILLEE;
    }

    public static ValidationStatus validerInteractionPnj(CamboPayloads.ChoisirDialogueC2S req) {
        Objects.requireNonNull(req, "req");
        if (req.distancePnjBlocs() < 0.0 || req.distancePnjBlocs() > DISTANCE_MAX_INTERACTION_BLOCS) {
            return ValidationStatus.REFUSE_TROP_LOIN;
        }
        return ValidationStatus.AUTORISE;
    }
}
