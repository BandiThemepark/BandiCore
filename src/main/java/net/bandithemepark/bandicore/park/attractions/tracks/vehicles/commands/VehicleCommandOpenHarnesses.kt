package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender

class VehicleCommandOpenHarnesses: TrackVehicleCommand("openharnesses", "<vehicleID>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 1) {
            val vehicle = BandiCore.instance.trackManager.vehicleManager.getVehicle(args[0])

            if(vehicle != null) {
                TrackUtil.setHarnessOpen(vehicle, true)
                sender.sendTranslatedMessage("track-vehicle-command-open-harnesses", BandiColors.YELLOW.toString())
            } else {
                sender.sendTranslatedMessage("track-vehicle-not-found", BandiColors.RED.toString())
            }
        } else {
            sendUsage(sender)
        }
    }
}