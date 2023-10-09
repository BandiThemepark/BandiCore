package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.BandiCore
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode

class CookingGame(val map: CookingMap) {
    companion object {
        const val DURATION_TICKS = 20 * 60 // * 5
    }

    var busy = false
    var currentPlayers = listOf<CookingPlayer>()
    var currentTime = 0

    fun start(players: List<CookingPlayer>) {
        busy = true
        currentPlayers = players

        for((index, player) in players.withIndex()) {
            player.teleport(map.spawnLocations[index])
            player.inventory.clear()
            player.gameMode = GameMode.ADVENTURE
        }
    }

    fun update() {
        if(!busy) return

        currentTime++

        currentPlayers.forEach { it.updateBossBar() }

        if(currentTime >= DURATION_TICKS) {
            end()
        }
    }

    private fun end() {
        busy = false

        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
            for(player in currentPlayers) {
                player.reset()
            }
        })
    }

    fun getTimeLeftString(): String {
        val ticksLeft = DURATION_TICKS - currentTime
        val secondsLeft = ticksLeft / 20
        
        // Format secondsLeft to mm:ss
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}