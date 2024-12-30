package net.bandithemepark.bandicore.server.achievements.menu

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.menu.AttractionMenu
import net.bandithemepark.bandicore.server.achievements.Achievement
import net.bandithemepark.bandicore.server.achievements.AchievementCategory
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class AchievementMenu(val player: Player, category: AchievementCategory) {
    var currentPage = 0
    var canNextPage = false
    var canPreviousPage = false
    var category: AchievementCategory? = null

    init {
        sessions[player] = this
        open(0, category)
    }

    fun open(page: Int, category: AchievementCategory) {
        currentPage = page
        this.category = category
        val inv = Bukkit.createInventory(null, 54, Util.color("<#FFFFFF>\uE002\uE015\uE002"))

        inv.setItem(40, category.getItemStack(player))

        val playerAchievements = BandiCore.instance.server.achievementManager.ownedAchievements[player] ?: mutableListOf()
        val categoryAchievements = category.achievements
        val newAchievements = mutableListOf<Achievement>()

        for(achievement in categoryAchievements) {
            if(achievement.type.showWhenNotUnlocked) {
                newAchievements.add(achievement)
                continue
            }

            if(playerAchievements.contains(achievement)) {
                newAchievements.add(achievement)
            }
        }

        newAchievements.sortBy { it.displayName }
        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 32, 33, 34, 35, 36, 37, 38)

        val toGetStart = page * availableSlots.size
        var toGetEnd = toGetStart + availableSlots.size
        if (toGetEnd > newAchievements.size) {
            toGetEnd = newAchievements.size
        }

        for ((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val type = newAchievements[i]
            val slot = availableSlots[i2]
            val item = type.getItemStack(player)
            inv.setItem(slot, item)
        }

        val pageCount = (newAchievements.size / availableSlots.size) + 1

        canNextPage = page < pageCount - 1
        canPreviousPage = page > 0

        val previousPageModelData = 1004
        val nextPageModelData = if (canNextPage) 1002 else 1003

        inv.setItem(
            37,
            ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${if(canPreviousPage) "Previous page" else "Back"}"))
                .setCustomModelData(previousPageModelData).build()
        )
        inv.setItem(
            43,
            ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Next page"))
                .setCustomModelData(nextPageModelData).build()
        )

        player.openInventory(inv)
    }

    companion object {
        val sessions = hashMapOf<Player, AchievementMenu>()
    }

    class Events : Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if (event.view.title() != Util.color("<#FFFFFF>\uE002\uE015\uE002")) return
            if (event.view.topInventory != event.clickedInventory) return

            event.isCancelled = true
            when (event.slot) {
                37 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if(session.currentPage == 0) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { AchievementCategoriesMenu(event.whoClicked as Player, true) })
                        return
                    }

                    if (session.canPreviousPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage - 1, session.category!!)
                        })
                        session.player.playSound(
                            Sound.sound(
                                Key.key("item.book.page_turn"),
                                Sound.Source.MASTER,
                                10F,
                                1F
                            )
                        )
                    } else {
                        session.player.playSound(
                            Sound.sound(
                                Key.key("entity.villager.no"),
                                Sound.Source.MASTER,
                                10F,
                                1F
                            )
                        )
                    }
                }

                43 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if (session.canNextPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage + 1, session.category!!)
                        })
                        session.player.playSound(
                            Sound.sound(
                                Key.key("item.book.page_turn"),
                                Sound.Source.MASTER,
                                10F,
                                1F
                            )
                        )
                    } else {
                        session.player.playSound(
                            Sound.sound(
                                Key.key("entity.villager.no"),
                                Sound.Source.MASTER,
                                10F,
                                1F
                            )
                        )
                    }
                }
            }
        }
    }
}