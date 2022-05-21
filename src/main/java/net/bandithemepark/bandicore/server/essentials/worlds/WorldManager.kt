package net.bandithemepark.bandicore.server.essentials.worlds

import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit
import org.bukkit.WorldCreator

class WorldManager {
    var loadedWorldNames = mutableListOf<String>()

    init {
        setupWorlds()
    }

    private fun setupWorlds() {
        // Load all worlds from the config
        val fm = FileManager()

        if(fm.getConfig("config.yml").get().contains("worlds")) return
        loadedWorldNames = fm.getConfig("config.yml").get().getStringList("worlds")

        // Loading the worlds if they don't exist
        for(worldName in loadedWorldNames) {
            if(Bukkit.getWorld(worldName) == null) {
                val worldCreator = WorldCreator(worldName)
                Bukkit.getServer().createWorld(worldCreator)
            }
        }
    }

    fun loadNewWorld(id: String): Boolean {
        return try {
            val worldCreator = WorldCreator(id)
            Bukkit.getServer().createWorld(worldCreator)

            val fm = FileManager()
            loadedWorldNames.add(id)
            fm.getConfig("config.yml").get().set("worlds", loadedWorldNames)
            fm.saveConfig("config.yml")

            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun unloadWorld(id: String) {
        Bukkit.getServer().unloadWorld(id, true)

        val fm = FileManager()
        loadedWorldNames.remove(id)
        fm.getConfig("config.yml").get().set("worlds", loadedWorldNames)
        fm.saveConfig("config.yml")
    }
}