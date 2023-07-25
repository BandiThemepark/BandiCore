package net.bandithemepark.bandicore.park.attractions.rideop

import com.google.gson.JsonObject
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class RideOPButton(val slot: Int) {
    abstract fun onClick(player: Player)
    abstract fun getItemStack(player: Player): ItemStack
    abstract fun getJSON(): JsonObject

    lateinit var rideOP: RideOP
}