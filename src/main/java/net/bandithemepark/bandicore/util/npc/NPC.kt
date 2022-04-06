package net.bandithemepark.bandicore.util.npc

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Pair
import net.bandithemepark.bandicore.BandiCore
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.TrapDoor
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class NPC(val name: String, val skinOwner: Player, var visibilityType: NPCVisibilityType, val server: MinecraftServer) {
    private var npc: ServerPlayer? = null
    private var profile: GameProfile? = null
    var spawned = false
    var visibilityList = mutableListOf<Player>()

    var location: Location? = null
        private set

    // Spawning and despawning
    fun spawn(location: Location) {
        this.location = location

        profile = GameProfile(UUID.randomUUID(), name)
        profile!!.properties.put("textures", (skinOwner as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next())

        npc = ServerPlayer(server, (location.world as CraftWorld).handle, profile!!)
        npc!!.setPos(location.x, location.y, location.z)

        sendPacket(ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc))
        sendPacket(ClientboundAddPlayerPacket(npc!!))
        showSecondLayer()

        spawned = true
        active.add(this)

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, { hideFromTabList() }, 2)
    }

    fun spawnFor(player: Player) {
        (player as CraftPlayer).handle.connection.send(ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc))
        player.handle.connection.send(ClientboundAddPlayerPacket(npc!!))

        val data = npc!!.entityData
        data.set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, 127.toByte())
        player.handle.connection.send(ClientboundSetEntityDataPacket(npc!!.id, data, true))

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            player.handle.connection.send(ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc))
            updatePosition(true)
            updateHelmet()
            updateItemInMainHand()
            updateItemInOffHand() }, 2)
    }

    fun deSpawn() {
        sendPacket(ClientboundRemoveEntitiesPacket(npc!!.id))
        spawned = false
        active.remove(this)
    }

    private fun hideFromTabList() {
        sendPacket(ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc))
    }

    private fun showSecondLayer() {
        val data = npc!!.entityData
        data.set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, 127.toByte())
        sendPacket(ClientboundSetEntityDataPacket(npc!!.id, data, true))
    }

    // Items
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
    private fun isOpenTrapdoor(block: Block): Boolean {
        if(block.type.toString().endsWith("TRAPDOOR")) {
            val trapdoor = location!!.block.blockData as TrapDoor
            if(trapdoor.isOpen) return true
        }

        return false
    }

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

    private fun updatePosition(heightCorrection: Boolean) {
        if(heightCorrection) {
            val newY = getHeightAtLocation()
            npc!!.absMoveTo(location!!.x, newY, location!!.z, location!!.yaw, location!!.pitch)
        } else {
            npc!!.absMoveTo(location!!.x, location!!.y, location!!.z, location!!.yaw, location!!.pitch)
        }

        sendPacket(ClientboundTeleportEntityPacket(npc as Entity))
    }

    fun moveHead(pitch: Float, yaw: Float) {
        sendPacket(ClientboundRotateHeadPacket(npc!!, ((yaw%360)*256/360).toInt().toByte()))
        sendPacket(ClientboundMoveEntityPacket.Rot(npc!!.id, ((yaw%360)*256/360).toInt().toByte(), ((pitch%360)*256/360).toInt().toByte(), false))
    }

    fun teleport(location: Location, heightCorrection: Boolean = true) {
        this.location = location
        updatePosition(heightCorrection)
    }

    // Pathfinding
    private var pathfinder = NPCPathfinding(this, 2.0)
    fun walkTo(location: Location) {
        pathfinder.walkTo(location)
    }
    var walkSpeed: Double
    get() { return pathfinder.speed }
    set(value) {
        pathfinder.speed = value
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

        fun getMinecraftServer(player: Player): MinecraftServer? {
            return (player as CraftPlayer).handle.getServer()
        }

        fun removeAll() {
            active.toList().forEach { it.deSpawn() }
        }

        fun startTimer() {
            NPCPathfinding.setup(Location(Bukkit.getWorld("world"), 17.5, 0.0, -144.5))

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
            for(npc in active) npc.spawnFor(event.player)
        }
    }

    enum class NPCVisibilityType {
        WHITELIST, BLACKLIST
    }
}