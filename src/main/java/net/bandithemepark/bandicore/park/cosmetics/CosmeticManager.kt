package net.bandithemepark.bandicore.park.cosmetics

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendCosmetic
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CosmeticManager {
    val cosmetics = mutableListOf<Cosmetic>()
    val ownedCosmetics = mutableListOf<PlayerOwnedCosmetics>()

    fun setup() {
        loadCosmetics()
    }

    private fun loadCosmetics() {
        BackendCosmetic.getAll() { array ->
            for(element in array) {
                val cosmetic = Cosmetic.fromJson(element.asJsonObject)
                cosmetics.add(cosmetic)
            }

            Bukkit.getLogger().info("Loaded ${cosmetics.size} cosmetics")
        }
    }

    fun loadOwnedCosmetics(player: Player) {
        BackendCosmetic.getOwned(player) { array ->
            val cosmetics = mutableListOf<OwnedCosmetic>()

            for(element in array) {
                val ownedCosmetic = OwnedCosmetic.fromJson(element.asJsonObject)
                cosmetics.add(ownedCosmetic)
            }

            ownedCosmetics.add(PlayerOwnedCosmetics(player, cosmetics))
        }
    }

    fun unloadOwnedCosmetics(player: Player) {
        ownedCosmetics.removeIf { it.owner == player }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            BandiCore.instance.cosmeticManager.loadOwnedCosmetics(event.player)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            BandiCore.instance.cosmeticManager.unloadOwnedCosmetics(event.player)
        }
    }
}