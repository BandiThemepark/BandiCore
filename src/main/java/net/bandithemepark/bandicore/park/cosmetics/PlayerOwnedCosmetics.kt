package net.bandithemepark.bandicore.park.cosmetics

import org.bukkit.entity.Player

data class PlayerOwnedCosmetics(val owner: Player, val ownedCosmetics: MutableList<OwnedCosmetic>) {
}