package net.bandithemepark.bandicore.server.restart

import com.google.common.io.ByteStreams
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.mode.ServerMode
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration

class Restart {
    var countdownLeft = 200 // Ticks, so 10 seconds
    var countdownStarted = false
    var toSend = mutableListOf<Player>()
    var restartSent = false

    fun start() {
        // TODO Close all attractions

        for(player in Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(
                Util.color("<${BandiColors.RED}>${player.getTranslatedMessage("restarting-soon-title")}"),
                Util.color("<${BandiColors.RED}>${player.getTranslatedMessage("restarting-soon-subtitle")}"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            ))

            player.sendMessage(" ")
            player.sendTranslatedMessage("restarting-soon-message", BandiColors.RED.toString())
            player.sendMessage(" ")
        }

        val fm = FileManager()
        fm.getConfig("config.yml").get().set("preRestartMode", BandiCore.instance.server.serverMode.id)
        fm.saveConfig("config.yml")

        BandiCore.instance.server.changeServerMode(ServerMode.getFromId("restart")!!)

        Bukkit.getScheduler().scheduleSyncRepeatingTask(BandiCore.instance, {
            update()
        }, 0, 1)
    }

    private fun update() {
        if(countdownStarted) {
            if(countdownLeft <= 0) {
                if(toSend.isNotEmpty()) {
                    val playerToSend = toSend.first()
                    toSend.remove(playerToSend)

                    sendToQueue(playerToSend)
                } else {
                    if(!restartSent) {
                        restartSent = true
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
                            Bukkit.getServer().spigot().restart()
                        }, 20)
                    }
                }
            } else {
                countdownLeft--

                for(player in Bukkit.getOnlinePlayers()) player.sendTranslatedActionBar("restarting-in-seconds", BandiColors.RED.toString(), MessageReplacement("seconds", (countdownLeft/20).toString()))

                if(countdownLeft == 0) {
                    toSend = Bukkit.getOnlinePlayers().toMutableList()
                }
            }
        } else {
            Bukkit.getOnlinePlayers().forEach { it.sendTranslatedActionBar("restarting-soon-message", BandiColors.RED.toString()) }

            var arePlayersRiding = false
            // TODO Actually check if any players are still on attractions
            if(!arePlayersRiding) countdownStarted = true
        }
    }

    fun sendToQueue(player: Player) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(BandiCore.instance.server.queueServer)
        player.sendPluginMessage(BandiCore.instance, "BungeeCord", out.toByteArray())
    }
}