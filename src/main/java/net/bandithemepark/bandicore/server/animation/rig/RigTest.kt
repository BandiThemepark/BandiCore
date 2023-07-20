package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.custom.player.NewCustomPlayer
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftItemDisplay
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.sin

class RigTest: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("rigtest", true)) return false
        if(sender !is Player) return false

        val itemDisplay = PacketItemDisplay()
        itemDisplay.spawn(sender.location)
        itemDisplay.setItemStack(ItemFactory(Material.DIAMOND_HOE).setCustomModelData(6).build())
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)

        itemDisplay.moveEntity(sender.location.x, sender.location.y + 2.0f, sender.location.z)
        itemDisplay.setInterpolationDuration(2)

        val matrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(Quaternion.fromYawPitchRoll(0.0, 0.0, 0.0).toBukkitQuaternion())
        itemDisplay.setTransformationMatrix(matrix)

        itemDisplay.updateMetadata()

        var rotation = 0.0
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            itemDisplay.setInterpolationDelay(-1)
            rotation += 2.0
            //val newMatrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(Quaternion.fromYawPitchRoll(rotation, rotation, rotation).toBukkitQuaternion())
            val newMatrix = Matrix4f().translation(0.0f, sin(Math.toRadians(rotation)).toFloat() * 5.0f, 0.0f)
            itemDisplay.setTransformationMatrix(newMatrix)
            itemDisplay.updateMetadata()
        }, 0, 1)


//        val displayEntity = sender.world.spawnEntity(sender.location, EntityType.ITEM_DISPLAY) as CraftItemDisplay
//        displayEntity.itemStack = ItemFactory(Material.DIAMOND_HOE).setCustomModelData(6).build()
//        displayEntity.itemDisplayTransform = ItemDisplay.ItemDisplayTransform.HEAD
//
//        val location = sender.location.add(Vector(0.0f, 2.0f, 0.0f))
//        location.pitch = 0.0f
//        location.yaw = 0.0f
//        displayEntity.teleport(location)
//        val matrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(Quaternion.fromYawPitchRoll(0.0, 0.0, 0.0).toBukkitQuaternion())
//        //val matrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(0.0f, 0.0f, 0.0f, 0.0f)
//        displayEntity.setTransformationMatrix(matrix)
//
//        displayEntity.interpolationDuration = 2
//
//        var rotation = 0.0
//        Bukkit.getScheduler().runTaskTimer(BandiCore.instance, Runnable {
//            displayEntity.interpolationDelay = -1
//            rotation += 5.0
//            val newMatrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(Quaternion.fromYawPitchRoll(rotation, rotation, rotation).toBukkitQuaternion())
//            displayEntity.setTransformationMatrix(newMatrix)
//        }, 0, 1)


//        val customPlayer = NewCustomPlayer(sender.getAdaptedSkin(), sender.location, Vector())
//        customPlayer.spawn()
//        customPlayer.rig.playAnimation("wave", true)

//        val customPlayer = NewCustomPlayer(sender.getAdaptedSkin(), Location(sender.world, 0.0, 0.0, 0.0), Vector())
//        customPlayer.spawn()
//        customPlayer.rig.playAnimation("wave", true)
//        //customPlayer.loadFrom("customplayer/rideposition/sit")
//
//        Bukkit.getScheduler().runTaskTimer(BandiCore.instance, Runnable {
//            customPlayer.moveTo(sender.location.toVector(), Vector(sender.location.pitch, sender.location.yaw, 0.0F))
//        }, 0, 1)

        return false
    }
}