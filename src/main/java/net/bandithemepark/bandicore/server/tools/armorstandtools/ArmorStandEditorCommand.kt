package net.bandithemepark.bandicore.server.tools.armorstandtools

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ArmorStandEditorCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("ast", true)) {
            if(sender is Player) {
                if(sender.hasPermission("bandithemepark.crew")) {
                    if(ArmorStandEditor.getSession(sender) == null) {
                        ArmorStandEditor.startSession(sender)
                        sender.sendTranslatedMessage("armor-stand-editor-started", "#E0D268")
                    } else {
                        ArmorStandEditor.getSession(sender)!!.finishSession()
                        sender.sendTranslatedMessage("armor-stand-editor-stopped", "#E0D268")
                    }
                } else {
                    sender.sendTranslatedMessage("no-permission", "#963939")
                }
            }
        }

        return false
    }
}