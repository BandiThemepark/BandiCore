package net.bandithemepark.bandicore.server.animatronics

import com.google.gson.JsonArray

class AnimatronicPose(json: JsonArray) {
    val nodes = mutableListOf<AnimatronicNodePose>()

    init {
        for(nodeJson in json) {
            nodes.add(AnimatronicNodePose(nodeJson.asJsonObject))
        }
    }
}