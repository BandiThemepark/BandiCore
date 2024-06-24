package net.bandithemepark.bandicore.util.entity

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

interface HoverableEntity {
    val translationId: String
    val permission: String?
    val detectionOffset: Double
    fun onInteract(player: Player)

    fun makeGlowFor(player: Player) {
        val before = (this as PacketEntity).handle!!.hasGlowingTag()
        (this as PacketEntity).handle!!.setGlowingTag(true)
        (this as PacketEntity).updateMetadataFor(player)
        (this as PacketEntity).handle!!.setGlowingTag(before)
    }

    fun stopGlowFor(player: Player) {
        val before = (this as PacketEntity).handle!!.hasGlowingTag()
        (this as PacketEntity).handle!!.setGlowingTag(false)
        (this as PacketEntity).updateMetadataFor(player)
        (this as PacketEntity).handle!!.setGlowingTag(before)
    }

    fun canUse(player: Player): Boolean {
        return permission == null || player.hasPermission(permission!!)
    }

    companion object {
        fun getHoverableEntities(): List<PacketEntity> {
            val clonedActive = PacketEntity.active.toList()
            return clonedActive.filter { it is HoverableEntity }
        }

        lateinit var timer: Timer

        fun setup() {
            timer = Timer()
            timer.runTaskTimerAsynchronously(BandiCore.instance, 0, 3)

            Bukkit.getServer().pluginManager.registerEvents(Events(), BandiCore.instance)
        }
    }

    object LookAtUtil {
        fun isLookingAt(eyeLocation: Location, target: PacketEntity, detectionOffset: Double = 0.0): Boolean {
            val toEntity = target.location.toVector().add(Vector(0.0, detectionOffset, 0.0)).subtract(eyeLocation.toVector())
            val dot = toEntity.normalize().dot(eyeLocation.direction)
            return dot > 0.94
        }
    }

    class Events: Listener {
        @EventHandler
        fun onMove(event: PlayerMoveEvent) {
            if(!event.isCancelled) timer.movements[event.player] = event.player.eyeLocation.clone()
        }

        @EventHandler
        fun onHandSwap(event: PlayerSwapHandItemsEvent) {
            if(timer.currentlyLookingAt.containsKey(event.player)) {
                (timer.currentlyLookingAt[event.player] as HoverableEntity).onInteract(event.player)
                event.isCancelled = true
            }
        }
    }

    class Timer: BukkitRunnable() {
        val movements = hashMapOf<Player, Location>()
        val currentlyLookingAt = hashMapOf<Player, PacketEntity>()

        override fun run() {
            val hoverableEntities = getHoverableEntities()

            for(player in movements.keys) {
                var foundAny = false
                val hoverableForPlayer = hoverableEntities.filter { (it as HoverableEntity).canUse(player) }.filter { Util.getLengthBetween(it.location!!.clone().add(0.0, 1.7, 0.0), movements[player]!!) < 3.0 }

                for(hoverable in hoverableForPlayer) {
                    if(LookAtUtil.isLookingAt(movements[player]!!, hoverable, (hoverable as HoverableEntity).detectionOffset)) {
                        currentlyLookingAt[player] = hoverable
                        (hoverable as HoverableEntity).makeGlowFor(player)

                        foundAny = true
                        break
                    }
                }

                if(!foundAny) {
                    if(currentlyLookingAt.containsKey(player)) {
                        (currentlyLookingAt[player]!! as HoverableEntity).stopGlowFor(player)
                        currentlyLookingAt.remove(player)
                    }
                }
            }

            for(player in currentlyLookingAt.keys) {
                val lookingAt = currentlyLookingAt[player] as HoverableEntity
                player.sendTranslatedActionBar(lookingAt.translationId, BandiColors.YELLOW.toString(), MessageReplacement("key", "<key:key.swapOffhand>"))
            }

            movements.clear()
        }
    }
}