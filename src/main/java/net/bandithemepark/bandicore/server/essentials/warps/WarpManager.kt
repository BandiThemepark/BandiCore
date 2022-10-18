package net.bandithemepark.bandicore.server.essentials.warps

import net.bandithemepark.bandicore.network.backend.BackendWarp
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class WarpManager {
    val warps = mutableListOf<Warp>()

    fun loadWarps() {
        warps.clear()

        BackendWarp.getAllWarpsData { data ->
            for(warpJson in data) {
                val warpJsonObject = warpJson.asJsonObject
                var permission = null as String?
                if(warpJsonObject.has("permission") && !warpJsonObject.get("permission").isJsonNull) permission = warpJsonObject.get("permission").asString

                val warp = Warp(
                    UUID.fromString(warpJsonObject.get("id").asString),
                    warpJsonObject.get("name").asString,
                    Location(
                        Bukkit.getWorld(warpJsonObject.get("world").asString),
                        warpJsonObject.get("x").asDouble,
                        warpJsonObject.get("y").asDouble,
                        warpJsonObject.get("z").asDouble,
                        warpJsonObject.get("yaw").asFloat,
                        warpJsonObject.get("pitch").asFloat,
                    ),
                    permission
                )
                warps.add(warp)
            }
        }
    }

    fun getWarpsFor(player: Player): List<Warp> {
        return warps.filter { warp -> warp.permission == null || player.hasPermission(warp.permission!!) }
    }
}