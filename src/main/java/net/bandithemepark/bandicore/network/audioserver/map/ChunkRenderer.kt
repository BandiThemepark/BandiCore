package net.bandithemepark.bandicore.network.audioserver.map

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.block.Block
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ChunkRenderer(val chunk: Chunk, val maxY: Int = 319) {
    /**
     * Renders this chunk and saves the rendered picture.
     */
    fun run(callback: () -> Unit) {
        // Finding the boundaries of the chunk
        val minX = chunk.x * 16
        val minZ = chunk.z * 16
        val maxX = minX + 15
        val maxZ = minZ + 15

        // Creating the image itself
        val image = BufferedImage(16*16, 16*16, BufferedImage.TYPE_INT_RGB)
        val imageGraphics = image.graphics as Graphics2D

        // Looping through all blocks and placing their textures
        for (x in minX..maxX) {
            for (z in minZ..maxZ) {
                val highestBlock = getNextBlockBelowWithTexture(chunk, x, maxY, z) ?: continue
                val highestBlockTexture = MaterialTextures.convertBukkitSafe(highestBlock)!!

                if(!highestBlockTexture.transparent) {
                    val highestBlockImage = getImage(highestBlockTexture.texture)
                    imageGraphics.drawImage(highestBlockImage, (x - minX)*16, (z - minZ)*16, null)
                } else {
                    val nextBlock = getNextBlockBelowWithTextureAndNotTransparent(chunk, x, maxY, z)

                    if(nextBlock != null) {
                        val nextBlockTexture = MaterialTextures.convertBukkitSafe(nextBlock)!!
                        val nextBlockImage = getImage(nextBlockTexture.texture)
                        imageGraphics.drawImage(nextBlockImage, (x - minX)*16, (z - minZ)*16, null)

                        val highestBlockImage = getImage(highestBlockTexture.texture)
                        imageGraphics.drawImage(highestBlockImage, (x - minX)*16, (z - minZ)*16, null)
                    } else {
                        val highestBlockImage = getImage(highestBlockTexture.texture)
                        imageGraphics.drawImage(highestBlockImage, (x - minX)*16, (z - minZ)*16, null)
                    }
                }
            }
        }

        // Saving/exporting the image
//        val saveLocation = File("${BandiCore.instance.dataFolder.path}/map/${chunk.world.name}_${chunk.x}_${chunk.z}.png")
//        if(!saveLocation.parentFile.exists()) saveLocation.parentFile.mkdirs()
//        ImageIO.write(image, "png", saveLocation)

        ZoomLevel.values().forEach { it.renderAndSaveResizedImage(image, chunk.world.name, "${chunk.x}_${chunk.z}") }

        callback.invoke()
    }

    private fun getImage(name: String): BufferedImage {
        val file = File("${BandiCore.instance.dataFolder.path}/blocktextures/$name.png")
        return ImageIO.read(file)
    }

    private fun getNextBlockBelowWithTexture(chunk: Chunk, x: Int, startY: Int, z: Int): Block? {
        var currentY = startY
        var blockUnder = null as Block?

        while(blockUnder == null && currentY > -64) {
            val blockAtNew = chunk.world.getBlockAt(x, currentY, z)
            val texture = MaterialTextures.convertBukkitSafe(blockAtNew)
            if(blockAtNew.type != Material.AIR && texture != null) {
                blockUnder = blockAtNew
            } else {
                currentY--
            }
        }

        return blockUnder
    }

    private fun getNextBlockBelowWithTextureAndNotTransparent(chunk: Chunk, x: Int, startY: Int, z: Int): Block? {
        var currentY = startY
        var blockUnder = null as Block?

        while(blockUnder == null && currentY > -64) {
            val blockAtNew = chunk.world.getBlockAt(x, currentY, z)
            val texture = MaterialTextures.convertBukkitSafe(blockAtNew)
            if(blockAtNew.type != Material.AIR && texture != null && !texture.transparent) {
                blockUnder = blockAtNew
            } else {
                currentY--
            }
        }

        return blockUnder
    }
}