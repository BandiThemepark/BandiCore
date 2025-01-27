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
    fun loopAsync(interval: Long, action: suspend () -> Unit) {
        loopAsyncDelayed(interval, 0, action)
    }

    /**
     * Run a task every interval with a delay
     * @param interval The interval in milliseconds
     * @param delay The delay in milliseconds
     * @param action The action to run
     */
    fun loopAsyncDelayed(interval: Long, delay: Long, action: suspend () -> Unit) {
        pluginScope.launch {
            delay(delay)

            while(isActive) {
                action()
                delay(interval)
                yield()
            }
        }
    }
}