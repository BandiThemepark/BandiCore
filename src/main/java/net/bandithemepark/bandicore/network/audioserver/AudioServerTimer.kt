package net.bandithemepark.bandicore.network.audioserver

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.coroutines.Scheduler
import org.bukkit.Bukkit

class AudioServerTimer {
    private var currentTick = 0
    private fun run() {
        if(BandiCore.instance.devMode) return

        currentTick++
        if(currentTick == 1) {
            currentTick = 0

            SpatialAudioSource.updateSources()
            BandiCore.instance.mqttConnector.sendMessage("/audioclient/playerlocations", getCurrentPlayerInfo().toString())
        }
    }

    fun startTimer() {
        Scheduler.loopAsyncDelayed(50, 1000) {
            run()
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