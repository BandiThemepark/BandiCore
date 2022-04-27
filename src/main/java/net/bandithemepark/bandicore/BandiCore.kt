package net.bandithemepark.bandicore

import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.bandithemepark.kaliba.KalibaEffects
import net.bandithemepark.bandicore.network.backend.BackendSetting
import net.bandithemepark.bandicore.park.attractions.tracks.TrackManager
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.commands.TrackCommand
import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands.TrackVehicleCommand
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditor
import net.bandithemepark.bandicore.park.effect.AmbientEffect
import net.bandithemepark.bandicore.server.Server
import net.bandithemepark.bandicore.server.tools.armorstandtools.ArmorStandEditorCommand
import net.bandithemepark.bandicore.server.tools.armorstandtools.ArmorStandEditorEvents
import net.bandithemepark.bandicore.server.customplayer.CustomPlayer
import net.bandithemepark.bandicore.server.essentials.GamemodeCommand
import net.bandithemepark.bandicore.server.essentials.JoinMessages
import net.bandithemepark.bandicore.server.essentials.VanishCommand
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

class BandiCore: JavaPlugin() {
    companion object {
        lateinit var instance: BandiCore
    }

    lateinit var server: Server
    lateinit var trackManager: TrackManager
    lateinit var afkManager: AfkManager

    var okHttpClient = OkHttpClient()
    var restarter = Restart()

    override fun onEnable() {
        instance = this

        // Saving the default settings
        if(!dataFolder.exists()) {
            val fm = FileManager()
            fm.getConfig("config.yml").saveDefaultConfig()
            fm.getConfig("ranks.yml").saveDefaultConfig()
            fm.getConfig("translations/english/crew.json").saveDefaultConfig()
            fm.getConfig("translations/english/player.json").saveDefaultConfig()
        }

        server = Server()
        prepareSettings()

        afkManager = AfkManager()

        trackManager = TrackManager(BezierSpline(), 25, 0.02)
        trackManager.setup()
        trackManager.vehicleManager.loadTrain("test", trackManager.loadedTracks[0], TrackPosition(trackManager.loadedTracks[0].nodes[0], 0), 10.0)
        //(trackManager.vehicleManager.vehicles[0].members[0].attachments[0].type as ModelAttachment).debug = true

        // Registering everything
        registerCommands()
        registerEvents()

        // Starting the necessary timers
        NPC.startTimer()
        PlayerNameTag.Timer().runTaskTimerAsynchronously(this, 0, 1)
        Playtime.startTimer()
        KalibaEffects()
        AmbientEffect.startTimer()

        // Things that need to be done for players who are already online (Like when a reload happens)
        forOnlinePlayers()

        // Registering the messaging channel for sending players
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord")
    }

    override fun onDisable() {
        // Deleting/removing entities
        trackManager.vehicleManager.deSpawnAllVehicles()
        PacketEntity.removeAll()
        NPC.removeAll()

        for(player in Bukkit.getOnlinePlayers()) server.rankManager.loadedPlayerRanks[player]?.removePermissions(player)
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
    }

    private fun prepareSettings() {
        BackendSetting("serverMode").createIfNotExistElseSet(server.serverMode.id)
        BackendSetting("motd").createIfNotExistElseSet(server.serverMode.motd)
    }

    private fun forOnlinePlayers() {
        for(player in Bukkit.getOnlinePlayers()) {
            LanguageUtil.loadLanguage(player)
            server.rankManager.loadRank(player)
            server.scoreboard.showFor(player)
        }
    }
}