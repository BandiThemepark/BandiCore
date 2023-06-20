package net.bandithemepark.bandicore.server.custom.player.animation

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animation.Animation
import net.bandithemepark.bandicore.server.animation.Channel
import net.bandithemepark.bandicore.server.custom.player.CustomPlayer
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getCustomPlayerSkin
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CustomPlayerAnimation(val animation: Animation, val baseLocation: Location, val customPlayer: CustomPlayer, var loop: Boolean = false) {
    val manager = BandiCore.instance.server.customPlayerAnimationManager
    var currentTick = 0

    fun play() {
        manager.activeAnimations.add(this)
    }

    fun stop() {
        manager.activeAnimations.remove(this)
    }

    fun update() {
        currentTick++
        updateCustomPlayerPose()
    }

    fun updateCustomPlayerPose() {
        // Head
        customPlayer.dynamicHeadOffset = animation.getDataAt(currentTick, "head", Channel.POSITION).asVector()
        val headRotationVector = animation.getDataAt(currentTick, "head", Channel.ROTATION).asVector()
        customPlayer.headRotation = Quaternion.fromYawPitchRoll(headRotationVector.x, headRotationVector.y, headRotationVector.z)

        // Body
        customPlayer.dynamicBodyOffset = animation.getDataAt(currentTick, "body", Channel.POSITION).asVector()
        val bodyRotationVector = animation.getDataAt(currentTick, "body", Channel.ROTATION).asVector()
        customPlayer.bodyRotation = Quaternion.fromYawPitchRoll(bodyRotationVector.x, bodyRotationVector.y, bodyRotationVector.z)

        // Left Arm
        customPlayer.dynamicLeftArmOffset = animation.getDataAt(currentTick, "left_arm", Channel.POSITION).asVector()
        val leftArmRotationVector = animation.getDataAt(currentTick, "left_arm", Channel.ROTATION).asVector()
        customPlayer.leftArmRotation = Quaternion.fromYawPitchRoll(leftArmRotationVector.x, leftArmRotationVector.y, leftArmRotationVector.z)

        // Right Arm
        customPlayer.dynamicRightArmOffset = animation.getDataAt(currentTick, "right_arm", Channel.POSITION).asVector()
        val rightArmRotationVector = animation.getDataAt(currentTick, "right_arm", Channel.ROTATION).asVector()
        customPlayer.rightArmRotation = Quaternion.fromYawPitchRoll(rightArmRotationVector.x, rightArmRotationVector.y, rightArmRotationVector.z)

        // Left Leg
        customPlayer.dynamicLeftLegOffset = animation.getDataAt(currentTick, "left_leg", Channel.POSITION).asVector()
        val leftLegRotationVector = animation.getDataAt(currentTick, "left_leg", Channel.ROTATION).asVector()
        customPlayer.leftLegRotation = Quaternion.fromYawPitchRoll(leftLegRotationVector.x, leftLegRotationVector.y, leftLegRotationVector.z)

        // Right Leg
        customPlayer.dynamicRightLegOffset = animation.getDataAt(currentTick, "right_leg", Channel.POSITION).asVector()
        val rightLegRotationVector = animation.getDataAt(currentTick, "right_leg", Channel.ROTATION).asVector()
        customPlayer.rightLegRotation = Quaternion.fromYawPitchRoll(rightLegRotationVector.x, rightLegRotationVector.y, rightLegRotationVector.z)

        // Updating custom player position
        customPlayer.updatePosition()
    }
}