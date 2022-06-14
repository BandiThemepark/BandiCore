package net.bandithemepark.bandicore.park.attractions.mode

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player

class AttractionModeVIP: AttractionMode("vip", "VIP-Preview active", BandiColors.YELLOW, true, true) {
    override fun canRide(player: Player): Boolean {
        return player.hasPermission("bandithemepark.vip")
    }

    override fun canWarp(player: Player): Boolean {
        return player.hasPermission("bandithemepark.vip")
    }

    override fun canOperate(player: Player): Boolean {
        return if(!player.hasPermission("bandithemepark.crew")) {
            player.sendTranslatedMessage("rideop-unavailable", BandiColors.RED.toString())
            false
        } else {
            true
        }
    }
}