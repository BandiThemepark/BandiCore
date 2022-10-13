package net.bandithemepark.bandicore.park.npc.path.editor

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PathPointEditorCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("patheditor", true)) {
            if(sender is Player) {
                if(sender.hasPermission("bandithemepark.crew")) {
                    if(PathPointEditor.getSession(sender) == null) {
                        PathPointEditor.startSession(sender)
                        sender.sendTranslatedMessage("path-editor-started", BandiColors.YELLOW.toString())
                    } else {
                        PathPointEditor.getSession(sender)!!.finishSession()
                        sender.sendTranslatedMessage("path-editor-stopped", BandiColors.YELLOW.toString())
                    }
                } else {
                    sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                }
            }
        }

        return false
    }
}