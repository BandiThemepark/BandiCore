package net.bandithemepark.bandicore.park.cosmetics

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendCosmetic
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoom
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.util.debug.Reloadable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CosmeticManager: Reloadable {
    val cosmetics = mutableListOf<Cosmetic>()
    val ownedCosmetics = mutableListOf<PlayerOwnedCosmetics>()
    val dressingRoom = DressingRoom()

    fun setup() {
        loadCosmetics()
        dressingRoom.spawnDecorations()
        startTimer()
        register("cosmetics")
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimer(BandiCore.instance, Runnable {
            DressingRoomSession.activeSessions.forEach { it.onTick() }
        }, 0, 1)
    }

    private fun loadCosmetics() {
        BackendCosmetic.getAll() { array ->
            for(element in array) {
                val cosmetic = Cosmetic.fromJson(element.asJsonObject)
                cosmetics.add(cosmetic)
            }

            Bukkit.getConsoleSender().sendMessage("Loaded ${cosmetics.size} cosmetics")
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

    override fun reload() {
        cosmetics.clear()
        loadCosmetics()
    }
}