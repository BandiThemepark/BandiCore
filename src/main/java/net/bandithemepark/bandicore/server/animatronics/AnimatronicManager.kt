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
            if(spawnedAnimatronics.isNotEmpty()) {
                spawnedAnimatronics.forEach { it.tick() }
                spawnedAnimatronics.removeIf { it.queuedForDeSpawn }
            }
        }, 0, 1)
    }
}