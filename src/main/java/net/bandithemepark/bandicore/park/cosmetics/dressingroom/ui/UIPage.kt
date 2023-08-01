package net.bandithemepark.bandicore.park.cosmetics.dressingroom.ui

import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Vector
import org.joml.Matrix4f
import java.awt.Button
import kotlin.math.ceil

abstract class UIPage(val title: String) {
    abstract fun onBack(player: Player)
    abstract fun customRender(location: Location, player: Player, yaw: Double)
    abstract fun getButtons(player: Player): List<UIButton>

    var buttons: List<UIButton> = listOf()
    var currentPosition = Position(0, 0)
    var scrollHeight = 0
    var lastMovement = System.currentTimeMillis()

    var selectedButton: UIButton? = null

    fun render(location: Location, player: Player, yaw: Double) {
        buttons.forEach { it.remove(player) }
        buttons = getButtons(player)

        spawnTitle(location, yaw)
        customRender(location, player, yaw)

        if(buttons.isEmpty()) return
        selectedButton = buttons[0]
        val baseIndex = scrollHeight * 3
        var endIndex = baseIndex + 8
        if(endIndex >= buttons.size) endIndex = buttons.size - 1

        for(i in baseIndex..endIndex) {
            val offset = getOffset(i-baseIndex)
            val matrix = Matrix4f().rotate(Quaternion.fromYawPitchRoll(0.0, yaw, 0.0).toBukkitQuaternion()).translate(offset.x.toFloat(), offset.y.toFloat(), 0.0f)
            buttons[i].render(location, player, matrix)
        }

        updateSelectedButton(player)
    }

    var titleDisplay: PacketTextDisplay? = null
    private fun spawnTitle(location: Location, yaw: Double) {
        titleDisplay = PacketTextDisplay()
        titleDisplay!!.spawn(location.clone().add(0.0, 1.3, 0.0))
        titleDisplay!!.setDefaultBackground(true)
        titleDisplay!!.setAlignment(TextDisplay.TextAlignment.CENTER)
        titleDisplay!!.setTransformationMatrix(Matrix4f().rotate(Quaternion.fromYawPitchRoll(0.0, yaw, 0.0).toBukkitQuaternion()))
        titleDisplay!!.setText(Util.color("<${BandiColors.YELLOW}>$title"))
        titleDisplay!!.updateMetadata()
    }

    private fun removeTitle() {
        titleDisplay?.deSpawn()
        titleDisplay = null
    }

    fun remove(player: Player) {
        buttons.forEach { it.remove(player) }
        removeTitle()
    }

    val anyOffset = 0.8
    fun getOffset(index: Int): Vector {
        return when(index) {
            0 -> {
                Vector(-anyOffset, anyOffset, 0.0)
            }
            1 -> {
                Vector(0.0, anyOffset, 0.0)
            }
            2 -> {
                Vector(anyOffset, anyOffset, 0.0)
            }
            3 -> {
                Vector(-anyOffset, 0.0, 0.0)
            }
            4 -> {
                Vector(0.0, 0.0, 0.0)
            }
            5 -> {
                Vector(anyOffset, 0.0, 0.0)
            }
            6 -> {
                Vector(-anyOffset, -anyOffset, 0.0)
            }
            7 -> {
                Vector(0.0, -anyOffset, 0.0)
            }
            8 -> {
                Vector(anyOffset, -anyOffset, 0.0)
            }
            else -> {
                Vector()
            }
        }
    }

    fun moveUp(player: Player) {
        if(buttons.isEmpty()) return
        if(currentPosition.y <= 0) return

        currentPosition.y--

        updateSelectedButton(player)
    }

    fun moveDown(player: Player) {
        if(buttons.isEmpty()) return
        val amountOfRows = ceil(buttons.size / 3.0)
        if(currentPosition.y >= amountOfRows) return

        currentPosition.y++

        updateSelectedButton(player)
    }

    fun moveLeft(player: Player) {
        if(buttons.isEmpty()) return
        if(currentPosition.x <= 0) return

        currentPosition.x--

        updateSelectedButton(player)
    }

    fun moveRight(player: Player) {
        if(buttons.isEmpty()) return
        if(currentPosition.x < 2 && getCurrentIndex() < buttons.size - 1) {
            currentPosition.x++

            updateSelectedButton(player)
        }
    }

    fun updateSelectedButton(player: Player) {
        if(buttons.isEmpty()) return
        selectedButton!!.onDeSelect(player)
        selectedButton = buttons[getCurrentIndex()]
        selectedButton!!.onSelect(player)
    }

    fun getCurrentIndex(): Int {
        return currentPosition.x + currentPosition.y * 3
    }

    data class Position(var x: Int, var y: Int)
}