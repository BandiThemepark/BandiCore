package net.bandithemepark.bandicore.util.chat.prompt

import io.papermc.paper.event.player.AsyncChatEvent
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.getText
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatPrompt(val player: Player, message: String, messageColor: String, val cancelMessage: String, val callback: (Player, String) -> Unit) {
    init {
        active.removeIf { it.player == player }
        active.add(this)
        player.sendMessage(Util.color("<$messageColor>$message"))
    }

    companion object {
        val active = mutableListOf<ChatPrompt>()

        fun getPrompt(player: Player): ChatPrompt? {
            return active.find { it.player == player }
        }
    }

    class Events: Listener {
        @EventHandler
        fun onChat(event: AsyncChatEvent) {
            val prompt = getPrompt(event.player)

            if (prompt != null) {
                event.isCancelled = true
                active.remove(prompt)

                if(event.message().getText().equals("cancel", true)) {
                    event.player.sendMessage(Util.color("<${BandiColors.RED}>${prompt.cancelMessage}"))
                } else {
                    prompt.callback.invoke(event.player, event.message().getText())
                }
            }
        }
    }
}