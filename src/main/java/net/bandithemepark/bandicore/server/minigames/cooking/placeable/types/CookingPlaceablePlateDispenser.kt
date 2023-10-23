package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceablePlateDispenser(location: Location): CookingPlaceable(location, ItemFactory(Material.DISPENSER).build()) {
    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem != null) return
        if(player.game.plateSupply <= 0) return

        player.setItem(CookingItem.PLATE)
        player.game.plateSupply--
    }

    override fun onLeftClick(player: CookingPlayer) {

    }
}