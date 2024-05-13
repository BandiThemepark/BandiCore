package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.server.effects.types.DarkOverlayEffect
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration

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
                // Increasing the ridecounter
                vehicle.getAllAttachments().filter { it.type is SeatAttachment }.forEach {
                    val passengers = (it.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>()
                    passengers.forEach { passenger ->
                        DarkOverlayEffect.active.forEach { effect ->
                            if(effect.showingFor != null && effect.showingFor!!.contains(passenger)) {
                                passenger.showTitle(
                                    Title.title(
                                        Component.text("\uE021"), Util.color(""), Title.Times.times(
                                            Duration.ofMillis(0L), Duration.ofMillis(0L), Duration.ofMillis(effect.fadeOutTime * 50L))))
                            }
                        }

                        BandiCore.instance.server.ridecounterManager.increase(passenger, attraction.id) {
                            val newCount = BandiCore.instance.server.ridecounterManager.getRidecountOnOf(passenger, attraction.id)
                            passenger.sendTranslatedMessage("ridecounter-increased", BandiColors.YELLOW.toString(), MessageReplacement("attraction", attraction.appearance.displayName), MessageReplacement("count", newCount.toString()))
                        }
                    }
                }

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