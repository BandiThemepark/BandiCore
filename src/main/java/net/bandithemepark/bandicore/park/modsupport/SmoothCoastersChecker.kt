package net.bandithemepark.bandicore.park.modsupport

import me.m56738.smoothcoasters.api.event.PlayerSmoothCoastersHandshakeEvent
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class SmoothCoastersChecker: Listener {
    @EventHandler (priority = EventPriority.LOW)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            if(!event.player.usingSmoothCoasters()) setNotUsing(event.player)
        }, 100)
    }

    @EventHandler
    fun onSmoothCoastersHandshake(event: PlayerSmoothCoastersHandshakeEvent) {
        setUsing(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerJoinEvent) {
        supportedPlayers.remove(event.player)
    }

    companion object {
        val supportedPlayers = mutableListOf<Player>()

        fun Player.usingSmoothCoasters(): Boolean {
            return supportedPlayers.contains(this)
        }

        fun setUsing(player: Player) {
            supportedPlayers.add(player)
            player.sendTranslatedMessage("smoothcoasters-using", BandiColors.YELLOW.toString())
        }

        fun setNotUsing(player: Player) {
            player.sendTranslatedMessage("smoothcoasters-not-using", BandiColors.YELLOW.toString(),
                MessageReplacement("linkstart", "<u><click:open_url:'https://www.bandithemepark.net/smoothcoasters/'>"),
                MessageReplacement("linkend", "</click></u>"))
        }
    }
}