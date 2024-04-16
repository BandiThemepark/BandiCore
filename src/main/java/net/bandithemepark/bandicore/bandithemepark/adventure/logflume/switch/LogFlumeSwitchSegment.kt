package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class LogFlumeSwitchSegment: SegmentType("logflumeswitch", true, "speedKMH") {
    var state = SwitchState.WAITING_FOR_ARRIVAL
    var waitTime = 0
    var currentVehicle: TrackVehicle? = null
    lateinit var rideOP: LogFlumeRideOP

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        rideOP = RideOP.get("logflume")!! as LogFlumeRideOP

        currentVehicle = vehicle
        vehicle.physicsType = TrackVehicle.PhysicsType.NONE

        if(rideOP.storageState != LogFlumeRideOP.StorageState.RETRIEVING) vehicle.speedKMH = -metadata[0].toDouble()

        if(state == SwitchState.RESETTING) state = SwitchState.WAITING_FOR_ARRIVAL
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        state.tick(vehicle, this, rideOP)
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
//        Bukkit.broadcast(Component.text("Vehicle left switch, storage state: ${rideOP.storageState}"))
        if(rideOP.storageState == LogFlumeRideOP.StorageState.STORING) {
            state = SwitchState.RESETTING
            currentVehicle = null
            return
        }

        if(state != SwitchState.WAITING_TO_EXIT) return
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
        currentVehicle = null

        object: BukkitRunnable() {
            override fun run() {
                state = SwitchState.RESETTING
                if(rideOP.switchMovingForward) rideOP.revertSwitch()
            }
        }.runTaskLaterAsynchronously(BandiCore.instance, 60)
    }

    fun sendIntoStorage() {
        currentVehicle!!.speedKMH = -metadata[0].toDouble()
    }

    var startOverride = false
    enum class SwitchState {
        WAITING_FOR_ARRIVAL {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP) {
                if(rideOP.storageState != LogFlumeRideOP.StorageState.RETRIEVING) {
                    if(!TrackUtil.isPastMiddle(segment.parent, vehicle)) {
                        vehicle.speed = 0.0
                        segment.waitTime = 20
                        segment.state = WAITING_TO_START
                        rideOP.vehicleMovingOnToSwitch = false
                        rideOP.updateMenu()
                    }
                } else {
                    if(TrackUtil.isPastMiddle(segment.parent, vehicle)) {
                        vehicle.speed = 0.0
                        segment.waitTime = 20
                        segment.state = WAITING_TO_START
                        rideOP.storageState = LogFlumeRideOP.StorageState.NONE
                        rideOP.updateMenu()
                    }
                }
            }
        },
        WAITING_TO_START {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP) {
                if(rideOP.transferModeActive && !segment.startOverride) return

                segment.waitTime--
                if(segment.waitTime <= 0 && !(RideOP.get("logflume") as LogFlumeRideOP).layout.eStop) {
                    segment.startOverride = false
                    segment.rideOP.startSwitch()
                    segment.state = MOVING
                }
            }
        },
        MOVING {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP) {
                if(segment.rideOP.switchTimeLeft <= 0) {
                    segment.waitTime = 20
                    segment.state = WAITING_TO_RELEASE
                }
            }
        },
        WAITING_TO_RELEASE {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP) {
                segment.waitTime--
                if(segment.waitTime <= 0) {
                    vehicle.speedKMH = segment.metadata[0].toDouble() * 2.0
                    segment.state = WAITING_TO_EXIT
                }
            }
        },
        WAITING_TO_EXIT {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP) {

            }
        },
        RESETTING {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP) {

            }
        };

        abstract fun tick(vehicle: TrackVehicle, segment: LogFlumeSwitchSegment, rideOP: LogFlumeRideOP)
    }
}