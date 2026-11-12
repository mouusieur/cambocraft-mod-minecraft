package com.cambocraft.server;

import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.RiceBowlGacha;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * État Cambocraft d'un joueur (carnet du Nen, compteurs de pity, progression), en mémoire serveur.
 *
 * <p>Une persistance NBT complète serait la suite logique ; en attendant, cet état survit aux
 * reconnexions tant que le serveur tourne, ce qui rend le gacha et le carnet utilisables.</p>
 */
public final class CamboPlayerData {
    private static final Map<UUID, CamboPlayerData> DONNEES = new HashMap<>();

    private final NenNotebook carnet = new NenNotebook();
    private final RiceBowlGacha.PityState pity = new RiceBowlGacha.PityState();
    private int niveau = 1;
    private long xp = 0L;

    private CamboPlayerData() {}

    public static CamboPlayerData pour(ServerPlayerEntity joueur) {
        return DONNEES.computeIfAbsent(joueur.getUuid(), uuid -> new CamboPlayerData());
    }

    public static void oublier(UUID uuid) {
        DONNEES.remove(uuid);
    }

    public NenNotebook carnet() {
        return carnet;
    }

    public RiceBowlGacha.PityState pity() {
        return pity;
    }

    public int niveau() {
        return niveau;
    }

    public long xp() {
        return xp;
    }
}
