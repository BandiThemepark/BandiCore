package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DressingRoomCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("dressingroom", true)) return false
        if(sender !is Player) return false

        val currentSession = DressingRoomSession.activeSessions.find { it.player == sender }
        if(currentSession != null) {
            sender.sendTranslatedMessage("already-in-dressing-room", BandiColors.RED.toString())
            return true
        }

        val newSession = DressingRoomSession(sender, BandiCore.instance.cosmeticManager.dressingRoom)

        return false
    }
}