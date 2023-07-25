package net.bandithemepark.bandicore.server.achievements.triggers

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerConnectEvent
import net.bandithemepark.bandicore.park.attractions.ridecounter.RidecounterIncreaseEvent
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideDispatchEvent
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

    @EventHandler
    fun onAudioServerConnect(event: AudioServerConnectEvent) {
        listeners.filter { it.value == "audioclient" }.forEach { (achievement) ->
            achievement.give(event.player)
        }
    }

    @EventHandler
    fun onRidecounterIncrease(event: RidecounterIncreaseEvent) {
        if(event.ridecount.count < 10) return
        listeners.filter { it.value == "favoriteride" }.forEach { (achievement) ->
            achievement.give(event.player)
        }
    }

    @EventHandler
    fun onRideDispatch(event: RideDispatchEvent) {
        listeners.filter { it.value == "dispatch" }.forEach { (achievement) ->
            achievement.give(event.player)
        }
    }
}