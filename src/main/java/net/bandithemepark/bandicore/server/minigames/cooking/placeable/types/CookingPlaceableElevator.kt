package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableElevator(location: Location): CookingPlaceable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(20).build()) {
    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem == null) return
        val product = player.game.map.products.find { it.item == player.currentItem } ?: return

        player.game.onDeliver(product, player)
        player.setItem(null)
    }

    override fun onLeftClick(player: CookingPlayer) {

    }
}