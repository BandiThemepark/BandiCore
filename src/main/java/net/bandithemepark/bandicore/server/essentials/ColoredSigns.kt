package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.getText
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

class ColoredSigns: Listener {
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
            val sign = event.block.state as Sign

            if(event.line(0) != null) sign.line(0, Util.legacyColor(event.line(0)!!.getText()))
            if(event.line(1) != null) sign.line(1, Util.legacyColor(event.line(1)!!.getText()))
            if(event.line(2) != null) sign.line(2, Util.legacyColor(event.line(2)!!.getText()))
            if(event.line(3) != null) sign.line(3, Util.legacyColor(event.line(3)!!.getText()))

            sign.update()
        })
    }
}