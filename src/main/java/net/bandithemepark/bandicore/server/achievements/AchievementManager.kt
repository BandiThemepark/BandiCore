package net.bandithemepark.bandicore.server.achievements

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendAchievement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.debug.Reloadable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

class AchievementManager: Reloadable {
    val categories = mutableListOf<AchievementCategory>()
    val ownedAchievements = hashMapOf<Player, MutableList<Achievement>>()

    fun ownsAchievement(player: Player, achievementId: String): Boolean {
        if(ownedAchievements[player] == null) return false
        val achievement = getAchievement(achievementId) ?: return false

        return ownedAchievements[player]!!.contains(achievement)
    }

    fun getAchievement(searchName: String): Achievement? {
        val allAchievements = mutableListOf<Achievement>()
        categories.forEach { allAchievements.addAll(it.achievements) }

        return allAchievements.find { it.searchName == searchName }
    }

    fun give(achievement: Achievement, player: Player) {
        BackendAchievement.giveAchievement(player, achievement) {
            if(ownedAchievements[player] != null && ownedAchievements[player]!!.contains(achievement)) return@giveAchievement
            if(!ownedAchievements.containsKey(player)) ownedAchievements[player] = mutableListOf()

            val owned = ownedAchievements[player]!!
            owned.add(achievement)
            ownedAchievements[player] = owned

            achievement.rewardType.onReward(player, achievement.rewardValue)

            player.playSound(player.location, "custom.achievement", 1f, 1f)

            player.sendMessage(Util.color(" "))
            player.sendMessage(Util.color(" \uE017${Util.getNegativeText(47)}\uE018"))
            player.sendMessage(Util.color("               <${BandiColors.YELLOW}>Achievement unlocked: ${achievement.displayName}"))
            player.sendMessage(Util.color("               <${BandiColors.LIGHT_GRAY}>${if(achievement.description.isNotEmpty()) achievement.description[0] else "Good job!"}"))
            player.sendMessage(Util.color("               <${BandiColors.LIGHT_GRAY}>${if(achievement.description.size > 1) achievement.description[1] else ""}${if(achievement.description.size > 2) "..." else ""}"))
            player.sendMessage(Util.color("               <${BandiColors.LIGHT_GRAY}>Reward: ${achievement.rewardType.getRewardText(achievement.rewardValue)}"))
            player.sendMessage(Util.color(" "))
            player.sendMessage(Util.color(" "))
        }
    }

    fun loadOf(player: Player) {
        BackendAchievement.getOwnedOf(player) { achievementArray ->
            val achievements = mutableListOf<Achievement>()

            for(element in achievementArray) {
                val achievementJson = element.asJsonObject
                val id = UUID.fromString(achievementJson.get("id").asString)
                val achievement = categories.find { it -> it.achievements.find { it.id == id } != null }!!.achievements.find { it.id == id }!!
                achievements.add(achievement)
            }

            ownedAchievements[player] = achievements
        }
    }

    fun unloadOf(player: Player) {
        ownedAchievements.remove(player)
    }

    fun setup() {
        BackendAchievement.getAllCategories { categoryArray ->
            for(element in categoryArray) {
                val categoryJson = element.asJsonObject

                val id = UUID.fromString(categoryJson.get("id").asString)
                val searchName = categoryJson.get("searchName").asString
                val displayName = categoryJson.get("displayName").asString
                val description = categoryJson.get("description").asString.split("&&")
                val type = AchievementCategoryType.valueOf(categoryJson.get("type").asString)
                val icon = Material.matchMaterial(categoryJson.get("icon").asString)!!
                val iconModelData = categoryJson.get("iconModelData").asInt

                val achievements = mutableListOf<Achievement>()
                for(achievementElement in categoryJson.getAsJsonArray("achievements")) {
                    val achievementJson = achievementElement.asJsonObject

                    val achievementId = UUID.fromString(achievementJson.get("id").asString)
                    val achievementSearchName = achievementJson.get("searchName").asString
                    val achievementDisplayName = achievementJson.get("displayName").asString
                    val achievementDescription = achievementJson.get("description").asString.split("&&")
                    val achievementType = AchievementType.valueOf(achievementJson.get("type").asString)
                    val achievementTriggerType =
                        AchievementTriggerType.types.find { it.id == (achievementJson.get("triggerType").asString) }
                            ?: continue
                    val achievementTriggerValue = achievementJson.get("triggerValue").asString
                    val achievementRewardType =
                        AchievementRewardType.types.find { it.id == (achievementJson.get("rewardType").asString) }
                            ?: continue
                    val achievementRewardValue = achievementJson.get("rewardValue").asString

                    val achievement = Achievement(
                        achievementId,
                        achievementSearchName,
                        achievementDisplayName,
                        achievementDescription,
                        achievementType,
                        achievementTriggerType,
                        achievementTriggerValue,
                        achievementRewardType,
                        achievementRewardValue
                    )

                    achievement.triggerType.startListening(achievement.triggerValue, achievement)
                    achievements.add(achievement)
                }

                val category = AchievementCategory(
                    id,
                    searchName,
                    displayName,
                    description,
                    type,
                    icon,
                    iconModelData,
                    achievements
                )

                categories.add(category)
            }

            val totalAchievements = categories.sumOf { it.achievements.size }
            Util.debug("Achievements", "Loaded $totalAchievements achievements")
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            BandiCore.instance.server.achievementManager.loadOf(event.player)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            BandiCore.instance.server.achievementManager.unloadOf(event.player)
        }
    }

    override fun reload() {
        categories.clear()
        setup()

        ownedAchievements.clear()
        Bukkit.getOnlinePlayers().forEach { BandiCore.instance.server.achievementManager.loadOf(it) }
    }
}