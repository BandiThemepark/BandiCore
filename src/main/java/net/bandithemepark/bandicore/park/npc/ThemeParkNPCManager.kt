package net.bandithemepark.bandicore.park.npc

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit

class ThemeParkNPCManager {
    val npcs = mutableListOf<ThemeParkNPC>()
    val cache = ThemeParkNPCSkin.Caching()

    init {
        cache.loadCache()
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            npcs.forEach { it.update() }
        }, 0, 1)
    }

    fun spawnAmount(amount: Int) {
        for (i in 0 until amount) {
            val skin = BandiCore.instance.server.themePark.themeParkNPCManager.cache.getRandomUnusedSkin()
            val newNPC = ThemeParkNPC(skin)
            newNPC.spawn()
            npcs.add(newNPC)
        }
    }
}