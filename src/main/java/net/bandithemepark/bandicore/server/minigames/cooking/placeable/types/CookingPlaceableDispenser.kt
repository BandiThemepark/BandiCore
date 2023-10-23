package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class CookingPlaceableDispenser(location: Location, model: ItemStack, val item: CookingItem): CookingPlaceable(location, model) {
    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem != null) return
        player.setItem(item)
    }

    override fun onLeftClick(player: CookingPlayer) {

    }
}