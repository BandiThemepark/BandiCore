package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCook
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCutting
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceableProgressable
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay

class CookingPlaceableCuttingBoard(location: Location): CookingPlaceableProgressable(location, ItemFactory(Material.CRAFTING_TABLE).build()) {
    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return player.game.map.recipes.filterIsInstance<CookingRecipeCutting>().any { it.input.contains(item) }
    }

    override fun canTake(): Boolean {
        return true
    }

    var recipe: CookingRecipeCutting? = null
    var currentProgress = 0
    var lastCut: Long = 0
    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {
        if(action == Action.PLACE || action == Action.SWAP) {
            currentProgress = 0
            recipe = player.game.map.recipes.filterIsInstance<CookingRecipeCutting>().find { it.input.contains(item) }
            updateProgressBar()
        }
    }

    val minTimeBetweenCutsMillis = 250
    override fun onLeftClick(player: CookingPlayer) {
        if(recipe == null) return
        if(System.currentTimeMillis() - lastCut < minTimeBetweenCutsMillis) return

        if(currentProgress < recipe!!.amountOfCuts) {
            currentProgress++
            lastCut = System.currentTimeMillis()
            updateProgressBar()

            if(currentProgress >= recipe!!.amountOfCuts) {
                setItem(recipe!!.result)
            }
        }
    }

    fun updateProgressBar() {
        if(recipe == null || currentProgress >= recipe!!.amountOfCuts) {
            textDisplay.setText(Util.color(""))
        } else {
            var text = "<#32a852>"

            val progress = currentProgress.toDouble() / recipe!!.amountOfCuts.toDouble()
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