package net.bandithemepark.bandicore.server.placeables

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File

class PlaceableManager {
    val types = mutableListOf<PlaceableType>()
    private val placed = mutableListOf<PlacedPlaceable>()
    val selectedColors = hashMapOf<Player, Color>()

    init {
        loadTypes()
    }

    private fun loadTypes() {
        val file = File("plugins/BandiCore/placeables/config.json")
        val json = JsonParser().parse(file.readText()).asJsonObject

        json.getAsJsonArray("types").forEach {
            types.add(PlaceableType.fromJson(it.asJsonObject))
        }

        Util.debug("Placeables", "Loaded ${types.size} placeable types")
    }

    fun loadPlaced() {
        val file = File("plugins/BandiCore/placeables/placed.json")
        val json = JsonParser().parse(file.readText()).asJsonObject

        json.getAsJsonArray("placed").forEach {
            placed.add(PlacedPlaceable.fromJson(it.asJsonObject))
        }

        Util.debug("Placeables", "Loaded ${placed.size} placed placeables")
        spawnPlaced()
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

    fun getPlacedAt(location: Location): PlacedPlaceable? {
        return placed.firstOrNull { it.location == location }
    }

    fun getPlacedNear(location: Location, radius: Double, filterNoBarrier: Boolean): List<PlacedPlaceable> {
        var placedNear = placed.toList()
        if(filterNoBarrier) placedNear = placedNear.filter { !it.type.barrierBlock }
        placedNear = placedNear.filter { it.location.distance(location) <= radius }

        return placedNear
    }
}