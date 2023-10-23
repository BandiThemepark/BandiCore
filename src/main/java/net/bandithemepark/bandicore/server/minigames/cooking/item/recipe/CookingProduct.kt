package net.bandithemepark.bandicore.server.minigames.cooking.item.recipe

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem

class CookingProduct(val item: CookingItem, val points: Int, val time: Int, val latePenalty: Int, val orderCharacter: String) {
}