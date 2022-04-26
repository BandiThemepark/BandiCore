package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GamemodeCommand : CommandExecutor, TabCompleter {

    // List of valid gamemode options. These are used for tab completion.
    private val survival = mutableListOf("0", "survival", "s")
    private val creative = mutableListOf("1", "creative", "c")
    private val adventure = mutableListOf("2", "adventure", "a")
    private val spectator = mutableListOf("3", "spectator", "sp")
    private val gameModes = survival.plus(creative).plus(adventure).plus(spectator)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("gamemode", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return true
        }
        if (args.size >= 1 && args.size <= 2) {
            val gameMode = getGamenode(args[0])
            if (gameMode == null) {
                sender.sendTranslatedMessage("gamemode-invalid-args", BandiColors.RED.toString())
                return true
            }
            // Change gamemode for single player
            if (args.size == 1 && sender is Player) {
                sender.gameMode = gameMode
                sender.sendTranslatedMessage(
                    "gamemode-changed-self",
                    BandiColors.YELLOW.toString(),
                    MessageReplacement("gamemode", gameMode.name)
                )
            }
            // Change gamemode for target player
            if (args.size == 2) {
                val target = Bukkit.getPlayer(args[1])
                if (target == null) {
                    sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                    return true
                }
                target.gameMode = gameMode
                sender.sendTranslatedMessage(
                    "gamemode-changed-player",
                    BandiColors.YELLOW.toString(),
                    MessageReplacement("gamemode", gameMode.name),
                    MessageReplacement("player", target.name)
                )
            }
            return true
        }
        sender.sendTranslatedMessage("gamemode-invalid-args", BandiColors.RED.toString())
        return true
    }

    fun getGamenode(string: String): GameMode? {
        if (survival.contains(string.lowercase())) {
            return GameMode.SURVIVAL
        }
        if (creative.contains(string.lowercase())) {
            return GameMode.CREATIVE
        }
        if (adventure.contains(string.lowercase())) {
            return GameMode.ADVENTURE
        }
        if (spectator.contains(string.lowercase())) {
            return GameMode.SPECTATOR
        }
        return null
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (command.name.equals("gamemode", true)) {
            if (sender.hasPermission("bandithemepark.crew")) {
                return if (args.size == 1) {
                    Util.getTabCompletions(
                        args[0],
                        gameModes
                    )
                } else if (args.size == 2) {
                    return null
                } else {
                    mutableListOf()
                }
            }
        }
        return null
    }

}