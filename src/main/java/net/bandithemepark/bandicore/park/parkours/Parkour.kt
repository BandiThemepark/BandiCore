package net.bandithemepark.bandicore.park.parkours

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.time.Duration

class Parkour(
    val id: String,
    val displayName: String,
    val startRegionId: String,
    val coreRegionId: String,
    val endRegionId: String
) {
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

    companion object {
        fun fromJson(json: JsonObject): Parkour {
            val id = json.get("id").asString
            val displayName = json.get("displayName").asString
            val startRegionId = json.get("startRegionId").asString
            val coreRegionId = json.get("coreRegionId").asString
            val endRegionId = json.get("endRegionId").asString
            return Parkour(id, displayName, startRegionId, coreRegionId, endRegionId)
        }
    }
}