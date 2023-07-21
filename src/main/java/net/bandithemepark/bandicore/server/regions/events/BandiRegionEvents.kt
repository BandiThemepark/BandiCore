package net.bandithemepark.bandicore.server.regions.events

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import net.bandithemepark.bandicore.server.regions.BandiRegion
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class BandiRegionEvents: Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        update(event.player, event.from, event.to)
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        update(event.player, event.from, event.to)
    }

    @EventHandler
    fun onPlayerPriorityRegionEnter(event: PlayerPriorityRegionEnterEvent) {
        event.player.getBossBar()?.regionText = event.toRegion.displayName
        event.player.getBossBar()?.update()
    }

    @EventHandler
    fun onPlayerPriorityRegionLeave(event: PlayerPriorityRegionLeaveEvent) {
        if (event.toRegion == null) {
            event.player.getBossBar()?.regionText = null
            event.player.getBossBar()?.update()
        }
    }

    fun update(player: Player, from: Location, to: Location) {
        if(from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) return

        val fromRegions = BandiCore.instance.regionManager.getRegionsAt(from)
        val toRegions = BandiCore.instance.regionManager.getRegionsAt(to)

        // Doing normal region events
        for(fromRegion in fromRegions) {
            if(toRegions.isEmpty()) {
                val regionLeaveEvent = PlayerRegionLeaveEvent(player, fromRegion, null, from, to)
                Bukkit.getPluginManager().callEvent(regionLeaveEvent)
                continue
            }

            for(toRegion in toRegions.filter { it != fromRegion }) {
                val regionEnterEvent = PlayerRegionEnterEvent(player, fromRegion, toRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionEnterEvent)
                val regionLeaveEvent = PlayerRegionLeaveEvent(player, fromRegion, toRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionLeaveEvent)
            }
        }

        for(toRegion in toRegions) {
            if(fromRegions.isEmpty()) {
                val regionEnterEvent = PlayerRegionEnterEvent(player, null, toRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionEnterEvent)
                continue
            }

            for(fromRegion in fromRegions.filter { it != toRegion }) {
                val regionEnterEvent = PlayerRegionEnterEvent(player, fromRegion, toRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionEnterEvent)
                val regionLeaveEvent = PlayerRegionLeaveEvent(player, fromRegion, toRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionLeaveEvent)
            }
        }

        // Doing events for the priority events
        val prioritizedFromRegion = fromRegions.maxByOrNull { it.priority }
        val prioritizedToRegion = toRegions.maxByOrNull { it.priority }

        if(prioritizedFromRegion != null || prioritizedToRegion != null) {
            if(prioritizedFromRegion == null) {
                val regionEnterEvent = PlayerPriorityRegionEnterEvent(player, null, prioritizedToRegion!!, from, to)
                Bukkit.getPluginManager().callEvent(regionEnterEvent)
            } else if(prioritizedToRegion == null) {
                val regionLeaveEvent = PlayerPriorityRegionLeaveEvent(player, prioritizedFromRegion, null, from, to)
                Bukkit.getPluginManager().callEvent(regionLeaveEvent)
            } else if(prioritizedFromRegion != prioritizedToRegion) {
                val regionEnterEvent = PlayerPriorityRegionEnterEvent(player, prioritizedFromRegion, prioritizedToRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionEnterEvent)
                val regionLeaveEvent = PlayerPriorityRegionLeaveEvent(player, prioritizedFromRegion, prioritizedToRegion, from, to)
                Bukkit.getPluginManager().callEvent(regionLeaveEvent)
            }
        }
    }
}