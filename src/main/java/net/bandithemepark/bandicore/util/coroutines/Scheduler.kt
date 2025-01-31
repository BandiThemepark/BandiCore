package net.bandithemepark.bandicore.util.coroutines

import kotlinx.coroutines.*
import net.bandithemepark.bandicore.BandiCore.Companion.pluginScope

object Scheduler {
    /**
     * Run a task every interval
     * @param interval The interval in milliseconds
     * @param action The action to run
     */
    fun loopAsync(interval: Long, action: suspend () -> Unit): Job {
        return loopAsyncDelayed(interval, 0, action)
    }

    /**
     * Run a task every interval with a delay
     * @param interval The interval in milliseconds
     * @param delay The delay in milliseconds
     * @param action The action to run
     */
    fun loopAsyncDelayed(interval: Long, delay: Long, action: suspend () -> Unit): Job {
        return pluginScope.launch {
            delay(delay)

            while(isActive) {
                try {
                    action()
                } catch(e: Exception) {
                    e.printStackTrace()
                }

                delay(interval)
                yield()
            }
        }
    }
}