package net.bandithemepark.bandicore.server.placeables

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlaceableRemoveCommand: CommandExecutor {
    private val RADIUS = 2.0

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("removenearplaceables", true)) return false
        if(sender !is Player) return false

        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        val near = BandiCore.instance.placeableManager.getPlacedNear(sender.location, RADIUS, true)
        if(near.isEmpty()) {
            sender.sendTranslatedMessage("placeable-remove-no-nearby", BandiColors.RED.toString())
            return true
        }

        near.forEach {
            it.remove()
            BandiCore.instance.placeableManager.removePlaced(it)
        }

        sender.sendTranslatedMessage("placeable-remove-success", BandiColors.GREEN.toString(), MessageReplacement("amount", near.size.toString()))

        return true
    }
}