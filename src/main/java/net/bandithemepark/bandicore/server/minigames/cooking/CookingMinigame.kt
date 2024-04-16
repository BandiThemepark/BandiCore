package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.minigames.Minigame
import net.bandithemepark.bandicore.server.minigames.MinigameGame
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Material

class CookingMinigame: Minigame("cooking", "Cooking", listOf("I cooka da pizza"), ItemFactory(Material.BREAD).build(), "cooking") {
    override val games: List<CookingMinigameGame> = listOf(CookingMinigameSolo())

    override fun update() {
        games.forEach { it.game.update() }
    }
}