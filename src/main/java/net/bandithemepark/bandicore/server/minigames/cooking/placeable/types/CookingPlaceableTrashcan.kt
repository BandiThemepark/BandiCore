package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingGame
import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableTrashcan(location: Location): CookingPlaceable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(24).build()) {
    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem.toString().contains("PLATE")) player.setItem(CookingItem.PLATE)
        player.setItem(null)
    }

    override fun onLeftClick(player: CookingPlayer) {

    }
}