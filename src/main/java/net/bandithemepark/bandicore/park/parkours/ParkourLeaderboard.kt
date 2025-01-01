package net.bandithemepark.bandicore.park.parkours

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.server.leaderboards.Leaderboard
import net.bandithemepark.bandicore.server.leaderboards.LeaderboardSettings
import org.bukkit.Bukkit
import org.bukkit.Location

class ParkourLeaderboard(val location: Location, val settings: LeaderboardSettings) {
    val leaderboard = Leaderboard(settings, location)

    fun spawn() {
        leaderboard.spawn()
    }

    fun deSpawn() {
        leaderboard.deSpawn()
    }

    companion object {
        fun fromJson(json: JsonObject, settings: LeaderboardSettings): ParkourLeaderboard {
            val world = json.get("world").asString
            val x = json.get("x").asDouble
            val y = json.get("y").asDouble
            val z = json.get("z").asDouble
            val yaw = json.get("yaw").asFloat

            val location = Location(Bukkit.getWorld(world), x, y, z, yaw, 0f)
            return ParkourLeaderboard(location, settings)
        }
    }
}