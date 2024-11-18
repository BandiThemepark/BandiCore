package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.util.entity.misc.PacketEntityBat
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class BalloonLeash(var from: Player, var to: Location) {
    val entity = PacketEntityBat()

    fun spawn() {
        entity.spawn(to)
        entity.setVisible(false)
        attach()
    }

    fun deSpawn() {
        entity.deSpawn()
        detach()
    }

    fun update() {
        entity.moveEntity(to.x, to.y, to.z)
    }

    private fun attach() {
        val packet = ClientboundSetEntityLinkPacket(entity.handle, (from as CraftPlayer).handle)
        entity.getPlayersVisibleFor().forEach { (it as CraftPlayer).handle.connection.send(packet) }
    }

    private fun detach() {
        val packet = ClientboundSetEntityLinkPacket(entity.handle, null)
        entity.getPlayersVisibleFor().forEach { (it as CraftPlayer).handle.connection.send(packet) }
    }
}