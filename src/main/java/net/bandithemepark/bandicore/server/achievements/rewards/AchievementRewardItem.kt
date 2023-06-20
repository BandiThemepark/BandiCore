package net.bandithemepark.bandicore.server.achievements.rewards

import net.bandithemepark.bandicore.server.achievements.AchievementRewardType
import org.bukkit.entity.Player

class AchievementRewardItem: AchievementRewardType("ITEM") {
    override fun onReward(player: Player, rewardValue: String) {
        // TODO Add later when backpack has been implemented
    }

    override fun getRewardText(rewardValue: String): String {
        // TODO Add later when backpack has been implemented
        return "Item $rewardValue"
    }
}