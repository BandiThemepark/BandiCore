package net.bandithemepark.bandicore.server.translations

import kotlinx.coroutines.*
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendPlayer
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.ceil

object LanguageUtil {
    val loadedLanguages = hashMapOf<Player, Language>()

    /**
     * Gets the language of a player
     * @param player The player to get the language of
     * @return The language of the player
     */
    fun getLanguage(player: Player): Language {
        return if(loadedLanguages.containsKey(player)) {
            loadedLanguages[player]!!
        } else {
            BandiCore.instance.server.getLanguage("english")!!
        }
    }

    /**
     * Loads the language of a player. This function is called when a player joins the server.
     * @param player The player to load the language of
     */
    fun loadLanguage(player: Player) {
          val backendPlayer = BackendPlayer(player)
          backendPlayer.get { data ->
              val language = BandiCore.instance.server.getShortenedLanguage(data.get("lang").asString)!!
              loadedLanguages[player] = language
          }
    }

    // Function that gets a message from a given language and message id. If the message is null, it will retrieve the message from english
    /**
     * Gets a translated message for a certain language. Returns english version if translation could not be found
     * @param language The language to get the message from
     * @param messageId The message id to get the message from
     * @return The translated message
     */
    fun getMessage(language: Language, messageId: String): String {
        val message = language.translations[messageId]
        if (message != null) {
            return message
        }
        return BandiCore.instance.server.getLanguage("english")!!.translations[messageId]!!
    }

    /**
     * Gets a translated message for a certain language with additional replacements. Returns english version if translation could not be found
     * @param language The language to get the message from
     * @param messageId The message id to get the message from
     * @param replacements The replacements to replace in the message. Optional
     * @return The translated message
     */
    fun getMessage(language: Language, messageId: String, vararg replacements: MessageReplacement): String {
        var message = getMessage(language, messageId)
        for(replacement in replacements) {
            message = message.replace("%${replacement.variable}%", replacement.replacement)
        }
        return message
    }

    // Function that gets a message from a player and message id
    /**
     * Gets a translated message for a player. Returns english version if translation could not be found
     * @param player The player to get the message for
     * @param messageId The message id to get the message from
     * @param replacements The replacements to replace in the message. Optional
     * @return The translated message
     */
    fun getMessage(player: Player, messageId: String, vararg replacements: MessageReplacement): String {
        var message = getMessage(getLanguage(player), messageId)
        for(replacement in replacements) {
            message = message.replace("%${replacement.variable}%", replacement.replacement)
        }
        return message
    }

    /**
     * Gets a translated message for a player. Returns english version if translation could not be found
     * @param messageId The message id to get the message from
     * @param replacements The replacements to replace in the message. Optional
     * @return The translated message
     */
    fun Player.getTranslatedMessage(messageId: String, vararg replacements: MessageReplacement): String {
        return getMessage(this, messageId, *replacements)
    }

    /**
     * Sends a translated message to a CommandSender. Message is in english if translation cannot be found
     * @param messageId The ID of the message.
     * @param color Hex color code to use. Format as #FFFFFF
     * @param replacements The replacements to replace in the message. Optional
     */
    fun CommandSender.sendTranslatedMessage(messageId: String, color: String, vararg replacements: MessageReplacement) {
        if(this is Player) {
            this.sendMessage(Util.color("<$color>"+this.getTranslatedMessage(messageId, *replacements)))
        } else {
            this.sendMessage(Util.color("<$color>"+getMessage(BandiCore.instance.server.getLanguage("english")!!, messageId, *replacements)))
        }
    }

    /**
     * Sends a translated message to a CommandSender as an action bar. Message is in english if translation cannot be found
     * @param messageId The ID of the message.
     * @param color Hex color code to use. Format as #FFFFFF
     * @param replacements The replacements to replace in the message. Optional
     */
    fun CommandSender.sendTranslatedActionBar(messageId: String, color: String, vararg replacements: MessageReplacement) {
        val message = if(this is Player) {
            this.getTranslatedMessage(messageId, *replacements)
        } else {
            getMessage(BandiCore.instance.server.getLanguage("english")!!, messageId, *replacements)
        }

        this.sendActionBar(Util.color("${Util.getBackgroundText(message)}<$color>$message"))
    }
}