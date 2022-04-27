package net.bandithemepark.bandicore.park.attractions.tracks.editing

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Material
import org.bukkit.entity.Player

class TrackEditorSession(val player: Player, val layout: TrackLayout) {
    val beforeInventory = player.inventory.contents
    var currentEditorIndex = 0
    var currentEditor = TrackEditorType.types[currentEditorIndex].getNew(player, layout)

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
        currentEditorIndex = if(currentEditorIndex == TrackEditorType.types.size - 1) 0 else currentEditorIndex + 1
        currentEditor = TrackEditorType.types[currentEditorIndex].getNew(player, layout)
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