package net.bandithemepark.bandicore.server.custom.player

import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.util.Vector

class CustomPlayerRig(val skin: CustomPlayerSkin) {
    lateinit var animatronic: Animatronic

    fun spawn(spawnLocation: Location) {
        spawnLocation.pitch = 0.0f
        spawnLocation.yaw = 0.0f

        animatronic = Animatronic("player_rig")
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
        animatronic.setBasePosition(position)
        animatronic.baseRotation = rotation
    }

    private fun updateDisplayEntities() {
        animatronic.displayEntities.values.forEach {
            it.setInterpolationDuration(10)
            it.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND)
        }

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