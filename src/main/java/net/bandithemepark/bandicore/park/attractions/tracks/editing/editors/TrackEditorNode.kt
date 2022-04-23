package net.bandithemepark.bandicore.park.attractions.tracks.editing.editors

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.park.attractions.tracks.editing.TrackEditorType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.BandiColors
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat
import kotlin.math.floor

class TrackEditorNode: TrackEditorType() {
    private var selectedNode: TrackNode? = null
    private var selectedOrigin = false

    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.NETHER_STAR, Util.color("<!i><${BandiColors.GREEN}>Advanced options menu"))
            1 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Move X"))
            2 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Move Y"))
            3 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Move Z"))
            4 -> ItemFactory.create(Material.STICK, Util.color("<!i><${BandiColors.GREEN}>Select nearest (Left) | Select origin (Right)"))
            5 -> ItemFactory.create(Material.STRING, Util.color("<!i><${BandiColors.GREEN}>Deconnect (Left) | Connect to nearest (Right)"))
            6 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.GREEN}>Create node"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                if(selectedNode == null) {
                    player!!.sendTranslatedActionBar("no-node-selected", BandiColors.RED.toString())
                    return
                }

                val inv = Bukkit.createInventory(null, 9, Component.text("Advanced node options"))
                inv.setItem(0, ItemFactory.create(Material.BARRIER, Util.color("<!i><${BandiColors.GREEN}>Delete node")))
                inv.setItem(1, ItemFactory.create(Material.TARGET, Util.color("<!i><${BandiColors.GREEN}>Snap node to middle")))
                inv.setItem(2, ItemFactory.create(Material.RED_STAINED_GLASS_PANE, Util.color("<!i><${BandiColors.GREEN}>Set strict")))
                inv.setItem(3, ItemFactory.create(Material.LIME_STAINED_GLASS_PANE, Util.color("<!i><${BandiColors.GREEN}>Set not strict")))
                inv.setItem(4, ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.GREEN}>Rename node")))
                player!!.openInventory(inv)
            }

            1 -> {
                if(selectedNode == null) {
                    player!!.sendTranslatedActionBar("no-node-selected", BandiColors.RED.toString())
                    return
                }

                if(selectedOrigin) {
                    if(clickType == ClickType.LEFT) {
                        layout!!.origin.x -= 0.1
                    } else {
                        layout!!.origin.x += 0.1
                    }

                    player!!.sendTranslatedActionBar("track-editor-changed-origin", BandiColors.YELLOW.toString(), MessageReplacement("axis", "X"), MessageReplacement("new-value", DecimalFormat("###.#").format(layout!!.origin.x)))
                } else {
                    if(clickType == ClickType.LEFT) {
                        selectedNode!!.x -= 0.1
                    } else {
                        selectedNode!!.x += 0.1
                    }

                    selectedNode!!.updateMovedNode(layout!!)
                    player!!.sendTranslatedActionBar("track-editor-changed-node", BandiColors.YELLOW.toString(), MessageReplacement("axis", "X"), MessageReplacement("id", selectedNode!!.id!!), MessageReplacement("new-value", DecimalFormat("###.#").format(selectedNode!!.x)))
                }
            }

            2 -> {
                if(selectedNode == null) {
                    player!!.sendTranslatedActionBar("no-node-selected", BandiColors.RED.toString())
                    return
                }

                if(selectedOrigin) {
                    if(clickType == ClickType.LEFT) {
                        layout!!.origin.y -= 0.1
                    } else {
                        layout!!.origin.y += 0.1
                    }

                    player!!.sendTranslatedActionBar("track-editor-changed-origin", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Y"), MessageReplacement("new-value", DecimalFormat("###.#").format(layout!!.origin.y)))
                } else {
                    if(clickType == ClickType.LEFT) {
                        selectedNode!!.y -= 0.1
                    } else {
                        selectedNode!!.y += 0.1
                    }

                    selectedNode!!.updateMovedNode(layout!!)
                    player!!.sendTranslatedActionBar("track-editor-changed-node", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Y"), MessageReplacement("id", selectedNode!!.id!!), MessageReplacement("new-value", DecimalFormat("###.#").format(selectedNode!!.y)))
                }
            }

            3 -> {
                if(selectedNode == null) {
                    player!!.sendTranslatedActionBar("no-node-selected", BandiColors.RED.toString())
                    return
                }

                if(selectedOrigin) {
                    if(clickType == ClickType.LEFT) {
                        layout!!.origin.z -= 0.1
                    } else {
                        layout!!.origin.z += 0.1
                    }

                    player!!.sendTranslatedActionBar("track-editor-changed-origin", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Z"), MessageReplacement("new-value", DecimalFormat("###.#").format(layout!!.origin.z)))
                } else {
                    if(clickType == ClickType.LEFT) {
                        selectedNode!!.z -= 0.1
                    } else {
                        selectedNode!!.z += 0.1
                    }

                    selectedNode!!.updateMovedNode(layout!!)
                    player!!.sendTranslatedActionBar("track-editor-changed-node", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Z"), MessageReplacement("id", selectedNode!!.id!!), MessageReplacement("new-value", DecimalFormat("###.#").format(selectedNode!!.z)))
                }
            }

            4 -> {
                if(clickType == ClickType.LEFT) {
                    selectedNode = layout!!.getNearestNode(player!!.location)
                    selectedOrigin = false
                    player!!.sendTranslatedActionBar("track-editor-selected-node", BandiColors.YELLOW.toString(), MessageReplacement("id", selectedNode!!.id!!))
                } else {
                    selectedOrigin = true
                    selectedNode = null
                    player!!.sendTranslatedActionBar("track-editor-selected-origin", BandiColors.YELLOW.toString())
                }
            }

            5 -> {
                if(selectedNode == null) {
                    player!!.sendTranslatedActionBar("no-node-selected", BandiColors.RED.toString())
                    return
                }

                if(clickType == ClickType.LEFT) {
                    selectedNode!!.disconnect(layout!!)
                    player!!.sendTranslatedActionBar("track-editor-disconnected-node", BandiColors.YELLOW.toString())
                } else {
                   val nearest = layout!!.getNearestNode(player!!.location)

                    if(nearest == selectedNode) {
                        player!!.sendTranslatedActionBar("track-editor-cannot-connect-to-self", BandiColors.RED.toString())
                        return
                    }

                    selectedNode!!.connectTo(layout!!, nearest)
                    player!!.sendTranslatedActionBar("track-editor-connected-node", BandiColors.YELLOW.toString())
                }
            }

            6 -> {
                var currentNodeNumber = layout!!.nodes.size
                while(layout!!.nodes.find { it.id == "$currentNodeNumber" } != null) {
                    currentNodeNumber++
                }

                val loc = player!!.location.clone()
                loc.x = floor(loc.x) + 0.5
                loc.y = floor(loc.y).toInt().toDouble()
                loc.z = floor(loc.z) + 0.5
                loc.subtract(layout!!.origin)

                val newNode = TrackNode("$currentNodeNumber", loc.x, loc.y, loc.z, false)
                layout!!.nodes.add(newNode)

                if(selectedNode != null && !selectedOrigin) {
                    selectedNode!!.connectTo(layout!!, newNode)
                }

                selectedNode = newNode
                player!!.sendTranslatedActionBar("track-editor-created-node", BandiColors.YELLOW.toString(), MessageReplacement("id", newNode.id!!))
            }
        }
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.view.title() == Component.text("Advanced node options")) {
                event.isCancelled = true
                val session = BandiCore.instance.trackManager.editor.getSession(event.whoClicked as Player)!!
                val selectedNode = (session.currentEditor as TrackEditorNode).selectedNode!!
                val layout = (session.currentEditor as TrackEditorNode).layout!!

                when(event.slot) {
                    0 -> {
                        selectedNode.getBefore(layout)?.disconnect(layout)

                        val connectedTo = selectedNode.connectedTo
                        layout.nodes.remove(selectedNode)
                        connectedTo?.updateMovedNode(layout)

                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            event.whoClicked.closeInventory()
                            (event.whoClicked as Player).sendTranslatedActionBar("track-editor-deleted-node", BandiColors.YELLOW.toString())
                        })
                    }

                    1 -> {
                        val loc = selectedNode.asVector().add(layout.origin)
                        loc.x = floor(loc.x) + 0.5
                        loc.y = floor(loc.y).toInt().toDouble()
                        loc.z = floor(loc.z) + 0.5
                        loc.subtract(layout.origin)

                        selectedNode.x = loc.x
                        selectedNode.y = loc.y
                        selectedNode.z = loc.z
                        selectedNode.updateMovedNode(layout)

                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            event.whoClicked.closeInventory()
                            (event.whoClicked as Player).sendTranslatedActionBar("track-editor-node-snapped", BandiColors.YELLOW.toString())
                        })
                    }

                    2 -> {
                        selectedNode.strict = true
                        selectedNode.updateMovedNode(layout)

                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            event.whoClicked.closeInventory()
                            (event.whoClicked as Player).sendTranslatedActionBar("track-editor-node-strict", BandiColors.YELLOW.toString())
                        })
                    }

                    3 -> {
                        selectedNode.strict = false
                        selectedNode.updateMovedNode(layout)

                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            event.whoClicked.closeInventory()
                            (event.whoClicked as Player).sendTranslatedActionBar("track-editor-node-not-strict", BandiColors.YELLOW.toString())
                        })
                    }

                    4 -> {
                        // TODO Change node ID using chat prompt
                    }
                }
            }
        }
    }
}