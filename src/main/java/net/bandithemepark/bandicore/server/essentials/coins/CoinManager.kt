package net.bandithemepark.bandicore.server.essentials.coins

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendPlayer
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

class CoinManager {
    val loadedBalances = hashMapOf<UUID, Int>()

    companion object {
        fun OfflinePlayer.getBalance(): Int {
            return BandiCore.instance.coinManager.loadedBalances[this.uniqueId] ?: 0
        }

        fun setLoadedBalance(offlinePlayer: OfflinePlayer, balance: Int) {
            BandiCore.instance.coinManager.loadedBalances[offlinePlayer.uniqueId] = balance
            if(offlinePlayer.isOnline) (offlinePlayer as Player).getBossBar().update()
        }

        fun reloadBalance(player: OfflinePlayer) {
            BackendPlayer(player).get { data ->
                val coins = data.get("coins").asInt
                setLoadedBalance(player, coins)
            }
        }

        fun saveBalance(player: OfflinePlayer) {
            val balance = player.getBalance()

            BackendPlayer(player).updatePlayer(false,
                updateCoins = true,
                coins = balance,
                rank = null,
                discordId = null,
                lang = null,
                updateLastJoined = false
            ) {}
        }
    }
}