package net.bandithemepark.bandicore.util.debug

import org.bukkit.command.CommandSender

interface Debuggable {
    fun debug(sender: CommandSender)

    fun register(id: String) {
        debuggables[id] = this
    }

    companion object {
        val debuggables = hashMapOf<String, Debuggable>()

        fun getDebuggable(id: String): Debuggable? {
            return debuggables[id]
        }
    }
}