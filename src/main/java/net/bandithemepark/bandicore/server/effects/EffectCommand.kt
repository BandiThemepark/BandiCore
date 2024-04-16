package net.bandithemepark.bandicore.server.effects

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EffectCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("effect", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size == 1) {
            if(args[0].equals("list", true)) {
                sendList(sender)
                return true
            }

            if(args[0].equals("reloadstarteffects", true)) {
                reloadStartEffects(sender)
                return true
            }
        }

        if(args.size == 2) {
            if(args[0].equals("play", true)) {
                playEffect(sender, args[1])
                return true
            }

            if(args[0].equals("stop", true)) {
                stopEffect(sender, args[1])
                return true
            }
        }

        sendHelp(sender)
        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/effect help"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/effect list"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/effect reloadstarteffects"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/effect play <id>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/effect stop <id>"))
    }

    private fun sendList(sender: CommandSender) {
        sender.sendTranslatedMessage("effect-command-list", BandiColors.YELLOW.toString(), MessageReplacement("effects", BandiCore.instance.effectManager.playingEffects.joinToString(", ") { it.fileName }))
    }

    private fun reloadStartEffects(sender: CommandSender) {
        sender.sendTranslatedMessage("effect-command-reload-start", BandiColors.YELLOW.toString())
        BandiCore.instance.effectManager.reloadServerStartEffects()
        sender.sendTranslatedMessage("effect-command-reload-finished", BandiColors.YELLOW.toString())
    }

    private fun playEffect(sender: CommandSender, id: String) {
        try {
            val effect = Effect(id, null)
            effect.play()

            sender.sendTranslatedMessage("effect-command-play", BandiColors.YELLOW.toString(), MessageReplacement("id", id))
        } catch (e: Exception) {
            sender.sendTranslatedMessage("effect-command-play-error", BandiColors.RED.toString(), MessageReplacement("id", id))
            e.printStackTrace()
        }
    }

    private fun stopEffect(sender: CommandSender, id: String) {
        val effect = BandiCore.instance.effectManager.playingEffects.find { it.fileName == id }

        if(effect == null) {
            sender.sendTranslatedMessage("effect-command-stop-not-found", BandiColors.RED.toString(), MessageReplacement("id", id))
            return
        }

        effect.stop()
        sender.sendTranslatedMessage("effect-command-stop", BandiColors.YELLOW.toString(), MessageReplacement("id", id))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("effect", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("help", "list", "reloadstarteffects", "play", "stop"))
        } else if(args.size == 2) {
            val list = listOf("stop")
            if(list.contains(args[0].lowercase())) {
                return Util.getTabCompletions(args[1], BandiCore.instance.effectManager.playingEffects.map { it.fileName })
            }
        }

        return null
    }
}