package net.bandithemepark.bandicore.server.regions.events

import net.bandithemepark.bandicore.server.regions.BandiRegion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerRegionLeaveEvent(var player: Player, var fromRegion: BandiRegion, var toRegion: BandiRegion?, var fromLocation: Location, var toLocation: Location):
    Event(), Cancellable {

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

    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return this.isCancelled
    }

    override fun setCancelled(isCancelled: Boolean) {
        this.isCancelled = isCancelled
    }
}