package net.bandithemepark.bandicore.server.translations

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object LanguageUtil {
    val loadedLanguages = hashMapOf<Player, Language>()

    fun getLanguage(player: Player): Language {
        return if(loadedLanguages.containsKey(player)) {
            loadedLanguages[player]!!
        } else {
            // TODO Actually retrieve the language from the API and storing it so it can be retrieved later
            BandiCore.instance.server.getLanguage("english")!!
        }
    }

    // Function that gets a message from a given language and message id. If the message is null, it will retrieve the message from english
    fun getMessage(language: Language, messageId: String): String {
        val message = language.translations[messageId]
        if (message != null) {
            return message
        }
        return BandiCore.instance.server.getLanguage("english")!!.translations[messageId]!!
    }

    fun getMessage(language: Language, messageId: String, vararg replacements: MessageReplacement): String {
        var message = getMessage(language, messageId)
        for(replacement in replacements) {
            message = message.replace("%${replacement.variable}%", replacement.replacement)
        }
        return message
    }

    // Function that gets a message from a player and message id
    fun getMessage(player: Player, messageId: String, vararg replacements: MessageReplacement): String {
        var message = getMessage(getLanguage(player), messageId)
        for(replacement in replacements) {
            message = message.replace("%${replacement.variable}%", replacement.replacement)
        }
        return message
    }

    fun Player.getTranslatedMessage(messageId: String, vararg replacements: MessageReplacement): String {
        return getMessage(this, messageId, *replacements)
    }

    fun CommandSender.sendTranslatedMessage(messageId: String, color: String, vararg replacements: MessageReplacement) {
        if(this is Player) {
            this.sendMessage(Util.color("<$color>"+this.getTranslatedMessage(messageId, *replacements)))
        } else {
            this.sendMessage(Util.color("<$color>"+getMessage(BandiCore.instance.server.getLanguage("english")!!, messageId, *replacements)))
        }
    }

    fun CommandSender.sendTranslatedActionBar(messageId: String, color: String, vararg replacements: MessageReplacement) {
        if(this is Player) {
            this.sendActionBar(Util.color("<$color>"+this.getTranslatedMessage(messageId, *replacements)))
        } else {
            this.sendActionBar(Util.color("<$color>"+getMessage(BandiCore.instance.server.getLanguage("english")!!, messageId, *replacements)))
        }
    }
}