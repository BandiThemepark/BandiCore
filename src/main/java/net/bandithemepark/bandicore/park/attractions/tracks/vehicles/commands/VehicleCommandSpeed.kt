package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender

class VehicleCommandSpeed: TrackVehicleCommand("setspeed", "<vehicleId> <speed>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 2) {
            val vehicle = BandiCore.instance.trackManager.vehicleManager.getVehicle(args[0])

            if(vehicle != null) {
                try {
                    val speed = args[1].toDouble()
                    vehicle.speedKMH = speed
                    sender.sendTranslatedMessage("track-vehicle-command-set-speed", BandiColors.YELLOW.toString())
                } catch (ex: NumberFormatException) {
                    sender.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                }
            } else {
                sender.sendTranslatedMessage("track-vehicle-not-found", BandiColors.RED.toString())
            }
        } else {
            sendUsage(sender)
        }
    }
}