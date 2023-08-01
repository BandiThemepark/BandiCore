package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.custom.player.NewCustomPlayer
import net.bandithemepark.bandicore.server.effects.Effect
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.world.entity.decoration.ArmorStand
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
        itemDisplay.setItemStack(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(8).build())
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay.updateMetadata()

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            itemDisplay.startGlowFor(sender)

            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                itemDisplay.endGlowFor(sender)
            }, 40)
        }, 40)

//        val bukkitDisplay = sender.world.spawnEntity(sender.location, EntityType.ITEM_DISPLAY) as CraftItemDisplay
//        bukkitDisplay.itemStack = ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(8).build()
//        bukkitDisplay.itemDisplayTransform = ItemDisplay.ItemDisplayTransform.HEAD
//
//        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
//            bukkitDisplay.isGlowing = true
//
//            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
//                bukkitDisplay.isGlowing = false
//            }, 40)
//        }, 40)

        return false
    }
}