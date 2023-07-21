package net.bandithemepark.bandicore.server.effects

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit

class EffectManager {
    val playingEffects = mutableListOf<Effect>()

    init {
        startTimer()
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            playingEffects.toList().forEach { it.tick() }
        }, 0, 1)
    }
}