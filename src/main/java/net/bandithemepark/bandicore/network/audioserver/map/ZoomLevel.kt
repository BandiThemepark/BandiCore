package net.bandithemepark.bandicore.network.audioserver.map

import net.bandithemepark.bandicore.BandiCore
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

enum class ZoomLevel(private val textureSize: Int, val index: Int) {
    FURTHEST(1, 1),
    FAR(2, 2),
    NORMAL(4, 3),
    CLOSE(8, 4),
    CLOSEST(16, 5);

    fun renderAndSaveResizedImage(image: BufferedImage, worldName: String, fileName: String) {
        var newImage = image

        if(this != CLOSEST) {
            val height = textureSize * 16
            newImage = toBufferedImage(image.getScaledInstance(height, height, BufferedImage.SCALE_DEFAULT))
        }

        val saveLocation = File("${BandiCore.instance.dataFolder.path}/map/$worldName/${index}/$fileName.png")
        if(!saveLocation.parentFile.exists()) saveLocation.parentFile.mkdirs()
        ImageIO.write(newImage, "png", saveLocation)
    }

    /**
     * Converts a given Image into a BufferedImage
     * Source: https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    open fun toBufferedImage(img: Image): BufferedImage {
        if (img is BufferedImage) {
            return img
        }

        // Create a buffered image with transparency
        val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

        // Draw the image on to the buffered image
        val bGr = bimage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()

        // Return the buffered image
        return bimage
    }
}