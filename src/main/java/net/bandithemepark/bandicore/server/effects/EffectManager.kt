package net.bandithemepark.bandicore.server.effects

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.debug.Reloadable
import org.bukkit.Bukkit

class EffectManager: Reloadable {
    val playingEffects = mutableListOf<Effect>()

    init {
        startTimer()
        register("server-start-effects")
    }

    /**
     * Plays all effects that have been listed in the config.json at server-start-effects
     */
    fun playServerStartEffects() {
        val effectNames = BandiCore.instance.config.json.getAsJsonArray("server-start-effects").map { it.asString }

        for(effectName in effectNames) {
            try {
                val effect = Effect(effectName, null)
                effect.play()
            } catch (e: Exception) {
                Bukkit.getLogger().warning("Could not load effect $effectName on server start. Please remove it from the config.json if it has been deleted")
            }
        }

        Bukkit.getLogger().info("Loaded ${playingEffects.size} server start effects")
    }

    /**
     * Reloads the config.json file, stops all effects that were previously playing from server start and starts the new effects
     */
    fun reloadServerStartEffects() {
        val beforeEffectNames = BandiCore.instance.config.json.getAsJsonArray("server-start-effects").map { it.asString }

        for(effectName in beforeEffectNames) {
            playingEffects.find { it.fileName == effectName }?.stop()
        }

        BandiCore.instance.config.reload()
        playServerStartEffects()
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            playingEffects.toList().forEach { it.tick() }
        }, 0, 1)
    }

    override fun reload() {
        reloadServerStartEffects()
    }
}