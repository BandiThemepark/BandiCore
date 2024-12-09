package net.bandithemepark.bandicore.server.custom.player

import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.server.animatronics.AnimatronicNode
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class CustomPlayerRig(val skin: CustomPlayerSkin) {
    lateinit var animatronic: Animatronic
    private var spawned = false

    var visibilityType: PacketEntity.VisibilityType = PacketEntity.VisibilityType.BLACKLIST
    var visibilityList = mutableListOf<Player>()

    fun spawn(spawnLocation: Location, hiddenFor: Player?) {
        // Remove rotation here, because rotation is added later using transform
        spawnLocation.pitch = 0.0f
        spawnLocation.yaw = 0.0f

        // Spawn animatronic
        animatronic = Animatronic("player_rig")
        animatronic.visibilityType = visibilityType
        animatronic.visibilityList = visibilityList
        if(hiddenFor != null) animatronic.visibilityList = mutableListOf(hiddenFor)

        // Update spawn order to make sure textures are correct
        val newNodes = mutableListOf<AnimatronicNode>()
        newNodes.add(animatronic.nodes.find { it.name == "body" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "head" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "left_arm" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "right_arm" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "left_leg" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "right_leg" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "hat" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "handheld" }!!)
        animatronic.nodes = newNodes

        animatronic.spawn(spawnLocation, Quaternion.fromYawPitchRoll(0.0, 0.0, 0.0))

        updateDisplayEntities()
        playPose("default")
        spawned = true
    }

    fun playPose(poseName: String) {
        if(!spawned) return
        animatronic.playAnimation(poseName, true)
    }

    fun playAnimationLooped(animationName: String) {
        if(!spawned) return
        animatronic.playAnimation(animationName, true)
    }

    fun playAnimationOnce(animationName: String, onComplete: Runnable) {
        if(!spawned) return
        animatronic.playAnimation(animationName, false)
        animatronic.onComplete = onComplete
    }

    fun deSpawn() {
        animatronic.deSpawn()
        spawned = false
    }

    fun moveTo(position: Vector, rotation: Quaternion) {
        if(!spawned) return
        animatronic.baseRotation = rotation
        animatronic.setBasePosition(position)
    }

    private fun updateDisplayEntities() {
        animatronic.displayEntities.values.forEach {
            it.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND)
        }

        setItem("body", 7)
        setItem("head", 8)

        setItem("left_arm", if(skin.slim) 5 else 3)
        setItem("right_arm", if(skin.slim) 6 else 4)

        setItem("left_leg", 1)
        setItem("right_leg", 2)

        val hatDisplayEntity = getDisplayEntity("hat")
        hatDisplayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        animatronic.setScaleOverride("hat", 0.55)
        animatronic.setRotationOverride("hat", Vector(0.0, 180.0, 0.0))
        animatronic.setPositionOverride("hat", Vector(0.0, -0.40, 0.0))
        setItem("hat", null)

        val handheldDisplayEntity = getDisplayEntity("handheld")
        handheldDisplayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND)
        animatronic.setRotationOverride("handheld", Vector(90.0, 180.0, 0.0))
        setItem("handheld", null)
    }

    fun setHat(itemStack: ItemStack?) {
        setItem("hat", itemStack)
    }

    fun setHandheld(itemStack: ItemStack?) {
        setItem("handheld", itemStack)
    }

    private fun getDisplayEntity(nodeName: String): PacketItemDisplay {
        return animatronic.displayEntities[animatronic.nodes.find { it.name == nodeName }!!.uuid]!!
    }

    private fun setItem(nodeName: String, modelData: Int) {
        val displayEntity = getDisplayEntity(nodeName)
        displayEntity.setItemStack(ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(modelData).build())
        displayEntity.updateMetadata()
    }

    private fun setItem(nodeName: String, itemStack: ItemStack?) {
        val displayEntity = getDisplayEntity(nodeName)

        if(!displayEntity.spawned && itemStack != null) {
            displayEntity.spawn(animatronic.basePosition.toLocation(animatronic.world).clone())

            displayEntity.setItemStack(itemStack)
            displayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)

            if(nodeName == "handheld") {
                displayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND)
            }

            displayEntity.setInterpolationDuration(2)
            displayEntity.setInterpolationDelay(-1)

            displayEntity.updateMetadata()
        }

        if(itemStack == null && displayEntity.spawned) {
            displayEntity.deSpawn()
            return
        }

        displayEntity.setItemStack(itemStack)
        displayEntity.updateMetadata()
    }
}