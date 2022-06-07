package net.bandithemepark.bandicore.park.attractions.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RideOPCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("rideop", true)) return false

        if(sender !is Player) {
            sender.sendTranslatedMessage("not-a-player", BandiColors.RED.toString())
            return false
        }

        if(!sender.hasPermission("bandithemepark.vip")) {
            sender.sendTranslatedMessage("rideop-vip-required", BandiColors.RED.toString())
            return false
        }

        val regionsAtPlayer = BandiCore.instance.regionManager.getRegionsAt(sender.location)
        val rideOPs = RideOP.rideOPs.filter { regionsAtPlayer.contains(it.region) }

        if(rideOPs.isEmpty()) {
            sender.sendTranslatedMessage("rideop-no-rideops", BandiColors.RED.toString())
            return false
        }

        rideOPs[0].openMenu(sender)

        return false
    }
}