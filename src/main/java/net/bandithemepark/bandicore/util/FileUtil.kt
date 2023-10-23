package net.bandithemepark.bandicore.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileReader

object FileUtil {
    fun loadJsonFrom(path: String): JsonObject {
        val reader = FileReader(path)
        return JsonParser().parse(reader).asJsonObject
    }

    fun saveToFile(json: JsonObject, fileName: String) {
        val builder = GsonBuilder().create()
        val stringJson = builder.toJson(json)

        val file = File(fileName)
        createDirectoryIfNotExists(file.parent)
        if(!file.exists()) file.createNewFile()
        file.writeText(stringJson)
    }

    fun createDirectoryIfNotExists(directoryPath: String) {
        val directory = File(directoryPath)
        if(!directory.exists()) directory.mkdirs()
    }

    fun getFilesInDirectory(path: String): List<File> {
        val directory = File(path)
        return directory.listFiles()?.toList() ?: listOf()
    }

    fun doesFileExist(path: String): Boolean {
        val file = File(path)
        return file.exists()
    }
}