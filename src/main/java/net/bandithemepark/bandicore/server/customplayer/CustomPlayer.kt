package net.bandithemepark.bandicore.server.customplayer

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.entity.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.ItemUtils
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class CustomPlayer(val skin: CustomPlayerSkin) {
    var spawned = false
    var location: Location? = null

    // All armorstands/bodypieces
    var head: PacketEntityArmorStand = PacketEntityArmorStand()
    var tempHead: PacketEntityArmorStand = PacketEntityArmorStand()
    var leftArm: PacketEntityArmorStand = PacketEntityArmorStand()
    var rightArm: PacketEntityArmorStand = PacketEntityArmorStand()
    var body: PacketEntityArmorStand = PacketEntityArmorStand()
    var leftLeg: PacketEntityArmorStand = PacketEntityArmorStand()
    var rightLeg: PacketEntityArmorStand = PacketEntityArmorStand()

    // All rotations for the available bodypieces
    var headRotation = Quaternion()
    var leftArmRotation = Quaternion()
    var rightArmRotation = Quaternion()
    var leftLegRotation = Quaternion()
    var rightLegRotation = Quaternion()

    fun spawn(spawnLocation: Location) {
        this.location = spawnLocation.clone().add(0.0, 0.63, 0.0)
        this.location!!.yaw = 0.0f

        head.spawn(location!!.clone().add(headOffset))
        head.handle!!.isInvisible = true
        (head.handle!! as ArmorStand).isMarker = true
        head.itemInMainHand = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 8)
        head.setArmsVisible()
        head.setRightArmPose(0.0, 0.0, 0.0)

        tempHead.spawn(location!!.clone().add(0.0, -2.1-0.63, 0.0))
        tempHead.handle!!.isInvisible = true
        (tempHead.handle!! as ArmorStand).isMarker = true
        tempHead.helmet = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 8)
        tempHead.setHeadPose(0.0, 0.0, 0.0)

        rightArm.spawn(location!!.clone().add(rightArmOffset))
        rightArm.handle!!.isInvisible = true
        (rightArm.handle!! as ArmorStand).isMarker = true
        rightArm.itemInMainHand = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 4)
        rightArm.setArmsVisible()
        rightArm.setRightArmPose(0.0, 0.0, 0.0)

        leftArm.spawn(location!!.clone().add(leftArmOffset))
        leftArm.handle!!.isInvisible = true
        (leftArm.handle!! as ArmorStand).isMarker = true
        leftArm.itemInMainHand = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 3)
        leftArm.setArmsVisible()
        leftArm.setRightArmPose(0.0, 0.0, 0.0)

        body.spawn(location!!.clone().add(bodyOffset))
        body.handle!!.isInvisible = true
        (body.handle!! as ArmorStand).isMarker = true
        body.itemInMainHand = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 7)
        body.setArmsVisible()
        body.setRightArmPose(0.0, 0.0, 0.0)

        rightLeg.spawn(location!!.clone().add(rightLegOffset))
        rightLeg.handle!!.isInvisible = true
        (rightLeg.handle!! as ArmorStand).isMarker = true
        rightLeg.itemInMainHand = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 2)
        rightLeg.setArmsVisible()
        rightLeg.setRightArmPose(0.0, 0.0, 0.0)

        leftLeg.spawn(location!!.clone().add(leftLegOffset))
        leftLeg.handle!!.isInvisible = true
        (leftLeg.handle!! as ArmorStand).isMarker = true
        leftLeg.itemInMainHand = ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(skin.uuid), 1)
        leftLeg.setArmsVisible()
        leftLeg.setRightArmPose(0.0, 0.0, 0.0)

        //tempHead.deSpawn()
    }

    // Position stuff
    var completeRotation = Quaternion()

    fun updatePosition() {
        val headPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, headOffset.x, headOffset.y, headOffset.z))
        val newHeadRotation = completeRotation.clone()
        newHeadRotation.multiply(headRotation)
        val headPose = MathUtil.getArmorStandPose(newHeadRotation)
        head.teleport(Location(head.location!!.world, headPosition.x, headPosition.y, headPosition.z))
        head.setRightArmPose(Math.toDegrees(headPose.x), Math.toDegrees(headPose.y), Math.toDegrees(headPose.z))

        val leftArmPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftArmOffset.x, leftArmOffset.y, leftArmOffset.z))
        val newLeftArmRotation = completeRotation.clone()
        newLeftArmRotation.multiply(leftArmRotation)
        val leftArmPose = MathUtil.getArmorStandPose(newLeftArmRotation)
        leftArm.teleport(Location(body.location!!.world, leftArmPosition.x, leftArmPosition.y, leftArmPosition.z))
        leftArm.setRightArmPose(Math.toDegrees(leftArmPose.x), Math.toDegrees(leftArmPose.y), Math.toDegrees(leftArmPose.z))

        val rightArmPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightArmOffset.x, rightArmOffset.y, rightArmOffset.z))
        val newRightArmRotation = completeRotation.clone()
        newRightArmRotation.multiply(rightArmRotation)
        val rightArmPose = MathUtil.getArmorStandPose(newRightArmRotation)
        rightArm.teleport(Location(body.location!!.world, rightArmPosition.x, rightArmPosition.y, rightArmPosition.z))
        rightArm.setRightArmPose(Math.toDegrees(rightArmPose.x), Math.toDegrees(rightArmPose.y), Math.toDegrees(rightArmPose.z))

        val bodyPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, bodyOffset.x, bodyOffset.y, bodyOffset.z))
        val bodyPose = MathUtil.getArmorStandPose(completeRotation)
        body.teleport(Location(body.location!!.world, bodyPosition.x, bodyPosition.y, bodyPosition.z))
        body.setRightArmPose(Math.toDegrees(bodyPose.x), Math.toDegrees(bodyPose.y), Math.toDegrees(bodyPose.z))

        val leftLegPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftLegOffset.x, leftLegOffset.y, leftLegOffset.z))
        val newLeftLegRotation = completeRotation.clone()
        newLeftLegRotation.multiply(leftLegRotation)
        val leftLegPose = MathUtil.getArmorStandPose(newLeftLegRotation)
        leftLeg.teleport(Location(body.location!!.world, leftLegPosition.x, leftLegPosition.y, leftLegPosition.z))
        leftLeg.setRightArmPose(Math.toDegrees(leftLegPose.x), Math.toDegrees(leftLegPose.y), Math.toDegrees(leftLegPose.z))

        val rightLegPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightLegOffset.x, rightLegOffset.y, rightLegOffset.z))
        val newRightLegRotation = completeRotation.clone()
        newRightLegRotation.multiply(rightLegRotation)
        val rightLegPose = MathUtil.getArmorStandPose(newRightLegRotation)
        rightLeg.teleport(Location(body.location!!.world, rightLegPosition.x, rightLegPosition.y, rightLegPosition.z))
        rightLeg.setRightArmPose(Math.toDegrees(rightLegPose.x), Math.toDegrees(rightLegPose.y), Math.toDegrees(rightLegPose.z))
    }

    fun deSpawn() {
        location = null

        head.deSpawn()
        leftArm.deSpawn()
        rightArm.deSpawn()
        body.deSpawn()
        leftLeg.deSpawn()
        rightLeg.deSpawn()
    }

    fun setVisibilityType(type: PacketEntity.VisibilityType) {
        head.visibilityType = type
        tempHead.visibilityType = type
        leftArm.visibilityType = type
        rightArm.visibilityType = type
        body.visibilityType = type
        leftLeg.visibilityType = type
        rightLeg.visibilityType = type
    }

    fun setVisibilityList(list: MutableList<Player>) {
        head.visibilityList = list
        tempHead.visibilityList = list
        leftArm.visibilityList = list
        rightArm.visibilityList = list
        body.visibilityList = list
        leftLeg.visibilityList = list
        rightLeg.visibilityList = list
    }

    companion object {
        val headOffset = Vector(5.0/16.0, 0.5/16.0-0.63, 0.0)
        val rightArmOffset = Vector(0.025, -0.05-0.63, 0.0)
        val leftArmOffset = Vector(-0.025+12.0/16.0, -0.05-0.63, 0.0)
        val bodyOffset = Vector(5.0/16.0, -0.63-0.63, 0.0)
        val rightLegOffset = Vector(4.2/16.0, -0.63-0.63, 0.0)
        val leftLegOffset = Vector(-4.2/16.0+12.0/16.0, -0.63-0.63, 0.0)
    }

    class TestCommand: CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
            if(command.name.equals("customplayertest", true)) {
                if(sender is Player) {
                    val customPlayer = CustomPlayer(CustomPlayerSkin(sender.uniqueId, (sender as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value))
                    customPlayer.setVisibilityType(PacketEntity.VisibilityType.WHITELIST)
                    customPlayer.setVisibilityList(mutableListOf(sender))
                    customPlayer.spawn(sender.location)

                    var currentRotation = 0
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(BandiCore.instance, {
                        currentRotation++
                        //customPlayer.completeRotation = Quaternion.fromYawPitchRoll(currentRotation.toDouble(), currentRotation.toDouble(), currentRotation.toDouble())
                        customPlayer.headRotation = Quaternion.fromYawPitchRoll(currentRotation.toDouble(), currentRotation.toDouble(), currentRotation.toDouble())
                        customPlayer.leftArmRotation = Quaternion.fromYawPitchRoll(currentRotation.toDouble(), currentRotation.toDouble(), currentRotation.toDouble())
                        customPlayer.rightArmRotation = Quaternion.fromYawPitchRoll(currentRotation.toDouble(), currentRotation.toDouble(), currentRotation.toDouble())
                        customPlayer.leftLegRotation = Quaternion.fromYawPitchRoll(currentRotation.toDouble(), currentRotation.toDouble(), currentRotation.toDouble())
                        customPlayer.rightLegRotation = Quaternion.fromYawPitchRoll(currentRotation.toDouble(), currentRotation.toDouble(), currentRotation.toDouble())
                        customPlayer.updatePosition()
                    }, 0, 1)
                }
            }
            return false
        }
    }
}