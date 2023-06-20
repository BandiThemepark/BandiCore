package net.bandithemepark.bandicore.server.achievements

import org.bukkit.entity.Player

abstract class AchievementRewardType(val id: String) {
    abstract fun onReward(player: Player, rewardValue: String)
    abstract fun getRewardText(rewardValue: String): String

    fun register() {
        types.add(this)
    }

    companion object {
        val types = mutableListOf<AchievementRewardType>()
    }
}