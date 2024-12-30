package net.bandithemepark.bandicore.server.essentials.worlds

import com.google.gson.JsonArray
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.WorldCreator

class WorldManager {
    var loadedWorldNames = mutableListOf<String>()

    init {
        setupWorlds()
    }

    private fun setupWorlds() {
        // Load all worlds from the config
        if(!BandiCore.instance.config.json.has("worlds")) return
        loadedWorldNames = BandiCore.instance.config.json.getAsJsonArray("worlds").map { it.asString }.toMutableList()

        // Loading the worlds if they don't exist
        for(worldName in loadedWorldNames) {
            if(Bukkit.getWorld(worldName) == null) {
                val worldCreator = WorldCreator(worldName)
                Bukkit.getServer().createWorld(worldCreator)
            }
        }

        Util.debug("Worlds", "Loaded ${loadedWorldNames.size} worlds")
    }

    fun loadNewWorld(id: String): Boolean {
        return try {
            val worldCreator = WorldCreator(id)
            Bukkit.getServer().createWorld(worldCreator)

            loadedWorldNames.add(id)

            val array = JsonArray()
            loadedWorldNames.forEach { array.add(it) }
            BandiCore.instance.config.json.add("worlds", array)
            BandiCore.instance.config.save()

            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun unloadWorld(id: String) {
        Bukkit.getServer().unloadWorld(id, true)

        loadedWorldNames.remove(id)
        val array = JsonArray()
        loadedWorldNames.forEach { array.add(it) }
        BandiCore.instance.config.json.add("worlds", array)
        BandiCore.instance.config.save()
    }
}