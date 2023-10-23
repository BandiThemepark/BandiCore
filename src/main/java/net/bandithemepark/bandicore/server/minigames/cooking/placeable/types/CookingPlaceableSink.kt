package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableProgressable
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableSink(location: Location): CookingPlaceableProgressable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(18).build()) {
    var plates = 0

    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return false
    }

    override fun canTake(): Boolean {
        return false
    }

    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {

    }

    val minTimeBetweenCutsMillis = 400
    val amountOfClicks = 5

    var lastClick = 0L
    var currentProgress = 0

    override fun onLeftClick(player: CookingPlayer) {
        if(plates == 0) return
        if(System.currentTimeMillis() - lastClick < minTimeBetweenCutsMillis) return

        if(currentProgress < amountOfClicks) {
            currentProgress++
            lastClick = System.currentTimeMillis()

            if(currentProgress >= amountOfClicks) {
                currentProgress = 0
                plates--
                player.game.map.placeables.filterIsInstance<CookingPlaceableWashedPlates>()[0].plates++
            }
        }
    }
}