package net.bandithemepark.bandicore.park.cosmetics.command

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class UnEquipCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("unequip", ignoreCase = true)) return false
        if(sender !is Player) return false

        if(args.size != 1) {
            sendHelp(sender)
            return false
        }

        val type = CosmeticType.getType(args[0])
        if(type == null) {
            sender.sendTranslatedMessage("equip-type-not-found", BandiColors.RED.toString())
            return false
        }

        BandiCore.instance.cosmeticManager.unEquip(sender, type.id)
        sender.sendTranslatedMessage("unequip-success", BandiColors.YELLOW.toString())

        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/unequip <type>"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("unequip", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], CosmeticType.types.map { it.id })
        }

        return null
    }
}