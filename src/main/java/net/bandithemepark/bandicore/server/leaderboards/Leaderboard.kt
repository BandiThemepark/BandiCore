package net.bandithemepark.bandicore.server.leaderboards

import net.bandithemepark.bandicore.server.leaderboards.display.LeaderboardEntryText
import net.bandithemepark.bandicore.server.leaderboards.display.LeaderboardText
import net.bandithemepark.bandicore.util.debug.Testable
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * An instance of a physical leaderboard in the world.
 * Will update itself when the settings update
 *
 * @param settings The settings for this leaderboard.
 * @param location The location of this leaderboard, the yaw will be used to determine the direction the leaderboard is facing.
 * @param regionId The region ID of the region this leaderboard should exclusively be visible in. Set to null (default) to make visible everywhere
 */
class Leaderboard(
    val settings: LeaderboardSettings,
    val location: Location,
    val regionId: String? = null
) {
    companion object {
        const val NAME_HEIGHT = 1.9
        const val SUBTEXT_OFFSET = 0.1
        const val NAME_SCALE = 1.0
    }

    private var entryTexts = settings.entries.mapIndexed { index, entry ->
        LeaderboardEntryText(entry, location, index, regionId)
    }

    init {
        settings.addUpdateHook { update() }
    }

    var nameText = LeaderboardText(settings.name, location.clone().add(0.0, NAME_HEIGHT, 0.0), Color.WHITE, NAME_SCALE, regionId)
    var subtextText = LeaderboardText(settings.subtext, location.clone().add(0.0, NAME_HEIGHT - SUBTEXT_OFFSET, 0.0), Color.SILVER, NAME_SCALE, regionId)

    var spawned = false
    fun spawn() {
        if(spawned) return

        entryTexts.forEach { it.spawn() }
        nameText.spawn()
        subtextText.spawn()

        spawned = true
    }

    fun deSpawn() {
        if(!spawned) return

        entryTexts.forEach { it.deSpawn() }
        nameText.deSpawn()
        subtextText.deSpawn()

        spawned = false
    }

    private fun update() {
        if(!spawned) return

        updateEntryTexts()
    }

    private fun updateEntryTexts() {
        entryTexts.forEach { it.deSpawn() }

        entryTexts = settings.entries.mapIndexed { index, entry ->
            LeaderboardEntryText(entry, location, index, regionId)
        }

        entryTexts.forEach { it.spawn() }
    }
}

class LeaderboardTest: Testable {
    override fun test(sender: CommandSender) {
        if(sender !is Player) return

        val leaderboard = Leaderboard(
            LeaderboardSettings(
                "Test Leaderboard",
                "Test Subtext",
                listOf(
                    LeaderboardEntry("Test Entry 1", "1"),
                    LeaderboardEntry("Test Entry 2", "2"),
                    LeaderboardEntry("Test Entry 3", "3"),
                    LeaderboardEntry("Test Entry 4", "4"),
                    LeaderboardEntry("Test Entry 5", "5"),
                )
            ),
            sender.location,
            null
        )

        leaderboard.spawn()
    }
}