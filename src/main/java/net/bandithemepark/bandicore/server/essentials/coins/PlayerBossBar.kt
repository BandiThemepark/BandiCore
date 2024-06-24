package net.bandithemepark.bandicore.server.essentials.coins

import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerBossBar(val player: Player) {
    private val bossBar = BossBar.bossBar(Util.color(""), 0f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS)
    var regionText = null as String?
    var overrideText = null as String?

    init {
        bossBar.addViewer(player)
    }

    fun update() {
        if(overrideText != null) {
            bossBar.name(Util.color(overrideText!!))
            return
        }

        val regionText = getDisplayedRegionText()
        val coinsText = "${player.getBalance()}"

        bossBar.name(Util.color("${Util.getBossBarBackgroundText(regionText, 14)}<font:boss_bar>\uE010 <color:${BandiColors.LIGHT_GRAY}>$regionText</color></font> \uE003 ${Util.getBossBarBackgroundText(coinsText, 14)}<font:boss_bar>\uE011 <color:${BandiColors.LIGHT_GRAY}>$coinsText"))
    }

    private fun getDisplayedRegionText(): String {
        return regionText ?: "BandiThemepark"
    }

    fun hideBossBar() {
        bossBar.removeViewer(player)
    }

    fun showBossBar() {
        bossBar.addViewer(player)
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