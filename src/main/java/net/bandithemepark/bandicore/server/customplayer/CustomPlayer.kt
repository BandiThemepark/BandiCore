package net.bandithemepark.bandicore.server.customplayer

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.entity.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.ItemUtils
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
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
    var bodyRotation = Quaternion()
    var leftLegRotation = Quaternion()
    var rightLegRotation = Quaternion()

    // Limb offsets
    var headOffset = Vector(5.0/16.0, 0.5/16.0-0.63, 0.0)
    var rightArmOffset = Vector(0.025, -0.05-0.63, 0.0)
    var leftArmOffset = Vector(-0.025+12.0/16.0, -0.05-0.63, 0.0)
    var bodyOffset = Vector(5.0/16.0, -0.63-0.63, 0.0)
    var rightLegOffset = Vector(4.2/16.0, -0.63-0.63, 0.0)
    var leftLegOffset = Vector(-4.2/16.0+12.0/16.0, -0.63-0.63, 0.0)

    // Rotation points locations
    var headRotationPoint = Vector(5.0/16.0, 0.5/16.0-0.63, 0.0)
    var rightArmRotationPoint = Vector(0.025, -0.05-0.63, 0.0)
    var leftArmRotationPoint = Vector(-0.025+12.0/16.0, -0.05-0.63, 0.0)
    var bodyRotationPoint = Vector(5.0/16.0, -0.63-0.63, 0.0)
    var rightLegRotationPoint = Vector(4.2/16.0, -0.63-0.63, 0.0)
    var leftLegRotationPoint = Vector(-4.2/16.0+12.0/16.0, -0.63-0.63, 0.0)

    init {
        loadFrom("default")
    }

    fun loadFrom(id: String) {
        val fm = FileManager()

        // Loading the head pose
        val headRotation = loadVector(fm, "customplayerposes/$id.yml", "head.rotation")
        this.headRotation = Quaternion.fromYawPitchRoll(headRotation.x, headRotation.y, headRotation.z)
        headRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "head.rotationPoint")
        headOffset = loadVector(fm, "customplayerposes/$id.yml", "head.offset")

        // Loading the body pose
        val bodyRotation = loadVector(fm, "customplayerposes/$id.yml", "body.rotation")
        this.bodyRotation = Quaternion.fromYawPitchRoll(bodyRotation.x, bodyRotation.y, bodyRotation.z)
        bodyRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "body.rotationPoint")
        bodyOffset = loadVector(fm, "customplayerposes/$id.yml", "body.offset")

        // Loading the left arm pose
        val leftArmRotation = loadVector(fm, "customplayerposes/$id.yml", "leftArm.rotation")
        this.leftArmRotation = Quaternion.fromYawPitchRoll(leftArmRotation.x, leftArmRotation.y, leftArmRotation.z)
        leftArmRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "leftArm.rotationPoint")
        leftArmOffset = loadVector(fm, "customplayerposes/$id.yml", "leftArm.offset")

        // Loading the right arm pose
        val rightArmRotation = loadVector(fm, "customplayerposes/$id.yml", "rightArm.rotation")
        this.rightArmRotation = Quaternion.fromYawPitchRoll(rightArmRotation.x, rightArmRotation.y, rightArmRotation.z)
        rightArmRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "rightArm.rotationPoint")
        rightArmOffset = loadVector(fm, "customplayerposes/$id.yml", "rightArm.offset")

        // Loading the left leg pose
        val leftLegRotation = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.rotation")
        this.leftLegRotation = Quaternion.fromYawPitchRoll(leftLegRotation.x, leftLegRotation.y, leftLegRotation.z)
        leftLegRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.rotationPoint")
        leftLegOffset = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.offset")

        // Loading the right leg pose
        val rightLegRotation = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.rotation")
        this.rightLegRotation = Quaternion.fromYawPitchRoll(rightLegRotation.x, rightLegRotation.y, rightLegRotation.z)
        rightLegRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.rotationPoint")
        rightLegOffset = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.offset")
    }

    fun loadVector(fm: FileManager, file: String, path: String): Vector {
        val x = fm.getConfig(file).get().getDouble("$path.x")
        val y = fm.getConfig(file).get().getDouble("$path.y")
        val z = fm.getConfig(file).get().getDouble("$path.z")
        return Vector(x, y, z)
    }

    /**
     * Spawns the custom player model at the given location
     * @param spawnLocation The location to spawn the custom player at
     */
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

    /**
     * Updates the position of the custom player and all of its limbs
     */
    fun updatePosition() {
        // Updating the head
        val headRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, headRotationPoint.x, headRotationPoint.y, headRotationPoint.z))
        val newHeadRotation = completeRotation.clone()
        newHeadRotation.multiply(headRotation)

        //val headPosition = headRotationPointPosition.clone().add(MathUtil.rotateAroundPoint(headRotation, headOffset.x, headOffset.y, headOffset.z))
        val headPosition = headRotationPointPosition.clone().add(headOffset)
        head.teleport(Location(head.location!!.world, headPosition.x, headPosition.y, headPosition.z))

        val headPose = MathUtil.getArmorStandPose(newHeadRotation)
        head.setRightArmPose(Math.toDegrees(headPose.x), Math.toDegrees(headPose.y), Math.toDegrees(headPose.z))

        // Updating the left arm
        val leftArmRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftArmRotationPoint.x, leftArmRotationPoint.y, leftArmRotationPoint.z))
        val newLeftArmRotation = completeRotation.clone()
        newLeftArmRotation.multiply(leftArmRotation)

        //val leftArmPosition = leftArmRotationPointPosition.clone().add(MathUtil.rotateAroundPoint(leftArmRotation, leftArmOffset.x, leftArmOffset.y, leftArmOffset.z))
        val leftArmPosition = leftArmRotationPointPosition.clone().add(leftArmOffset)
        leftArm.teleport(Location(leftArm.location!!.world, leftArmPosition.x, leftArmPosition.y, leftArmPosition.z))

        val leftArmPose = MathUtil.getArmorStandPose(newLeftArmRotation)
        leftArm.setRightArmPose(Math.toDegrees(leftArmPose.x), Math.toDegrees(leftArmPose.y), Math.toDegrees(leftArmPose.z))

        // Updating the right arm
        val rightArmRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightArmRotationPoint.x, rightArmRotationPoint.y, rightArmRotationPoint.z))
        val newRightArmRotation = completeRotation.clone()
        newRightArmRotation.multiply(rightArmRotation)

        //val rightArmPosition = rightArmRotationPointPosition.clone().add(MathUtil.rotateAroundPoint(rightArmRotation, rightArmOffset.x, rightArmOffset.y, rightArmOffset.z))
        val rightArmPosition = rightArmRotationPointPosition.clone().add(rightArmOffset)
        rightArm.teleport(Location(rightArm.location!!.world, rightArmPosition.x, rightArmPosition.y, rightArmPosition.z))

        val rightArmPose = MathUtil.getArmorStandPose(newRightArmRotation)
        rightArm.setRightArmPose(Math.toDegrees(rightArmPose.x), Math.toDegrees(rightArmPose.y), Math.toDegrees(rightArmPose.z))

        // Updating the body
        val bodyRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, bodyRotationPoint.x, bodyRotationPoint.y, bodyRotationPoint.z))
        val newBodyRotation = completeRotation.clone()
        newBodyRotation.multiply(bodyRotation)

        //val bodyPosition = bodyRotationPointPosition.clone().add(MathUtil.rotateAroundPoint(bodyRotation, bodyOffset.x, bodyOffset.y, bodyOffset.z))
        val bodyPosition = bodyRotationPointPosition.clone().add(bodyOffset)
        body.teleport(Location(body.location!!.world, bodyPosition.x, bodyPosition.y, bodyPosition.z))

        val bodyPose = MathUtil.getArmorStandPose(newBodyRotation)
        body.setRightArmPose(Math.toDegrees(bodyPose.x), Math.toDegrees(bodyPose.y), Math.toDegrees(bodyPose.z))

        // Updating the left leg
        val leftLegRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftLegRotationPoint.x, leftLegRotationPoint.y, leftLegRotationPoint.z))
        val newLeftLegRotation = completeRotation.clone()
        newLeftLegRotation.multiply(leftLegRotation)

        //val leftLegPosition = leftLegRotationPointPosition.clone().add(MathUtil.rotateAroundPoint(leftLegRotation, leftLegOffset.x, leftLegOffset.y, leftLegOffset.z))
        val leftLegPosition = leftLegRotationPointPosition.clone().add(leftLegOffset)
        leftLeg.teleport(Location(leftLeg.location!!.world, leftLegPosition.x, leftLegPosition.y, leftLegPosition.z))

        val leftLegPose = MathUtil.getArmorStandPose(newLeftLegRotation)
        leftLeg.setRightArmPose(Math.toDegrees(leftLegPose.x), Math.toDegrees(leftLegPose.y), Math.toDegrees(leftLegPose.z))

        // Updating the right leg
        val rightLegRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightLegRotationPoint.x, rightLegRotationPoint.y, rightLegRotationPoint.z))
        val newRightLegRotation = completeRotation.clone()
        newRightLegRotation.multiply(rightLegRotation)

        //val rightLegPosition = rightLegRotationPointPosition.clone().add(MathUtil.rotateAroundPoint(rightLegRotation, rightLegOffset.x, rightLegOffset.y, rightLegOffset.z))
        val rightLegPosition = rightLegRotationPointPosition.clone().add(rightLegOffset)
        rightLeg.teleport(Location(rightLeg.location!!.world, rightLegPosition.x, rightLegPosition.y, rightLegPosition.z))

        val rightLegPose = MathUtil.getArmorStandPose(newRightLegRotation)
        rightLeg.setRightArmPose(Math.toDegrees(rightLegPose.x), Math.toDegrees(rightLegPose.y), Math.toDegrees(rightLegPose.z))
    }

    fun debugPositions(player: Player) {
//        // Updating the head
//        val headRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, headRotationPoint.x, headRotationPoint.y, headRotationPoint.z))
//        markerAt(headRotationPointPosition, player)
//
//        // Updating the left arm
//        val leftArmRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftArmRotationPoint.x, leftArmRotationPoint.y, leftArmRotationPoint.z))
//        markerAt(leftArmRotationPointPosition, player)
//
//        // Updating the right arm
//        val rightArmRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightArmRotationPoint.x, rightArmRotationPoint.y, rightArmRotationPoint.z))
//        markerAt(rightArmRotationPointPosition, player)
//
//        // Updating the body
//        val bodyRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, bodyRotationPoint.x, bodyRotationPoint.y, bodyRotationPoint.z))
//        markerAt(bodyRotationPointPosition, player)
//
//        // Updating the left leg
//        val leftLegRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftLegRotationPoint.x, leftLegRotationPoint.y, leftLegRotationPoint.z))
//        markerAt(leftLegRotationPointPosition, player)
//
//        // Updating the right leg
//        val rightLegRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightLegRotationPoint.x, rightLegRotationPoint.y, rightLegRotationPoint.z))
//        markerAt(rightLegRotationPointPosition, player)

        // Updating the main position
        markerAt(location!!, player)
    }

    fun markerAt(vector: Location, player: Player) {
        val marker = PacketEntityMarker(player.world)
        marker.addViewer(player)
        marker.moveEntity(vector.toVector())
    }

    /**
     * Despawns the custom player
     */
    fun deSpawn() {
        location = null

        head.deSpawn()
        leftArm.deSpawn()
        rightArm.deSpawn()
        body.deSpawn()
        leftLeg.deSpawn()
        rightLeg.deSpawn()

        tempHead.deSpawn()
    }

    /**
     * Sets the visibility type of the custom player. It is recommended to do this before spawning
     * @param type The type to set it to
     */
    fun setVisibilityType(type: PacketEntity.VisibilityType) {
        head.visibilityType = type
        tempHead.visibilityType = type
        leftArm.visibilityType = type
        rightArm.visibilityType = type
        body.visibilityType = type
        leftLeg.visibilityType = type
        rightLeg.visibilityType = type
    }

    /**
     * Sets the list the visibility type should be applied to. It is recommended to do this before spawning
     * @param list The list to set it to
     */
    fun setVisibilityList(list: MutableList<Player>) {
        head.visibilityList = list
        tempHead.visibilityList = list
        leftArm.visibilityList = list
        rightArm.visibilityList = list
        body.visibilityList = list
        leftLeg.visibilityList = list
        rightLeg.visibilityList = list
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