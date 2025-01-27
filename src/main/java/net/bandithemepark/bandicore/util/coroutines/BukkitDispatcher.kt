package net.bandithemepark.bandicore.util.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import kotlin.coroutines.CoroutineContext

object BukkitDispatcher: CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().runTask(BandiCore.instance, block)
    }
}