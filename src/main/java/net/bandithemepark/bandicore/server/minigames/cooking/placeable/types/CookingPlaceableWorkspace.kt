package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableHolder
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableWorkspace(location: Location): CookingPlaceableHolder(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(19).build()) {
    override var currentItem: CookingItem? = null
    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {

    }

    override fun onLeftClick(player: CookingPlayer) {

    }

    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return true
    }

    override fun canTake(): Boolean {
        return true
    }
}