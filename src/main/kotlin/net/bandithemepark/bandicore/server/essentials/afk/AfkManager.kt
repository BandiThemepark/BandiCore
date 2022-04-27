package net.bandithemepark.bandicore.server.essentials.afk

import io.papermc.paper.event.player.AsyncChatEvent
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.scheduler.BukkitRunnable

class AfkManager {
    private val timeToAfk = 180

    private val sessions = mutableListOf<AfkSession>()
    private val timeSinceLastUpdate = hashMapOf<Player, Int>()
    private val protected = mutableListOf<Player>()

    init {
        BandiCore.instance.getServer().pluginManager.registerEvents(Events(this), BandiCore.instance)
        Timer(this).runTaskTimerAsynchronously(BandiCore.instance, 0, 20)
    }

    fun getSession(player: Player): AfkSession? {
        return sessions.find { it.player == player }
    }

    fun isAfk(player: Player): Boolean {
        return sessions.find { it.player == player } != null
    }

    fun startAfk(player: Player) {
        val session = AfkSession(player)
        session.sendFadeInTitle()
        sessions.add(session)
        timeSinceLastUpdate.remove(player)
    }

    fun resetAfkTime(player: Player) {
        if(isAfk(player)) {
            val session = getSession(player)!!
            session.sendFadeOutTitle()
            sessions.remove(session)
        }

        timeSinceLastUpdate[player] = 0
    }

    fun addAfkTime(player: Player) {
        timeSinceLastUpdate[player] = timeSinceLastUpdate.getOrDefault(player, 0) + 1
    }

    class Timer(val afkManager: AfkManager): BukkitRunnable() {
        override fun run() {
            for(player in Bukkit.getOnlinePlayers().filter { !afkManager.protected.contains(it) && !afkManager.isAfk(it) }) {
                afkManager.addAfkTime(player)
            }

            for(session in afkManager.sessions) session.sendNormalTitle()

            for(player in afkManager.timeSinceLastUpdate.keys) {
                if(afkManager.timeSinceLastUpdate[player]!! >= afkManager.timeToAfk) {
                    afkManager.startAfk(player)
                }
            }
        }
    }

    companion object {
        /**
         * Protects the player from going AFK. This is useful for things like being on rides
         * @param boolean Whether the player is protected
         */
        fun Player.setAfkProtection(boolean: Boolean) {
            if (boolean) {
                if(!BandiCore.instance.afkManager.protected.contains(this)) BandiCore.instance.afkManager.protected.add(this)
            } else {
                BandiCore.instance.afkManager.protected.remove(this)
            }
        }
    }

    class Events(val afkManager: AfkManager): Listener {
        @EventHandler
        fun onMove(event: PlayerMoveEvent) {
            if(event.from.blockX != event.to.blockX || event.from.blockY != event.to.blockY || event.from.blockZ != event.to.blockZ) {
                afkManager.resetAfkTime(event.player)
            }
        }

        @EventHandler
        fun onTeleport(event: PlayerTeleportEvent) {
            afkManager.resetAfkTime(event.player)
        }

        @EventHandler
        fun onChat(event: AsyncChatEvent) {
            afkManager.resetAfkTime(event.player)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            afkManager.resetAfkTime(event.player)
        }
    }
}