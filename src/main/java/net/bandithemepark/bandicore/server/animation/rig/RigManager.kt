package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animation.Animation
import net.bandithemepark.bandicore.server.custom.player.animation.CustomPlayerAnimation
import org.bukkit.scheduler.BukkitRunnable

class RigManager {
    val loadedAnimations = mutableMapOf<String, Animation>()

    fun loadAnimation(id: String): Animation {
        if(loadedAnimations.contains(id)) return loadedAnimations[id]!!
        loadedAnimations[id] = Animation.load(id)
        return loadedAnimations[id]!!
    }

    val activeAnimations = mutableListOf<RigAnimation>()

    fun update() {
        val toRemove = mutableListOf<RigAnimation>()

        for(animation in activeAnimations) {
            animation.update()

            if(animation.currentTick >= animation.animation.getLength()) {
                if(animation.looped) {
                    animation.currentTick = 0
                } else {
                    toRemove.add(animation)
                }
            }
        }

        activeAnimations.removeAll(toRemove)
    }


    init {
        startTimer()
    }

    fun startTimer() {
        object: BukkitRunnable(){
            override fun run() {
                update()
            }
        }.runTaskTimerAsynchronously(BandiCore.instance, 0, 1)
    }
}