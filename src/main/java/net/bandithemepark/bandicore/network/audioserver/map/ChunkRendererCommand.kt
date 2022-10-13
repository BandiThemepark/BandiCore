package net.bandithemepark.bandicore.network.audioserver.map

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChunkRendererCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("chunkrenderer", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return false
        }

        if(sender !is Player) return false

        val worldEdit = WorldEdit.getInstance()
        val session = worldEdit.sessionManager.get(BukkitAdapter.adapt(sender))

        if(session == null || session.selectionWorld == null) {
            sender.sendTranslatedMessage("region-command-no-selection", BandiColors.RED.toString())
            return false
        }

        // Getting all chunks in the selection
        val chunks = session.getSelection(session.selectionWorld).chunks
        val convertedChunks = mutableListOf<Chunk>()

        for(chunk in chunks) {
            convertedChunks.add(sender.world.getChunkAt(chunk.x, chunk.z))
        }

        // Starting the renderer
        sender.sendTranslatedMessage("chunk-renderer-started", BandiColors.YELLOW.toString())
        renderChunks(sender, convertedChunks)

        return false
    }

    private fun renderChunks(player: Player, chunks: MutableList<Chunk>) {
        if(chunks.isEmpty()) {
            player.sendTranslatedMessage("chunk-renderer-finished", BandiColors.YELLOW.toString())
            return
        }

        val nextChunk = chunks[0]
        chunks.removeAt(0)

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            val renderer = ChunkRenderer(nextChunk)
            renderer.run {
                renderChunks(player, chunks)
            }
        }, 2)
    }
}