package com.cambocraft.game;

import com.cambocraft.power.InvocationsPouvoir;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.PassifsPouvoir;
import com.cambocraft.power.ZonesPouvoir;
import com.cambocraft.server.CamboPlayerData;
import com.cambocraft.util.Log;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Entretien des pouvoirs du Nen : régénération d'énergie, zones persistantes, invocations et
 * effets passifs.
 *
 * <p>Le lancement d'un pouvoir est instantané (voir {@code PowerEffects}), mais beaucoup d'effets
 * durent : une zone frappe à chaque tick, un allié invoqué vit trente secondes, un passif
 * s'applique en permanence. Ce service est le battement de cœur qui fait vivre ces effets, et
 * qui rend l'énergie aux joueurs pour qu'ils puissent relancer leurs pouvoirs.</p>
 */
public final class PowerService {
    /** Énergie rendue par seconde (20 ticks). */
    public static final int ENERGIE_PAR_SECONDE = 4;
    /** Cadence de rafraîchissement des passifs, en ticks. */
    public static final int CADENCE_PASSIFS = 20;

    private PowerService() {}

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(PowerService::surTickServeur);
        Log.info("Cambocraft : entretien des pouvoirs branché (énergie, zones, invocations, passifs)");
    }

    private static void surTickServeur(MinecraftServer serveur) {
        if (serveur == null) {
            return;
        }
        boolean seconde = serveur.getTicks() % 20 == 0;
        boolean passifs = serveur.getTicks() % CADENCE_PASSIFS == 0;

        for (Object monde : serveur.getWorlds()) {
            if (monde instanceof ServerWorld serveurMonde) {
                ZonesPouvoir.avancer(serveurMonde);
            }
        }
        InvocationsPouvoir.avancer(serveur);

        for (ServerPlayerEntity joueur : serveur.getPlayerManager().getPlayerList()) {
            CamboPlayerData donnees = CamboPlayerData.pour(joueur);
            if (donnees == null) {
                continue;
            }
            NenNotebook carnet = donnees.carnet();
            if (seconde) {
                // L'énergie revient avec le temps : sans cela, un pouvoir à 50 d'énergie ne
                // pourrait être lancé qu'une fois par partie.
                carnet.regenererEnergie(ENERGIE_PAR_SECONDE);
            }
            if (passifs) {
                PassifsPouvoir.rafraichir(joueur, carnet);
            }
        }
    }

    /** Nombre de zones et d'invocations actives : exposé pour {@code /cambocraft statut}. */
    public static String etat() {
        return ZonesPouvoir.actives() + " zone(s), " + InvocationsPouvoir.actives() + " invocation(s)";
    }
}
