package net.bandithemepark.bandicore.server.effects

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import java.io.File

/**
 * Represents an effect, created using our own effect scripting system
 * @param fileName The name of the file, without the extension. For example, if the file is named "effect.json", the fileName would be "effect". The default directory (plugins/BandiCore/effects/) is automatically included
 */
class Effect(val fileName: String) {
    var duration = 0
    var loop = false
    val keyframes = mutableListOf<EffectKeyframe>()

    init {
        val file = File("plugins/BandiCore/effects/$fileName.json")
        val json = JsonParser().parse(file.readText()).asJsonObject
        loadData(json)
    }

    /**
     * Loads the data from the JSON file, called automatically in the init block
     */
    private fun loadData(json: JsonObject) {
        // Load main values
        duration = json.get("duration").asInt
        loop = json.get("loop").asBoolean

        // Load all keyframes
        val keyframesJson = json.getAsJsonArray("keyframes")
        for(keyframeJson in keyframesJson) {
            keyframes.add(EffectKeyframe(keyframeJson.asJsonObject))
        }
    }

    var currentTick = 0

    /**
     * Starts playing the effect
     */
    fun play() {
        playCurrentFrame()
        BandiCore.instance.effectManager.playingEffects.add(this)
    }

    /**
     * Updates the effect
     * Called by the EffectManager every tick
     */
    fun tick() {
        currentTick++

        if(currentTick >= duration) {
            if(loop) {
                currentTick = 0
            } else {
                stop()
                return
            }
        }

        playCurrentFrame()
    }

    /**
     * Looks up keyframes with the current time, and plays them
     */
    private fun playCurrentFrame() {
        for(keyframe in keyframes.filter { it.time == currentTick }) {
            keyframe.type.onPlay()
        }

        for(keyframe in keyframes.filter { it.time <= currentTick }) {
            keyframe.type.onTick()
        }
    }

    /**
     * Stops the effect
     */
    fun stop() {
        for(keyframe in keyframes) keyframe.type.onEffectEnd()
        BandiCore.instance.effectManager.playingEffects.remove(this)
    }
}