package net.bandithemepark.bandicore.server.custom.blocks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class CustomBlockMenu(val player: Player) {
    var currentPage = 0
    var canNextPage = false
    var canPreviousPage = false

    init {
        sessions[player] = this
        sortTypes[player] = SortType.DEFAULT
        open(0)
    }

    fun open(page: Int) {
        currentPage = page
        val inv = Bukkit.createInventory(null, 54, Util.color("<#FFFFFF>\uE002\uE010"))

        val sortType = sortTypes[player]!!
        val types = sortType.sort(BandiCore.instance.customBlockManager.types.toList())

        val toGetStart = page * 20
        var toGetEnd = toGetStart + 20
        if(toGetEnd > types.size) {
            toGetEnd = types.size
        }

        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 32, 33, 34, 35, 36)
        for((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val type = types[i]
            val slot = availableSlots[i2]
            val item = type.getItemStack()
            inv.setItem(slot, item)
        }

        val pageCount = (types.size / 20) + 1

        canNextPage = page < pageCount-1
        canPreviousPage = page > 0

        val previousPageModelData = if(canPreviousPage) 1004 else 1005
        val nextPageModelData = if(canNextPage) 1002 else 1003

        inv.setItem(37, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Previous page")).setCustomModelData(previousPageModelData).build())
        inv.setItem(43, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Next page")).setCustomModelData(nextPageModelData).build())

        val sortByLore = mutableListOf<Component>()
        for(sortType2 in SortType.values()) {
            sortByLore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>- <${if(sortType2 == sortType) BandiColors.GREEN else BandiColors.LIGHT_GRAY}>${sortType2.text}"))
        }
        inv.setItem(42, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Sort by")).setCustomModelData(1001).setLore(sortByLore).build())

        player.openInventory(inv)
    }

    companion object {
        val sessions = hashMapOf<Player, CustomBlockMenu>()
        val sortTypes = hashMapOf<Player, SortType>()
    }

    enum class SortType(
        val text: String
    ) {
        DEFAULT("Default") {
            override fun sort(types: List<CustomBlock>): List<CustomBlock> {
                return types
            }
        },
        ALPHABETICALLY("A-Z") {
            override fun sort(types: List<CustomBlock>): List<CustomBlock> {
                return types.sortedBy { it.name }
            }
        },
        ALPHABETICALLY_REVERSE("Z-A") {
            override fun sort(types: List<CustomBlock>): List<CustomBlock> {
                return types.sortedByDescending { it.name }
            }
        };

        abstract fun sort(types: List<CustomBlock>): List<CustomBlock>
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.view.title() != Util.color("<#FFFFFF>\uE002\uE010")) return
            if(event.view.topInventory != event.clickedInventory) return

            event.isCancelled = true
            when(event.slot) {
                37 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if(session.canPreviousPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage-1)
                        })
                        session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                    } else {
                        session.player.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.MASTER, 10F, 1F))
                    }
                }
                43 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if(session.canNextPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage+1)
                        })
                        session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                    } else {
                        session.player.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.MASTER, 10F, 1F))
                    }
                }
                42 -> {
                    val currentSortType = sortTypes[event.whoClicked as Player] ?: SortType.DEFAULT
                    val newSortType = SortType.values()[(currentSortType.ordinal + 1) % SortType.values().size]
                    sortTypes[event.whoClicked as Player] = newSortType

                    val session = sessions[event.whoClicked as Player]!!
                    session.player.playSound(Sound.sound(Key.key("block.wooden_button.click_on"), Sound.Source.MASTER, 10F, 1F))
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        session.open(session.currentPage)
                    })
                }
                else -> {
                    if(event.clickedInventory!!.getItem(event.slot) == null) return

                    val blockType = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("customblock") ?: return
                    event.whoClicked.inventory.addItem(CustomBlock.getType(blockType)!!.getItemStack())
                }
            }
        }
    }
}