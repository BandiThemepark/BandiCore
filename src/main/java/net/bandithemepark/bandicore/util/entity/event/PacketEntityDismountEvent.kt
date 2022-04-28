package net.bandithemepark.bandicore.util.entity.event

import net.bandithemepark.bandicore.util.entity.PacketEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PacketEntityDismountEvent(var dismounted: PacketEntity, var player: Player): Event(), Cancellable {

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