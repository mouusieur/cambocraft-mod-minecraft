package com.cambocraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lecture tolérante des JSON de données : une clé absente donne une valeur par défaut. */
public final class JsonUtil {
    private JsonUtil() {}

    public static boolean has(JsonObject o, String key) {
        return o != null && o.has(key) && !o.get(key).isJsonNull();
    }

    public static String str(JsonObject o, String key, String def) {
        return has(o, key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : def;
    }

    public static int integer(JsonObject o, String key, int def) {
        try {
            return has(o, key) ? o.get(key).getAsInt() : def;
        } catch (RuntimeException e) {
            return def;
        }
    }

    public static int integer(JsonObject o, String key, int def, int min, int max) {
        int v = integer(o, key, def);
        return Math.max(min, Math.min(max, v));
    }

    public static double real(JsonObject o, String key, double def, double min, double max) {
        double v = num(o, key, def);
        return Math.max(min, Math.min(max, v));
    }

    public static long longValue(JsonObject o, String key, long def) {
        try {
            return has(o, key) ? o.get(key).getAsLong() : def;
        } catch (RuntimeException e) {
            return def;
        }
    }

    public static double num(JsonObject o, String key, double def) {
        try {
            return has(o, key) ? o.get(key).getAsDouble() : def;
        } catch (RuntimeException e) {
            return def;
        }
    }

    public static boolean bool(JsonObject o, String key, boolean def) {
        try {
            return has(o, key) ? o.get(key).getAsBoolean() : def;
        } catch (RuntimeException e) {
            return def;
        }
    }

    public static JsonObject obj(JsonObject o, String key) {
        return has(o, key) && o.get(key).isJsonObject() ? o.getAsJsonObject(key) : new JsonObject();
    }

    public static JsonArray arr(JsonObject o, String key) {
        return has(o, key) && o.get(key).isJsonArray() ? o.getAsJsonArray(key) : new JsonArray();
    }

    public static List<String> strings(JsonObject o, String key) {
        return strList(o, key);
    }

    public static List<String> strList(JsonObject o, String key) {
        if (!has(o, key)) return Collections.emptyList();
        JsonElement e = o.get(key);
        List<String> out = new ArrayList<>();
        if (e.isJsonArray()) {
            for (JsonElement x : e.getAsJsonArray()) out.add(x.getAsString());
        } else if (e.isJsonPrimitive()) {
            out.add(e.getAsString());
        }
        return out;
    }

    public static List<JsonObject> objList(JsonObject o, String key) {
        List<JsonObject> out = new ArrayList<>();
        for (JsonElement x : arr(o, key)) {
            if (x.isJsonObject()) out.add(x.getAsJsonObject());
        }
        return out;
    }

    /** Lit une table « clé vers entier » (par exemple la réputation par faction). */
    public static Map<String, Integer> intMap(JsonObject o, String key) {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : obj(o, key).entrySet()) {
            try {
                out.put(e.getKey(), e.getValue().getAsInt());
            } catch (RuntimeException ignored) {
                // valeur non numérique : ignorée
            }
        }
        return out;
    }

    public static Map<String, Double> numMap(JsonObject o, String key) {
        Map<String, Double> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : obj(o, key).entrySet()) {
            try {
                out.put(e.getKey(), e.getValue().getAsDouble());
            } catch (RuntimeException ignored) {
                // valeur non numérique : ignorée
            }
        }
        return out;
    }

    /**
     * Lit une valeur « par niveau » : soit un nombre unique (identique aux 5 niveaux),
     * soit un tableau de 5 nombres (niveaux 1 à 5). Un tableau plus court est prolongé
     * par sa dernière valeur.
     */
    public static double[] levels(JsonElement e, double def) {
        double[] out = new double[5];
        if (e == null || e.isJsonNull()) {
            java.util.Arrays.fill(out, def);
        } else if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            double last = def;
            for (int i = 0; i < 5; i++) {
                if (i < a.size()) last = a.get(i).getAsDouble();
                out[i] = last;
            }
        } else {
            java.util.Arrays.fill(out, e.getAsDouble());
        }
        return out;
    }

    public static JsonArray toArray(Iterable<String> values) {
        JsonArray a = new JsonArray();
        for (String s : values) a.add(s);
        return a;
    }
}
