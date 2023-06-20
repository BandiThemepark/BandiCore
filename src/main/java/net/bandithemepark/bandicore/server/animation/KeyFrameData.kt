package net.bandithemepark.bandicore.server.animation

import com.google.gson.JsonObject
import org.bukkit.util.Vector

class KeyFrameData(val x: Double, val y: Double, val z: Double) {
    fun convertToJson(): JsonObject {
        val json = JsonObject()

        json.addProperty("x", x)
        json.addProperty("y", y)
        json.addProperty("z", z)

        return json
    }

    fun asVector(): Vector {
        return Vector(x, y, z)
    }

    companion object {
        fun getFromJson(json: JsonObject, channel: Channel): KeyFrameData {
            val x = json.get("x").asDouble
            val y = json.get("y").asDouble
            val z = json.get("z").asDouble

            return when(channel) {
                Channel.POSITION -> KeyFrameData(x, y, -z)
                Channel.ROTATION -> KeyFrameData(x, y, z)
            }
        }
    }
}