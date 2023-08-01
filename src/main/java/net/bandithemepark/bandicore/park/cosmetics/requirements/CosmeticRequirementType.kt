package net.bandithemepark.bandicore.park.cosmetics.requirements

import org.bukkit.entity.Player

abstract class CosmeticRequirementType(val id: String) {
    abstract fun check(player: Player, settings: String): Boolean
    abstract fun getText(settings: String): String

    fun register() {
        types.add(this)
    }

    companion object {
        val types = mutableListOf<CosmeticRequirementType>()

        fun getType(id: String): CosmeticRequirementType? {
            return types.find { it.id == id }
        }
    }
}