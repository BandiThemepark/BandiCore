package net.bandithemepark.bandicore.server.effects.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.server.effects.EffectType
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Location

class AnimatronicEffect: EffectType("animatronic") {
    lateinit var name: String
    lateinit var animationName: String
    var loop = false
    lateinit var baseLocation: Location
    lateinit var baseRotation: Quaternion

    var forwards: Boolean = false

    override fun loadSettings(json: JsonObject) {
        name = json.get("name").asString
        animationName = json.get("animation").asString
        loop = json.get("loop").asBoolean

        val baseLocationJson = json.getAsJsonObject("base_location")
        val world = Bukkit.getWorld(baseLocationJson.get("world").asString)
        val x = baseLocationJson.get("x").asDouble
        val y = baseLocationJson.get("y").asDouble
        val z = baseLocationJson.get("z").asDouble
        baseLocation = Location(world, x, y, z)

        val baseRotationJson = json.getAsJsonObject("base_rotation")
        val pitch = baseRotationJson.get("pitch").asDouble
        val yaw = baseRotationJson.get("yaw").asDouble
        val roll = baseRotationJson.get("roll").asDouble
        baseRotation = Quaternion.fromYawPitchRoll(pitch, yaw, roll)

        if(json.has("forwards")) forwards = json.get("forwards").asBoolean
    }

    lateinit var animatronic: Animatronic

    override fun onPlay() {
        animatronic = Animatronic(name)
        if(!animatronic.spawned) animatronic.spawn(baseLocation, baseRotation)
        animatronic.playAnimation(animationName, loop)

        if(debug) Util.debug("AnimatronicEffect", "Playing animation $animationName on animatronic $name")

        if(!loop && !forwards) {
            animatronic.onComplete = Runnable { animatronic.deSpawn() }
        }
    }

    override fun onTick() {

    }

    override fun onEffectEnd() {
        if(!animatronic.spawned) return

        animatronic.stopAnimation()
        animatronic.deSpawn()
    }
}