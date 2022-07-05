package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.EStopButton
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.title.Title
import java.time.Duration

class RupsbaanEStopButton: EStopButton(13) {
    override fun setActive(active: Boolean) {
        (rideOP as RupsbaanRideOP).ride.eStop = active

        if(active) {
            for(cart in (rideOP as RupsbaanRideOP).ride.carts) {
                if((cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    (cart.seat1Attachment.type as SeatAttachment).seat!!.getPassengers()[0].showTitle(Title.title(
                        Util.color("<${BandiColors.RED}>E-Stop activated"),
                        Util.color("<${BandiColors.RED}>Please remain seated until harnesses open"),
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(5),
                            Duration.ofSeconds(1)
                        )
                    ))
                }

                if((cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers().isNotEmpty()) {
                    (cart.seat2Attachment.type as SeatAttachment).seat!!.getPassengers()[0].showTitle(Title.title(
                        Util.color("<${BandiColors.RED}>E-Stop activated"),
                        Util.color("<${BandiColors.RED}>Please remain seated until harnesses open"),
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(5),
                            Duration.ofSeconds(1)
                        )
                    ))
                }
            }
        }
    }

    override fun isActive(): Boolean {
        return (rideOP as RupsbaanRideOP).ride.eStop
    }
}