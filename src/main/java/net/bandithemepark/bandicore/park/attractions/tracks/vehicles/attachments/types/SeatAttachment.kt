package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import com.mojang.authlib.GameProfile
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.ride.SpecialAudioManagement
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.server.custom.player.CustomPlayer
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerRig
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getCustomPlayerSkin
import net.bandithemepark.bandicore.server.custom.player.NewCustomPlayer
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInputEvent
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap

class SeatAttachment: AttachmentType("seat", "ATTRACTION_ID, HARNESS_ATTACHMENT_ID") {
    lateinit var parent: Attachment
    var seat: PacketEntitySeat? = null
    private lateinit var marker: PacketEntityMarker
    var attraction: Attraction? = null
    var tickMovementDirection = null as MovementDirection?
    var harnessAttachmentId: String? = null
    var harnessAttachment: Attachment? = null

    lateinit var world: World

    override fun onSpawn(location: Location, parent: Attachment) {
        this.parent = parent
        world = location.world

        if(harnessAttachmentId != null) harnessAttachment = parent.parent!!.getAllAttachments().find { it.id == harnessAttachmentId }
        marker = PacketEntityMarker(location.world)

        seat = PacketEntitySeat(attraction)
        seat!!.exitingLocation = attraction?.exitingLocation
        seat!!.spawn(location)
        seat!!.handle.isInvisible = true
        seat!!.handle.isNoGravity = true
        seat!!.updateMetadata()
        updateSecondPosition()

        connections[seat!!] = this
    }

