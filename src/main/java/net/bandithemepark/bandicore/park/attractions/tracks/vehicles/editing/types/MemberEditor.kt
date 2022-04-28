package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.types

import net.bandithemepark.bandicore.BandiCore
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

class MemberEditor(vehicle: TrackVehicle, session: TrackVehicleEditor, val member: TrackVehicleMember): TrackVehicleEditorType(vehicle, session, true) {
    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.CHEST, Util.color("<!i><${BandiColors.YELLOW}>Set size (${member.size.toDouble()/BandiCore.instance.trackManager.pointsPerMeter.toDouble()})"))
            1 -> ItemFactory.create(Material.DEAD_BUSH, Util.color("<!i><${BandiColors.YELLOW}>Delete this member"))
            2 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.YELLOW}>Add attachment"))
            3 -> ItemFactory.create(Material.SCAFFOLDING, Util.color("<!i><${BandiColors.YELLOW}>Select attachment"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("vehicle-editor-size"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("vehicle-editor-size-cancelled"),
                ) { player: Player, message: String ->
                    try {
                        val multiplier = message.toDouble()
                        member.size = (multiplier*BandiCore.instance.trackManager.pointsPerMeter).toInt()
                        session.updatePlayerItems()
                        player.sendTranslatedMessage("vehicle-editor-size-success", BandiColors.YELLOW.toString())
                    } catch (ex: NumberFormatException) {
                        player.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                    }
                }
            }

            1 -> {
                session.setEditor(VehicleEditor(vehicle, session))
                member.getAllAttachments().forEach { it.type.onDeSpawn() }
                vehicle.members.remove(member)
            }

            2 -> {
                val attachment = Attachment("main", AttachmentPosition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), mutableListOf(), AttachmentType.get("model", listOf("DIAMOND_SWORD", "1"))!!, mutableListOf())
                attachment.type.onSpawn(vehicle.ridingOn.origin.toLocation(vehicle.ridingOn.world), attachment)
                member.attachments.add(attachment)

                markAll()
                player.sendTranslatedActionBar("vehicle-editor-attachment-added", BandiColors.YELLOW.toString())
            }

            3 -> {
                session.setEditor(AttachmentSelector(vehicle, session, member))
            }
        }
    }

    override fun onBackButtonPress() {
        session.setEditor(MemberSelector(vehicle, session))
    }

    override fun markAll() {
        member.getAllAttachments().forEach { it.type.markFor(player) }
    }

    override fun unMarkAll() {
        member.getAllAttachments().forEach { it.type.unMarkFor(player) }
    }
}