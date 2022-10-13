package net.bandithemepark.bandicore.park.npc.path.editor

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.npc.path.PathPoint
import net.bandithemepark.bandicore.park.npc.path.PathPointType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*

class PathPointEditorEvents: Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        val session = PathPointEditor.getSession(event.player) ?: return

        event.isCancelled = true

        when(event.player.inventory.heldItemSlot) {
            0 -> {
                session.selected = BandiCore.instance.server.themePark.pathManager.getClosestPathPoint(event.player.location)
                session.updateItems()
                event.player.sendTranslatedActionBar("path-editor-selected", BandiColors.YELLOW.toString())
            }

            1 -> {
                if(session.selected == null) {
                    event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                    return
                }

                if(isLeftClick(event.action)) {
                    session.selected!!.location.add(-0.1, 0.0, 0.0)
                } else {
                    session.selected!!.location.add(0.1, 0.0, 0.0)
                }
            }

            2 -> {
                if(session.selected == null) {
                    event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                    return
                }

                if(isLeftClick(event.action)) {
                    session.selected!!.location.add(0.0, -0.1, 0.0)
                } else {
                    session.selected!!.location.add(0.0, 0.1, 0.0)
                }
            }

            3 -> {
                if(session.selected == null) {
                    event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                    return
                }

                if(isLeftClick(event.action)) {
                    session.selected!!.location.add(0.0, 0.0, -0.1)
                } else {
                    session.selected!!.location.add(0.0, 0.0, 0.1)
                }
            }

            4 -> {
                if(session.selected == null) {
                    event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                    return
                }

                if(isLeftClick(event.action)) {
                    session.selected!!.radius -= 0.1
                    if(session.selected!!.radius < 0.0) session.selected!!.radius = 0.0
                } else {
                    session.selected!!.radius += 0.1
                }
            }

            5 -> {
                if(session.selected == null) {
                    event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                    return
                }

                val nearest = BandiCore.instance.server.themePark.pathManager.getClosestPathPoint(event.player.location) ?: return

                if(nearest == session.selected) {
                    event.player.sendTranslatedActionBar("path-editor-cannot-connect-self", BandiColors.RED.toString())
                    return
                }

                if(isLeftClick(event.action)) {
                    session.selected!!.connectedTo.add(nearest)
                    nearest.connectedTo.add(session.selected!!)
                    event.player.sendTranslatedActionBar("path-editor-point-connected", BandiColors.YELLOW.toString())
                } else {
                    session.selected!!.connectedTo.remove(nearest)
                    nearest.connectedTo.remove(session.selected!!)
                    event.player.sendTranslatedActionBar("path-editor-point-disconnected", BandiColors.YELLOW.toString())
                }
            }

            6 -> {
                if(isLeftClick(event.action)) {
                    // Create
                    val newPoint = PathPoint(UUID.randomUUID(), event.player.location, 0.0, PathPointType.DEFAULT, mutableListOf())

                    if(session.selected != null) {
                        newPoint.connectedTo.add(session.selected!!)
                        session.selected!!.connectedTo.add(newPoint)
                    }

                    BandiCore.instance.server.themePark.pathManager.pathPoints.add(newPoint)
                    session.selected = newPoint

                    event.player.sendTranslatedActionBar("path-editor-point-created", BandiColors.YELLOW.toString())
                } else {
                    if(session.selected == null) {
                        event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                        return
                    }

                    // Find all nodes that are connected to selected node
                    val connectedToSelected = BandiCore.instance.server.themePark.pathManager.pathPoints.filter { it.connectedTo.contains(session.selected) }
                    for(connected in connectedToSelected) {
                        connected.connectedTo.remove(session.selected)
                    }

                    // Remove the node
                    BandiCore.instance.server.themePark.pathManager.pathPoints.remove(session.selected)
                    session.selected = null
                    session.updateItems()

                    event.player.sendTranslatedActionBar("path-editor-point-deleted", BandiColors.YELLOW.toString())
                }
            }

            7 -> {
                if(session.selected == null) {
                    event.player.sendTranslatedActionBar("path-editor-none-selected", BandiColors.RED.toString())
                    return
                }

                if(isLeftClick(event.action)) {
                    // Change the type of the selected path point to the next type
                    session.selected!!.type = session.selected!!.type.next()
                    session.updateItems()
                } else {
                    // Give UUID of the selected path point
                    event.player.sendTranslatedMessage("path-editor-uuid", BandiColors.YELLOW.toString(), MessageReplacement("uuid", "<click:copy_to_clipboard:${session.selected!!.uuid}>${session.selected!!.uuid}</click>"))
                }
            }

            8 -> {
                event.player.sendTranslatedActionBar("path-editor-saving", BandiColors.YELLOW.toString())
                BandiCore.instance.server.themePark.pathManager.savePaths()
                event.player.sendTranslatedActionBar("path-editor-saved", BandiColors.YELLOW.toString())
            }
        }
    }

    fun isLeftClick(action: Action): Boolean {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK
    }
}