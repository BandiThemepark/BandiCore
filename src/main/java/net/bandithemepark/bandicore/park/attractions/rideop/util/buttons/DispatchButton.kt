package net.bandithemepark.bandicore.park.attractions.rideop.util.buttons

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideDispatchEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class DispatchButton(slot: Int): NormalButton(slot, "rideop-button-dispatch-title", "rideop-button-dispatch-description") {
    override fun onClick(player: Player) {
        if(isAvailable()) {
            onDispatch(player)
            rideOP.updateMenu()

            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                Bukkit.getPluginManager().callEvent(RideDispatchEvent(player))
            })
        }
    }

    abstract fun onDispatch(player: Player)
}