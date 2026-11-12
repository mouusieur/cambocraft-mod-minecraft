package com.cambocraft.ecology;

import com.cambocraft.util.Tick;
import com.cambocraft.world.CambodiaDimension;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

/**
 * Phénomènes atmosphériques et hydrologiques de la dimension {@code cambocraft:cambodia} (§4.3–4.4) :
 * niveau saisonnier du Grand Lac, temples engloutis, brume matinale, pluies de mousson et orages.
 */
public final class WorldAtmosphere {
    /** Niveau d'étiage du Grand Lac en saison douce (découvre les temples engloutis). */
    public static final int NIVEAU_LAC_ETIAGE = 62;
    /** Niveau intermédiaire en saison chaude. */
    public static final int NIVEAU_LAC_CHAUD = 64;
    /** Niveau de crue maximale en mousson (+6 blocs par rapport à l'étiage). */
    public static final int NIVEAU_LAC_CRUE = 68;

    private WorldAtmosphere() {}

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(WorldAtmosphere::surTickMonde);
    }

    /** Altitude Y de la surface du Grand Lac selon la saison courante. */
    public static int hauteurGrandLac(Season saison) {
        return switch (saison) {
            case SAISON_DOUCE -> NIVEAU_LAC_ETIAGE;
            case SAISON_CHAUDE -> NIVEAU_LAC_CHAUD;
            case MOUSSON -> NIVEAU_LAC_CRUE;
        };
    }

    /**
     * Indique si l'entrée d'un temple englouti du Grand Lac (typiquement située entre Y=63 et Y=66)
     * est émergée et accessible à pied sec pendant la saison donnée.
     */
    public static boolean templeEngloutiAccessible(Season saison, int altitudeEntree) {
        return altitudeEntree >= hauteurGrandLac(saison);
    }

    /** Vrai pendant les premières heures du jour (0 à 2 400 ticks) en saison douce ou en mousson. */
    public static boolean estBrumeMatinale(Season saison, long tempsJour) {
        long heure = Math.floorMod(tempsJour, (long) Tick.UN_DAY);
        return heure <= 2400L && (saison == Season.SAISON_DOUCE || saison == Season.MOUSSON);
    }

    /**
     * Multiplicateur de vitesse de marche sous une pluie battante de mousson (§4.4) :
     * léger ralentissement (-10 %) à découvert, annulé sous un toit ou un abri.
     */
    public static double multiplicateurMarchePluie(Season saison, boolean ilPleut, boolean sousAbri) {
        if (saison == Season.MOUSSON && ilPleut && !sousAbri) {
            return 0.90;
        }
        return 1.00;
    }

    private static void surTickMonde(ServerWorld monde) {
        if (!CambodiaDimension.estCambodge(monde)) return;
        long temps = monde.getTime();
        // À chaque lever du soleil (toutes les 24 000 ticks), ajuste la météo saisonnière et l'indice du riz
        if (temps % Tick.UN_DAY == 0) {
            Season saison = Seasons.saison(temps);
            RiceCycle.calculerRendementSaisonnier(saison, saison == Season.MOUSSON ? 0.90 : 0.70);
            boolean pluie = ((temps / Tick.UN_DAY) % 10) < Math.round(saison.probaPluie * 10);
            boolean orage = pluie && saison == Season.MOUSSON && ((temps / Tick.UN_DAY) % 3 == 0);
            if (pluie) {
                monde.setWeather(0, Tick.UN_DAY, true, orage);
            } else {
                monde.setWeather(Tick.UN_DAY, 0, false, false);
            }
        }
    }
}
