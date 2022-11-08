package net.bandithemepark.bandicore.park.attractions.ridecounter

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class RidecounterIncreaseEvent(var player: Player, var ridecount: RidecounterManager.PlayerRidecount):
    Event() {

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}