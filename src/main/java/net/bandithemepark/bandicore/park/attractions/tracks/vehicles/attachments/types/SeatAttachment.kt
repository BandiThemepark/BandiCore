package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.server.custom.player.CustomPlayer
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin
import net.bandithemepark.bandicore.util.Util.isAlexSkin
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.Vector

class SeatAttachment: AttachmentType("seat", "ATTRACTION_ID") {
    val BODY_HEIGHT = 1.1
    val ARMOR_STAND_HEIGHT = 1.675

    lateinit var parent: Attachment
    var seat: PacketEntitySeat? = null
    lateinit var marker: PacketEntityMarker

    override fun onSpawn(location: Location, parent: Attachment) {
        this.parent = parent
        marker = PacketEntityMarker(location.world)

        seat = PacketEntitySeat()
        seat!!.spawn(location)
        seat!!.handle!!.isInvisible = true
        seat!!.handle!!.isNoGravity = true
        seat!!.updateMetadata()
        updateSecondPosition()

        connections[seat!!] = this
    }

    var lastPosition = Vector(0.0, 0.0, 0.0)
    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector) {
        marker.moveEntity(mainPosition)

        val seatPosition = secondaryPositions.keys.toList()[0]
        val seatRotation = secondaryPositions[seatPosition]!!
        val editedPosition = seatPosition.clone()
        editedPosition.y = editedPosition.y - (BODY_HEIGHT+ARMOR_STAND_HEIGHT)
        seat!!.moveEntity(editedPosition, seatRotation, rotationDegrees)

        customPlayer?.location = mainPosition.clone().toLocation(seat!!.location!!.world)
        customPlayer?.completeRotation = mainRotation.clone()
        customPlayer?.updatePosition()
        lastPosition = mainPosition.clone()
//        val editedPosition = mainPosition.clone()
//        editedPosition.y = editedPosition.y - ARMOR_STAND_HEIGHT
//        seat!!.moveEntity(editedPosition, mainRotation, rotationDegrees)
    }

    override fun onDeSpawn() {
        connections.remove(seat)
        seat!!.deSpawn()
        seat = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        // TODO Load attraction and it's exiting location and apply it to the seat
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
    var customPlayer: CustomPlayer? = null

    fun spawnCustomPlayer(player: Player) {
        Bukkit.broadcast(Component.text("Player is slim? " + player.isAlexSkin()))

        customPlayer = CustomPlayer(CustomPlayerSkin(player.uniqueId, (player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next().value))
        customPlayer!!.setVisibilityType(PacketEntity.VisibilityType.BLACKLIST)
        customPlayer!!.setVisibilityList(mutableListOf(player))
        customPlayer!!.spawn(lastPosition.clone().add(Vector(0.0, 0.63, 0.0)).toLocation(seat!!.location!!.world))
        customPlayer!!.loadFrom("sit")
    }

    fun deSpawnCustomPlayer(player: Player) {
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
            }
        }

        @EventHandler
        fun onSeatExit(event: SeatExitEvent) {
            if(connections.keys.contains(event.exiting)) {
                show(event.player)
                val seat = connections[event.exiting]!!
                seat.deSpawnCustomPlayer(event.player)
            }
        }

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            updateForJoining(event.player)
        }
    }

    companion object {
        val connections = hashMapOf<PacketEntitySeat, SeatAttachment>()
        private val hiddenPlayers = mutableListOf<Player>()

        fun updateForJoining(joining: Player) {
            hiddenPlayers.forEach { joining.hidePlayer(BandiCore.instance, it) }
        }

        fun hide(player: Player) {
            hiddenPlayers.add(player)
            for(player2 in Bukkit.getOnlinePlayers()) player2.hidePlayer(BandiCore.instance, player)
        }

        fun show(player: Player) {
            hiddenPlayers.remove(player)
            for(player2 in Bukkit.getOnlinePlayers()) player2.showPlayer(BandiCore.instance, player)
        }
    }

    // TODO Create class for coaster rider. To add support for NPCs
}