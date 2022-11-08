package net.bandithemepark.bandicore.server.achievements.menu

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
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

class AchievementCategoriesMenu(val player: Player) {
    var currentPage = 0
    var canNextPage = false
    var canPreviousPage = false

    init {
        sessions[player] = this
        open(0)
    }

    fun open(page: Int) {
        currentPage = page
        val inv = Bukkit.createInventory(null, 54, Util.color("<#FFFFFF>\uE002\uE015"))

        val playerAchievements = BandiCore.instance.server.achievementManager.ownedAchievements[player] ?: mutableListOf()
        val newCategories = mutableListOf<AchievementCategory>()

        for(category in BandiCore.instance.server.achievementManager.categories) {
            if(category.type.showWhenNoneUnlocked) {
                newCategories.add(category)
                continue
            }

            if(playerAchievements.any { category.achievements.contains(it) }) {
                newCategories.add(category)
            }
        }

        newCategories.sortBy { it.displayName }
        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 32, 33, 34, 35, 36)

        val toGetStart = page * availableSlots.size
        var toGetEnd = toGetStart + availableSlots.size
        if (toGetEnd > newCategories.size) {
            toGetEnd = newCategories.size
        }

        for ((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val type = newCategories[i]
            val slot = availableSlots[i2]
            val item = type.getItemStack(player)
            inv.setItem(slot, item)
        }

        val pageCount = (newCategories.size / availableSlots.size) + 1

        canNextPage = page < pageCount - 1
        canPreviousPage = page > 0

        val previousPageModelData = if (canPreviousPage) 1004 else 1005
        val nextPageModelData = if (canNextPage) 1002 else 1003

        inv.setItem(
            37,
            ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Previous page"))
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
        val sessions = hashMapOf<Player, AchievementCategoriesMenu>()
    }

    class Events : Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if (event.view.title() != Util.color("<#FFFFFF>\uE002\uE015")) return
            if (event.view.topInventory != event.clickedInventory) return

            event.isCancelled = true
            when (event.slot) {
                37 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if (session.canPreviousPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage - 1)
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
                            session.open(session.currentPage + 1)
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

                else -> {
                    if (event.clickedInventory!!.getItem(event.slot) == null) return

                    val categorySearchName = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("category") ?: return
                    val category = BandiCore.instance.server.achievementManager.categories.find { it.searchName == categorySearchName } ?: return

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        AchievementMenu(event.whoClicked as Player, category)
                    })
                }
            }
        }
    }
}