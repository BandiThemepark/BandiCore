package net.bandithemepark.bandicore.park.parkours

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.parkours.ParkourManager.Companion.getParkourSession
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionEnterEvent
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionLeaveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

class ParkourEvents : Listener {
    @EventHandler
    fun onRegionEnter(event: PlayerPriorityRegionEnterEvent) {
        val parkourSession = event.player.getParkourSession()
        if(parkourSession != null) return
        if(event.fromRegion == null) return

        for(parkour in BandiCore.instance.parkourManager.parkours) {
            if(event.fromRegion!!.name == parkour.startRegionId && event.toRegion.name == parkour.coreRegionId) {
                parkour.start(event.player)
                return
            }
        }
    }

    @EventHandler
    fun onRegionLeave(event: PlayerPriorityRegionLeaveEvent) {
        val parkourSession = event.player.getParkourSession() ?: return

        if(event.fromRegion.name == parkourSession.parkour.coreRegionId) {
            if(event.toRegion?.name == parkourSession.parkour.endRegionId) {
                parkourSession.finish()
            } else {
                parkourSession.cancel()
            }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val parkourSession = event.player.getParkourSession() ?: return
        parkourSession.cancel()
    }

    @EventHandler
    fun onPlayerFly(event: PlayerToggleFlightEvent) {
        if(!event.isFlying) return
        val parkourSession = event.player.getParkourSession() ?: return
        parkourSession.cancel()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val parkourSession = event.player.getParkourSession() ?: return
        parkourSession.cancel()
    }

    @EventHandler
    fun onPlayerBlockPlace(event: BlockPlaceEvent) {
        event.player.getParkourSession() ?: return
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerBlockBreak(event: BlockBreakEvent) {
        event.player.getParkourSession() ?: return
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        event.player.getParkourSession() ?: return
        event.isCancelled = true
    }
}