package net.bandithemepark.bandicore.server.statistics

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendPlayer
import net.bandithemepark.bandicore.server.essentials.ranks.scoreboard.BandiScoreboard.Companion.updatePlayerList
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable

class Playtime {
    companion object {
        val saved = hashMapOf<Player, Int>()
        val savedAfk = hashMapOf<Player, Int>()

        val toSave = hashMapOf<Player, Int>()
        val toSaveAFK = hashMapOf<Player, Int>()

        fun startTimer() {
            Timer().runTaskTimerAsynchronously(BandiCore.instance, 0, 20)
        }

        fun save(player: Player) {
            val data = JsonObject()
            data.addProperty("playtime", saved.getOrDefault(player, 0) + toSave.getOrDefault(player, 0))

            BackendPlayer(player).updatePlayer(data) {
                saved[player] = it.get("playtime").asInt
                toSave[player] = 0
            }

            // TODO Save AFK time here
        }

        fun Player.getPlaytime(): Int {
            return saved.getOrDefault(this, 0) + toSave.getOrDefault(this, 0)
        }

        fun Player.getPlaytimeAfk(): Int {
            return savedAfk.getOrDefault(this, 0) + toSaveAFK.getOrDefault(this, 0)
        }
    }

    class Timer: BukkitRunnable() {
        var delay = 0

        override fun run() {
            delay++
            if (delay == 300) {
                delay = 0
                Bukkit.getOnlinePlayers().forEach { save(it) }
            }

            for(player in Bukkit.getOnlinePlayers()) {
                toSave[player] = toSave.getOrDefault(player, 0) + 1
                if(BandiCore.instance.afkManager.isAfk(player)) toSaveAFK[player] = toSaveAFK.getOrDefault(player, 0) + 1

                player.updatePlayerList()
            }
        }
    }

    class Events: Listener {
        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            save(event.player)
        }
    }
}