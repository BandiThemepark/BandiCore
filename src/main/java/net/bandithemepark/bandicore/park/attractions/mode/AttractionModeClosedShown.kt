package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

class AttractionModeClosedShown: AttractionMode("closedshown", "Closed", BandiColors.RED, false, true) {
    override fun canRide(player: Player): Boolean {
        return false
    }

    override fun canWarp(player: Player): Boolean {
        return false
    }
}