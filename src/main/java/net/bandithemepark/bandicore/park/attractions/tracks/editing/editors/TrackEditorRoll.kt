package net.bandithemepark.bandicore.park.attractions.tracks.editing.editors

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.RollNode
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.editing.TrackEditorType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class TrackEditorRoll: TrackEditorType() {
    private var selected: RollNode? = null

    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.STICK, Util.color("<!i><${BandiColors.GREEN}>Select nearest roll node"))
            1 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Move"))
            2 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Change roll"))
            3 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.GREEN}>Create roll node"))
            4 -> ItemFactory.create(Material.BARRIER, Util.color("<!i><${BandiColors.GREEN}>Delete selected"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                selected = layout!!.getNearestRollNode(player!!.location)
                player!!.sendTranslatedActionBar("track-editor-selected-roll-node", BandiColors.YELLOW.toString())
            }

            1 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-roll-node-selected", BandiColors.RED.toString())
                    return
                }

                try {
                    if(clickType == ClickType.LEFT) {
                        selected!!.position.move(layout!!, BandiCore.instance.trackManager.pointsPerMeter / -5.0)
                    } else {
                        selected!!.position.move(layout!!, BandiCore.instance.trackManager.pointsPerMeter / 5.0)
                    }

                    layout!!.updateRoll()
                    player!!.sendTranslatedActionBar("track-editor-roll-node-moved", BandiColors.YELLOW.toString(), MessageReplacement("nodePos", selected!!.position.nodePosition.id!!), MessageReplacement("pos", selected!!.position.position.toInt().toString()))
                } catch(ex: IllegalStateException) {
                    player!!.sendTranslatedActionBar("track-editor-move-roll-node-fail", BandiColors.RED.toString())
                }
            }

            2 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-roll-node-selected", BandiColors.RED.toString())
                    return
                }

                if(clickType == ClickType.LEFT) {
                    if(selected!!.roll == -175.0) {
                        selected!!.roll = 180.0
                    } else {
                        selected!!.roll -= 5.0
                    }
                } else {
                    if(selected!!.roll == 175.0) {
                        selected!!.roll = -180.0
                    } else {
                        selected!!.roll += 5.0
                    }
                }

                layout!!.updateRoll()
                player!!.sendTranslatedActionBar("track-editor-roll-changed", BandiColors.YELLOW.toString(), MessageReplacement("roll", selected!!.roll.toString()))
            }

            3 -> {
                val closestNode = layout!!.getNearestNode(player!!.location)
                val rollNode = RollNode(TrackPosition(closestNode, 0), 0.0)

                layout!!.rollNodes.add(rollNode)
                layout!!.updateRoll()
                selected = rollNode

                player!!.sendTranslatedActionBar("track-editor-roll-node-created", BandiColors.YELLOW.toString())
            }

            4 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-roll-node-selected", BandiColors.RED.toString())
                    return
                }

                layout!!.rollNodes.remove(selected!!)
                layout!!.updateRoll()
                selected = null

                player!!.sendTranslatedActionBar("track-editor-roll-node-deleted", BandiColors.YELLOW.toString())
            }
        }
    }
}