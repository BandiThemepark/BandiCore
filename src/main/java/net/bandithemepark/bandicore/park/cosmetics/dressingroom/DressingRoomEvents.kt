package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.util.entity.event.PacketEntityDismountEvent
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInputEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class DressingRoomEvents: Listener {
    @EventHandler
    fun onEntityInput(event: PacketEntityInputEvent) {
        val session = DressingRoomSession.activeSessions.find { it.player == event.player } ?: return
        if(System.currentTimeMillis() - 200 < session.lastInteraction) return

        if(event.crouching) {
            session.lastInteraction = System.currentTimeMillis()
            session.currentPage.onBack(event.player)
        }

        if(event.jumping) {
            session.lastInteraction = System.currentTimeMillis()
            session.currentPage.selectedButton?.onClick(event.player)
        }

        if(event.x < 0) {
            session.lastInteraction = System.currentTimeMillis()
            session.currentPage.moveRight(event.player)
        }

        if(event.x > 0) {
            session.lastInteraction = System.currentTimeMillis()
            session.currentPage.moveLeft(event.player)
        }

        if(event.z > 0) {
            session.lastInteraction = System.currentTimeMillis()
            session.currentPage.moveUp(event.player)
        }

        if(event.z < 0) {
            session.lastInteraction = System.currentTimeMillis()
            session.currentPage.moveDown(event.player)
        }
    }

    @EventHandler
    fun onEntityDismount(event: PacketEntityDismountEvent) {
        val session = DressingRoomSession.activeSessions.find { it.player == event.player } ?: return
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val session = DressingRoomSession.activeSessions.find { it.player == event.player } ?: return
        session.resetPosition()
    }
}