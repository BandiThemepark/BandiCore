package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import net.bandithemepark.bandicore.server.minigames.MinigamePlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItemHolder
import org.bukkit.entity.Player

class CookingPlayer(player: Player, val game: CookingGame, val type: CookingMinigameGame): MinigamePlayer(player), CookingItemHolder {
    fun updateBossBar() {
        getBossBar()?.overrideText = "§7${type.name} | ${game.getTimeLeftString()} left"
        getBossBar()?.update()
    }

    override var currentItem: CookingItem? = null

    override fun onItemHold() {
        inventory.setItem(4, currentItem?.getItemStack())
    }

    override fun onItemRelease() {
        inventory.setItem(4, null)
    }
}