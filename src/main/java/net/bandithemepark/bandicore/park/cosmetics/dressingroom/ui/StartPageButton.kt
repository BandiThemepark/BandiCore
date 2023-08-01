package net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui

import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.joml.Matrix4f

class StartPageButton(val text: String, val pageToOpen: UIPage): UIButton() {
    var itemDisplay: PacketItemDisplay? = null
    var textDisplay: PacketTextDisplay? = null

    override fun onClick(player: Player) {
        val session = DressingRoomSession.activeSessions.find { it.player == player } ?: return
        session.openPage(pageToOpen)
    }

    override fun render(location: Location, player: Player, transform: Matrix4f) {
        itemDisplay = PacketItemDisplay()
        itemDisplay!!.spawn(location)
        itemDisplay!!.setItemStack(ItemFactory(Material.PAPER).setCustomModelData(1021).build())
        itemDisplay!!.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay!!.setTransformationMatrix(transform)
        itemDisplay!!.updateMetadata()

        textDisplay = PacketTextDisplay()
        textDisplay!!.spawn(location)
        textDisplay!!.setBackgroundColor(Color.fromARGB(0, 0, 0, 0))
        textDisplay!!.setText(Util.color(text))
        textDisplay!!.setAlignment(TextDisplay.TextAlignment.CENTER)
        textDisplay!!.setTransformationMatrix((transform.clone() as Matrix4f).translate(0.0f, -0.4f, 0.2f).scale(0.4f))
        textDisplay!!.updateMetadata()
    }

    override fun remove(player: Player) {
        itemDisplay?.deSpawn()
        itemDisplay = null

        textDisplay?.deSpawn()
        textDisplay = null
    }

    override fun onSelect(player: Player) {
        itemDisplay?.startGlowFor(player)
    }

    override fun onDeSelect(player: Player) {
        itemDisplay?.endGlowFor(player)
    }
}