package com.cambocraft.game;

import com.cambocraft.network.CamboNetworking;
import com.cambocraft.power.PowerDefinition;
import com.cambocraft.power.RiceBowlGacha;
import com.cambocraft.registry.CamboItems;
import com.cambocraft.server.CamboPlayerData;
import com.cambocraft.util.Log;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Le Bol de Riz Béni consommé en jeu : le tirage au gacha devient une action de survie.
 *
 * <p>Avant ce service, les 84 pouvoirs du Nen n'étaient accessibles que par la commande de
 * diagnostic {@code /cambocraft gacha}. Désormais, manger un Bol de Riz Béni déclenche un tirage
 * réel : rareté tirée selon les probabilités de base, garanties (« pity ») appliquées, pouvoir
 * ajouté au carnet, message coloré au joueur et synchronisation réseau. Le bol est consommé sauf
 * en créatif.</p>
 */
public final class GachaService {
    private GachaService() {}

    /** Branche l'écouteur d'utilisation d'objet. */
    public static void init() {
        UseItemCallback.EVENT.register(GachaService::surUtilisation);
        Log.info("Cambocraft : Bol de Riz Béni branché sur le tirage du Nen");
    }

    private static TypedActionResult<Object> surUtilisation(PlayerEntity joueur, World monde, Hand main) {
        ItemStack pile = joueur.getStackInHand(main);
        if (pile == null || pile.getItem() != CamboItems.BOL_DE_RIZ_BENI || monde.isClient) {
            return TypedActionResult.pass(null);
        }
        if (!(joueur instanceof ServerPlayerEntity serveur)) {
            return TypedActionResult.pass(null);
        }
        CamboPlayerData donnees = CamboPlayerData.pour(serveur);
        RiceBowlGacha.GachaRollResult resultat = RiceBowlGacha.degusterBolDeRizBeni(
                donnees.pity(), donnees.carnet(),
                new java.util.Random(serveur.getRandom().nextLong() ^ serveur.getServerWorld().getTime()));
        annoncerResultat(serveur, resultat, donnees);
        if (!serveur.isCreative()) {
            pile.decrement(1);
        }
        // L'XP du tirage accompagne la progression : un tirage reste un moment de jeu.
        donnees.ajouterXp(25L);
        CamboNetworking.synchroniserProfil(serveur, donnees);
        return TypedActionResult.success(pile);
    }

    private static void annoncerResultat(ServerPlayerEntity joueur, RiceBowlGacha.GachaRollResult resultat,
                                         CamboPlayerData donnees) {
        PowerDefinition pouvoir = resultat.pouvoir();
        Formatting couleur = switch (resultat.rareteObtenue()) {
            case MYTHIQUE -> Formatting.LIGHT_PURPLE;
            case LEGENDAIRE -> Formatting.GOLD;
            case EPIQUE -> Formatting.AQUA;
            case RARE -> Formatting.BLUE;
            default -> Formatting.GRAY;
        };
        String nom = pouvoir == null ? "?" : pouvoir.id();
        MutableText message = Text.literal("✦ " + nom + " — " + resultat.rareteObtenue().name().toLowerCase())
                .formatted(couleur);
        if (resultat.garantiePityActivee()) {
            message.append(Text.literal(" (garantie)").formatted(Formatting.YELLOW));
        }
        if (resultat.convertiEnMaitrise()) {
            message.append(Text.literal(" → maîtrise +25").formatted(Formatting.LIGHT_PURPLE));
        } else {
            message.append(Text.literal(" → niveau " + resultat.nouveauNiveauPouvoir()).formatted(Formatting.WHITE));
        }
        joueur.sendMessage(message, false);
        // Petit rappel des compteurs : le joueur sait où il en est de ses garanties.
        joueur.sendMessage(Text.literal("§8pity : " + donnees.pity().tiragesSansEpique() + "/30 épique · "
                + donnees.pity().tiragesSansLegendaire() + "/80 légendaire · "
                + donnees.pity().tiragesSansMythique() + "/250 mythique"), true);
    }
}
