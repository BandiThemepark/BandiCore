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

class LimbEditor(customPlayer: CustomPlayer, session: CustomPlayerEditor, val limb: LimbSelector.Selection): CustomPlayerEditorType(customPlayer, session, true) {
    override fun getItem(slot: Int): ItemStack? {
        return when (slot) {
            0 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Move X")).build()
            1 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Move Y")).build()
            2 -> ItemFactory(Material.SHEARS).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Move Z")).build()
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                if(clickType == ClickType.RIGHT) {
                    limb.addPositionRotation(customPlayer, Vector(0.05, 0.0, 0.0))
                } else {
                    limb.addPositionRotation(customPlayer, Vector(-0.05, 0.0, 0.0))
                }

                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-limb-moved", BandiColors.YELLOW.toString(), MessageReplacement("axis", "X"))
            }

            1 -> {
                if(clickType == ClickType.RIGHT) {
                    limb.addPositionRotation(customPlayer, Vector(0.0, 0.05, 0.0))
                } else {
                    limb.addPositionRotation(customPlayer, Vector(0.0, -0.05, 0.0))
                }

                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-limb-moved", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Y"))
            }

            2 -> {
                if(clickType == ClickType.RIGHT) {
                    limb.addPositionRotation(customPlayer, Vector(0.0, 0.0, 0.05))
                } else {
                    limb.addPositionRotation(customPlayer, Vector(0.0, 0.0, -0.05))
                }

                customPlayer.updatePosition()
                player.sendTranslatedActionBar("custom-player-editor-limb-moved", BandiColors.YELLOW.toString(), MessageReplacement("axis", "Z"))
            }
        }
    }

    override fun onBackButtonPress() {
        session.setEditor(LimbSelector(customPlayer, session))
    }

    override fun markAll() {
        limb.setMarked(true, customPlayer)
    }

    override fun unMarkAll() {
        limb.setMarked(false, customPlayer)
    }
}