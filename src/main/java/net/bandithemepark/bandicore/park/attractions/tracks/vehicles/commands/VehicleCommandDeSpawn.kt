package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender

class VehicleCommandDeSpawn: TrackVehicleCommand("despawn", "<vehicleID>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 1) {
            val vehicle = BandiCore.instance.trackManager.vehicleManager.getVehicle(args[0])

            if(vehicle != null) {
                vehicle.deSpawnAttachments()
                BandiCore.instance.trackManager.vehicleManager.vehicles.remove(vehicle)
                sender.sendTranslatedMessage("track-vehicle-command-despawned", BandiColors.YELLOW.toString())
            } else {
                sender.sendTranslatedMessage("track-vehicle-not-found", BandiColors.RED.toString())
            }
        } else {
            sendUsage(sender)
        }
    }
}