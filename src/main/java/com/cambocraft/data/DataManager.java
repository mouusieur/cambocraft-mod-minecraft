package com.cambocraft.data;

import com.cambocraft.util.DataBundle;
import com.cambocraft.util.Ids;
import com.cambocraft.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

/**
 * Charge et distribue les données JSON du mod ({@code data/cambocraft/cambo/**}).
 *
 * <p>Le chargement passe par le gestionnaire de ressources de Minecraft : les packs de données
 * peuvent donc remplacer un fichier du mod, et {@code /reload} recharge l'ensemble sans
 * redémarrage. Chaque sous-système s'inscrit comme {@link DataConsumer} ; l'ordre d'appel est
 * celui de l'inscription, ce qui permet de charger les espèces avant les zones qui les citent.</p>
 *
 * <p>En cas d'erreur de syntaxe, le fichier fautif est ignoré, l'erreur est consignée et le
 * chargement continue : une donnée cassée ne doit jamais empêcher de jouer.</p>
 */
public final class DataManager {
    private static final String DOSSIER = "cambo";
    private static final List<DataConsumer> CONSUMERS = new ArrayList<>();
    private static DataBundle dernier = new DataBundle();
    private static int version;

    private DataManager() {}

    /** Enregistre l'écouteur de rechargement serveur (démarrage du monde et commande {@code /reload}). */
    public static void init() {
        Ecouteur.enregistrer();
    }

    private static final class Ecouteur implements SimpleSynchronousResourceReloadListener {
        static void enregistrer() {
            ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new Ecouteur());
        }

        @Override
        public Identifier getFabricId() {
            return Ids.id("cambo_data");
        }

        @Override
        public void reload(ResourceManager manager) {
            recharger(manager);
        }
    }

    public static void enregistrer(DataConsumer consumer) {
        if (!CONSUMERS.contains(consumer)) {
            CONSUMERS.add(consumer);
        }
    }

    public static DataBundle bundle() {
        return dernier;
    }

    /** Incrémenté à chaque rechargement : sert aux caches (client) pour se rafraîchir. */
    public static int version() {
        return version;
    }

    /** Relit tous les fichiers du mod et prévient les consommateurs. */
    public static void recharger(ResourceManager manager) {
        DataBundle bundle = new DataBundle();
        Map<Identifier, List<Resource>> fichiers = manager.findAllResources(
                DOSSIER, id -> Ids.NAMESPACE.equals(id.getNamespace()) && id.getPath().endsWith(".json"));
        int lus = 0;
        for (Map.Entry<Identifier, List<Resource>> entree : fichiers.entrySet()) {
            Identifier id = entree.getKey();
            if (entree.getValue().isEmpty()) continue;
            // La première ressource est celle du pack de plus haute priorité (comportement vanilla).
            Resource ressource = entree.getValue().get(0);
            String chemin = id.getPath();
            String relatif = chemin.substring(DOSSIER.length() + 1, chemin.length() - ".json".length());
            int barre = relatif.indexOf('/');
            if (barre <= 0) {
                bundle.error(chemin + " : un fichier de données doit être rangé dans un sous-dossier (pêche, pouvoirs…)");
                continue;
            }
            String categorie = relatif.substring(0, barre);
            String cle = relatif.substring(barre + 1);
            try (BufferedReader lecteur = ressource.getReader()) {
                StringBuilder texte = new StringBuilder();
                String ligne;
                while ((ligne = lecteur.readLine()) != null) texte.append(ligne).append('\n');
                int avant = bundle.errors().size();
                bundle.putText(categorie, cle, texte.toString());
                if (bundle.errors().size() == avant) lus++;
            } catch (Exception e) {
                bundle.error(chemin + " : lecture impossible (" + e.getMessage() + ")");
            }
        }
        appliquer(bundle, lus);
    }

    /** Charge les JSON depuis un dossier sur disque (tests hors jeu et outils de validation). */
    public static DataBundle chargerDepuisDossier(Path dataRoot) throws IOException {
        DataBundle bundle = DataBundle.fromDirectory(dataRoot);
        appliquer(bundle, bundle.size());
        return bundle;
    }

    /** Applique un paquet de données déjà construit et notifie les consommateurs dans l'ordre. */
    public static void appliquer(DataBundle bundle, int lus) {
        dernier = bundle;
        version++;
        Log.info("Données : {} fichier(s) chargé(s), {} erreur(s)", lus, bundle.errors().size());
        for (String erreur : bundle.errors()) Log.dataError(erreur);
        for (DataConsumer consumer : CONSUMERS) {
            try {
                consumer.charger(bundle);
            } catch (RuntimeException e) {
                Log.dataError("Le sous-système « {} » a échoué : {}", consumer.nom(), e.toString());
            }
        }
    }

    /** Réinitialise (tests et changement de serveur). */
    public static void reinitialiser() {
        dernier = new DataBundle();
        version++;
    }
}
