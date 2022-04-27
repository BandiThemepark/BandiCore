package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.editing.TrackVehicleEditor
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class VehicleCommandEdit: TrackVehicleCommand("edit", "[<vehicleID>]") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(sender is Player) {
            val editingSession = TrackVehicleEditor.getEditor(sender)

            if(editingSession != null) {
                TrackVehicleEditor.stopEditing(sender)
                sender.sendTranslatedMessage("track-vehicle-editor-stopped", BandiColors.YELLOW.toString())
            } else {
                if(args.size == 1) {
                    val vehicle = BandiCore.instance.trackManager.vehicleManager.getVehicle(args[0])

                    if(vehicle != null) {
                        TrackVehicleEditor.startEditing(sender, vehicle)
                        sender.sendTranslatedMessage("track-vehicle-editor-started", BandiColors.YELLOW.toString())
                    } else {
                        sender.sendTranslatedMessage("track-vehicle-not-found", BandiColors.RED.toString())
                    }
                } else {
                    sendUsage(sender)
                }
            }
        }
    }
}