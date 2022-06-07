package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class LogFlumeTransferSegment: SegmentType("logflumetransfer", true, "speedKMH") {
    var state = TransferState.WAITING_FOR_ARRIVAL
    var waitTime = 0
    lateinit var rideOP: LogFlumeRideOP

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        rideOP = RideOP.get("logflume")!! as LogFlumeRideOP

        vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        vehicle.speedKMH = metadata[0].toDouble()
        state = TransferState.WAITING_FOR_ARRIVAL
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        state.tick(vehicle, this)
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL

        object: BukkitRunnable() {
            override fun run() {
                state = TransferState.RESETTING
                if(rideOP.transferMovingForward) rideOP.revertTransfer()
            }
        }.runTaskLaterAsynchronously(BandiCore.instance, 40)
    }

    enum class TransferState {
        WAITING_FOR_ARRIVAL {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment) {
                if(TrackUtil.isPastMiddle(segment.parent, vehicle)) {
                    vehicle.speed = 0.0
                    segment.waitTime = 20
                    segment.state = WAITING_TO_START
                }
            }
        },
        WAITING_TO_START {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment) {
                segment.waitTime--
                if(segment.waitTime <= 0 && !(RideOP.get("logflume") as LogFlumeRideOP).layout.eStop) {
                    segment.rideOP.startTransfer()
                    segment.state = MOVING
                }
            }
        },
        MOVING {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment) {
                if(segment.rideOP.transferTimeLeft <= 0) {
                    segment.waitTime = 20
                    segment.state = WAITING_TO_RELEASE
                }
            }
        },
        WAITING_TO_RELEASE {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment) {
                segment.waitTime--
                if(segment.waitTime <= 0) {
                    vehicle.speedKMH = -segment.metadata[0].toDouble()
                    segment.state = WAITING_TO_EXIT
                }
            }
        },
        WAITING_TO_EXIT {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment) {

            }
        },
        RESETTING {
            override fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment) {

            }
        };

        abstract fun tick(vehicle: TrackVehicle, segment: LogFlumeTransferSegment)
    }
}