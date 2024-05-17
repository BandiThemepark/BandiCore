package net.bandithemepark.bandicore.park.cosmetics.command

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class EquipCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("equip", ignoreCase = true)) return false
        if(sender !is Player) return false

        if(args.size != 2) {
            sendHelp(sender)
            return false
        }

        val type = CosmeticType.getType(args[0])
        if(type == null) {
            sender.sendTranslatedMessage("equip-type-not-found", BandiColors.RED.toString())
            return false
        }

        val playerOwnedCosmetics = BandiCore.instance.cosmeticManager.ownedCosmetics.find { it.owner == sender } ?: return false
        val cosmetic = playerOwnedCosmetics.ownedCosmetics.find { it.cosmetic.name == args[1] }

        if(cosmetic == null) {
            sender.sendTranslatedMessage("equip-cosmetic-not-found", BandiColors.RED.toString())
            return false
        }

        BandiCore.instance.cosmeticManager.equip(sender, cosmetic.cosmetic)
        sender.sendTranslatedMessage("equip-success", BandiColors.YELLOW.toString(), MessageReplacement("cosmetic", cosmetic.cosmetic.displayName))

        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/equip <type> <cosmetic>"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("equip", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], CosmeticType.types.map { it.id })
        } else if(args.size == 2) {
            val type = CosmeticType.getType(args[0]) ?: return null
            val playerOwnedCosmetics = BandiCore.instance.cosmeticManager.ownedCosmetics.find { it.owner == sender } ?: return null
            val ownedCosmetics = playerOwnedCosmetics.ownedCosmetics.filter { it.cosmetic.type.id == type.id }
            return Util.getTabCompletions(args[1], ownedCosmetics.map { it.cosmetic.name })
        }

        return null
    }
}