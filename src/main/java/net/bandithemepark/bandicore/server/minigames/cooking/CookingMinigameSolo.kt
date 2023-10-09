package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.minigames.MinigamePlayer
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material

class CookingMinigameSolo: CookingMinigameGame("cooking-solo", 1, 1, "Cooking (Solo)", listOf("Play solo"), ItemFactory(Material.PAPER).build()) {
    override val map = CookingMap(listOf(
        Location(Bukkit.getWorld("world"), -83.5, 14.0, -115.5, 120f, 0f)
    ))

    override val game = CookingGame(map)

    override fun isAvailable(): Boolean {
        return !game.busy
    }

    override fun onStart(players: List<MinigamePlayer>) {
        game.start(players.map { CookingPlayer(it, game, this) })
    }
}