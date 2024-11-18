package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.util.math.MathUtil
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.sin

class BalloonPhysics(attachmentPoint: Vector, val onDespawn: () -> Unit) {
    var attachmentPoint: Vector? = attachmentPoint
    private var velocity: Vector = Vector(0, 0, 0)

    var position: Vector = Vector(attachmentPoint.x, attachmentPoint.y+SPAWN_HEIGHT, attachmentPoint.z)
        private set

    private var internalPosition = Vector(attachmentPoint.x, attachmentPoint.y+SPAWN_HEIGHT, attachmentPoint.z)

    var rotation: Vector = Vector(0, 0, 0)
        private set

    fun resetVelocity() {
        velocity = Vector(0, 0, 0)
        if(attachmentPoint != null) internalPosition = Vector(attachmentPoint!!.x, attachmentPoint!!.y+SPAWN_HEIGHT, attachmentPoint!!.z)
    }

    fun tick() {
        if(velocity.y < MAX_UPWARDS_VELOCITY) {
            velocity.y += UPWARDS_ACCELERATION_PER_TICK
        }

        velocity = velocity.multiply(FRICTION_PER_TICK)
        internalPosition = internalPosition.add(velocity)

        if(attachmentPoint == null) {
            updateFlyAwayPhysics()
        } else {
            updateAttachedPhysics()
            flyAwayTicks = 0
        }

        position = internalPosition.clone().add(windPositionOffset)
    }

    private var flyAwayTicks = 0
    private fun updateFlyAwayPhysics() {
        flyAwayTicks++

        if(velocity.y > MAX_UPWARDS_WIND_VELOCITY) {
            velocity.y = MAX_UPWARDS_WIND_VELOCITY
        }

        updateRotation()

        if(flyAwayTicks > DESPAWN_TIME_TICKS) {
            onDespawn()
        }
    }

    private fun updateAttachedPhysics() {
        val ropeLength = internalPosition.distance(attachmentPoint!!)
        if(ropeLength > MAX_ROPE_LENGTH) {
            // Update velocity to bounce back
            val direction = attachmentPoint!!.clone().subtract(internalPosition).normalize()
            velocity = direction.clone().multiply(velocity.length()).multiply(BOUNCE_FRICTION)

            // Move to the point where rope is at max length
            internalPosition = attachmentPoint!!.clone().subtract(direction.clone().multiply(MAX_ROPE_LENGTH))
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

        updateWindSimulation()
        rotation = Vector(-actualPitch, actualYaw-90, 0.0).add(windRotationOffset)

    }

    private var idleTime = 0
    private var windTime = 0
    private var windRotationOffset = Vector()
    private var windPositionOffset = Vector()

    private fun updateWindSimulation() {
        windTime++

        idleTime++
        if(idleTime > FULL_WIND_EFFECT_TIME) idleTime = FULL_WIND_EFFECT_TIME

        val horizontalDistance = Vector(velocity.x, 0.0, velocity.z).length()
        if(horizontalDistance > IDLE_RESET_DISTANCE) {
            idleTime = 0
        }

        val pitchOffset = sin(windTime.toDouble() / WIND_TILT_TIME_PITCH * Math.PI * 2) * WIND_TILT_STRENGTH
        val yawOffset = sin(windTime.toDouble() / WIND_TILT_TIME_YAW * Math.PI * 2) * WIND_TILT_STRENGTH

        windRotationOffset = Vector(pitchOffset, yawOffset, 0.0)

        // Dampen the effect when idleTime is at 0, with full effect at 20
        val idleDampening = idleTime.toDouble() / FULL_WIND_EFFECT_TIME
        windRotationOffset = windRotationOffset.multiply(idleDampening)

        val windYOffset = sin(windTime.toDouble() / WIND_Y_TIME * Math.PI * 2) * WIND_Y_STRENGTH
        windPositionOffset = Vector(0.0, windYOffset, 0.0)
        windPositionOffset = windPositionOffset.multiply(idleDampening)
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
        const val IDLE_RESET_DISTANCE = 0.005
        const val WIND_TILT_STRENGTH = 5.0
        const val FULL_WIND_EFFECT_TIME = 20
        const val WIND_TILT_TIME_PITCH = 73
        const val WIND_TILT_TIME_YAW = 80
        const val WIND_Y_STRENGTH = 0.1
        const val WIND_Y_TIME = 90
        const val DESPAWN_TIME_TICKS = 200
        const val MAX_UPWARDS_WIND_VELOCITY = 0.1
    }
}