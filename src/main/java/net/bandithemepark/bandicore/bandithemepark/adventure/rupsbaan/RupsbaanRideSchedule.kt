package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.math.MathUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class RupsbaanRideSchedule(val ride: Rupsbaan, val keyframes: HashMap<Int, Double>) {
    val endTime = keyframes.keys.maxOf { it }

    fun getSpeedAt(ticks: Int): Double {
         // find the biggest key in keyframes that is smaller then ticks
        var biggestKey = 0
        for (key in keyframes.keys) {
            if(key in (biggestKey + 1)..ticks) {
                biggestKey = key
            }
        }

        // find the smallest key that is bigger than ticks
        var smallestKey = Int.MAX_VALUE
        for (key in keyframes.keys) {
            if(key in (ticks + 1) until smallestKey) {
                smallestKey = key
            }
        }

        val progress = (ticks - smallestKey).toDouble() / (biggestKey - smallestKey).toDouble()
        return MathUtil.lerp(keyframes.getOrDefault(smallestKey, 0.0), keyframes.getOrDefault(biggestKey, 0.0), progress)
    }

    fun update() {
        if(!active) return

        if(currentTicks > endTime) {
            active = false
            ride.currentSpeed = 0.0

            if((RideOP.get("rupsbaan") as RupsbaanRideOP).operator == null) {
                ride.carts.forEach { it.startUpwardsInterpolation() }
            }

            for(cart in ride.carts) {
                if((cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    val passengers = (cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
                    (cart.seat1Attachment.type as SeatAttachment).seat!!.ejectPassengers()

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        passengers.forEach { player ->
                            (cart.seat1Attachment.type as SeatAttachment).deSpawnCustomPlayer(player)
                            SeatAttachment.show(player)
                            player.getNameTag()?.hidden = false
                        }
                    })
                }

                if((cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    val passengers = (cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
                    (cart.seat2Attachment.type as SeatAttachment).seat!!.ejectPassengers()

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        passengers.forEach { player ->
                            (cart.seat2Attachment.type as SeatAttachment).deSpawnCustomPlayer(player)
                            SeatAttachment.show(player)
                            player.getNameTag()?.hidden = false
                        }
                    })
                }
            }
            return
        }

        if(ride.eStop) {
            ride.currentSpeed -= 0.1

            if(ride.currentSpeed <= 0.0) {
                ride.currentSpeed = 0.0
                active = false
            }

            return
        }

        currentTicks++
        ride.currentSpeed = getSpeedAt(currentTicks)
    }

    var active = false
    var currentTicks = 0
    fun start() {
        active = true
        currentTicks = 0
    }
}