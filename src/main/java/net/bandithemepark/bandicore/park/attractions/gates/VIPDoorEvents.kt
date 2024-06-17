package net.bandithemepark.bandicore.park.attractions.gates

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util.getText
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Door
import org.bukkit.block.data.type.Sign
import org.bukkit.block.sign.Side
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

class VIPDoorEvents: Listener {
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        if(event.line(0) == null) return
        if(!event.line(0)!!.getText().equals("[VIP]", true)) return

        event.player.sendTranslatedMessage("vip-door-created", BandiColors.YELLOW.toString())
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if(event.from.blockX == event.to.blockX && event.from.blockY == event.to.blockY && event.from.blockZ == event.to.blockZ) return
        if(event.to.block.blockData !is Door) return

        val blockBelowDoor = event.to.block.getRelative(0, -2, 0)
        if(blockBelowDoor.blockData !is Sign) return
        val sign = blockBelowDoor.state as org.bukkit.block.Sign
        if(!sign.getSide(Side.FRONT).line(0).getText().equals("[VIP]", true)) return

        if(event.player.hasPermission("bandithemepark.vip")) return

        event.player.spawnParticle(Particle.BLOCK_MARKER, event.to.blockX + 0.5, event.to.blockY + 1.5, event.to.blockZ + 0.5, 1, Bukkit.createBlockData(Material.BARRIER))
        val delta = event.to.toVector().subtract(event.from.toVector()).multiply(-1.5)
        val toLocation = event.from.toVector().add(delta).toLocation(event.from.world)
        toLocation.yaw = event.player.location.yaw
        toLocation.pitch = event.player.location.pitch
        event.player.teleport(toLocation)
        event.player.sendTranslatedActionBar("vip-door-no-permission", BandiColors.RED.toString())
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.action != Action.RIGHT_CLICK_BLOCK) return
        if(event.clickedBlock == null) return
        if(event.clickedBlock!!.blockData !is Door) return

        val pressedTopBlock = (event.clickedBlock!!.blockData as Door).half == Bisected.Half.TOP

        val blockBelowDoor = event.clickedBlock!!.getRelative(0, if(pressedTopBlock) -3 else -2, 0)
        if(blockBelowDoor.blockData !is Sign) return
        val sign = blockBelowDoor.state as org.bukkit.block.Sign
        if(!sign.getSide(Side.FRONT).line(0).getText().equals("[VIP]", true)) return

        if(!event.player.hasPermission("bandithemepark.vip")) return

        event.isCancelled = false
    }
}