package net.bandithemepark.bandicore.server.minigames.cooking.item

interface CookingItemHolder {
    var currentItem: CookingItem?

    fun onItemChange(item: CookingItem?)

    fun setItem(item: CookingItem?) {
        onItemChange(item)
        currentItem = item
    }
}