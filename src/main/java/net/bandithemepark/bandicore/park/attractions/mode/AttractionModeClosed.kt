package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

class AttractionModeClosed: AttractionMode("closed", "Closed", BandiColors.RED, false, false) {
    override fun canRide(player: Player): Boolean {
        return false
    }

    override fun canWarp(player: Player): Boolean {
        return false
    }

    override fun canOperate(player: Player): Boolean {
        player.sendTranslatedMessage("rideop-unavailable", BandiColors.RED.toString())
        return false
    }
}