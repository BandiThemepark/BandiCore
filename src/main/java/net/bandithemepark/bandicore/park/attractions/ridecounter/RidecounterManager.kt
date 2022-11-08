package net.bandithemepark.bandicore.park.attractions.ridecounter

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendRidecounter
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class RidecounterManager {
    private val playerRidecounters = mutableListOf<PlayerRidecounter>()
    private val rideRidecounters = mutableListOf<RideRidecounter>()

    fun increase(player: Player, rideId: String, callback: () -> Unit) {
        BackendRidecounter.increase(player, rideId) { json ->
            val count = json.get("count").asInt
            val lastRide = json.get("lastRide").asString
            val firstRide = json.get("firstRide").asString

            // Updating player ride counter
            val playerRidecounter = getPlayerRidecounter(player)
            val oldRidecount = playerRidecounter.counters.find { it.ride == rideId }

            if(oldRidecount != null) {
                oldRidecount.count = count
                oldRidecount.lastRide = lastRide
            } else {
                val newRidecount = PlayerRidecount(rideId, count, lastRide, firstRide)
                playerRidecounter.counters.add(newRidecount)
            }

            // Updating ride ride counter
            val rideRidecounter = getRideRidecounter(rideId)
            val oldPlayerCount = rideRidecounter.counters.find { it.player.uniqueId == player.uniqueId }

            if(oldPlayerCount != null) {
                oldPlayerCount.count = count
                oldPlayerCount.lastRide = lastRide
            } else {
                val newPlayerCount = RideRidecount(player, count, lastRide, firstRide)
                rideRidecounter.counters.add(newPlayerCount)
            }

            val event = RidecounterIncreaseEvent(player, playerRidecounter.counters.find { it.ride == rideId }!!)
            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                Bukkit.getPluginManager().callEvent(event)
            })

            callback.invoke()
        }
    }

    fun getRidecountOnOf(player: Player, rideId: String): Int {
        val playerRidecounter = getPlayerRidecounter(player)
        val ridecount = playerRidecounter.counters.find { it.ride == rideId }

        return ridecount?.count ?: 0
    }

    fun getPlayerRidecounter(player: Player): PlayerRidecounter {
        return playerRidecounters.find { it.player == player } ?: createPlayerRidecounter(player)
    }

    fun createPlayerRidecounter(player: Player): PlayerRidecounter {
        val playerRidecounter = PlayerRidecounter(player, mutableListOf())
        playerRidecounters.add(playerRidecounter)
        return playerRidecounter
    }

    fun getRideRidecounter(rideId: String): RideRidecounter {
        return rideRidecounters.find { it.ride == rideId } ?: createRideRidecounter(rideId)
    }

    fun createRideRidecounter(rideId: String): RideRidecounter {
        val newRideRidecounter = RideRidecounter(rideId, mutableListOf())
        rideRidecounters.add(newRideRidecounter)
        return newRideRidecounter
    }

    fun loadOf(player: Player) {
        BackendRidecounter.getAllOfPlayer(player) { json ->
            val counters = mutableListOf<PlayerRidecount>()

            for(element in json) {
                val playerRidecount = PlayerRidecount(
                    element.asJsonObject.get("ride").asString,
                    element.asJsonObject.get("count").asInt,
                    element.asJsonObject.get("lastRide").asString,
                    element.asJsonObject.get("firstRide").asString
                )
                counters.add(playerRidecount)
            }

            counters.sortBy { it.count }

            val playerRidecounter = PlayerRidecounter(player, counters)
            playerRidecounters.add(playerRidecounter)
        }
    }

    fun unloadOf(player: Player) {
        playerRidecounters.removeIf { it.player == player }
    }

    fun setupOfRide(rideId: String) {
        BackendRidecounter.getTopOfRide(rideId) { json ->
            val counters = mutableListOf<RideRidecount>()

            for(element in json) {
                val rideRidecount = RideRidecount(
                    Bukkit.getOfflinePlayer(UUID.fromString(element.asJsonObject.getAsJsonObject("player").get("uuid").asString)),
                    element.asJsonObject.get("count").asInt,
                    element.asJsonObject.get("lastRide").asString,
                    element.asJsonObject.get("firstRide").asString
                )
                counters.add(rideRidecount)
            }

            counters.sortBy { it.count }

            val rideRidecounter = RideRidecounter(rideId, counters)
            rideRidecounters.add(rideRidecounter)
        }
    }

    data class PlayerRidecounter(val player: Player, val counters: MutableList<PlayerRidecount>) {
        fun getRidecount(rideId: String): PlayerRidecount? {
            return counters.find { it.ride == rideId }
        }
    }
    data class PlayerRidecount(val ride: String, var count: Int, var lastRide: String, val firstRide: String)

    data class RideRidecounter(val ride: String, val counters: MutableList<RideRidecount>) {
        fun getRidecount(player: OfflinePlayer): RideRidecount? {
            return counters.find { it.player == player }
        }
    }
    data class RideRidecount(val player: OfflinePlayer, var count: Int, var lastRide: String, val firstRide: String)

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            BandiCore.instance.server.ridecounterManager.loadOf(event.player)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            BandiCore.instance.server.ridecounterManager.unloadOf(event.player)
        }
    }
}