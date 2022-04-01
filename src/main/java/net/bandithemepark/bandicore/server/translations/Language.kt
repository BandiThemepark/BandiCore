package net.bandithemepark.bandicore.server.translations

import net.bandithemepark.bandicore.util.FileManager

class Language(val id: String) {
    val translations = hashMapOf<String, String>()

    init {
        loadTranslations()
    }

    fun loadTranslations() {
        translations.clear()
        val fm = FileManager()
        for(key in fm.getConfig("translations/$id.yml").get().getConfigurationSection("")!!.getKeys(false)) {
            translations[key] = fm.getConfig("translations/$id.yml").get().getString(key)!!
        }
    }
}