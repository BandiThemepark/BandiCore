package net.bandithemepark.bandicore.server.achievements.triggers

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.achievements.AchievementTriggerType
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionEnterEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AchievementTriggerSpecial: AchievementTriggerType("SPECIAL"), Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskLater(BandiCore.instance, kotlinx.coroutines.Runnable {
            listeners.filter { it.value == "join" }.forEach { (achievement) ->
                achievement.give(event.player)
            }
        }, 20)
    }
}