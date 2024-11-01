package net.bandithemepark.bandicore.server.essentials.warps

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!command.name.equals("spawn", true)) return false
        if(sender !is Player) return false

        val warp = BandiCore.instance.server.warpManager.warps.find { it.name == "spawn" }
        if(warp == null) {
            sender.sendTranslatedMessage("spawn-not-set", BandiColors.RED.toString())
            return false
        }

        sender.teleport(warp.location)
        return false
    }
}