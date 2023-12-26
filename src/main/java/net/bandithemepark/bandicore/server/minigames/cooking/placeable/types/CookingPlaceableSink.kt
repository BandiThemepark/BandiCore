package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableProgressable
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay

class CookingPlaceableSink(location: Location): CookingPlaceableProgressable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(18).build()) {
    var plates = 0

    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return false
    }

    override fun canTake(): Boolean {
        return false
    }

    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {

    }

    val minTimeBetweenCutsMillis = 400
    val amountOfClicks = 5

    var lastClick = 0L
    var currentProgress = 0

    override fun onLeftClick(player: CookingPlayer) {
        if(plates == 0) return
        if(System.currentTimeMillis() - lastClick < minTimeBetweenCutsMillis) return

        if(currentProgress < amountOfClicks) {
            currentProgress++
            lastClick = System.currentTimeMillis()

            if(currentProgress >= amountOfClicks) {
                currentProgress = 0
                plates--
                player.game.map.placeables.filterIsInstance<CookingPlaceableWashedPlates>()[0].plates++
            }
        }
    }

    fun updateProgressBar() {
        if(plates == 0) {
            textDisplay.setText(Util.color(""))
        } else {
            var text = "<#32a852>"

            val progress = currentProgress.toDouble() / amountOfClicks.toDouble()
            val progressBars = (progress * 10).toInt()
            val unfilled = 10 - progressBars

            for(i in 0 until progressBars) {
                text += "|"
            }

            text += "<${BandiColors.LIGHT_GRAY}>"

            for(i in 0 until unfilled) {
                text += "|"
            }

            textDisplay.setText(Util.color(text))
        }

        textDisplay.updateMetadata()
    }

    lateinit var textDisplay: PacketTextDisplay
    override fun place(players: List<CookingPlayer>) {
        super.place(players)

        textDisplay = PacketTextDisplay()
        textDisplay.visibilityType = PacketEntity.VisibilityType.WHITELIST
        textDisplay.visibilityList = players.toMutableList()
        textDisplay.spawn(location.clone().add(0.0, 1.8, 0.0))
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER)
        textDisplay.setBillboard(Display.Billboard.CENTER)
        updateProgressBar()
    }

    override fun remove() {
        super.remove()

        textDisplay.deSpawn()
    }
}