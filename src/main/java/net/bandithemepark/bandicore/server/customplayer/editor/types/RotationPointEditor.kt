package net.bandithemepark.bandicore.server.customplayer.editor.types

import net.bandithemepark.bandicore.server.customplayer.CustomPlayer
import net.bandithemepark.bandicore.server.customplayer.editor.CustomPlayerEditor
import net.bandithemepark.bandicore.server.customplayer.editor.CustomPlayerEditorType
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class RotationPointEditor(customPlayer: CustomPlayer, session: CustomPlayerEditor, val rotationPoint: RotationPointSelector.Selection): CustomPlayerEditorType(customPlayer, session, true) {
    val marker = PacketEntityMarker(session.player.world)

    override fun getItem(slot: Int): ItemStack? {
        return when (slot) {
            0 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Move X")).build()
            1 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Move Y")).build()
            2 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Move Z")).build()
            3 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Rotate X")).build()
            4 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Rotate Y")).build()
            5 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Rotate Z")).build()
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                if(clickType == ClickType.RIGHT) {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.05, 0.0, 0.0), Vector(0.0, 0.0, 0.0))
                } else {
                    rotationPoint.addPositionRotation(customPlayer, Vector(-0.05, 0.0, 0.0), Vector(0.0, 0.0, 0.0))
                }
                updateMarker()
                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-rotation-point-moved", BandiColors.YELLOW.toString(), MessageReplacement("axis", "X"))
            }

            1 -> {
                if(clickType == ClickType.RIGHT) {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.05, 0.0), Vector(0.0, 0.0, 0.0))
                } else {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, -0.05, 0.0), Vector(0.0, 0.0, 0.0))
                }
                updateMarker()
                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-rotation-point-moved", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Y"))
            }

            2 -> {
                if(clickType == ClickType.RIGHT) {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.05), Vector(0.0, 0.0, 0.0))
                } else {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, -0.05), Vector(0.0, 0.0, 0.0))
                }
                updateMarker()
                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-rotation-point-moved", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Z"))
            }

            3 -> {
                if(clickType == ClickType.RIGHT) {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.0), Vector(3.0, 0.0, 0.0))
                } else {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.0), Vector(-3.0, 0.0, 0.0))
                }
                updateMarker()
                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-rotation-point-rotated", BandiColors.YELLOW.toString(), MessageReplacement("axis", "X"))
            }

            4 -> {
                if(clickType == ClickType.RIGHT) {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.0), Vector(0.0, 3.0, 0.0))
                } else {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.0), Vector(0.0, -3.0, 0.0))
                }
                updateMarker()
                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-rotation-point-rotated", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Y"))
            }

            5 -> {
                if(clickType == ClickType.RIGHT) {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, 3.0))
                } else {
                    rotationPoint.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, -3.0))
                }
                updateMarker()
                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-rotation-point-rotated", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Z"))
            }
        }
    }

    override fun onBackButtonPress() {
        session.setEditor(RotationPointSelector(customPlayer, session))
    }

    override fun markAll() {
        if(!marker.viewers.contains(player)) marker.addViewer(player)
        updateMarker()
    }

    override fun unMarkAll() {
        marker.removeViewer(player)
    }

    fun updateMarker() {
        marker.moveEntity(rotationPoint.getMarkerPosition(customPlayer))
    }
}