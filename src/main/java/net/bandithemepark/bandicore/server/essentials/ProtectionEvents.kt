package net.bandithemepark.bandicore.server.essentials

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

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

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if(event.player.hasPermission("bandithemepark.crew")) return
        event.player.gameMode = GameMode.ADVENTURE
    }

    @EventHandler
    fun onBreakItemFrame(event: HangingBreakByEntityEvent) {
        if(event.remover.hasPermission("bandithemepark.crew")) return
        event.isCancelled = true
    }
}