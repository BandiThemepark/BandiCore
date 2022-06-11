package net.bandithemepark.bandicore.network.audioserver.map

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChunkRendererCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("chunkrenderer", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return false
        }

        if(sender !is Player) return false

        sender.sendTranslatedMessage("chunk-renderer-started", BandiColors.YELLOW.toString())
        val renderer = ChunkRenderer(sender.location.chunk)
        renderer.run {
            sender.sendTranslatedMessage("chunk-renderer-finished", BandiColors.YELLOW.toString())
        }

        return false
    }
}