package net.bandithemepark.bandicore.park.shops

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ShopsMenuCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!command.name.equals("shops", true)) return false
        if(sender !is Player) return false

        val menu = ShopsMenu(sender)
        menu.open(0)

        return false
    }
}