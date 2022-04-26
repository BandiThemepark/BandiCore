package net.bandithemepark.bandicore.server.essentials.ranks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SetRankCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("setrank", true)) {
            if(sender is Player) {
                if(args.size == 2) {
                    val rank = BandiCore.instance.server.rankManager.loadedRanks.find { it.id == args[1] }

                    if(rank != null) {
                        val player = Bukkit.getPlayer(args[0])

                        if(player != null) {
                            GlobalScope.launch {
                                withContext(Dispatchers.IO) {
                                    BandiCore.instance.server.rankManager.setNewRank(player, rank)
                                    sender.sendTranslatedMessage("rank-changed", BandiColors.YELLOW.toString(), MessageReplacement("rank", rank.id), MessageReplacement("player", player.name))
                                }
                            }
                        } else {
                            sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                        }
                    } else {
                        sender.sendTranslatedMessage("rank-not-found", BandiColors.RED.toString())
                    }
                } else {
                    sender.sendTranslatedMessage("set-rank-invalid-args", BandiColors.RED.toString())
                }
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): MutableList<String>? {
        if(command.name.equals("setrank", true)) {
            if(args.size == 2) {
                return Util.getTabCompletions(args[1], BandiCore.instance.server.rankManager.loadedRanks.map { it.id })
            }
        }
        return null
    }
}