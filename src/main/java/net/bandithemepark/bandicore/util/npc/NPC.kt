package net.bandithemepark.bandicore.util.npc

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import net.bandithemepark.bandicore.BandiCore
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.ChatVisiblity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.TrapDoor
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class NPC(val name: String, val textureProperty: Property, var visibilityType: NPCVisibilityType, val server: MinecraftServer) {
    private var npc: ServerPlayer? = null
    private var profile: GameProfile? = null
    var spawned = false
    var visibilityList = mutableListOf<Player>()

    var location: Location? = null
        private set

    /**
     * Spawns the NPC at a given location
     * @param location The location to spawn the NPC at
     */
    fun spawn(location: Location) {
        this.location = location

        profile = GameProfile(UUID.randomUUID(), name)
        profile!!.properties.put("textures", textureProperty)

        // Creating the actual NPC instance and moving it to the spawn location
        npc = ServerPlayer(server, (location.world as CraftWorld).handle, profile!!, ClientInformation("", 1, ChatVisiblity.FULL, true, 127, HumanoidArm.RIGHT, true, true))
        npc!!.setPos(location.x, location.y, location.z)

        // Sending packets for spawning the NPC
        sendPacket(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc!!))
        //sendPacket(ClientboundAddPlayerPacket(npc!!))
        showSecondLayer()

        spawned = true
        active.add(this)

        // Updating the scoreboard so their nametag gets hidden
        BandiCore.instance.server.scoreboard.updateScoreboard()

        // Hiding the NPC on the tablist (they are automatically shown there)
        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, { hideFromTabList() }, 2)
    }

    // The spawning function, just for one player and with some extra stuff for when the NPC has already been spawned
    /**
     * Spawns the NPC for a player (used for example when a player joins and needs to see the NPC after it has spawned)
     * @param player The player to spawn the NPC for
     */
    fun spawnFor(player: Player) {
        (player as CraftPlayer).handle.connection.send(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc!!))
        //player.handle.connection.send(ClientboundAddPlayerPacket(npc!!))

        val data = npc!!.entityData
        data.set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, 127.toByte())
        //player.handle.connection.send(ClientboundSetEntityDataPacket(npc!!.id, data, true))
        player.handle.connection.send(ClientboundSetEntityDataPacket(npc!!.id, data.nonDefaultValues!!))

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            player.handle.connection.send(ClientboundPlayerInfoRemovePacket(mutableListOf(npc!!.uuid)))
            showSecondLayer()
            moveHead(location!!.pitch, location!!.yaw)
            updatePosition(true)
            updateHelmet()
            updateItemInMainHand()
            updateItemInOffHand() }, 2)
    }

    /**
     * Despawns the NPC for everyone
     */
    fun deSpawn() {
        sendPacket(ClientboundRemoveEntitiesPacket(npc!!.id))
        spawned = false
        active.remove(this)
    }

    private fun hideFromTabList() {
        sendPacket(ClientboundPlayerInfoRemovePacket(mutableListOf(npc!!.uuid)))
    }

    private fun showSecondLayer() {
        val data = npc!!.entityData
        data.set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, 0x7E.toByte())
        //sendPacket(ClientboundSetEntityDataPacket(npc!!.id, data, true)) Pre 1.20 packet
        sendPacket(ClientboundSetEntityDataPacket(npc!!.id, data.nonDefaultValues!!))
    }

    // All items to hold
    var itemInMainHand: ItemStack? = null
        set(value) {
            field = value
            updateItemInMainHand()
        }

    var itemInOffHand: ItemStack? = null
        set(value) {
            field = value
            updateItemInOffHand()
        }

    var helmet: ItemStack? = null
        set(value) {
            field = value
            updateHelmet()
        }

    private fun updateItemInMainHand() {
        sendPacket(ClientboundSetEquipmentPacket(npc!!.id, listOf(Pair(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(itemInMainHand)))))
    }

    private fun updateItemInOffHand() {
        sendPacket(ClientboundSetEquipmentPacket(npc!!.id, listOf(Pair(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(itemInOffHand)))))
    }

    private fun updateHelmet() {
        sendPacket(ClientboundSetEquipmentPacket(npc!!.id, listOf(Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmet)))))
    }

    // Everything related to moving/teleporting
    // Utility function to check if a certain block is a trapdoor
    private fun isOpenTrapdoor(block: Block): Boolean {
        if(block.type.toString().endsWith("TRAPDOOR") && location!!.block.blockData is TrapDoor) {
            val trapdoor = location!!.block.blockData as TrapDoor
            if(trapdoor.isOpen) return true
        }

        return false
    }

    // Utility function to get the height at a certain location/get the height of the block at that location
    private fun getHeightAtLocation(): Double {
        // Special case where open trapdoors would lift NPCs up
        if(isOpenTrapdoor(location!!.block)) return location!!.blockY.toDouble()

        // Normal
        val blockUnder = if(location!!.block.type == Material.AIR) location!!.block.getRelative(BlockFace.DOWN) else location!!.block

        if(location!!.x <= location!!.blockX+0.7 && location!!.x >= location!!.blockX+0.3 && location!!.z <= location!!.blockZ+0.7 && location!!.z >= location!!.blockZ+0.3) {
            return blockUnder.boundingBox.maxY
        } else {
            var currentTallest = blockUnder.boundingBox.maxY

            if(location!!.x > location!!.blockX+0.7) {
                val blockEast = blockUnder.getRelative(BlockFace.EAST)
                if(blockEast.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockEast)) currentTallest = blockEast.boundingBox.maxY

                val blockAbove = blockEast.getRelative(BlockFace.UP)
                if(blockAbove.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockAbove)) currentTallest = blockAbove.boundingBox.maxY
            }

            if(location!!.x < location!!.blockX+0.3) {
                val blockEast = blockUnder.getRelative(BlockFace.WEST)
                if(blockEast.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockEast)) currentTallest = blockEast.boundingBox.maxY

                val blockAbove = blockEast.getRelative(BlockFace.UP)
                if(blockAbove.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockAbove)) currentTallest = blockAbove.boundingBox.maxY
            }

            if(location!!.z > location!!.blockZ+0.7) {
                val blockEast = blockUnder.getRelative(BlockFace.SOUTH)
                if(blockEast.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockEast)) currentTallest = blockEast.boundingBox.maxY

                val blockAbove = blockEast.getRelative(BlockFace.UP)
                if(blockAbove.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockAbove)) currentTallest = blockAbove.boundingBox.maxY
            }

            if(location!!.z < location!!.blockZ+0.3) {
                val blockEast = blockUnder.getRelative(BlockFace.NORTH)
                if(blockEast.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockEast)) currentTallest = blockEast.boundingBox.maxY

                val blockAbove = blockEast.getRelative(BlockFace.UP)
                if(blockAbove.boundingBox.maxY > currentTallest && !isOpenTrapdoor(blockAbove)) currentTallest = blockAbove.boundingBox.maxY
            }

            return currentTallest
        }
    }

    // Function that moves the NPC to a certain location
    private fun updatePosition(heightCorrection: Boolean) {
        if(heightCorrection) {
            val newY = getHeightAtLocation()
            npc!!.absMoveTo(location!!.x, newY, location!!.z, location!!.yaw, location!!.pitch)
        } else {
            npc!!.absMoveTo(location!!.x, location!!.y, location!!.z, location!!.yaw, location!!.pitch)
        }

        sendPacket(ClientboundTeleportEntityPacket(npc as Entity))
    }

    // Function to move the head of the NPC
    /**
     * Rotates the head of the NPC
     * @param pitch The pitch to rotate to
     * @param yaw The yaw to rotate to
     */
    fun moveHead(pitch: Float, yaw: Float) {
        sendPacket(ClientboundRotateHeadPacket(npc!!, ((yaw%360)*256/360).toInt().toByte()))
        sendPacket(ClientboundMoveEntityPacket.Rot(npc!!.id, ((yaw%360)*256/360).toInt().toByte(), ((pitch%360)*256/360).toInt().toByte(), false))
    }

    /**
     * Teleports the NPC to a given location
     * @param location The location to teleport to
     * @param heightCorrection Whether to correct the height of the NPC (for example when you have slabs). Default is true
     */
    fun teleport(location: Location, heightCorrection: Boolean = true) {
        this.location = location
        updatePosition(heightCorrection)
    }

    // Pathfinding
    var pathfinder = NPCPathfinding(this, 2.0)
    var walkSpeed: Double
    get() { return pathfinder.speed }
    set(value) {
        pathfinder.speed = value
    }

    /**
     * Makes the NPC pathfind to a given location
     * @param location The location to pathfind to
     */
    fun walkTo(location: Location) {
        pathfinder.walkTo(location)
    }

    // Utilities
    private fun sendPacket(packet: Packet<*>) {
        if(visibilityType == NPCVisibilityType.WHITELIST) {
            for(player in visibilityList) (player as CraftPlayer).handle.connection.send(packet)
        }

        if(visibilityType == NPCVisibilityType.BLACKLIST) {
            for (player in Bukkit.getOnlinePlayers()) {
                if(!visibilityList.contains(player)) {
                    (player as CraftPlayer).handle.connection.send(packet)
                }
            }
        }
    }

    companion object {
        val active = mutableListOf<NPC>()

        /**
         * Gets the MinecraftServer a player is on (NMS Instance)
         * @param player The player to get the server of
         * @return The NMS MinecraftServer the player is on
         */
        fun getMinecraftServer(player: Player): MinecraftServer? {
            return (player as CraftPlayer).handle.getServer()
        }

        /**
         * Despawns all active NPCs
         */
        fun removeAll() {
            if(active.isNotEmpty()) active.toList().forEach { it.deSpawn() }
        }

        fun startTimer() {
            NPCPathfinding.setup(Location(Bukkit.getWorld("world"), -25.5, 21.0, -177.5))

            val runnable = object: BukkitRunnable() {
                override fun run() {
                    active.forEach { it.pathfinder.update() }
                }
            }

            runnable.runTaskTimer(BandiCore.instance, 0, 1)
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
                for(npc in active) npc.spawnFor(event.player)
            }, 40)
        }
    }

    enum class NPCVisibilityType {
        WHITELIST, BLACKLIST
    }
}