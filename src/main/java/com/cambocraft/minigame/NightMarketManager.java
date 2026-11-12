package com.cambocraft.minigame;

import com.cambocraft.npc.Economy;
import com.cambocraft.npc.ReputationTier;
import com.cambocraft.util.Tick;
import java.util.List;

/**
 * Gestionnaire du **Marché de nuit aux lanternes** :
 * ouvert chaque nuit de {@code 14000} à {@code 22500} ticks, proposant une sélection rotative
 * de spécialités culinaires, d'étoffes de soie et de reliques rares en Pièces du Lotus.
 */
public final class NightMarketManager {
    public static final long TICK_OUVERTURE = 14000L;
    public static final long TICK_FERMETURE = 22500L;

    public record NightOffer(String itemId, int prixBasePieces, boolean rareteNocturne) {}

    private static final List<NightOffer> ROTATION = List.of(
            new NightOffer("cambocraft:bol_de_riz_beni", 95, true),
            new NightOffer("cambocraft:soie_royale_doree", 80, true),
            new NightOffer("cambocraft:cristal_de_forge_superieur", 110, true),
            new NightOffer("cambocraft:amok", 22, false),
            new NightOffer("cambocraft:lok_lak", 24, false),
            new NightOffer("cambocraft:lanterne_flottante", 12, false)
    );

    private NightMarketManager() {}

    public static boolean estOuvert(long worldTick) {
        long heure = Math.floorMod(worldTick, (long) Tick.UN_DAY);
        return heure >= TICK_OUVERTURE && heure <= TICK_FERMETURE;
    }

    public static List<NightOffer> offresDuSoir() {
        return ROTATION;
    }

    public static int prixOffreNocturne(NightOffer offre, int reputation, double indiceRiz) {
        return Economy.prixAchat(
                offre.prixBasePieces(),
                reputation,
                offre.rareteNocturne() ? 1.0 : indiceRiz);
    }
}
