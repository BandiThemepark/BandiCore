package net.bandithemepark.bandicore.park.npc

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.coroutines.Scheduler
import org.bukkit.Bukkit

class ThemeParkNPCManager {
    val npcs = mutableListOf<ThemeParkNPC>()
    val cache = ThemeParkNPCSkin.Caching()

    init {
        cache.loadCache()
        Scheduler.loopAsync(50) {
            npcs.forEach { it.update() }
        }
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