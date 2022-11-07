package net.bandithemepark.bandicore.server.animation

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.server.animation.KeyFrame
import net.bandithemepark.bandicore.server.animation.KeyFrameData
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.io.File

class Animation(val keyFrames: List<KeyFrame>) {
    fun convertToJson(): JsonObject {
        val json = JsonObject()

        val keyFramesArray = JsonArray()

        keyFrames.forEach {
            keyFramesArray.add(it.convertToJson())
        }

        json.add("keyframes", keyFramesArray)

        return json
    }

    fun getDataAt(time: Int, part: String, channel: Channel): KeyFrameData {
        val keyFrames = keyFrames.filter { it.part == part && it.channel == channel }
        if(keyFrames.isEmpty()) return KeyFrameData(0.0, 0.0, 0.0)
        if(keyFrames.size == 1) return keyFrames[0].data

        val keyFrame = keyFrames.filter { it.time <= time }.maxByOrNull { it.time }!!
        val nextKeyFrame = keyFrames.filter { it.time > time }.minByOrNull { it.time } ?: return keyFrame.data

        val t = (time - keyFrame.time).toDouble() / (nextKeyFrame.time - keyFrame.time).toDouble()

        return KeyFrameData(
            keyFrame.interpolation.interpolate(t, keyFrame.data.x, nextKeyFrame.data.x),
            keyFrame.interpolation.interpolate(t, keyFrame.data.y, nextKeyFrame.data.y),
            keyFrame.interpolation.interpolate(t, keyFrame.data.z, nextKeyFrame.data.z)
        )
    }

    fun getLength(): Int {
        return keyFrames.maxByOrNull { it.time }?.time ?: 0
    }

    companion object {
        fun getFromJson(json: JsonObject): Animation {
            val keyFrames = mutableListOf<KeyFrame>()

            for(keyFrameJson in json.getAsJsonArray("keyframes")) {
                val keyFrame = KeyFrame.getFromJson(keyFrameJson.asJsonObject)
                keyFrames.add(keyFrame)
            }

            return Animation(keyFrames)
        }

        fun load(id: String): Animation {
            val file = File("plugins/BandiCore/animations/$id.json")
            val json = JsonParser().parse(file.readText()).asJsonObject
            return getFromJson(json)
        }
    }
}