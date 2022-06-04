package net.bandithemepark.bandicore.server.regions

import net.bandithemepark.bandicore.network.backend.BackendRegion
import org.bukkit.Location

class BandiRegionManager {
    var regions = mutableListOf<BandiRegion>()

    fun getRegionsAt(location: Location): List<BandiRegion> {
        return regions.filter { it.containsLocation(location) }
    }

    fun getFromId(id: String): BandiRegion? {
        return regions.find { it.name == id }
    }

    fun getPrioritized(list: List<BandiRegion>): BandiRegion? {
        return list.maxByOrNull { it.priority }
    }

    fun getAllIds(): List<String> {
        return regions.map { it.name }
    }

    fun createNew(name: String, callback: () -> Unit): BandiRegion {
        val region = BandiRegion(name, name, 0, mutableListOf())
        regions.add(region)

        BackendRegion().create(name) { callback.invoke() }

        return region
    }

    fun deleteRegion(region: BandiRegion, callback: () -> Unit) {
        regions.remove(region)
        BackendRegion().deleteWithName(region.name) { callback.invoke() }
    }

    fun saveRegion(region: BandiRegion, callback: () -> Unit) {
        BackendRegion().save(region.name, region.displayName, region.priority, region.areasToJSON()) { callback.invoke() }
    }

    fun loadRegion(name: String, callback: () -> Unit) {
        BackendRegion().getWithName(name) {
            val displayName = it.get("displayName").asString
            val priority = it.get("priority").asInt
            val areasJson = it.getAsJsonArray("areas").asJsonObject
            val areas = BandiRegion.loadAreasFromJson(areasJson)

            val oldRegion = getFromId(name)!!
            oldRegion.displayName = displayName
            oldRegion.priority = priority
            oldRegion.areas = areas

            callback.invoke()
        }
    }

    fun loadAll() {
        BackendRegion().getAll { data ->
            val regions = mutableListOf<BandiRegion>()

            for(region in data) {
                val name = region.asJsonObject.get("name").asString
                val displayName = region.asJsonObject.get("displayName").asString
                val priority = region.asJsonObject.get("priority").asInt
                val areasJson = region.asJsonObject.getAsJsonArray("areas").asJsonObject
                val areas = BandiRegion.loadAreasFromJson(areasJson)
                regions.add(BandiRegion(name, displayName, priority, areas))
            }

            this.regions = regions
        }
    }
}