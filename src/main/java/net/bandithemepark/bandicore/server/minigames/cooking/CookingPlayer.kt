package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import net.bandithemepark.bandicore.server.minigames.MinigamePlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItemHolder
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class CookingPlayer(player: Player, val game: CookingGame, val type: CookingMinigameGame): MinigamePlayer(player), CookingItemHolder {
    var bossBar: BossBar? = null
    fun createBossBar() {
        bossBar = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID)
        bossBar!!.addPlayer(this)
    }

    fun removeBossBar() {
        bossBar!!.removePlayer(this)
        bossBar = null
    }

    fun updateBossBar() {
        getBossBar()?.overrideText = "§7${type.name} | ${game.getTimeLeftString()} left | ${game.points} points"
        getBossBar()?.update()

        var ordersText = ""
        game.orders.sortedByDescending { it.timeLeft }.forEach {
            ordersText += it.product.orderCharacter + " "
        }
        bossBar?.setTitle(ordersText)
    }

    override fun reset() {
        super.reset()
        removeBossBar()
        removePotionEffect(PotionEffectType.JUMP)
    }

    override var currentItem: CookingItem? = null

    override fun onItemChange(item: CookingItem?) {
        inventory.setItem(4, item?.itemStack)
    }
}