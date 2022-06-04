package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

class AttractionModeClosed: AttractionMode("closed", "Closed", BandiColors.RED, false, false) {
    override fun canRide(player: Player): Boolean {
        return false
    }

    override fun canWarp(player: Player): Boolean {
        return false
    }
}