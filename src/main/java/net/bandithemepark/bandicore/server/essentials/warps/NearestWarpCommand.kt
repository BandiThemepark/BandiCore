package net.bandithemepark.bandicore.server.essentials.warps

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NearestWarpCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("nearestwarp", true)) return false
        if(sender !is Player) return false

        val playerWarps = BandiCore.instance.server.warpManager.getWarpsFor(sender)
        val nearestWarp = playerWarps.minByOrNull { it.location.distance(sender.location) }!!
        sender.teleport(nearestWarp.location)

        return false
    }
}