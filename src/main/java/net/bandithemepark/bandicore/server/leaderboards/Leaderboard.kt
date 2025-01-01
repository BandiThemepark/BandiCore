package net.bandithemepark.bandicore.server.leaderboards

import org.bukkit.Location

/**
 * An instance of a physical leaderboard in the world.
 * Will update itself when the settings update, and will automatically spawn when instantiated.
 *
 * @param settings The settings for this leaderboard.
 * @param location The location of this leaderboard, the yaw will be used to determine the direction the leaderboard is facing.
 */
class Leaderboard(
    val settings: LeaderboardSettings,
    val location: Location
) {
    init {
        settings.addUpdateHook { update() }
        spawn()
    }

    var spawned = false
    fun spawn() {
        if(spawned) return
        spawned = true
    }

    fun deSpawn() {
        if(!spawned) return
        spawned = false
    }

    private fun update() {
        if(!spawned) return
    }
}