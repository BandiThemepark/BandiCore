package net.bandithemepark.bandicore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.bandithemepark.kaliba.KalibaEffects
import net.bandithemepark.bandicore.network.backend.BackendSetting
import net.bandithemepark.bandicore.park.attractions.tracks.TrackManager
import net.bandithemepark.bandicore.park.attractions.tracks.commands.TrackCommand
import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands.TrackVehicleCommand
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditor
import net.bandithemepark.bandicore.park.effect.AmbientEffect
import net.bandithemepark.bandicore.server.Server
import net.bandithemepark.bandicore.server.tools.armorstandtools.ArmorStandEditorCommand
import net.bandithemepark.bandicore.server.tools.armorstandtools.ArmorStandEditorEvents
import net.bandithemepark.bandicore.server.essentials.afk.AfkManager
import net.bandithemepark.bandicore.server.essentials.ranks.RankManager
import net.bandithemepark.bandicore.server.essentials.ranks.SetRankCommand
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag
import net.bandithemepark.bandicore.server.essentials.ranks.scoreboard.BandiScoreboard
import net.bandithemepark.bandicore.server.mode.ServerModeCommand
import net.bandithemepark.bandicore.server.restart.Restart
import net.bandithemepark.bandicore.server.restart.RestartCommand
import net.bandithemepark.bandicore.server.statistics.Playtime
import net.bandithemepark.bandicore.server.tools.painter.ItemPainter
import net.bandithemepark.bandicore.server.translations.Language
import net.bandithemepark.bandicore.server.translations.LanguageUtil
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.chat.prompt.ChatPrompt
import net.bandithemepark.bandicore.util.npc.NPC
import okhttp3.OkHttpClient
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import me.m56738.smoothcoasters.api.SmoothCoastersAPI
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.LogFlumeAttraction
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.RupsbaanAttraction
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.RupsbaanCart
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanAttraction
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.network.audioserver.AudioCommand
import net.bandithemepark.bandicore.network.audioserver.AudioServerTimer
import net.bandithemepark.bandicore.network.audioserver.VolumeCommand
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerEventListeners
import net.bandithemepark.bandicore.network.audioserver.map.ChunkRendererCommand
import net.bandithemepark.bandicore.network.audioserver.ride.SpecialAudioManagement
import net.bandithemepark.bandicore.network.backend.audioserver.BackendAudioServerCredentials
import net.bandithemepark.bandicore.network.mqtt.MQTTConnector
import net.bandithemepark.bandicore.network.queue.QueueCommand
import net.bandithemepark.bandicore.park.attractions.AttractionCommand
import net.bandithemepark.bandicore.park.attractions.gates.OneWayGateEvents
import net.bandithemepark.bandicore.park.attractions.gates.VIPDoorEvents
import net.bandithemepark.bandicore.park.attractions.ridecounter.RidecounterManager
import net.bandithemepark.bandicore.park.attractions.info.AttractionInfoBoard
import net.bandithemepark.bandicore.park.attractions.menu.AttractionMenu
import net.bandithemepark.bandicore.park.attractions.mode.*
import net.bandithemepark.bandicore.park.attractions.ridecounter.RideCounterMenu
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPCommand
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPEvents
import net.bandithemepark.bandicore.park.attractions.rideop.camera.RideOPCamera
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.park.cosmetics.CosmeticManager
import net.bandithemepark.bandicore.park.cosmetics.balloons.Balloon
import net.bandithemepark.bandicore.park.cosmetics.command.EquipCommand
import net.bandithemepark.bandicore.park.cosmetics.command.UnEquipCommand
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.*
import net.bandithemepark.bandicore.park.cosmetics.requirements.types.AchievementCosmeticRequirement
import net.bandithemepark.bandicore.park.cosmetics.requirements.types.RidecounterCosmeticRequirement
import net.bandithemepark.bandicore.park.cosmetics.requirements.types.VIPCosmeticRequirement
import net.bandithemepark.bandicore.park.cosmetics.types.*
import net.bandithemepark.bandicore.park.modsupport.SmoothCoastersChecker
import net.bandithemepark.bandicore.park.npc.ThemeParkNPCSkin
import net.bandithemepark.bandicore.park.npc.path.editor.PathPointEditorCommand
import net.bandithemepark.bandicore.park.npc.path.editor.PathPointEditorEvents
import net.bandithemepark.bandicore.park.parkours.ParkourEvents
import net.bandithemepark.bandicore.park.parkours.ParkourManager
import net.bandithemepark.bandicore.park.shops.ShopManager
import net.bandithemepark.bandicore.park.shops.ShopMenu
import net.bandithemepark.bandicore.park.shops.ShopsMenu
import net.bandithemepark.bandicore.park.shops.ShopsMenuCommand
import net.bandithemepark.bandicore.park.shops.opener.ShopOpenerCommand
import net.bandithemepark.bandicore.server.BandiThemeParkCommand
import net.bandithemepark.bandicore.server.achievements.AchievementManager
import net.bandithemepark.bandicore.server.achievements.menu.AchievementCategoriesMenu
import net.bandithemepark.bandicore.server.achievements.menu.AchievementMenu
import net.bandithemepark.bandicore.server.achievements.menu.AchievementMenuCommand
import net.bandithemepark.bandicore.server.achievements.rewards.AchievementRewardCoins
import net.bandithemepark.bandicore.server.achievements.rewards.AchievementRewardItem
import net.bandithemepark.bandicore.server.achievements.triggers.AchievementTriggerRegionEnter
import net.bandithemepark.bandicore.server.achievements.triggers.AchievementTriggerRidecounterIncrease
import net.bandithemepark.bandicore.server.achievements.triggers.AchievementTriggerSpecial
import net.bandithemepark.bandicore.server.animatronics.AnimatronicManager
import net.bandithemepark.bandicore.server.custom.blocks.CustomBlock
import net.bandithemepark.bandicore.server.custom.blocks.CustomBlockMenu
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerRigTest
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin
import net.bandithemepark.bandicore.server.custom.player.editor.CustomPlayerEditor
import net.bandithemepark.bandicore.server.discord.DiscordConnectCommand
import net.bandithemepark.bandicore.server.effects.EffectCommand
import net.bandithemepark.bandicore.server.effects.EffectManager
import net.bandithemepark.bandicore.server.effects.types.AnimatronicEffect
import net.bandithemepark.bandicore.server.effects.types.DarkOverlayEffect
import net.bandithemepark.bandicore.server.effects.types.ParticleEffect
import net.bandithemepark.bandicore.server.effects.types.SpatialAudioEffect
import net.bandithemepark.bandicore.server.essentials.*
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinsListener
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import net.bandithemepark.bandicore.server.essentials.coins.boosters.CoinBoosterManager
import net.bandithemepark.bandicore.server.essentials.moderation.BanCommand
import net.bandithemepark.bandicore.server.essentials.moderation.KickCommand
import net.bandithemepark.bandicore.server.essentials.moderation.UnBanCommand
import net.bandithemepark.bandicore.server.essentials.ranks.test.RankTest
import net.bandithemepark.bandicore.server.essentials.worlds.WorldCommands
import net.bandithemepark.bandicore.server.essentials.worlds.WorldManager
import net.bandithemepark.bandicore.server.essentials.teleport.BackCommand
import net.bandithemepark.bandicore.server.essentials.teleport.SelfCommand
import net.bandithemepark.bandicore.server.essentials.teleport.TeleportCommand
import net.bandithemepark.bandicore.server.essentials.warps.*
import net.bandithemepark.bandicore.server.leaderboards.LeaderboardTest
import net.bandithemepark.bandicore.server.menu.MainMenu
import net.bandithemepark.bandicore.server.minigames.Minigame
import net.bandithemepark.bandicore.server.minigames.MinigameTest
import net.bandithemepark.bandicore.server.minigames.casino.Casino
import net.bandithemepark.bandicore.server.minigames.casino.slotmachine.SlotMachineEvents
import net.bandithemepark.bandicore.server.minigames.cooking.CookingEvents
import net.bandithemepark.bandicore.server.minigames.cooking.CookingMinigame
import net.bandithemepark.bandicore.server.placeables.PlaceableEvents
import net.bandithemepark.bandicore.server.placeables.PlaceableManager
import net.bandithemepark.bandicore.server.placeables.PlaceableRemoveCommand
import net.bandithemepark.bandicore.server.regions.BandiRegionCommand
import net.bandithemepark.bandicore.server.regions.BandiRegionManager
import net.bandithemepark.bandicore.server.regions.events.BandiRegionEvents
import net.bandithemepark.bandicore.util.BandiConfig
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.HoverableEntity
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener

