package net.bandithemepark.bandicore.server.minigames.casino.slotmachine

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.minigames.casino.CasinoOdd
import net.bandithemepark.bandicore.server.minigames.casino.CasinoOdds
import net.bandithemepark.bandicore.util.Util.sendColoredActionBar
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class SlotMachine(val amount: Int, val origin: Location) {
    lateinit var player: Player
    fun play(player: Player) {
        this.player = player
        timer.runTaskTimer(BandiCore.instance, 0, 1)
        activeOrigins.add(origin)
    }

    var timer = object: BukkitRunnable() {
        override fun run() {
            update()
        }
    }

    var currentTick = 0
    fun update() {
        currentTick++

        if(currentTick % 2 == 0) {
            setRandomBlock()
        }

        if(currentTick == 80) {
            currentTick = 0
            end()
        }
    }

    fun setRandomBlock() {
        val randomId = blocks.keys.random()
        setBlock(blocks[randomId]!!)
        player.playSound(origin, Sound.UI_BUTTON_CLICK, 1f, 1f)
    }

    fun setBlock(material: Material) {
        origin.clone().add(0.0, 2.0, 0.0).block.type = material
    }

    fun end() {
        timer.cancel()
        val odd = ODDS.play(player, amount)
        setBlock(blocks[odd.id]!!)
        player.sendColoredActionBar(messages[odd.id]!!.replace("{amount}", (amount * odd.multiplier).toInt().toString()))
        activeOrigins.remove(origin)
    }

    companion object {
        val ODDS = CasinoOdds(listOf(
            CasinoOdd("lose", 0.0, 0.5, false),
            CasinoOdd("jackpot", 0.0, 0.001, true),
            CasinoOdd("x2", 2.7, 0.499, false),
        ))

        val blocks = hashMapOf<String, Material>(
            "lose" to Material.RED_WOOL,
            "jackpot" to Material.DIAMOND_BLOCK,
            "x2" to Material.GOLD_BLOCK,
        )

        val messages = hashMapOf<String, String>(
            "lose" to "<${BandiColors.RED}>You lost!",
            "jackpot" to "<${BandiColors.YELLOW}>You won the jackpot!",
            "x2" to "<${BandiColors.YELLOW}>You won {amount}",
        )

        val activeOrigins = mutableListOf<Location>()
    }
}