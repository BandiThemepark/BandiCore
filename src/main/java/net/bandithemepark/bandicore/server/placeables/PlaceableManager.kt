package net.bandithemepark.bandicore.server.placeables

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

class PlaceableManager {
    val types = mutableListOf<PlaceableType>()
    private val placed = mutableListOf<PlacedPlaceable>()

    init {
        loadTypes()
        loadPlaced()
        spawnPlaced()
    }

    private fun loadTypes() {
        val file = File("plugins/BandiCore/placeables/config.json")
        val json = JsonParser().parse(file.readText()).asJsonObject

        json.getAsJsonArray("types").forEach {
            types.add(PlaceableType.fromJson(it.asJsonObject))
        }
    }

    private fun loadPlaced() {
        val file = File("plugins/BandiCore/placeables/placed.json")
        val json = JsonParser().parse(file.readText()).asJsonObject

        json.getAsJsonArray("placed").forEach {
            placed.add(PlacedPlaceable.fromJson(it.asJsonObject))
        }
    }

    private fun spawnPlaced() {
        placed.forEach { it.spawn() }
    }

    private fun savePlaced() {
        val json = JsonObject()
        val array = JsonArray()
        placed.forEach { array.add(it.toJson()) }
        json.add("placed", array)

        val file = File("plugins/BandiCore/placeables/placed.json")
        file.writeText(json.toString())
    }

    fun getType(id: String): PlaceableType? {
        return types.firstOrNull { it.id == id }
    }

    fun addPlaced(placed: PlacedPlaceable) {
        this.placed.add(placed)
        savePlaced()
    }

    fun removePlaced(placed: PlacedPlaceable) {
        this.placed.remove(placed)
        savePlaced()
    }
}