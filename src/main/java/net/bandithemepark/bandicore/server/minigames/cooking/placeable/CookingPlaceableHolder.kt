package net.bandithemepark.bandicore.server.minigames.cooking.placeable

import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItemHolder

abstract class CookingPlaceableHolder: CookingPlaceable(), CookingItemHolder {
    override fun onItemHold() {

    }

    override fun onItemRelease() {

    }
}