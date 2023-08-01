package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.rideop.camera.RideOPCamera
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.StartPage
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.UIPage
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.entity.HoverableEntity
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.time.Duration

class DressingRoomSession(
    val player: Player,
    val dressingRoom: DressingRoom,

) {
    val beforeGameMode = player.gameMode
    val beforeLocation = player.location.clone()

    lateinit var bukkitEntity: ArmorStand
    lateinit var vehicleEntity: PacketEntityArmorStand

    var exited = false
    var currentPage: UIPage = StartPage()
    var lastInteraction = System.currentTimeMillis()

    init {
        setupCamera()
        startView()
        currentPage.render(dressingRoom.interfacePosition.toLocation(dressingRoom.world), player, dressingRoom.interfaceYaw)

        activeSessions.add(this)
    }

    fun exit() {
        stopView()

        activeSessions.remove(this)
    }

    fun openPage(page: UIPage) {
        currentPage.remove(player)
        currentPage = page
        currentPage.render(dressingRoom.interfacePosition.toLocation(dressingRoom.world), player, dressingRoom.interfaceYaw)

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            resetPosition()
        }, 2)
    }

    private fun setupCamera() {
        bukkitEntity = dressingRoom.world.spawnEntity(dressingRoom.cameraPosition.toLocation(dressingRoom.world,
            dressingRoom.cameraYaw.toFloat(), dressingRoom.cameraPitch.toFloat()
        ), EntityType.ARMOR_STAND) as ArmorStand
        bukkitEntity.isPersistent = false
        bukkitEntity.isInvisible = true
        bukkitEntity.isMarker = true
        bukkitEntity.setGravity(false)

        vehicleEntity = PacketEntityArmorStand()
        vehicleEntity.spawn(dressingRoom.cameraPosition.toLocation(dressingRoom.world,
            dressingRoom.cameraYaw.toFloat(), dressingRoom.cameraPitch.toFloat()
        ).clone().add(Vector(0.0, -2.0, 0.0)))

        vehicleEntity.handle.isNoGravity = true
        vehicleEntity.handle.isInvisible = true
        (vehicleEntity.handle as net.minecraft.world.entity.decoration.ArmorStand).isMarker = true
        vehicleEntity.updateMetadata()
    }

    private fun removeCamera() {
        bukkitEntity.remove()
        vehicleEntity.deSpawn()
    }

    private fun startView() {
        player.showTitle(
            Title.title(
                Component.text("\uE000"),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(0), Duration.ofMillis(500))
            )
        )

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            startCamera()
        }, 10)
    }

    private fun stopView() {
        player.showTitle(
            Title.title(
                Component.text("\uE000"),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(0), Duration.ofMillis(500))
            )
        )

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            currentPage.remove(player)
            stopCamera()
            removeCamera()
        }, 10)
    }

    private fun startCamera() {
        player.getNameTag()!!.hidden = true
        exited = false

        player.teleport(dressingRoom.cameraPosition.toLocation(dressingRoom.world))
        HoverableEntity.timer.movements[player] = player.location.clone()

        vehicleEntity.addPassenger(player)
        vehicleEntity.updatePassengers()

        player.gameMode = GameMode.SPECTATOR
        resetPosition()
//        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket((bukkitEntity as CraftArmorStand).handle))
//        player.handle.connection.resetPosition()
    }

    private fun stopCamera() {
        player.getNameTag()!!.hidden = false

        vehicleEntity.removePassenger(player)
        vehicleEntity.updatePassengers()

        player.gameMode = beforeGameMode
        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket(player.handle))

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            player.teleport(beforeLocation)
        }, 2)
    }

    fun onTick() {
        //val packet = ClientboundTeleportEntityPacket()
        //(player as CraftPlayer).handle.connection.send(packet)
    }

    fun resetPosition() {
        val location = dressingRoom.cameraPosition.toLocation(dressingRoom.world)
        location.yaw = dressingRoom.cameraYaw.toFloat()
        location.pitch = dressingRoom.cameraPitch.toFloat()
        location.y -= 2.3
        player.teleport(location)

        vehicleEntity.removePassenger(player)
        vehicleEntity.updatePassengers()
        vehicleEntity.addPassenger(player)
        vehicleEntity.updatePassengers()
    }

    companion object {
        val activeSessions = mutableListOf<DressingRoomSession>()
    }
}