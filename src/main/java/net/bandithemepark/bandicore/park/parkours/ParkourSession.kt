package net.bandithemepark.bandicore.park.parkours

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendParkour
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.time.Duration

class ParkourSession(val player: Player, val parkour: Parkour) {
    private val startTime = System.currentTimeMillis()

    fun cancel() {
        player.showTitle(
            Title.title(
                Util.color("<${BandiColors.RED}>${player.getTranslatedMessage("parkour-cancelled")}"),
                Util.color("<${BandiColors.RED}>${player.getTranslatedMessage("parkour-cancelled-subtitle")}"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            )
        )

        BandiCore.instance.parkourManager.sessions.remove(this)
        resetFlying()
        BackendParkour.saveEntry(player.uniqueId, parkour.id, System.currentTimeMillis()-startTime, false) { }
    }

    fun finish() {
        val deltaTime = System.currentTimeMillis() - startTime
        player.showTitle(
            Title.title(
                Util.color("<${BandiColors.GREEN}>${player.getTranslatedMessage("parkour-finished")}"),
                Util.color("<${BandiColors.GREEN}>${player.getTranslatedMessage("parkour-finished-subtitle", MessageReplacement("time", formatTime(deltaTime)))}"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            )
        )

        player.sendTranslatedMessage("parkour-finished-chat",
            BandiColors.GREEN.toString(),
            MessageReplacement("time", formatTimeWithMillis(deltaTime)),
            MessageReplacement("parkour", parkour.displayName)
        )

        BandiCore.instance.parkourManager.sessions.remove(this)
        resetFlying()
        BackendParkour.saveEntry(player.uniqueId, parkour.id, deltaTime, true) { success ->
            if(success) parkour.updateTop()
        }
    }

    fun showActionBar() {
        player.sendTranslatedActionBar("parkour-time", BandiColors.YELLOW.toString(), MessageReplacement("time", formatTime(System.currentTimeMillis() - startTime)))
    }

    fun resetFlying() {
        if(player.hasPermission("bandithemepark.vip")) player.allowFlight = true
    }

    /**
     * Formats the time in milliseconds to a human-readable format, like this:
     * 1h 2m 3s
     * If hours or minutes are 0, they will not be displayed.
     * @param millis The time in milliseconds
     * @return The formatted time
     */
    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val formattedSeconds = seconds % 60
        val formattedMinutes = minutes % 60

        return if(hours > 0) {
            "${hours}h ${formattedMinutes}m ${formattedSeconds}s"
        } else if(minutes > 0) {
            "${formattedMinutes}m ${formattedSeconds}s"
        } else {
            "${formattedSeconds}s"
        }
    }

    /**
     * Formats the time in milliseconds to a human-readable format, like this:
     * 1h 2m 3s 4ms
     * If hours or minutes are 0, they will not be displayed.
     * @param millis The time in milliseconds
     * @return The formatted time
     */
    private fun formatTimeWithMillis(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val formattedSeconds = seconds % 60
        val formattedMinutes = minutes % 60

        return if(hours > 0) {
            "${hours}h ${formattedMinutes}m ${formattedSeconds}s ${millis % 1000}ms"
        } else if(minutes > 0) {
            "${formattedMinutes}m ${formattedSeconds}s ${millis % 1000}ms"
        } else {
            "${formattedSeconds}s ${millis % 1000}ms"
        }
    }
}