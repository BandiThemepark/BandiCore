package net.bandithemepark.bandicore.server.customplayer.editor.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.customplayer.CustomPlayer
import net.bandithemepark.bandicore.server.customplayer.editor.CustomPlayerEditor
import net.bandithemepark.bandicore.server.customplayer.editor.CustomPlayerEditorType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.chat.prompt.ChatPrompt
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.io.File

class PlayerEditor(customPlayer: CustomPlayer, session: CustomPlayerEditor): CustomPlayerEditorType(customPlayer, session, false) {
    override fun getItem(slot: Int): ItemStack? {
        return when(slot) {
            0 -> ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Export pose")).build()
            1 -> ItemFactory(Material.MAGENTA_GLAZED_TERRACOTTA).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Load pose")).build()
            2 -> ItemFactory(Material.LEVER).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Set mode (${session.mode.toString().lowercase()})")).build()

            3 -> {
                if(session.mode == CustomPlayerEditor.Mode.ROTATION_POINT) {
                    ItemFactory(Material.ENDER_PEARL).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Select rotation point")).build()
                } else {
                    ItemFactory(Material.STICK).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Select limb")).build()
                }
            }

            4 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Set body rotation X (${session.customPlayer.completeRotation.getYawPitchRoll().x})")).build()
            5 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Set body rotation Y (${session.customPlayer.completeRotation.getYawPitchRoll().y})")).build()
            6 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.GREEN}>Set body rotation Z (${session.customPlayer.completeRotation.getYawPitchRoll().z})")).build()

            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("custom-player-editor-save"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("custom-player-editor-save-cancelled"),
                ) { player: Player, message: String ->
                    saveAs(message)
                    player.sendTranslatedMessage("custom-player-editor-save-success", BandiColors.YELLOW.toString())
                }
            }

            1 -> {
                ChatPrompt(player,
                    player.getTranslatedMessage("custom-player-editor-load"),
                    BandiColors.YELLOW.toString(),
                    player.getTranslatedMessage("custom-player-editor-load-cancelled"),
                ) { player: Player, message: String ->
                    loadFrom(message)
                    customPlayer.debugPositions(player)
                    player.sendTranslatedMessage("custom-player-editor-load-success", BandiColors.YELLOW.toString())
                }
            }

            2 -> {
                if(session.mode == CustomPlayerEditor.Mode.ROTATION_POINT) {
                    session.mode = CustomPlayerEditor.Mode.LIMB_OFFSET
                } else {
                    session.mode = CustomPlayerEditor.Mode.ROTATION_POINT
                }
                session.updatePlayerItems()
                player.sendTranslatedActionBar("custom-player-editor-set-mode", BandiColors.YELLOW.toString(), MessageReplacement("mode", session.mode.toString()))
            }

            3 -> {
                if(session.mode == CustomPlayerEditor.Mode.ROTATION_POINT) {
                    session.setEditor(RotationPointSelector(customPlayer, session))
                } else {
                    session.setEditor(LimbSelector(customPlayer, session))
                }
            }

            4 -> {
                if(clickType == ClickType.RIGHT) {
                    session.customPlayer.completeRotation = Quaternion.fromYawPitchRoll(session.customPlayer.completeRotation.getYawPitchRoll().x+5.0, session.customPlayer.completeRotation.getYawPitchRoll().y, session.customPlayer.completeRotation.getYawPitchRoll().z)
                } else {
                    session.customPlayer.completeRotation = Quaternion.fromYawPitchRoll(session.customPlayer.completeRotation.getYawPitchRoll().x-5.0, session.customPlayer.completeRotation.getYawPitchRoll().y, session.customPlayer.completeRotation.getYawPitchRoll().z)
                }
                session.customPlayer.updatePosition()

                player.sendTranslatedActionBar("custom-player-editor-body-x", BandiColors.YELLOW.toString())
                session.updatePlayerItems()
            }

