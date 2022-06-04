package net.bandithemepark.bandicore.server.custom.player.editor

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.custom.player.CustomPlayer
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin
import net.bandithemepark.bandicore.server.custom.player.editor.types.PlayerEditor
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.PacketEntity
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

class CustomPlayerEditor(val player: Player) {
    val beforeInventory = player.inventory.contents
    var mode = Mode.ROTATION_POINT
    var customPlayer = CustomPlayer(CustomPlayerSkin(player.uniqueId, (player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value))

    lateinit var currentEditor: CustomPlayerEditorType
        private set

    fun setEditor(editor: CustomPlayerEditorType, unMark: Boolean = true) {
        if(unMark) currentEditor.unMarkAll()

        currentEditor = editor
        editor.player = player
        editor.markAll()

        updatePlayerItems()
    }

    init {
        setEditor(PlayerEditor(customPlayer, this), false)

        customPlayer.setVisibilityType(PacketEntity.VisibilityType.WHITELIST)
        customPlayer.setVisibilityList(mutableListOf(player))
        customPlayer.spawn(player.location)
        customPlayer.marker.addViewer(player)

        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
            customPlayer.loadFrom("default")
        })
    }

    /**
     * Updates the player's inventory with the current editor's items.
     */
    fun updatePlayerItems() {
        if(currentEditor.hasBackButton) player.inventory.setItem(8, ItemFactory(Material.BARRIER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Back")).build()) else player.inventory.setItem(8, null)
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
        private val activeSessions = mutableListOf<CustomPlayerEditor>()

        /**
         * Starts the vehicle editor for a player
         * @param player The player to start the editor for
         */
        fun startEditing(player: Player) {
            activeSessions.add(CustomPlayerEditor(player))
        }

        /**
         * Stops the editing session of a player and restores their inventory if they are editing
         * @param player The player to stop editing
         */
        fun stopEditing(player: Player) {
            val session = getEditor(player)

            if (session != null) {
                session.customPlayer.deSpawn()
                session.resetInventory()
                activeSessions.remove(session)
            }
        }

        /**
         * Gets the editor session of a player
         * @param player The player to get the editor session of
         * @return The editor session of the player or null if they are not editing
         */
        fun getEditor(player: Player): CustomPlayerEditor? {
            return activeSessions.find { it.player == player }
        }
    }

    enum class Mode {
        LIMB_OFFSET, ROTATION_POINT
    }

    class Command: CommandExecutor {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if(!command.name.equals("customplayereditor", true)) return false
            if(sender !is Player) return false

            if (!sender.hasPermission("bandithemepark.crew")) {
                sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                return false
            }

            if(getEditor(sender) != null) {
                stopEditing(sender)
                sender.sendTranslatedMessage("custom-player-editor-stopped", BandiColors.YELLOW.toString())
            } else {
                startEditing(sender)
                sender.sendTranslatedMessage("custom-player-editor-started", BandiColors.YELLOW.toString())
            }

            return false
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