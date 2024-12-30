package net.bandithemepark.bandicore.park.shops

import net.bandithemepark.bandicore.network.backend.BackendShop
import net.bandithemepark.bandicore.park.shops.opener.ShopOpenerManager
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.debug.Reloadable
import org.bukkit.Bukkit

class ShopManager: Reloadable {
    val shops = mutableListOf<Shop>()
    val shopOpenerManager = ShopOpenerManager()

    fun setup() {
        loadShops()
        register("shops")
        ShopMenu.Test().register("shop-menu")
    }

    private fun loadShops() {
        BackendShop.getAll { array ->
            for(element in array) {
                val shop = Shop.fromJson(element.asJsonObject)
                shops.add(shop)
            }

            Util.debug("Shops", "Loaded ${shops.size} shops")
        }
    }

    override fun reload() {
        shops.clear()
        loadShops()
    }
}