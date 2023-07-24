package net.bandithemepark.bandicore.server.custom.player

import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.server.animatronics.AnimatronicNode
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class CustomPlayerRig(val skin: CustomPlayerSkin) {
    lateinit var animatronic: Animatronic
    lateinit var parentArmorStand: PacketEntityArmorStand

    fun spawn(spawnLocation: Location, hiddenFor: Player) {
        // Remove rotation here, because rotation is added later using transform
        spawnLocation.pitch = 0.0f
        spawnLocation.yaw = 0.0f

        // Spawn parent armorstand for smoothness
        parentArmorStand = PacketEntityArmorStand()
        parentArmorStand.visibilityType = PacketEntity.VisibilityType.BLACKLIST
        parentArmorStand.visibilityList = mutableListOf(hiddenFor)
        parentArmorStand.spawn(spawnLocation)
        parentArmorStand.handle!!.isInvisible = true
        parentArmorStand.handle!!.isNoGravity = true
        (parentArmorStand.handle!! as ArmorStand).isMarker = true
        parentArmorStand.updateMetadata()

        // Spawn animatronic
        animatronic = Animatronic("player_rig")
        animatronic.visibilityType = PacketEntity.VisibilityType.BLACKLIST
        animatronic.visibilityList = mutableListOf(hiddenFor)

        // Update spawn order to make sure textures are correct
        val newNodes = mutableListOf<AnimatronicNode>()
        newNodes.add(animatronic.nodes.find { it.name == "body" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "head" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "left_arm" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "right_arm" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "left_leg" }!!)
        newNodes.add(animatronic.nodes.find { it.name == "right_leg" }!!)
        animatronic.nodes = newNodes

        animatronic.spawn(spawnLocation, Quaternion.fromYawPitchRoll(0.0, 0.0, 0.0))

        updateDisplayEntities()
        playPose("default")
    }

    fun playPose(poseName: String) {
        animatronic.playAnimation(poseName, true)
    }

    fun deSpawn() {
        animatronic.deSpawn()
    }

    fun moveTo(position: Vector, rotation: Quaternion) {
        parentArmorStand.moveEntity(position.x, position.y, position.z)
        animatronic.baseRotation = rotation
    }

    private fun updateDisplayEntities() {
        animatronic.displayEntities.values.forEach {
            it.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND)
            parentArmorStand.addPassenger(it.handle!!.id)
        }
        parentArmorStand.updatePassengers()

        setItem("body", 7)
        setItem("head", 8)

        setItem("left_arm", if(skin.slim) 5 else 3)
        setItem("right_arm", if(skin.slim) 6 else 4)

        setItem("left_leg", 1)
        setItem("right_leg", 2)
    }

    private fun getDisplayEntity(nodeName: String): PacketItemDisplay {
        return animatronic.displayEntities[animatronic.nodes.find { it.name == nodeName }!!.uuid]!!
    }

    private fun setItem(nodeName: String, modelData: Int) {
        val displayEntity = getDisplayEntity(nodeName)
        displayEntity.setItemStack(ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(modelData).build())
        displayEntity.updateMetadata()
    }
}