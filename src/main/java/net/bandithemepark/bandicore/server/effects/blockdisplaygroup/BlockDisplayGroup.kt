package net.bandithemepark.bandicore.server.effects.blockdisplaygroup

import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector
import org.joml.Matrix4f
import kotlin.math.max
import kotlin.math.min

class BlockDisplayGroup(val pos1: Location, val pos2: Location, val origin: Vector, var rotation: Vector, val offset: Vector) {
    val parts = mutableListOf<BlockDisplayPart>()

    fun spawn() {
        parts.clear()

        val x1 = min(pos1.x, pos2.x)
        val y1 = min(pos1.y, pos2.y)
        val z1 = min(pos1.z, pos2.z)

        val x2 = max(pos1.x, pos2.x)
        val y2 = max(pos1.y, pos2.y)
        val z2 = max(pos1.z, pos2.z)

        val x = x2 - x1
        val y = y2 - y1
        val z = z2 - z1

        for(i in 0 until x.toInt()) {
            for(j in 0 until y.toInt()) {
                for(k in 0 until z.toInt()) {
                    val location = Location(pos1.world, x1, y1, z1).add(i.toDouble(), j.toDouble(), k.toDouble())

                    if(location.block.type.isAir) {
                        continue
                    }

                    val offsetToOrigin = location.clone().subtract(origin).toVector()
                    val part = BlockDisplayPart(offsetToOrigin, location.block.blockData)
                    part.spawn(origin.toLocation(location.world!!).add(offset))
                    parts.add(part)
                }
            }
        }
    }

    fun deSpawn() {
        for(part in parts) {
            part.deSpawn()
        }

        parts.clear()
    }

    fun update() {
        for(part in parts) {
            val matrix = Matrix4f()
            val rotationQuaternion = Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z).toBukkitQuaternion()
            matrix.rotation(rotationQuaternion)
            matrix.translate(part.offset.x.toFloat(), part.offset.y.toFloat(), part.offset.z.toFloat())
            part.update(matrix)
        }
    }
}