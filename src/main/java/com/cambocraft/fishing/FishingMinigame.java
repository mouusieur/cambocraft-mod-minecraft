package com.cambocraft.fishing;

import com.cambocraft.util.Ids;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Mini-jeu de pêche, entièrement décidé côté serveur.
 *
 * <p>Déroulement : le joueur lance sa ligne (premier clic droit), attend la touche (délai tiré au
 * sort selon la difficulté de l'espèce), puis ferre d'un second clic droit. Le serveur seul
 * connaît l'instant de la touche : ferrer trop tôt ou trop tard échoue, et aucun paquet client ne
 * peut forcer une prise. La marge de réaction est élargie si le joueur a du retard réseau déclaré
 * (voir {@code docs/DESIGN.md} § Pêche).</p>
 *
 * <p>Le poisson tiré dépend de la zone, de la saison, de l'heure et de la chance du joueur ;
 * la taille est tirée dans l'intervalle de l'espèce et fixe le prix de vente.</p>
 */
public final class FishingMinigame {
    /** Fenêtre de ferrage de base, en ticks (0,9 s), élargie par la difficulté. */
    private static final int FENETRE_BASE = 18;
    /** Délai minimal et maximal avant la touche, en ticks. */
    private static final int ATTENTE_MIN = 20;
    private static final int ATTENTE_MAX = 70;

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Random ALEA = new Random();

    private FishingMinigame() {}

    /** État d'une ligne lancée. */
    private record Session(long toucheA, long expiration, String zone, String saison, double chance) {}

    public static TypedActionResult<ItemStack> begin(PlayerEntity user, ItemStack canne) {
        if (!(user instanceof ServerPlayerEntity joueur)) {
            // Le client ne décide rien : il affiche seulement l'animation de lancer.
            return TypedActionResult.success(canne);
        }
        World monde = joueur.getWorld();
        long maintenant = monde.getTime();
        Session enCours = SESSIONS.get(joueur.getUuid());
        if (enCours == null) {
            String zone = FishingContext.zone(joueur);
            String saison = FishingContext.saison(monde);
            double chance = FishingContext.chance(joueur, canne);
            Optional<FishSpecies> apercu = FishingRegistry.table().tirer(zone, saison, monde.isDay(), chance, ALEA);
            if (apercu.isEmpty()) {
                joueur.sendMessage(Text.translatable("cambocraft.peche.aucune_prise"), true);
                return TypedActionResult.fail(canne);
            }
            double difficulte = apercu.get().difficulty() * apercu.get().rarity().difficultyMultiplier;
            int attente = ATTENTE_MIN + (int) Math.round(ALEA.nextDouble() * (ATTENTE_MAX - ATTENTE_MIN) * (0.4 + difficulte));
            int fenetre = (int) Math.round(FENETRE_BASE * (1.35 - 0.55 * difficulte));
            SESSIONS.put(joueur.getUuid(), new Session(maintenant + attente, maintenant + attente + fenetre,
                    zone, saison, chance));
            joueur.sendMessage(Text.translatable("cambocraft.peche.lancee"), true);
            canne.damage(1, joueur, net.minecraft.entity.EquipmentSlot.MAINHAND);
            return TypedActionResult.success(canne);
        }
        SESSIONS.remove(joueur.getUuid());
        if (maintenant < enCours.toucheA()) {
            joueur.sendMessage(Text.translatable("cambocraft.peche.trop_tot"), true);
            return TypedActionResult.fail(canne);
        }
        if (maintenant > enCours.expiration()) {
            joueur.sendMessage(Text.translatable("cambocraft.peche.trop_tard"), true);
            return TypedActionResult.fail(canne);
        }
        return resoudre(joueur, canne, enCours);
    }

    private static TypedActionResult<ItemStack> resoudre(ServerPlayerEntity joueur, ItemStack canne, Session session) {
        Optional<FishSpecies> prise = FishingRegistry.table()
                .tirer(session.zone(), session.saison(), joueur.getWorld().isDay(), session.chance(), ALEA);
        if (prise.isEmpty()) {
            joueur.sendMessage(Text.translatable("cambocraft.peche.aucune_prise"), true);
            return TypedActionResult.fail(canne);
        }
        FishSpecies espece = prise.get();
        int taille = FishingTable.taille(espece, ALEA);
        Identifier idObjet = Identifier.tryParse(espece.itemId());
        Item objet = idObjet == null ? null : Registries.ITEM.get(idObjet);
        if (objet == null || objet == net.minecraft.item.Items.AIR) {
            joueur.sendMessage(Text.translatable("cambocraft.peche.objet_inconnu", espece.itemId()), true);
            return TypedActionResult.fail(canne);
        }
        ItemStack butin = new ItemStack(objet);
        FishingCatch.marquer(butin, espece.id(), taille, espece.prixDeVente(taille));
        if (!joueur.getInventory().insertStack(butin)) {
            joueur.dropItem(butin, false);
        }
        joueur.sendMessage(Text.translatable("cambocraft.peche.prise",
                Text.translatable(objet.getTranslationKey()), taille), true);
        return TypedActionResult.success(canne);
    }

    /** Abandonne une ligne en cours (déconnexion, changement de dimension). */
    public static void oublier(UUID joueur) {
        SESSIONS.remove(joueur);
    }

    public static boolean pecheEnCours(UUID joueur) {
        return SESSIONS.containsKey(joueur);
    }

    /** Utilitaire de test : nombre de sessions actives. */
    static int sessionsActives() {
        return SESSIONS.size();
    }
}
