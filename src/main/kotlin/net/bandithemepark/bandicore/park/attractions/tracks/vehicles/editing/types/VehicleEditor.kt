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

class VehicleEditor(vehicle: TrackVehicle, session: TrackVehicleEditor): TrackVehicleEditorType(vehicle, session, false) {
    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.PAPER, Util.color("<!i><${BandiColors.YELLOW}>Friction multiplier (${vehicle.frictionMultiplier})"))
            1 -> ItemFactory.create(Material.PAPER, Util.color("<!i><${BandiColors.YELLOW}>Save as"))
            2 -> ItemFactory.create(Material.PISTON, Util.color("<!i><${BandiColors.YELLOW}>Add member"))
            3 -> ItemFactory.create(Material.MINECART, Util.color("<!i><${BandiColors.YELLOW}>Select member"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("vehicle-editor-friction-multiplier"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("vehicle-editor-friction-multiplier-cancelled"),
                ) { player: Player, message: String ->
                    try {
                        val multiplier = message.toDouble()
                        vehicle.frictionMultiplier = multiplier
                        session.updatePlayerItems()
                        player.sendTranslatedMessage("vehicle-editor-friction-multiplier-success", BandiColors.YELLOW.toString())
                    } catch (ex: NumberFormatException) {
                        player.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                    }
                }
            }

            1 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("vehicle-editor-save"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("vehicle-editor-save-cancelled"),
                ) { player: Player, message: String ->
                    vehicle.saveAs(message)
                    player.sendTranslatedMessage("vehicle-editor-save-success", BandiColors.YELLOW.toString())
                }
            }

            2 -> {
                val member = TrackVehicleMember(vehicle, BandiCore.instance.trackManager.pointsPerMeter)
                member.attachments.add(Attachment("main", AttachmentPosition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), listOf(), AttachmentType.get("model", listOf("DIAMOND_SWORD", "1"))!!, mutableListOf()))
                member.attachments[0].type.onSpawn(vehicle.ridingOn.origin.toLocation(vehicle.ridingOn.world))
                vehicle.members.add(member)

                markAll()
                player.sendTranslatedActionBar("vehicle-editor-member-added", BandiColors.YELLOW.toString())
            }

            3 -> {
                session.setEditor(MemberSelector(vehicle, session))
            }
        }
    }

    override fun onBackButtonPress() {

    }

    override fun markAll() {
        vehicle.getAllAttachments().forEach { it.type.markFor(player) }
    }

    override fun unMarkAll() {
        vehicle.getAllAttachments().forEach { it.type.unMarkFor(player) }
    }
}