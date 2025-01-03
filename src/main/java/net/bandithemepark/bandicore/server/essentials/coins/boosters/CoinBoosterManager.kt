package net.bandithemepark.bandicore.server.essentials.coins.boosters

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.modsupport.SmoothCoastersChecker.Companion.usingSmoothCoasters
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class CoinBoosterManager {
    private val activeBoosters = mutableListOf<CoinBooster>()

    private fun getPlayerCoinBoosters(player: Player): List<CoinBooster> {
        val playerCoinBoosters = activeBoosters.toMutableList()

        return playerCoinBoosters
    }

    init {
        startTimer()
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            for(player in Bukkit.getOnlinePlayers()) {
                val coinBoosters = getPlayerCoinBoosters(player)
                val coinsPerMinute = coinBoosters.sumOf { it.perMinute }
                CoinManager.setLoadedBalance(player, player.getBalance() + coinsPerMinute)
                CoinManager.saveBalance(player)
            }
        }, 20*60, 20*60)
    }

    companion object {
        private var instance: CoinBoosterManager? = null

        fun getInstance(): CoinBoosterManager {
            if(instance == null) instance = CoinBoosterManager()
            return instance!!
        }
    }
}