package net.bandithemepark.bandicore.server.essentials.ranks.test

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.ranks.Rank
import net.bandithemepark.bandicore.util.debug.Testable
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RankTest private constructor() {

    companion object {
        private var instance = null as RankTest?

        fun getInstance(): RankTest {
            if (instance == null) instance = RankTest()
            return instance!!
        }
    }

    init {
        GuestTest().register("guest")
        VIPTest().register("vip")
    }

    val activeSessions = mutableListOf<RankTestSession>()
    private fun startRankTest(player: Player, targetRank: Rank) {
        activeSessions.add(RankTestSession(player, targetRank))
    }

    private fun endRankTest(player: Player) {
        val session = activeSessions.find { it.player == player } ?: return
        session.end()
        activeSessions.remove(session)
    }

    fun toggleRankTest(player: Player, rankId: String) {
        val targetRank = getRank(rankId) ?: return
        val session = activeSessions.find { it.player == player }
        if (session == null) startRankTest(player, targetRank)
        else endRankTest(player)
    }

    private fun getRank(id: String): Rank? {
        return BandiCore.instance.server.rankManager.loadedRanks.find { it.id.equals(id, true) }
    }

    class GuestTest: Testable {
        override fun test(sender: CommandSender) {
            if(sender !is Player) return
            getInstance().toggleRankTest(sender, "guest")
        }
    }

    class VIPTest: Testable {
        override fun test(sender: CommandSender) {
            if(sender !is Player) return
            getInstance().toggleRankTest(sender, "vip")
        }
    }
}