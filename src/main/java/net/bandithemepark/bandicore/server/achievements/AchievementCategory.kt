package net.bandithemepark.bandicore.server.achievements

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class AchievementCategory(
    val id: UUID,
    val searchName: String,
    val displayName: String,
    val description: List<String>,
    val type: AchievementCategoryType,
    val icon: Material,
    val iconModelData: Int,
    val achievements: List<Achievement>
) {
    fun getItemStack(player: Player): ItemStack {
        val playerAchievements = BandiCore.instance.server.achievementManager.ownedAchievements[player]?.filter { achievements.contains(it) } ?: mutableListOf()
        val lore = mutableListOf<Component>()

        if(type.textTranslationId != null) {
            lore.add(Util.color("<!i><${BandiColors.RED}>${player.getTranslatedMessage(type.textTranslationId)}"))
            lore.add(Util.color(" "))
        }

        for(line in description) lore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>$line"))
        if(description.isNotEmpty()) lore.add(Util.color(" "))

        for(type in AchievementType.values()) {
            val achievementsOfType = achievements.filter { it.type == type }
            val ownedAchievementsOfType = playerAchievements.filter { it.type == type }

            if(achievementsOfType.isEmpty()) continue

            lore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${ownedAchievementsOfType.size}/${achievementsOfType.size} ${player.getTranslatedMessage(type.unlockedTranslationId)}"))
        }

        return ItemFactory(icon)
            .setCustomModelData(iconModelData)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>$displayName"))
            .setLore(lore)
            .setKeyInPersistentStorage("category", searchName)
            .build()
    }
}