package net.bandithemepark.bandicore.server.animatronics

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.coroutines.Scheduler
import org.bukkit.Bukkit

class AnimatronicManager {
    val spawnedAnimatronics = mutableListOf<Animatronic>()

    init {
        startTimer()
    }

    private fun startTimer() {
        Scheduler.loopAsync(50) {
            if(spawnedAnimatronics.isNotEmpty()) {
                spawnedAnimatronics.forEach { it.tick() }
                spawnedAnimatronics.removeIf { it.queuedForDeSpawn }
            }
        }
    }
}