package net.bandithemepark.bandicore.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

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
}