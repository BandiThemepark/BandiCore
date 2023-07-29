package net.bandithemepark.bandicore.park.attractions.rideop.camera

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPButton
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RideOPCameraButton(slot: Int, val text: String, val location: Location, parentRideOP: RideOP): RideOPButton(slot) {
    val camera: RideOPCamera = RideOPCamera(location, parentRideOP)

    init {
        camera.setup()
    }

    override fun onClick(player: Player) {
        if(RideOPCamera.activeCameras.any { it.currentPlayer == player && it == camera }) return

        camera.startView(player)
        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { player.closeInventory() } )
    }

    override fun getItemStack(player: Player): ItemStack {
        return ItemFactory(Material.DIAMOND_SHOVEL)
            .setCustomModelData(10)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>$text"))
            .setLore(0, Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage("rideop-camera-description")}"))
            .build()
    }

    override fun getJSON(): JsonObject {
        val json = JsonObject()
        json.addProperty("type", "camera")
        json.addProperty("text", text)
        return json
    }
}