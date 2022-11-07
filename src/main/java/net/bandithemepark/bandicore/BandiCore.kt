package net.bandithemepark.bandicore

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
import net.bandithemepark.bandicore.server.custom.player.CustomPlayer
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
import net.bandithemepark.bandicore.util.npc.NPCPathfinding
import okhttp3.OkHttpClient
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import me.m56738.smoothcoasters.api.SmoothCoastersAPI
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.LogFlumeAttraction
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.RupsbaanAttraction
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.RupsbaanCart
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.network.audioserver.AudioCommand
import net.bandithemepark.bandicore.network.audioserver.AudioServerTimer
import net.bandithemepark.bandicore.network.audioserver.VolumeCommand
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerEventListeners
import net.bandithemepark.bandicore.network.audioserver.map.ChunkRendererCommand
import net.bandithemepark.bandicore.network.backend.audioserver.BackendAudioServerCredentials
import net.bandithemepark.bandicore.network.mqtt.MQTTConnector
import net.bandithemepark.bandicore.network.queue.QueueCommand
import net.bandithemepark.bandicore.park.attractions.AttractionCommand
import net.bandithemepark.bandicore.park.attractions.RidecounterManager
import net.bandithemepark.bandicore.park.attractions.info.AttractionInfoBoard
import net.bandithemepark.bandicore.park.attractions.menu.AttractionMenu
import net.bandithemepark.bandicore.park.attractions.mode.*
import net.bandithemepark.bandicore.park.attractions.ridecounter.RideCounterMenu
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPCommand
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPEvents
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.park.modsupport.SmoothCoastersChecker
import net.bandithemepark.bandicore.park.npc.ThemeParkNPCSkin
import net.bandithemepark.bandicore.park.npc.path.editor.PathPointEditorCommand
import net.bandithemepark.bandicore.park.npc.path.editor.PathPointEditorEvents
import net.bandithemepark.bandicore.server.animation.rig.RigTest
import net.bandithemepark.bandicore.server.custom.blocks.CustomBlock
import net.bandithemepark.bandicore.server.custom.blocks.CustomBlockMenu
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin
import net.bandithemepark.bandicore.server.custom.player.animation.CustomPlayerAnimation
import net.bandithemepark.bandicore.server.custom.player.editor.CustomPlayerEditor
import net.bandithemepark.bandicore.server.essentials.*
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinsListener
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import net.bandithemepark.bandicore.server.essentials.worlds.WorldCommands
import net.bandithemepark.bandicore.server.essentials.worlds.WorldManager
import net.bandithemepark.bandicore.server.essentials.teleport.BackCommand
import net.bandithemepark.bandicore.server.essentials.teleport.SelfCommand
import net.bandithemepark.bandicore.server.essentials.teleport.TeleportCommand
import net.bandithemepark.bandicore.server.essentials.warps.DeleteWarpCommand
import net.bandithemepark.bandicore.server.essentials.warps.NearestWarpCommand
import net.bandithemepark.bandicore.server.essentials.warps.SetWarpCommand
import net.bandithemepark.bandicore.server.essentials.warps.WarpCommand
import net.bandithemepark.bandicore.server.regions.BandiRegionCommand
import net.bandithemepark.bandicore.server.regions.BandiRegionManager
import net.bandithemepark.bandicore.server.regions.events.BandiRegionEvents
import net.bandithemepark.bandicore.util.entity.HoverableEntity
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat

class BandiCore: JavaPlugin() {
    companion object {
        lateinit var instance: BandiCore
    }

    var startTime = 0L
    lateinit var server: Server
    lateinit var trackManager: TrackManager
    lateinit var afkManager: AfkManager
    lateinit var smoothCoastersAPI: SmoothCoastersAPI
    lateinit var mqttConnector: MQTTConnector
    lateinit var customBlockManager: CustomBlock.Manager
    lateinit var worldManager: WorldManager
    lateinit var coinManager: CoinManager
    lateinit var regionManager: BandiRegionManager

    var okHttpClient = OkHttpClient()
    var restarter = Restart()

