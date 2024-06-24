package net.bandithemepark.bandicore.server.essentials.ranks.nametag

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.GameMode
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class PlayerNameTag(val player: Player) {
    val parentArmorStand = PacketEntityArmorStand()
    val titleParentArmorStand = PacketEntityArmorStand()
    val textDisplay = PacketTextDisplay()
    val titleDisplay = PacketTextDisplay()
    var heightOffset = 0.0
    private var isVisible = false

    var title: Component? = null
        set(value) {
            field = value

            if(value == null) {
                if(titleDisplay.spawned) titleDisplay.deSpawn()
                if(titleParentArmorStand.spawned) titleParentArmorStand.deSpawn()
            } else {
                if(!titleDisplay.spawned) {
                    spawnTitleDisplay()
                } else {
                    titleDisplay.setText(value)
                    titleDisplay.updateMetadata()
                }
            }
        }

    var hidden = false
    set(value) {
        field = value
        if(value) {
            if(isVisible) {
                deSpawn()
                isVisible = true
            }
        } else {
            if(isVisible) spawn()
        }
    }

    private fun spawnTitleDisplay() {
        if(title == null) return
        if(hidden) return
        if(titleDisplay.spawned) return

        titleParentArmorStand.spawn(player.location.clone().add(0.0, 2.0 + heightOffset + TITLE_HEIGHT_OFFSET, 0.0))
        titleParentArmorStand.handle.isInvisible = true
        (titleParentArmorStand.handle as ArmorStand).isMarker = true
        titleParentArmorStand.handle.isNoGravity = true
        titleParentArmorStand.updateMetadata()

        titleDisplay.visibilityType = PacketEntity.VisibilityType.BLACKLIST
        titleDisplay.visibilityList = mutableListOf(player)

        titleDisplay.spawn(player.location.clone().add(0.0, 2.0 + heightOffset + TITLE_HEIGHT_OFFSET, 0.0))
        titleDisplay.setBillboard(Display.Billboard.CENTER)
        titleDisplay.setDefaultBackground(true)
        titleDisplay.setSeeThrough(false)
        titleDisplay.setAlignment(TextDisplay.TextAlignment.CENTER)
        titleDisplay.setText(title!!)
        titleDisplay.updateMetadata()

        titleParentArmorStand.addPassenger(titleDisplay.handle.id)
        titleParentArmorStand.updatePassengers()
    }

    /**
     * Spawns the name tag. Automatically called on initialization.
     */
    fun spawn() {
        isVisible = true

        if(!textDisplay.spawned && !hidden) {
            parentArmorStand.spawn(player.location.clone().add(0.0, 2.0 + heightOffset, 0.0))
            parentArmorStand.handle.isInvisible = true
            (parentArmorStand.handle as ArmorStand).isMarker = true
            parentArmorStand.handle.isNoGravity = true
            parentArmorStand.updateMetadata()

            textDisplay.visibilityType = PacketEntity.VisibilityType.BLACKLIST
            textDisplay.visibilityList = mutableListOf(player)

            textDisplay.spawn(player.location.clone().add(0.0, 2.0 + heightOffset, 0.0))

            val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
            val text = Util.color("${rank.name} <${rank.color}>${player.name}")
            textDisplay.setText(text)

            textDisplay.setBillboard(Display.Billboard.CENTER)
            textDisplay.setDefaultBackground(true)
            textDisplay.setSeeThrough(false)
            textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER)

            textDisplay.updateMetadata()

            parentArmorStand.addPassenger(textDisplay.handle.id)
            parentArmorStand.updatePassengers()

            spawnTitleDisplay()
        }
    }

    /**
     * De-spawns the name tag, automatically called on player quit
     */
    fun deSpawn() {
        isVisible = false
        textDisplay.deSpawn()
        parentArmorStand.deSpawn()
        if(titleDisplay.spawned) titleDisplay.deSpawn()
        if(titleParentArmorStand.spawned) titleParentArmorStand.deSpawn()
    }

    /**
     * Updates the name tag's displayname. Use this when you change the player's rank, or something like that.
     */
    fun updateName() {
        val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
        val text = Util.color("${rank.name} <${rank.color}>${player.name}")
        textDisplay.setText(text)
        textDisplay.updateMetadata()
    }

    /**
     * Updates the position of the name tag. Doesn't need to be called really.
     */
    fun updatePosition() {
        val position = player.location.toVector().add(Vector(0.0, 2.0+heightOffset, 0.0))
        parentArmorStand.moveEntity(position.x, position.y, position.z)

        if(titleParentArmorStand.spawned) {
            val titlePosition = player.location.toVector().add(Vector(0.0, 2.0 + heightOffset + TITLE_HEIGHT_OFFSET, 0.0))
            titleParentArmorStand.moveEntity(titlePosition.x, titlePosition.y, titlePosition.z)
        }
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

        const val TITLE_HEIGHT_OFFSET = 0.35
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

        @EventHandler
        fun onSeatEnter(event: SeatEnterEvent) {
            event.player.getNameTag()?.hidden = true
        }

        @EventHandler
        fun onSeatExit(event: SeatExitEvent) {
            event.player.getNameTag()?.hidden = false
        }
    }

    class Timer: BukkitRunnable() {
        override fun run() {
            active.forEach { it.updatePosition() }
        }
    }
}