package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideOperateEvent
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideStopOperatingEvent
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.ModelAttachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInputEvent
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector

class RupsbaanCart {
    val seat1Attachment = Attachment("seat1",
        AttachmentPosition(0.5, 0.5, -1.3, 0.0, 0.0, 0.0),
        mutableListOf(),
        AttachmentType.get("seat", listOf("rupsbaan", "kaliba"))!!,
        mutableListOf()
    )

    val seat2Attachment = Attachment("seat2",
        AttachmentPosition(-0.5, 0.5, -1.3, 0.0, 0.0, 0.0),
        mutableListOf(),
        AttachmentType.get("seat", listOf("rupsbaan", "kaliba"))!!,
        mutableListOf()
    )

    val harnessAttachment = Attachment("harness",
        AttachmentPosition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        mutableListOf(),
        AttachmentType.get("model", listOf("DIAMOND_HOE", "4", "kaliba"))!!,
        mutableListOf()
    )

    private val modelAttachment = Attachment("model",
        AttachmentPosition(0.0, 0.0, 0.875, 0.0, 0.0, 0.0),
        mutableListOf(),
        AttachmentType.get("model", listOf("DIAMOND_HOE", "2", "kaliba"))!!,
        mutableListOf(seat1Attachment, seat2Attachment)
    )

    private lateinit var lastLocation: Location
    fun spawnAt(location: Location) {
        lastLocation = location
        seat1Attachment.type.onSpawn(location, seat1Attachment)
        seat2Attachment.type.onSpawn(location, seat2Attachment)
        modelAttachment.type.onSpawn(location, modelAttachment)
    }

    fun deSpawn() {
        seat1Attachment.type.onDeSpawn()
        seat2Attachment.type.onDeSpawn()
        modelAttachment.type.onDeSpawn()
        if(harnessPosition != 0.0) harnessAttachment.type.onDeSpawn()
    }

    fun update(location: Location) {
        lastLocation = location
        val rotationQuaternion = Quaternion.fromYawPitchRoll(location.pitch.toDouble(), location.yaw.toDouble(), 0.0)
        val originalRotation = Vector(location.pitch, location.yaw, 0.0F)
        modelAttachment.update(location.toVector(), rotationQuaternion, originalRotation)
    }

    fun updateSeatHarnesses() {
        val harnessesOpen = harnessPosition != 0.0
        (seat1Attachment.type as SeatAttachment).seat!!.harnessesOpen = harnessesOpen
        (seat2Attachment.type as SeatAttachment).seat!!.harnessesOpen = harnessesOpen
    }

    private var startPosition = 0.0
    private var currentProgress = 30
    fun startUpwardsInterpolation() {
        startPosition = harnessPosition
        currentProgress = 0
    }

    fun updateUpwardsInterpolation() {
        if(currentProgress >= 30) return

        currentProgress += 1
        val progress = currentProgress.toDouble() / 30.0
        harnessPosition = MathUtil.easeOutBounceInterpolation(startPosition, 20.0, progress)

        if(currentProgress == 30) {
            harnessPosition = 20.0
            RideOP.get("rupsbaan")!!.updateMenu()
        }
    }

    private var startDownwardsPosition = 0.0
    var currentProgressDown = 30
    fun startDownwardsInterpolation() {
        startDownwardsPosition = harnessPosition
        currentProgressDown = 0
    }

    fun updateDownwardsInterpolation() {
        if(currentProgressDown >= 30) return

        currentProgressDown += 1
        val progress = currentProgressDown.toDouble() / 30.0
        harnessPosition = MathUtil.cosineInterpolation(progress, startDownwardsPosition, 0.0)

        if(currentProgressDown == 30) {
            harnessPosition = 0.0
            RideOP.get("rupsbaan")!!.updateMenu()
        }
    }

    private fun spawnHarness(spawnLocation: Location) {
        if((harnessAttachment.type as ModelAttachment).displayEntity != null) return

        harnessAttachment.type.onSpawn(spawnLocation, harnessAttachment)
        BandiCore.instance.server.scoreboard.setGlowColor((harnessAttachment.type as ModelAttachment).displayEntity!!.handle!!.uuid.toString(), ChatColor.RED)
        try { BandiCore.instance.server.scoreboard.updateScoreboard() } catch(_: Exception) {}
        modelAttachment.children.add(harnessAttachment)

        modelAttachment.type.onMetadataLoad(listOf("DIAMOND_HOE", "3", "kaliba"))
    }

