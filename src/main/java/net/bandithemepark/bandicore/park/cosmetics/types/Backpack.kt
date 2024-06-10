package net.bandithemepark.bandicore.park.cosmetics.types

import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import org.joml.Matrix4f
import kotlin.math.atan2

class Backpack(val player: Player) {
    private var isVisible = false
    val display = PacketItemDisplay()

    var model: ItemStack? = null
        set(value) {
            field = value

            if(value == null) {
                if(display.spawned) display.deSpawn()
            } else {
                if(!display.spawned) {
                    spawn()
                }

                display.setItemStack(value)
                display.updateMetadata()
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

    fun spawn() {
        if(display.spawned) return
        if(model == null) return

        isVisible = true

        display.spawn(player.location.clone().add(OFFSET))
        display.setItemStack(model!!)
        display.setInterpolationDelay(-1)
        display.setInterpolationDuration(2)
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        display.updateMetadata()
    }

    fun deSpawn() {
        if(!display.spawned) return
        display.deSpawn()
        isVisible = false
    }

    var crouching = false
    var lastPosition = player.location.toVector()
    fun updatePosition() {
        if(!display.spawned) return

        val position = player.location.toVector().add(OFFSET)
        display.moveEntity(position.x, position.y, position.z)

        val deltaPosition = player.location.toVector().subtract(lastPosition)
        var playerLookYaw = (player.yaw.toDouble() - 90.0) % 360.0
        if(playerLookYaw < 0) playerLookYaw += 360.0

//        var deltaYaw = (Math.toDegrees(atan2(deltaPosition.z, deltaPosition.x)))
//
//        val bodyRotation = if(deltaPosition.isZero) {
//            player.bodyYaw.toDouble()
//        } else {
//            deltaYaw -= 90.0
//            if(deltaYaw < 0) deltaYaw += 360.0
//            Bukkit.broadcast(Component.text("PlayerLookYaw: $playerLookYaw, DeltaYaw: $deltaYaw"))
//
//            if(playerLookYaw - 10 < deltaYaw && deltaYaw < playerLookYaw + 10) {
//                deltaYaw
//            } else if(playerLookYaw < deltaYaw) {
//                deltaYaw - 45
//            } else {
//                deltaYaw + 45
//            }
//        }
        val bodyRotation = player.bodyYaw.toDouble()

        val matrix = Matrix4f()
        matrix.scale(SCALE.toFloat())
        matrix.rotate(Quaternion.fromYawPitchRoll(0.0, bodyRotation, 0.0).toBukkitQuaternion())
        display.setTransformationMatrix(matrix)
        display.setInterpolationDuration(2)
        display.updateMetadata()

        lastPosition = player.location.toVector()
    }

    companion object {
        val OFFSET = Vector(0.0, 1.65, 0.0)
        const val SCALE = 1.0 / 1.6
        val active = mutableListOf<Backpack>()

        fun get(player: Player): Backpack? {
            return active.find { it.player == player }
        }

        fun Player.getBackpack(): Backpack? {
            return get(this)
        }
    }

    class Events: Listener {
        @EventHandler
        fun onSneak(event: PlayerToggleSneakEvent) {
            if(event.player.gameMode == GameMode.SPECTATOR) return
            val backpack = event.player.getBackpack()
            if(event.isSneaking) {
                if(!event.player.isFlying) {
                    backpack?.crouching = true
                }
            } else {
                backpack?.crouching = false
            }
        }

        @EventHandler
        fun onFlyingLand(event: PlayerToggleFlightEvent) {
            if(event.player.gameMode == GameMode.SPECTATOR) return
            if(!event.isFlying) {
                if(event.player.isSneaking) {
                    event.player.getBackpack()?.crouching = true
                }
            }
        }

        @EventHandler
        fun onGameModeSwitch(event: PlayerGameModeChangeEvent) {
            if(event.newGameMode == GameMode.SPECTATOR) {
                event.player.getBackpack()?.deSpawn()
            } else {
                if(event.player.gameMode == GameMode.SPECTATOR) {
                    if(!event.player.isSneaking) {
                        event.player.getBackpack()?.crouching = false
                    } else {
                        event.player.getBackpack()?.crouching = !event.player.isFlying
                    }
                }
            }
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            val backpack = event.player.getBackpack()
            backpack?.deSpawn()
            active.remove(backpack)
        }

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            val backpack = Backpack(event.player)
            if(event.player.gameMode == GameMode.SPECTATOR) return

            backpack.spawn()
        }

        @EventHandler
        fun onSeatEnter(event: SeatEnterEvent) {
            event.player.getBackpack()?.hidden = true
        }

        @EventHandler
        fun onSeatExit(event: SeatExitEvent) {
            event.player.getBackpack()?.hidden = false
        }
    }

    class Timer: BukkitRunnable() {
        override fun run() {
            active.forEach { it.updatePosition() }
        }
    }
}