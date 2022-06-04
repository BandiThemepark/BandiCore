package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

class AttractionModeOpen: AttractionMode("open", "Open", BandiColors.GREEN, false, true) {
    override fun canRide(player: Player): Boolean {
        return true
    }

    override fun canWarp(player: Player): Boolean {
        return true
    }
}