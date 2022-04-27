package net.bandithemepark.bandicore.server.essentials.ranks.nametag

import io.papermc.paper.adventure.PaperAdventure
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.PacketEntityArmorStand
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.scheduler.BukkitRunnable

class PlayerNameTag(val player: Player) {
    val armorStand = PacketEntityArmorStand()
    var heightOffset = 0.0
    private var isVisible = false

    var hidden = false
    set(value) {
        field = value
        if(value) {
            if(isVisible) deSpawn()
        } else {
            if(isVisible) spawn()
        }
    }

    /**
     * Spawns the name tag. Automatically called on initialization.
     */
    fun spawn() {
        isVisible = true
        if(!armorStand.spawned && !hidden) {
            armorStand.visibilityType = PacketEntity.VisibilityType.BLACKLIST
            armorStand.visibilityList = mutableListOf(player)

            armorStand.spawn(player.location.clone().add(0.0, 1.7 + heightOffset, 0.0))
            armorStand.handle!!.isInvisible = true
            armorStand.handle!!.isNoGravity = true
            armorStand.handle!!.isCustomNameVisible = true
            (armorStand.handle!! as ArmorStand).isMarker = true

            val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
            armorStand.handle!!.customName = PaperAdventure.asVanilla(Util.color("<${rank.color}>${rank.name} ${player.name}"))
            armorStand.updateMetadata()
        }
    }

    /**
     * De-spawns the name tag, automatically called on player quit
     */
    fun deSpawn() {
        isVisible = false
        armorStand.deSpawn()
    }

    /**
     * Updates the name tag's displayname. Use this when you change the player's rank, or something like that.
     */
    fun updateName() {
        val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
        armorStand.handle!!.customName = PaperAdventure.asVanilla(Util.color("<${rank.color}>${rank.name} ${player.name}"))
        armorStand.updateMetadata()
    }

    /**
     * Updates the position of the name tag. Doesn't need to be called really.
     */
    fun updatePosition() {
        armorStand.teleport(player.location.clone().add(0.0, 1.7+heightOffset, 0.0))
    }

    init {
        spawn()
        active.add(this)
    }

    companion object {
        val active = mutableListOf<PlayerNameTag>()

        fun get(player: Player): PlayerNameTag? {
            return active.find { it.player == player }
        }

        fun Player.getNameTag(): PlayerNameTag? {
            return get(this)
        }
    }

    class Events: Listener {
        @EventHandler
        fun onSneak(event: PlayerToggleSneakEvent) {
            if(event.player.gameMode == GameMode.SPECTATOR) return
            val nameTag = event.player.getNameTag()
            if(event.isSneaking) {
                if(!event.player.isFlying) {
                    nameTag?.deSpawn()
                }
            } else {
                nameTag?.spawn()
            }
        }

        @EventHandler
        fun onFlyingLand(event: PlayerToggleFlightEvent) {
            if(event.player.gameMode == GameMode.SPECTATOR) return
            if(!event.isFlying) {
                if(event.player.isSneaking) {
                    event.player.getNameTag()?.deSpawn()
                }
            }
        }

        @EventHandler
        fun onGameModeSwitch(event: PlayerGameModeChangeEvent) {
            if(event.newGameMode == GameMode.SPECTATOR) {
                event.player.getNameTag()?.deSpawn()
            } else {
                if(event.player.gameMode == GameMode.SPECTATOR) {
                    if(!event.player.isSneaking) {
                        event.player.getNameTag()?.spawn()
                    } else {
                        if(event.player.isFlying) {
                            event.player.getNameTag()?.spawn()
                        } else {
                            event.player.getNameTag()?.deSpawn()
                        }
                    }
                }
            }
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            val nameTag = event.player.getNameTag()
            nameTag?.deSpawn()
            active.remove(nameTag)
        }
    }

    class Timer: BukkitRunnable() {
        override fun run() {
            active.forEach { it.updatePosition() }
        }
    }
}