    var parentVehicle: TrackVehicle? = null
    var failedToFind = false
    private var lastPosition = Vector(0.0, 0.0, 0.0)
    val poseDebuff = 2
    var poseDebuffCounter = 0
    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector) {
        if(seat!!.spawned) {
            if(!seat!!.harnessesOpen && seat!!.getPassengers().isEmpty()) {
                // De-spawn seat if no passengers are in it and the harnesses are closed
                connections.remove(seat)
                seat!!.deSpawn()
            }
        } else {
            if(seat!!.harnessesOpen) {
                // Re-spawn seat if the harnesses are opened
                seat!!.spawn(mainPosition.toLocation(world))
                seat!!.handle.isInvisible = true
                seat!!.handle.isNoGravity = true
                seat!!.updateMetadata()
                connections[seat!!] = this
            }
        }

        if(parentVehicle == null && !failedToFind) {
            parentVehicle = BandiCore.instance.trackManager.vehicleManager.vehicles.find { it.getAllAttachments().contains(parent) }
            if(parentVehicle == null) failedToFind = true
        }

        if(parentVehicle != null) {
            val speed = parentVehicle!!.speedKMH
            var baseValue = 0.1 + (speed / 45.0) * (0.15 - 0.1)
            if(baseValue < 0) baseValue = -baseValue
            if(baseValue > 0.15) baseValue = 0.15
            seat!!.getPassengers().filterIsInstance<Player>().forEach {
                it.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = baseValue
            }
        }

        // Updating the pose of the player rig
        // TODO Make these poses configurable
        // TODO Load these poses on metadata load
        poseDebuffCounter++
        if(poseDebuffCounter >= poseDebuff) {
            poseDebuffCounter = 0

            if (tickMovementDirection != null && !seat!!.harnessesOpen) {
                if (tickMovementDirection == MovementDirection.UP) {
                    customPlayer?.playPose("scream")
                }
                if (tickMovementDirection == MovementDirection.DOWN) {
                    customPlayer?.playPose("shield")
                }
            } else {
                customPlayer?.playPose("default")
            }
            tickMovementDirection = null
        }

        // Updating the position of the marker
        marker.moveEntity(mainPosition)

        // Updating the position of the seat
        val seatPosition = secondaryPositions.keys.toList()[0]
        val seatRotation = secondaryPositions[seatPosition]!!
        val editedPosition = seatPosition.clone()
        editedPosition.y -= (BODY_HEIGHT + ARMOR_STAND_HEIGHT)
        seat!!.moveEntity(editedPosition, seatRotation, rotationDegrees)

        // Updating the position of the player rig
        customPlayer?.moveTo(mainPosition.clone(), mainRotation.clone())
        lastPosition = mainPosition.clone()
    }

    override fun onDeSpawn() {
        connections.remove(seat)
        seat!!.deSpawn()
        seat = null

        marker.viewers.forEach { marker.removeViewer(it) }
    }

    override fun onMetadataLoad(metadata: List<String>) {
        attraction = Attraction.get(metadata[0])

        if(metadata.size >= 2) {
            harnessAttachmentId = metadata[1]
        }
    }

    override fun markFor(player: Player) {
        if(!marker.viewers.contains(player)) marker.addViewer(player)
    }

    override fun unMarkFor(player: Player) {
        marker.removeViewer(player)
    }

    private fun updateSecondPosition() {
        parent.secondaryPositions.clear()
        val pos = parent.position
        parent.secondaryPositions.add(AttachmentPosition(pos.x, pos.y + BODY_HEIGHT, pos.z, pos.pitch, pos.yaw, pos.roll))
    }

    // Stuff related to custom player models
    var customPlayer: CustomPlayerRig? = null

    fun spawnCustomPlayer(player: Player) {
        customPlayer = CustomPlayerRig(player.getAdaptedSkin())
        customPlayer!!.spawn(seat!!.location.clone(), player)
    }

    fun deSpawnCustomPlayer(player: Player?) {
        customPlayer?.deSpawn()
        customPlayer = null
    }

    class Listeners: Listener {
        @EventHandler
        fun onSeatEnter(event: SeatEnterEvent) {
            if(connections.keys.contains(event.entering)) {
                hide(event.player)
                val seat = connections[event.entering]!!
                seat.spawnCustomPlayer(event.player)

                if(seat.harnessAttachment != null) {
                    seat.harnessAttachment!!.type.markFor(event.player)
                }
            }
        }

        @EventHandler
        fun onSeatExit(event: SeatExitEvent) {
            SpecialAudioManagement.stopOnrideAudio(event.player)

            if(connections.keys.contains(event.exiting)) {
                show(event.player)
                val seat = connections[event.exiting]!!
                seat.deSpawnCustomPlayer(event.player)
                event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = 0.1

                if(seat.harnessAttachment != null) {
                    seat.harnessAttachment!!.type.unMarkFor(event.player)
                }
            }
        }

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            updateForJoining(event.player)
        }

        @EventHandler
        fun onInput(event: PacketEntityInputEvent) {
            if(event.entity is PacketEntitySeat) {
                if(connections.keys.contains(event.entity)) {
                    val seat = connections[event.entity]!!

                    if(event.z > 0) {
                        seat.tickMovementDirection = MovementDirection.UP
                    } else if(event.z < 0) {
                        seat.tickMovementDirection = MovementDirection.DOWN
                    } else if(event.x > 0) {
                        seat.tickMovementDirection = MovementDirection.LEFT
                    } else if(event.x < 0) {
                        seat.tickMovementDirection = MovementDirection.RIGHT
                    }

                    if(event.z >= 0) return
                    if(seat.harnessAttachment == null) return
                    if(!(seat.harnessAttachment!!.type as HarnessAttachment).harnessesLocked) return

                    val harness = seat.harnessAttachment!!.type as HarnessAttachment
                    if(harness.harnessPosition - 0.5 < 0.0) {
                        harness.harnessPosition = 0.0
                        harness.unMarkFor(event.player)
                        if(seat.attraction?.rideOP?.operator != null) harness.unMarkFor(seat.attraction!!.rideOP!!.operator!!)
                    } else {
                        harness.harnessPosition -= 0.5
                    }
                }
            }
        }
    }

    companion object {
        val connections = hashMapOf<PacketEntitySeat, SeatAttachment>()
        private val hiddenPlayers = mutableListOf<Player>()

        fun updateForJoining(joining: Player) {
            hiddenPlayers.forEach {
                joining.hidePlayer(BandiCore.instance, it)
            }
        }

        fun hide(player: Player) {
            hiddenPlayers.add(player)

            for(player2 in Bukkit.getOnlinePlayers()) {
                if(player2 == player) continue
                player2.hidePlayer(BandiCore.instance, player)
            }
        }

        fun show(player: Player) {
            hiddenPlayers.remove(player)

            for(player2 in Bukkit.getOnlinePlayers()) {
                if(player2 == player) continue
                player2.showPlayer(BandiCore.instance, player)
            }
        }

        const val BODY_HEIGHT = 1.1
        const val ARMOR_STAND_HEIGHT = 1.85
    }

    enum class MovementDirection {
        UP, DOWN, LEFT, RIGHT
    }

    // TODO Create class for coaster rider. To add support for NPCs
}