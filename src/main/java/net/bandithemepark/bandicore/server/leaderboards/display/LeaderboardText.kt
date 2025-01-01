package net.bandithemepark.bandicore.server.leaderboards.display

import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.toHexString
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay

/**
 * An instance of a text display in the world, with some default configuration for display on leaderboards
 * The text will update itself when the text property is set.
 */
class LeaderboardText(text: String, val location: Location, val color: Color, val scale: Double, val regionId: String? = null) {
    var textDisplay: PacketTextDisplay? = null

    var text: String = text
        set(value) {
            field = value

            if(!spawned) return
            textDisplay?.setText(Util.color("<${color.toHexString()}>$value"))
            textDisplay?.updateMetadata()
        }

    var spawned = false
    fun spawn() {
        if(spawned) return

        textDisplay = PacketTextDisplay()
        textDisplay!!.spawn(location, regionId)
        textDisplay!!.setText(Util.color("<${color.toHexString()}>$text"))
        textDisplay!!.setBillboard(Display.Billboard.FIXED)
        textDisplay!!.setAlignment(TextDisplay.TextAlignment.CENTER)
        textDisplay!!.setScale(scale)
        textDisplay!!.setBackgroundColor(Color.fromARGB(0, 0, 0, 0))
        textDisplay!!.updateMetadata()

        spawned = true
    }

    fun deSpawn() {
        if(!spawned) return

        textDisplay?.deSpawn()
        textDisplay = null

        spawned = false
    }
}