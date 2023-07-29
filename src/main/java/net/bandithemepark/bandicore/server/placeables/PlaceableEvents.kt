package net.bandithemepark.bandicore.server.placeables

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PlaceableEvents: Listener {
    val lastPlacementTimes = hashMapOf<Player, Long>()
    val placementDelay = 200

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        if(event.clickedBlock == null) return
        if(event.item == null) return
        if(event.item!!.type == Material.AIR) return
        if(event.item!!.getPersistentData("placeable") == null) return
        if(event.action != Action.RIGHT_CLICK_BLOCK) return
        if(lastPlacementTimes.getOrDefault(event.player, 0L) >= System.currentTimeMillis() - placementDelay) return

        event.setUseInteractedBlock(Event.Result.DENY)

        val toPlaceAt= event.clickedBlock!!.getRelative(event.blockFace).location
        if(toPlaceAt.block.type != Material.AIR) return
        if(event.player.location.block == toPlaceAt.block) return
        if(event.player.location.clone().add(0.0, 1.0, 0.0).block == toPlaceAt.block) return

        lastPlacementTimes[event.player] = System.currentTimeMillis()
        val placeableType = BandiCore.instance.placeableManager.getType(event.item!!.getPersistentData("placeable")!!)!!
        val rotation = Math.round(event.player.location.yaw / placeableType.rotationStep) * placeableType.rotationStep

        // Round player yaw based on placeableType rotationStep
        placeAt(event.player, placeableType, toPlaceAt, rotation, BandiCore.instance.placeableManager.selectedColors[event.player])
    }

    private fun placeAt(player: Player, type: PlaceableType, location: Location, rotation: Double, color: Color?) {
        val placedPlaceable = PlacedPlaceable(location, type, rotation, color)
        placedPlaceable.spawn()
        player.playSound(Sound.sound(Key.key("block.stone.place"), Sound.Source.MASTER, 10F, 1F))
        BandiCore.instance.placeableManager.addPlaced(placedPlaceable)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if(event.block.type != Material.BARRIER) return
        val placed = BandiCore.instance.placeableManager.getPlacedAt(event.block.location) ?: return
        placed.remove()
        BandiCore.instance.placeableManager.removePlaced(placed)
    }
}