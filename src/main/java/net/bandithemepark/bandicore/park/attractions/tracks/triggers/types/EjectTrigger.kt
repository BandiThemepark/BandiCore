package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class EjectTrigger: TrackTriggerType("eject", "ATTRACTION_ID, EJECT_AT_EXIT_LOCATION") {
    override fun onActivation(vehicle: TrackVehicle) {
        val attraction = Attraction.get(metadata[0])
        val ejectAtExitLocation = metadata[1].toBoolean()

        if(attraction != null && ejectAtExitLocation) {
            vehicle.getAllAttachments().filter { it.type is SeatAttachment }.forEach {
                (it.type as SeatAttachment).seat!!.ejectPassengersAt(attraction.exitingLocation)
            }
        } else {
            if(attraction != null) {
                // TODO Add to ridecounter
                // TODO Give achievement
            }

            if(!ejectAtExitLocation) {
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
}