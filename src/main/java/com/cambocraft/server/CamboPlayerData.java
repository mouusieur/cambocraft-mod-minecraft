package com.cambocraft.server;

import com.cambocraft.npc.Faction;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.RiceBowlGacha;
import com.cambocraft.progression.ProgressionCurve;
import com.cambocraft.quest.QuestEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * État Cambocraft persistant d'un joueur : progression, carnet du Nen, compteurs de garantie,
 * suivi des quêtes, réputation par faction, boss vaincus, pierres du lotus découvertes et
 * Renaissance.
 *
 * <p>Le stockage réel est assuré par {@link CamboWorldState}, sauvegardé en NBT avec le monde :
 * ces données survivent à la déconnexion <b>et</b> au redémarrage du serveur. Cette classe ne
 * garde qu'un cache mémoire pour éviter de relire le NBT à chaque accès ; le cache est purgé à la
 * déconnexion pour que deux sessions successives repartent du disque.</p>
 */
public final class CamboPlayerData {
    /** Cache mémoire par joueur ; vidé à la déconnexion et à l'arrêt du serveur. */
    private static final Map<UUID, CamboPlayerData> CACHE = new HashMap<>();

    /** Version du format de sauvegarde : permet de migrer sans perdre les anciennes parties. */
    public static final int VERSION_FORMAT = 2;

    private final UUID uuid;
    private final NenNotebook carnet = new NenNotebook();
    private final RiceBowlGacha.PityState pity = new RiceBowlGacha.PityState();
    private final QuestEngine quetes = new QuestEngine();
    private final JoueurReputation reputation = new JoueurReputation();
    private final Set<String> bossVaincus = new LinkedHashSet<>();
    private final Set<String> pierresDecouvertes = new LinkedHashSet<>();
    private final Set<String> zonesVisitees = new LinkedHashSet<>();
    private int niveau = 1;
    private long xp;
    private int renaissances;
    private String queteEpinglee = "";

    private CamboPlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Renvoie l'état d'un joueur, en le chargeant depuis la sauvegarde du monde au besoin.
     *
     * @param joueur joueur connecté, utilisé pour retrouver le monde et donc la sauvegarde
     */
    public static CamboPlayerData pour(ServerPlayerEntity joueur) {
        UUID uuid = joueur.getUuid();
        CamboPlayerData donnees = CACHE.get(uuid);
        if (donnees != null) {
            return donnees;
        }
        donnees = new CamboPlayerData(uuid);
        MinecraftServer serveur = joueur.getServer();
        if (serveur != null) {
            NbtCompound nbt = CamboWorldState.obtenir(serveur).joueur(uuid);
            if (nbt != null) {
                donnees.lire(nbt);
            }
        }
        CACHE.put(uuid, donnees);
        return donnees;
    }

    /**
     * Crée un état <b>détaché</b> du cache et de la sauvegarde, réservé à l'affichage côté client.
     *
     * <p>Le client ne doit jamais écrire de progression : il reconstitue un objet à partir des
     * paquets reçus, uniquement pour les écrans qui en ont besoin (Carnet, journal de quêtes).</p>
     */
    public static CamboPlayerData pourAffichage() {
        return new CamboPlayerData(new UUID(0L, 0L));
    }

    /** Fixe le niveau et le rang de Renaissance d'un état d'affichage (aucune écriture disque). */
    public void definirNiveauPourAffichage(int niveauAffiche, int rangRenaissance) {
        this.niveau = Math.max(1, Math.min(ProgressionCurve.NIVEAU_MAX, niveauAffiche));
        this.renaissances = Math.max(0, Math.min(ProgressionCurve.RENAISSANCE_MAX, rangRenaissance));
    }

    /** Écrit l'état dans la sauvegarde du monde (appelé à la déconnexion et à l'arrêt). */
    public void sauvegarder(MinecraftServer serveur) {
        CamboWorldState.obtenir(serveur).ecrire(uuid, ecrire());
        CACHE.remove(uuid);
    }

    /** Écrit l'état dans le monde sans vider le cache (sauvegarde périodique). */
    public void sauvegarderSansPurger(MinecraftServer serveur) {
        CamboWorldState.obtenir(serveur).ecrire(uuid, ecrire());
    }

    /** Régénère l'énergie spirituelle du carnet (appelé chaque seconde côté serveur). */
    public void regenererEnergie(int montant) {
        carnet.definirEnergieMax(energieMax());
        carnet.regenererEnergie(montant);
    }

    /** Sauvegarde l'état d'un joueur puis purge son cache (déconnexion). */
    public static void sauvegarderJoueur(ServerPlayerEntity joueur, MinecraftServer serveur) {
        CamboPlayerData donnees = CACHE.get(joueur.getUuid());
        if (donnees != null) {
            donnees.sauvegarder(serveur);
        }
    }

