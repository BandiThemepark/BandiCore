package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.types.VehicleEditor
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

class TrackVehicleEditor(val player: Player, val trackVehicle: TrackVehicle) {
    val beforeInventory = player.inventory.contents

    lateinit var currentEditor: TrackVehicleEditorType
        private set

    fun setEditor(editor: TrackVehicleEditorType, unMark: Boolean = true) {
        if(unMark) currentEditor.unMarkAll()

        currentEditor = editor
        editor.player = player
        editor.markAll()

        updatePlayerItems()
    }

    init {
        setEditor(VehicleEditor(trackVehicle, this), false)
    }

    /**
     * Updates the player's inventory with the current editor's items.
     */
    fun updatePlayerItems() {
        if(currentEditor.hasBackButton) player.inventory.setItem(8, ItemFactory.create(Material.BARRIER, Util.color("<!i><${BandiColors.YELLOW}>Back"))) else player.inventory.setItem(8, null)
        for(i in 0..7) player.inventory.setItem(i, currentEditor.getItem(i))
    }

    /**
     * Restores the inventory of the player to what it was before editing
     */
    fun resetInventory() {
        player.inventory.clear()
        player.inventory.contents = beforeInventory
        player.updateInventory()
    }

    companion object {
        val activeSessions = mutableListOf<TrackVehicleEditor>()

        /**
         * Starts the vehicle editor for a player
         * @param player The player to start the editor for
         * @param trackVehicle The vehicle to edit
         */
        fun startEditing(player: Player, trackVehicle: TrackVehicle) {
            activeSessions.add(TrackVehicleEditor(player, trackVehicle))
        }

        /**
         * Stops the editing session of a player and restores their inventory if they are editing
         * @param player The player to stop editing
         */
        fun stopEditing(player: Player) {
            val session = getEditor(player)

            if (session != null) {
                session.currentEditor.unMarkAll()
                session.resetInventory()
                activeSessions.remove(session)
            }
        }

        /**
         * Gets the editor session of a player
         * @param player The player to get the editor session of
         * @return The editor session of the player or null if they are not editing
         */
        fun getEditor(player: Player): TrackVehicleEditor? {
            return activeSessions.find { it.player == player }
        }
    }

    class Events: Listener {
        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            stopEditing(event.player)
        }

        @EventHandler
        fun onPlayerInteract(event: PlayerInteractEvent) {
            if(event.hand != EquipmentSlot.HAND) return
            if(getEditor(event.player) == null) return

            if(event.player.inventory.heldItemSlot == 8) {
                if(event.player.inventory.itemInMainHand.type == Material.BARRIER) {
                    getEditor(event.player)!!.currentEditor.onBackButtonPress()
                }
            } else {
                val clickType = if(isLeftClick(event)) ClickType.LEFT else ClickType.RIGHT
                getEditor(event.player)!!.currentEditor.use(event.player.inventory.heldItemSlot, clickType)
            }

            event.isCancelled = true
        }

        private fun isLeftClick(event: PlayerInteractEvent): Boolean {
            return event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK
        }
    }
}