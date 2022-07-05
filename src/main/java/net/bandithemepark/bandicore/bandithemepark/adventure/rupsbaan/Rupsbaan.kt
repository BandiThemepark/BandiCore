package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan

import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.math.MathUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.sin

class Rupsbaan(val location: Location) {
    // Settings
    val amountOfCarts = 24
    val radius = 7.9
    val heightOffset = 1.0

    val carts = mutableListOf<RupsbaanCart>()
    var currentSpeed = 0.0
    var currentRotation = 0.0

    var eStop = false

    var harnessesLocked = true
        private set

    fun spawn() {
        for (i in 0 until amountOfCarts) {
            val cart = RupsbaanCart()
            carts.add(cart)
            cart.spawnAt(location)
        }
    }

    fun update() {
        currentRotation -= currentSpeed
        if(currentRotation > 180.0) currentRotation -= 360.0
        if(currentRotation < -180.0) currentRotation += 360.0

        val angleToNext = 360.0 / amountOfCarts
        for((index, value) in carts.withIndex()) {
            val rotationBack = currentRotation + (index * (360.0 / amountOfCarts)) - angleToNext/2.0
            val heightBack = sin(Math.toRadians(rotationBack * 2.0 + 90.0)) * heightOffset
            val positionBack = MathUtil.getPointOnCircleXZ(radius, rotationBack)
            positionBack.add(Vector(0.0, heightBack, 0.0))

            val rotationFront = currentRotation + (index * (360.0 / amountOfCarts)) + angleToNext/2.0
            val heightFront = sin(Math.toRadians(rotationFront * 2.0 + 90.0)) * heightOffset
            val positionFront = MathUtil.getPointOnCircleXZ(radius, rotationFront)
            positionFront.add(Vector(0.0, heightFront, 0.0))

            val difference = positionFront.clone().subtract(positionBack)

            val directionLocation = location.clone()
            directionLocation.direction = difference

            var yaw = directionLocation.yaw + 180.0
            if(yaw == 90.0) yaw = 90.1
            if(yaw == -90.0) yaw = -89.9
            if(yaw == 0.0) yaw = 0.1
            if(yaw == 180.0) yaw = 179.1

            var pitch = directionLocation.pitch.toDouble()
//            if(pitch == 0.0) pitch = 10.1
//            if(pitch == -0.0) pitch = 10.1
            // TODO Fix attachment children not always updating properly

            val average = positionBack.clone().add(positionFront).multiply(0.5).add(location.toVector())
            value.updateUpwardsInterpolation()
            value.updateDownwardsInterpolation()
            value.update(Location(Bukkit.getWorld("world"), average.x, average.y, average.z, yaw.toFloat(), -pitch.toFloat()))
        }
    }

    fun deSpawn() {
        for (cart in carts) {
            cart.deSpawn()
        }

        carts.clear()
    }

    fun setHarnesses(open: Boolean) {
        harnessesLocked = !open
        carts.forEach {
            it.updateSeatHarnesses()

            if(!open) {
                if ((it.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    it.harnessAttachment.type.markFor((it.seat1Attachment.type as SeatAttachment).seat!!.getPassengers()[0] as Player)
                }
                if ((it.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    it.harnessAttachment.type.markFor((it.seat2Attachment.type as SeatAttachment).seat!!.getPassengers()[0] as Player)
                }

                val rupsbaanRideOP = RideOP.get("rupsbaan") as RupsbaanRideOP
                if (rupsbaanRideOP.operator != null) it.harnessAttachment.type.markFor(rupsbaanRideOP.operator!!)
            }
        }

        if(open) {
            carts.forEach {
                it.startUpwardsInterpolation()

                if(it.harnessPosition != 0.0) {
                    if ((it.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                        it.harnessAttachment.type.unMarkFor((it.seat1Attachment.type as SeatAttachment).seat!!.getPassengers()[0] as Player)
                    }
                    if ((it.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                        it.harnessAttachment.type.unMarkFor((it.seat2Attachment.type as SeatAttachment).seat!!.getPassengers()[0] as Player)
                    }

                    val rupsbaanRideOP = RideOP.get("rupsbaan") as RupsbaanRideOP
                    if (rupsbaanRideOP.operator != null) it.harnessAttachment.type.unMarkFor(rupsbaanRideOP.operator!!)
                }
            }
        }
    }
}