package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.custom.player.NewCustomPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class RigTest: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("rigtest", true)) return false
        if(sender !is Player) return false

//        val customPlayer = NewCustomPlayer(sender.getAdaptedSkin(), sender.location, Vector())
//        customPlayer.spawn()
//        customPlayer.rig.playAnimation("wave", true)

        val customPlayer = NewCustomPlayer(sender.getAdaptedSkin(), Location(sender.world, 0.0, 0.0, 0.0), Vector())
        customPlayer.spawn()
        customPlayer.rig.playAnimation("wave", true)
        //customPlayer.loadFrom("customplayer/rideposition/sit")

        Bukkit.getScheduler().runTaskTimer(BandiCore.instance, Runnable {
            customPlayer.moveTo(sender.location.toVector(), Vector(sender.location.pitch, sender.location.yaw, 0.0F))
        }, 0, 1)

        return false
    }
}