package net.bandithemepark.bandicore.util.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import com.mojang.datafixers.util.Pair
import me.partypronl.themeparkcore.util.packetwrappers.WrapperPlayServerEntityTeleport
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.regions.BandiRegion
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.event.PacketEntityDismountEvent
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInputEvent
import net.bandithemepark.bandicore.util.entity.event.PacketEntityInteractEvent
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.SynchedEntityData.DataValue
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

abstract class PacketEntity {
    lateinit var handle: Entity
    var spawned = false

    var visibilityType = VisibilityType.BLACKLIST
    var visibilityList = mutableListOf<Player>()

    private val passengers = mutableListOf<org.bukkit.entity.Entity>()
    private val passengerIds = mutableListOf<Int>()

    var debug = false

    // Region based visibility stuff
    var region: BandiRegion? = null
    var playersInRegion = mutableListOf<Player>()

    // Spawning and despawning
    abstract fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): Entity

    val uuid = UUID.randomUUID()
    fun isInitialized() = ::handle.isInitialized

    /**
     * Spawns the entity at a certain location
     * @param spawnLocation The location to spawn the entity at
     */
    open fun spawn(spawnLocation: Location, regionId: String? = null) {
        handle = getInstance((spawnLocation.world as CraftWorld).handle, spawnLocation.x, spawnLocation.y, spawnLocation.z)

        val packetListener = object: PacketAdapter(BandiCore.instance, ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY) {
            override fun onPacketSending(event: PacketEvent) {
                val packet = event.packet.handle as ClientboundAddEntityPacket
                Util.debug("PacketEntity Spawn", "Spawn packet sent to ${event.player.name}, entityType: ${packet.type}, entityId: ${packet.id}, x: ${packet.x}, y: ${packet.y}, z: ${packet.z}")
            }
        }

        if(debug) {
            ProtocolLibrary.getProtocolManager().addPacketListener(packetListener)
        }

        if(regionId != null) {
            region = BandiCore.instance.regionManager.getFromId(regionId)
            region!!.packetEntities.add(this)
            if(region != null) playersInRegion = Bukkit.getOnlinePlayers().filter { region!!.containsPlayer(it) }.toMutableList()
        }

        val packet = ClientboundAddEntityPacket(handle.id, handle.uuid, spawnLocation.x, spawnLocation.y, spawnLocation.z, 0f, 0f, handle.type, 0, handle.deltaMovement, 0.0)
        sendPacket(packet)

        this.location = spawnLocation
        updateLocation()

        spawned = true
        active.add(this)

        if(debug) {
            Util.debug("PacketEntity", "Spawned entity with ID ${handle.id} at ${spawnLocation.x}, ${spawnLocation.y}, ${spawnLocation.z}")
            Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
                ProtocolLibrary.getProtocolManager().removePacketListener(packetListener)
            }, 2)
        }
    }

    /**
     * Spawns the entity for a certain player
     * @param player The player to spawn the entity for
     */
    fun spawnFor(player: Player) {
        val packet = ClientboundAddEntityPacket(handle.id, handle.uuid, location.x, location.y, location.z, 0f, 0f, handle.type, 0, handle.deltaMovement, 0.0)
        (player as CraftPlayer).handle.connection.send(packet)
    }

    /**
     * Despawns the entity
     */
    open fun deSpawn() {
        val packet = ClientboundRemoveEntitiesPacket(handle.id)
        sendPacket(packet)

        spawned = false
        active.remove(this)

        if(region != null) {
            region!!.packetEntities.remove(this)
            region = null
            playersInRegion.clear()
        }
    }

    /**
     * Despawns the entity for just one player. Does not despawn the entity on the server side
     * @param player The player to despawn the entity for
     */
    fun deSpawnFor(player: Player) {
        val packet = ClientboundRemoveEntitiesPacket(handle.id)
        (player as CraftPlayer).handle.connection.send(packet)
    }

    // Position
    lateinit var location: Location
        private set

    private fun updateLocation() {
        val packet = WrapperPlayServerEntityTeleport()
        packet.entityID = handle.id
        packet.x = location.x
        packet.y = location.y
        packet.z = location.z
        packet.pitch = location.pitch
        packet.yaw = location.yaw

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
        this.location = Location(location.world, x, y, z)
        updateLocation()
    }

    /**
     * Other move entity but with pitch and yaw
     */
    fun moveEntity(x: Double, y: Double, z: Double, pitch: Float, yaw: Float) {
        this.location = Location(location.world, x, y, z, pitch, yaw)
        updateLocation()
    }

    // Equipment
    var helmet: ItemStack? = null
        set(value) {
            field = value
            if(spawned) sendPacket(ClientboundSetEquipmentPacket(handle.id, listOf(Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmet)))))
        }

    var chestPlate: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle.id, listOf(Pair(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chestPlate)))))
        }

    var leggings: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle.id, listOf(Pair(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(leggings)))))
        }

    var boots: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle.id, listOf(Pair(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(boots)))))
        }

    var itemInMainHand: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle.id, listOf(Pair(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(itemInMainHand)))))
        }

    var itemInOffHand: ItemStack? = null
        set(value) {
            field = value
            sendPacket(ClientboundSetEquipmentPacket(handle.id, listOf(Pair(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(itemInOffHand)))))
        }

    /**
     * Sends the update packets of the equipment to one player
     * @param player The player to send the packets to
     */
    fun updateEquipmentFor(player: Player) {
        (player as CraftPlayer).handle.connection.send(ClientboundSetEquipmentPacket(handle.id, listOf(
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
    open fun updateMetadata() {
        val packet = ClientboundSetEntityDataPacket(handle.id, handle.entityData.nonDefaultValues!!)
        sendPacket(packet)
    }

    /**
     * Updates the entity metadata for one single player
     * @param player The player to update the metadata for
     */
    open fun updateMetadataFor(player: Player) {
        if(handle.entityData.nonDefaultValues == null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
                updateMetadataFor(player)
            }, 1)
            return
        }

        handle.entityData.refresh((player as CraftPlayer).handle)
    }

    private fun sendPacket(packet: Packet<*>) {
        if(visibilityType == VisibilityType.WHITELIST) {
            for(player in visibilityList) {
                if(region != null) {
                    if(playersInRegion.contains(player)) (player as CraftPlayer).handle.connection.send(packet)
                } else {
                    (player as CraftPlayer).handle.connection.send(packet)
                }
            }
        }

        if(visibilityType == VisibilityType.BLACKLIST) {
            for(player in Bukkit.getOnlinePlayers().filter { !visibilityList.contains(it) }) {
                if(region != null) {
                    if(playersInRegion.contains(player)) (player as CraftPlayer).handle.connection.send(packet)
                } else {
                    (player as CraftPlayer).handle.connection.send(packet)
                }
            }
        }
    }

    /**
     * Returns the players that can see this entity
     */
    fun getPlayersVisibleFor(): List<Player> {
        return when(visibilityType) {
            VisibilityType.WHITELIST -> {
                visibilityList
            }

            VisibilityType.BLACKLIST -> {
                Bukkit.getOnlinePlayers().filter { !visibilityList.contains(it) }
            }
        }
    }

    private fun sendPacket(packet: PacketContainer) {
        val pm = ProtocolLibrary.getProtocolManager()

        if(visibilityType == VisibilityType.WHITELIST) {
            for(player in visibilityList) pm.sendServerPacket(player, packet)
        }

        if(visibilityType == VisibilityType.BLACKLIST) {
            for(player in Bukkit.getOnlinePlayers().filter { !visibilityList.contains(it) }) pm.sendServerPacket(player, packet)
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

    /**
     * Updates the passengers in this vehicle for everyone that can see this entity
     */
    fun updatePassengers() {
        val ids = passengers.map { it.entityId }.toMutableList()
        ids.addAll(passengerIds)

        val intArray = IntArray(ids.size)
        for(i in ids.indices) intArray[i] = ids[i]

        val pm = ProtocolLibrary.getProtocolManager()
        val packet = pm.createPacket(PacketType.Play.Server.MOUNT)
        packet.modifier.writeDefaults()
        packet.integers.write(0, handle.id)
        packet.integerArrays.write(0, intArray)

        sendPacket(packet)
    }

    /**
     * Updates the passengers in this vehicle for one player
     * @param player The player to update the passengers for
     */
    fun updatePassengersFor(player: Player) {
        val ids = passengers.map { it.entityId }.toMutableList()
        ids.addAll(passengerIds)

        val pm = ProtocolLibrary.getProtocolManager()
        val packet = pm.createPacket(PacketType.Play.Server.MOUNT)
        packet.modifier.writeDefaults()
        packet.integers.write(0, handle.id)
        packet.integerArrays.write(0, ids.toIntArray())

        pm.sendServerPacket(player, packet)
    }

    /**
     * Adds an entity to the passengers of this vehicle. Does NOT update the passengers for anyone.
     * @param entity The entity to add
     */
    fun addPassenger(entity: org.bukkit.entity.Entity) {
        passengers.add(entity)
    }

    /**
     * Removes an entity from the passengers of this vehicle. Does NOT update the passengers for anyone.
     * @param entity The entity to remove
     */
    @Deprecated("Use ejectPassenger() instead")
    fun removePassenger(entity: org.bukkit.entity.Entity) {
        passengers.remove(entity)
    }

    /**
     * Ejects the given passenger. Also updates the passengers, and resets Smooth Coasters rotation
     * @param entity The entity to eject
     */
    fun ejectPassenger(entity: org.bukkit.entity.Entity) {
        passengers.remove(entity)
        updatePassengers()
        if(entity is Player) BandiCore.instance.smoothCoastersAPI.resetRotation(null, entity)

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            val location = location.clone()
            location.pitch = entity.location.pitch
            location.yaw = entity.location.yaw
            entity.teleport(location.add(0.0, 0.5+1.4375, 0.0))

        }, 1)
    }

    /**
     * Ejects the given passenger at a given location. Also updates the passengers, and resets Smooth Coasters rotation
     * @param entity The entity to eject
     * @param location The location to eject the passenger at
     */
    fun ejectPassengerAt(entity: org.bukkit.entity.Entity, location: Location) {
        passengers.remove(entity)
        updatePassengers()
        if(entity is Player) BandiCore.instance.smoothCoastersAPI.resetRotation(null, entity)

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            entity.teleport(location)
        }, 2)
    }

    /**
     * Ejects the given passenger. Also updates the passengers, and resets Smooth Coasters rotation
     */
    fun ejectPassengers() {
        val passengersCopy = passengers.toMutableList()
        passengers.clear()
        updatePassengers()
        passengersCopy.forEach {
            if(it is Player) BandiCore.instance.smoothCoastersAPI.resetRotation(null, it)
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            passengersCopy.forEach {
                val location = location.clone()
                location.pitch = it.location.pitch
                location.yaw = it.location.yaw
                it.teleport(location.add(0.0, 0.5+1.4375, 0.0))
            }
        }, 1)
    }

    /**
     * Ejects all passengers at a given location. Also updates the passengers, and resets Smooth Coasters rotation
     * @param location The location to eject the passenger at
     */
    fun ejectPassengersAt(location: Location) {
        val passengersCopy = passengers.toMutableList()
        passengers.clear()
        updatePassengers()
        passengersCopy.forEach {
            it.teleport(location)
            if(it is Player) BandiCore.instance.smoothCoastersAPI.resetRotation(null, it)
        }
    }

    /**
     * Adds an entity to the passengers of this vehicle. Does NOT update the passengers for anyone.
     * @param id The ID of the entity to add
     */
    fun addPassenger(id: Int) {
        passengerIds.add(id)
    }

    /**
     * Removes an entity from the passengers of this vehicle. Does NOT update the passengers for anyone.
     * @param id The ID of the entity to remove
     */
    fun removePassenger(id: Int) {
        passengerIds.remove(id)
    }

    /**
     * Gets the passengers on this entity
     * @return The passengers on this entity
     */
    fun getPassengers(): List<org.bukkit.entity.Entity> {
        return passengers
    }

    /**
     * Gets all manually added passenger IDs
     * @return All manually added passenger IDs
     */
    fun getPassengerIds(): List<Int> {
        return passengerIds
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
            val toUpdateRotationFor = mutableListOf<PacketEntity>()
            for(entity in active) {
                try {
                    if (entity.isVisibleFor(event.player)) {
                        toUpdateRotationFor.add(entity)
                        entity.spawnFor(event.player)
                        entity.updateMetadataFor(event.player)
                        entity.updateEquipmentFor(event.player)
                        entity.updatePassengersFor(event.player)
                    }
                } catch(_: NullPointerException) {}
            }

            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                toUpdateRotationFor.forEach {
                    it.updateLocation()
                }
            })
        }
    }

    object PacketListeners {
        fun startListeners() {
            val pm = ProtocolLibrary.getProtocolManager()

            pm.addPacketListener(object: PacketAdapter(BandiCore.instance, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
                override fun onPacketReceiving(event: PacketEvent) {
                    val packet = event.packet.handle as ServerboundPlayerInputPacket

                    for(entity in active.toList()) {
                        try {
                        if(entity.passengers.contains(event.player)) {
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                                val inputEvent = PacketEntityInputEvent(entity, event.player, packet.xxa, packet.zza, packet.isShiftKeyDown, packet.isJumping)
                                Bukkit.getPluginManager().callEvent(inputEvent)

                                if (packet.isShiftKeyDown) {
                                    val dismountEvent = PacketEntityDismountEvent(entity, event.player)
                                    Bukkit.getPluginManager().callEvent(dismountEvent)
                                    if (!event.isCancelled) entity.ejectPassenger(event.player)
                                }
                            })
                        }
                        } catch (_: NullPointerException) {}
                    }
                }
            })

            pm.addPacketListener(object: PacketAdapter(BandiCore.instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                override fun onPacketReceiving(event: PacketEvent) {
                    val packet = event.packet
                    val entityId = packet.integers.read(0)

                    for(entity in active) {
                        try {
                            if (entity.handle.id == entityId) {
                                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                                    val interactEvent = PacketEntityInteractEvent(entity, event.player)
                                    Bukkit.getPluginManager().callEvent(interactEvent)

                                    if (interactEvent.isCancelled) event.isCancelled = true
                                })
                            }
                        } catch(_: NullPointerException) {}
                    }
                }
            })
        }
    }

    enum class VisibilityType {
        WHITELIST, BLACKLIST
    }
}