package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.minigames.Minigame
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItemHolder
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingProduct
import net.bandithemepark.bandicore.server.minigames.cooking.orders.CookingOrder
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.types.CookingPlaceableSink
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.types.CookingPlaceableStove
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.sendColoredActionBar
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration
import kotlin.random.Random

class CookingGame(val map: CookingMap) {
    companion object {
        const val DURATION_TICKS = 20 * 60 // * 5
    }

    var busy = false
    var currentPlayers = listOf<CookingPlayer>()
    var currentTime = 0
    var points = 0
    var orders = mutableListOf<CookingOrder>()
    var plateSupply = 0

    var returningPlates = mutableListOf<ReturningPlate>()

    fun start(players: List<CookingPlayer>) {
        busy = true
        currentPlayers = players
        currentTime = 0
        points = 0
        orders = mutableListOf()
        plateSupply = map.defaultPlates
        returningPlates = mutableListOf()

        for((index, player) in currentPlayers.withIndex()) {
            player.teleport(map.spawnLocations[index])
            player.inventory.clear()
            player.gameMode = GameMode.ADVENTURE
            player.foodLevel = 5
            player.createBossBar()
            player.inventory.heldItemSlot = 4
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999, 200, false, false))
        }

        map.placeables.forEach {
            it.place(currentPlayers)
            if(it is CookingItemHolder) it.setItem(null)
        }

        // TODO: Update player reach attribute when Minecraft has updated

        addOrder()
        addOrder()
        addOrder()
    }

    fun update() {
        if(!busy) return

        currentTime++

        currentPlayers.forEach { it.updateBossBar() }
        updateOrders()
        updateReturningPlates()

        map.placeables.filterIsInstance<CookingPlaceableStove>().forEach { it.update() }

        if(currentTime >= DURATION_TICKS) {
            end()
        }
    }

    val REACH = 2.0

    var nextOrderIn = 0
    private fun updateOrders() {
        nextOrderIn--
        if(nextOrderIn <= 0) addOrder()

        orders.forEach {
            it.timeLeft--

            if(it.timeLeft <= 0) {
                points -= it.product.latePenalty
                currentPlayers.forEach { player -> player.sendColoredActionBar("<${BandiColors.RED}>Order expired (-${it.product.latePenalty} points)") }
            }
        }

        orders = orders.filter { it.timeLeft > 0 }.toMutableList()
    }

    fun updateReturningPlates() {
        returningPlates.forEach {
            it.timeLeft--
        }

        var platesToReturn = returningPlates.filter { it.timeLeft <= 0 }.size
        addPlateToSink(platesToReturn)
        returningPlates = returningPlates.filter { it.timeLeft > 0 }.toMutableList()
    }

    val MIN_NEW_ORDER_TIME = 100
    val MAX_NEW_ORDER_TIME = 200
    private fun addOrder() {
        val product = map.products.random()
        orders.add(CookingOrder(product))
        nextOrderIn = (Random(1).nextDouble() * (MAX_NEW_ORDER_TIME - MIN_NEW_ORDER_TIME) + MIN_NEW_ORDER_TIME).toInt()
    }

    private fun end() {
        busy = false

        map.placeables.forEach { it.remove() }
        (Minigame.minigames.find { it is CookingMinigame }!! as CookingMinigame).games.find { it.game == this }?.currentPlayers = listOf()

        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
            for(player in currentPlayers) {
                player.reset()
            }

            currentPlayers = listOf()
        })
    }

    fun getTimeLeftString(): String {
        val ticksLeft = DURATION_TICKS - currentTime
        val secondsLeft = ticksLeft / 20
        
        // Format secondsLeft to mm:ss
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    fun onDeliver(product: CookingProduct, player: CookingPlayer) {
        points += product.points

        val firstOrder = orders.sortedByDescending { it.timeLeft }.find { it.product == product }

        if(firstOrder == null) {
            addPlateToSink()
            player.showTitle(Title.title(
                Util.color(""),
                Util.color("<${BandiColors.RED}>There were no orders with this item!"),
                Title.Times.times(
                    Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))))
        } else {
            returningPlates.add(ReturningPlate(20 * 10))
            orders.remove(firstOrder)
            player.showTitle(Title.title(
                Util.color(""),
                Util.color("<${BandiColors.GREEN}>You delivered ${product.item.getName()} (+${product.points} points)"),
                Title.Times.times(
                    Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))))
        }
    }

    fun addPlateToSink(amount: Int = 1) {
        val sink = map.placeables.filterIsInstance<CookingPlaceableSink>()[0]
        sink.plates += amount
        sink.updateProgressBar()
    }

    data class ReturningPlate(var timeLeft: Int) {

    }
}