    /** Sauvegarde en une passe tous les états encore en cache (arrêt du serveur). */
    public static int sauvegarderTout(MinecraftServer serveur) {
        if (!CACHE.isEmpty()) {
            CamboWorldState etat = CamboWorldState.obtenir(serveur);
            for (CamboPlayerData donnees : new ArrayList<>(CACHE.values())) {
                etat.ecrire(donnees.uuid, donnees.ecrire());
            }
        }
        int n = CACHE.size();
        CACHE.clear();
        return n;
    }

    /** Nombre d'états actuellement en mémoire (diagnostic). */
    public static int tailleCache() {
        return CACHE.size();
    }

    /** Vide le cache sans écrire : utilisé par les tests et les rechargements de monde. */
    public static void viderCache() {
        CACHE.clear();
    }

    public UUID uuid() {
        return uuid;
    }

    public NenNotebook carnet() {
        return carnet;
    }

    public RiceBowlGacha.PityState pity() {
        return pity;
    }

    public QuestEngine quetes() {
        return quetes;
    }

    public JoueurReputation reputation() {
        return reputation;
    }

    public int niveau() {
        return niveau;
    }

    public long xp() {
        return xp;
    }

    public int renaissances() {
        return renaissances;
    }

    public String queteEpinglee() {
        return queteEpinglee;
    }

    public void epinglerQuete(String queteId) {
        this.queteEpinglee = queteId == null ? "" : queteId;
    }

    public Set<String> bossVaincus() {
        return Set.copyOf(bossVaincus);
    }

    public void marquerBossVaincu(String bossId) {
        if (bossId != null && !bossId.isEmpty()) {
            bossVaincus.add(bossId);
        }
    }

    public Set<String> pierresDecouvertes() {
        return Set.copyOf(pierresDecouvertes);
    }

    public void marquerPierreDecouverte(String zoneId) {
        if (zoneId != null && !zoneId.isEmpty()) {
            pierresDecouvertes.add(zoneId);
        }
    }

    public Set<String> zonesVisitees() {
        return Set.copyOf(zonesVisitees);
    }

    public void marquerZoneVisitee(String zoneId) {
        if (zoneId != null && !zoneId.isEmpty()) {
            zonesVisitees.add(zoneId);
        }
    }

    /** Ajoute de l'expérience et applique la progression (montée de niveau, Renaissance). */
    public ProgressionCurve.EtatNiveau ajouterXp(long gain) {
        ProgressionCurve.EtatNiveau etat = ProgressionCurve.ajouterXp(niveau, xp, gain);
        niveau = etat.niveau();
        xp = etat.xpDansNiveau();
        return etat;
    }

    /** Accorde une Renaissance si les conditions sont réunies (niveau 200 + Gardien vaincu). */
    public boolean renaitre() {
        if (!ProgressionCurve.peutRenaitre(niveau, renaissances,
                bossVaincus.contains("le_gardien_de_l_equilibre"))) {
            return false;
        }
        renaissances++;
        niveau = 1;
        xp = 0;
        return true;
    }

    /** Énergie spirituelle maximale dérivée du niveau et du rang de Renaissance. */
    public int energieMax() {
        return ProgressionCurve.energieMax(niveau, renaissances);
    }

    // --- sérialisation NBT ---------------------------------------------------

    public NbtCompound ecrire() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("version", VERSION_FORMAT);
        nbt.putInt("niveau", niveau);
        nbt.putLong("xp", xp);
        nbt.putInt("renaissances", renaissances);
        nbt.putString("queteEpinglee", queteEpinglee);

        nbt.putInt("pityEpique", pity.tiragesSansEpique());
        nbt.putInt("pityLegendaire", pity.tiragesSansLegendaire());
        nbt.putInt("pityMythique", pity.tiragesSansMythique());
        nbt.putInt("pityTotal", pity.totalTirages());

        NbtCompound carnetNbt = new NbtCompound();
        for (Map.Entry<String, NenNotebook.PowerProgress> e : carnet.collection().entrySet()) {
            NbtCompound pouvoir = new NbtCompound();
            pouvoir.putInt("niveau", e.getValue().niveau());
            pouvoir.putInt("utilisations", e.getValue().utilisations());
            pouvoir.putBoolean("maitrise", e.getValue().maitriseDebloquee());
            carnetNbt.put(e.getKey(), (NbtElement) pouvoir);
        }
        nbt.put("carnet", (NbtElement) carnetNbt);
        nbt.putInt("eclats", carnet.eclatsDeNen());
        nbt.putInt("energie", carnet.energieActuelle());
        nbt.putInt("energieMax", carnet.energieMax());
        NbtList emplacements = new NbtList();
        for (int i = 0; i < NenNotebook.NB_EMPLACEMENTS_ACTIFS; i++) {
            String p = carnet.pouvoirEquipe(i);
            emplacements.add(NbtString.of(p == null ? "" : p));
        }
        nbt.put("emplacements", emplacements);

