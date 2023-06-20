package net.bandithemepark.bandicore.server.achievements.triggers

import net.bandithemepark.bandicore.server.achievements.AchievementTriggerType
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionEnterEvent
import net.bandithemepark.bandicore.server.regions.events.PlayerRegionEnterEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AchievementTriggerRegionEnter: AchievementTriggerType("REGION_ENTER"), Listener {
    @EventHandler
    fun onRegionEnter(event: PlayerRegionEnterEvent) {
        val newRegionId = event.toRegion.name
        val player = event.player

        listeners.filter { it.value == newRegionId }.forEach { (achievement) ->
            achievement.give(player)
        }
    }
}