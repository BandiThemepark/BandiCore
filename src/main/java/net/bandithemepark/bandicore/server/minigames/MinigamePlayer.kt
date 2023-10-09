package net.bandithemepark.bandicore.server.minigames

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.coins.PlayerBossBar.Companion.getBossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player

open class MinigamePlayer(player: Player): CraftPlayer(Bukkit.getServer() as CraftServer, (player as CraftPlayer).handle) {
    val originalLocation = player.location.clone()
    val originalInventory = player.inventory.contents!!.clone()
    val originalGameMode = player.gameMode

    fun reset() {
        inventory.contents = originalInventory
        gameMode = originalGameMode
        teleport(originalLocation)
        getBossBar()?.overrideText = null
        getBossBar()?.update()
    }
}