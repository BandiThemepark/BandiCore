package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.custom.player.NewCustomPlayer
import net.bandithemepark.bandicore.server.effects.Effect
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

//        val effect = Effect("test_effect")
//        effect.play()
//
//        return false

        val animatronic = Animatronic("animation_test")
        animatronic.spawn(sender.location, Quaternion.fromYawPitchRoll(20.0, 90.0, 45.0))
        animatronic.playAnimation("wave", true)

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            animatronic.setBasePosition(animatronic.basePosition.clone().add(Vector(0.0, 2.0, 0.0)))
        }, 20 * 5)

//        val itemDisplay = PacketItemDisplay()
//        itemDisplay.spawn(sender.location)
//        itemDisplay.setItemStack(ItemFactory(Material.DIAMOND_HOE).setCustomModelData(6).build())
//        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
//
//        itemDisplay.moveEntity(sender.location.x, sender.location.y + 2.0f, sender.location.z)
//        itemDisplay.setInterpolationDuration(2)
//
//        val matrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(Quaternion.fromYawPitchRoll(0.0, 0.0, 0.0).toBukkitQuaternion())
//        itemDisplay.setTransformationMatrix(matrix)
//
//        itemDisplay.updateMetadata()
//
//        var rotation = 0.0
//        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
//            itemDisplay.setInterpolationDelay(-1)
//            rotation += 2.0
//            //val newMatrix = Matrix4f().translation(0.0f, 0.0f, 0.0f).rotation(Quaternion.fromYawPitchRoll(rotation, rotation, rotation).toBukkitQuaternion())
//            val newMatrix = Matrix4f().translation(0.0f, sin(Math.toRadians(rotation)).toFloat() * 5.0f, 0.0f)
//            itemDisplay.setTransformationMatrix(newMatrix)
//            itemDisplay.updateMetadata()
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