    override fun onEnable() {
        instance = this
        startTime = System.currentTimeMillis()
        smoothCoastersAPI = SmoothCoastersAPI(this)

        // Saving the default settings
        if(!dataFolder.exists()) {
            val fm = FileManager()
            fm.getConfig("config.yml").saveDefaultConfig()
            fm.getConfig("ranks.yml").saveDefaultConfig()
            fm.getConfig("afkTitles.yml").saveDefaultConfig()
            fm.getConfig("blocks.yml").saveDefaultConfig()

            fm.getConfig("data/placed-blocks.yml").saveDefaultConfig()

            fm.getConfig("translations/english/crew.json").saveDefaultConfig()
            fm.getConfig("translations/english/player.json").saveDefaultConfig()
        }

        worldManager = WorldManager()

        // Setting up the server
        server = Server()
        server.themePark.setup()
        server.warpManager.loadWarps()
        prepareSettings()
        coinManager = CoinManager()

        // Connecting to the MQTT server and registering listeners
        CoinsListener().register()
        AudioServerEventListeners.ListenerMQTT().register()
        mqttConnector = MQTTConnector()
        AudioServerTimer().runTaskTimer(this, 20, 1)

        afkManager = AfkManager()
        HoverableEntity.setup()

        // Setting up the track manager
        trackManager = TrackManager(BezierSpline(), 25, 0.02)
        trackManager.setup()

        // Registering everything
        registerCommands()
        registerEvents()

        // Starting the necessary timers
        NPC.startTimer()
        PlayerNameTag.Timer().runTaskTimerAsynchronously(this, 0, 1)
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

        // Things that need to be done for players who are already online (Like when a reload happens)
        forOnlinePlayers()

//        runAngleInterpolationTest { a1, a2, t ->
//            return@runAngleInterpolationTest MathUtil.interpolateAngles(a1, a2, t)
//        }

        // Registering the messaging channel for sending players
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord")
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
            player.getBossBar().hideBossBar()
        }

