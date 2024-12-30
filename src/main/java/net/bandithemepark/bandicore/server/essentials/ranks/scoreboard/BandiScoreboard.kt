package net.bandithemepark.bandicore.server.essentials.ranks.scoreboard

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.VanishCommand
import net.bandithemepark.bandicore.server.statistics.Playtime.Companion.getPlaytime
import net.bandithemepark.bandicore.server.statistics.Playtime.Companion.getPlaytimeAfk
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.npc.NPC
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class BandiScoreboard {
    val mainScoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard

    private fun setupScoreboardTeams() {
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
        for(color in NamedTextColor.NAMES.keys()) {
            val team = mainScoreboard.registerNewTeam(color)
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
            team.color(NamedTextColor.NAMES.value(color))
        }
    }

    private fun clearScoreboardTeams() {
        val teams = mainScoreboard.teams.toMutableList()
        for(team in teams) {
            team.unregister()
        }
    }

    /**
     * Updates the main scoreboard, and applies it to everyone
     */
    fun updateScoreboard() {
        // Re-register all teams, so that they are cleared
        clearScoreboardTeams()
        setupScoreboardTeams()

        // Add players of each rank to their corresponding team
        for (rank in BandiCore.instance.server.rankManager.loadedRanks) {
            val team = mainScoreboard.getTeam(rank.scoreboardName)!!

            // Add all players with the rank to the team
            for (player in BandiCore.instance.server.rankManager.loadedPlayerRanks.keys.filter { rank == BandiCore.instance.server.rankManager.loadedPlayerRanks[it] }) {
                team.addEntry(player.name)
            }
        }

        // Update team entries for each color
        for((name, color) in NamedTextColor.NAMES.keyToValue()) {
            val team = mainScoreboard.getTeam(name)!!

            // Add all entities with the color to the team
            for ((key, value) in selectedColors.entries.toSet()) {
                if (value == color) {
                    team.addEntry(key)
                }
            }
        }

        // Add NPCs to team
        val npcTeam = mainScoreboard.getTeam("npc")!!
        for(npc in NPC.active) {
            npcTeam.addEntry(npc.name)
        }

        // Tablist updates
        updatePlayerTabListNames()
    }

    private fun updatePlayerTabListNames() {
        for(player in Bukkit.getOnlinePlayers()) {
            // Update name
            val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]
            val rankName = rank?.name ?: "Guest"
            player.playerListName(Util.color("$rankName <${rank?.color ?: "#64666b"}>${player.name}"))

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

    private val selectedColors = hashMapOf<String, NamedTextColor>()
    fun setGlowColor(entityUUID: String, color: NamedTextColor) {
        selectedColors[entityUUID] = color
    }

    companion object {
        fun Player.updatePlayerList() {
            val onlinePlayers = Bukkit.getOnlinePlayers().size - VanishCommand.currentlyHidden.size

            val serverName = if(BandiCore.instance.devMode) "Development" else "BandiThemepark"
            val serverColor = if(BandiCore.instance.devMode) "#ef4444" else "#ffbb1d"

            this.sendPlayerListHeaderAndFooter(Util.color("<newline><newline><${serverColor}><bold>${serverName}<newline><!bold><${BandiColors.LIGHT_GRAY}> Welcome! There are $onlinePlayers player(s) online <newline>"),
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