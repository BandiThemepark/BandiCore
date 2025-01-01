package net.bandithemepark.bandicore.park.parkours

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendParkour
import net.bandithemepark.bandicore.server.leaderboards.LeaderboardEntry
import net.bandithemepark.bandicore.server.leaderboards.LeaderboardSettings
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

class Parkour(
    val id: String,
    val displayName: String,
    val startRegionId: String,
    val coreRegionId: String,
    val endRegionId: String
) {
    private val leaderboardSettings = LeaderboardSettings(displayName, "Best times", listOf())
    val leaderboards = mutableListOf<ParkourLeaderboard>()

    fun start(player: Player): ParkourSession {
        val session = ParkourSession(player, this)
        BandiCore.instance.parkourManager.sessions.add(session)
        player.isFlying = false
        player.allowFlight = false

        player.showTitle(
            Title.title(
                Util.color("<${BandiColors.YELLOW}>${player.getTranslatedMessage("parkour-started")}"),
                Util.color("<${BandiColors.YELLOW}>${player.getTranslatedMessage("parkour-started-subtitle")}"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            )
        )

        return session
    }

    fun updateTop() {
        BackendParkour.getTopTenOfParkour(id) { data ->
            val entries = mutableListOf<LeaderboardEntry>()

            for(json in data) {
                val jsonObject = json.asJsonObject
                val playerUUID = UUID.fromString(jsonObject.get("playerId").asString)
                val timeMillis = jsonObject.get("timeToComplete").asLong

                val playerName: String = Bukkit.getOfflinePlayer(playerUUID).name ?: "Unknown"
                val formattedTime = formatTime(timeMillis)

                entries.add(LeaderboardEntry(playerName, formattedTime))
            }

            leaderboardSettings.entries = entries
        }
    }

    /**
     * Formats the time in milliseconds to a human-readable format, like this:
     * 1h 2m 3s 4ms
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
            "${hours}h ${formattedMinutes}m ${formattedSeconds}s ${millis % 1000}ms"
        } else if(minutes > 0) {
            "${formattedMinutes}m ${formattedSeconds}s ${millis % 1000}ms"
        } else {
            "${formattedSeconds}s ${millis % 1000}ms"
        }
    }

    companion object {
        fun fromJson(json: JsonObject): Parkour {
            val id = json.get("id").asString
            val displayName = json.get("displayName").asString
            val startRegionId = json.get("startRegionId").asString
            val coreRegionId = json.get("coreRegionId").asString
            val endRegionId = json.get("endRegionId").asString

            val parkour = Parkour(id, displayName, startRegionId, coreRegionId, endRegionId)

            if(json.has("leaderboards")) {
                val leaderboardsArray = json.getAsJsonArray("leaderboards")
                for (leaderboardJson in leaderboardsArray) {
                    val leaderboard = ParkourLeaderboard.fromJson(leaderboardJson.asJsonObject, parkour.leaderboardSettings)
                    leaderboard.spawn()
                    parkour.leaderboards.add(leaderboard)
                }
            }

            parkour.updateTop()

            return parkour
        }
    }
}