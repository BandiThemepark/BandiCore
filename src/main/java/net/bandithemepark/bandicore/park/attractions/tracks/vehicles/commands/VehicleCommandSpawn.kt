package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender

class VehicleCommandSpawn: TrackVehicleCommand("spawn", "<vehicleID> <trackID>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 2) {
            val track = BandiCore.instance.trackManager.loadedTracks.find { it.id == args[1] }

            if(track != null) {
                BandiCore.instance.trackManager.vehicleManager.loadTrain(args[0], track, TrackPosition(track.nodes[0], 0), 10.0)
                sender.sendTranslatedMessage("track-vehicle-command-spawned", BandiColors.YELLOW.toString())
            } else {
                sender.sendTranslatedMessage("track-not-loaded", BandiColors.RED.toString())
            }
        } else {
            sendUsage(sender)
        }
    }
}