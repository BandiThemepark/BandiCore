package net.bandithemepark.bandicore.server.essentials.ranks.scoreboard

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.VanishCommand
import net.bandithemepark.bandicore.server.statistics.Playtime.Companion.getPlaytime
import net.bandithemepark.bandicore.server.statistics.Playtime.Companion.getPlaytimeAfk
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.npc.NPC
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
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
        // Register teams if they have not already been registered
        if(mainScoreboard.teams.isEmpty()) {
            // Register teams for all ranks
            for (rank in BandiCore.instance.server.rankManager.loadedRanks) {
                val team = mainScoreboard.registerNewTeam(rank.scoreboardName)
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
            }

            // Register team for NPCs
            val npcTeam = mainScoreboard.registerNewTeam("npc")
            npcTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)

            // Register team for each color (used for hover)
            for(color in ChatColor.values()) {
                val team = mainScoreboard.registerNewTeam(color.name)
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
                team.color(paperColors[color])
            }
        }

        for (rank in BandiCore.instance.server.rankManager.loadedRanks) {
            // Remove all entries from the team
            val team = mainScoreboard.getTeam(rank.scoreboardName)!!
            team.removeEntries(team.entries)

            // Add all players with the rank to the team
            for (player in BandiCore.instance.server.rankManager.loadedPlayerRanks.keys.filter { rank == BandiCore.instance.server.rankManager.loadedPlayerRanks[it] }) {
                team.addEntry(player.name)
            }
        }

        // Update team entries for each color
        for(color in ChatColor.values()) {
            // Clear team
            val team = mainScoreboard.getTeam(color.name)!!
            team.removeEntries(team.entries)

            // Add all entities with the color to the team
            for ((key, value) in selectedColors.entries.toSet()) {
                if (value == color) {
                    team.addEntry(key)
                }
            }
        }

        // Clear NPC team
        val npcTeam = mainScoreboard.getTeam("npc")!!
        npcTeam.removeEntries(npcTeam.entries)

        // Add NPCs to team
        for(npc in NPC.active) {
            npcTeam.addEntry(npc.name)
        }

        // Tablist updates
        for(player in Bukkit.getOnlinePlayers()) {
            // Update name
            val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]
            val rankName = rank?.name ?: "Guest"
            player.playerListName(Util.color("$rankName ${player.name}").color(TextColor.fromHexString(rank?.color ?: "#64666b")))

            // Update tablist for player
            player.updatePlayerList()
        }
    }

    /**
     * Shows the scoreboard to a player
     * @param player The player to show it for
     */
    fun showFor(player: Player) {
        try {
            player.scoreboard = mainScoreboard
        } catch(_: NullPointerException) {
            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                player.scoreboard = mainScoreboard
            }, 1)
        }
    }

    private val selectedColors = hashMapOf<String, ChatColor>()
    fun setGlowColor(entityUUID: String, color: ChatColor) {
        selectedColors[entityUUID] = color
    }

    val paperColors = hashMapOf<ChatColor, NamedTextColor>(
        ChatColor.RED to NamedTextColor.RED,
        ChatColor.DARK_RED to NamedTextColor.DARK_RED,
        ChatColor.BLUE to NamedTextColor.BLUE,
        ChatColor.DARK_BLUE to NamedTextColor.DARK_BLUE,
        ChatColor.GREEN to NamedTextColor.GREEN,
        ChatColor.DARK_GREEN to NamedTextColor.DARK_GREEN,
        ChatColor.YELLOW to NamedTextColor.YELLOW,
        ChatColor.GOLD to NamedTextColor.GOLD,
        ChatColor.AQUA to NamedTextColor.AQUA,
        ChatColor.DARK_AQUA to NamedTextColor.DARK_AQUA,
        ChatColor.LIGHT_PURPLE to NamedTextColor.LIGHT_PURPLE,
        ChatColor.DARK_PURPLE to NamedTextColor.DARK_PURPLE,
        ChatColor.GOLD to NamedTextColor.GOLD,
        ChatColor.GRAY to NamedTextColor.GRAY,
        ChatColor.DARK_GRAY to NamedTextColor.DARK_GRAY,
        ChatColor.BLACK to NamedTextColor.BLACK,
        ChatColor.WHITE to NamedTextColor.WHITE
    )

    companion object {
        fun Player.updatePlayerList() {
            val onlinePlayers = Bukkit.getOnlinePlayers().size - VanishCommand.currentlyHidden.size

            this.sendPlayerListHeaderAndFooter(Util.color("<newline><#ffbb1d><bold>BandiThemepark<newline><!bold><${BandiColors.LIGHT_GRAY}> Welcome! There are $onlinePlayers player(s) online <newline>"),
            Util.color("<newline><${BandiColors.LIGHT_GRAY}>You've played for ${formatTime(player!!.getPlaytime())}<newline>of which ${formatTime(player!!.getPlaytimeAfk())} you were AFK<newline>"))
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
            timeText += "${seconds}s"

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