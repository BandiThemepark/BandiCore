package net.bandithemepark.bandicore.park.attractions.rideop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class RideOPButton(val slot: Int) {
    abstract fun onClick(player: Player)
    abstract fun getItemStack(): ItemStack

    lateinit var rideOP: RideOP
}