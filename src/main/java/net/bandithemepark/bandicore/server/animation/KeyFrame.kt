package net.bandithemepark.bandicore.server.animation

import com.google.gson.JsonObject

class KeyFrame(val part: String, val time: Int, val channel: Channel, val interpolation: Interpolation, val data: KeyFrameData) {
    fun convertToJson(): JsonObject {
        val json = JsonObject()

        json.addProperty("part", part)
        json.addProperty("time", time)
        json.addProperty("channel", channel.name.lowercase())
        json.addProperty("interpolation", interpolation.name.lowercase())
        json.add("data", data.convertToJson())

        return json
    }

    companion object {
        fun getFromJson(json: JsonObject): KeyFrame {
            val part = json.get("part").asString
            val time = json.get("time").asInt
            val channel = Channel.valueOf(json.get("channel").asString.uppercase())
            val interpolation = Interpolation.valueOf(json.get("interpolation").asString.uppercase())
            val data = KeyFrameData.getFromJson(json.getAsJsonObject("data"), channel)

            return KeyFrame(part, time, channel, interpolation, data)
        }
    }
}