package net.bandithemepark.bandicore.util.entity

import com.mojang.datafixers.util.Pair
import me.partypronl.themeparkcore.util.packetwrappers.WrapperPlayServerEntityTeleport
import net.bandithemepark.bandicore.util.npc.NPC
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

abstract class PacketEntity {
    var handle: Entity? = null
    var spawned = false

    var visibilityType = VisibilityType.BLACKLIST
    var visibilityList = mutableListOf<Player>()

    // Spawning and despawning
    abstract fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): LivingEntity

    /**
     * Spawns the entity at a certain location
     * @param location The location to spawn the entity at
     */
    fun spawn(location: Location) {
        handle = getInstance((location.world as CraftWorld).handle, location.x, location.y, location.z)

        val packet = ClientboundAddEntityPacket(handle!!)
        sendPacket(packet)
        //for(player in Bukkit.getOnlinePlayers()) (player as CraftPlayer).handle.connection.send(packet)

        this.location = location
        updateLocation()

        spawned = true
        active.add(this)
    }

    /**
     * Spawns the entity for a certain player
     * @param player The player to spawn the entity for
     */
    fun spawnFor(player: Player) {
        val packet = ClientboundAddEntityPacket(handle!!)
        (player as CraftPlayer).handle.connection.send(packet)
    }

    /**
     * Despawns the entity
     */
    fun deSpawn() {
        val packet = ClientboundRemoveEntitiesPacket(handle!!.id)
        sendPacket(packet)

        spawned = false
        active.remove(this)
    }

    // Position
    var location: Location? = null
        private set

    private fun updateLocation() {
        val packet = WrapperPlayServerEntityTeleport()
        packet.entityID = handle!!.id
        packet.x = location!!.x
        packet.y = location!!.y
        packet.z = location!!.z
        packet.pitch = location!!.pitch
        packet.yaw = location!!.yaw

        if(visibilityType == VisibilityType.WHITELIST) {
            for(player in visibilityList) packet.sendPacket(player)
        }

        if(visibilityType == VisibilityType.BLACKLIST) {
            for (player in Bukkit.getOnlinePlayers()) {
                if(!visibilityList.contains(player)) {
                    packet.sendPacket(player)
                }
            }
        }
    }

    /**
     * Teleports the entity to a given location
     */
    fun teleport(location: Location) {
        this.location = location
        updateLocation()
    }

    /**
     * Moves the entity to a given location. Basically the same as teleport()
     */
    fun moveEntity(x: Double, y: Double, z: Double) {
        this.location = Location(location!!.world, x, y, z)
        updateLocation()
    }

    // Equipment
    var helmet: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle!!.id, listOf(Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmet)))))
        }

    var chestPlate: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle!!.id, listOf(Pair(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chestPlate)))))
        }

    var leggings: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle!!.id, listOf(Pair(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(leggings)))))
        }

    var boots: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle!!.id, listOf(Pair(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(boots)))))
        }

    var itemInMainHand: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle!!.id, listOf(Pair(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(itemInMainHand)))))
        }

    var itemInOffHand: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle!!.id, listOf(Pair(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(itemInOffHand)))))
        }

    /**
     * Sends the update packets of the equipment to one player
     * @param player The player to send the packets to
     */
    fun updateEquipmentFor(player: Player) {
        (player as CraftPlayer).handle.connection.send(ClientboundSetEquipmentPacket(handle!!.id, listOf(
            Pair(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(itemInMainHand)),
            Pair(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(itemInOffHand)),
            Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmet)),
            Pair(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chestPlate)),
            Pair(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(leggings)),
            Pair(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(boots))
        )))
    }

    // Metadata
    /**
     * Updates entity metadata for all players that can see the armor stand
     */
    fun updateMetadata() {
        val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData, true)
        sendPacket(packet)
    }

    /**
     * Updates the entity metadata for one single player
     * @param player The player to update the metadata for
     */
    fun updateMetadataFor(player: Player) {
        val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData, true)
        (player as CraftPlayer).handle.connection.send(packet)
    }

    private fun sendPacket(packet: Packet<*>) {
        if(visibilityType == VisibilityType.WHITELIST) {
            for(player in visibilityList) (player as CraftPlayer).handle.connection.send(packet)
        }

        if(visibilityType == VisibilityType.BLACKLIST) {
            for (player in Bukkit.getOnlinePlayers()) {
                if(!visibilityList.contains(player)) {
                    (player as CraftPlayer).handle.connection.send(packet)
                }
            }
        }
    }

    /**
     * Tells you whether this entity is visible for a certain player
     * @param player The player to check
     * @return Whether the entity is visible for the player
     */
    fun isVisibleFor(player: Player): Boolean {
        return if(visibilityType == VisibilityType.WHITELIST) {
            visibilityList.contains(player)
        } else {
            !visibilityList.contains(player)
        }
    }

    companion object {
        val active = mutableListOf<PacketEntity>()

        /**
         * Removes all PacketEntities and despawns them
         */
        fun removeAll() {
            if(active.isNotEmpty()) active.toList().forEach { it.deSpawn() }
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            for(entity in active) {
                if(entity.isVisibleFor(event.player)) {
                    entity.spawnFor(event.player)
                    entity.updateMetadataFor(event.player)
                    entity.updateEquipmentFor(event.player)
                }
            }
        }
    }

    enum class VisibilityType {
        WHITELIST, BLACKLIST
    }
}