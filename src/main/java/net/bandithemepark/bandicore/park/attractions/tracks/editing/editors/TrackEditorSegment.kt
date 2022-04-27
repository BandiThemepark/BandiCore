package net.bandithemepark.bandicore.park.attractions.tracks.editing.editors

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.editing.TrackEditorType
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
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

class TrackEditorSegment: TrackEditorType() {
    private var selected: SegmentSeparator? = null

    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.STICK, Util.color("<!i><${BandiColors.GREEN}>Select nearest segment separator"))
            1 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.GREEN}>Move"))
            2 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.GREEN}>Change type (and metadata)"))
            3 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.GREEN}>Change metadata"))
            4 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.GREEN}>Create segment separator"))
            5 -> ItemFactory.create(Material.BARRIER, Util.color("<!i><${BandiColors.GREEN}>Delete segment separator"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                selected = layout!!.getNearestSegmentSeparator(player!!.location)
                player!!.sendTranslatedActionBar("track-editor-selected-segment", BandiColors.YELLOW.toString())
            }

            1 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-segment-selected", BandiColors.RED.toString())
                    return
                }

                try {
                    if(clickType == ClickType.LEFT) {
                        selected!!.position.move(layout!!, BandiCore.instance.trackManager.pointsPerMeter / -5.0)
                    } else {
                        selected!!.position.move(layout!!, BandiCore.instance.trackManager.pointsPerMeter / 5.0)
                    }

                    layout!!.updateSegments()
                    player!!.sendTranslatedActionBar("track-editor-segment-moved", BandiColors.YELLOW.toString(), MessageReplacement("nodePos", selected!!.position.nodePosition.id!!), MessageReplacement("pos", selected!!.position.position.toInt().toString()))
                } catch(ex: IllegalStateException) {
                    player!!.sendTranslatedActionBar("track-editor-move-segment-fail", BandiColors.RED.toString())
                }
            }

            2 -> {
                ChatPrompt(
                    player!!,
                    player!!.getTranslatedMessage("track-editor-segment-type-prompt"),
                    BandiColors.YELLOW.toString(),
                    player!!.getTranslatedMessage("track-editor-segment-type-cancelled")
                ) { player: Player, typeId: String ->
                    if(typeId.equals("none", true)) {
                        selected!!.type = null
                        player.sendTranslatedMessage("track-editor-segment-type-cleared", BandiColors.YELLOW.toString())
                    } else {
                        val type = SegmentType.types.find { it.id == typeId }

                        if(type == null) {
                            player.sendTranslatedMessage("segment-type-not-found", BandiColors.RED.toString())
                        } else {
                            ChatPrompt(
                                player,
                                player.getTranslatedMessage("track-editor-segment-metadata-prompt", MessageReplacement("format", type.howToUse)),
                                BandiColors.YELLOW.toString(),
                                player.getTranslatedMessage("track-editor-segment-metadata-cancelled")
                            ) { player2: Player, message: String ->
                                val metadataList = message.replace(", ", ",").split(",")
                                val newType = SegmentType.getNew(type, selected!!, metadataList)
                                selected!!.type = newType
                                player2.sendTranslatedMessage("track-editor-segment-metadata-success", BandiColors.YELLOW.toString())
                            }
                        }
                    }
                }
            }

            3 -> {
                ChatPrompt(
                    player!!,
                    player!!.getTranslatedMessage("track-editor-segment-metadata-prompt", MessageReplacement("format", selected!!.type!!.howToUse)),
                    BandiColors.YELLOW.toString(),
                    player!!.getTranslatedMessage("track-editor-segment-metadata-cancelled")
                ) { player: Player, message: String ->
                    val metadataList = message.replace(", ", ",").split(",")
                    selected!!.type!!.metadata = metadataList
                    player.sendTranslatedMessage("track-editor-segment-metadata-success", BandiColors.YELLOW.toString())
                }
            }

            4 -> {
                val closestNode = layout!!.getNearestNode(player!!.location)
                val segmentSeparator = SegmentSeparator(TrackPosition(closestNode, 0), null)

                layout!!.segmentSeparators.add(segmentSeparator)
                layout!!.updateSegments()
                selected = segmentSeparator

                player!!.sendTranslatedActionBar("track-editor-segment-created", BandiColors.YELLOW.toString())
            }

            5 -> {
                if(selected == null) {
                    player!!.sendTranslatedActionBar("track-editor-no-segment-selected", BandiColors.RED.toString())
                    return
                }

                layout!!.segmentSeparators.remove(selected!!)
                layout!!.updateSegments()
                selected = null

                player!!.sendTranslatedActionBar("track-editor-segment-deleted", BandiColors.YELLOW.toString())
            }
        }
    }
}