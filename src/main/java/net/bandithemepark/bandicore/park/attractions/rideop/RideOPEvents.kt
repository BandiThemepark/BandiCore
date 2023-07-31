package net.bandithemepark.bandicore.park.attractions.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerEventListeners
import net.bandithemepark.bandicore.park.attractions.rideop.camera.RideOPCamera
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideOperateEvent
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideStopOperatingEvent
import net.bandithemepark.bandicore.server.essentials.afk.PlayerStartAfkEvent
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionEnterEvent
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionLeaveEvent
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerQuitEvent

class RideOPEvents: Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if(event.inventory.holder is RideOP) {
            event.isCancelled = true

            val rideOP = event.inventory.holder as RideOP
            val player = event.whoClicked as Player

            // Check if the clicked slot is for one of the pages
            for(page in rideOP.loadedPages) {
                if(page.iconSlot == event.slot) {
                    player.playSound(Sound.sound(Key.key("block.wooden_button.click_on"), Sound.Source.MASTER, 10F, 1F))
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideOP.openMenu(player, page) })
                    return
                }
            }

            // Check if the clicked slot is for the operator
            if(event.slot == 48) {
                if(rideOP.operator == null) {
                    if(canOperate(player, rideOP)) startOperating(player, rideOP)
                } else {
                    if(rideOP.operator == player) {
                        stopOperating(player, rideOP)
                    } else {
                        if(canOperate(player, rideOP) && player.hasPermission("bandithemepark.crew")) {
                            stopOperating(rideOP.operator!!, rideOP)
                            startOperating(player, rideOP)
                        }
                    }
                }
            }

            // Check if the clicked slot is for the broadcast button

            // Check if the clicked slot is for one of the normal buttons
            val convertedSlot = RideOPPage.convertBukkitSlotToRideOPSlot(event.slot)
            if(convertedSlot != -1) {
                val button = rideOP.lastPage[player]!!.loadedButtons.find { it.slot == convertedSlot }
                if(button != null) {
                    if(rideOP.operator == player) {
                        button.onClick(player)
                    }
                }
            }
        }
    }

    fun canOperate(player: Player, rideOP: RideOP): Boolean {
        if(RideOP.isOperating(player)) return false
        if(player.isInsideVehicle) return false
        if(PacketEntitySeat.isRiding(player)) return false
        if(!rideOP.getParentAttraction()!!.mode.canOperate(player)) return false

        return true
    }

    fun startOperating(player: Player, rideOP: RideOP) {
        val event = RideOperateEvent(rideOP, player)
        Bukkit.getPluginManager().callEvent(event)

        if(!event.isCancelled) {
            rideOP.operator = player
            rideOP.updateMenu()
            player.sendTranslatedMessage("rideop-started", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName))
        }
    }

    fun stopOperating(player: Player, rideOP: RideOP) {
        val event = RideStopOperatingEvent(rideOP, player)
        Bukkit.getPluginManager().callEvent(event)

        if(!event.isCancelled) {
            rideOP.operator = null
            rideOP.updateMenu()
            player.sendTranslatedMessage("rideop-stopped", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName))

            val camera = RideOPCamera.activeCameras.find { it.currentPlayer == event.player } ?: return
            camera.stopView(event.player)
            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { player.closeInventory() })
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if(RideOP.isOperating(event.player)) {
            val rideOP = RideOP.getOperating(event.player)!!
            rideOP.operator = null
            rideOP.updateMenu()
        }
    }

    @EventHandler
    fun onRegionEnter(event: PlayerPriorityRegionEnterEvent) {
        if(!event.player.hasPermission("bandithemepark.vip")) return
        val rideOP = RideOP.rideOPs.find { it.region == event.toRegion } ?: return
        if(rideOP.vipsInRegion.contains(event.player)) return

        rideOP.vipsInRegion.add(event.player)

        if(!AudioServerEventListeners.connectedPlayers.contains(event.player)) return
        rideOP.sendInfoToNewPlayer(event.player)
    }

    @EventHandler
    fun onRegionExit(event: PlayerPriorityRegionLeaveEvent) {
        if(!event.player.hasPermission("bandithemepark.vip")) return

        for(rideOP in RideOP.rideOPs) {
            if(rideOP.region != event.fromRegion) continue
            if(rideOP.cameraProtection) return

            rideOP.vipsInRegion.remove(event.player)
            if(AudioServerEventListeners.connectedPlayers.contains(event.player)) rideOP.sendRemove(event.player)

            if(rideOP.operator == event.player) {
                rideOP.operator = null
                rideOP.updateMenu()
                event.player.sendTranslatedMessage("rideop-area-left", BandiColors.RED.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName))
            }
        }
    }

    @EventHandler
    fun onAFK(event: PlayerStartAfkEvent) {
        if (!event.player.hasPermission("bandithemepark.vip")) return

        val rideOP = RideOP.rideOPs.find { it.operator == event.player } ?: return
        RideOPCamera.activeCameras.find { it.currentPlayer == event.player }?.stopView(event.player)

        rideOP.operator = null
        rideOP.updateMenu()
        event.player.sendTranslatedMessage("rideop-area-left", BandiColors.RED.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName)
        )
    }
}