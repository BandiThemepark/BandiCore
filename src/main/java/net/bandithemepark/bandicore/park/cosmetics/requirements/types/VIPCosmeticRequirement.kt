package net.bandithemepark.bandicore.park.cosmetics.requirements.types

import net.bandithemepark.bandicore.park.cosmetics.requirements.CosmeticRequirementType
import org.bukkit.entity.Player

class VIPCosmeticRequirement: CosmeticRequirementType("vip") {
    override fun check(player: Player, settings: String): Boolean {
        return player.hasPermission("bandithemepark.vip")
    }

    override fun getText(settings: String): String {
        return "Have a VIP rank (/buy)"
    }
}