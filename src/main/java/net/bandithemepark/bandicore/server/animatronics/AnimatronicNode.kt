package net.bandithemepark.bandicore.server.animatronics

import com.google.gson.JsonObject
import java.util.*

class AnimatronicNode(uuidString: String, nodeJson: JsonObject) {
    val uuid = UUID.fromString(uuidString)
    val name = nodeJson.get("name").asString
    val customModelData = nodeJson.get("custom_model_data").asInt
}