package net.bandithemepark.bandicore.util

import com.google.gson.JsonObject

class BandiConfig {
    var json: JsonObject = FileUtil.loadJsonFrom("plugins/BandiCore/config.json")

    fun save() {
        FileUtil.saveToFile(json, "plugins/BandiCore/config.json")
    }

    fun reload() {
        json = FileUtil.loadJsonFrom("plugins/BandiCore/config.json")
    }
}