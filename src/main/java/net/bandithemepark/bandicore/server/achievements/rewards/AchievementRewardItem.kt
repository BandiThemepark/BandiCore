package net.bandithemepark.bandicore.server.achievements.rewards

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendCosmetic
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticManager.Companion.getOwnedCosmetics
import net.bandithemepark.bandicore.park.cosmetics.OwnedCosmetic
import net.bandithemepark.bandicore.server.achievements.AchievementRewardType
import org.bukkit.entity.Player

class AchievementRewardItem: AchievementRewardType("ITEM") {
    override fun onReward(player: Player, rewardValue: String) {
        val cosmetic = getCosmetic(rewardValue) ?: return
        BackendCosmetic.give(player, cosmetic) {
            player.getOwnedCosmetics()!!.ownedCosmetics.add(OwnedCosmetic(cosmetic, false, 1, null))
        }
    }

    override fun getRewardText(rewardValue: String): String {
        val cosmetic = getCosmetic(rewardValue)
        return if(cosmetic != null) {
            "Cosmetic ${cosmetic.displayName}"
        } else {
            "Unknown cosmetic"
        }
    }

    private fun getCosmetic(rewardValue: String): Cosmetic? {
        return BandiCore.instance.cosmeticManager.cosmetics.find { it.name == rewardValue }
    }
}