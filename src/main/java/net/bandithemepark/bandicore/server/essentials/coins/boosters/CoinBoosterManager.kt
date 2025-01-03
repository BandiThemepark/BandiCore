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

        playerCoinBoosters.add(CoinBooster(
            id = UUID.randomUUID(),
            name = "Passive",
            perMinute = 5,
            durationMillis = -1
        ))

        if(player.hasPermission("bandithemepark.vip")) {
            playerCoinBoosters.add(CoinBooster(
                id = UUID.randomUUID(),
                name = "VIP",
                perMinute = 5,
                durationMillis = -1
            ))
        }

        if(player.usingSmoothCoasters()) {
            playerCoinBoosters.add(CoinBooster(
                id = UUID.randomUUID(),
                name = "SmoothCoasters",
                perMinute = 1,
                durationMillis = -1
            ))
        }

        return playerCoinBoosters
    }

    init {
        startTimer()
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            for(player in Bukkit.getOnlinePlayers()) {
                if(BandiCore.instance.afkManager.isAfk(player)) continue
                
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