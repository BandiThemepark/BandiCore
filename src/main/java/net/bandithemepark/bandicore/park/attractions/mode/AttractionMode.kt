package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

abstract class AttractionMode(val id: String, val text: String, val textColor: BandiColors, val glow: Boolean, val shown: Boolean) {
    abstract fun canRide(player: Player): Boolean
    abstract fun canWarp(player: Player): Boolean
    abstract fun canOperate(player: Player): Boolean

    fun register() {
        modes.add(this)
    }

    companion object {
        val modes = mutableListOf<AttractionMode>()

        fun getMode(id: String): AttractionMode? {
            return modes.find { it.id == id }
        }
    }
}