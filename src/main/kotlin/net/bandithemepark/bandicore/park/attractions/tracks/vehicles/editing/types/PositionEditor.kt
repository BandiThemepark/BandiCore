package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicleMember
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditor
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditorType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat
import java.text.NumberFormat

class PositionEditor(vehicle: TrackVehicle, session: TrackVehicleEditor, val member: TrackVehicleMember, val attachment: Attachment): TrackVehicleEditorType(vehicle, session, true) {
    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.YELLOW}>Move X (${DecimalFormat("##.#").format(attachment.position.x)})"))
            1 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.YELLOW}>Move Y (${DecimalFormat("##.#").format(attachment.position.y)})"))
            2 -> ItemFactory.create(Material.SHEARS, Util.color("<!i><${BandiColors.YELLOW}>Move Z (${DecimalFormat("##.#").format(attachment.position.z)})"))
            3 -> ItemFactory.create(Material.HEART_OF_THE_SEA, Util.color("<!i><${BandiColors.YELLOW}>Rotate pitch (${DecimalFormat("##.#").format(attachment.position.pitch)})"))
            4 -> ItemFactory.create(Material.HEART_OF_THE_SEA, Util.color("<!i><${BandiColors.YELLOW}>Rotate yaw (${DecimalFormat("##.#").format(attachment.position.yaw)})"))
            5 -> ItemFactory.create(Material.HEART_OF_THE_SEA, Util.color("<!i><${BandiColors.YELLOW}>Rotate roll (${DecimalFormat("##.#").format(attachment.position.roll)})"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                if(clickType == ClickType.LEFT) {
                    attachment.position.x -= 0.1
                } else {
                    attachment.position.x += 0.1
                }
                session.updatePlayerItems()
            }

            1 -> {
                if(clickType == ClickType.LEFT) {
                    attachment.position.y -= 0.1
                } else {
                    attachment.position.y += 0.1
                }
                session.updatePlayerItems()
            }

            2 -> {
                if(clickType == ClickType.LEFT) {
                    attachment.position.z -= 0.1
                } else {
                    attachment.position.z += 0.1
                }
                session.updatePlayerItems()
            }

            3 -> {
                if(clickType == ClickType.LEFT) {
                    attachment.position.pitch -= 5
                } else {
                    attachment.position.pitch += 5
                }
                session.updatePlayerItems()
            }

            4 -> {
                if(clickType == ClickType.LEFT) {
                    attachment.position.yaw -= 5
                } else {
                    attachment.position.yaw += 5
                }
                session.updatePlayerItems()
            }


            5 -> {
                if(clickType == ClickType.LEFT) {
                    attachment.position.roll -= 5
                } else {
                    attachment.position.roll += 5
                }
                session.updatePlayerItems()
            }
        }
    }

    override fun onBackButtonPress() {
        session.setEditor(AttachmentEditor(vehicle, session, member, attachment))
    }

    override fun markAll() {
        attachment.getAllAttachments().forEach { it.type.markFor(player) }
    }

    override fun unMarkAll() {
        attachment.getAllAttachments().forEach { it.type.unMarkFor(player) }
    }
}