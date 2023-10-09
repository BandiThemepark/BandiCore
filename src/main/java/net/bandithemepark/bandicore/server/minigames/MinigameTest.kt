package net.bandithemepark.bandicore.server.minigames

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MinigameTest: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("cooking", true)) return false
        if(!sender.hasPermission("bandithemepark.crew")) return false
        if(sender !is Player) return false

        val minigame = Minigame.get("cooking")!!
        minigame.games[0].onStart(listOf(MinigamePlayer(sender)))

        return false
    }
}