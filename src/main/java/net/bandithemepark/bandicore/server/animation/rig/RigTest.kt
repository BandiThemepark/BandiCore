package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.joml.Matrix4f

class RigTest: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("rigtest", true)) return false
        if(sender !is Player) return false

        val textDisplay = PacketTextDisplay()
        textDisplay.spawn(sender.location)
        textDisplay.setText(Util.color("<${BandiColors.YELLOW}>I am a text display"))
        textDisplay.setAlignment(TextDisplay.TextAlignment.RIGHT)
        textDisplay.setBackgroundColor(Color.fromARGB(100, 10, 0, 0))
        textDisplay.setSeeThrough(false)
        textDisplay.setShadow(true)
        textDisplay.setTextOpacity(0.9)
        textDisplay.setBillboard(Display.Billboard.VERTICAL)
        textDisplay.setTransformationMatrix(Matrix4f().scale(1f, 1f, 1f))
        textDisplay.updateMetadata()


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