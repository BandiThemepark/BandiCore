package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableWashedPlates(location: Location): CookingPlaceable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(23).build()) {
    var plates = 0

    override fun onRightClick(player: CookingPlayer) {
        if(plates <= 0) return
        if(player.currentItem != null) return

        plates--
        player.setItem(CookingItem.PLATE)
    }

    override fun onLeftClick(player: CookingPlayer) {

    }
}