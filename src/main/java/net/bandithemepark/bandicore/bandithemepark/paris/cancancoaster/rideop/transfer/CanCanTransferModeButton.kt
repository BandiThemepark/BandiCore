package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.transfer

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SwitchButton
import org.bukkit.entity.Player

class CanCanTransferModeButton: SwitchButton(0, "rideop-cancan-transfer-mode-title", "rideop-cancan-transfer-mode-description") {
    override fun isActivated(): Boolean {
        return (rideOP as CanCanRideOP).transferMode
    }

    override fun onClick(player: Player) {
        val rideOP = rideOP as CanCanRideOP

        if(!rideOP.transferMode) {
            if(rideOP.canEnableTransferMode()) {
                rideOP.enableTransferMode()
                rideOP.updateMenu()
            }
        } else {
            if(rideOP.canDisableTransferMode()) {
                rideOP.disableTransferMode()
                rideOP.updateMenu()
            }
        }
    }

}