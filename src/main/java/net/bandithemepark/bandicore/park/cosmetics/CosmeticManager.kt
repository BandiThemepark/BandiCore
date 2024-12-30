package net.bandithemepark.bandicore.park.cosmetics

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendCosmetic
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoom
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.debug.Reloadable
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CosmeticManager: Reloadable {
    companion object {
        fun Player.getOwnedCosmetics(): PlayerOwnedCosmetics? {
            return BandiCore.instance.cosmeticManager.ownedCosmetics.find { it.owner == this }
        }

        fun Player.getEquipped(typeId: String): OwnedCosmetic? {
            val ownedCosmetics = getOwnedCosmetics() ?: return null
            return ownedCosmetics.ownedCosmetics.find { it.cosmetic.type.id == typeId && it.equipped }
        }
    }

    val cosmetics = mutableListOf<Cosmetic>()
    val ownedCosmetics = mutableListOf<PlayerOwnedCosmetics>()
    val dressingRoom = DressingRoom()

    fun setup() {
        loadCosmetics(true)
        dressingRoom.spawnDecorations()
        startTimer()
        register("cosmetics")
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimer(BandiCore.instance, Runnable {
            DressingRoomSession.activeSessions.forEach { it.onTick() }
        }, 0, 1)
    }

    /**
     * Loads all cosmetics from the database
     */
    private fun loadCosmetics(loadShops: Boolean = false) {
        BackendCosmetic.getAll { array ->
            for(element in array) {
                val cosmetic = Cosmetic.fromJson(element.asJsonObject)
                cosmetics.add(cosmetic)
            }

            if(loadShops) BandiCore.instance.shopManager.setup()

            Util.debug("Cosmetics", "Loaded ${cosmetics.size} cosmetics")
        }
    }

    /**
     * Loads all owned cosmetics for a player
     * @param player The player to load the cosmetics for
     */
    fun loadOwnedCosmetics(player: Player, callback: () -> Unit) {
        BackendCosmetic.getOwned(player) { array ->
            val cosmetics = mutableListOf<OwnedCosmetic>()

            for(element in array) {
                val ownedCosmetic = OwnedCosmetic.fromJson(element.asJsonObject)
                cosmetics.add(ownedCosmetic)
            }

            ownedCosmetics.add(PlayerOwnedCosmetics(player, cosmetics))
            callback()
        }
    }

    /**
     * Unloads all owned cosmetics for a player
     */
    fun unloadOwnedCosmetics(player: Player) {
        ownedCosmetics.removeIf { it.owner == player }
    }

    /**
     * Checks if a player owns a cosmetic
     * @param player The player to check
     * @param cosmetic The cosmetic to check
     * @return True if the player owns the cosmetic, false otherwise
     */
    fun ownsCosmetic(player: Player, cosmetic: Cosmetic): Boolean {
        val ownedCosmetic = ownedCosmetics.find { it.owner == player } ?: return false
        return ownedCosmetic.ownedCosmetics.any { it.cosmetic == cosmetic }
    }

    /**
     * Checks if a player owns a cosmetic by name
     * @param player The player to check
     * @param cosmeticName The name of the cosmetic to check
     * @return True if the player owns the cosmetic, false otherwise
     */
    fun ownsCosmetic(player: Player, cosmeticName: String): Boolean {
        val ownedCosmetic = ownedCosmetics.find { it.owner == player } ?: return false
        return ownedCosmetic.ownedCosmetics.any { it.cosmetic.name == cosmeticName }
    }

    /**
     * Equips a cosmetic to a player
     * @param player The player to equip the cosmetic to
     * @param cosmetic The cosmetic to equip
     * @param color The color of the cosmetic
     */
    fun equip(player: Player, cosmetic: Cosmetic, color: Color? = null) {
        val ownedCosmetic = ownedCosmetics.find { it.owner == player } ?: return
        val owned = ownedCosmetic.ownedCosmetics.find { it.cosmetic == cosmetic } ?: return
        val currentEquipped = ownedCosmetic.ownedCosmetics.filter { it.cosmetic.type.id == cosmetic.type.id }.find { it.equipped }

        if(currentEquipped != null) {
            currentEquipped.cosmetic.type.onUnEquip(player)
            currentEquipped.equipped = false
            BackendCosmetic.updateOwned(player, currentEquipped.cosmetic, equipped = false) { }
        }

        owned.color = color
        owned.cosmetic.type.onEquip(player, owned.color, owned.cosmetic)
        owned.equipped = true

        BackendCosmetic.updateOwned(player, cosmetic, equipped = true, color = color) { }
    }

    /**
     * Unequips a cosmetic from a player
     * @param player The player to unequip the cosmetic from
     * @param cosmeticTypeId The type ID of the cosmetic to unequip
     */
    fun unEquip(player: Player, cosmeticTypeId: String) {
        val ownedCosmetic = ownedCosmetics.find { it.owner == player } ?: return
        val equipped = ownedCosmetic.ownedCosmetics.filter { it.cosmetic.type.id == cosmeticTypeId }.find { it.equipped } ?: return

        equipped.cosmetic.type.onUnEquip(player)
        equipped.equipped = false

        BackendCosmetic.updateOwned(player, equipped.cosmetic, equipped = false) { }
    }

    /**
     * Re-equips all equipped cosmetics for a player
     */
    fun giveEquippedCosmetics(player: Player) {
        val ownedCosmetic = ownedCosmetics.find { it.owner == player } ?: return

        CosmeticType.types.forEach { it.onJoin(player) }

        ownedCosmetic.ownedCosmetics.forEach {
            if(it.equipped) it.cosmetic.type.onEquip(player, it.color, it.cosmetic)
        }
    }

    class Events: Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun onJoin(event: PlayerJoinEvent) {
            BandiCore.instance.cosmeticManager.loadOwnedCosmetics(event.player) {
                BandiCore.instance.cosmeticManager.giveEquippedCosmetics(event.player)
            }
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