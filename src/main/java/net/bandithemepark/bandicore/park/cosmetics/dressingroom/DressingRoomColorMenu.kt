package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.park.cosmetics.OwnedCosmetic
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class DressingRoomColorMenu(val player: Player, val ownedCosmetic: OwnedCosmetic): InventoryHolder {
    var lastInventory = Bukkit.createInventory(this, 45, Util.color("<#FFFFFF>\uE002\uE009"))
    var activeShadeGroup = ShadeGroup.WHITE
    var activeShadeIndex = 0

    init {
        if(ownedCosmetic.color != null) {
            activeShadeGroup = ShadeGroup.entries.find { it.shades.contains(ownedCosmetic.color) } ?: ShadeGroup.WHITE
            activeShadeIndex = activeShadeGroup.shades.indexOf(ownedCosmetic.color)
            if(activeShadeIndex == -1) {
                activeShadeIndex = 0
            }
        }
    }

    fun open() {
        val inv = Bukkit.createInventory(this, 45, Util.color("<#FFFFFF>\uE002\uE009"))
        lastInventory = inv

        inv.setItem(10, ownedCosmetic.cosmetic.type.getDressingRoomItem(player, ShadeGroup.WHITE.templateShade, ownedCosmetic.cosmetic))
        inv.setItem(16, ownedCosmetic.cosmetic.type.getDressingRoomItem(player, activeShadeGroup.shades[activeShadeIndex], ownedCosmetic.cosmetic))
        inv.setItem(19, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Back")).setCustomModelData(1004).build())

        val shadeGroupSlots = listOf(3, 4, 5, 12, 13, 14, 21, 22, 23)
        for(shadeGroup in ShadeGroup.entries) {
            val isSelected = shadeGroup == activeShadeGroup
            val itemStack = ItemFactory(Material.LEATHER_HORSE_ARMOR).setArmorColor(shadeGroup.templateShade).setCustomModelData(1002).setDisplayName(Util.color("<!i><${shadeGroup.templateShade.toHex()}>${shadeGroup.displayName}"))
                .setLore(mutableListOf(
                    if(isSelected) Util.color("<!i><${BandiColors.GREEN}>Selected") else Util.color("<!i><${BandiColors.LIGHT_GRAY}>Click to select"),
                )).build()
            inv.setItem(shadeGroupSlots[shadeGroup.ordinal], itemStack)
        }

        val shadesSlots = listOf(37, 38, 39, 40, 41, 42, 43)
        for(shade in activeShadeGroup.shades) {
            val isSelected = shade == activeShadeGroup.shades[activeShadeIndex]
            val itemStack = ItemFactory(Material.LEATHER_HORSE_ARMOR).setArmorColor(shade).setCustomModelData(1002).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Shade #${activeShadeGroup.shades.indexOf(shade) + 1}"))
                .setLore(mutableListOf(
                    if(isSelected) Util.color("<!i><${BandiColors.GREEN}>Selected") else Util.color("<!i><${BandiColors.LIGHT_GRAY}>Click to select"),
                )).build()
            inv.setItem(shadesSlots[activeShadeGroup.shades.indexOf(shade)], itemStack)
        }

        player.openInventory(inv)
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }

    private fun Color.toHex(): String {
        return String.format("#%02x%02x%02x", red, green, blue)
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if (event.clickedInventory?.holder !is DressingRoomColorMenu) return
            event.isCancelled = true

            val session = (event.clickedInventory!!.holder as DressingRoomColorMenu)
            when(event.slot) {
                in 3..5 -> {
                    val shadeGroup = ShadeGroup.entries[event.slot - 3]
                    session.activeShadeGroup = shadeGroup
                    session.activeShadeIndex = 0
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.open() })
                }
                in 12..14 -> {
                    val shadeGroup = ShadeGroup.entries[event.slot - 9]
                    session.activeShadeGroup = shadeGroup
                    session.activeShadeIndex = 0
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.open() })
                }
                in 21..23 -> {
                    val shadeGroup = ShadeGroup.entries[event.slot - 15]
                    session.activeShadeGroup = shadeGroup
                    session.activeShadeIndex = 0
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.open() })
                }
                in 37..42 -> {
                    val shade = session.activeShadeGroup.shades[event.slot - 37]
                    session.activeShadeIndex = session.activeShadeGroup.shades.indexOf(shade)
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.open() })
                }
                16 -> {
                    val dressingRoomSession = DressingRoomSession.activeSessions.find { it.player == event.whoClicked } ?: return
                    session.ownedCosmetic.color = session.activeShadeGroup.shades[session.activeShadeIndex]
                    dressingRoomSession.equipCosmetic(session.ownedCosmetic)

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })
                }
                19 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        DressingRoomCategoryMenu(event.whoClicked as Player, session.ownedCosmetic.cosmetic.type).open(0)
                    })
                }
            }
        }
    }

    enum class ShadeGroup(
        val displayName: String,
        val templateShade: Color,
        val shades: List<Color>
    ) {
        RED("Red", Color.fromRGB(255, 0, 0), listOf(
            Color.fromRGB(255, 0, 0),
            Color.fromRGB(204, 0, 0),
            Color.fromRGB(153, 0, 0),
            Color.fromRGB(102, 0, 0),
            Color.fromRGB(51, 0, 0),
            Color.fromRGB(25, 0, 0),
            Color.fromRGB(0, 0, 0),
        )),

        ORANGE("Orange", Color.fromRGB(255, 128, 0), listOf(
            Color.fromRGB(255, 128, 0),
            Color.fromRGB(204, 102, 0),
            Color.fromRGB(153, 76, 0),
            Color.fromRGB(102, 51, 0),
            Color.fromRGB(51, 26, 0),
            Color.fromRGB(25, 13, 0),
            Color.fromRGB(0, 0, 0),
        )),

        YELLOW("Yellow", Color.fromRGB(255, 255, 0), listOf(
            Color.fromRGB(255, 255, 0),
            Color.fromRGB(204, 204, 0),
            Color.fromRGB(153, 153, 0),
            Color.fromRGB(102, 102, 0),
            Color.fromRGB(51, 51, 0),
            Color.fromRGB(25, 25, 0),
            Color.fromRGB(0, 0, 0),
        )),

        PINK("Pink", Color.fromRGB(255, 0, 255), listOf(
            Color.fromRGB(255, 0, 255),
            Color.fromRGB(204, 0, 204),
            Color.fromRGB(153, 0, 153),
            Color.fromRGB(102, 0, 102),
            Color.fromRGB(51, 0, 51),
            Color.fromRGB(25, 0, 25),
            Color.fromRGB(0, 0, 0),
        )),

        WHITE("White", Color.fromRGB(255, 255, 255), listOf(
            Color.fromRGB(255, 255, 255),
            Color.fromRGB(230, 230, 230),
            Color.fromRGB(204, 204, 204),
            Color.fromRGB(179, 179, 179),
            Color.fromRGB(153, 153, 153),
            Color.fromRGB(128, 128, 128),
            Color.fromRGB(102, 102, 102),
        )),

        GREEN("Green", Color.fromRGB(0, 255, 0), listOf(
            Color.fromRGB(0, 255, 0),
            Color.fromRGB(0, 204, 0),
            Color.fromRGB(0, 153, 0),
            Color.fromRGB(0, 102, 0),
            Color.fromRGB(0, 51, 0),
            Color.fromRGB(0, 25, 0),
            Color.fromRGB(0, 0, 0),
        )),

        PURPLE("Purple", Color.fromRGB(128, 0, 255), listOf(
            Color.fromRGB(128, 0, 255),
            Color.fromRGB(102, 0, 204),
            Color.fromRGB(76, 0, 153),
            Color.fromRGB(51, 0, 102),
            Color.fromRGB(26, 0, 51),
            Color.fromRGB(13, 0, 25),
            Color.fromRGB(0, 0, 0),
        )),

        BLUE("Blue", Color.fromRGB(0, 0, 255), listOf(
            Color.fromRGB(0, 0, 255),
            Color.fromRGB(0, 0, 204),
            Color.fromRGB(0, 0, 153),
            Color.fromRGB(0, 0, 102),
            Color.fromRGB(0, 0, 51),
            Color.fromRGB(0, 0, 25),
            Color.fromRGB(0, 0, 0),
        )),

        LIGHT_BLUE("Light Blue", Color.fromRGB(0, 255, 255), listOf(
            Color.fromRGB(0, 255, 255),
            Color.fromRGB(0, 204, 204),
            Color.fromRGB(0, 153, 153),
            Color.fromRGB(0, 102, 102),
            Color.fromRGB(0, 51, 51),
            Color.fromRGB(0, 25, 25),
            Color.fromRGB(0, 0, 0),
        )),
    }
}