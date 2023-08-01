package net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.hat

import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.UIButton
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.joml.Matrix4f

class HatButton(val cosmetic: Cosmetic, val color: Color?, val amount: Int): UIButton() {
    var itemDisplay: PacketItemDisplay? = null
    var textDisplay: PacketTextDisplay? = null

    override fun onClick(player: Player) {
        Bukkit.broadcast(Component.text("Selected hat "))
    }

    override fun render(location: Location, player: Player, transform: Matrix4f) {
        itemDisplay = PacketItemDisplay()
        itemDisplay!!.spawn(location)
        itemDisplay!!.setItemStack(cosmetic.type.getDressingRoomItem(player, color, cosmetic))
        itemDisplay!!.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay!!.setTransformationMatrix((transform.clone() as Matrix4f).scale(0.5f).rotate(Quaternion.fromYawPitchRoll(40.0, -10.0, 0.0).toBukkitQuaternion()))
        itemDisplay!!.updateMetadata()

        textDisplay = PacketTextDisplay()
        textDisplay!!.spawn(location)
        textDisplay!!.setBackgroundColor(Color.fromARGB(0, 0, 0, 0))
        val text = if (amount > 1) "${amount}x ${cosmetic.displayName}" else cosmetic.displayName
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