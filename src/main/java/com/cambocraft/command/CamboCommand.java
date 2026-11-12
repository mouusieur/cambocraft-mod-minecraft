package com.cambocraft.command;

import com.cambocraft.combat.CombatRegistry;
import com.cambocraft.craft.CookingRegistry;
import com.cambocraft.npc.ArtisanDialogueService;
import com.cambocraft.npc.NpcRegistry;
import com.cambocraft.power.NenNotebook;
import com.cambocraft.power.PowerDefinition;
import com.cambocraft.power.PowerRegistry;
import com.cambocraft.power.RiceBowlGacha;
import com.cambocraft.progression.ProgressionCurve;
import com.cambocraft.quest.QuestRegistry;
import com.cambocraft.server.CamboPlayerData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Commande {@code /cambocraft} : surface de jeu ET outil de diagnostic de tous les sous-systèmes.
 *
 * <p>Avant elle, les 85 quêtes, les 84 pouvoirs et les 12 boss n'avaient aucun déclencheur : ils
 * n'existaient que sous forme de données validées. Chaque sous-commande branche un système réel.</p>
 */
public final class CamboCommand {
    private CamboCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((com.mojang.brigadier.builder.LiteralArgumentBuilder<ServerCommandSource>)
                CommandManager.literal("cambocraft")
                .then(CommandManager.literal("statut").executes(CamboCommand::statut))
                .then(CommandManager.literal("gacha").executes(CamboCommand::gacha))
                .then(CommandManager.literal("carnet").executes(CamboCommand::carnet))
                .then(CommandManager.literal("quetes").executes(CamboCommand::quetes))
                .then(CommandManager.literal("pnjs").executes(CamboCommand::pnjs))
                .then(CommandManager.literal("plats").executes(CamboCommand::plats))
                .then(CommandManager.literal("boss").executes(CamboCommand::boss))
                .then(CommandManager.literal("niveau")
                        .then(CommandManager.argument("n", IntegerArgumentType.integer(1, 200))
                                .executes(CamboCommand::niveau)))
                .then(CommandManager.literal("donner_pouvoir")
                        .then(CommandManager.argument("id", StringArgumentType.word())
                                .executes(CamboCommand::donnerPouvoir)))
                .then(CommandManager.literal("dialogue")
                        .then(CommandManager.argument("arbre", StringArgumentType.word())
                                .then(CommandManager.argument("noeud", StringArgumentType.word())
                                        .executes(CamboCommand::dialogue)))));
    }

    private static ServerPlayerEntity joueur(CommandContext<ServerCommandSource> ctx)
            throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrThrow();
    }

    private static void dire(CommandContext<ServerCommandSource> ctx, String texte) {
        ctx.getSource().sendFeedback(() -> Text.literal(texte), false);
    }

    private static int statut(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        joueur(ctx);
        dire(ctx, "=== Cambocraft 1.0.0 ===");
        dire(ctx, "PNJ " + NpcRegistry.pnjs().size()
                + " · quêtes " + QuestRegistry.quetes().size()
                + " · pouvoirs " + PowerRegistry.tous().size()
                + " · ennemis " + CombatRegistry.tous().size()
                + " · plats " + CookingRegistry.recettes().size());
        dire(ctx, "Dimension : cambocraft:cambodia · portail : anneau 3×3 + Bol de Riz Béni");
        return 1;
    }

    private static int gacha(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData data = CamboPlayerData.pour(p);
        RiceBowlGacha.GachaRollResult resultat = RiceBowlGacha.degusterBolDeRizBeni(
                data.pity(), data.carnet(), new java.util.Random(p.getRandom().nextLong()));
        PowerDefinition pouvoir = resultat.pouvoir();
        p.sendMessage(Text.literal("✦ " + (pouvoir == null ? "?" : pouvoir.id())
                + " — " + resultat.rareteObtenue()
                + (resultat.garantiePityActivee() ? " (pity)" : ""))
                .formatted(Formatting.LIGHT_PURPLE), false);
        dire(ctx, "Carnet : " + data.carnet().nombrePouvoirsDebloques() + " pouvoirs");
        return 1;
    }

    private static int carnet(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity p = joueur(ctx);
        NenNotebook carnet = CamboPlayerData.pour(p).carnet();
        dire(ctx, "Carnet du Nen : " + carnet.nombrePouvoirsDebloques() + " pouvoirs, "
                + carnet.eclatsDeNen() + " éclats, énergie " + carnet.energieActuelle());
        for (Map.Entry<String, NenNotebook.PowerProgress> e : carnet.collection().entrySet()) {
            p.sendMessage(Text.literal("  · " + e.getKey() + " niv. " + e.getValue().niveau())
                    .formatted(Formatting.AQUA), false);
        }
        return 1;
    }

    private static int quetes(CommandContext<ServerCommandSource> ctx) {
        dire(ctx, "Quêtes : " + QuestRegistry.quetes().size() + " au catalogue");
        int n = 0;
        for (String id : QuestRegistry.quetes().keySet()) {
            if (n++ >= 12) { dire(ctx, "  …"); break; }
            dire(ctx, "  · " + id);
        }
        return 1;
    }

    private static int pnjs(CommandContext<ServerCommandSource> ctx) {
        dire(ctx, "PNJ : " + NpcRegistry.pnjs().size());
        NpcRegistry.pnjs().keySet().forEach(id -> dire(ctx, "  · " + id));
        return 1;
    }

    private static int plats(CommandContext<ServerCommandSource> ctx) {
        dire(ctx, "Cuisine khmère : " + CookingRegistry.recettes().size());
        CookingRegistry.recettes().keySet().forEach(id -> dire(ctx, "  · " + id));
        return 1;
    }

    private static int boss(CommandContext<ServerCommandSource> ctx) {
        var boss = CombatRegistry.boss();
        dire(ctx, "Boss : " + boss.size());
        for (var b : boss) {
            dire(ctx, "  · " + b.id());
        }
        return 1;
    }

    private static int niveau(CommandContext<ServerCommandSource> ctx) {
        int n = IntegerArgumentType.getInteger(ctx, "n");
        dire(ctx, "Niveau " + n + " : " + ProgressionCurve.xpCumuleePourAtteindre(n)
                + " XP cumulés (courbe round(120·n^1.8))");
        return 1;
    }

    private static int donnerPouvoir(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        PowerDefinition def = PowerRegistry.get(id);
        if (def == null) {
            ctx.getSource().sendError(Text.literal("Pouvoir inconnu : " + id));
            return 0;
        }
        var retour = CamboPlayerData.pour(joueur(ctx)).carnet().ajouterTirage(def);
        dire(ctx, "Pouvoir " + id + " → niveau " + retour.niveauActuel());
        return 1;
    }

    private static int dialogue(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String arbreId = StringArgumentType.getString(ctx, "arbre");
        String noeud = StringArgumentType.getString(ctx, "noeud");
        var arbre = NpcRegistry.dialogue(arbreId);
        if (arbre == null) {
            ctx.getSource().sendError(Text.literal("Dialogue inconnu : " + arbreId));
            return 0;
        }
        ArtisanDialogueService.publier(joueur(ctx), arbre, noeud, 0, java.util.Set.of());
        return 1;
    }
}
