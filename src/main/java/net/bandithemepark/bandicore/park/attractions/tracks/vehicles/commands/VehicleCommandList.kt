package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender

class VehicleCommandList: TrackVehicleCommand("list", "") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        val vehicleIds = BandiCore.instance.trackManager.vehicleManager.vehicles.joinToString(", ") { it.id }
        sender.sendTranslatedMessage("track-vehicle-command-list", BandiColors.YELLOW.toString(), MessageReplacement("vehicles", vehicleIds))
    }
}