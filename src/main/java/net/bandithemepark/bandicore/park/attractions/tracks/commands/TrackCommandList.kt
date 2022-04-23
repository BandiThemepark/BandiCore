package net.bandithemepark.bandicore.park.attractions.tracks.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.BandiColors
import org.bukkit.command.CommandSender

class TrackCommandList: TrackCommand("list", "") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        val loadedTracks = BandiCore.instance.trackManager.loadedTracks.joinToString(", ") { it.id }
        sender.sendTranslatedMessage("track-list", BandiColors.YELLOW.toString(), MessageReplacement("tracks", loadedTracks))
    }
}