package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.minigames.MinigameGame
import org.bukkit.inventory.ItemStack

abstract class CookingMinigameGame(id: String, minPlayers: Int, maxPlayers: Int, name: String, description: List<String>, icon: ItemStack): MinigameGame(id, minPlayers, maxPlayers, name, description, icon) {
    abstract val map: CookingMap
    abstract val game: CookingGame
}