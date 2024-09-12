package net.bandithemepark.bandicore.server.custom.player

import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.isAlexSkin
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.mineskin.MineskinClient
import java.awt.Color
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

class CustomPlayerSkin(val playerUUID: UUID?, val texture: String, val slim: Boolean) {
    companion object {
        val adaptedSkins = mutableMapOf<Player, CustomPlayerSkin>()
        const val BACKUP_SKIN = "e11371e8c60249e0ad8f53c74a2c99217d7a6f1fe47f057b86f8cb44c2e6cc61"

        fun convertProfileTexture(texture: String): String {
            val decodedValue = String(Base64.getDecoder().decode(texture))

            val json = JsonParser().parse(decodedValue).asJsonObject
            val url = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").asString

            return url.replace("http://textures.minecraft.net/texture/", "")
        }

        fun Player.getAdaptedSkin(): CustomPlayerSkin {
            if(adaptedSkins[this] != null) {
                return adaptedSkins[this]!!
            } else {
                Util.debug("CustomPlayerSkin", "Failed to load adapted skin for player ${this.name}. Returning a default skin for now, and generating a new skin in the meanwhile...")
                generateSkin(this)
                return CustomPlayerSkin(this.uniqueId, BACKUP_SKIN, false)
            }
        }

        fun Player.getCustomPlayerSkin(): CustomPlayerSkin {
            return getAdaptedSkin()

//            return CustomPlayerSkin(this.uniqueId,
//                convertProfileTexture((player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value),
//                this.isAlexSkin())
        }

        fun generateSkin(player: Player) {
            Bukkit.getScheduler().runTaskAsynchronously(BandiCore.instance, Runnable {
                val playerTextures = (player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value
                val isAlexSkin = player.isAlexSkin()

                val decodedValue = String(Base64.getDecoder().decode(playerTextures))
                val json = JsonParser().parse(decodedValue).asJsonObject
                val url = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").asString

                // Download image from url
                val image = ImageIO.read(URL(url))

                // Add red pixel in top right if not alexskin, add green pixel if alexskin
                if(!isAlexSkin) {
                    image.setRGB(image.width - 1, 0, Color(255, 0, 0).rgb)
                } else {
                    image.setRGB(image.width - 1, 0, Color(0, 255, 0).rgb)
                }

                val mineSkinClient = MineskinClient("BandiCore")
                mineSkinClient.generateUpload(image).thenAccept { skin ->
                    val newUrl = skin.data.texture.url
                    val texture = newUrl.replace("http://textures.minecraft.net/texture/", "")
                    adaptedSkins[player] = CustomPlayerSkin(player.uniqueId, texture, isAlexSkin)
                }
            })
        }
    }

    class Events: Listener {
        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            generateSkin(event.player)
        }
    }

    class Command: CommandExecutor {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if(!command.name.equals("getskin", true)) return false
            if(sender !is Player) return false
            if(!sender.hasPermission("bandithemepark.crew")) return false

            sender.sendMessage(Util.color("<${BandiColors.YELLOW}><click:copy_to_clipboard:${sender.getCustomPlayerSkin().texture}>Click me to copy your skin texture (adapted)</click>"))

            return false
        }
    }
}