        // Quêtes en cours : identifiant, étape, avancement et branche choisie.
        NbtList actives = new NbtList();
        for (QuestEngine.ProgressionActive p : quetes.actives().values()) {
            NbtCompound q = new NbtCompound();
            q.putString("id", p.queteId());
            q.putInt("etape", p.indexEtape());
            q.putInt("avancement", p.avancementEtape());
            if (p.brancheChoisie() != null) {
                q.putString("branche", p.brancheChoisie());
            }
            actives.add(q);
        }
        nbt.put("quetesActives", actives);
        NbtList terminees = new NbtList();
        for (String id : quetes.terminees()) {
            terminees.add(NbtString.of(id));
        }
        nbt.put("quetesTerminees", terminees);

        NbtCompound reputations = new NbtCompound();
        for (Map.Entry<Faction, Integer> e : reputation.tout().entrySet()) {
            reputations.putInt(e.getKey().id, e.getValue());
        }
        nbt.put("reputation", (NbtElement) reputations);

        nbt.put("bossVaincus", chaine(bossVaincus));
        nbt.put("pierresDecouvertes", chaine(pierresDecouvertes));
        nbt.put("zonesVisitees", chaine(zonesVisitees));
        return nbt;
    }

    private static NbtList chaine(Iterable<String> valeurs) {
        NbtList liste = new NbtList();
        for (String v : valeurs) {
            liste.add(NbtString.of(v));
        }
        return liste;
    }

    private static List<String> lireChaines(NbtCompound nbt, String cle) {
        List<String> res = new ArrayList<>();
        if (nbt.contains(cle)) {
            NbtList liste = nbt.getList(cle, 8);
            for (int i = 0; i < liste.size(); i++) {
                res.add(liste.getString(i));
            }
        }
        return res;
    }

    public void lire(NbtCompound nbt) {
        niveau = Math.max(1, Math.min(ProgressionCurve.NIVEAU_MAX, nbt.getInt("niveau")));
        xp = Math.max(0L, nbt.getLong("xp"));
        renaissances = Math.max(0, Math.min(ProgressionCurve.RENAISSANCE_MAX, nbt.getInt("renaissances")));
        queteEpinglee = nbt.contains("queteEpinglee") ? nbt.getString("queteEpinglee") : "";

        pity.restaurer(nbt.getInt("pityEpique"), nbt.getInt("pityLegendaire"),
                nbt.getInt("pityMythique"), nbt.getInt("pityTotal"));

        if (nbt.contains("carnet")) {
            NbtCompound carnetNbt = nbt.getCompound("carnet");
            for (Object brute : carnetNbt.getKeys()) {
                String id = String.valueOf(brute);
                NbtCompound pouvoir = carnetNbt.getCompound(id);
                carnet.restaurerPouvoir(id, pouvoir.getInt("niveau"), pouvoir.getInt("utilisations"),
                        pouvoir.contains("maitrise") && pouvoir.getBoolean("maitrise"));
            }
        }
        if (nbt.contains("energieMax") && nbt.getInt("energieMax") > 0) {
            carnet.definirEnergieMax(nbt.getInt("energieMax"));
        }
        if (nbt.contains("eclats")) {
            carnet.ajouterEclats(nbt.getInt("eclats"));
        }
        if (nbt.contains("emplacements")) {
            NbtList emplacements = nbt.getList("emplacements", 8);
            for (int i = 0; i < Math.min(emplacements.size(), NenNotebook.NB_EMPLACEMENTS_ACTIFS); i++) {
                carnet.restaurerEmplacement(i, emplacements.getString(i));
            }
        }
        if (nbt.contains("energie")) {
            carnet.definirEnergie(nbt.getInt("energie"));
        } else {
            carnet.definirEnergie(carnet.energieMax());
        }

        for (String id : lireChaines(nbt, "quetesTerminees")) {
            quetes.restaurerTerminee(id);
        }
        if (nbt.contains("quetesActives")) {
            NbtList liste = nbt.getList("quetesActives", 10);
            for (int i = 0; i < liste.size(); i++) {
                NbtCompound q = liste.getCompound(i);
                quetes.restaurerActive(q.getString("id"), q.getInt("etape"), q.getInt("avancement"),
                        q.contains("branche") ? q.getString("branche") : null);
            }
        }

        if (nbt.contains("reputation")) {
            NbtCompound reputations = nbt.getCompound("reputation");
            for (Faction f : Faction.values()) {
                if (reputations.contains(f.id)) {
                    reputation.restaurer(f, reputations.getInt(f.id));
                }
            }
        }
        bossVaincus.addAll(lireChaines(nbt, "bossVaincus"));
        pierresDecouvertes.addAll(lireChaines(nbt, "pierresDecouvertes"));
        zonesVisitees.addAll(lireChaines(nbt, "zonesVisitees"));
    }
}
