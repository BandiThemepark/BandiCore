package net.bandithemepark.bandicore.server.custom.player

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getCustomPlayerSkin
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.ItemUtils
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
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

    // Rotation points locations
    var headRotationPoint = Vector(5.0/16.0, 0.5/16.0-0.63, 0.0)
    var rightArmRotationPoint = Vector(0.025, -0.05-0.63, 0.0)
    var leftArmRotationPoint = Vector(-0.025+12.0/16.0, -0.05-0.63, 0.0)
    var bodyRotationPoint = Vector(5.0/16.0, -0.63-0.63, 0.0)
    var rightLegRotationPoint = Vector(4.2/16.0, -0.63-0.63, 0.0)
    var leftLegRotationPoint = Vector(-4.2/16.0+12.0/16.0, -0.63-0.63, 0.0)

    lateinit var marker: PacketEntityMarker
    init {
        loadFrom("default")
    }

    fun loadFrom(id: String) {
        val fm = FileManager()

        // Loading the head pose
        val headRotation = loadVector(fm, "customplayerposes/$id.yml", "head.rotation")
        this.headRotation = Quaternion.fromYawPitchRoll(headRotation.x, headRotation.y, headRotation.z)
        headRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "head.rotationPoint")

        // Loading the body pose
        val bodyRotation = loadVector(fm, "customplayerposes/$id.yml", "body.rotation")
        this.bodyRotation = Quaternion.fromYawPitchRoll(bodyRotation.x, bodyRotation.y, bodyRotation.z)
        bodyRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "body.rotationPoint")

        // Loading the left arm pose
        val leftArmRotation = loadVector(fm, "customplayerposes/$id.yml", "leftArm.rotation")
        this.leftArmRotation = Quaternion.fromYawPitchRoll(leftArmRotation.x, leftArmRotation.y, leftArmRotation.z)
        leftArmRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "leftArm.rotationPoint")

        // Loading the right arm pose
        val rightArmRotation = loadVector(fm, "customplayerposes/$id.yml", "rightArm.rotation")
        this.rightArmRotation = Quaternion.fromYawPitchRoll(rightArmRotation.x, rightArmRotation.y, rightArmRotation.z)
        rightArmRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "rightArm.rotationPoint")

        // Loading the left leg pose
        val leftLegRotation = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.rotation")
        this.leftLegRotation = Quaternion.fromYawPitchRoll(leftLegRotation.x, leftLegRotation.y, leftLegRotation.z)
        leftLegRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.rotationPoint")

        // Loading the right leg pose
        val rightLegRotation = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.rotation")
        this.rightLegRotation = Quaternion.fromYawPitchRoll(rightLegRotation.x, rightLegRotation.y, rightLegRotation.z)
        rightLegRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.rotationPoint")
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
        marker = PacketEntityMarker(spawnLocation.world)

        this.location = spawnLocation.clone().add(0.0, 0.63, 0.0)
        this.location!!.yaw = 0.0f

        head.spawn(location!!.clone().add(staticHeadOffset))
        head.handle!!.isInvisible = true
        (head.handle!! as ArmorStand).isMarker = true
        head.itemInMainHand = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(8).build()
        head.setArmsVisible()
        head.setRightArmPose(0.0, 0.0, 0.0)

        tempHead.spawn(location!!.clone().add(0.0, -2.1-0.63, 0.0))
        tempHead.handle!!.isInvisible = true
        (tempHead.handle!! as ArmorStand).isMarker = true
        (tempHead.handle!! as ArmorStand).isSmall = true
        tempHead.helmet = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(8).build()
        tempHead.setHeadPose(0.0, 0.0, 0.0)

        rightArm.spawn(location!!.clone().add(staticArmOffset))
        rightArm.handle!!.isInvisible = true
        (rightArm.handle!! as ArmorStand).isMarker = true
        val rightArmCustomModelData = if(skin.slim) 6 else 4
        rightArm.itemInMainHand = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(rightArmCustomModelData).build()
        rightArm.setArmsVisible()
        rightArm.setRightArmPose(0.0, 0.0, 0.0)

        leftArm.spawn(location!!.clone().add(staticArmOffset))
        leftArm.handle!!.isInvisible = true
        (leftArm.handle!! as ArmorStand).isMarker = true
        val leftArmCustomModelData = if(skin.slim) 5 else 3
        leftArm.itemInMainHand = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(leftArmCustomModelData).build()
        leftArm.setArmsVisible()
        leftArm.setRightArmPose(0.0, 0.0, 0.0)

        body.spawn(location!!.clone().add(staticBodyOffset))
        body.handle!!.isInvisible = true
        (body.handle!! as ArmorStand).isMarker = true
        body.itemInMainHand = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(7).build()
        body.setArmsVisible()
        body.setRightArmPose(0.0, 0.0, 0.0)

        rightLeg.spawn(location!!.clone().add(staticLegOffset))
        rightLeg.handle!!.isInvisible = true
        (rightLeg.handle!! as ArmorStand).isMarker = true
        rightLeg.itemInMainHand = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(2).build()
        rightLeg.setArmsVisible()
        rightLeg.setRightArmPose(0.0, 0.0, 0.0)

        leftLeg.spawn(location!!.clone().add(staticLegOffset))
        leftLeg.handle!!.isInvisible = true
        (leftLeg.handle!! as ArmorStand).isMarker = true
        leftLeg.itemInMainHand = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(1).build()
        leftLeg.setArmsVisible()
        leftLeg.setRightArmPose(0.0, 0.0, 0.0)

        //tempHead.deSpawn()
    }

    // Position stuff
    var completeRotation = Quaternion()

    val armRotationPointOffset = 0.063
    val staticHeadOffset = Vector(0.3125, -1.35, 0.0)
    val staticTempHeadOffset = Vector(0.0, -0.675, 0.0)
    val staticBodyOffset = Vector(0.3125, -1.31, 0.0)
    val staticArmOffset = Vector(0.38-armRotationPointOffset, -1.38, 0.0) // 0.38, -1.38, 0.0 // 0.43
    val staticLegOffset = Vector(0.3625-armRotationPointOffset, -1.26, 0.0) // 0.3625, -1.26, 0.0 // 0.4125

    val movingOffset = Vector(armRotationPointOffset, 0.0, 0.0)

    /**
     * Updates the position of the custom player and all of its limbs
     */
    fun updatePosition() {
        if(!head.spawned) return

        // Updating the head
        val headRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, headRotationPoint.x, headRotationPoint.y, headRotationPoint.z))
        val newHeadRotation = Quaternion.multiply(completeRotation.clone(), headRotation)

        val headPosition = headRotationPointPosition.clone().add(staticHeadOffset)
        head.teleport(Location(head.location!!.world, headPosition.x, headPosition.y, headPosition.z))

        val headPose = MathUtil.getArmorStandPose(newHeadRotation)
        head.setRightArmPose(Math.toDegrees(headPose.x), Math.toDegrees(headPose.y), Math.toDegrees(headPose.z))

        // Updating the body
        val bodyRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, bodyRotationPoint.x, bodyRotationPoint.y, bodyRotationPoint.z))
        val newBodyRotation = Quaternion.multiply(completeRotation.clone(), bodyRotation)

        val bodyPosition = bodyRotationPointPosition.clone().add(staticBodyOffset)
        body.teleport(Location(body.location!!.world, bodyPosition.x, bodyPosition.y, bodyPosition.z))

        val bodyPose = MathUtil.getArmorStandPose(newBodyRotation)
        body.setRightArmPose(Math.toDegrees(bodyPose.x), Math.toDegrees(bodyPose.y), Math.toDegrees(bodyPose.z))

        // Also moving the temp head to the same position as the body to hide it
        val tempHeadRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, bodyRotationPoint.x, bodyRotationPoint.y, bodyRotationPoint.z))

        val tempHeadPosition = tempHeadRotationPointPosition.clone().add(staticTempHeadOffset)
        tempHead.teleport(Location(body.location!!.world, tempHeadPosition.x, tempHeadPosition.y, tempHeadPosition.z-0.0))
        tempHead.setHeadPose(Math.toDegrees(bodyPose.x), Math.toDegrees(bodyPose.y), Math.toDegrees(bodyPose.z))

        // Updating the right arm
        val rightArmRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightArmRotationPoint.x, rightArmRotationPoint.y, rightArmRotationPoint.z))
        val newRightArmRotation = Quaternion.multiply(completeRotation.clone(), rightArmRotation)

        val rightArmPosition = rightArmRotationPointPosition.clone().add(staticArmOffset).add(MathUtil.rotateAroundPoint(newRightArmRotation, this.movingOffset.x, this.movingOffset.y, this.movingOffset.z))
        rightArm.teleport(Location(rightArm.location!!.world, rightArmPosition.x, rightArmPosition.y, rightArmPosition.z))

        val rightArmPose = MathUtil.getArmorStandPose(newRightArmRotation)
        rightArm.setRightArmPose(Math.toDegrees(rightArmPose.x), Math.toDegrees(rightArmPose.y), Math.toDegrees(rightArmPose.z))

        // Updating the left arm
        val leftArmRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftArmRotationPoint.x, leftArmRotationPoint.y, leftArmRotationPoint.z))
        val newLeftArmRotation = Quaternion.multiply(completeRotation.clone(), leftArmRotation)

        val leftArmPosition = leftArmRotationPointPosition.clone().add(staticArmOffset).add(MathUtil.rotateAroundPoint(newLeftArmRotation, this.movingOffset.x, this.movingOffset.y, this.movingOffset.z))
        leftArm.teleport(Location(leftArm.location!!.world, leftArmPosition.x, leftArmPosition.y, leftArmPosition.z))

        val leftArmPose = MathUtil.getArmorStandPose(newLeftArmRotation)
        leftArm.setRightArmPose(Math.toDegrees(leftArmPose.x), Math.toDegrees(leftArmPose.y), Math.toDegrees(leftArmPose.z))

        // Updating the right leg
        val rightLegRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, rightLegRotationPoint.x, rightLegRotationPoint.y, rightLegRotationPoint.z))
        val newRightLegRotation = Quaternion.multiply(completeRotation.clone(), rightLegRotation)

        val rightLegPosition = rightLegRotationPointPosition.clone().add(staticLegOffset).add(MathUtil.rotateAroundPoint(newRightLegRotation, this.movingOffset.x, this.movingOffset.y, this.movingOffset.z))
        rightLeg.teleport(Location(rightLeg.location!!.world, rightLegPosition.x, rightLegPosition.y, rightLegPosition.z))

        val rightLegPose = MathUtil.getArmorStandPose(newRightLegRotation)
        rightLeg.setRightArmPose(Math.toDegrees(rightLegPose.x), Math.toDegrees(rightLegPose.y), Math.toDegrees(rightLegPose.z))

        // Updating the left leg
        val leftLegRotationPointPosition = location!!.clone().add(MathUtil.rotateAroundPoint(completeRotation, leftLegRotationPoint.x, leftLegRotationPoint.y, leftLegRotationPoint.z))
        val newLeftLegRotation = Quaternion.multiply(completeRotation.clone(), leftLegRotation)

        val leftLegPosition = leftLegRotationPointPosition.clone().add(staticLegOffset).add(MathUtil.rotateAroundPoint(newLeftLegRotation, this.movingOffset.x, this.movingOffset.y, this.movingOffset.z))
        leftLeg.teleport(Location(leftLeg.location!!.world, leftLegPosition.x, leftLegPosition.y, leftLegPosition.z))

        val leftLegPose = MathUtil.getArmorStandPose(newLeftLegRotation)
        leftLeg.setRightArmPose(Math.toDegrees(leftLegPose.x), Math.toDegrees(leftLegPose.y), Math.toDegrees(leftLegPose.z))
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
                    val customPlayer = CustomPlayer(sender.getCustomPlayerSkin())
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