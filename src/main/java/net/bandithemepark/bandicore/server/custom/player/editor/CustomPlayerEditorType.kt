package net.bandithemepark.bandicore.server.custom.player.editor

import net.bandithemepark.bandicore.server.custom.player.CustomPlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class CustomPlayerEditorType(val customPlayer: CustomPlayer, val session: CustomPlayerEditor, val hasBackButton: Boolean): Cloneable {
    lateinit var player: Player

    abstract fun getItem(slot: Int): ItemStack?
    abstract fun use(slot: Int, clickType: ClickType)
    abstract fun onBackButtonPress()
    abstract fun markAll()
    abstract fun unMarkAll()
}