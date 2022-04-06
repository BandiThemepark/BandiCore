package net.bandithemepark.bandicore.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import org.bukkit.util.Vector
import kotlin.math.sqrt

object Util {

    // Documentation for MiniMessage format can be found at https://docs.adventure.kyori.net/minimessage/format.html
    // Simply use <#FFFFFF>, <u> Underlined, <b> Bold, <i> Italic, <st> Strikethrough, <obf> Obfuscated, <newline> Newline
    // All tags can optionally be ended
    // There are also tags for gradients, transitions, hover events, and click events
    fun color(message: String): Component {
        return MiniMessage.miniMessage().deserialize(message)
    }

    fun Player.sendColoredMessage(message: String) {
        this.sendMessage(color(message))
    }

    fun Player.sendColoredActionBar(message: String) {
        this.sendActionBar(color(message))
    }

    fun getTabCompletions(currentArg: String, options: List<String>): MutableList<String> {
        val completions = mutableListOf<String>()
        StringUtil.copyPartialMatches(currentArg, options, completions)
        completions.sort()
        return completions
    }

    fun getLengthBetween(loc1: Location, loc2: Location): Double {
        val x = loc2.x - loc1.x
        val y = loc2.y - loc1.y
        val z = loc2.z - loc1.z
        val one = sqrt(x * x + z * z)
        return sqrt(one * one + y * y)
    }

    fun getLengthBetween(loc1: Vector, loc2: Vector): Double {
        val x = loc2.x - loc1.x
        val y = loc2.y - loc1.y
        val z = loc2.z - loc1.z
        val one = sqrt(x * x + z * z)
        return sqrt(one * one + y * y)
    }
}