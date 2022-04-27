package net.bandithemepark.bandicore.park.attractions.tracks.editing.editors

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.editing.TrackEditorType
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTrigger
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.chat.prompt.ChatPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class TrackEditorTrigger: TrackEditorType() {
    private var selected: TrackTrigger? = null

    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.STICK, Util.color("<!i><${BandiColors.GREEN}>Select nearest trigger"))
            1 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Move"))
            2 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.GREEN}>Change type (and metadata)"))
            3 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.GREEN}>Change metadata"))
            4 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.GREEN}>Create trigger"))
            5 -> ItemFactory.create(Material.BARRIER, Util.color("<!i><${BandiColors.GREEN}>Delete trigger"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                selected = layout!!.getNearestTrigger(player!!.location)
                player!!.sendTranslatedActionBar("track-editor-selected-trigger", BandiColors.YELLOW.toString())
            }

            1 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-trigger-selected", BandiColors.RED.toString())
                    return
                }

                try {
                    if(clickType == ClickType.LEFT) {
                        selected!!.position.move(layout!!, BandiCore.instance.trackManager.pointsPerMeter / -5.0)
                    } else {
                        selected!!.position.move(layout!!, BandiCore.instance.trackManager.pointsPerMeter / 5.0)
                    }

                    player!!.sendTranslatedActionBar("track-editor-trigger-moved", BandiColors.YELLOW.toString(), MessageReplacement("nodePos", selected!!.position.nodePosition.id!!), MessageReplacement("pos", selected!!.position.position.toInt().toString()))
                } catch(ex: IllegalStateException) {
                    player!!.sendTranslatedActionBar("track-editor-move-trigger-fail", BandiColors.RED.toString())
                }
            }

            2 -> {
                ChatPrompt(
                    player!!,
                    player!!.getTranslatedMessage("track-editor-trigger-type-prompt"),
                    BandiColors.YELLOW.toString(),
                    player!!.getTranslatedMessage("track-editor-trigger-type-cancelled")
                ) { player: Player, typeId: String ->
                    if(typeId.equals("none", true)) {
                        selected!!.type = null
                        player.sendTranslatedMessage("track-editor-trigger-type-cleared", BandiColors.YELLOW.toString())
                    } else {
                        val type = TrackTriggerType.types.find { it.id == typeId }

                        if(type == null) {
                            player.sendTranslatedMessage("trigger-type-not-found", BandiColors.RED.toString())
                        } else {
                            ChatPrompt(
                                player,
                                player.getTranslatedMessage("track-editor-trigger-metadata-prompt", MessageReplacement("format", type.howToUse)),
                                BandiColors.YELLOW.toString(),
                                player.getTranslatedMessage("track-editor-trigger-metadata-cancelled")
                            ) { player2: Player, message: String ->
                                val metadataList = message.replace(", ", ",").split(",")
                                val newType = TrackTriggerType.getNew(type, selected!!, metadataList)
                                selected!!.type = newType
                                player2.sendTranslatedMessage("track-editor-trigger-metadata-success", BandiColors.YELLOW.toString())
                            }
                        }
                    }
                }
            }

            3 -> {
                ChatPrompt(
                    player!!,
                    player!!.getTranslatedMessage("track-editor-trigger-metadata-prompt", MessageReplacement("format", selected!!.type!!.howToUse)),
                    BandiColors.YELLOW.toString(),
                    player!!.getTranslatedMessage("track-editor-trigger-metadata-cancelled")
                ) { player: Player, message: String ->
                    val metadataList = message.replace(", ", ",").split(",")
                    selected!!.type!!.metadata = metadataList
                    player.sendTranslatedMessage("track-editor-trigger-metadata-success", BandiColors.YELLOW.toString())
                }
            }

            4 -> {
                val closestNode = layout!!.getNearestNode(player!!.location)
                val trigger = TrackTrigger(TrackPosition(closestNode, 0), null)

                layout!!.triggers.add(trigger)
                selected = trigger

                player!!.sendTranslatedActionBar("track-editor-trigger-created", BandiColors.YELLOW.toString())
            }

            5 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-trigger-selected", BandiColors.RED.toString())
                    return
                }

                layout!!.triggers.remove(selected!!)
                selected = null

                player!!.sendTranslatedActionBar("track-editor-trigger-deleted", BandiColors.YELLOW.toString())
            }
        }
    }
}