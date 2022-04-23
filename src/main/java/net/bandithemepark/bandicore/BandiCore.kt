package net.bandithemepark.bandicore

import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.bandithemepark.kaliba.KalibaEffects
import net.bandithemepark.bandicore.park.attractions.tracks.TrackManager
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.commands.TrackCommand
import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import net.bandithemepark.bandicore.park.effect.AmbientEffect
import net.bandithemepark.bandicore.server.Server
import net.bandithemepark.bandicore.server.tools.armorstandtools.ArmorStandEditorCommand
import net.bandithemepark.bandicore.server.tools.armorstandtools.ArmorStandEditorEvents
import net.bandithemepark.bandicore.server.customplayer.CustomPlayer
import net.bandithemepark.bandicore.server.essentials.GamemodeCommand
import net.bandithemepark.bandicore.server.mode.ServerModeCommand
import net.bandithemepark.bandicore.server.tools.painter.ItemPainter
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.npc.NPC
import net.bandithemepark.bandicore.util.npc.NPCPathfinding
import org.bukkit.plugin.java.JavaPlugin

class BandiCore: JavaPlugin() {
    companion object {
        lateinit var instance: BandiCore
    }

    lateinit var server: Server
    lateinit var trackManager: TrackManager

    override fun onEnable() {
        instance = this

        // Saving the default settings
        if(!dataFolder.exists()) {
            val fm = FileManager()
            fm.getConfig("config.yml").saveDefaultConfig()
            fm.getConfig("translations/english.json").saveDefaultConfig()
        }

        server = Server()
        trackManager = TrackManager(BezierSpline(), 25, 0.02)
        trackManager.setup()
        trackManager.vehicleManager.loadTrain("test", trackManager.loadedTracks[0], TrackPosition(trackManager.loadedTracks[0].nodes[0], 0), 20.0)

        // Registering everything
        registerCommands()
        registerEvents()

        // Starting the necessary timers
        NPC.startTimer()

        KalibaEffects()
        AmbientEffect.startTimer()

        // Setting up the network messaging channels
//        server.messenger.registerIncomingPluginChannel(this, "bandicore:queue", BandiQueueUpdater())
//        server.messenger.registerOutgoingPluginChannel(this, "bandicore:queue")

    }

    override fun onDisable() {
        // Deleting/removing entities
        trackManager.vehicleManager.deSpawnAllVehicles()
        PacketEntity.removeAll()
        NPC.removeAll()
    }

    private fun registerCommands() {
        getCommand("servermode")!!.setExecutor(ServerModeCommand())
        getCommand("npctest")!!.setExecutor(NPCPathfinding.TestCommand())
        getCommand("ast")!!.setExecutor(ArmorStandEditorCommand())
        getCommand("customplayertest")!!.setExecutor(CustomPlayer.TestCommand())
        getCommand("painter")!!.setExecutor(ItemPainter.Command())
        getCommand("track")!!.setExecutor(TrackCommand.Command())
        getCommand("gamemode")!!.setExecutor(GamemodeCommand())
    }

    private fun registerEvents() {
        getServer().pluginManager.registerEvents(PacketEntity.Events(), this)
        getServer().pluginManager.registerEvents(NPC.Events(), this)
        getServer().pluginManager.registerEvents(ArmorStandEditorEvents(), this)
        getServer().pluginManager.registerEvents(ItemPainter.Events(), this)
    }
}