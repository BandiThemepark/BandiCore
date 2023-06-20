package net.bandithemepark.bandicore.server.achievements.rewards

import net.bandithemepark.bandicore.server.achievements.AchievementRewardType
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import org.bukkit.entity.Player

class AchievementRewardCoins: AchievementRewardType("COINS") {
    override fun onReward(player: Player, rewardValue: String) {
        CoinManager.setLoadedBalance(player, player.getBalance() + rewardValue.toInt())
        CoinManager.saveBalance(player)
    }

    override fun getRewardText(rewardValue: String): String {
        return "$rewardValue coins"
    }
}