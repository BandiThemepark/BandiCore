package net.bandithemepark.bandicore.park.effect

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

abstract class AmbientEffect {
    abstract fun onTick()
    fun register() {
        active.add(this)
    }

    companion object {
        val active = mutableListOf<AmbientEffect>()

        fun startTimer() {
            object: BukkitRunnable() {
                override fun run() {
                    active.forEach { it.onTick() }
                }
            }.runTaskTimerAsynchronously(BandiCore.instance, 0, 1)
        }
    }
}