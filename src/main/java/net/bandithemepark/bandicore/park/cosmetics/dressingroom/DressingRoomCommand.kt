package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendCosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.park.cosmetics.OwnedCosmetic
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class DressingRoomCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("dressingroom", true)) return false
        if(sender !is Player) return false

        if(args.isEmpty() || !sender.hasPermission("bandithemepark.crew")) {
            val currentSession = DressingRoomSession.activeSessions.find { it.player == sender }
            if (currentSession != null) {
                sender.sendTranslatedMessage("already-in-dressing-room", BandiColors.RED.toString())
                return true
            }

            val newSession = DressingRoomSession(sender, BandiCore.instance.cosmeticManager.dressingRoom)
        } else {
            if(args.size == 3) {
                val target = Bukkit.getPlayer(args[1])

                if(target == null) {
                    sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                    return false
                }

                val cosmetic = BandiCore.instance.cosmeticManager.cosmetics.find { it.name.equals(args[2], true) }
                if(cosmetic == null) {
                    sender.sendTranslatedMessage("equip-cosmetic-not-found", BandiColors.RED.toString())
                    return false
                }

                val ownedCosmetics = BandiCore.instance.cosmeticManager.ownedCosmetics.find { it.owner == target } ?: return false
                if(args[0].equals("give", true)) {
                    if(BandiCore.instance.cosmeticManager.ownsCosmetic(target, cosmetic)) {
                        sender.sendTranslatedMessage("player-already-owns-cosmetic", BandiColors.RED.toString())
                        return false
                    }

                    BackendCosmetic.give(target, cosmetic) {
                        ownedCosmetics.ownedCosmetics.add(OwnedCosmetic(cosmetic, false, 1, null))
                        sender.sendTranslatedMessage("cosmetic-given", BandiColors.YELLOW.toString())
                    }
                } else if(args[0].equals("remove", true)) {
                    if(!BandiCore.instance.cosmeticManager.ownsCosmetic(target, cosmetic)) {
                        sender.sendTranslatedMessage("player-does-not-own-cosmetic", BandiColors.RED.toString())
                        return false
                    }

                    BackendCosmetic.remove(target, cosmetic) {
                        ownedCosmetics.ownedCosmetics.removeIf { it.cosmetic == cosmetic }
                        sender.sendTranslatedMessage("cosmetic-removed", BandiColors.YELLOW.toString())
                    }
                } else {
                    sendHelp(sender)
                }
            } else {
                sendHelp(sender)
            }
        }

        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/cosmetic <give/remove> <player> <cosmetic>"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("dressingroom", true)) return null
        if(!sender.hasPermission("bandithemepark.crew")) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], mutableListOf("give", "remove"))
        } else if(args.size == 2) {
            return Util.getTabCompletions(args[1], Bukkit.getOnlinePlayers().map { it.name })
        } else if(args.size == 3) {
            val target = Bukkit.getPlayer(args[1]) ?: return null
            val ownedCosmetics = BandiCore.instance.cosmeticManager.ownedCosmetics.find { it.owner == target } ?: return null

            if(args[0].equals("give", true)) {
                return Util.getTabCompletions(args[2], BandiCore.instance.cosmeticManager.cosmetics.filter { !BandiCore.instance.cosmeticManager.ownsCosmetic(target, it)}.map { it.name })
            } else if(args[0].equals("remove", true)) {
                return Util.getTabCompletions(args[2], ownedCosmetics.ownedCosmetics.map { it.cosmetic.name })
            }
        }

        return null
    }
}