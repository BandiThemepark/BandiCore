package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import org.bukkit.scheduler.BukkitRunnable

class LogFlumeSwitchSegment: SegmentType("logflumeswitch", true, "speedKMH") {
    var state = SwitchState.WAITING_FOR_ARRIVAL
    var waitTime = 0
    lateinit var rideOP: LogFlumeRideOP

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        rideOP = RideOP.get("logflume")!! as LogFlumeRideOP

        vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        vehicle.speedKMH = -metadata[0].toDouble()

        if(state == SwitchState.RESETTING) state = SwitchState.WAITING_FOR_ARRIVAL
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        state.tick(vehicle, this)
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        if(state != SwitchState.WAITING_TO_EXIT) return

        vehicle.physicsType = TrackVehicle.PhysicsType.ALL

        object: BukkitRunnable() {
            override fun run() {
                state = SwitchState.RESETTING
                if(rideOP.switchMovingForward) rideOP.revertSwitch()
            }
        }.runTaskLaterAsynchronously(BandiCore.instance, 60)
    }

    enum class SwitchState {
        WAITING_FOR_ARRIVAL {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment) {
                if(!TrackUtil.isPastMiddle(segment.parent, vehicle)) {
                    vehicle.speed = 0.0
                    segment.waitTime = 20
                    segment.state = WAITING_TO_START
                }
            }
        },
        WAITING_TO_START {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment) {
                segment.waitTime--
                if(segment.waitTime <= 0 && !(RideOP.get("logflume") as LogFlumeRideOP).layout.eStop) {
                    segment.rideOP.startSwitch()
                    segment.state = MOVING
                }
            }
        },
        MOVING {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment) {
                if(segment.rideOP.switchTimeLeft <= 0) {
                    segment.waitTime = 20
                    segment.state = WAITING_TO_RELEASE
                }
            }
        },
        WAITING_TO_RELEASE {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment) {
                segment.waitTime--
                if(segment.waitTime <= 0) {
                    vehicle.speedKMH = segment.metadata[0].toDouble() * 2.0
                    segment.state = WAITING_TO_EXIT
                }
            }
        },
        WAITING_TO_EXIT {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment) {

            }
        },
        RESETTING {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment) {

            }
        };

        abstract fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment)
    }
}