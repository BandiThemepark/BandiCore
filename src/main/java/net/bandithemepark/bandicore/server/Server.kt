package net.bandithemepark.bandicore.server

import net.bandithemepark.bandicore.server.mode.ServerMode
import net.bandithemepark.bandicore.server.translations.Language
import net.bandithemepark.bandicore.util.FileManager

class Server {
    var serverMode: ServerMode
    val languages = mutableListOf<Language>()

    init {
        ServerMode("open", "Yey we are open!", true, true).register()
        ServerMode("vip", "Open only for our VIPs!", true, false).register()
        ServerMode("maintenance", "We are in maintenance!", false, false).register()

        val fm = FileManager()
        serverMode = ServerMode.getFromId(fm.getConfig("config.yml").get().getString("serverMode")!!)!!
        for(language in fm.getConfig("config.yml").get().getStringList("languages")) {
            languages.add(Language(language.split("-")[0], language.split("-")[1]))
        }
    }

    // Utility to get a language from an id
    fun getLanguage(id: String): Language? {
        return languages.find { it.id == id }
    }

    fun getShortenedLanguage(shortenedId: String): Language? {
        return languages.find { it.shortenedId == shortenedId }
    }

    fun changeServerMode(mode: ServerMode) {
        serverMode = mode
        val fm = FileManager()
        fm.getConfig("config.yml").get().set("serverMode", mode.id)
        fm.saveConfig("config.yml")
    }
}