package net.bandithemepark.bandicore.server.essentials.moderation

import com.google.common.io.ByteStreams
import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.*

class UnBanCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("unban", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.isEmpty()) {
            sender.sendTranslatedMessage("unban-invalid-args", BandiColors.RED.toString())
            return true
        }

        try {
            UUID.fromString(args[0])
        } catch (e: IllegalArgumentException) {
            sender.sendTranslatedMessage("invalid-uuid", BandiColors.RED.toString())
            return true
        }

        val uuid = UUID.fromString(args[0])

        val messageJson = JsonObject()
        messageJson.addProperty("action", "unban")
        messageJson.addProperty("uuid", uuid.toString())

        val out = ByteStreams.newDataOutput()
        out.writeUTF(messageJson.toString())
        Bukkit.getServer().sendPluginMessage(BandiCore.instance, "bandicore:ban", out.toByteArray())

        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        sender.sendTranslatedMessage("unban-success", BandiColors.GREEN.toString(), MessageReplacement("uuid", uuid.toString()), MessageReplacement("username", offlinePlayer.name ?: "unknown player"))

        return false
    }
}