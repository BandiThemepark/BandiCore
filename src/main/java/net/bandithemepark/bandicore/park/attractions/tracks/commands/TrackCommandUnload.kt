package net.bandithemepark.bandicore.park.attractions.tracks.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.BandiColors
import org.bukkit.command.CommandSender

class TrackCommandUnload: TrackCommand("unload", "<id>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 1) {
            if(BandiCore.instance.trackManager.loadedTracks.find { it.id == args[0] } != null) {
                BandiCore.instance.trackManager.unloadTrack(args[0])
                sender.sendTranslatedMessage("track-unloaded", BandiColors.YELLOW.toString())
            } else {
                sender.sendTranslatedMessage("track-not-loaded", BandiColors.RED.toString())
            }
        } else {
            sendUsage(sender)
        }
    }
}