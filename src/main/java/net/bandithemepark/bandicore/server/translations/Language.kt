package net.bandithemepark.bandicore.server.translations

import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.FileManager
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import java.io.File
import java.nio.charset.Charset

class Language(val id: String) {
    val translations = hashMapOf<String, String>()

    init {
        loadTranslations()
    }

    fun loadTranslations() {
        translations.clear()

        val file = File(BandiCore.instance.dataFolder, "/translations/$id.json")
        val data = FileUtils.readFileToString(file, Charset.defaultCharset())
        val json = JsonParser.parseString(data).asJsonObject

        for(key in json.keySet()) {
            translations[key] = json.get(key).asString
        }
    }
}