package net.bandithemepark.bandicore.server.essentials.moderation

import com.google.common.io.ByteStreams
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BanCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("ban", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size < 2) {
            sender.sendTranslatedMessage("ban-invalid-args", BandiColors.RED.toString())
            return true
        }

        val target = Bukkit.getPlayer(args[0])

        if(target == null) {
            sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
            return true
        }

        val reason = args.sliceArray(1 until args.size).joinToString(" ")
        val messageJson = JsonObject()
        messageJson.addProperty("action", "ban")
        messageJson.addProperty("uuid", target.uniqueId.toString())
        messageJson.addProperty("reason", reason)

        val out = ByteStreams.newDataOutput()
        out.writeUTF(messageJson.toString())
        Bukkit.getServer().sendPluginMessage(BandiCore.instance, "bandicore:ban", out.toByteArray())

        return false
    }
}