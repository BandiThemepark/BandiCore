package net.bandithemepark.bandicore.server.minigames

import org.bukkit.inventory.ItemStack

abstract class MinigameGame(val id: String, val minPlayers: Int, val maxPlayers: Int, val name: String, val description: List<String>, val icon: ItemStack) {
    abstract fun isAvailable(): Boolean
    abstract fun onStart(players: List<MinigamePlayer>)
}