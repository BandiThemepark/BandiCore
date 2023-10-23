package net.bandithemepark.bandicore.server.minigames.cooking.orders

import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingProduct

class CookingOrder(val product: CookingProduct) {
    var timeLeft = product.time
}