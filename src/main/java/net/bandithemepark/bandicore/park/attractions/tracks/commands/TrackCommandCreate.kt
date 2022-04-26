package net.bandithemepark.bandicore.park.attractions.tracks.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TrackCommandCreate: TrackCommand("create", "<id>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 1) {
            if(BandiCore.instance.trackManager.loadedTracks.find { it.id == args[0] } != null) {
                sender.sendTranslatedMessage("track-already-exists", BandiColors.RED.toString())
            } else {
                if(sender is Player) {
                    BandiCore.instance.trackManager.createTrack(args[0], Location(sender.world, sender.location.blockX+0.5, sender.location.y, sender.location.blockZ+0.5))
                    sender.sendTranslatedMessage("track-created", BandiColors.YELLOW.toString())
                }
            }
        } else {
            sendUsage(sender)
        }
    }
}