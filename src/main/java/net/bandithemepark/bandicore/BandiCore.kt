package net.bandithemepark.bandicore

import net.bandithemepark.bandicore.network.queue.BandiQueueUpdater
import net.bandithemepark.bandicore.server.Server
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

        // Starting the necessary timers
        NPC.startTimer()

        // Setting up the network messaging channels
//        server.messenger.registerIncomingPluginChannel(this, "bandicore:queue", BandiQueueUpdater())
//        server.messenger.registerOutgoingPluginChannel(this, "bandicore:queue")

    }

    override fun onDisable() {
        // Deleting/removing entities
        NPC.removeAll()
    }

    private fun registerCommands() {
        getCommand("servermode")!!.setExecutor(ServerModeCommand())
        getCommand("npctest")!!.setExecutor(NPCPathfinding.TestCommand())
    }
}