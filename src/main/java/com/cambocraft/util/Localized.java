package com.cambocraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** Texte bilingue (français et anglais) lu depuis les données. */
public final class Localized {
    public static final Localized EMPTY = new Localized("", "");

    public final String fr;
    public final String en;

    public Localized(String fr, String en) {
        this.fr = fr == null ? "" : fr;
        this.en = en == null ? "" : en;
    }

    /** Renvoie le texte dans la langue demandée, avec repli sur l'autre langue. */
    public String get(String lang) {
        boolean french = lang != null && lang.startsWith("fr");
        String first = french ? fr : en;
        String second = french ? en : fr;
        return first.isEmpty() ? second : first;
    }

    public String fr() {
        return fr;
    }

    public String en() {
        return en;
    }

    public boolean isEmpty() {
        return fr.isEmpty() && en.isEmpty();
    }

    public static Localized fromJson(JsonElement e) {
        if (e == null || e.isJsonNull()) return EMPTY;
        if (e.isJsonPrimitive()) {
            String s = e.getAsString();
            return new Localized(s, s);
        }
        JsonObject o = e.getAsJsonObject();
        return new Localized(JsonUtil.str(o, "fr", ""), JsonUtil.str(o, "en", ""));
    }

    public static Localized fromJson(JsonElement e, String fallback) {
        Localized loc = fromJson(e);
        if (loc.isEmpty()) {
            return new Localized(fallback, fallback);
        }
        return loc;
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("fr", fr);
        o.addProperty("en", en);
        return o;
    }
}
