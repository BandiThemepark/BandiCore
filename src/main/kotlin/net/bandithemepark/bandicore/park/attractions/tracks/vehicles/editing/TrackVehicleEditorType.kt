package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class TrackVehicleEditorType(val vehicle: TrackVehicle, val session: TrackVehicleEditor, val hasBackButton: Boolean): Cloneable {
    lateinit var player: Player

    abstract fun getItem(slot: Int): ItemStack?
    abstract fun use(slot: Int, clickType: ClickType)
    abstract fun onBackButtonPress()
    abstract fun markAll()
    abstract fun unMarkAll()
}