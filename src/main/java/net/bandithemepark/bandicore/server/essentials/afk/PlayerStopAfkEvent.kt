package net.bandithemepark.bandicore.server.essentials.afk

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerStopAfkEvent(val player: Player): Event(), Cancellable {
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