package net.bandithemepark.bandicore.park.attractions.tracks.commands

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.BandiColors
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TrackCommandEdit: TrackCommand("edit", "<id>") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        if(args.size == 1) {
            if(BandiCore.instance.trackManager.loadedTracks.find { it.id == args[0] } != null) {
                if(sender is Player) {
                    if(BandiCore.instance.trackManager.editor.isEditing(sender)) {
                        BandiCore.instance.trackManager.editor.stopEditor(sender)
                        sender.sendTranslatedMessage("track-editor-stopped", BandiColors.YELLOW.toString())
                    } else {
                        if(BandiCore.instance.trackManager.loadedTracks.find { it.id == args[0] } != null) {
                            BandiCore.instance.trackManager.editor.startEditor(sender, BandiCore.instance.trackManager.loadedTracks.find { it.id == args[0] }!!)
                            sender.sendTranslatedMessage("track-editor-started", BandiColors.YELLOW.toString())
                        } else {
                            sender.sendTranslatedMessage("track-not-loaded", BandiColors.RED.toString())
                        }
                    }
                }
            } else {
                sender.sendTranslatedMessage("track-not-loaded", BandiColors.RED.toString())
            }
        } else if(BandiCore.instance.trackManager.editor.isEditing(sender as Player)) {
            BandiCore.instance.trackManager.editor.stopEditor(sender)
            sender.sendTranslatedMessage("track-editor-stopped", BandiColors.YELLOW.toString())
        } else {
            sendUsage(sender)
        }
    }
}