package net.bandithemepark.bandicore.server.minigames.casino

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import org.bukkit.entity.Player

class CasinoOdds(val odds: List<CasinoOdd>) {
    init {
        validateOdds()
    }

    /**
     * Runs the odds and returns the amount of money the player won.
     * Automatically changes player balance and adds the played data to the jackpot.
     * @param inputAmount The amount of money the player bet
     * @return The won odd
     */
    fun play(player: Player, inputAmount: Int): CasinoOdd {
        val random = Math.random()
        var currentChance = 0.0

        var winAmount = 0
        var winOdd = odds[0]
        for(odd in odds) {
            currentChance += odd.chance
            if(random <= currentChance) {
                if(odd.isJackpot) {
                    val jackpot = BandiCore.instance.casino.jackpot
                    jackpot.addPlayed(inputAmount, 0)
                    jackpot.winJackpot(player)
                    return odd
                }

                winAmount = (inputAmount * odd.multiplier).toInt()
                winOdd = odd
                break
            }
        }

        val jackpot = BandiCore.instance.casino.jackpot
        jackpot.addPlayed(inputAmount, winAmount)

        val balanceChange = winAmount - inputAmount
        CoinManager.setLoadedBalance(player, player.getBalance() + balanceChange)
        CoinManager.saveBalance(player)

        return winOdd
    }

    private fun validateOdds() {
        // Total chances need to be 100%
        val totalChance = odds.sumOf { it.chance }
        if(totalChance != 1.0) {
            throw InvalidOddsException("Total chance of all odds need to be 100%. Currently it is ${(totalChance * 100).toInt()}%")
        }

        // Average multiplier need to be 0.9
        val averageMultiplier = odds.filter { !it.isJackpot }.sumOf { it.multiplier } / odds.size

        // Calculate how much you should lower one of the multipliers to get the average multiplier to 0.9
        val multiplierDifference = 1.0 - CasinoJackpot.JACKPOT_CUT_PERCENTAGE - averageMultiplier
        val multiplierDifferencePerOdd = multiplierDifference * odds.filter { !it.isJackpot }.size

        if(averageMultiplier != 1.0 - CasinoJackpot.JACKPOT_CUT_PERCENTAGE) {
            throw InvalidOddsException("Average multiplier of all odds need to be ${1.0 - CasinoJackpot.JACKPOT_CUT_PERCENTAGE}. Currently it is $averageMultiplier. You should change one of them by $multiplierDifferencePerOdd")
        }
    }
}

class InvalidOddsException(message: String): Exception(message)