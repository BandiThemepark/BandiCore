package net.bandithemepark.bandicore.park.attractions.rideop

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class RideOP(val id: String) {
    var loadedPages = listOf<RideOPPage>()

    abstract fun getPages(): List<RideOPPage>
    abstract fun onTick()
    abstract fun onSecond()
    abstract fun onServerStart()
    abstract fun onServerStop()

    var operator = null as Player?

    // TODO Laden van region

    fun openMenu(player: Player) {
        // TODO Openen van een menu
    }

    fun updateMenu() {
        // TODO Detect wie inventory gebruikt met InventoryHolder en update voor hen
    }

    private fun setup() {
        loadedPages = getPages()
        loadedPages.forEach {
            it.rideOP = this
            it.loadButtons()
        }

        // TODO Laden van alle pages en al hun buttons
        onServerStart()
    }

    fun register() {
        rideOPs.add(this)
        setup()
    }

    companion object {
        val rideOPs = mutableListOf<RideOP>()

        fun get(id: String): RideOP? {
            return rideOPs.find { it.id == id }
        }

        fun getOperating(player: Player): RideOP? {
            return rideOPs.find { it.operator == player }
        }

        fun isOperating(player: Player): Boolean {
            return getOperating(player) != null
        }
    }

    class Timer {
        init {
            start()
        }

        fun start() {
            var i = 0

            Bukkit.getScheduler().scheduleSyncRepeatingTask(BandiCore.instance, {
                rideOPs.forEach { it.onTick() }

                i++
                if (i == 20) {
                    rideOPs.forEach { it.onSecond() }
                    i = 0
                }
            }, 1, 0)
        }
    }
}