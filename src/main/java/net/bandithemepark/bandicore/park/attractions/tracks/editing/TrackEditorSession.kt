package net.bandithemepark.bandicore.park.attractions.tracks.editing

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.util.BandiColors
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Material
import org.bukkit.entity.Player

class TrackEditorSession(val player: Player, val layout: TrackLayout) {
    val beforeInventory = player.inventory.contents
    var currentEditor = TrackEditorType.types[0].getNew(player, layout)

    init {
        updatePlayerItems()
    }

    /**
     * Updates the player's inventory with the current editor's items.
     */
    fun updatePlayerItems() {
        player.inventory.setItem(8, ItemFactory.create(Material.ARROW, Util.color("<!i><${BandiColors.YELLOW}>Next editor (Left click) | Save track (Right click)")))
        for(i in 0..7) player.inventory.setItem(i, currentEditor.getItem(i))
    }

    /**
     * Switches to the next editor.
     */
    fun openNextEditor() {
        val index = TrackEditorType.types.indexOf(currentEditor)

        currentEditor = if(index == TrackEditorType.types.size - 1) {
            TrackEditorType.types[0].getNew(player, layout)
        } else {
            TrackEditorType.types[index + 1].getNew(player, layout)
        }

        updatePlayerItems()
    }

    /**
     * Restores the inventory of the player to what it was before editing
     */
    fun resetInventory() {
        player.inventory.clear()
        player.inventory.contents = beforeInventory
        player.updateInventory()
    }
}