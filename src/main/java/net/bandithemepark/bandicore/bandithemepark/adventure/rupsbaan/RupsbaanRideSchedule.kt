package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.network.audioserver.ride.SpecialAudioManagement
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.math.MathUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class RupsbaanRideSchedule(val ride: Rupsbaan, val keyframes: HashMap<Int, Double>) {
    val endTime = keyframes.keys.maxOf { it }

    fun getSpeedAt(ticks: Int): Double {
         // find the biggest key in keyframes that is smaller then ticks
        var biggestKey = 0
        for (key in keyframes.keys) {
            if(key in (biggestKey + 1)..ticks) {
                biggestKey = key
            }
        }

        // find the smallest key that is bigger than ticks
        var smallestKey = Int.MAX_VALUE
        for (key in keyframes.keys) {
            if(key in (ticks + 1) until smallestKey) {
                smallestKey = key
            }
        }

        val progress = (ticks - smallestKey).toDouble() / (biggestKey - smallestKey).toDouble()
        val speed = MathUtil.lerp(keyframes.getOrDefault(smallestKey, 0.0), keyframes.getOrDefault(biggestKey, 0.0), progress)
        return (speed / 10.0) * RupsbaanRideOP.topSpeed
    }

    fun update() {
        if(!active) return

        if(currentTicks > endTime) {
            active = false
            ride.currentSpeed = 0.0

            if((RideOP.get("rupsbaan") as RupsbaanRideOP).operator == null) {
                ride.carts.forEach { it.startUpwardsInterpolation() }
            }

            for(cart in ride.carts) {
                if((cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    val passengers = (cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
                    (cart.seat1Attachment.type as SeatAttachment).seat!!.ejectPassengers()
                    passengers.forEach { player -> SpecialAudioManagement.stopOnrideAudio(player) }

                    // Increasing the ridecounter
                    passengers.forEach { passenger ->
                        BandiCore.instance.server.ridecounterManager.increase(passenger, "rupsbaan") {
                            val newCount = BandiCore.instance.server.ridecounterManager.getRidecountOnOf(passenger, "rupsbaan")
                            passenger.sendTranslatedMessage("ridecounter-increased", BandiColors.YELLOW.toString(), MessageReplacement("attraction", Attraction.get("rupsbaan")!!.appearance.displayName), MessageReplacement("count", newCount.toString()))
                        }
                    }

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        passengers.forEach { player ->
                            (cart.seat1Attachment.type as SeatAttachment).deSpawnCustomPlayer(player)
                            SeatAttachment.show(player)
                            player.getNameTag()?.hidden = false
                        }
                    })
                }

                if((cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    val passengers = (cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
                    (cart.seat2Attachment.type as SeatAttachment).seat!!.ejectPassengers()
                    passengers.forEach { player -> SpecialAudioManagement.stopOnrideAudio(player) }

                    // Increasing the ridecounter
                    passengers.forEach { passenger ->
                        BandiCore.instance.server.ridecounterManager.increase(passenger, "rupsbaan") {
                            val newCount = BandiCore.instance.server.ridecounterManager.getRidecountOnOf(passenger, "rupsbaan")
                            passenger.sendTranslatedMessage("ridecounter-increased", BandiColors.YELLOW.toString(), MessageReplacement("attraction", Attraction.get("rupsbaan")!!.appearance.displayName), MessageReplacement("count", newCount.toString()))
                        }
                    }

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        passengers.forEach { player ->
                            (cart.seat2Attachment.type as SeatAttachment).deSpawnCustomPlayer(player)
                            SeatAttachment.show(player)
                            player.getNameTag()?.hidden = false
                        }
                    })
                }
            }
            return
        }

        if(ride.eStop) {
            ride.currentSpeed -= 0.1

            if(ride.currentSpeed <= 0.0) {
                ride.currentSpeed = 0.0
                active = false
            }

            return
        }

        currentTicks++
        ride.currentSpeed = getSpeedAt(currentTicks)
    }

    var active = false
    var currentTicks = 0
    fun start() {
        // Get all players and play audio for them
        val players = mutableListOf<Player>()
        ride.carts.forEach { cart ->
            if((cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                players.addAll((cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>())
            }

            if((cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                players.addAll((cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>())
            }
        }

        players.forEach { player ->
            SpecialAudioManagement.playOnrideAudio(player, "52d53375-2598-11ee-a1ee-0242ac1d0002", 0)
        }

        active = true
        currentTicks = 0
    }
}