package net.bandithemepark.bandicore.server.effects

import com.google.gson.JsonObject

class EffectKeyframe(json: JsonObject) {
    val time = json.get("time").asInt
    var type: EffectType = EffectType.getType(json.get("type").asString)!!.clone()

    init {
        type.loadSettings(json.getAsJsonObject("settings"))
        type.debug = json.get("debug").asBoolean
    }
}