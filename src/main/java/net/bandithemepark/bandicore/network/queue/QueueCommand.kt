package net.bandithemepark.bandicore.network.queue

import com.google.common.io.ByteStreams
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class QueueCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("queue", true)) return false
        if(sender !is Player) return false

        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")

        if(BandiCore.instance.devMode) {
            out.writeUTF("bandithemepark")
        } else {
            out.writeUTF(BandiCore.instance.server.queueServer)
        }

        sender.sendPluginMessage(BandiCore.instance, "BungeeCord", out.toByteArray())

        return false
    }
}