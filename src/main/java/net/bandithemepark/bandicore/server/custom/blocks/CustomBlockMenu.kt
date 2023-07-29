package net.bandithemepark.bandicore.server.custom.blocks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.placeables.BandikeaEntry
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.chat.prompt.ChatPrompt
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
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

        val allTypes = mutableListOf<BandikeaEntry>()
        allTypes.addAll(BandiCore.instance.customBlockManager.types)
        allTypes.addAll(BandiCore.instance.placeableManager.types)

        val sortType = sortTypes[player]!!
        val types = sortType.sort(allTypes)

        val toGetStart = page * 20
        var toGetEnd = toGetStart + 20
        if(toGetEnd > types.size) {
            toGetEnd = types.size
        }

        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 32, 33, 34, 35, 36, 37, 38)
        for((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val type = types[i]
            val slot = availableSlots[i2]
            val item = type.getBandikeaItemStack()
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
            override fun sort(types: List<BandikeaEntry>): List<BandikeaEntry> {
                return types
            }
        },
        ALPHABETICALLY("A-Z") {
            override fun sort(types: List<BandikeaEntry>): List<BandikeaEntry> {
                return types.sortedBy { it.name }
            }
        },
        ALPHABETICALLY_REVERSE("Z-A") {
            override fun sort(types: List<BandikeaEntry>): List<BandikeaEntry> {
                return types.sortedByDescending { it.name }
            }
        };

        abstract fun sort(types: List<BandikeaEntry>): List<BandikeaEntry>
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

                    val blockType = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("customblock")
                    if(blockType != null) event.whoClicked.inventory.addItem(CustomBlock.getType(blockType)!!.getItemStack())

                    val placeableType = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("placeable")
                    if(placeableType != null) {
                        val type = BandiCore.instance.placeableManager.getType(placeableType)!!

                        if(type.colorable) {
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })

                            ChatPrompt(event.whoClicked as Player, "Enter a hex color code (#FFFFFF)", BandiColors.YELLOW.toString(), "Cancelled color selection. No item has been given") { player: Player, message: String ->
                                val color = hexTextToColor(message)
                                event.whoClicked.inventory.addItem(type.getColoredItemStack(color))
                                BandiCore.instance.placeableManager.selectedColors[player] = color
                            }
                        } else {
                            event.whoClicked.inventory.addItem(type.getItemStack())
                        }
                    }
                }
            }
        }

        private fun hexTextToColor(text: String): Color {
            val hex = text.replace("&", "").replace("#", "")
            val r = Integer.valueOf(hex.substring(0, 2), 16)
            val g = Integer.valueOf(hex.substring(2, 4), 16)
            val b = Integer.valueOf(hex.substring(4, 6), 16)
            return Color.fromRGB(r, g, b)
        }
    }
}