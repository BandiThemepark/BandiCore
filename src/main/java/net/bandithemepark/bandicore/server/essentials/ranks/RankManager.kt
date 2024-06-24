package net.bandithemepark.bandicore.server.essentials.ranks

import io.papermc.paper.event.player.AsyncChatEvent
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendPlayer
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.server.statistics.Playtime
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.Util
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class RankManager {
    val loadedRanks = mutableListOf<Rank>()
    val loadedPlayerRanks = mutableMapOf<Player, Rank>()

    init {
        loadRanks()
    }

    private fun loadRanks() {
        val fm = FileManager()

        for(rankId in fm.getConfig("ranks.yml").get().getConfigurationSection("")!!.getKeys(false)) {
            val name = fm.getConfig("ranks.yml").get().getString("$rankId.name")!!
            val color = fm.getConfig("ranks.yml").get().getString("$rankId.color")!!
            val textColor = fm.getConfig("ranks.yml").get().getString("$rankId.textColor")!!
            val scoreboardName = fm.getConfig("ranks.yml").get().getString("$rankId.scoreboardName")!!

            val permissionsHashMap = hashMapOf<String, Boolean>()

            if(fm.getConfig("ranks.yml").get().contains("$rankId.permissions")) {
                for(permission in fm.getConfig("ranks.yml").get().getStringList("$rankId.permissions")) permissionsHashMap[permission] = true
            }

            if(fm.getConfig("ranks.yml").get().contains("$rankId.blocked")) {
                for(permission in fm.getConfig("ranks.yml").get().getStringList("$rankId.blocked")) permissionsHashMap[permission] = false
            }

            loadedRanks.add(Rank(rankId, name, color, textColor, scoreboardName, permissionsHashMap))
        }
    }

    var Player.rank: Rank?
        get() = loadedPlayerRanks[this]
        private set(value) {
            if(value != null) {
                loadedPlayerRanks[this] = value
            } else {
                loadedPlayerRanks[this] = loadedRanks.find { it.id == "guest" }!!
            }
        }

    /**
     * Sets the rank of a player.
     * @param player The player to set the rank of
     * @param rank The rank to set the player to
     */
    fun setNewRank(player: Player, rank: Rank) {
        player.rank?.removePermissions(player)
        val backendPlayer = BackendPlayer(player)
        backendPlayer.updatePlayer(false, false, 0, rank.id, null, null, false) {}
        player.rank = rank
        BandiCore.instance.server.scoreboard.updateScoreboard()
        rank.applyPermissions(player)
        player.getNameTag()?.updateName()
    }

    /**
     * Internal function to load a player's rank. Do not use this externally! Use [RankManager.setNewRank] instead. Also loads the player's playtime
     * @param player The player to load the rank of
     */
    fun loadRank(player: Player) {
        val backendPlayer = BackendPlayer(player)
        backendPlayer.get { data ->
            // Setting saved playtime
            Playtime.saved[player] = data.get("playtime").asInt
            Playtime.savedAfk[player] = data.get("afkTime").asInt
            CoinManager.setLoadedBalance(player, data.get("coins").asInt)

            // Loading rank
            val rank = loadedRanks.find { it.id == data.get("rank").asString }!!
            loadedPlayerRanks[player] = rank
            rank.applyPermissions(player)

            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                PlayerNameTag(player)
                BandiCore.instance.server.scoreboard.updateScoreboard()
            }, 3)
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            BandiCore.instance.server.rankManager.loadRank(event.player)
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        fun onChat(event: AsyncChatEvent) {
            if(!event.isCancelled) {
                event.isCancelled = true
                val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[event.player]!!
                Bukkit.broadcast(Util.color("${rank.name} <${rank.color}>${event.player.name} ").append(event.message().color(TextColor.fromHexString(rank.textColor))))
            }
        }
    }
}