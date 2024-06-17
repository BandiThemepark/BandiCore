package net.bandithemepark.bandicore.server.essentials

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

class ProtectionEvents: Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if(event.player.hasPermission("bandithemepark.crew")) return
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if(event.player.hasPermission("bandithemepark.crew")) return
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.player.hasPermission("bandithemepark.crew")) return
        event.isCancelled = true
    }
}