package net.bandithemepark.bandicore.park.attractions.tracks.editing

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class TrackEditorType: Cloneable {
    var player: Player? = null
    var layout: TrackLayout? = null

    abstract fun getItem(slot: Int): ItemStack?
    abstract fun use(slot: Int, clickType: ClickType)

    /**
     * Returns a new TrackEditorType instance
     * @param player The player who the TrackEditor is for
     * @param layout The layout of the TrackEditor
     * @return A new TrackEditorType instance
     */
    fun getNew(player: Player, layout: TrackLayout): TrackEditorType {
        val clone = clone()
        clone.player = player
        clone.layout = layout
        return clone
    }

    override fun clone(): TrackEditorType {
        return super.clone() as TrackEditorType
    }

    fun register() {
        types.add(this)
    }

    companion object {
        val types = mutableListOf<TrackEditorType>()
    }
}