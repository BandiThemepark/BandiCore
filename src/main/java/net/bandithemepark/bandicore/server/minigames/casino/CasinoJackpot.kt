package net.bandithemepark.bandicore.server.minigames.casino

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import net.bandithemepark.bandicore.util.FileUtil
import net.bandithemepark.bandicore.util.Util.sendColoredMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CasinoJackpot {
    companion object {
        const val JACKPOT_CUT_PERCENTAGE = 0.1
    }

    var jackpot = 0
    var incoming = 0
    var outgoing = 0

    init {
        setupDefaultConfig()

        val json = FileUtil.loadJsonFrom("plugins/BandiCore/casino/jackpot.json")
        jackpot = json.get("jackpot").asInt
        incoming = json.get("incoming").asInt
        outgoing = json.get("outgoing").asInt
    }

    /**
     * When a player plays using the casino odds system, their playing data is added here.
     * @param inputAmount The amount of money the player bet
     * @param wonAmount The amount of money the player won
     */
    fun addPlayed(inputAmount: Int, wonAmount: Int) {
        incoming += inputAmount
        outgoing += wonAmount
        jackpot += (inputAmount * JACKPOT_CUT_PERCENTAGE).toInt()
        saveJackpot()
    }

    /**
     * Saves the jackpot to the jackpot.json file
     */
    fun saveJackpot() {
        val json = JsonObject()
        json.addProperty("jackpot", jackpot)
        json.addProperty("incoming", incoming)
        json.addProperty("outgoing", outgoing)

        FileUtil.saveToFile(json, "plugins/BandiCore/casino/jackpot.json")
    }

    private fun setupDefaultConfig() {
        if(FileUtil.doesFileExist("plugins/BandiCore/casino/jackpot.json")) return

        val json = JsonObject()
        json.addProperty("jackpot", 0)
        json.addProperty("incoming", 0)
        json.addProperty("outgoing", 0)

        FileUtil.saveToFile(json, "plugins/BandiCore/casino/jackpot.json")
    }

    /**
     * When a player wins the jackpot, this function is called.
     * @param player The player who won the jackpot
     */
    fun winJackpot(player: Player) {
        val wonAmount = jackpot
        jackpot = 0

        CoinManager.setLoadedBalance(player, player.getBalance() + wonAmount)
        CoinManager.saveBalance(player)

        Bukkit.getOnlinePlayers().forEach {
            it.sendColoredMessage(" ")
            it.sendColoredMessage("<gradient:#E89C31:#DBA858>JACKPOT WINNER!</gradient>")
            it.sendColoredMessage("<#DBA858>${player.name} has won the jackpot of <#8C0E0F>$wonAmount <#DBA858>coins!")
            it.sendColoredMessage(" ")
        }
    }
}