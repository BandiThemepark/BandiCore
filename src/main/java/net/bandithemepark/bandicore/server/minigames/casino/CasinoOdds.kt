package net.bandithemepark.bandicore.server.minigames.casino

import net.bandithemepark.bandicore.BandiCore

class CasinoOdds(val odds: List<CasinoOdd>) {
    init {
        validateOdds()
    }

    /**
     * Runs the odds and returns the amount of money the player won
     * @param inputAmount The amount of money the player bet
     * @return The amount of money the player won
     */
    fun play(inputAmount: Int): Int {
        val random = Math.random()
        var currentChance = 0.0

        var winAmount = 0
        for(odd in odds) {
            currentChance += odd.chance
            if(random <= currentChance) {
                winAmount = (inputAmount * odd.multiplier).toInt()
                break
            }
        }

        val jackpot = BandiCore.instance.casino.jackpot
        jackpot.addPlayed(inputAmount, winAmount)

        return winAmount
    }

    private fun validateOdds() {
        // Total chances need to be 100%
        val totalChance = odds.sumOf { it.chance }
        if(totalChance != 1.0) {
            throw InvalidOddsException("Total chance of all odds need to be 100%. Currently it is ${(totalChance * 100).toInt()}%")
        }

        // Average multiplier need to be 0.9
        val averageMultiplier = odds.sumOf { it.multiplier } / odds.size
        if(averageMultiplier != 1.0 - CasinoJackpot.JACKPOT_CUT_PERCENTAGE) {
            throw InvalidOddsException("Average multiplier of all odds need to be ${1.0 - CasinoJackpot.JACKPOT_CUT_PERCENTAGE}. Currently it is $averageMultiplier")
        }
    }
}

class InvalidOddsException(message: String): Exception(message)