        // Disconnect any clients
        mqttConnector.disconnect()
    }

    private fun registerCommands() {
        getCommand("servermode")!!.setExecutor(ServerModeCommand())
        getCommand("npctest")!!.setExecutor(NPCPathfinding.TestCommand())
        getCommand("ast")!!.setExecutor(ArmorStandEditorCommand())
        getCommand("customplayertest")!!.setExecutor(CustomPlayer.TestCommand())
        getCommand("painter")!!.setExecutor(ItemPainter.Command())
        getCommand("track")!!.setExecutor(TrackCommand.Command())
        getCommand("trackvehicle")!!.setExecutor(TrackVehicleCommand.Command())
        getCommand("gamemode")!!.setExecutor(GamemodeCommand())
        getCommand("setlanguage")!!.setExecutor(Language.Command())
        getCommand("setrank")!!.setExecutor(SetRankCommand())
        getCommand("bandirestart")!!.setExecutor(RestartCommand())
        getCommand("vanish")!!.setExecutor(VanishCommand())
        getCommand("queue")!!.setExecutor(QueueCommand())
        getCommand("customplayereditor")!!.setExecutor(CustomPlayerEditor.Command())
        getCommand("bandikea")!!.setExecutor(CustomBlock.Command())
        getCommand("loadworld")!!.setExecutor(WorldCommands())
        getCommand("unloadworld")!!.setExecutor(WorldCommands())
        getCommand("worldtp")!!.setExecutor(WorldCommands())
        getCommand("teleport")!!.setExecutor(TeleportCommand())
        getCommand("back")!!.setExecutor(BackCommand())
        getCommand("self")!!.setExecutor(SelfCommand())
        getCommand("day")!!.setExecutor(TimeManagement())
        getCommand("night")!!.setExecutor(TimeManagement())
        getCommand("region")!!.setExecutor(BandiRegionCommand())
        getCommand("rideop")!!.setExecutor(RideOPCommand())
        getCommand("attraction")!!.setExecutor(AttractionCommand())
        getCommand("chunkrenderer")!!.setExecutor(ChunkRendererCommand())
        getCommand("patheditor")!!.setExecutor(PathPointEditorCommand())
        getCommand("volume")!!.setExecutor(VolumeCommand())
        getCommand("warp")!!.setExecutor(WarpCommand())
        getCommand("deletewarp")!!.setExecutor(DeleteWarpCommand())
        getCommand("setwarp")!!.setExecutor(SetWarpCommand())
        getCommand("nearestwarp")!!.setExecutor(NearestWarpCommand())
        getCommand("rigtest")!!.setExecutor(RigTest())
        getCommand("getskin")!!.setExecutor(CustomPlayerSkin.Command())
        getCommand("audio")!!.setExecutor(AudioCommand())
    }

    private fun registerEvents() {
        getServer().pluginManager.registerEvents(PacketEntity.Events(), this)
        getServer().pluginManager.registerEvents(NPC.Events(), this)
        getServer().pluginManager.registerEvents(ArmorStandEditorEvents(), this)
        getServer().pluginManager.registerEvents(ItemPainter.Events(), this)
        getServer().pluginManager.registerEvents(Language.Events(), this)
        getServer().pluginManager.registerEvents(RankManager.Events(), this)
        getServer().pluginManager.registerEvents(ChatPrompt.Events(), this)
        getServer().pluginManager.registerEvents(BandiScoreboard.Events(), this)
        getServer().pluginManager.registerEvents(PlayerNameTag.Events(), this)
        getServer().pluginManager.registerEvents(TrackVehicleEditor.Events(), this)
        getServer().pluginManager.registerEvents(JoinMessages(), this)
        getServer().pluginManager.registerEvents(Playtime.Events(), this)
        getServer().pluginManager.registerEvents(PacketEntitySeat.Events(), this)
        getServer().pluginManager.registerEvents(CustomPlayerEditor.Events(), this)
        getServer().pluginManager.registerEvents(SeatAttachment.Listeners(), this)
        getServer().pluginManager.registerEvents(CustomBlock.Events(), this)
        getServer().pluginManager.registerEvents(CustomBlockMenu.Events(), this)
        getServer().pluginManager.registerEvents(BackCommand.Events(), this)
        getServer().pluginManager.registerEvents(PlayerBossBar.Events(), this)
        getServer().pluginManager.registerEvents(ColoredSigns(), this)
        getServer().pluginManager.registerEvents(BandiRegionEvents(), this)
        getServer().pluginManager.registerEvents(RideOPEvents(), this)
        getServer().pluginManager.registerEvents(AttractionMenu.Events(), this)
        getServer().pluginManager.registerEvents(RupsbaanCart.Events(), this)
        getServer().pluginManager.registerEvents(SmoothCoastersChecker(), this)
        getServer().pluginManager.registerEvents(PathPointEditorEvents(), this)
        getServer().pluginManager.registerEvents(ThemeParkNPCSkin.Caching.Events(), this)
        getServer().pluginManager.registerEvents(AudioServerEventListeners.BukkitEventListeners(), this)
        getServer().pluginManager.registerEvents(AttractionInfoBoard.Events(), this)
        getServer().pluginManager.registerEvents(RideCounterMenu.Events(), this)
        getServer().pluginManager.registerEvents(CustomPlayerSkin.Events(), this)
        getServer().pluginManager.registerEvents(BackendAudioServerCredentials.Events(), this)
        getServer().pluginManager.registerEvents(AudioCommand.Events(), this)
        getServer().pluginManager.registerEvents(RidecounterManager.Events(), this)
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
    }

    private fun registerAttractions() {
        LogFlumeAttraction().register()
        RupsbaanAttraction().register()
    }

//    private fun runAngleInterpolationTest(formula: (Double, Double, Double) -> Double) {
//        for (i in 0..20) Bukkit.broadcast(Component.text("Interpolating from -175 to 175, with T = ${i/10.0}: ${formula.invoke(-175.0, 175.0, i/20.0)}"))
//        for (i in 0..20) Bukkit.broadcast(Component.text("Interpolating from 175 to -175, with T = ${i/10.0}: ${formula.invoke(175.0, -175.0, i/20.0)}"))
//    }
}