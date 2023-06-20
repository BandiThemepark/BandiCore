package net.bandithemepark.bandicore.server.achievements.triggers

import net.bandithemepark.bandicore.park.attractions.ridecounter.RidecounterIncreaseEvent
import net.bandithemepark.bandicore.server.achievements.AchievementTriggerType
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionEnterEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AchievementTriggerRidecounterIncrease: AchievementTriggerType("RIDECOUNTER_INCREASE"), Listener {
    @EventHandler
    fun onRidecounterIncrease(event: RidecounterIncreaseEvent) {
        val rideId = event.ridecount.ride
        val player = event.player

        listeners.filter { it.value == rideId }.forEach { (achievement) ->
            achievement.give(player)
        }
    }
}