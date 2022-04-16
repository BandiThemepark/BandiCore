package net.bandithemepark.bandicore

import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.bandithemepark.kaliba.KalibaEffects
import net.bandithemepark.bandicore.park.effect.AmbientEffect
import net.bandithemepark.bandicore.server.Server
import net.bandithemepark.bandicore.server.customplayer.CustomPlayer
import net.bandithemepark.bandicore.server.mode.ServerModeCommand
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.npc.NPC
import net.bandithemepark.bandicore.util.npc.NPCPathfinding
import org.bukkit.plugin.java.JavaPlugin

class BandiCore: JavaPlugin() {
    companion object {
        lateinit var instance: BandiCore
    }

    lateinit var server: Server

    override fun onEnable() {
        instance = this

        // Saving the default settings
        if(!dataFolder.exists()) {
            val fm = FileManager()
            fm.getConfig("config.yml").saveDefaultConfig()
            fm.getConfig("translations/english.json").saveDefaultConfig()
        }

        server = Server()

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
        NPC.removeAll()
        PacketEntity.removeAll()
    }

    private fun registerCommands() {
        getCommand("servermode")!!.setExecutor(ServerModeCommand())
        getCommand("npctest")!!.setExecutor(NPCPathfinding.TestCommand())
        //getCommand("ast")!!.setExecutor(ArmorStandEditorCommand())
        getCommand("customplayertest")!!.setExecutor(CustomPlayer.TestCommand())
    }

    private fun registerEvents() {
        getServer().pluginManager.registerEvents(PacketEntity.Events(), this)
        getServer().pluginManager.registerEvents(NPC.Events(), this)
        getServer().pluginManager.registerEvents(NPCPathfinding.Events(), this)
        //getServer().pluginManager.registerEvents(ArmorStandEditorEvents(), this)
    }
}