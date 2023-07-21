package net.bandithemepark.bandicore.server.essentials.coins

import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerBossBar(val player: Player) {
    private val bossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID)
    var regionText = null as String?

    init {
        bossBar.addPlayer(player)
    }

    fun update() {
        bossBar.setTitle("§7${getDisplayedRegionText()} | ${player.getBalance()} coins")
    }

    private fun getDisplayedRegionText(): String {
        return regionText ?: "BandiThemepark"
    }

    fun hideBossBar() {
        bossBar.removePlayer(player)
    }

    fun showBossBar() {
        bossBar.addPlayer(player)
    }

    companion object {
        private val loadedBossBars = hashMapOf<Player, PlayerBossBar>()

        fun Player.getBossBar(): PlayerBossBar? {
            return loadedBossBars[this]
        }

        fun createFor(player: Player) {
            loadedBossBars[player] = PlayerBossBar(player)
            player.getBossBar()?.update()
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            createFor(event.player)
        }
    }
}