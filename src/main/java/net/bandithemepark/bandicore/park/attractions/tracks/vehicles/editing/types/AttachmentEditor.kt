package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicleMember
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditor
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditorType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.chat.prompt.ChatPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class AttachmentEditor(vehicle: TrackVehicle, session: TrackVehicleEditor, val member: TrackVehicleMember, val attachment: Attachment): TrackVehicleEditorType(vehicle, session, true) {
    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.DEAD_BUSH, Util.color("<!i><${BandiColors.YELLOW}>Delete self (includes children)"))
            1 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.YELLOW}>Add child"))
            2 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.YELLOW}>Set type (${attachment.type.id})"))
            3 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.YELLOW}>Set metadata"))
            4 -> ItemFactory.create(Material.TARGET, Util.color("<!i><${BandiColors.YELLOW}>Change position"))
            5 -> ItemFactory.create(Material.NAME_TAG, Util.color("<!i><${BandiColors.YELLOW}>Change ID"))
            6 -> if(attachment.children.isNotEmpty()) ItemFactory.create(Material.TARGET, Util.color("<!i><${BandiColors.YELLOW}>Select child")) else null
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                val parent = member.getAllAttachments().find { it.children.contains(attachment) }

                if(parent == null) { // BATMAN
                    session.setEditor(MemberEditor(vehicle, session, member))
                } else {
                    session.setEditor(AttachmentEditor(vehicle, session, member, parent))
                }

                attachment.getAllAttachments().forEach { it.type.onDeSpawn() }
                member.attachments.remove(attachment)
            }

            1 -> {
                val attachment = Attachment("main", AttachmentPosition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), listOf(), AttachmentType.get("model", listOf("DIAMOND_SWORD", "1"))!!, mutableListOf())
                attachment.type.onSpawn(vehicle.ridingOn.origin.toLocation(vehicle.ridingOn.world))
                this.attachment.children.add(attachment)

                session.updatePlayerItems()
                player.sendTranslatedActionBar("vehicle-editor-child-added", BandiColors.YELLOW.toString())
            }

            2 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("vehicle-editor-type"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("vehicle-editor-type-cancelled"),
                ) { player: Player, type: String ->
                    if(AttachmentType.get(type) != null) {
                        val howToConfigure = AttachmentType.get(type)!!.howToConfigure

                        ChatPrompt(player,
                            player.getTranslatedMessage("vehicle-editor-metadata").replace("%format%", howToConfigure),
                            BandiColors.YELLOW.toString(),
                            player.getTranslatedMessage("vehicle-editor-metadata-cancelled"),
                        ) { player2: Player, metadata: String ->
                            try {
                                val metadataList = metadata.replace(", ", ",").split(",")

                                attachment.type.onDeSpawn()
                                attachment.type = AttachmentType.get(type, metadataList)!!
                                attachment.type.onSpawn(vehicle.ridingOn.origin.toLocation(vehicle.ridingOn.world))
                                session.updatePlayerItems()
                                markAll()

                                player2.sendTranslatedMessage("vehicle-editor-metadata-success", BandiColors.YELLOW.toString())
                            } catch(e: Exception) {
                                player2.sendTranslatedMessage("vehicle-editor-metadata-invalid", BandiColors.RED.toString())
                            }
                        }
                    }
                }
            }

            3 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("vehicle-editor-metadata").replace("%format%", attachment.type.howToConfigure),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("vehicle-editor-metadata-cancelled"),
                ) { player2: Player, metadata: String ->
                    try {
                        val metadataList = metadata.replace(", ", ",").split(",")

                        attachment.type.onDeSpawn()
                        val oldType = attachment.type.id
                        attachment.type = AttachmentType.get(oldType, metadataList)!!
                        attachment.type.onSpawn(vehicle.ridingOn.origin.toLocation(vehicle.ridingOn.world))
                        session.updatePlayerItems()
                        markAll()

                        player2.sendTranslatedMessage("vehicle-editor-metadata-success", BandiColors.YELLOW.toString())
                    } catch(e: Exception) {
                        player2.sendTranslatedMessage("vehicle-editor-metadata-invalid", BandiColors.RED.toString())
                    }
                }
            }

            4 -> {
                session.setEditor(PositionEditor(vehicle, session, member, attachment))
            }

            5 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("vehicle-editor-id"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("vehicle-editor-id-cancelled"),
                ) { player: Player, message: String ->
                    attachment.id = message
                    player.sendTranslatedMessage("vehicle-editor-id-success", BandiColors.YELLOW.toString())
                }
            }

            6 -> {
                if(attachment.children.isNotEmpty()) {
                    session.setEditor(ChildSelector(vehicle, session, member, attachment))
                }
            }
        }
    }

    override fun onBackButtonPress() {
        val parent = member.getAllAttachments().find { it.children.contains(attachment) }

        if(parent == null) { // BATMAN
            session.setEditor(AttachmentSelector(vehicle, session, member))
        } else {
            session.setEditor(ChildSelector(vehicle, session, member, parent))
        }
    }

    override fun markAll() {
        attachment.getAllAttachments().forEach { it.type.markFor(player) }
    }

    override fun unMarkAll() {
        attachment.getAllAttachments().forEach { it.type.unMarkFor(player) }
    }
}