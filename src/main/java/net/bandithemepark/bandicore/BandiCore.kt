package net.bandithemepark.bandicore

import org.bukkit.plugin.java.JavaPlugin

class BandiCore: JavaPlugin() {
    companion object {
        lateinit var instance: BandiCore
    }

    override fun onEnable() {
        instance = this
    }

    override fun onDisable() {

    }
}