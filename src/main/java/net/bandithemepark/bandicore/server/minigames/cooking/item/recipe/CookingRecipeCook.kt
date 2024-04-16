package net.bandithemepark.bandicore.server.minigames.cooking.item.recipe

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem

class CookingRecipeCook(input: CookingItem, result: CookingItem, val burned: CookingItem, cookingTime: Int, val burnTime: Int): CookingRecipeProgressable(input, cookingTime, result) {
}