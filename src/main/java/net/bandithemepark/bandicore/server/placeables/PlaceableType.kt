package net.bandithemepark.bandicore.server.placeables

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class PlaceableType(
    id: String,
    name: String,
    val material: Material,
    val customModelData: Int,
    val colorable: Boolean,
    val rotationStep: Double,
    val barrierBlock: Boolean,
    val positionOffset: Vector,
    val rotationOffset: Vector,
    val renderSlot: ItemDisplay.ItemDisplayTransform
): BandikeaEntry(id, name) {

    fun toJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("id", id)
        json.addProperty("name", name)
        json.addProperty("material", material.name)
        json.addProperty("customModelData", customModelData)
        json.addProperty("colorable", colorable)
        json.addProperty("rotationStep", rotationStep)
        json.addProperty("barrierBlock", barrierBlock)
        json.addProperty("positionOffsetX", positionOffset.x)
        json.addProperty("positionOffsetY", positionOffset.y)
        json.addProperty("positionOffsetZ", positionOffset.z)
        json.addProperty("rotationOffsetPitch", rotationOffset.x)
        json.addProperty("rotationOffsetYaw", rotationOffset.y)
        json.addProperty("rotationOffsetRoll", rotationOffset.z)
        json.addProperty("renderSlot", renderSlot.name)
        return json
    }

    fun getItemStack(): ItemStack {
        return if(colorable) ItemFactory(material)
            .setDisplayName(Util.color("<!i><#FFFFFF>${name}"))
            .setKeyInPersistentStorage("placeable", id)
            .setCustomModelData(customModelData)
            .setArmorColor(Color.fromRGB(255, 255, 255))
            .build()
        else ItemFactory(material)
            .setDisplayName(Util.color("<!i><#FFFFFF>${name}"))
            .setKeyInPersistentStorage("placeable", id)
            .setCustomModelData(customModelData)
            .build()
    }

    fun getColoredItemStack(color: Color): ItemStack {
        return ItemFactory(material)
            .setDisplayName(Util.color("<!i><#FFFFFF>${name}"))
            .setKeyInPersistentStorage("placeable", id)
            .setCustomModelData(customModelData)
            .setArmorColor(color)
            .build()
    }

    companion object {
        fun fromJson(json: JsonObject): PlaceableType {
            return PlaceableType(
                json.get("id").asString,
                json.get("name").asString,
                Material.matchMaterial(json.get("material").asString)!!,
                json.get("customModelData").asInt,
                json.get("colorable").asBoolean,
                json.get("rotationStep").asDouble,
                json.get("barrierBlock").asBoolean,
                Vector(
                    json.get("positionOffsetX").asDouble,
                    json.get("positionOffsetY").asDouble,
                    json.get("positionOffsetZ").asDouble
                ),
                Vector(
                    json.get("rotationOffsetPitch").asDouble,
                    json.get("rotationOffsetYaw").asDouble,
                    json.get("rotationOffsetRoll").asDouble
                ),
                ItemDisplay.ItemDisplayTransform.valueOf(json.get("renderSlot").asString)
            )
        }
    }

    override fun getBandikeaItemStack(): ItemStack {
        return getItemStack()
    }

}