package net.bandithemepark.bandicore.server.essentials.ranks.scoreboard

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.VanishCommand
import net.bandithemepark.bandicore.server.statistics.Playtime.Companion.getPlaytime
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.npc.NPC
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.text.SimpleDateFormat
import java.util.*

class BandiScoreboard {
    val mainScoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard

    /**
     * Updates the main scoreboard, and applies it to everyone
     */
    fun updateScoreboard() {
        mainScoreboard.teams.forEach { it.unregister() }

        for(rank in BandiCore.instance.server.rankManager.loadedRanks) {
            val team = mainScoreboard.registerNewTeam(rank.scoreboardName)
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)

            for(player in BandiCore.instance.server.rankManager.loadedPlayerRanks.keys.filter { rank == BandiCore.instance.server.rankManager.loadedPlayerRanks[it] }) {
                team.addEntry(player.name)
            }
        }

        val npcTeam = mainScoreboard.registerNewTeam("npc")
        npcTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)

        for(npc in NPC.active) {
            npcTeam.addEntry(npc.name)
        }

        for(player in Bukkit.getOnlinePlayers()) {
            val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
            player.playerListName(Util.color("${rank.name} ${player.name}").color(TextColor.fromHexString(rank.color)))
            player.updatePlayerList()
        }
    }

    /**
     * Shows the scoreboard to a player
     * @param player The player to show it for
     */
    fun showFor(player: Player) {
        player.scoreboard = mainScoreboard
    }

    companion object {
        fun Player.updatePlayerList() {
            val onlinePlayers = Bukkit.getOnlinePlayers().size - VanishCommand.currentlyHidden.size

            this.sendPlayerListHeaderAndFooter(Util.color("<newline><#ffbb1d><bold>BandiThemepark<newline><!bold><${BandiColors.LIGHT_GRAY}> Welcome! There are $onlinePlayers player(s) online <newline>"),
            Util.color("<newline><${BandiColors.LIGHT_GRAY}>You've played for ${formatTime(player!!.getPlaytime())}<newline>and been AFK for 0d 0h 0m 0s<newline>"))
        }

        private fun formatTime(time: Int): String {
            var timeText = ""
            val days = time / 86400
            val hours = time % 86400 / 3600
            val minutes = time % 3600 / 60
            val seconds = time % 60

            if(days > 0) timeText += "${days}d "
            if(hours > 0) timeText += "${hours}h "
            if(minutes > 0) timeText += "${minutes}m "
            if(seconds > 0) timeText += "${seconds}s"

            return timeText
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            BandiCore.instance.server.scoreboard.showFor(event.player)
        }
    }
}