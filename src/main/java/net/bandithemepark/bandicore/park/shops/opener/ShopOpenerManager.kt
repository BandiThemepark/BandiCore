package net.bandithemepark.bandicore.park.shops.opener

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.shops.Shop
import net.bandithemepark.bandicore.util.FileUtil
import org.bukkit.Location
import java.util.*

class ShopOpenerManager {
    val shopOpeners = mutableListOf<ShopOpener>()

    init {
        load()
    }

    /**
     * Load the shop openers from the file and spawn them
     */
    fun load() {
        if(!FileUtil.doesFileExist("plugins/BandiCore/data/shop-openers.json")) {
            val json = JsonObject()
            json.add("shopOpeners", JsonArray())
            FileUtil.saveToFile(json, "plugins/BandiCore/data/shop-openers.json")
            return
        }

        val json = FileUtil.loadJsonFrom("plugins/BandiCore/data/shop-openers.json")
        json.getAsJsonArray("shopOpeners").forEach {
            shopOpeners.add(ShopOpener.fromJSON(it.asJsonObject))
        }

        shopOpeners.forEach { it.spawn() }
    }

    /**
     * Get the nearest shop opener in the radius
     * @param location The location to check
     * @param radius The radius to check
     * @return The nearest shop opener in the radius, null if none
     */
    fun getNearestInRadius(location: Location, radius: Double): ShopOpener? {
        return shopOpeners.filter { it.location.distance(location) <= radius }.minByOrNull { it.location.distance(location) }
    }

    /**
     * Delete a shop opener, deSpawn it and save the changes
     * @param shopOpener The shop opener to delete
     */
    fun deleteShopOpener(shopOpener: ShopOpener) {
        shopOpener.deSpawn()
        shopOpeners.remove(shopOpener)
        save()
    }

    /**
     * Create a shop opener, spawn it and save the changes
     * @param location The location of the shop opener
     * @param shop The shop to open
     */
    fun createShopOpener(location: Location, shop: Shop) {
        val shopOpener = ShopOpener(UUID.randomUUID(), shop.id, location)
        shopOpeners.add(shopOpener)
        shopOpener.spawn()
        save()
    }

    /**
     * Save the shop openers to the file
     */
    fun save() {
        val json = JsonObject()

        val array = JsonArray()
        shopOpeners.forEach { array.add(it.toJSON()) }
        json.add("shopOpeners", array)

        FileUtil.saveToFile(json, "plugins/BandiCore/data/shop-openers.json")
    }
}