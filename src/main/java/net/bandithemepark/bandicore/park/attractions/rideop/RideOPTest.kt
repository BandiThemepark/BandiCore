package net.bandithemepark.bandicore.park.attractions.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments.LogflumeStationSegment
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RideOPTest: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("rideoptest", true)) return false
        if(sender !is Player) return false

        val inv = Bukkit.createInventory(null, 54, Util.color("<#FFFFFF>\uE002\uE011"))

        inv.setItem(8, ItemFactory(Material.PAPER).setCustomModelData(1009).build())
        inv.setItem(17, ItemFactory(Material.PAPER).setCustomModelData(1006).build())
        inv.setItem(26, ItemFactory(Material.PAPER).setCustomModelData(1010).build())

        inv.setItem(48, ItemFactory(Material.PLAYER_HEAD).setSkullOwner(sender).build())
        inv.setItem(49, ItemFactory(Material.BELL).build())
        inv.setItem(50, ItemFactory(Material.MINECART).build())

        sender.openInventory(inv)

        (BandiCore.instance.trackManager.loadedTracks[1].segmentSeparators.filter { it.type is LogflumeStationSegment }[0].type as LogflumeStationSegment).dispatch()

        return false
    }
}
