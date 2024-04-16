package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.minigames.Minigame
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableHolder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class CookingEvents: Listener {
    fun getGame(player: Player): CookingMinigameGame? {
        val minigame = Minigame.getCurrentGame(player)
        if(minigame !is CookingMinigameGame) return null
        return minigame
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        if(event.clickedBlock == null) return

        val game = getGame(event.player) ?: return
        val placeable = game.map.getPlaceableAt(event.clickedBlock!!.location) ?: return
        val player = game.game.currentPlayers.find { it == event.player }!!

        if(event.action == Action.RIGHT_CLICK_BLOCK) {
            if (placeable !is CookingPlaceableHolder) {
                placeable.onRightClick(player)
                return
            }

            if (placeable.currentItem == null || player.currentItem == null) {
                placeable.onRightClick(player)
            } else {
                val recipe = game.map.getMatchingCombineRecipe(placeable.currentItem!!, player.currentItem!!)

                if (recipe != null) {
                    placeable.setItem(recipe.result)
                    player.setItem(null)
                } else {
                    placeable.onRightClick(player)
                }
            }
        }

        if(event.action == Action.LEFT_CLICK_BLOCK) {
            placeable.onLeftClick(player)
        }
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        getGame(event.player) ?: return
        event.isCancelled = true
    }
}