package net.bandithemepark.bandicore.server.leaderboards.display

import net.bandithemepark.bandicore.server.leaderboards.LeaderboardEntry
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Color
import org.bukkit.Location

class LeaderboardEntryText(var entry: LeaderboardEntry, baseLocation: Location, val index: Int, val regionId: String? = null) {
    companion object {
        const val TEXT_SCALE = 0.35
        const val DISTANCE_BETWEEN_ENTRIES = 0.30
        const val DISTANCE_BETWEEN_NAME_AND_SCORE = 0.1
        const val ENTRIES_START_HEIGHT = 1.5
    }

    private var nameText = LeaderboardText(
        Util.convertToSmallText(entry.name),
        baseLocation.clone().add(0.0, ENTRIES_START_HEIGHT - (index * DISTANCE_BETWEEN_ENTRIES), 0.0),
        Color.WHITE, TEXT_SCALE,
        regionId
    )

    private var scoreText  = LeaderboardText(
        Util.convertToSmallText(entry.score),
        baseLocation.clone().add(0.0, ENTRIES_START_HEIGHT - (index * DISTANCE_BETWEEN_ENTRIES) - DISTANCE_BETWEEN_NAME_AND_SCORE, 0.0),
        Color.SILVER, TEXT_SCALE,
        regionId
    )

    var spawned = false
    fun spawn() {
        if(spawned) return

        nameText.spawn()
        scoreText.spawn()

        spawned = true
    }

    fun deSpawn() {
        if (!spawned) return

        nameText.deSpawn()
        scoreText.deSpawn()

        spawned = false
    }

    fun update() {
        nameText.text = entry.name
        scoreText.text = entry.score
    }
}