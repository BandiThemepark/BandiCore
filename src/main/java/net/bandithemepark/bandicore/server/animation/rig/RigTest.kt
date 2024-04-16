package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animatronics.Animatronic
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerRig
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.effects.blockdisplaygroup.BlockDisplayGroup
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Vector
import org.joml.Matrix4f

class RigTest: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("rigtest", true)) return false
        if(sender !is Player) return false

        val group = BlockDisplayGroup(
            Location(sender.world,  -43.0, 8.0, -85.0),
            Location(sender.world,  -61.0, 18.0, -103.0),
            Vector(-51.5, 8.0, -93.5),
            Vector(0.0, 0.0, -10.0),
            Vector(0.0, 12.0, 0.0)
        )

        group.spawn()
        group.update()

        Bukkit.getScheduler().scheduleSyncRepeatingTask(BandiCore.instance, Runnable {
            group.rotation = group.rotation.clone().add(Vector(0.0, 2.0, 0.0))
            group.update()
        }, 1, 0)

//        val customPlayer = CustomPlayerRig(sender.getAdaptedSkin())
//        customPlayer.spawn(sender.location.clone().add(0.0, 0.0, 0.0), null)
//        customPlayer.playAnimationLooped("stand")


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