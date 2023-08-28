package net.bandithemepark.bandicore.network.audioserver

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Location
import java.util.*

class SpatialAudioSource(var uuid: UUID, private var location: Location, var audioSourceId: String, var looping: Boolean, var innerRange: Double, var outerRange: Double) {
    val world = location.world

    init {
        active.add(this)
        sourceUpdates.add(SpatialAudioSourceUpdateAdded(this))
    }

    fun setLocation(location: Location, volume: Double = 1.0, pitch: Double = 1.0) {
        this.location = location

        val existing = try {
            sourceUpdates.find { it.uuid == uuid && it is SpatialAudioSourceUpdateMovement }
        } catch (e: Exception) {
            null
        }

        if(existing != null) {
            (existing as SpatialAudioSourceUpdateMovement).toLocation = location
            existing.volume = volume
            existing.pitch = pitch
        } else {
            sourceUpdates.add(SpatialAudioSourceUpdateMovement(uuid, location, volume, pitch))
        }
    }

    fun remove() {
        active.remove(this)
        sourceUpdates.add(SpatialAudioSourceUpdateRemoved(uuid))
    }

    fun toJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("id", uuid.toString())
        json.addProperty("world", location.world.name)
        json.addProperty("x", location.x)
        json.addProperty("y", location.y)
        json.addProperty("z", location.z)
        json.addProperty("source", audioSourceId)
        json.addProperty("looping", looping)
        json.addProperty("innerRange", innerRange)
        json.addProperty("outerRange", outerRange)
        return json
    }

    companion object {
        val active = mutableListOf<SpatialAudioSource>()
        var sourceUpdates = mutableListOf<SpatialAudioSourceUpdate>()

        fun updateSources() {
            if(sourceUpdates.isEmpty()) return

            // Sending the updates to the AudioClients
            val messageJson = JsonObject()

            val array = JsonArray()
            sourceUpdates.forEach {
                try {
                    array.add(it.toJson())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            messageJson.add("sources", array)
            BandiCore.instance.mqttConnector.sendMessage("/audioclient/sourceupdates", messageJson.toString())

            // Clearing the updated sources for the next turn
            sourceUpdates.clear()
        }
    }

    abstract class SpatialAudioSourceUpdate(val uuid: UUID) {
        abstract fun toJson(): JsonObject
    }

    class SpatialAudioSourceUpdateMovement(uuid: UUID, var toLocation: Location, var volume: Double = 1.0, var pitch: Double = 1.0): SpatialAudioSourceUpdate(uuid) {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("action", "movement")
            json.addProperty("id", uuid.toString())
            json.addProperty("world", toLocation.world.name)
            json.addProperty("x", toLocation.x)
            json.addProperty("y", toLocation.y)
            json.addProperty("z", toLocation.z)
            json.addProperty("volume", volume)
            json.addProperty("pitch", pitch)
            return json
        }
    }

    class SpatialAudioSourceUpdateAdded(val source: SpatialAudioSource): SpatialAudioSourceUpdate(source.uuid) {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("action", "added")
            json.addProperty("id", source.uuid.toString())
            json.addProperty("world", source.location.world.name)
            json.addProperty("x", source.location.x)
            json.addProperty("y", source.location.y)
            json.addProperty("z", source.location.z)
            json.addProperty("source", source.audioSourceId)
            json.addProperty("looping", source.looping)
            json.addProperty("innerRange", source.innerRange)
            json.addProperty("outerRange", source.outerRange)
            return json
        }
    }

    class SpatialAudioSourceUpdateRemoved(uuid: UUID): SpatialAudioSourceUpdate(uuid) {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("action", "removed")
            json.addProperty("id", uuid.toString())
            return json
        }
    }
}