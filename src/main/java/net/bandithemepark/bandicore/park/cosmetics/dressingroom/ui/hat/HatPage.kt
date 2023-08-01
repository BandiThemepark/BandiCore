package net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.hat

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.StartPage
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.UIButton
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui.UIPage
import org.bukkit.Location
import org.bukkit.entity.Player

class HatPage: UIPage("Hat select") {
    override fun onBack(player: Player) {
        val session = DressingRoomSession.activeSessions.find { it.player == player } ?: return
        session.openPage(StartPage())
    }

    override fun customRender(location: Location, player: Player, yaw: Double) {
        if(buttons.isEmpty()) {

        }
    }

    override fun getButtons(player: Player): List<UIButton> {
        val ownedCosmetics = BandiCore.instance.cosmeticManager.ownedCosmetics.find { it.owner == player }
        return ownedCosmetics!!.ownedCosmetics.map { HatButton(it.cosmetic, it.color, it.amount) }
    }
}