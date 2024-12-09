package net.bandithemepark.bandicore.server.custom.player

import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.util.debug.Testable
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CustomPlayerRigTest: Testable {
    override fun test(sender: CommandSender) {
        if(sender !is Player) return

        val customPlayerRig = CustomPlayerRig(sender.getAdaptedSkin())
        customPlayerRig.visibilityType = PacketEntity.VisibilityType.WHITELIST
        customPlayerRig.visibilityList = mutableListOf(sender)
        customPlayerRig.spawn(sender.location, sender)
        customPlayerRig.moveTo(sender.location.toVector(), Quaternion.fromYawPitchRoll(0.0, 0.0, 0.0))
    }
}