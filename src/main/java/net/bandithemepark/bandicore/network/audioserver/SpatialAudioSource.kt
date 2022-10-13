package net.bandithemepark.bandicore.network.audioserver

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Location

class SpatialAudioSource(var id: String, location: Location, var audioSourceId: String) {
    var location = location
        set(value) {
            field = value
            updatedSources.add(this)
        }

    fun toJson(): JsonObject {
        val json = JsonObject()

        json.addProperty("id", id)
        json.addProperty("world", location.world.name)
        json.addProperty("x", location.x)
        json.addProperty("y", location.y)
        json.addProperty("z", location.z)
        json.addProperty("audioSourceId", audioSourceId)

        return json
    }

    companion object {
        val active = mutableListOf<SpatialAudioSource>()
        var updatedSources = mutableListOf<SpatialAudioSource>()

        fun updateSources() {
            if(updatedSources.isEmpty()) return

            // Sending the updates to the AudioClients
            val messageJson = JsonObject()

            val array = JsonArray()
            updatedSources.forEach {
                array.add(it.toJson())
            }

            messageJson.add("sources", array)
            BandiCore.instance.mqttConnector.sendMessage("/audioclient/sourcemovement", messageJson.toString())

            // Clearing the updated sources for the next turn
            updatedSources.clear()
        }
    }
}