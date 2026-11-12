package com.cambocraft.world;

import com.cambocraft.progression.ZoneDefinition;
import com.cambocraft.progression.ZoneRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Réseau de Téléporteurs de Pierre du Lotus reliant les 12 zones de {@code cambocraft:cambodia} (§12.2).
 *
 * <p>Toute demande de téléportation est vérifiée côté serveur : niveau requis, boss de la zone
 * précédente vaincu et présence effective dans la dimension.</p>
 */
public final class LotusTeleporter {
    private LotusTeleporter() {}

    /** Renvoie la liste des zones actuellement débloquées pour un joueur donné. */
    public static List<ZoneDefinition> zonesAccessibles(int niveauJoueur, int renaissances, Set<String> bossVaincus) {
        List<ZoneDefinition> res = new ArrayList<>();
        for (ZoneDefinition z : ZoneRegistry.zones()) {
            if (z.estDebloquee(niveauJoueur, renaissances, bossVaincus)) {
                res.add(z);
            }
        }
        return res;
    }

    /** Téléporte le joueur vers la zone demandée si et seulement si elle est débloquée côté serveur. */
    public static boolean teleporterVersZone(
            ServerPlayerEntity joueur,
            String zoneId,
            int niveauJoueur,
            int renaissances,
            Set<String> bossVaincus) {
        ZoneDefinition zone = ZoneRegistry.zone(zoneId);
        if (zone == null) return false;
        if (!zone.estDebloquee(niveauJoueur, renaissances, bossVaincus)) {
            if (joueur != null) {
                joueur.sendMessage(
                        Text.translatable("cambocraft.teleporteur.verrouille", String.valueOf(zone.niveauMin())),
                        true);
            }
            return false;
        }
        if (joueur != null) {
            ServerWorld monde = joueur.getServerWorld();
            if (monde != null && CambodiaDimension.estCambodge(monde)) {
                joueur.teleport(monde, zone.tpX() + 0.5, zone.tpY() + 1.0, zone.tpZ() + 0.5, joueur.getYaw(), joueur.getPitch());
                joueur.sendMessage(Text.translatable("cambocraft.teleporteur.voyage", zone.nom().fr), true);
            }
        }
        return true;
    }
}
