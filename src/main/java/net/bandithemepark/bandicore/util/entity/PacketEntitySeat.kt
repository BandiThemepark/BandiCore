package net.bandithemepark.bandicore.util.entity

import me.m56738.smoothcoasters.api.RotationMode
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.afk.AfkManager.Companion.setAfkProtection
import net.bandithemepark.bandicore.util.entity.event.PacketEntityDismountEvent
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInteractEvent
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector

class PacketEntitySeat: PacketEntity() {
    var harnessesOpen = true
    var exitingLocation: Location? = null

    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): LivingEntity {
        return ArmorStand(world, x, y, z)
    }

    fun moveEntity(position: Vector, rotation: Quaternion, rotationDegrees: Vector) {
        val angles = rotation.getYawPitchRoll()
        super.moveEntity(position.x, position.y, position.z, rotationDegrees.y.toFloat(), rotationDegrees.y.toFloat())

        for(passenger in getPassengers().filter { it is Player }) {
            BandiCore.instance.smoothCoastersAPI.setRotation(null, passenger as Player, rotation.x.toFloat(), rotation.y.toFloat(), rotation.z.toFloat(), rotation.w.toFloat(), 3.toByte())
        }
    }

    override fun spawn(location: Location) {
        super.spawn(location)
        seats.add(this)
    }

    override fun deSpawn() {
        super.deSpawn()
        seats.remove(this)
    }

    /**
     * Sits the given player on this seat
     * @param player The player to sit
     * @return Whether the player was successfully seated
     */
    fun sit(player: Player): Boolean {
        // TODO Using attraction system, check if a player can actually sit on it
        if(!harnessesOpen) return false
        if(getPassengers().isNotEmpty()) return false
        if(isRiding(player)) return false
        // TODO Check if player isn't operating this attraction

        addPassenger(player)
        updatePassengers()
        player.setAfkProtection(true)
        BandiCore.instance.smoothCoastersAPI.setRotationMode(null, player, RotationMode.PLAYER)

        return true
    }

    class Events: Listener {
        @EventHandler
        fun onInteract(event: PacketEntityInteractEvent) {
            if(event.clicked is PacketEntitySeat) {
                val enterEvent = SeatEnterEvent(event.clicked as PacketEntitySeat, event.player)
                Bukkit.getPluginManager().callEvent(enterEvent)

                if(!enterEvent.isCancelled) {
                    event.isCancelled = true
                    (event.clicked as PacketEntitySeat).sit(event.player)
                }
            }
        }

        @EventHandler
        fun onDismount(event: PacketEntityDismountEvent) {
            if(event.dismounted is PacketEntitySeat) {
                val exitEvent = SeatExitEvent(event.dismounted as PacketEntitySeat, event.player)
                Bukkit.getPluginManager().callEvent(exitEvent)

                if(!exitEvent.isCancelled) {
                    if(!(event.dismounted as PacketEntitySeat).harnessesOpen) {
                        event.isCancelled = true
                        event.player.setAfkProtection(false)
                        event.dismounted.ejectPassenger(event.player)
                        //event.dismounted.ejectPassengerAt(event.player, ) // TODO Retrieve exiting location
                    }
                } else {
                    event.isCancelled = true
                }
            }
        }
    }

    companion object {
        val seats = mutableListOf<PacketEntitySeat>()

        fun getSeat(entity: Entity): PacketEntitySeat? {
            return seats.find { it.getPassengers().contains(entity) }
        }

        fun isRiding(entity: Entity): Boolean {
            return seats.any { it.getPassengers().contains(entity) }
        }
    }
}