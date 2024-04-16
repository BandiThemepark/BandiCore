package net.bandithemepark.bandicore.server.minigames.cooking.item.recipe

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem

open class CookingRecipeProgressable(input: CookingItem, val maxProgress: Int, result: CookingItem): CookingRecipe(listOf(input), result) {
}