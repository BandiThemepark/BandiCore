package net.bandithemepark.bandicore.server.animatronics

import com.google.gson.JsonObject

class AnimatronicAnimationFrame(frameJson: JsonObject) {
    val time = (frameJson.get("time").asDouble * 20.0).toInt()
    val pose: AnimatronicPose

    init {
        pose = AnimatronicPose(frameJson.getAsJsonArray("nodes"))
    }
}