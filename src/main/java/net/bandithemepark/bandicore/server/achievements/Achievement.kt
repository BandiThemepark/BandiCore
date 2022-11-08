package net.bandithemepark.bandicore.server.achievements

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class Achievement(
    val id: UUID,
    val searchName: String,
    val displayName: String,
    val description: List<String>,
    val type: AchievementType,
    val triggerType: AchievementTriggerType,
    val triggerValue: String,
    val rewardType: AchievementRewardType,
    val rewardValue: String,
) {
    fun give(player: Player) {
        BandiCore.instance.server.achievementManager.give(this, player)
    }

    fun getItemStack(player: Player): ItemStack {
        val playerAchievements = BandiCore.instance.server.achievementManager.ownedAchievements[player] ?: mutableListOf()
        val lore = mutableListOf<Component>()

        if(type.typeTranslationId != null) {
            lore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage(type.typeTranslationId)}"))
            lore.add(Util.color(" "))
        }

        if(type.showDescriptionWhenNotUnlocked || playerAchievements.contains(this)) {
            description.forEach { lore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>$it")) }
            if(description.isNotEmpty()) {
                lore.add(Util.color(" "))
            }
        }

        lore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>Reward: ${rewardType.getRewardText(rewardValue)}"))

        val material = if(playerAchievements.contains(this)) {
            Material.LIME_TERRACOTTA
        } else {
            Material.RED_TERRACOTTA
        }

        return ItemFactory(material)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>$displayName"))
            .setLore(lore)
            .build()
    }
}