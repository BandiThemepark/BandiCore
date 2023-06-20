package net.bandithemepark.bandicore.network.audioserver

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class AudioServerTimer: BukkitRunnable() {
    var currentTick = 0
    override fun run() {
        currentTick++
        if(currentTick == 2) {
            currentTick = 0

            SpatialAudioSource.updateSources()

            BandiCore.instance.mqttConnector.sendMessage("/audioclient/playerlocations", getCurrentPlayerInfo().toString())
        }
    }

    fun getCurrentPlayerInfo(): JsonObject {
        val json = JsonObject()

        val array = JsonArray()
        for(player in Bukkit.getOnlinePlayers()) {
            val playerJson = JsonObject()

            playerJson.addProperty("uuid", player.uniqueId.toString())
            playerJson.addProperty("name", player.name)
            playerJson.addProperty("world", player.location.world.name)
            playerJson.addProperty("x", player.location.x)
            playerJson.addProperty("y", player.location.y)
            playerJson.addProperty("z", player.location.z)
            playerJson.addProperty("pitch", player.location.pitch)
            playerJson.addProperty("yaw", player.location.yaw)

            array.add(playerJson)
        }

        json.add("players", array)

        return json
    }
}