package net.bandithemepark.bandicore.util.entity.marker

import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class PacketEntityMarker(val world: World): PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): LivingEntity {
        return ArmorStand(world, x, y, z)
    }

    init {
        visibilityType = VisibilityType.WHITELIST
    }

    var lastPosition = Vector()
    fun moveEntity(position: Vector) {
        lastPosition = position

        if(spawned) {
            val movedPosition = position.clone().add(Vector(0.0, -1.675, 0.0))
            moveEntity(movedPosition.x, movedPosition.y, movedPosition.z)
        }
    }

    val viewers = mutableListOf<Player>()

    /**
     * Adds a player to view this marker
     * @param player The player to add
     */
    fun addViewer(player: Player) {
        viewers.add(player)
        visibilityList.add(player)

        if(viewers.size == 1) {
            spawn(lastPosition.toLocation(world).add(Vector(0.0, -1.675, 0.0)))
            handle!!.isInvisible = true
            (handle!! as ArmorStand).isMarker = true
            handle!!.isNoGravity = true
            handle!!.setGlowingTag(true)
            updateMetadata()
            helmet = ItemStack(Material.STONE_BUTTON)
        } else {
            spawnFor(player)
            updateEquipmentFor(player)
            updateMetadataFor(player)
        }
    }

    /**
     * Removes a player from the viewing list
     * @param player The player to remove
     */
    fun removeViewer(player: Player) {
        viewers.remove(player)

        if(viewers.isEmpty()) {
            deSpawn()
        } else {
            deSpawnFor(player)
        }

        visibilityList.remove(player)
    }
}