package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCook
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableProgressable
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableStove(location: Location): CookingPlaceableProgressable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(22).build()) {
    var currentProgress = 0
    var burning = false
    var recipe: CookingRecipeCook? = null
    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {
        if(action == Action.PLACE || action == Action.SWAP) {
            currentProgress = 0
            burning = false
            recipe = player.game.map.recipes.filterIsInstance<CookingRecipeCook>().find { it.input.contains(item) }
        }

        if(action == Action.TAKE) {
            currentProgress = 0
            burning = false
            recipe = null
        }
    }

    override fun onLeftClick(player: CookingPlayer) {

    }

    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return player.game.map.recipes.filterIsInstance<CookingRecipeCook>().any { it.input.contains(item) }
    }

    override fun canTake(): Boolean {
        return true
    }

    fun update() {
        if(recipe != null) {
            if(!burning) {
                currentProgress++

                if(currentProgress >= recipe!!.maxProgress) {
                    burning = true
                    setItem(recipe!!.result)
                    currentProgress = 0
                }
            } else {
                currentProgress++

                if(currentProgress >= recipe!!.burnTime) {
                    burning = false
                    setItem(recipe!!.burned)
                    currentProgress = 0
                    recipe = null
                }
            }
        }
    }
}