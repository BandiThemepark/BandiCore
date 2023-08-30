package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class TrackVehicleUpdater {
    fun onTick() {
        // Normal moving
        for(vehicle in BandiCore.instance.trackManager.vehicleManager.vehicles) {
            // Saving the position before moving to use later
            val oldPosition = vehicle.position.clone()

            // Moving the train with its speed
            try {
                vehicle.position.move(vehicle.ridingOn, vehicle.speed * BandiCore.instance.trackManager.pointsPerMeter)
            } catch(e: IllegalStateException) {
                vehicle.position = oldPosition
                vehicle.speed *= -1.0
            }

            // Getting the front and back of the train
            val totalLength = vehicle.getLengthInPoints()
            var back = vehicle.position.clone()
            var front = vehicle.position.clone()

            try {
                back.move(vehicle.ridingOn, -totalLength / 2.0)
                front.move(vehicle.ridingOn, totalLength / 2.0)
            } catch(e: IllegalStateException) {
                vehicle.position = oldPosition
                vehicle.speed *= -1.0
                back = oldPosition.clone()
                front = oldPosition.clone()

                back.move(vehicle.ridingOn, -totalLength / 2.0)
                front.move(vehicle.ridingOn, totalLength / 2.0)
            }

            // Updating the collisions

            // Check if collided vehicles are still colliding, and if not, update states so that vehicles can move again
            if(vehicle.hasCollided()) {
                if(vehicle.collidedInFront != null) {
                    // Break vehicle if it is getting closer to vehicle in front
                    if(vehicle.collidedInFront!!.speed < vehicle.speed) {
                        vehicle.speed = vehicle.collidedInFront!!.speed
                    }

                    if(vehicle.collidedInFront!!.speed > vehicle.speed) {
                        // The vehicle can move again and the collision is over
                        vehicle.collidedInFront = null
                        vehicle.collidedBehind?.collidedInFront = null
                    }
                } else {
                    // Break vehicle behind if it is getting too close
                    if(vehicle.collidedBehind!!.speed > vehicle.speed) {
                        vehicle.collidedBehind!!.speed = vehicle.speed
                    }

                    if(vehicle.collidedBehind!!.speed < vehicle.speed) {
                        // The vehicle can move again and the collision is over
                        vehicle.collidedBehind = null
                        vehicle.collidedInFront?.collidedBehind = null
                    }
                }
            }

            // Collision checking
            if(vehicle.ridingOn.getVehicles().size > 1 && vehicle.speed != 0.0) {
                if(vehicle.speed > 0) {
                    val nextVehicle = vehicle.getNextVehicle()

                    if(nextVehicle != null && vehicle.overlaps(nextVehicle)) {
                        // A collision has occurred
                        val finalVehicle = nextVehicle.getFinalCollidedForwards()

                        if(finalVehicle == nextVehicle) {
                            if(nextVehicle.physicsType != TrackVehicle.PhysicsType.NONE) {
                                val nextSpeed = nextVehicle.speed
                                nextVehicle.speed = vehicle.speed
                                vehicle.speed = nextSpeed
                            } else {
                                vehicle.speed = nextVehicle.speed
                            }

                            nextVehicle.collidedBehind = vehicle
                            vehicle.collidedInFront = nextVehicle
                        } else {
                            if(finalVehicle.physicsType != TrackVehicle.PhysicsType.NONE) {
                                finalVehicle.speed = vehicle.speed

                                finalVehicle.collidedBehind!!.collidedInFront = null
                                finalVehicle.collidedBehind = null // TODO: Check if this is correct
                            }

                            vehicle.speed = nextVehicle.speed

                            nextVehicle.collidedBehind = vehicle
                            vehicle.collidedInFront = nextVehicle
                        }

                        val backSecond = nextVehicle.getBack()
                        val frontFirst = vehicle.getFront()
                        val distance = backSecond.getDistanceTo(frontFirst)

                        vehicle.position.move(vehicle.ridingOn, -(distance + 1))
                    }
                }

                if(vehicle.speed < 0) {
                    val previousVehicle = vehicle.getPreviousVehicle()

                    if(previousVehicle != null && previousVehicle.overlaps(vehicle)) {
                        // A collision has occurred
                        val finalVehicle = previousVehicle.getFinalCollidedBehind()

                        if(finalVehicle == previousVehicle) {
                            if(previousVehicle.physicsType != TrackVehicle.PhysicsType.NONE) {
                                val previousSpeed = previousVehicle.speed
                                previousVehicle.speed = vehicle.speed
                                vehicle.speed = previousSpeed
                            } else {
                                vehicle.speed = previousVehicle.speed
                            }

                            previousVehicle.collidedInFront = vehicle
                            vehicle.collidedBehind = previousVehicle
                        } else {
                            if(finalVehicle.physicsType != TrackVehicle.PhysicsType.NONE) {
                                finalVehicle.speed = vehicle.speed

                                finalVehicle.collidedInFront!!.collidedBehind = null
                                finalVehicle.collidedInFront = null
                            }

                            vehicle.speed = previousVehicle.speed

                            previousVehicle.collidedInFront = vehicle
                            vehicle.collidedBehind = previousVehicle
                        }

                        val backSecond = vehicle.getBack()
                        val frontFirst = previousVehicle.getFront()
                        val distance = backSecond.getDistanceTo(frontFirst)

                        vehicle.position.move(vehicle.ridingOn, distance + 1)
                    }
                }
            }

            // Physics calculations
            if(vehicle.physicsType != TrackVehicle.PhysicsType.NONE && vehicle.physicsType != TrackVehicle.PhysicsType.COLLISION_ONLY) {
                // Getting the train pitch
                val directionLocation = Location(vehicle.ridingOn.world, 0.0, 0.0, 0.0)
                directionLocation.direction = back.getPathPoint().asVector().subtract(front.getPathPoint().asVector())
                val pitch = -directionLocation.pitch.toDouble()

                // Calculating acceleration and friction
                var acceleration = 9.81 * sin(Math.toRadians(pitch)) * vehicle.gravityMultiplier
                val normalForce = 9.81 * cos(Math.toRadians(pitch))
                var friction = normalForce * BandiCore.instance.trackManager.frictionCoefficient * vehicle.frictionMultiplier / 20

                // Applying physics types
                if(vehicle.physicsType == TrackVehicle.PhysicsType.UP && pitch > 0) {
                    acceleration = 0.0
                    friction = 0.0
                }

                if(vehicle.physicsType == TrackVehicle.PhysicsType.DOWN && pitch < 0) {
                    acceleration = 0.0
                    friction = 0.0
                }

                // Applying the speeds
                if(acceleration != 0.0 && friction != 0.0) {
                    vehicle.speedMS += acceleration / 20

                    if(vehicle.speedMS > 0) {
                        if(friction > vehicle.speed) friction = vehicle.speed
                        vehicle.speedMS -= friction
                    } else {
                        if(friction < vehicle.speed) friction = vehicle.speed
                        vehicle.speedMS += friction
                    }
                }
            }

            // Updating segments
            if(vehicle.ridingOn.segmentSeparators.size > 1) {
                val currentCurvePoint = vehicle.position.getPathPoint()
                val segmentSeparator = vehicle.ridingOn.getSegmentFromCurvePoint(currentCurvePoint)

                if(segmentSeparator == null) {
                    if(BandiCore.instance.trackManager.vehicleManager.lastSegments.containsKey(vehicle)) {
                        val lastSegment = BandiCore.instance.trackManager.vehicleManager.lastSegments[vehicle]!!
                        lastSegment.vehicles.remove(vehicle)
                        if(lastSegment.type != null) lastSegment.type!!.onVehicleLeave(vehicle)
                        BandiCore.instance.trackManager.vehicleManager.lastSegments.remove(vehicle)
                    }
                } else {
                    val lastSegment = BandiCore.instance.trackManager.vehicleManager.lastSegments[vehicle]

                    if(lastSegment != null) {
                        if(lastSegment == segmentSeparator) {
                            if(lastSegment.type != null) lastSegment.type!!.onVehicleUpdate(vehicle)
                        } else {
                            if(lastSegment.vehicles.contains(vehicle)) lastSegment.vehicles.removeAt(lastSegment.vehicles.indexOf(vehicle))
                            segmentSeparator.vehicles.add(vehicle)

                            if(lastSegment.type != null) lastSegment.type!!.onVehicleLeave(vehicle)
                            if(segmentSeparator.type != null) segmentSeparator.type!!.onVehicleEnter(vehicle)

                            BandiCore.instance.trackManager.vehicleManager.lastSegments[vehicle] = segmentSeparator
                        }
                    } else {
                        segmentSeparator.vehicles.add(vehicle)
                        if(segmentSeparator.type != null) segmentSeparator.type!!.onVehicleEnter(vehicle)
                        BandiCore.instance.trackManager.vehicleManager.lastSegments[vehicle] = segmentSeparator
                    }
                }
            }

            // Updating triggers
            if (vehicle.ridingOn.triggers.isNotEmpty()) {
                val travelledCurve = if(vehicle.speed < 0.0) TrackUtil.getCurveBetweenPositions(vehicle.position, oldPosition) else TrackUtil.getCurveBetweenPositions(oldPosition, vehicle.position)

                for(trigger in vehicle.ridingOn.triggers.filter { it.type != null }) {
                    val curvePoint = trigger.position.getPathPoint()
                    if(travelledCurve.contains(curvePoint)) {
                        trigger.type!!.onActivation(vehicle)
                    }
                }
            }

            // Updating individual members
            var currentSize = 0.0
            for(member in vehicle.members) {
                // Getting the position of the member
                val memberPosition = vehicle.position.clone()
                memberPosition.move(vehicle.ridingOn, totalLength/2.0 - currentSize - member.size/2.0)

                // Updating the currentSize for the next member
                currentSize += member.size

                // Getting the back and front of the member
                val wheelsBack = memberPosition.clone()
                val wheelsFront = memberPosition.clone()

                wheelsBack.move(vehicle.ridingOn, -member.size/2.0)
                wheelsFront.move(vehicle.ridingOn, member.size/2.0)

                val wheelsBackPoint = wheelsBack.nodePosition.curve[wheelsBack.position.toInt()]
                val wheelsFrontPoint = wheelsFront.nodePosition.curve[wheelsFront.position.toInt()]

                // Getting the center of those wheels
                val x = BezierSpline().linear(wheelsBackPoint.x, wheelsFrontPoint.x, 0.5) + vehicle.ridingOn.origin.x
                val y = BezierSpline().linear(wheelsBackPoint.y, wheelsFrontPoint.y, 0.5) + vehicle.ridingOn.origin.y
                val z = BezierSpline().linear(wheelsBackPoint.z, wheelsFrontPoint.z, 0.5) + vehicle.ridingOn.origin.z

                // Roll at current position
                val roll = (wheelsBackPoint.roll + wheelsFrontPoint.roll) / 2.0

                // Preparing pitch and yaw to calculate a rotation quaternion
                val deltaLoc = wheelsFrontPoint.asVector().subtract(wheelsBackPoint.asVector())
                val directionLoc = Location(vehicle.ridingOn.world, 0.0, 0.0, 0.0)
                directionLoc.direction = deltaLoc

                val pitch = directionLoc.pitch.toDouble()
                var yaw = directionLoc.yaw.toDouble()

                // Adjusting yaw to avoid the lock
                if(yaw > 180.0) yaw -= 360.0
                if(yaw == 90.0) yaw = 90.1
                if(yaw == -90.0) yaw = -90.1

                // Calculating the rotation quaternion
                val upVectorQuaternion = Quaternion.fromYawPitchRoll(pitch, yaw, roll)
                val upVector = upVectorQuaternion.upVector()
                val rotationQuaternion = Quaternion.fromLookDirection(deltaLoc, upVector)

                // Updating the attachments themselves
                for(attachment in member.attachments) {
                    attachment.update(Vector(x, y, z), rotationQuaternion.clone(), Vector(pitch, yaw, roll))
                }
            }
        }

//        // Everything before for the collisions
//        for(track in BandiCore.instance.trackManager.loadedTracks) {
//            if(track.getVehicles().size <= 1) continue
//
//            for(vehicle in track.getVehicles()) {
//                if(vehicle.speed == 0.0) continue
//
//                if(vehicle.speed > 0) {
//                    val nextVehicle = vehicle.getNextVehicle() ?: continue
//
//                    if(vehicle.overlaps(nextVehicle)) {
//                        if(nextVehicle.physicsType == TrackVehicle.PhysicsType.NONE) {
//                            vehicle.speed = nextVehicle.speed
//                        } else {
//                            vehicle.speed = vehicle.speed/10.0
//                            nextVehicle.speed += vehicle.speed
//                        }
//
//                        val backSecond = nextVehicle.getBack()
//                        val frontFirst = vehicle.getFront()
//                        val distance = backSecond.getDistanceTo(frontFirst)
//
//                        Bukkit.broadcast(Component.text("backSecond: ${backSecond.nodePosition.id} at ${backSecond.position}"))
//                        Bukkit.broadcast(Component.text("frontFirst: ${frontFirst.nodePosition.id} at ${frontFirst.position}"))
//                        Bukkit.broadcast(Component.text("Position before: " + vehicle.position.nodePosition.id + " at " + vehicle.position.position + ", moving ${-(distance + 2)}"))
//                        vehicle.position.move(vehicle.ridingOn, -(distance + 2))
//                        Bukkit.broadcast(Component.text("Position after: " + vehicle.position.nodePosition.id + " at " + vehicle.position.position))
//                    }
//                } else {
//
//                }
//            }
//        }
    }
}