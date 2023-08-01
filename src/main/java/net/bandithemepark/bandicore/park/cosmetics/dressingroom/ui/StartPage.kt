package net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui

import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.hat.HatPage
import org.bukkit.Location
import org.bukkit.entity.Player

class StartPage: UIPage("Select category") {
    override fun onBack(player: Player) {
        DressingRoomSession.activeSessions.find { it.player == player }!!.exit()
    }

    override fun customRender(location: Location, player: Player, yaw: Double) {

    }

    override fun getButtons(player: Player): List<UIButton> {
        return listOf(
            StartPageButton("Hat", HatPage()),
            StartPageButton("Handheld", HatPage()),
            StartPageButton("Balloon", HatPage()),
            StartPageButton("Chestplate", HatPage()),
            StartPageButton("Leggings", HatPage()),
            StartPageButton("Boots", HatPage()),
            StartPageButton("Title", HatPage()),
            StartPageButton("Karts", HatPage()),
            StartPageButton("Coinbooster?", HatPage())
        )
    }
}