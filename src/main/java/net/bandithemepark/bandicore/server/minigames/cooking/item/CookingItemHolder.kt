package net.bandithemepark.bandicore.server.minigames.cooking.item

interface CookingItemHolder {
    var currentItem: CookingItem?

    fun onItemHold()
    fun onItemRelease()
}