package net.bandithemepark.bandicore.park.npc.path.editor

import net.bandithemepark.bandicore.park.npc.path.PathPoint
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PathPointEditor(val player: Player) {
    private val previousInventory: Array<ItemStack?>? = player.inventory.contents
    var selected: PathPoint? = null

    init {
        updateItems()
        activeSessions.add(this)
    }

    fun updateItems() {
        player.inventory.clear()
        player.inventory.setItem(0, ItemFactory(Material.STICK).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Select nearest")).build())
        player.inventory.setItem(1, ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Move X")).build())
        player.inventory.setItem(2, ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Move Y")).build())
        player.inventory.setItem(3, ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Move Z")).build())
        player.inventory.setItem(4, ItemFactory(Material.ENDER_PEARL).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Change radius")).build())
        player.inventory.setItem(5, ItemFactory(Material.STRING).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Connect to nearest/disconnect from nearest")).build())
        player.inventory.setItem(6, ItemFactory(Material.PISTON).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Create/delete")).build())

        if(selected == null) {
            player.inventory.setItem(7, ItemFactory(Material.POLISHED_ANDESITE).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Cycle type/get UUID")).build())
        } else {
            player.inventory.setItem(7, ItemFactory(Material.POLISHED_ANDESITE).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Cycle type (${selected!!.type})/get UUID")).build())
        }

        player.inventory.setItem(8, ItemFactory(Material.MAGENTA_GLAZED_TERRACOTTA).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Save")).build())

        player.updateInventory()
    }

    fun finishSession() {
        player.inventory.contents = previousInventory!!
        player.updateInventory()

        activeSessions.remove(this)
    }

    companion object {
        val activeSessions = mutableListOf<PathPointEditor>()

        fun getSession(player: Player): PathPointEditor? {
            for(session in activeSessions) {
                if(session.player == player) {
                    return session
                }
            }

            return null
        }

        fun startSession(player: Player) {
            PathPointEditor(player)
        }
    }
}