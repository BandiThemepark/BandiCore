package net.bandithemepark.bandicore.server.animatronics

import com.google.gson.JsonObject

class AnimatronicAnimation(val name: String, animationJson: JsonObject) {
    val duration = animationJson.get("duration").asInt
    val frames = mutableListOf<AnimatronicAnimationFrame>()

    init {
        val framesJson = animationJson.get("frames").asJsonArray

        for(frameJson in framesJson) {
            frames.add(AnimatronicAnimationFrame(frameJson.asJsonObject))
        }
    }
}