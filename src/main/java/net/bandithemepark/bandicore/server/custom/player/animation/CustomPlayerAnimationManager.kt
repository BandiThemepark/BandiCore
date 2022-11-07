package net.bandithemepark.bandicore.server.custom.player.animation

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.animation.Animation
import org.bukkit.scheduler.BukkitRunnable

class CustomPlayerAnimationManager {
    val loadedAnimations = mutableMapOf<String, Animation>()

    fun loadAnimation(id: String): Animation {
        if(loadedAnimations.contains(id)) return loadedAnimations[id]!!
        loadedAnimations[id] = Animation.load(id)
        return loadedAnimations[id]!!
    }

    val activeAnimations = mutableListOf<CustomPlayerAnimation>()

    fun update() {
        val toRemove = mutableListOf<CustomPlayerAnimation>()

        for(animation in activeAnimations) {
            animation.update()

            if(animation.currentTick >= animation.animation.getLength()) {
                if(animation.loop) {
                    animation.currentTick = 0
                } else {
                    toRemove.add(animation)
                }
            }
        }
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