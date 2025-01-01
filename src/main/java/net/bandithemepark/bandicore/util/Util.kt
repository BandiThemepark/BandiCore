package net.bandithemepark.bandicore.util

import com.google.gson.JsonParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.map.MinecraftFont
import org.bukkit.util.StringUtil
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.sqrt

object Util {
    fun debug(topic: String, message: String) {
        Bukkit.getConsoleSender().sendMessage("[DEBUG] [$topic] $message")
    }

    /**
     * Formats a string using MiniMessage. MiniMessage documentation can be found at https://docs.adventure.kyori.net/minimessage/format.html.
     * Use things like <#FFFFFF>, <u> Underlined, <b> Bold, <i> Italic, <st> Strikethrough, <obf> Obfuscated, <newline> Newline in your messages.
     * @param message String to format
     * @return Formatted string as Paper Component
     */
    fun color(message: String): Component {
        return MiniMessage.miniMessage().deserialize(message)
    }

    fun legacyColor(message: String): Component {
        return Component.text(ChatColor.translateAlternateColorCodes('&', message))
    }

    fun Player.sendColoredMessage(message: String) {
        this.sendMessage(color(message))
    }

    fun Player.sendColoredActionBar(message: String) {
        this.sendActionBar(color(message))
    }

    /**
     * Get the length of a string in pixels
     */
    fun getLengthOfText(text: String): Int {
        return MinecraftFont.Font.getWidth(text)
    }

    /**
     * Get the length of a component in pixels
     */
    fun getLengthOfText(text: Component): Int {
        return MinecraftFont.Font.getWidth(text.getText())
    }

    /**
     * Utility function that generates tab completions
     * @param currentArg Currently filled in argument
     * @param options Available options that should be presented to the sender
     * @return Tab completions
     */
    fun getTabCompletions(currentArg: String, options: List<String>): MutableList<String> {
        val completions = mutableListOf<String>()
        StringUtil.copyPartialMatches(currentArg, options, completions)
        completions.sort()
        return completions
    }

    /**
     * Calculates the distance between two locations
     * @param loc1 First location
     * @param loc2 Second location
     * @return Distance between the two locations as a double
     */
    fun getLengthBetween(loc1: Location, loc2: Location): Double {
        val x = loc2.x - loc1.x
        val y = loc2.y - loc1.y
        val z = loc2.z - loc1.z
        val one = sqrt(x * x + z * z)
        return sqrt(one * one + y * y)
    }

    /**
     * Calculates the distance between two vectors
     * @param loc1 First vector
     * @param loc2 Second vector
     * @return Distance between the two vectors as a double
     */
    fun getLengthBetween(loc1: Vector, loc2: Vector): Double {
        val x = loc2.x - loc1.x
        val y = loc2.y - loc1.y
        val z = loc2.z - loc1.z
        val one = sqrt(x * x + z * z)
        return sqrt(one * one + y * y)
    }

    /**
     * Gets the text from a component
     * @return The text from the component
     */
    fun Component.getText(): String {
        return PlainTextComponentSerializer.plainText().serialize(this)
    }

    /**
     * Used to tell you if a player's skin has slim arms (the Alex skin model). Decodes the texture data from the player's GameProfile using Base64.
     * @return True if the player's skin has slim arms, false if not
     */
    fun Player.isAlexSkin(): Boolean {
        val encodedValue = (this as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value
        val decodedValue = String(Base64.getDecoder().decode(encodedValue))

        val json = JsonParser().parse(decodedValue).asJsonObject
        val skinObject = json.getAsJsonObject("textures").getAsJsonObject("SKIN")

        if(!skinObject.has("metadata")) return false
        if(!skinObject.getAsJsonObject("metadata").has("model")) return false

        val model = skinObject.getAsJsonObject("metadata").get("model").asString
        return model == "slim"
    }

    fun getNegativeText(amount: Int): String {
        var text = ""
        for(i in 0 until amount) { text += "\uE019" }
        return text
    }

    data class BackgroundCharacter(val character: String, val length: Int)

    val bossBarBackgroundCharacters = listOf(
        BackgroundCharacter("\uE009", 128),
        BackgroundCharacter("\uE008", 64),
        BackgroundCharacter("\uE007", 32),
        BackgroundCharacter("\uE006", 16),
        BackgroundCharacter("\uE005", 8),
        BackgroundCharacter("\uE004", 4),
        BackgroundCharacter("\uE003", 2),
        BackgroundCharacter("\uE002", 1)
    )

    val backgroundCharacters = listOf(
        BackgroundCharacter("\uE032", 128),
        BackgroundCharacter("\uE031", 64),
        BackgroundCharacter("\uE030", 32),
        BackgroundCharacter("\uE029", 16),
        BackgroundCharacter("\uE028", 8),
        BackgroundCharacter("\uE027", 4),
        BackgroundCharacter("\uE026", 2),
        BackgroundCharacter("\uE025", 1)
    )

    fun getBossBarBackgroundText(text: String, additionalLength: Int = 0): String {
        val length = getLengthOfText(text) + additionalLength + 4

        var backgroundText = "<font:boss_bar>\uE001\uE012"

        var remainingLength = length
        while(remainingLength > 0) {
            for(character in bossBarBackgroundCharacters) {
                if(remainingLength >= character.length) {
                    backgroundText += "${character.character}\uE012"
                    remainingLength -= character.length
                    break
                }
            }
        }

        backgroundText += "\uE001\uE012</font>"

        val negativeText = getNegativeText(length - 1)

        return "$backgroundText$negativeText"
    }

    fun getBackgroundText(text: String, additionalLength: Int = 0): String {
        val length = getLengthOfText(text) + additionalLength + 8

        var backgroundText = "\uE024\uE019"

        var remainingLength = length
        while(remainingLength > 0) {
            for(character in backgroundCharacters) {
                if(remainingLength >= character.length) {
                    backgroundText += "${character.character}\uE019"
                    remainingLength -= character.length
                    break
                }
            }
        }

        backgroundText += "\uE024\uE019"

        val negativeText = getNegativeText(length - 3)

        return "$backgroundText$negativeText"
    }

    private val characters = "abcdefghijklmnopqrstuvwxyz0123456789"
    private val smallCharacters = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡxʏᴢ0123456789"

    fun convertToSmallText(text: String): String {
        var newText = ""
        for(character in text.lowercase()) {
            if(characters.indexOf(character) == -1) {
                newText += character
                continue
            }

            newText += smallCharacters[characters.indexOf(character)]
        }
        return newText
    }

    /**
     * Converts a hex string to a Bukkit Color
     * @param hex Hex string to convert
     * @return Bukkit Color
     */
    fun hexToColor(hex: String): Color {
        val r = hex.substring(1, 3).toInt(16)
        val g = hex.substring(3, 5).toInt(16)
        val b = hex.substring(5, 7).toInt(16)
        return Color.fromRGB(r, g, b)
    }

    /**
     * Converts a Bukkit Color to a hex string
     * @return Hex string, e.g. "#FFFFFF"
     */
    fun Color.toHexString(): String {
        return "#${Integer.toHexString(this.red)}${Integer.toHexString(this.green)}${Integer.toHexString(this.blue)}"
    }
}