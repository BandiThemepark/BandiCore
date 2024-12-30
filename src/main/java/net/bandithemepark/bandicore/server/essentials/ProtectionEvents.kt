package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.server.regions.events.PlayerRegionLeaveEvent
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
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
        event.player.foodLevel = 20
    }

    @EventHandler
    fun onBreakItemFrame(event: HangingBreakByEntityEvent) {
        if(event.remover.hasPermission("bandithemepark.crew")) return
        event.isCancelled = true
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.entity.foodLevel = 20
    }

    @EventHandler
    fun onRegionExit(event: PlayerRegionLeaveEvent) {
        if(event.fromRegion.name != "border") return
        if(event.toRegion !=  null) return

        if(event.player.hasPermission("bandithemepark.crew")) {
            event.player.sendTranslatedActionBar("border-crew", BandiColors.YELLOW.toString())
            return
        }

        event.player.spawnParticle(Particle.BLOCK_MARKER, event.toLocation.blockX + 0.5, event.toLocation.blockY + 1.5, event.toLocation.blockZ + 0.5, 1, Bukkit.createBlockData(Material.BARRIER))
        event.player.sendTranslatedActionBar("border-no-permission", BandiColors.RED.toString())

        val delta = event.toLocation.toVector().subtract(event.fromLocation.toVector()).multiply(-1.5)
        delta.y = 0.0
        val toLocation = event.fromLocation.toVector().add(delta).toLocation(event.fromLocation.world)
        toLocation.yaw = event.player.location.yaw
        toLocation.pitch = event.player.location.pitch
        event.player.teleport(toLocation)
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        if(event.damager.hasPermission("bandithemepark.crew")) return
        event.isCancelled = true
    }

    @EventHandler
    fun onFallDamage(event: EntityDamageEvent) {
        if(event.cause != EntityDamageEvent.DamageCause.FALL) return
        event.isCancelled = true
    }
}