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
import com.cambocraft.quest.QuestDefinition;
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
                .then(CommandManager.literal("persistance").executes(CamboCommand::persistance))
                .then(CommandManager.literal("reputation").executes(CamboCommand::reputation))
                .then(CommandManager.literal("journal").executes(CamboCommand::journal))
                .then(CommandManager.literal("quete")
                        .then(CommandManager.argument("id", StringArgumentType.word())
                                .then(CommandManager.literal("demarrer").executes(CamboCommand::demarrerQuete))
                                .then(CommandManager.literal("avancer").executes(CamboCommand::avancerQuete))))
                .then(CommandManager.literal("boss_vaincu")
                        .then(CommandManager.argument("id", StringArgumentType.word())
                                .executes(CamboCommand::bossVaincu)))
                .then(CommandManager.literal("renaissance").executes(CamboCommand::renaitre))
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

    private static int persistance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData donnees = CamboPlayerData.pour(p);
        donnees.sauvegarderSansPurger(p.getServer());
        dire(ctx, "État sauvegardé dans world/data/" + com.cambocraft.server.CamboWorldState.ID + ".dat");
        dire(ctx, "  joueur " + p.getUuid() + " · " + CamboPlayerData.tailleCache() + " en cache");
        dire(ctx, "  niveau " + donnees.niveau() + " · XP " + donnees.xp()
                + " · Renaissance " + donnees.renaissances());
        dire(ctx, "  carnet " + donnees.carnet().nombrePouvoirsDebloques() + " pouvoirs · "
                + donnees.carnet().eclatsDeNen() + " éclats");
        dire(ctx, "  quêtes " + donnees.quetes().actives().size() + " actives · "
                + donnees.quetes().terminees().size() + " terminées");
        return 1;
    }

    private static int reputation(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        CamboPlayerData donnees = CamboPlayerData.pour(joueur(ctx));
        for (var e : donnees.reputation().tout().entrySet()) {
            dire(ctx, "  " + e.getKey().nomFr + " : " + e.getValue() + " ("
                    + donnees.reputation().palier(e.getKey()).id + ")");
        }
        dire(ctx, "Boss vaincus : " + donnees.bossVaincus().size() + " · pierres : "
                + donnees.pierresDecouvertes().size() + " · zones : " + donnees.zonesVisitees().size());
        return 1;
    }

    private static int journal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData donnees = CamboPlayerData.pour(p);
        dire(ctx, "Journal : " + donnees.quetes().actives().size() + " quête(s) en cours");
        for (var e : donnees.quetes().actives().entrySet()) {
            var q = QuestRegistry.quetes().get(e.getKey());
            if (q == null) continue;
            var prog = e.getValue();
            String objectif = prog.indexEtape() < q.etapes().size()
                    ? q.etapes().get(prog.indexEtape()).objectif() + " "
                            + q.etapes().get(prog.indexEtape()).cible() + " "
                            + prog.avancementEtape() + "/" + q.etapes().get(prog.indexEtape()).quantite()
                    : "prête à clore";
            dire(ctx, "  · " + q.titre().get("fr_fr") + " — " + objectif);
        }
        return 1;
    }

    private static int demarrerQuete(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        QuestDefinition q = QuestRegistry.quetes().get(id);
        if (q == null) {
            ctx.getSource().sendError(Text.literal("Quête inconnue : " + id));
            return 0;
        }
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData donnees = CamboPlayerData.pour(p);
        boolean ok = donnees.quetes().demarrer(q, donnees.niveau(),
                com.cambocraft.game.ProgressionService.saisonCourante(p));
        dire(ctx, ok ? "Quête démarrée : " + q.titre().get("fr_fr")
                : "Démarrage refusé (niveau, prérequis, saison ou déjà en cours)");
        return ok ? 1 : 0;
    }

    private static int avancerQuete(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        QuestDefinition q = QuestRegistry.quetes().get(id);
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData donnees = CamboPlayerData.pour(p);
        if (q == null || !donnees.quetes().actives().containsKey(id)) {
            ctx.getSource().sendError(Text.literal("Quête non active : " + id));
            return 0;
        }
        var prog = donnees.quetes().actives().get(id);
        if (prog.indexEtape() < q.etapes().size()) {
            var etape = q.etapes().get(prog.indexEtape());
            com.cambocraft.game.ProgressionService.notifierQuetes(donnees, p,
                    etape.objectif(), etape.cible());
        }
        dire(ctx, "Avancement notifié pour " + id);
        return 1;
    }

    private static int bossVaincu(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData donnees = CamboPlayerData.pour(p);
        donnees.marquerBossVaincu(id);
        dire(ctx, "Boss marqué vaincu : " + id + " (" + donnees.bossVaincus().size() + " au total)");
        return 1;
    }

    private static int renaitre(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity p = joueur(ctx);
        CamboPlayerData donnees = CamboPlayerData.pour(p);
        boolean ok = donnees.renaitre();
        dire(ctx, ok ? "Renaissance accordée : rang " + donnees.renaissances()
                : "Conditions non remplies (niveau 200 + Gardien de l'Équilibre vaincu)");
        return ok ? 1 : 0;
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
