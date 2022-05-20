package net.bandithemepark.bandicore.server.customplayer.editor.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.customplayer.CustomPlayer
import net.bandithemepark.bandicore.server.customplayer.editor.CustomPlayerEditor
import net.bandithemepark.bandicore.server.customplayer.editor.CustomPlayerEditorType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class LimbSelector(customPlayer: CustomPlayer, session: CustomPlayerEditor): CustomPlayerEditorType(customPlayer, session, true) {
    var currentSelected = Selection.values()[0]
        set(value) {
            field.setMarked(false, session.customPlayer)
            field = value
            value.setMarked(true, session.customPlayer)
        }

    init {
        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
            currentSelected.setMarked(true, session.customPlayer)
        })
    }

    override fun getItem(slot: Int): ItemStack? {
        return when (slot) {
            0 -> ItemFactory(Material.LIME_TERRACOTTA).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Next limb")).build()
            1 -> ItemFactory(Material.RED_TERRACOTTA).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Previous limb")).build()
            2 -> ItemFactory(Material.HOPPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Select")).build()
            else -> null
        }
    }

    override fun use(slot: Int, clickType: ClickType) {
        when(slot) {
            0 -> {
                var index = currentSelected.ordinal + 1
                if(index >= Selection.values().size) index = 0
                currentSelected = Selection.values()[index]
            }

            1 -> {
                var index = currentSelected.ordinal - 1
                if(index < 0) index = Selection.values().size - 1
                currentSelected = Selection.values()[index]
            }

            2 -> {
                session.setEditor(LimbEditor(customPlayer, session, currentSelected))
            }
        }
    }

    override fun onBackButtonPress() {
        session.setEditor(PlayerEditor(customPlayer, session))
    }

    override fun markAll() {

    }

    override fun unMarkAll() {
        currentSelected.setMarked(false, customPlayer)
    }

    enum class Selection {
        HEAD {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.head
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector) {
                customPlayer.headOffset.add(position)
            }
        }, BODY {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.body
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector) {
                customPlayer.bodyOffset.add(position)
            }
        }, LEFT_LEG {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.leftLeg
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector) {
                customPlayer.leftLegOffset.add(position)
            }
        }, RIGHT_LEG {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.rightLeg
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector) {
                customPlayer.rightLegOffset.add(position)
            }
        }, LEFT_ARM {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.leftArm
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector) {
                customPlayer.leftArmOffset.add(position)
            }
        }, RIGHT_ARM {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.rightArm
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector) {
                customPlayer.rightArmOffset.add(position)
            }
        };

        abstract fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand
        abstract fun addPositionRotation(customPlayer: CustomPlayer, position: Vector)

        fun setMarked(marked: Boolean, customPlayer: CustomPlayer) {
            getArmorStand(customPlayer).handle!!.setGlowingTag(marked)
            getArmorStand(customPlayer).updateMetadata()
        }
    }
}