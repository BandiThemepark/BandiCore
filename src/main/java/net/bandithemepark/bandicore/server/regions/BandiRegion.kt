package net.bandithemepark.bandicore.server.regions

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector2
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.Polygonal2DRegion
import net.bandithemepark.bandicore.util.entity.PacketEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class BandiRegion(val uuid: UUID, val name: String, var displayName: String, var priority: Int, var areas: MutableList<Polygonal2DRegion>) {
    val packetEntities = mutableListOf<PacketEntity>()

    fun containsLocation(location: Location): Boolean {
        areas.forEach {
            if(it.contains(BlockVector3.at(location.blockX, location.blockY, location.blockZ))) return true
        }
        return false
    }

    fun containsPlayer(player: Player): Boolean {
        return containsLocation(player.location)
    }

    fun areasToJSON(): JsonObject {
        val array = JsonArray()

        areas.forEach {
            val jsonObject = JsonObject()
            jsonObject.addProperty("minY", it.minimumY)
            jsonObject.addProperty("maxY", it.maximumY)
            jsonObject.addProperty("world", it.world!!.id)

            val points = JsonArray()
            it.points.forEach { point ->
                val pointJson = JsonObject()
                pointJson.addProperty("x", point.x)
                pointJson.addProperty("z", point.z)
                points.add(pointJson)
            }

            jsonObject.add("points", points)
            array.add(jsonObject)
        }

        val json = JsonObject()
        json.add("areas", array)
        return json
    }

    fun loadAreasFromJson(json: JsonObject) {
        val areas = mutableListOf<Polygonal2DRegion>()
        val array = json.getAsJsonArray("areas")

        array.forEach {
            val minY = it.asJsonObject.get("minY").asInt
            val maxY = it.asJsonObject.get("maxY").asInt
            val worldId = it.asJsonObject.get("world").asString
            val world = BukkitAdapter.adapt(Bukkit.getWorld(worldId))

            val points = mutableListOf<BlockVector2>()
            val pointsArray = it.asJsonObject.getAsJsonArray("points")
            pointsArray.forEach { point ->
                val x = point.asJsonObject.get("x").asInt
                val z = point.asJsonObject.get("z").asInt
                points.add(BlockVector2.at(x, z))
            }

            areas.add(Polygonal2DRegion(world, points, minY, maxY))
        }

        this.areas = areas
    }

    companion object {
        fun loadAreasFromJson(json: JsonObject): MutableList<Polygonal2DRegion> {
            val areas = mutableListOf<Polygonal2DRegion>()

            json.getAsJsonArray("areas")?.forEach {
                val minY = it.asJsonObject.get("minY").asInt
                val maxY = it.asJsonObject.get("maxY").asInt
                val worldId = it.asJsonObject.get("world").asString
                val world = BukkitAdapter.adapt(Bukkit.getWorld(worldId))

                val points = mutableListOf<BlockVector2>()
                val pointsArray = it.asJsonObject.getAsJsonArray("points")
                pointsArray.forEach { point ->
                    val x = point.asJsonObject.get("x").asInt
                    val z = point.asJsonObject.get("z").asInt
                    points.add(BlockVector2.at(x, z))
                }

                areas.add(Polygonal2DRegion(world, points, minY, maxY))
            }

            return areas
        }
    }
}