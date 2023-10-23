package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCook
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCutting
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableProgressable
import net.bandithemepark.bandicore.util.ItemFactory
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material

class CookingPlaceableCuttingBoard(location: Location): CookingPlaceableProgressable(location, ItemFactory(Material.CRAFTING_TABLE).build()) {
    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return player.game.map.recipes.filterIsInstance<CookingRecipeCutting>().any { it.input.contains(item) }
    }

    override fun canTake(): Boolean {
        return true
    }

    var recipe: CookingRecipeCutting? = null
    var currentProgress = 0
    var lastCut: Long = 0
    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {
        if(action == Action.PLACE || action == Action.SWAP) {
            currentProgress = 0
            recipe = player.game.map.recipes.filterIsInstance<CookingRecipeCutting>().find { it.input.contains(item) }
        }
    }

    val minTimeBetweenCutsMillis = 400
    override fun onLeftClick(player: CookingPlayer) {
        if(recipe == null) return
        if(System.currentTimeMillis() - lastCut < minTimeBetweenCutsMillis) return

        if(currentProgress < recipe!!.amountOfCuts) {
            currentProgress++
            lastCut = System.currentTimeMillis()

            if(currentProgress >= recipe!!.amountOfCuts) {
                setItem(recipe!!.result)
            }
        }
    }
}