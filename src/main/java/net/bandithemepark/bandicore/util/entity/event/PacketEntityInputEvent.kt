package net.bandithemepark.bandicore.util.entity.event

import net.bandithemepark.bandicore.util.entity.PacketEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PacketEntityInputEvent(var entity: PacketEntity, var player: Player, var x: Float, var z: Float, var crouching: Boolean, var jumping: Boolean): Event() {

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