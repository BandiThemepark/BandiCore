package net.bandithemepark.bandicore.server.placeables

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.joml.Matrix4f
import kotlin.math.floor

class PlacedPlaceable(val location: Location, val type: PlaceableType, val rotation: Double, val color: Color?) {

    fun toJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("type", type.id)
        json.addProperty("world", location.world.name)
        json.addProperty("x", location.x)
        json.addProperty("y", location.y)
        json.addProperty("z", location.z)
        json.addProperty("rotation", rotation)

        if(color != null) {
            json.addProperty("colorRed", color.red)
            json.addProperty("colorGreen", color.green)
            json.addProperty("colorBlue", color.blue)
        }

        return json
    }

    lateinit var displayEntity: PacketItemDisplay
    private val HEIGHT_OFFSET = 0.5
    fun spawn() {
        val spawnLocation = Location(location.world, location.x, location.y, location.z)
        spawnLocation.x = floor(location.x) + 0.5
        spawnLocation.y = floor(location.y) + HEIGHT_OFFSET
        spawnLocation.z = floor(location.z) + 0.5

        displayEntity = PacketItemDisplay()
        displayEntity.spawn(spawnLocation)

        if(color != null) {
            displayEntity.setItemStack(type.getColoredItemStack(color))
        } else {
            displayEntity.setItemStack(type.getItemStack())
        }

        displayEntity.setItemDisplayTransform(type.renderSlot)

        displayEntity.setTransformationMatrix(Matrix4f()
            .translate(type.positionOffset.x.toFloat(), type.positionOffset.y.toFloat(), type.positionOffset.z.toFloat())
            .rotate(Quaternion.fromYawPitchRoll(type.rotationOffset.x, type.rotationOffset.y + rotation, type.rotationOffset.z).toBukkitQuaternion()))

        displayEntity.updateMetadata()

        if(type.barrierBlock) {
            location.block.type = Material.BARRIER
        }
    }

    fun remove() {
        displayEntity.deSpawn()

        if(type.barrierBlock) {
            location.block.type = Material.AIR
        }
    }

    companion object {
        fun fromJson(json: JsonObject): PlacedPlaceable {
            val location = Location(
                Bukkit.getWorld(json.get("world").asString)!!,
                json.get("x").asDouble,
                json.get("y").asDouble,
                json.get("z").asDouble,
            )
            val type = BandiCore.instance.placeableManager.getType(json.get("type").asString)!!
            val rotation = json.get("rotation").asDouble

            var color: Color? = null
            if(json.has("colorRed")) {
                color = Color.fromRGB(
                    json.get("colorRed").asInt,
                    json.get("colorGreen").asInt,
                    json.get("colorBlue").asInt
                )
            }

            return PlacedPlaceable(
                location,
                type,
                rotation,
                color
            )
        }
    }

}