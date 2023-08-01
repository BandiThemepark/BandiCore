package net.bandithemepark.bandicore.park.cosmetics.requirements.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.requirements.CosmeticRequirementType
import org.bukkit.entity.Player

class AchievementCosmeticRequirement: CosmeticRequirementType("achievement") {
    override fun check(player: Player, settings: String): Boolean {
        return BandiCore.instance.server.achievementManager.ownsAchievement(player, settings)
    }

    override fun getText(settings: String): String {
        val achievement = BandiCore.instance.server.achievementManager.getAchievement(settings)!!

        return "Found achievement ${achievement.displayName}"
    }
}