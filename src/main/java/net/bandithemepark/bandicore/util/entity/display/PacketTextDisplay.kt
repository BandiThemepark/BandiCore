package net.bandithemepark.bandicore.util.entity.display

import com.mojang.math.Transformation
import io.papermc.paper.adventure.PaperAdventure
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.kyori.adventure.text.Component
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Display.BillboardConstraints
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftTextDisplay
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.TextDisplay.TextAlignment
import org.joml.Matrix4f

class PacketTextDisplay: PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): Entity {
        return TextDisplay(EntityType.TEXT_DISPLAY, world)
    }

    /**
     * Sets how the text should be aligned
     * @param alignment The alignment to set
     */
    fun setAlignment(alignment: TextAlignment) {
        when (alignment) {
            TextAlignment.LEFT -> {
                this.setFlag(TextDisplay.FLAG_ALIGN_LEFT.toInt(), true)
                this.setFlag(TextDisplay.FLAG_ALIGN_RIGHT.toInt(), false)
            }

            TextAlignment.RIGHT -> {
                this.setFlag(TextDisplay.FLAG_ALIGN_LEFT.toInt(), false)
                this.setFlag(TextDisplay.FLAG_ALIGN_RIGHT.toInt(), true)
            }

            TextAlignment.CENTER -> {
                this.setFlag(TextDisplay.FLAG_ALIGN_LEFT.toInt(), false)
                this.setFlag(TextDisplay.FLAG_ALIGN_RIGHT.toInt(), false)
            }
        }
    }

    /**
     * Sets the background color of the text
     * @param color The color to set, can be null to remove the background. ARGB supported
     */
    fun setBackgroundColor(color: Color?) {
        if(color == null) (handle as TextDisplay).backgroundColor = -1 else (handle as TextDisplay).backgroundColor = color.asARGB()
    }

    /**
     * Sets the maximum line width used to split lines
     * @param lineWidth The maximum line width in amount of characters
     */
    fun setLineWidth(lineWidth: Int) {
        (handle as TextDisplay).lineWidth = lineWidth
    }

    /**
     * Set whether the text should be visible through blocks
     * @param seeThrough Whether the text should be visible through blocks
     */
    fun setSeeThrough(seeThrough: Boolean) {
        setFlag(2, seeThrough)
    }

    /**
     * Set whether the text should have shadow or not
     * @param shadow Whether the text should have shadow or not
     */
    fun setShadow(shadow: Boolean) {
        setFlag(1, shadow)
    }

    /**
     * Sets the text of the display
     * @param text The text to set (Use Util#color(string))
     */
    fun setText(text: Component) {
        (handle as TextDisplay).text = PaperAdventure.asVanilla(text)
    }

    /**
     * Sets the opacity of the text to the given number
     * @param opacity A percentage between 0-1
     */
    fun setTextOpacity(opacity: Double) {
        (handle as TextDisplay).textOpacity = (opacity * 255.0).toInt().toByte()
    }

    /**
     * Sets whether the background should be the default chat background color
     * @param defaultBackground Whether the background should be the default chat background color
     */
    fun setDefaultBackground(defaultBackground: Boolean) {
        setFlag(4, defaultBackground)
    }

    /**
     * Sets the billboard type of the text display
     * @param billboard The billboard type to set
     */
    fun setBillboard(billboard: Billboard) {
        (handle as TextDisplay).billboardConstraints = BillboardConstraints.valueOf(billboard.name)
    }

    /**
     * Sets the transformation matrix of the text display
     * @param transformationMatrix The transformation matrix to set
     */
    fun setTransformationMatrix(transformationMatrix: Matrix4f) {
        (handle as TextDisplay).setTransformation(Transformation(transformationMatrix))
    }

    /**
     * Sets the scale of the text display
     * @param scale The scale to set to
     */
    fun setScale(scale: Double) {
        setTransformationMatrix(Matrix4f().scale(scale.toFloat()))
    }

    private fun setFlag(flag: Int, set: Boolean) {
        var flagBits: Byte = getFlags()

        flagBits = if (set) {
            (flagBits.toInt() or flag).toByte()
        } else {
            (flagBits.toInt() and flag.inv()).toByte()
        }

        setFlags(flagBits)
    }

    private fun getFlags(): Byte {
        return (handle as TextDisplay).flags
    }

    private fun setFlags(flags: Byte) {
        (handle as TextDisplay).flags = flags
    }
}