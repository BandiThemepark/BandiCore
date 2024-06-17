package net.bandithemepark.bandicore.park.attractions.gates

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util.getText
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.type.Sign
import org.bukkit.block.sign.Side
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.atan2

class OneWayGateEvents: Listener {
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        if(event.line(0) == null) return
        if(!event.line(0)!!.getText().equals("[GATE]", true)) return

        event.player.sendTranslatedMessage("one-way-gate-created", BandiColors.YELLOW.toString())
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if(event.from.blockX == event.to.blockX && event.from.blockY == event.to.blockY && event.from.blockZ == event.to.blockZ) return

        val blockUnderFloor = event.to.block.getRelative(0, -2, 0)
        if(blockUnderFloor.blockData !is Sign) return
        val sign = blockUnderFloor.state as org.bukkit.block.Sign
        if(!sign.getSide(Side.FRONT).line(0).getText().equals("[GATE]", true)) return

        if(event.player.hasPermission("bandithemepark.crew")) return

        val signDirection = (blockUnderFloor.blockData as Sign).rotation.direction
        val signYaw = atan2(signDirection.x, signDirection.z)
        val playerDelta = event.to.toVector().subtract(event.from.toVector())
        val playerYaw = atan2(playerDelta.x, playerDelta.z)

        val signYawDegrees = Math.toDegrees(signYaw)
        val playerYawDegrees = Math.toDegrees(playerYaw)

        // Check if player yaw is within 90 degrees of sign yaw
        if(playerYawDegrees < signYawDegrees - 90 || playerYawDegrees > signYawDegrees + 90) return

        event.player.spawnParticle(
            Particle.BLOCK_MARKER, event.to.blockX + 0.5, event.to.blockY + 1.5, event.to.blockZ + 0.5, 1, Bukkit.createBlockData(
                Material.BARRIER))
        val delta = event.to.toVector().subtract(event.from.toVector()).multiply(-1.5)
        val toLocation = event.from.toVector().add(delta).toLocation(event.from.world)
        toLocation.yaw = event.player.location.yaw
        toLocation.pitch = event.player.location.pitch
        event.player.teleport(toLocation)
        event.player.sendTranslatedActionBar("one-way-gate-blocked", BandiColors.RED.toString())
    }
}