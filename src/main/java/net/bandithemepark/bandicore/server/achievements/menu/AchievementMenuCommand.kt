package net.bandithemepark.bandicore.server.achievements.menu

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AchievementMenuCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("achievements", true)) return false
        if(sender !is Player) return false

        AchievementCategoriesMenu(sender)

        return false
    }
}