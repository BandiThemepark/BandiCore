package net.bandithemepark.bandicore.server.minigames.cooking.item.recipe

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem

class CookingRecipeCombine(first: CookingItem, second: CookingItem, result: CookingItem): CookingRecipe(listOf(first, second), result) {
}