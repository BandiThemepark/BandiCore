package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

class AttractionModeCrew: AttractionMode("crew", "Closed", BandiColors.RED, false, true) {
    override fun canRide(player: Player): Boolean {
        return player.hasPermission("bandithemepark.crew")
    }

    override fun canWarp(player: Player): Boolean {
        return player.hasPermission("bandithemepark.crew")
    }
}