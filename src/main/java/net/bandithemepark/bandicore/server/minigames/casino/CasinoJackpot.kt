package net.bandithemepark.bandicore.server.minigames.casino

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.util.FileUtil

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
}