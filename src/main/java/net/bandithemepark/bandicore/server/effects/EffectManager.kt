package net.bandithemepark.bandicore.server.effects

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit

class EffectManager {
    val playingEffects = mutableListOf<Effect>()

    init {
        startTimer()
    }

    /**
     * Plays all effects that have been listed in the config.yml at server-start-effects
     */
    fun playServerStartEffects() {
        val fm = FileManager()
        val effectNames = fm.getConfig("config.yml").get().getStringList("server-start-effects")

        for(effectName in effectNames) {
            try {
                val effect = Effect(effectName)
                effect.play()
            } catch (e: Exception) {
                Bukkit.getLogger().warning("Could not load effect $effectName on server start. Please remove it from the config.yml if it has been deleted")
            }
        }

        Bukkit.getLogger().info("Loaded ${playingEffects.size} server start effects")
    }

    /**
     * Reloads the config.yml file, stops all effects that were previously playing from server start and starts the new effects
     */
    fun reloadServerStartEffects() {
        val fm = FileManager()
        val beforeEffectNames = fm.getConfig("config.yml").get().getStringList("server-start-effects")

        for(effectName in beforeEffectNames) {
            playingEffects.find { it.fileName == effectName }?.stop()
        }

        fm.reloadConfig("config.yml")
        fm.saveConfig("config.yml")
        val newEffectNames = fm.getConfig("config.yml").get().getStringList("server-start-effects")

        for(effectName in newEffectNames) {
            try {
                val effect = Effect(effectName)
                effect.play()
            } catch (e: Exception) {
                Bukkit.getLogger().warning("Could not load effect $effectName on server start. Please remove it from the config.yml if it has been deleted")
            }
        }
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            playingEffects.toList().forEach { it.tick() }
        }, 0, 1)
    }
}