package net.bandithemepark.bandicore.server.animatronics

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit

class AnimatronicManager {
    val spawnedAnimatronics = mutableListOf<Animatronic>()

    init {
        startTimer()
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            spawnedAnimatronics.forEach { it.tick() }
        }, 0, 1)
    }
}