package net.bandithemepark.bandicore.park.parkours

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.FileUtil
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.debug.Reloadable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ParkourManager: Reloadable {
    init {
        register("parkours")
    }

    fun setup() {
        loadParkours()
        startTimer()
    }

    var parkours = mutableListOf<Parkour>()
    private fun loadParkours() {
        parkours = mutableListOf()
        val json = FileUtil.loadJsonFrom("plugins/BandiCore/parkours.json")

        if(!json.has("parkours")) {
            Util.debug("Parkours", "No parkours found. Create a parkours.json file and add configuration to it")
            return
        }

        json.getAsJsonArray("parkours").forEach {
            val parkour = Parkour.fromJson(it.asJsonObject)
            parkours.add(parkour)
        }

        Util.debug("Parkours", "Loaded ${parkours.size} parkours")
    }

    private fun startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
            sessions.forEach {
                it.showActionBar()
            }
        }, 0, 1)
    }

    val sessions = mutableListOf<ParkourSession>()

    companion object {
        fun Player.getParkourSession(): ParkourSession? {
            return BandiCore.instance.parkourManager.sessions.firstOrNull { it.player == this }
        }
    }

    override fun reload() {
        parkours.forEach { parkour -> parkour.leaderboards.forEach { it.deSpawn() } }
        loadParkours()
    }
}