            5 -> {
                if(clickType == ClickType.RIGHT) {
                    session.customPlayer.completeRotation = Quaternion.fromYawPitchRoll(session.customPlayer.completeRotation.getYawPitchRoll().x, session.customPlayer.completeRotation.getYawPitchRoll().y+5.0, session.customPlayer.completeRotation.getYawPitchRoll().z)
                } else {
                    session.customPlayer.completeRotation = Quaternion.fromYawPitchRoll(session.customPlayer.completeRotation.getYawPitchRoll().x, session.customPlayer.completeRotation.getYawPitchRoll().y-5.0, session.customPlayer.completeRotation.getYawPitchRoll().z)
                }
                session.customPlayer.updatePosition()

                player.sendTranslatedActionBar("custom-player-editor-body-y", BandiColors.YELLOW.toString())
                session.updatePlayerItems()
            }

            6 -> {
                if(clickType == ClickType.RIGHT) {
                    session.customPlayer.completeRotation = Quaternion.fromYawPitchRoll(session.customPlayer.completeRotation.getYawPitchRoll().x, session.customPlayer.completeRotation.getYawPitchRoll().y, session.customPlayer.completeRotation.getYawPitchRoll().z+5.0)
                } else {
                    session.customPlayer.completeRotation = Quaternion.fromYawPitchRoll(session.customPlayer.completeRotation.getYawPitchRoll().x, session.customPlayer.completeRotation.getYawPitchRoll().y, session.customPlayer.completeRotation.getYawPitchRoll().z-5.0)
                }
                session.customPlayer.updatePosition()

                player.sendTranslatedActionBar("custom-player-editor-body-z", BandiColors.YELLOW.toString())
                session.updatePlayerItems()
            }
        }
    }

    override fun onBackButtonPress() {

    }

    override fun markAll() {

    }

    override fun unMarkAll() {

    }

    fun loadFrom(id: String) {
        val fm = FileManager()

        // Loading the head pose
        val headRotation = loadVector(fm, "customplayerposes/$id.yml", "head.rotation")
        customPlayer.headRotation = Quaternion.fromYawPitchRoll(headRotation.x, headRotation.y, headRotation.z)
        customPlayer.headRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "head.rotationPoint")
        customPlayer.headOffset = loadVector(fm, "customplayerposes/$id.yml", "head.offset")

        // Loading the body pose
        val bodyRotation = loadVector(fm, "customplayerposes/$id.yml", "body.rotation")
        customPlayer.bodyRotation = Quaternion.fromYawPitchRoll(bodyRotation.x, bodyRotation.y, bodyRotation.z)
        customPlayer.bodyRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "body.rotationPoint")
        customPlayer.bodyOffset = loadVector(fm, "customplayerposes/$id.yml", "body.offset")

        // Loading the left arm pose
        val leftArmRotation = loadVector(fm, "customplayerposes/$id.yml", "leftArm.rotation")
        customPlayer.leftArmRotation = Quaternion.fromYawPitchRoll(leftArmRotation.x, leftArmRotation.y, leftArmRotation.z)
        customPlayer.leftArmRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "leftArm.rotationPoint")
        customPlayer.leftArmOffset = loadVector(fm, "customplayerposes/$id.yml", "leftArm.offset")

        // Loading the right arm pose
        val rightArmRotation = loadVector(fm, "customplayerposes/$id.yml", "rightArm.rotation")
        customPlayer.rightArmRotation = Quaternion.fromYawPitchRoll(rightArmRotation.x, rightArmRotation.y, rightArmRotation.z)
        customPlayer.rightArmRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "rightArm.rotationPoint")
        customPlayer.rightArmOffset = loadVector(fm, "customplayerposes/$id.yml", "rightArm.offset")

        // Loading the left leg pose
        val leftLegRotation = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.rotation")
        customPlayer.leftLegRotation = Quaternion.fromYawPitchRoll(leftLegRotation.x, leftLegRotation.y, leftLegRotation.z)
        customPlayer.leftLegRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.rotationPoint")
        customPlayer.leftLegOffset = loadVector(fm, "customplayerposes/$id.yml", "leftLeg.offset")

        // Loading the right leg pose
        val rightLegRotation = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.rotation")
        customPlayer.rightLegRotation = Quaternion.fromYawPitchRoll(rightLegRotation.x, rightLegRotation.y, rightLegRotation.z)
        customPlayer.rightLegRotationPoint = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.rotationPoint")
        customPlayer.rightLegOffset = loadVector(fm, "customplayerposes/$id.yml", "rightLeg.offset")

        customPlayer.updatePosition()
    }

    fun saveAs(id: String) {
        val fm = FileManager()

        // Creating the file if it doesn't exist
        val file = File(BandiCore.instance.dataFolder, "customplayerposes/$id.yml")
        if(!file.exists()) {
            file.createNewFile()
        }

        // Saving the head
        saveVector(customPlayer.headRotation.getYawPitchRoll(), fm, "customplayerposes/$id.yml", "head.rotation")
        saveVector(customPlayer.headRotationPoint, fm, "customplayerposes/$id.yml", "head.rotationPoint")
        saveVector(customPlayer.headOffset, fm, "customplayerposes/$id.yml", "head.offset")

        // Saving the body
        saveVector(customPlayer.bodyRotation.getYawPitchRoll(), fm, "customplayerposes/$id.yml", "body.rotation")
        saveVector(customPlayer.bodyRotationPoint, fm, "customplayerposes/$id.yml", "body.rotationPoint")
        saveVector(customPlayer.bodyOffset, fm, "customplayerposes/$id.yml", "body.offset")

        // Saving the left arm
        saveVector(customPlayer.leftArmRotation.getYawPitchRoll(), fm, "customplayerposes/$id.yml", "leftArm.rotation")
        saveVector(customPlayer.leftArmRotationPoint, fm, "customplayerposes/$id.yml", "leftArm.rotationPoint")
        saveVector(customPlayer.leftArmOffset, fm, "customplayerposes/$id.yml", "leftArm.offset")

        // Saving the right arm
        saveVector(customPlayer.rightArmRotation.getYawPitchRoll(), fm, "customplayerposes/$id.yml", "rightArm.rotation")
        saveVector(customPlayer.rightArmRotationPoint, fm, "customplayerposes/$id.yml", "rightArm.rotationPoint")
        saveVector(customPlayer.rightArmOffset, fm, "customplayerposes/$id.yml", "rightArm.offset")

        // Saving the left leg
        saveVector(customPlayer.leftLegRotation.getYawPitchRoll(), fm, "customplayerposes/$id.yml", "leftLeg.rotation")
        saveVector(customPlayer.leftLegRotationPoint, fm, "customplayerposes/$id.yml", "leftLeg.rotationPoint")
        saveVector(customPlayer.leftLegOffset, fm, "customplayerposes/$id.yml", "leftLeg.offset")

        // Saving the right leg
        saveVector(customPlayer.rightLegRotation.getYawPitchRoll(), fm, "customplayerposes/$id.yml", "rightLeg.rotation")
        saveVector(customPlayer.rightLegRotationPoint, fm, "customplayerposes/$id.yml", "rightLeg.rotationPoint")
        saveVector(customPlayer.rightLegOffset, fm, "customplayerposes/$id.yml", "rightLeg.offset")
    }

    fun saveVector(vector: Vector, fm: FileManager, file: String, path: String) {
        fm.getConfig(file).get().set("$path.x", vector.x)
        fm.getConfig(file).get().set("$path.y", vector.y)
        fm.getConfig(file).get().set("$path.z", vector.z)
        fm.saveConfig(file)
    }

    fun loadVector(fm: FileManager, file: String, path: String): Vector {
        val x = fm.getConfig(file).get().getDouble("$path.x")
        val y = fm.getConfig(file).get().getDouble("$path.y")
        val z = fm.getConfig(file).get().getDouble("$path.z")
        return Vector(x, y, z)
    }
}