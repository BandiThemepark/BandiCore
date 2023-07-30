package net.bandithemepark.bandicore.park.attractions.rideop.camera

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.server.essentials.afk.AfkManager
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.event.PacketEntityDismountEvent
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInputEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector
import java.time.Duration

class RideOPCamera(val location: Location, val rideOP: RideOP) {
    lateinit var bukkitEntity: ArmorStand
    lateinit var vehicleEntity: PacketEntityArmorStand

    var exited = false
    var currentPlayer: Player? = null

    fun setup() {
        bukkitEntity = location.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand
        bukkitEntity.isPersistent = false
        bukkitEntity.isInvisible = true
        bukkitEntity.isMarker = true
        bukkitEntity.setGravity(false)

        vehicleEntity = PacketEntityArmorStand()
        vehicleEntity.spawn(location.clone().add(Vector(0.0, -2.0, 0.0)))
        vehicleEntity.handle.isNoGravity = true
        vehicleEntity.handle.isInvisible = true
        (vehicleEntity.handle as net.minecraft.world.entity.decoration.ArmorStand).isMarker = true
        vehicleEntity.updateMetadata()

        activeCameras.add(this)
    }

    fun startView(player: Player) {
        player.showTitle(
            Title.title(
                Component.text("\uE000"),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(0), Duration.ofMillis(500))
            )
        )

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            if(isAlreadyViewing(player)) switch(player) else start(player)
        }, 10)
    }

    fun stopView(player: Player) {
        player.showTitle(
            Title.title(
                Component.text("\uE000"),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(0), Duration.ofMillis(500))
            )
        )

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable { stop(player) }, 10)
    }

    private fun isAlreadyViewing(player: Player): Boolean {
        return activeCameras.any { it.currentPlayer == player }
    }

    private fun switch(player: Player) {
        val old = activeCameras.find { it.currentPlayer == player }!!
        old.stop(player, true)
        start(player, true)
    }

    private fun start(player: Player, switching: Boolean = false) {
        exited = false
        currentPlayer = player

        if(!switching) {
            beforeSettings[player] = BeforeSettings(player.gameMode, player.location.clone())
            rideOP.cameraProtection = true
        }

        player.teleport(location)

        vehicleEntity.addPassenger(player)
        vehicleEntity.updatePassengers()

        player.gameMode = GameMode.SPECTATOR
        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket((bukkitEntity as CraftArmorStand).handle))
        player.handle.connection.resetPosition()
    }

    private fun stop(player: Player, switching: Boolean = false) {
        vehicleEntity.removePassenger(player)
        vehicleEntity.updatePassengers()

        if(!switching) player.gameMode = beforeSettings[player]!!.gameMode
        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket(player.handle))
        currentPlayer = null

        if(!switching) {
            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                player.teleport(beforeSettings[player]!!.location)
                rideOP.cameraProtection = false
            }, 2)
        }
    }

    companion object {
        val activeCameras = mutableListOf<RideOPCamera>()
        val beforeSettings = hashMapOf<Player, BeforeSettings>()
    }

    data class BeforeSettings(val gameMode: GameMode, val location: Location)

    class Events: Listener {
        @EventHandler
        fun onEntityInput(event: PacketEntityInputEvent) {
            val camera = activeCameras.find { it.vehicleEntity == event.entity } ?: return

            if(event.jumping) {
                camera.rideOP.openMenu(event.player, camera.rideOP.lastPage[event.player])
                BandiCore.instance.afkManager.resetAfkTime(event.player)
            }
        }

        @EventHandler
        fun onEntityDismount(event: PacketEntityDismountEvent) {
            val camera = activeCameras.find { it.vehicleEntity == event.dismounted } ?: return
            event.isCancelled = true

            if(camera.exited) return

            camera.exited = true
            camera.stopView(event.player)
        }
    }

    class Timer {
        fun tick() {
            for(camera in activeCameras) {
                if(camera.currentPlayer == null) continue

                camera.currentPlayer!!.sendTranslatedActionBar("rideop-camera-actionbar", BandiColors.YELLOW.toString())

                try {
                    (camera.currentPlayer!! as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket((camera.bukkitEntity as CraftArmorStand).handle))
                    (camera.currentPlayer!! as CraftPlayer).handle.connection.resetPosition()
                } catch(_: NullPointerException) {}
            }
        }
    }
}