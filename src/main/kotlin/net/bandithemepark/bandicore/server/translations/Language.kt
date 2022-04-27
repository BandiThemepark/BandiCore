package net.bandithemepark.bandicore.server.translations

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendPlayer
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.Util
import org.apache.commons.io.FileUtils
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.nio.charset.Charset

class Language(val id: String, val shortenedId: String) {
    val translations = hashMapOf<String, String>()

    init {
        loadTranslations()
    }

    private fun loadTranslations() {
        translations.clear()

        val fm = FileManager()
        val filesToTranslate = fm.getConfig("config.yml").get().getStringList("filesToTranslate")

        for(fileName in filesToTranslate) {
            val file = File(BandiCore.instance.dataFolder, "/translations/$id/$fileName.json")
            val data = FileUtils.readFileToString(file, Charset.defaultCharset())
            val json = JsonParser.parseString(data).asJsonObject

            for (key in json.keySet()) {
                translations[key] = json.get(key).asString
            }
        }
    }

    class Events: Listener {
        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            LanguageUtil.loadLanguage(event.player)
        }
    }

    class Command: CommandExecutor, TabCompleter {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if(command.name.equals("setlanguage", true)) {
                if(sender is Player) {
                    if(args.size == 1) {
                        val language = BandiCore.instance.server.getLanguage(args[0])

                        if(language != null) {
                             val backendPlayer = BackendPlayer(sender)
                             backendPlayer.updatePlayer(false, false, 0, null, null, language.shortenedId, false) {}
                             LanguageUtil.loadedLanguages[sender] = language
                             sender.sendTranslatedMessage("language-changed", BandiColors.YELLOW.toString(), MessageReplacement("language", language.id.replaceFirstChar { it.uppercase() }))
                        } else {
                            sender.sendTranslatedMessage("language-not-found", BandiColors.RED.toString())
                        }
                    } else {
                        sender.sendTranslatedMessage("set-language-invalid-args", BandiColors.RED.toString())
                    }
                }
            }
            return false
        }

        override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): MutableList<String>? {
            if(command.name.equals("setlanguage", true)) {
                if(args.size == 1) {
                    return Util.getTabCompletions(args[0], BandiCore.instance.server.languages.map { it.id })
                }
            }
            return null
        }
    }
}