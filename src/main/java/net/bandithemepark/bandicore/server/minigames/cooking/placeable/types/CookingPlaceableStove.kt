package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCook
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

class CookingPlaceableStove(location: Location): CookingPlaceableProgressable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(22).build()) {
    var currentProgress = 0
    var burning = false
    var recipe: CookingRecipeCook? = null
    var lastPlayer: CookingPlayer? = null
    override fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action) {
        if(action == Action.PLACE || action == Action.SWAP) {
            currentProgress = 0
            burning = false
            recipe = player.game.map.recipes.filterIsInstance<CookingRecipeCook>().find { it.input.contains(item) }
            lastPlayer = player
        }

        if(action == Action.TAKE) {
            currentProgress = 0
            burning = false
            recipe = null
        }
    }

    override fun onLeftClick(player: CookingPlayer) {

    }

    override fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean {
        return player.game.map.recipes.filterIsInstance<CookingRecipeCook>().any { it.input.contains(item) }
    }

    override fun canTake(): Boolean {
        return true
    }

    fun update() {
        if(recipe != null) {
            if(!burning) {
                currentProgress++

                if(currentProgress >= recipe!!.maxProgress) {
                    burning = true
                    setItem(recipe!!.result)
                    currentProgress = 0
                    lastPlayer!!.game.currentPlayers.forEach {
                        it.playSound(lastPlayer!!.location, "cooking.burning", 1f, 1f)
                    }
                }
            } else {
                currentProgress++

                if(currentProgress >= recipe!!.burnTime) {
                    burning = false
                    setItem(recipe!!.burned)
                    currentProgress = 0
                    recipe = null
                }
            }

            updateProgressBar()
        }
    }

    fun updateProgressBar() {
        if(recipe == null) {
            textDisplay.setText(Util.color(""))
        } else if(!burning) {
            var text = "<#32a852>"

            val progress = currentProgress.toDouble() / recipe!!.maxProgress.toDouble()
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
        } else {
            var text = "<${BandiColors.RED}>"

            val progress = currentProgress.toDouble() / recipe!!.burnTime.toDouble()
            val progressBars = (progress * 10).toInt()
            val unfilled = 10 - progressBars

            for(i in 0 until progressBars) {
                text += "|"
            }

            text += "<#32a852>"

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