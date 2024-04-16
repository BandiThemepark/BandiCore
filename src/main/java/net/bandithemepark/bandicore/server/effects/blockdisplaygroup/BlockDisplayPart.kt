package net.bandithemepark.bandicore.server.effects.blockdisplaygroup

import net.bandithemepark.bandicore.util.entity.display.PacketBlockDisplay
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector
import org.joml.Matrix4f

class BlockDisplayPart(val offset: Vector, private val blockData: BlockData) {
    private var blockDisplay: PacketBlockDisplay? = null

    fun spawn(location: Location) {
        blockDisplay = PacketBlockDisplay()
        blockDisplay!!.spawn(location)
        blockDisplay!!.setBlockState(blockData)
        blockDisplay!!.setInterpolationDuration(4)
        blockDisplay!!.updateMetadata()
    }

    fun deSpawn() {
        blockDisplay!!.deSpawn()
        blockDisplay = null
    }

    fun update(transformationMatrix: Matrix4f) {
        blockDisplay!!.setTransformationMatrix(transformationMatrix)
        blockDisplay!!.updateMetadata()
    }
}