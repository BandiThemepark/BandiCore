package net.bandithemepark.bandicore.server.minigames

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class Minigame(val id: String, val name: String, val description: List<String>, val icon: ItemStack, val warpId: String) {
    abstract val games: List<MinigameGame>

    fun register() {
        minigames.add(this)
    }

    abstract fun update()

    companion object {
        val minigames = mutableListOf<Minigame>()

        fun get(id: String): Minigame? {
            return minigames.find { it.id == id }
        }

        fun getCurrentGame(player: Player): MinigameGame? {
            return minigames.flatMap { it.games }.find { it.currentPlayers.contains(player as MinigamePlayer) }
        }

        fun startTimer() {
            Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
                minigames.forEach { it.update() }
            }, 0, 1)
        }
    }
}