package net.bandithemepark.bandicore.server.essentials.ranks.test

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.ranks.Rank
import org.bukkit.entity.Player

class RankTestSession(val player: Player, private val targetRank: Rank) {
    private val startRank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
    private val wasOperator = player.isOp

    init {
        start()
    }

    fun start() {
        BandiCore.instance.server.rankManager.setNewRank(player, targetRank)
        player.isOp = false
    }

    fun end() {
        BandiCore.instance.server.rankManager.setNewRank(player, startRank)
        player.isOp = wasOperator
    }
}