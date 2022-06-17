package net.bandithemepark.bandicore.server.custom.player

import com.google.gson.JsonParser
import net.bandithemepark.bandicore.util.Util.isAlexSkin
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class CustomPlayerSkin(val playerUUID: UUID, val texture: String, val slim: Boolean) {
    companion object {
        fun convertProfileTexture(texture: String): String {
            val decodedValue = String(Base64.getDecoder().decode(texture))

            val json = JsonParser.parseString(decodedValue).asJsonObject
            val url = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").asString

            return url.replace("http://textures.minecraft.net/texture/", "")
        }

        fun Player.getCustomPlayerSkin(): CustomPlayerSkin {
            return CustomPlayerSkin(this.uniqueId,
                convertProfileTexture((player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value),
                this.isAlexSkin())
        }
    }
}