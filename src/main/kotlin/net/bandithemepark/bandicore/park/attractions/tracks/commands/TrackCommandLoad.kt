package net.bandithemepark.bandicore.park.attractions.tracks.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandSender

class TrackCommandLoad: TrackCommand("load", "<id>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 1) {
            if(BandiCore.instance.trackManager.loadedTracks.find { it.id == args[0] } != null) {
                sender.sendTranslatedMessage("track-already-exists", BandiColors.RED.toString())
            } else {
                BandiCore.instance.trackManager.loadTrack(args[0])
                sender.sendTranslatedMessage("track-loaded", BandiColors.YELLOW.toString())
            }
        } else {
            sendUsage(sender)
        }
    }
}