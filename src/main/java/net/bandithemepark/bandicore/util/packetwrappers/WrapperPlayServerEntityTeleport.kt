package me.partypronl.themeparkcore.util.packetwrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.World
import org.bukkit.entity.Entity

class WrapperPlayServerEntityTeleport : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer?) : super(packet, TYPE) {}
    /**
     * Retrieve entity ID.
     *
     * @return The current EID
     */
    /**
     * Set entity ID.
     *
     * @param value - new value.
     */
    var entityID: Int
        get() = handle.integers.read(0)
        set(value) {
            handle.integers.write(0, value)
        }

    /**
     * Retrieve the entity.
     *
     * @param world - the current world of the entity.
     * @return The entity.
     */
    fun getEntity(world: World?): Entity {
        return handle.getEntityModifier(world!!).read(0)
    }

    /**
     * Retrieve the entity.
     *
     * @param event - the packet event.
     * @return The entity.
     */
    fun getEntity(event: PacketEvent): Entity {
        return getEntity(event.player.world)
    }

    var x: Double
        get() = handle.doubles.read(0)
        set(value) {
            handle.doubles.write(0, value)
        }
    var y: Double
        get() = handle.doubles.read(1)
        set(value) {
            handle.doubles.write(1, value)
        }
    var z: Double
        get() = handle.doubles.read(2)
        set(value) {
            handle.doubles.write(2, value)
        }
    /**
     * Retrieve the yaw of the current entity.
     *
     * @return The current Yaw
     */
    /**
     * Set the yaw of the current entity.
     *
     * @param value - new yaw.
     */
    var yaw: Float
        get() = handle.bytes.read(0) * 360f / 256.0f
        set(value) {
            handle.bytes.write(0, (value * 256.0f / 360.0f).toInt().toByte())
        }
    /**
     * Retrieve the pitch of the current entity.
     *
     * @return The current pitch
     */
    /**
     * Set the pitch of the current entity.
     *
     * @param value - new pitch.
     */
    var pitch: Float
        get() = handle.bytes.read(1) * 360f / 256.0f
        set(value) {
            handle.bytes.write(1, (value * 256.0f / 360.0f).toInt().toByte())
        }
    var onGround: Boolean
        get() = handle.booleans.read(0)
        set(value) {
            handle.booleans.write(0, value)
        }

    companion object {
        val TYPE = PacketType.Play.Server.ENTITY_TELEPORT
    }
}