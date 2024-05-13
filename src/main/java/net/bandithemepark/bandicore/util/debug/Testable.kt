package net.bandithemepark.bandicore.util.debug

import org.bukkit.command.CommandSender

interface Testable {
    fun test(sender: CommandSender)

    fun register(id: String) {
        testables[id] = this
    }

    companion object {
        val testables = hashMapOf<String, Testable>()

        fun getTestable(id: String): Testable? {
            return testables[id]
        }
    }
}