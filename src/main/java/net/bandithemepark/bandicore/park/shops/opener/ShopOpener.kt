package net.bandithemepark.bandicore.park.shops.opener

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.shops.ShopMenu
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.display.HoverableItemDisplay
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import java.util.UUID

class ShopOpener(
    val id: UUID,
    val shopId: UUID,
    val location: Location
) {
    val itemDisplay = object: HoverableItemDisplay("open-shop", null) {
        override fun onInteract(player: Player) {
            ShopMenu(player, BandiCore.instance.shopManager.shops.find { it.id == shopId }!!)
        }
    }

    fun spawn() {
        itemDisplay.spawn(location)

        itemDisplay.setItemStack(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(21).build())
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay.updateMetadata()
    }

    fun deSpawn() {
        itemDisplay.deSpawn()
    }

    fun toJSON(): JsonObject {
        val json = JsonObject()
        json.addProperty("id", id.toString())
        json.addProperty("shopId", shopId.toString())

        json.addProperty("world", location.world.name)
        json.addProperty("x", location.x)
        json.addProperty("y", location.y)
        json.addProperty("z", location.z)
        json.addProperty("yaw", location.yaw)

        return json
    }

    companion object {
        fun fromJSON(json: JsonObject): ShopOpener {
            val id = UUID.fromString(json.get("id").asString)
            val shopId = UUID.fromString(json.get("shopId").asString)

            val world = json.get("world").asString
            val x = json.get("x").asDouble
            val y = json.get("y").asDouble
            val z = json.get("z").asDouble
            val yaw = json.get("yaw").asFloat
            val location = Location(Bukkit.getWorld(world), x, y, z, yaw, 0f)

            return ShopOpener(id, shopId, location)
        }
    }
}