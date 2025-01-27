package net.bandithemepark.bandicore.util.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import net.bandithemepark.bandicore.BandiCore.Companion.pluginScope

object Scheduler {
    /**
     * Run a task every interval
     * @param interval The interval in milliseconds
     * @param action The action to run
     */
    fun loopAsync(interval: Long, action: () -> Unit) {
        pluginScope.launch {
            while(isActive) {
                action()
                delay(interval)
                yield()
            }
        }
    }
}