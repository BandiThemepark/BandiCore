package net.bandithemepark.bandicore.park.cosmetics.requirements.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.cosmetics.requirements.CosmeticRequirementType
import org.bukkit.entity.Player

class RidecounterCosmeticRequirement: CosmeticRequirementType("ridecounter") {
    override fun check(player: Player, settings: String): Boolean {
        val attractionId = getAttractionId(settings)
        val minRideCount = getRideCount(settings)

        return BandiCore.instance.server.ridecounterManager.getRidecountOnOf(player, attractionId) >= minRideCount
    }

    override fun getText(settings: String): String {
        val attraction = Attraction.get(getAttractionId(settings))!!
        val rideCount = getRideCount(settings)

        return "Ride ${attraction.appearance.displayName} at least $rideCount times"
    }

    private fun getAttractionId(settings: String): String {
        return settings.replace(", ", ",").split(",")[0]
    }

    private fun getRideCount(settings: String): Int {
        return settings.replace(", ", ",").split(",")[1].toInt()
    }
}