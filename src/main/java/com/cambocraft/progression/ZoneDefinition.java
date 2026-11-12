package com.cambocraft.progression;

import com.cambocraft.util.JsonUtil;
import com.cambocraft.util.Localized;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Set;

/**
 * Définition d'une des 12 zones de progression (§12.2), chargée depuis
 * {@code data/cambocraft/cambo/zones/<id>.json}.
 */
public record ZoneDefinition(
        String id,
        int ordre,
        Localized nom,
        int niveauMin,
        int niveauMax,
        List<String> biomes,
        String bossRequis,
        String bossId,
        int tpX,
        int tpY,
        int tpZ,
        List<String> ennemis) {

    /**
     * Vérifie si un joueur a débloqué l'accès à cette zone et à son Téléporteur de Lotus :
     * il doit avoir atteint le niveau minimal ET avoir vaincu le boss de la zone précédente.
     * Pour la zone 12 (Royaume des Esprits), une Renaissance (ou niveau 200 + Gardien vaincu) est requise.
     */
    public boolean estDebloquee(int niveauJoueur, int renaissances, Set<String> bossVaincus) {
        if (ordre == 12) {
            boolean bossOk = bossRequis == null || bossRequis.isEmpty() || bossVaincus.contains(bossRequis);
            return bossOk && (renaissances >= 1 || niveauJoueur >= 200);
        }
        if (niveauJoueur < niveauMin && renaissances == 0) {
            return false;
        }
        if (bossRequis != null && !bossRequis.isEmpty() && !bossVaincus.contains(bossRequis)) {
            return false;
        }
        return true;
    }

    public static ZoneDefinition fromJson(String id, JsonObject o) {
        JsonObject tp = JsonUtil.obj(o, "teleporteur");
        String req = JsonUtil.str(o, "boss_requis", "");
        return new ZoneDefinition(
                id,
                JsonUtil.integer(o, "ordre", 1),
                Localized.fromJson(o.get("nom"), id),
                JsonUtil.integer(o, "niveau_min", 1),
                JsonUtil.integer(o, "niveau_max", 15),
                JsonUtil.strings(o, "biomes"),
                req.isEmpty() ? null : req,
                JsonUtil.str(o, "boss_id", ""),
                JsonUtil.integer(tp, "x", 0),
                JsonUtil.integer(tp, "y", 72),
                JsonUtil.integer(tp, "z", 0),
                JsonUtil.strings(o, "ennemis"));
    }
}
