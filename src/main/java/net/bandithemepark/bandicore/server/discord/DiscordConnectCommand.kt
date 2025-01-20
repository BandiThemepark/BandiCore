package net.bandithemepark.bandicore.server.discord

import net.bandithemepark.bandicore.network.backend.discord.BackendDiscordConnector
import net.bandithemepark.bandicore.network.backend.discord.DiscordAlreadyConnectedException
import net.bandithemepark.bandicore.network.backend.discord.DiscordConnectFailedException
import net.bandithemepark.bandicore.network.backend.discord.DiscordTokenExpiredException
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class DiscordConnectCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("discord", ignoreCase = true)) return false
        if(sender !is Player) return false

        if(args.size == 2) {
            if(args[0].equals("connect", ignoreCase = true)) {
                BackendDiscordConnector.connect(
                    sender.uniqueId,
                    args[1],
                    onSuccess = {
                        sender.sendTranslatedMessage("discord-connected", BandiColors.YELLOW.toString())
                    },
                    onError = {
                        when(it) {
                            is DiscordAlreadyConnectedException -> {
                                sender.sendTranslatedMessage("discord-connected-already", BandiColors.RED.toString())
                            }
                            is DiscordTokenExpiredException -> {
                                sender.sendTranslatedMessage("discord-connect-token-expired", BandiColors.RED.toString())
                            }
                            is DiscordConnectFailedException -> {
                                sender.sendTranslatedMessage("discord-connect-failed", BandiColors.RED.toString())
                            }
                            else -> {
                                it.printStackTrace()
                                sender.sendTranslatedMessage("discord-connect-failed", BandiColors.RED.toString())
                            }
                        }
                    }
                )
            } else {
                sendHelp(sender)
            }
        } else {
            sendHelp(sender)
        }

        return false
    }

    fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/discord connect <token>"))
    }

    override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("discord", ignoreCase = true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("connect"))
        }

        return null
    }
}