    private fun deSpawnHarness() {
        harnessAttachment.type.onDeSpawn()
        modelAttachment.children.remove(harnessAttachment)

        modelAttachment.type.onMetadataLoad(listOf("DIAMOND_HOE", "2", "kaliba"))
    }

    var harnessPosition: Double = 0.0
        set(value) {
            if(field == 0.0 && value != 0.0) {
                val spawnLocation = lastLocation.clone().add(Vector(0.0, -3.0, 0.0))
                spawnLocation.pitch = 0.0f
                spawnLocation.yaw = 0.0f

                spawnHarness(spawnLocation)
            } else if(field != 0.0 && value == 0.0) {
                deSpawnHarness()
            }

            field = value

            if(value != 0.0) {
                harnessAttachment.position.pitch = value
            }

            updateSeatHarnesses()
        }

    fun getPlayers(): List<Player> {
        return (seat1Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>() + (seat2Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
    }

    class Events: Listener {
        private fun getCart(packetEntitySeat: PacketEntitySeat): RupsbaanCart? {
            val rupsbaanRideOP = RideOP.get("rupsbaan") as RupsbaanRideOP
            val rideSeats = hashMapOf<PacketEntitySeat, RupsbaanCart>()
            rupsbaanRideOP.ride.carts.forEach { cart ->
                rideSeats[(cart.seat1Attachment.type as SeatAttachment).seat!!] = cart
                rideSeats[(cart.seat2Attachment.type as SeatAttachment).seat!!] = cart
            }

            return rideSeats[packetEntitySeat]
        }

        @EventHandler
        fun onSeatEnter(event: SeatEnterEvent) {
            val cart = getCart(event.entering)

            if(cart != null) {
                if(!(RideOP.get("rupsbaan") as RupsbaanRideOP).ride.harnessesLocked) return
                if(cart.harnessPosition == 0.0) return
                cart.harnessAttachment.type.markFor(event.player)
            }
        }

        @EventHandler
        fun onSeatExit(event: SeatExitEvent) {
            val cart = getCart(event.exiting)

            if(cart != null) {
                if(!(RideOP.get("rupsbaan") as RupsbaanRideOP).ride.harnessesLocked) return
                if(cart.harnessPosition == 0.0) return
                cart.harnessAttachment.type.unMarkFor(event.player)
            }
        }

        @EventHandler
        fun onOperate(event: RideOperateEvent) {
            if(event.rideOP.id != "rupsbaan") return
            if(!(event.rideOP as RupsbaanRideOP).ride.harnessesLocked) return

            for(cart in (event.rideOP as RupsbaanRideOP).ride.carts) {
                if(cart.harnessPosition == 0.0) continue
                cart.harnessAttachment.type.markFor(event.player)
            }
        }

        @EventHandler
        fun onStopOperating(event: RideStopOperatingEvent) {
            if(event.rideOP.id != "rupsbaan") return
            if(!(event.rideOP as RupsbaanRideOP).ride.harnessesLocked) return

            for(cart in (event.rideOP as RupsbaanRideOP).ride.carts) {
                if(cart.harnessPosition == 0.0) continue
                cart.harnessAttachment.type.unMarkFor(event.player)
            }
        }

        @EventHandler
        fun onSeatInput(event: PacketEntityInputEvent) {
            if(event.entity !is PacketEntitySeat) return
            if(!(RideOP.get("rupsbaan") as RupsbaanRideOP).ride.harnessesLocked) return
            val cart = getCart(event.entity as PacketEntitySeat)

            if(cart != null) {
                var newPosition = cart.harnessPosition

                if(event.z < 0.0) {
                    newPosition -= 0.5
                }

                if(newPosition < 0.0) newPosition = 0.0
                if(newPosition > 20.0) newPosition = 20.0
                cart.harnessPosition = newPosition
            }
        }
    }
}