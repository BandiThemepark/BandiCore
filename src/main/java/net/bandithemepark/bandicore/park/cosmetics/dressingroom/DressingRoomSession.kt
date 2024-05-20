package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerRig
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.entity.HoverableEntity
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
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
    lateinit var customPlayer: CustomPlayerRig

    var exited = false

    init {
        setupCamera()
        setupCustomPlayer()
        startView()

        activeSessions.add(this)
    }

    private fun setupCustomPlayer() {
        customPlayer = CustomPlayerRig(player.getAdaptedSkin())
        customPlayer.spawn(dressingRoom.playerPosition.toLocation(dressingRoom.world), null)
        customPlayer.moveTo(dressingRoom.playerPosition, Quaternion.fromYawPitchRoll(0.0, dressingRoom.playerYaw, 0.0))

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            customPlayer.playAnimationOnce("dressing_room_enter") { playRandomIdleAnimation() }

        }, 10)
    }

    val idleAnimations = listOf("dressing_room_idle_1", "dressing_room_idle_2")
    private fun playRandomIdleAnimation() {
        val animationId = idleAnimations.random()
        customPlayer.playAnimationOnce(animationId) { playRandomIdleAnimation() }
    }

    fun playAnimation(animationName: String) {
        customPlayer.playAnimationOnce(animationName) { playRandomIdleAnimation() }
    }

    private fun removeCustomPlayer() {
        customPlayer.deSpawn()
    }

    fun exit() {
        stopView()

        activeSessions.remove(this)
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
            stopCamera()
            removeCustomPlayer()
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
        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket((bukkitEntity as CraftArmorStand).handle))
        player.handle.connection.resetPosition()
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

    }

    companion object {
        val activeSessions = mutableListOf<DressingRoomSession>()
    }
}