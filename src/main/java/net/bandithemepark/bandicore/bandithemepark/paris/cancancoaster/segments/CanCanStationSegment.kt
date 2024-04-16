package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CanCanStationSegment: SegmentType("cancanstation", true, "DISPATCH_SPEED_KMH") {
    var pastMiddle = false
    var dispatched = false
    var currentVehicle: TrackVehicle? = null

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.NONE

        pastMiddle = false
        dispatched = false
        currentVehicle = vehicle
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(!pastMiddle && !dispatched) {
            if(TrackUtil.isPastMiddle(parent, vehicle)) {
                pastMiddle = true
                vehicle.speed = 0.0

                val rideOP = RideOP.get("cancancoaster")!! as CanCanRideOP
                rideOP.dispatchedFromFinal = false
                if(rideOP.transferMode) {
                    rideOP.resetTransferState()
                }

                vehicle.getPlayerPassengers().forEach {
                    BandiCore.instance.server.ridecounterManager.increase(it, "cancancoaster") {
                        val newCount = BandiCore.instance.server.ridecounterManager.getRidecountOnOf(it, "cancancoaster")
                        it.sendTranslatedMessage("ridecounter-increased", BandiColors.YELLOW.toString(), MessageReplacement("attraction", Attraction.get("cancancoaster")!!.appearance.displayName), MessageReplacement("count", newCount.toString()))
                    }
                }

                vehicle.getAllAttachments().filter { it.type is SeatAttachment }.forEach {
                    val passengers = (it.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
                    (it.type as SeatAttachment).seat!!.ejectPassengers()

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        passengers.forEach { player ->
                            (it.type as SeatAttachment).deSpawnCustomPlayer(player)
                            SeatAttachment.show(player)
                        }
                    })
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        if(dispatched) vehicle.physicsType = TrackVehicle.PhysicsType.ALL
        currentVehicle = null
//        Bukkit.broadcast(Component.text("Vehicle left station, was sent backwards? ${!dispatched}"))
    }

    fun dispatch() {
        dispatched = true
        currentVehicle?.speedKMH = metadata[0].toDouble()
    }

    fun sendBackwards() {
        dispatched = false
        currentVehicle?.speedKMH = -metadata[0].toDouble()
    }
}