package net.bandithemepark.bandicore.server.animation.rig

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animation.Animation
import net.bandithemepark.bandicore.server.animation.Channel
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.io.File

class Rig(val parts: MutableList<RigPart>, val basePosition: Location, val baseRotation: Vector) {
    var offset = Vector()
    var rotation = Vector()

    /**
     * Retrieves all parts on this rig, including children
     */
    fun getAllParts(): List<RigPart> {
        val parts = mutableListOf<RigPart>()
        this.parts.forEach { parts.addAll(it.getAllParts()) }
        return parts
    }

    /**
     * Spawns the rig
     */
    fun spawn() {
        val allParts = getAllParts().sortedBy { it.spawnOrder }
        allParts.forEach { it.spawn(basePosition.clone()) }
    }

    /**
     * Despawns the rig
     */
    fun deSpawn() {
        getAllParts().forEach { it.deSpawn() }
    }

    fun update() {
        val position = basePosition.toVector().add(offset)
        val rotation = baseRotation.clone().add(this.rotation)
        val rotationQuaternion = Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z)

        parts.forEach { it.update(position, rotation, rotationQuaternion) }
    }

    fun setToAnimationAt(animation: Animation, time: Int) {
        getAllParts().forEach { part ->
            part.offset = animation.getDataAt(time, part.name, Channel.POSITION).asVector()
            part.rotation = animation.getDataAt(time, part.name, Channel.ROTATION).asVector()
        }
    }

    fun playAnimation(id: String, looped: Boolean = false) {
        val rigAnimation = RigAnimation(BandiCore.instance.server.rigManager.loadAnimation(id), looped, this)
        BandiCore.instance.server.rigManager.activeAnimations.add(rigAnimation)
    }

    fun stopAnimation() {
        BandiCore.instance.server.rigManager.activeAnimations.removeAll { it.rig == this }
    }

    /**
     * Sets the visibility type of the custom player. It is recommended to do this before spawning
     * @param type The type to set it to
     */
    fun setVisibilityType(type: PacketEntity.VisibilityType) {
        getAllParts().forEach { it.armorStand.visibilityType = type }
    }

    /**
     * Sets the list the visibility type should be applied to. It is recommended to do this before spawning
     * @param list The list to set it to
     */
    fun setVisibilityList(list: MutableList<Player>) {
        getAllParts().forEach { it.armorStand.visibilityList = list }
    }

    fun toJson(): JsonObject {
        val json = JsonObject()

        getAllParts().forEach { part ->
            part.children.forEach { child ->
                child.parent = part.name
            }
        }

        val array = JsonArray()
        getAllParts().forEach { array.add(it.toJson()) }

        json.add("parts", array)

        return json
    }

    fun saveTo(id: String) {
        val file = File("plugins/BandiCore/rigs/$id.json")
        if(!file.exists()) file.createNewFile()
        file.writeText(toJson().toString())
    }

    companion object {
        fun getFromJson(json: JsonObject, basePosition: Location, baseRotation: Vector): Rig {
            val baseParts = mutableListOf<RigPart>()

            for(partsJson in json.getAsJsonArray("parts")) {
                val part = RigPart.getFromJson(partsJson.asJsonObject)
                baseParts.add(part)
            }

            val parts = mutableListOf<RigPart>()
            baseParts.forEach {
                if(it.parent != null) {
                    baseParts.find { part -> part.name == it.parent }?.children?.add(it)
                } else {
                    parts.add(it)
                }
            }

            return Rig(parts, basePosition, baseRotation)
        }

        fun load(id: String, basePosition: Location, baseRotation: Vector): Rig {
            val file = File("plugins/BandiCore/rigs/$id.json")
            val json = JsonParser().parse(file.readText()).asJsonObject
            return getFromJson(json, basePosition, baseRotation)
        }
    }
}