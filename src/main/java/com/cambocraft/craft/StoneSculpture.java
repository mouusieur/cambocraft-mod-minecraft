package com.cambocraft.craft;

import java.util.List;

/**
 * Sculpture sur pierre khmère sur {@code cambocraft:atelier_de_sculpture} (§7.5) :
 * requiert le Maillet de Sculpteur et le Ciseau de Sculpteur pour façonner le grès khmer
 * et la pierre du lotus en bas-reliefs, balustrades naga, lions gardiens, tours-visages et statuettes.
 */
public final class StoneSculpture {
    public record OeuvreSculptee(String id, String materiauBrut, String blocOuObjetResultat, int coupsDeCiseau, int xpArtisanat) {}

    public static final List<OeuvreSculptee> OEUVRES = List.of(
            new OeuvreSculptee("gres_sculpte", "cambocraft:gres_khmer", "cambocraft:gres_sculpte", 3, 15),
            new OeuvreSculptee("bas_relief", "cambocraft:gres_khmer", "cambocraft:bas_relief", 5, 30),
            new OeuvreSculptee("naga_balustrade", "cambocraft:gres_khmer", "cambocraft:naga_balustrade", 6, 40),
            new OeuvreSculptee("lion_de_pierre", "cambocraft:gres_khmer_poli", "cambocraft:lion_de_pierre", 7, 50),
            new OeuvreSculptee("visage_de_pierre", "cambocraft:gres_khmer_poli", "cambocraft:visage_de_pierre", 8, 65),
            new OeuvreSculptee("statue_de_pierre", "cambocraft:pierre_du_lotus", "cambocraft:statue_de_pierre", 7, 55),
            new OeuvreSculptee("pierre_du_lotus_sculptee", "cambocraft:pierre_du_lotus", "cambocraft:pierre_du_lotus_sculptee", 4, 25),
            new OeuvreSculptee("statuette_de_naga", "cambocraft:bloc_de_gres_brut", "cambocraft:statuette_de_naga", 4, 35),
            new OeuvreSculptee("statuette_d_apsara", "cambocraft:bloc_de_gres_brut", "cambocraft:statuette_d_apsara", 5, 45));

    private StoneSculpture() {}

    public static OeuvreSculptee parId(String id) {
        for (OeuvreSculptee o : OEUVRES) {
            if (o.id.equals(id)) return o;
        }
        return null;
    }
}