class BandiCore: JavaPlugin() {
    companion object {
        lateinit var instance: BandiCore
        val pluginScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }
    var devMode = true

    var startTime = 0L
    lateinit var config: BandiConfig
    lateinit var server: Server
    lateinit var trackManager: TrackManager
    lateinit var afkManager: AfkManager
    lateinit var smoothCoastersAPI: SmoothCoastersAPI
    lateinit var mqttConnector: MQTTConnector
    lateinit var customBlockManager: CustomBlock.Manager
    lateinit var worldManager: WorldManager
    lateinit var coinManager: CoinManager
    lateinit var regionManager: BandiRegionManager
    lateinit var animatronicManager: AnimatronicManager
    lateinit var effectManager: EffectManager
    lateinit var placeableManager: PlaceableManager
    lateinit var cosmeticManager: CosmeticManager
    lateinit var shopManager: ShopManager
    lateinit var casino: Casino
    lateinit var parkourManager: ParkourManager

    var okHttpClient = OkHttpClient()
    var restarter = Restart()

    override fun onEnable() {
        instance = this
        if(Bukkit.getServer().port == 25566) {
            devMode = false
            Bukkit.getConsoleSender().sendMessage("Starting in PRODUCTION mode")
        } else {
            logger.warning("Starting in DEVELOPMENT mode")
        }

        startTime = System.currentTimeMillis()
        Util.debug("AudioServer", "Server start time set to $startTime")
        smoothCoastersAPI = SmoothCoastersAPI(this)

        // Saving the default settings
        if(!dataFolder.exists()) {
            val fm = FileManager()
            fm.getConfig("ranks.yml").saveDefaultConfig()
            fm.getConfig("afkTitles.yml").saveDefaultConfig()
            fm.getConfig("blocks.yml").saveDefaultConfig()

            fm.getConfig("data/placed-blocks.yml").saveDefaultConfig()

            fm.getConfig("translations/english/crew.json").saveDefaultConfig()
            fm.getConfig("translations/english/player.json").saveDefaultConfig()
        }
        config = BandiConfig()

        worldManager = WorldManager()
        registerAchievementRewardTypes()

        // Setting up the server
        server = Server()
        server.themePark.setup()
        server.warpManager.loadWarps()
        prepareSettings()
        coinManager = CoinManager()
        CoinBoosterManager.getInstance()

        // Connecting to the MQTT server and registering listeners
        CoinsListener().register()
        AudioServerEventListeners.ListenerMQTT().register()
        mqttConnector = MQTTConnector()
        AudioServerTimer().startTimer()

        afkManager = AfkManager()
        HoverableEntity.setup()

        // Setting up the track manager
        trackManager = TrackManager(BezierSpline(), 25, 0.02)
        trackManager.setup()

        // Registering everything
        registerCommands()
        registerEvents()

        // Setup achievements after triggers have been registered
        server.achievementManager.setup()
        server.achievementManager.register("achievements")

        // Everything related to cosmetics and shops
        registerCosmeticTypes()
        registerCosmeticRequirementTypes()
        shopManager = ShopManager()
        cosmeticManager = CosmeticManager()
        cosmeticManager.setup()

        // Starting the necessary timers
        //NPC.startTimer()
        PlayerNameTag.Timer().runTaskTimerAsynchronously(this, 0, 1)
        Backpack.Timer().runTaskTimerAsynchronously(this, 0, 1)
        Playtime.startTimer()
        KalibaEffects()
        AmbientEffect.startTimer()

        // Registering packet listeners
        PacketEntity.PacketListeners.startListeners()

        // Loading custom blocks
        customBlockManager = CustomBlock.Manager()
        customBlockManager.loadPlaced()

        // Setting up the rest related to attractions
        registerAttractionModes()
        registerRideOPs()
        registerAttractions()

        regionManager = BandiRegionManager()
        regionManager.loadAll()

        animatronicManager = AnimatronicManager()
        registerEffectTypes()
        effectManager = EffectManager()
        effectManager.playServerStartEffects()

        placeableManager = PlaceableManager()
        placeableManager.loadPlaced()

        registerMinigames()
        registerDebuggables()
        registerTests()
        Balloon.startTimer()

        // Things that need to be done for players who are already online (Like when a reload happens)
        forOnlinePlayers()

        // Registering the messaging channel for sending players and banning
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord")
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "bandicore:ban")
    }

    override fun onDisable() {
        // Resetting all attractions
        RideOP.rideOPs.forEach { it.onServerStop() }

        smoothCoastersAPI.unregister()

        // Deleting/removing entities
        trackManager.vehicleManager.deSpawnAllVehicles()
        PacketEntity.removeAll()
        NPC.removeAll()

        // Removing player permissions and hiding boss bars
        for(player in Bukkit.getOnlinePlayers()) {
            server.rankManager.loadedPlayerRanks[player]?.removePermissions(player)
            player.getBossBar()?.hideBossBar()
        }

        // Disconnect any clients
        mqttConnector.disconnect()
        pluginScope.coroutineContext.cancelChildren()
    }

    private fun registerCommands() {
        registerCommand("servermode", ServerModeCommand())
        registerCommand("ast", ArmorStandEditorCommand())
        registerCommand("painter", ItemPainter.Command())
        registerCommand("track", TrackCommand.Command())
        registerCommand("trackvehicle", TrackVehicleCommand.Command())
        registerCommand("gamemode", GamemodeCommand())
        registerCommand("setlanguage", Language.Command())
        registerCommand("setrank", SetRankCommand())
        registerCommand("bandirestart", RestartCommand())
        registerCommand("vanish", VanishCommand())
        registerCommand("queue", QueueCommand())
        registerCommand("bandikea", CustomBlock.Command())
        registerCommand("loadworld", WorldCommands())
        registerCommand("unloadworld", WorldCommands())
        registerCommand("worldtp", WorldCommands())
        registerCommand("teleport", TeleportCommand())
        registerCommand("back", BackCommand())
        registerCommand("self", SelfCommand())
        registerCommand("day", TimeManagement())
        registerCommand("night", TimeManagement())
        registerCommand("region", BandiRegionCommand())
        registerCommand("rideop", RideOPCommand())
        registerCommand("attraction", AttractionCommand())
        registerCommand("chunkrenderer", ChunkRendererCommand())
        registerCommand("patheditor", PathPointEditorCommand())
        registerCommand("volume", VolumeCommand())
        registerCommand("warp", WarpCommand())
        registerCommand("deletewarp", DeleteWarpCommand())
        registerCommand("setwarp", SetWarpCommand())
        registerCommand("nearestwarp", NearestWarpCommand())
        registerCommand("getskin", CustomPlayerSkin.Command())
        registerCommand("audio", AudioCommand())
        registerCommand("achievements", AchievementMenuCommand())
        registerCommand("effect", EffectCommand())
        registerCommand("removenearplaceables", PlaceableRemoveCommand())
        registerCommand("dressingroom", DressingRoomCommand())
        registerCommand("cooking", MinigameTest())
        registerCommand("bandithemepark", BandiThemeParkCommand())
        registerCommand("equip", EquipCommand())
        registerCommand("unequip", UnEquipCommand())
        registerCommand("fly", FlyCommand())
        registerCommand("message", MessageCommand())
        registerCommand("react", ReactCommand())
        registerCommand("shopopener", ShopOpenerCommand())
        registerCommand("spawn", SpawnCommand())
        registerCommand("ban", BanCommand())
        registerCommand("unban", UnBanCommand())
        registerCommand("kick", KickCommand())
        registerCommand("shops", ShopsMenuCommand())
        registerCommand("ranktest", RankTest.Command())
        registerCommand("discord", DiscordConnectCommand())
    }

    private fun registerEvents() {
        registerEvents(PacketEntity.Events())
        registerEvents(NPC.Events())
        registerEvents(ArmorStandEditorEvents())
        registerEvents(ItemPainter.Events())
        registerEvents(Language.Events())
        registerEvents(RankManager.Events())
        registerEvents(ChatPrompt.Events())
        registerEvents(BandiScoreboard.Events())
        registerEvents(PlayerNameTag.Events())
        registerEvents(TrackVehicleEditor.Events())
        registerEvents(JoinMessages())
        registerEvents(Playtime.Events())
        registerEvents(PacketEntitySeat.Events())
        registerEvents(CustomPlayerEditor.Events())
        registerEvents(SeatAttachment.Listeners())
        registerEvents(CustomBlock.Events())
        registerEvents(CustomBlockMenu.Events())
        registerEvents(BackCommand.Events())
        registerEvents(PlayerBossBar.Events())
        registerEvents(ColoredSigns())
        registerEvents(BandiRegionEvents())
        registerEvents(RideOPEvents())
        registerEvents(AttractionMenu.Events())
        registerEvents(RupsbaanCart.Events())
        registerEvents(SmoothCoastersChecker())
        registerEvents(PathPointEditorEvents())
        registerEvents(ThemeParkNPCSkin.Caching.Events())
        registerEvents(AudioServerEventListeners.BukkitEventListeners())
        registerEvents(AttractionInfoBoard.Events())
        registerEvents(RideCounterMenu.Events())
        registerEvents(CustomPlayerSkin.Events())
        registerEvents(BackendAudioServerCredentials.Events())
        registerEvents(AudioCommand.Events())
        registerEvents(RidecounterManager.Events())
        registerEvents(AchievementManager.Events())
        registerEvents(AchievementTriggerRegionEnter())
        registerEvents(AchievementTriggerRidecounterIncrease())
        registerEvents(AchievementTriggerSpecial())
        registerEvents(AchievementCategoriesMenu.Events())
        registerEvents(AchievementMenu.Events())
        registerEvents(SpecialAudioManagement())
        registerEvents(PlaceableEvents())
        registerEvents(RideOPCamera.Events())
        registerEvents(CosmeticManager.Events())
        registerEvents(DressingRoomEvents())
        registerEvents(CanCanRideOP.Events())
        registerEvents(CookingEvents())
        registerEvents(SlotMachineEvents())
        registerEvents(BandiThemeParkCommand())
        registerEvents(HandheldCosmetic.Events())
        registerEvents(DressingRoomMenu.Events())
        registerEvents(DressingRoomCategoryMenu.Events())
        registerEvents(Backpack.Events())
        registerEvents(VIPDoorEvents())
        registerEvents(ProtectionEvents())
        registerEvents(OneWayGateEvents())
        registerEvents(ShopMenu.Events())
        registerEvents(JoinItems())
        registerEvents(DressingRoomColorMenu.Events())
        registerEvents(Balloon.Events())
        registerEvents(ShopsMenu.Events())
        registerEvents(MainMenu.Events())
        registerEvents(ParkourEvents())
    }

    private fun prepareSettings() {
        BackendSetting("serverMode").createIfNotExistElseSet(server.serverMode.id)
        BackendSetting("motd").createIfNotExistElseSet(server.serverMode.motd)
    }

    private fun forOnlinePlayers() {
        for(player in Bukkit.getOnlinePlayers()) {
            PlayerBossBar.createFor(player)
            LanguageUtil.loadLanguage(player)
            server.rankManager.loadRank(player)
            server.scoreboard.showFor(player)
            CustomPlayerSkin.generateSkin(player)
            server.ridecounterManager.loadOf(player)
            server.achievementManager.loadOf(player)
        }
    }

    private fun registerAttractionModes() {
        AttractionModeOpen().register()
        AttractionModeClosed().register()
        AttractionModeNew().register()
        AttractionModeVIP().register()
        AttractionModeCrew().register()
        AttractionModeClosedShown().register()
    }

    private fun registerRideOPs() {
        LogFlumeRideOP().register()
        RupsbaanRideOP().register()
        CanCanRideOP().register()
    }

    private fun registerAttractions() {
        LogFlumeAttraction().register()
        RupsbaanAttraction().register()
        CanCanAttraction().register()
    }

    private fun registerAchievementRewardTypes() {
        AchievementRewardCoins().register()
        AchievementRewardItem().register()
    }

    private fun registerEffectTypes() {
        AnimatronicEffect().register()
        SpatialAudioEffect().register()
        ParticleEffect().register()
        DarkOverlayEffect().register()
    }

    private fun registerCosmeticTypes() {
        HatCosmetic().register()
        HandheldCosmetic().register()
        TitleCosmetic().register()
        BackpackCosmetic().register()
        BootsCosmetic().register()
        LeggingsCosmetic().register()
        ChestplateCosmetic().register()
        BalloonCosmetic().register()
    }

    private fun registerCosmeticRequirementTypes() {
        VIPCosmeticRequirement().register()
        RidecounterCosmeticRequirement().register()
        AchievementCosmeticRequirement().register()
    }

    private fun registerMinigames() {
        casino = Casino()
        parkourManager = ParkourManager()
        parkourManager.setup()

        CookingMinigame().register()
        Minigame.startTimer()
    }

    private fun registerDebuggables() {
        CanCanRideOP.Debug().register("cancan")
    }

    private fun registerTests() {
        Balloon(ItemFactory(Material.RED_WOOL).build(), Bukkit.getWorld("world")!!).register("balloon")
        CustomPlayerRigTest().register("custom-player-shaders")
        RankTest.getInstance()
        LeaderboardTest().register("leaderboard")
    }
    
    // Utils
    private fun registerEvents(listener: Listener) {
        getServer().pluginManager.registerEvents(listener, this)
    }
    
    private fun registerCommand(name: String, executor: CommandExecutor) {
        getCommand(name)!!.setExecutor(executor)
    }
}