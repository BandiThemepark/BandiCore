package net.bandithemepark.bandicore.server.animation.rig

import net.bandithemepark.bandicore.server.animation.Animation

class RigAnimation(val animation: Animation, val looped: Boolean = false, val rig: Rig) {
    var currentTick = 0

    fun update() {
        currentTick++

        rig.setToAnimationAt(animation, currentTick)
        rig.update()
    }
}