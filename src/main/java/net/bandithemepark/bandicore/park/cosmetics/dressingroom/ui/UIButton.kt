package net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui

import org.bukkit.Location
import org.bukkit.entity.Player
import org.joml.Matrix4f

abstract class UIButton() {
    abstract fun onClick(player: Player)
    abstract fun render(location: Location, player: Player, transform: Matrix4f)
    abstract fun remove(player: Player)
    abstract fun onSelect(player: Player)
    abstract fun onDeSelect(player: Player)
}