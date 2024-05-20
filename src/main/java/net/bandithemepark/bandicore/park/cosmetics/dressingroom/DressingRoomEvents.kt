package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.entity.event.PacketEntityDismountEvent
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInputEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class DressingRoomEvents: Listener {

    @EventHandler
    fun onEntityDismount(event: PacketEntityDismountEvent) {
        val session = DressingRoomSession.activeSessions.find { it.player == event.player } ?: return
        event.isCancelled = true
        session.exit()
    }

    @EventHandler
    fun onEntityInput(event: PacketEntityInputEvent) {
        if(event.jumping) {
            // Open UI
            Bukkit.broadcast(Component.text("Open UI"))
            BandiCore.instance.afkManager.resetAfkTime(event.player)
        }
    }
}