package net.bandithemepark.bandicore.server.custom.player

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animation.rig.Rig
import net.bandithemepark.bandicore.server.animation.rig.RigManager
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class NewCustomPlayer(skin: CustomPlayerSkin, var location: Location, rotation: Vector) {
    var rig: Rig

    init {
        rig = Rig.load("customplayer", Location(location.world, location.x, location.y, location.z), rotation)
        val allParts = rig.getAllParts()

        allParts.find { it.name == "head" }?.itemStack = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(8).build()

        val leftArmCustomModelData = if(skin.slim) 5 else 3
        val rightArmCustomModelData = if(skin.slim) 6 else 4
        allParts.find { it.name == "left_arm" }?.itemStack = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(leftArmCustomModelData).build()
        allParts.find { it.name == "right_arm" }?.itemStack = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(rightArmCustomModelData).build()

        allParts.find { it.name == "body" }?.itemStack = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(7).build()
        allParts.find { it.name == "left_leg" }?.itemStack = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(1).build()
        allParts.find { it.name == "right_leg" }?.itemStack = ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(2).build()

//        val partHead = RigPart(
//            "head",
//            mutableListOf(),
//            ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(8).build(),
//            Vector(0.0, 0.70, 0.0), Vector())
//        partHead.spawnOrder = 2
//        partHead.type = RigPart.Type.RIGHT_ARM
//
//        val leftArmCustomModelData = if(skin.slim) 5 else 3
//        val partLeftArm = RigPart(
//            "left_arm",
//            mutableListOf(),
//            ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(leftArmCustomModelData).build(),
//            Vector(0.35, 0.5825, 0.0), Vector())
//        partLeftArm.spawnOrder = 3
//        partLeftArm.type = RigPart.Type.LEFT_ARM
//
//        val rightArmCustomModelData = if(skin.slim) 6 else 4
//        val partRightArm = RigPart(
//            "right_arm",
//            mutableListOf(),
//            ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(rightArmCustomModelData).build(),
//            Vector(-0.35, 0.5825, 0.0), Vector())
//        partRightArm.spawnOrder = 4
//        partRightArm.type = RigPart.Type.RIGHT_ARM
//
//        val partBody = RigPart(
//            "body",
//            mutableListOf(partHead, partLeftArm, partRightArm),
//            ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(7).build(),
//            Vector(), Vector())
//        partBody.spawnOrder = 1
//        partBody.type = RigPart.Type.RIGHT_ARM
//
//        val partLeftLeg = RigPart(
//            "left_leg",
//            mutableListOf(),
//            ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(1).build(),
//            Vector(0.11, 0.0, 0.0), Vector())
//        partLeftLeg.spawnOrder = 5
//        partLeftLeg.type = RigPart.Type.LEFT_ARM
//
//        val partRightLeg = RigPart(
//            "right_leg",
//            mutableListOf(),
//            ItemFactory(Material.PLAYER_HEAD).setSkullTexture(skin.texture).setCustomModelData(2).build(),
//            Vector(-0.11, 0.0, 0.0), Vector())
//        partRightLeg.spawnOrder = 6
//        partRightLeg.type = RigPart.Type.RIGHT_ARM
//
//        val partRig = RigPart("rig", mutableListOf(partBody, partLeftLeg, partRightLeg), null, Vector(0.0, 11.0/16.0, 0.0), Vector(), false)
//        rig = Rig(listOf(partRig), Location(location.world, location.x, location.y, location.z), rotation)
    }

    fun spawn() {
        rig.spawn()
        rig.update()
    }

    fun deSpawn() {
        rig.deSpawn()
    }

    fun moveTo(position: Vector, rotation: Vector) {
        rig.offset = position.clone()
        rig.rotation = rotation.clone()
        rig.update()
    }

    fun loadFrom(id: String) {
        val animation = BandiCore.instance.server.rigManager.loadAnimation(id)
        rig.setToAnimationAt(animation, 0)
    }

    /**
     * Sets the visibility type of the custom player. It is recommended to do this before spawning
     * @param type The type to set it to
     */
    fun setVisibilityType(type: PacketEntity.VisibilityType) {
        rig.setVisibilityType(type)
    }

    /**
     * Sets the list the visibility type should be applied to. It is recommended to do this before spawning
     * @param list The list to set it to
     */
    fun setVisibilityList(list: MutableList<Player>) {
        rig.setVisibilityList(list)
    }
}