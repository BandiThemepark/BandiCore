package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class CanCanLiftSegment: SegmentType("cancanlift", true, "SPEED_KMH, WAIT_POSITION_PERCENTAGE, BLOCK_CHECK_POSITION_PERCENTAGE, LIFT_AVAILABLE_PERCENTAGE") {
    val vehicleStates = hashMapOf<TrackVehicle, State>()
    var paused = false
    var waitingCanGo = false
    var currentFirstVehicle: TrackVehicle? = null
    var available = true
    lateinit var track: TrackLayout

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        if(TrackUtil.isPast(parent, vehicle, metadata[3].toDouble())) {
            // This vehicle was spawned in on the lift
            vehicleStates[vehicle] = State.MOVING
            vehicle.speedKMH = metadata[0].toDouble()
            vehicle.physicsType = TrackVehicle.PhysicsType.DOWN
            currentFirstVehicle = vehicle
            track = TrackUtil.getTrack(parent)!!
            return
        }

        vehicleStates[vehicle] = State.ENTERING
        vehicle.physicsType = TrackVehicle.PhysicsType.DOWN
        currentFirstVehicle = vehicle
        track = TrackUtil.getTrack(parent)!!
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        val indexOfVehicle = vehicleStates.keys.indexOf(vehicle)

        if(indexOfVehicle == 0) {
            // This is the last train
            if(paused) {
                if(isNextBlockClear()) {
                    paused = false
                }
            } else {
                if(track.eStop) paused = true

                if(TrackUtil.isPast(parent, vehicle, metadata[2].toDouble())) {
                    if(!isNextBlockClear()) {
                        paused = true
                    }
                }
            }
        }

        if(vehicle.id == currentFirstVehicle!!.id) {
            val previousAvailable = available
            available = TrackUtil.isPast(parent, vehicle, metadata[3].toDouble())
            if(previousAvailable != available) RideOP.get("cancancoaster")!!.updateMenu()

            // This is the first train
            if(vehicleStates[vehicle] == State.ENTERING) {
                if(TrackUtil.isPast(parent, vehicle, metadata[1].toDouble())) {
                    vehicleStates[vehicle] = State.WAITING
                    vehicle.speed = 0.0
                    waitingCanGo = false

                    Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                        waitingCanGo = true
                    }, 20 * 5)
                }
            }

            if(vehicleStates[vehicle] == State.WAITING) {
                if(waitingCanGo && !track.eStop) {
                    vehicleStates[vehicle] = State.MOVING
                    vehicle.speedKMH = metadata[0].toDouble()
                }
            }
        }

        if(paused && vehicleStates[vehicle] == State.MOVING) {
            vehicleStates[vehicle] = State.PAUSED
            vehicle.speed = 0.0
        }

        if(!paused && vehicleStates[vehicle] == State.PAUSED) {
            vehicleStates[vehicle] = State.MOVING
            vehicle.speedKMH = metadata[0].toDouble()
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicleStates.remove(vehicle)
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }

    enum class State {
        ENTERING, WAITING, PAUSED, MOVING
    }
}