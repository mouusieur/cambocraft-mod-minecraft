package com.cambocraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ensemble des JSON de données du mod, rangés par catégorie puis par identifiant.
 *
 * <p>En jeu, il est rempli par le gestionnaire de ressources du serveur (rechargeable avec
 * {@code /reload}). Dans les tests et les outils, il est lu directement dans le dossier
 * {@code data/cambocraft/cambo}. Le noyau de règles ne dépend ainsi jamais de Minecraft.</p>
 */
public final class DataBundle {
    private final Map<String, Map<String, JsonObject>> categories = new TreeMap<>();
    private final List<String> errors = new ArrayList<>();

    public void put(String category, String id, JsonObject json) {
        categories.computeIfAbsent(category, k -> new TreeMap<>()).put(id, json);
    }

    public Map<String, JsonObject> category(String category) {
        Map<String, JsonObject> m = categories.get(category);
        return m == null ? Collections.<String, JsonObject>emptyMap() : m;
    }

    public JsonObject single(String category, String id) {
        JsonObject o = category(category).get(id);
        return o == null ? new JsonObject() : o;
    }

    public List<String> errors() {
        return errors;
    }

    public void error(String message) {
        errors.add(message);
    }

    public int size() {
        int n = 0;
        for (Map<String, JsonObject> m : categories.values()) n += m.size();
        return n;
    }

    /** Lit un texte JSON et l'ajoute ; une erreur de syntaxe est consignée, jamais levée. */
    public void putText(String category, String id, String text) {
        try {
            JsonElement e = JsonParser.parseString(text);
            if (e.isJsonObject()) {
                put(category, id, e.getAsJsonObject());
            } else {
                error(category + "/" + id + " : la racine doit être un objet JSON");
            }
        } catch (RuntimeException ex) {
            error(category + "/" + id + " : JSON invalide (" + ex.getMessage() + ")");
        }
    }

    /**
     * Charge tous les fichiers {@code <racine>/cambo/<catégorie>/**.json}.
     * L'identifiant est le chemin relatif sans extension, avec des barres obliques.
     */
    public static DataBundle fromDirectory(Path dataRoot) throws IOException {
        DataBundle bundle = new DataBundle();
        Path base = dataRoot.resolve("cambo");
        if (!Files.isDirectory(base)) return bundle;
        List<Path> files;
        try (Stream<Path> s = Files.walk(base)) {
            files = s.filter(p -> p.toString().endsWith(".json")).sorted().collect(Collectors.toList());
        }
        for (Path p : files) {
            Path rel = base.relativize(p);
            if (rel.getNameCount() < 2) continue;
            String category = rel.getName(0).toString();
            String id = rel.subpath(1, rel.getNameCount()).toString().replace('\\', '/');
            id = id.substring(0, id.length() - ".json".length());
            try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                JsonElement e = JsonParser.parseReader(r);
                if (e.isJsonObject()) bundle.put(category, id, e.getAsJsonObject());
                else bundle.error(category + "/" + id + " : la racine doit être un objet JSON");
            } catch (RuntimeException ex) {
                bundle.error(category + "/" + id + " : JSON invalide (" + ex.getMessage() + ")");
            }
        }
        return bundle;
    }
}
