package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingGame
import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import java.time.Duration

class CookingPlaceableElevator(location: Location): CookingPlaceable(location, ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(20).build()) {
    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem == null) return
        player.game.currentPlayers.forEach {
            it.playSound(player.location, "cooking.deliver", 1f, 1f)
        }

        val product = player.game.map.products.find { it.item == player.currentItem }

        if(product == null) {
            if(player.currentItem.toString().contains("PLATE")) player.game.returningPlates.add(CookingGame.ReturningPlate(20 * 10))

            player.showTitle(
                Title.title(
                Util.color(""),
                Util.color("<${BandiColors.RED}>There were no orders with this item!"),
                Title.Times.times(
                    Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))))
            player.setItem(null)

            return
        }

        player.game.onDeliver(product, player)
        player.setItem(null)
    }

    override fun onLeftClick(player: CookingPlayer) {

    }
}