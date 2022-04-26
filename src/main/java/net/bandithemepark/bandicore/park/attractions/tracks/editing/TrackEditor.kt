package net.bandithemepark.bandicore.park.attractions.tracks.editing

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

class TrackEditor {
    val activeSessions = mutableListOf<TrackEditorSession>()

    /**
     * Starts a new track editor session for the given player
     * @param player The player to start the session for
     */
    fun startEditor(player: Player, layout: TrackLayout) {
        activeSessions.add(TrackEditorSession(player, layout))
    }

    /**
     * Stops the editor of a player
     * @param player The player to stop the editor of
     */
    fun stopEditor(player: Player) {
        val session = activeSessions.find { it.player == player }

        if (session != null) {
            session.layout.save()
            session.resetInventory()
            activeSessions.remove(session)
        }
    }

    /**
     * Gives you the track editor session of a player
     * @param player The player to get the session of
     * @return The session of the player, null if the player is not editing a track
     */
    fun getSession(player: Player): TrackEditorSession? {
        return activeSessions.find { it.player == player }
    }

    /**
     * Tells you if a player is in a track editor
     * @param player The player to check
     * @return True if the player is in a track editor, false otherwise
     */
    fun isEditing(player: Player): Boolean {
        return activeSessions.find { it.player == player } != null
    }

    init {
        BandiCore.instance.getServer().pluginManager.registerEvents(Events(), BandiCore.instance)
    }

    class Events: Listener {
        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            if(BandiCore.instance.trackManager.editor.isEditing(event.player)) {
                BandiCore.instance.trackManager.editor.stopEditor(event.player)
            }
        }

        @EventHandler
        fun onPlayerInteract(event: PlayerInteractEvent) {
            if(event.hand != EquipmentSlot.HAND) return
            if(!BandiCore.instance.trackManager.editor.isEditing(event.player)) return

            if(event.player.inventory.heldItemSlot == 8) {
                if(isLeftClick(event)) {
                    BandiCore.instance.trackManager.editor.getSession(event.player)!!.openNextEditor()
                    event.player.sendTranslatedActionBar("track-editor-next", BandiColors.YELLOW.toString())
                } else {
                    val session = BandiCore.instance.trackManager.editor.getSession(event.player)!!
                    session.layout.save()
                    event.player.sendTranslatedActionBar("track-editor-saved", BandiColors.YELLOW.toString(), MessageReplacement("track", session.layout.id))
                }
            } else {
                val clickType = if(isLeftClick(event)) ClickType.LEFT else ClickType.RIGHT
                BandiCore.instance.trackManager.editor.getSession(event.player)!!.currentEditor.use(event.player.inventory.heldItemSlot, clickType)
            }

            event.isCancelled = true
        }

        private fun isLeftClick(event: PlayerInteractEvent): Boolean {
            return event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK
        }
    }
}