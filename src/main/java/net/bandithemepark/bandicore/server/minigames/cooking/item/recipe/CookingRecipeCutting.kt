package net.bandithemepark.bandicore.server.minigames.cooking.item.recipe

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem

class CookingRecipeCutting(input: CookingItem, result: CookingItem, val amountOfCuts: Int): CookingRecipe(listOf(input), result) {
}