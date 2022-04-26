package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicleMember
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditor
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditorType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class ChildSelector(vehicle: TrackVehicle, session: TrackVehicleEditor, val member: TrackVehicleMember, val parent: Attachment): TrackVehicleEditorType(vehicle, session, true) {
    var currentSelected = parent.children[0]
        set(value) {
            field.getAllAttachments().forEach { it.type.unMarkFor(player) }
            field = value
            value.getAllAttachments().forEach { it.type.markFor(player) }
        }

    init {
        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
            currentSelected.getAllAttachments().forEach { it.type.markFor(player) }
        })
    }

    override fun getItem(slot: Int): ItemStack? {
        return when (slot) {
            0 -> ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.YELLOW}>Next child"))
            1 -> ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.YELLOW}>Previous child"))
            2 -> ItemFactory.create(Material.HOPPER, Util.color("<!i><${BandiColors.YELLOW}>Select"))
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                if(parent.children.size == 1) return
                val index = parent.children.indexOf(currentSelected)
                currentSelected = if (index == vehicle.members.size - 1) {
                    parent.children[0]
                } else {
                    parent.children[index + 1]
                }
            }

            1 -> {
                if(parent.children.size == 1) return
                val index = parent.children.indexOf(currentSelected)
                currentSelected = if (index == 0) {
                    parent.children[vehicle.members.size - 1]
                } else {
                    parent.children[index - 1]
                }
            }

            2 -> {
                session.setEditor(AttachmentEditor(vehicle, session, member, currentSelected))
            }
        }
    }

    override fun onBackButtonPress() {
        session.setEditor(AttachmentEditor(vehicle, session, member, parent))
    }

    override fun markAll() {

    }

    override fun unMarkAll() {

    }
}