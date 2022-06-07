package net.bandithemepark.bandicore.park.attractions.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.server.regions.BandiRegion
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.armorstand.HoverableArmorStand
import net.bandithemepark.bandicore.util.menu.MenuUtil
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class RideOP(val id: String, val regionId: String, private val panelLocation: Location): InventoryHolder {
    var loadedPages = listOf<RideOPPage>()
    lateinit var region: BandiRegion

    abstract fun getPages(): List<RideOPPage>
    abstract fun onTick()
    abstract fun onSecond()
    abstract fun onServerStart()
    abstract fun onServerStop()

    var operator = null as Player?

    private fun placePanel() {
        val entity = object: HoverableArmorStand("rideop", "bt.vip") {
            override fun onInteract(player: Player) {
                Bukkit.dispatchCommand(player, "rideop")
            }
        }
        entity.spawn(panelLocation)
        entity.handle!!.isInvisible = true
        (entity.handle!! as ArmorStand).isMarker = true
        entity.helmet = ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(8).build()
        entity.updateMetadata()
    }

    fun getParentAttraction(): Attraction? {
        return Attraction.attractions.find { it.rideOP == this }
    }

    private var lastInventory = Bukkit.createInventory(this, 54, Util.color(MenuUtil.GENERIC_54))
    val lastPage = hashMapOf<Player, RideOPPage>()
    fun openMenu(player: Player, pageToOpen: RideOPPage? = null) {
        val page = pageToOpen ?: lastPage.getOrDefault(player, loadedPages.first())
        lastPage[player] = page

        val inv = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE011"))
        lastInventory = inv

        // Adding page icons
        for(page2 in loadedPages) {
            if(page2 == page) {
                inv.setItem(page2.iconSlot, ItemFactory(Material.PAPER)
                    .setCustomModelData(page2.clickedIconCustomModelData)
                    .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage(page2.titleTranslationId)}"))
                    .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage(page2.descriptionTranslationId)}")))
                    .build())
            } else {
                inv.setItem(page2.iconSlot, ItemFactory(Material.PAPER)
                    .setCustomModelData(page2.iconCustomModelData)
                    .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage(page2.titleTranslationId)}"))
                    .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage(page2.descriptionTranslationId)}")))
                    .build())
            }
        }

        // Adding the operator button
        if(operator == null) {
            inv.setItem(48, ItemFactory(Material.PLAYER_HEAD)
                .setSkullTexture("bc8ea1f51f253ff5142ca11ae45193a4ad8c3ab5e9c6eec8ba7a4fcb7bac40")
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage("rideop-operator-title")}"))
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage("rideop-operator-empty")}")))
                .build())
        } else {
            inv.setItem(48, ItemFactory(Material.PLAYER_HEAD)
                .setSkullOwner(operator!!)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage("rideop-operator-title")}"))
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${operator!!.name}")))
                .build())
        }

//        inv.setItem(49, ItemFactory(Material.BELL).build())
//        inv.setItem(50, ItemFactory(Material.MINECART).build())

        // Adding the operating buttons
        for(button in page.loadedButtons) {
            inv.setItem(RideOPPage.convertRideOPSlotToBukkitSlot(button.slot), button.getItemStack(player))
        }

        player.openInventory(inv)
    }

    fun updateMenu() {
        for(player in Bukkit.getOnlinePlayers()) {
            if(player.openInventory.topInventory.holder == this) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { openMenu(player) })
            }
        }
    }

    private fun setup() {
        loadedPages = getPages()
        loadedPages.forEach {
            it.rideOP = this
            it.loadButtons()
        }

        placePanel()
        onServerStart()
    }

    fun register() {
        rideOPs.add(this)
        setup()
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }

    companion object {
        val rideOPs = mutableListOf<RideOP>()

        fun get(id: String): RideOP? {
            return rideOPs.find { it.id == id }
        }

        fun getOperating(player: Player): RideOP? {
            return rideOPs.find { it.operator == player }
        }

        fun isOperating(player: Player): Boolean {
            return getOperating(player) != null
        }
    }

    class Timer {
        var i = 0

        fun onTick() {
            rideOPs.forEach { it.onTick() }

            i++
            if (i == 20) {
                rideOPs.forEach { it.onSecond() }
                i = 0
            }
        }
    }
}