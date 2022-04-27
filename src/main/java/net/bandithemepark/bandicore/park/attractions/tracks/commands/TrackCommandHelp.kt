package net.bandithemepark.bandicore.park.attractions.tracks.commands

import org.bukkit.command.CommandSender

class TrackCommandHelp: TrackCommand("help", "") {
    override fun onUse(sender: CommandSender, args: MutableList<String>) {
        sendHelp(sender)
    }
}