package com.cambocraft;

import com.cambocraft.combat.CombatRegistry;
import com.cambocraft.command.CamboCommand;
import com.cambocraft.entity.CamboEntities;
import com.cambocraft.game.GachaService;
import com.cambocraft.game.PowerService;
import com.cambocraft.game.ProgressionService;
import com.cambocraft.game.SpawnNaturel;
import com.cambocraft.craft.CookingRegistry;
import com.cambocraft.data.DataManager;
import com.cambocraft.ecology.WorldAtmosphere;
import com.cambocraft.event.ShadowNightManager;
import com.cambocraft.fishing.FishingRegistry;
import com.cambocraft.npc.NpcRegistry;
import com.cambocraft.server.CamboServerEvents;
import com.cambocraft.power.PowerRegistry;
import com.cambocraft.network.CamboNetworking;
import com.cambocraft.progression.ZoneRegistry;
import com.cambocraft.quest.QuestRegistry;
import com.cambocraft.registry.CamboBlocks;
import com.cambocraft.registry.CamboItemGroup;
import com.cambocraft.registry.CamboItems;
import com.cambocraft.util.Log;
import com.cambocraft.world.CambodiaDimension;
import com.cambocraft.world.LotusPortal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * Point d'entrée commun (client et serveur) de Cambocraft.
 *
 * <p>L'ordre d'initialisation est important : les blocs d'abord (car les objets de bloc en
 * dépendent), puis les objets, puis le groupe créatif, puis les sous-systèmes (monde, quêtes,
 * pouvoirs…). Chaque sous-système s'enregistre lui-même dans son paquet ; cette classe ne fait
 * que fixer l'ordre.</p>
 */
public class Cambocraft implements ModInitializer {
    /** Espace de noms du mod. */
    public static final String MOD_ID = "cambocraft";

    @Override
    public void onInitialize() {
        Log.info("Cambocraft : initialisation du contenu de base");
        // Toucher les classes force leur chargement, ce qui déclenche les enregistrements statiques.
        CamboBlocks.class.getName();
        CamboItems.class.getName();
        CamboItemGroup.register();
        FishingRegistry.init();
        NpcRegistry.init();
        CookingRegistry.init();
        ZoneRegistry.init();
        QuestRegistry.init();
        PowerRegistry.init();
        CombatRegistry.init();
        ShadowNightManager.init();
        DataManager.init();
        LotusPortal.init();
        WorldAtmosphere.init();
        // Entités : sans cet enregistrement, les 16 PNJ et les 12 boss du catalogue
        // n'existaient que sous forme de fichiers JSON.
        CamboEntities.register();
        // Réseau : codecs des paquets puis récepteurs serveur (C2S) et évènements de persistance.
        CamboNetworking.enregistrerTypes();
        CamboNetworking.enregistrerRecepteurs();
        CamboServerEvents.enregistrer();
        // Déclencheurs de jeu : gacha au Bol de Riz Béni, progression de combat, apparition des PNJ.
        GachaService.init();
        ProgressionService.init();
        SpawnNaturel.init();
        PowerService.init();
        // Commandes : surface de jeu et de diagnostic de tous les sous-systèmes.
        CommandRegistrationCallback.EVENT.register((dispatcher, registre, environnement) ->
                CamboCommand.register(dispatcher));
        Log.info(
                "Cambocraft : {} blocs, {} objets et {} biomes initialisés",
                CamboBlocks.ALL.size(),
                CamboItems.ALL.size(),
                CambodiaDimension.BIOMES.size());
    }
}
