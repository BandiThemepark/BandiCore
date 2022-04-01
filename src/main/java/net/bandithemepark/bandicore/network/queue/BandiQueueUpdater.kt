package net.bandithemepark.bandicore.network.queue

import com.google.common.io.ByteStreams
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class BandiQueueUpdater: PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        val stringMessage = String(message)

        if(stringMessage == "PING") {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("PONG")
            Bukkit.getOnlinePlayers().toList()[0].sendPluginMessage(BandiCore.instance, "bandicore:queue", out.toByteArray())
        }
    }
}