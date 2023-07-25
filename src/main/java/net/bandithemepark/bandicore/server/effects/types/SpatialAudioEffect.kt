package net.bandithemepark.bandicore.server.effects.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.network.audioserver.SpatialAudioSource
import net.bandithemepark.bandicore.server.effects.EffectType
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

class SpatialAudioEffect: EffectType("spatial_audio") {
    lateinit var source: String
    var innerRange: Double = 0.0
    var outerRange: Double = 0.0
    lateinit var location: Location

    override fun loadSettings(json: JsonObject) {
        source = json.get("source").asString
        innerRange = json.get("innerRange").asDouble
        outerRange = json.get("outerRange").asDouble

        val locationJson = json.getAsJsonObject("location")
        val world = locationJson.get("world").asString
        val x = locationJson.get("x").asDouble
        val y = locationJson.get("y").asDouble
        val z = locationJson.get("z").asDouble
        location = Location(Bukkit.getWorld(world)!!, x, y, z)
    }

    var spatialAudio: SpatialAudioSource? = null
    override fun onPlay() {
        spatialAudio = SpatialAudioSource(
            UUID.randomUUID(),
            location,
            source,
            false,
            innerRange,
            outerRange
        )
    }

    override fun onEffectEnd() {
        spatialAudio?.remove()
    }
}
