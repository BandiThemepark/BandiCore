package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.util.math.MathUtil
import org.bukkit.util.Vector
import kotlin.math.atan2

class BalloonPhysics(attachmentPoint: Vector, val onDespawn: () -> Unit) {
    var attachmentPoint: Vector? = attachmentPoint
    private var velocity: Vector = Vector(0, 0, 0)

    var position: Vector = Vector(attachmentPoint.x, attachmentPoint.y+SPAWN_HEIGHT, attachmentPoint.z)
        private set

    var rotation: Vector = Vector(0, 0, 0)
        private set

    fun tick() {
        if(attachmentPoint == null) {
            //updateFlyAwayPhysics()
        } else {
            updateAttachedPhysics()
        }
    }

    private fun updateAttachedPhysics() {
        if(velocity.y < MAX_UPWARDS_VELOCITY) {
            velocity.y += UPWARDS_ACCELERATION_PER_TICK
        }

        velocity = velocity.multiply(FRICTION_PER_TICK)
        position = position.add(velocity)

        val ropeLength = position.distance(attachmentPoint!!)
        if(ropeLength > MAX_ROPE_LENGTH) {
            // Update velocity to bounce back
            val direction = attachmentPoint!!.clone().subtract(position).normalize()
            velocity = direction.clone().multiply(velocity.length()).multiply(BOUNCE_FRICTION)

            // Move to the point where rope is at max length
            position = attachmentPoint!!.clone().subtract(direction.clone().multiply(MAX_ROPE_LENGTH))
        }

        updateRotation()
    }

    private var targetYaw = 0.0
    private var actualYaw = 0.0

    private var targetPitch = 0.0
    private var actualPitch = 0.0

    private fun updateRotation() {
        val horizontalVelocity = Vector(velocity.x, 0.0, velocity.z)
        val direction = horizontalVelocity.clone().normalize()

        targetYaw = Math.toDegrees(atan2(direction.z, direction.x))
        if(targetYaw.isNaN()) targetYaw = 0.0

        actualYaw += (targetYaw - actualYaw) * YAW_ROTATION_SPEED

        // Interpolate pitch based on velocity
        targetPitch = MathUtil.lerp(0.0, 90.0, horizontalVelocity.length() / 0.05)
        actualPitch += (targetPitch - actualPitch) * PITCH_ROTATION_SPEED

        rotation = Vector(actualPitch, actualYaw+90, 0.0)
    }

    companion object {
        const val SPAWN_HEIGHT = 1.5
        const val UPWARDS_ACCELERATION_PER_TICK = 0.02
        const val MAX_UPWARDS_VELOCITY = 0.2
        const val FRICTION_PER_TICK = 0.9 // Lower means less friction
        const val BOUNCE_FRICTION = 0.6 // Lower means less bounce
        const val MAX_ROPE_LENGTH = 3.0
        const val YAW_ROTATION_SPEED = 0.05
        const val PITCH_ROTATION_SPEED = 0.1
    }
}