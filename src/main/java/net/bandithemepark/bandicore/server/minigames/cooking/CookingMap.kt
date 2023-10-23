package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingProduct
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipe
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCombine
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import org.bukkit.Location

class CookingMap(val spawnLocations: List<Location>, val placeables: List<CookingPlaceable>, val recipes: List<CookingRecipe>, val products: List<CookingProduct>, val defaultPlates: Int) {
    fun getPlaceableAt(location: Location): CookingPlaceable? {
        return placeables.find { it.location.block == location.block }
    }

    fun getMatchingCombineRecipe(firstItem: CookingItem, secondItem: CookingItem): CookingRecipeCombine? {
        return recipes.filterIsInstance<CookingRecipeCombine>().find { it.input.contains(firstItem) && it.input.contains(secondItem) }
    }
}