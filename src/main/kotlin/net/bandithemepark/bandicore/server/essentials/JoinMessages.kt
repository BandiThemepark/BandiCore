package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Duration

class JoinMessages: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Util.color("<${BandiColors.LIGHT_GRAY}>${event.player.name} joined"))
        event.player.showTitle(Title.title(Component.text("\uE000"), Component.text(""), Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(3000), Duration.ofMillis(500))))
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(Util.color("<${BandiColors.LIGHT_GRAY}>${event.player.name} left"))
    }
}