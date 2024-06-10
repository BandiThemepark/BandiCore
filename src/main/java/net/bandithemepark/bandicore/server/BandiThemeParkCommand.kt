package net.bandithemepark.bandicore.server

import com.google.common.io.ByteStreams
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.mode.ServerMode
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.debug.Debuggable
import net.bandithemepark.bandicore.util.debug.Reloadable
import net.bandithemepark.bandicore.util.debug.Testable
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class BandiThemeParkCommand: CommandExecutor, TabCompleter, Listener {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("bandithemepark", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size == 1) {
            if(args[0].equals("restart", true)) {
                BandiCore.instance.restarter.start()
            } else if(args[0].equals("switch", true)) {
                if(sender !is Player) return false

                val out = ByteStreams.newDataOutput()
                out.writeUTF("Connect")
                if(BandiCore.instance.devMode) {
                    out.writeUTF("development")
                } else {
                    out.writeUTF("development")
                }
                sender.sendPluginMessage(BandiCore.instance, "BungeeCord", out.toByteArray())
            } else {
                sendHelp(sender)
            }
        } else if(args.size == 2) {
            if(args[0].equals("servermode", true)) {
                val newServerMode = ServerMode.getFromId(args[1])

                if(newServerMode == null) {
                    sender.sendTranslatedMessage("server-mode-invalid-mode", BandiColors.RED.toString())
                } else {
                    BandiCore.instance.server.changeServerMode(newServerMode)
                    sender.sendTranslatedMessage("server-mode-changed", BandiColors.YELLOW.toString(), MessageReplacement("mode", newServerMode.id))
                }
            } else if(args[0].equals("debug", true)) {
                val debuggable = Debuggable.getDebuggable(args[1])

                if(debuggable != null) {
                    sender.sendTranslatedMessage("debug-start", BandiColors.YELLOW.toString(), MessageReplacement("option", args[1]))
                    debuggable.debug(sender)
                } else {
                    sender.sendTranslatedMessage("debug-invalid-option", BandiColors.RED.toString())
                }
            } else if(args[0].equals("test", true)) {
                val testable = Testable.getTestable(args[1])

                if(testable != null) {
                    sender.sendTranslatedMessage("test-start", BandiColors.YELLOW.toString(), MessageReplacement("option", args[1]))
                    testable.test(sender)
                } else {
                    sender.sendTranslatedMessage("test-invalid-option", BandiColors.RED.toString())
                }
            } else if(args[0].equals("reload", true)) {
                if(args[1].equals("all", true)) {
                    for(reloadable in Reloadable.reloadables.values) {
                        val id = Reloadable.reloadables.keys.find { Reloadable.getReloadable(it) == reloadable }!!
                        sender.sendTranslatedMessage("reload-start", BandiColors.YELLOW.toString(), MessageReplacement("option", id))
                        reloadable.reload()
                    }
                    return false
                }

                val reloadable = Reloadable.getReloadable(args[1])

                if(reloadable != null) {
                    sender.sendTranslatedMessage("reload-start", BandiColors.YELLOW.toString(), MessageReplacement("option", args[1]))
                    reloadable.reload()
                } else {
                    sender.sendTranslatedMessage("reload-invalid-option", BandiColors.RED.toString())
                }
            } else {
                sendHelp(sender)
            }
        } else {
            sendHelp(sender)
        }

        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark help"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark restart"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark switch"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark servermode <mode>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark debug <option>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark test <option>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark reload <option/all>"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("bandithemepark", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("restart", "servermode", "debug", "help", "test", "reload", "switch"))
        } else if(args.size == 2) {
            if(args[0].equals("servermode", true)) {
                return Util.getTabCompletions(args[1], ServerMode.getAllIds())
            } else if(args[0].equals("debug", true)) {
                return Util.getTabCompletions(args[1], Debuggable.debuggables.keys.toList())
            } else if(args[0].equals("test", true)) {
                return Util.getTabCompletions(args[1], Testable.testables.keys.toList())
            } else if(args[0].equals("reload", true)) {
                return Util.getTabCompletions(args[1], Reloadable.reloadables.keys.toList().plus("all"))
            }
        }

        return null
    }

    // Disable the default /restart command and make it execute the custom restart
    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if(!event.player.hasPermission("bandithemepark.crew")) return

        if(event.message.equals("/restart", true)) {
            event.isCancelled = true
            BandiCore.instance.restarter.start()
        }
    }
}