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

class RotationPointSelector(customPlayer: CustomPlayer, session: CustomPlayerEditor): CustomPlayerEditorType(customPlayer, session, true) {
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
            0 -> ItemFactory(Material.LIME_TERRACOTTA).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Next rotation point")).build()
            1 -> ItemFactory(Material.RED_TERRACOTTA).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Previous rotation point")).build()
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
                session.setEditor(RotationPointEditor(customPlayer, session, currentSelected))
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

            override fun getMarkerPosition(customPlayer: CustomPlayer): Vector {
                return customPlayer.location!!.clone().toVector().add(MathUtil.rotateAroundPoint(customPlayer.completeRotation, customPlayer.headRotationPoint.x, customPlayer.headRotationPoint.y, customPlayer.headRotationPoint.z))
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector) {
                customPlayer.headRotationPoint.add(position)
                customPlayer.headRotation.multiply(Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z))
            }
        }, BODY {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.body
            }

            override fun getMarkerPosition(customPlayer: CustomPlayer): Vector {
                return customPlayer.location!!.clone().toVector().add(MathUtil.rotateAroundPoint(customPlayer.completeRotation, customPlayer.bodyRotationPoint.x, customPlayer.bodyRotationPoint.y, customPlayer.bodyRotationPoint.z))
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector) {
                customPlayer.bodyRotationPoint.add(position)
                customPlayer.bodyRotation.multiply(Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z))
            }
        }, LEFT_LEG {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.leftLeg
            }

            override fun getMarkerPosition(customPlayer: CustomPlayer): Vector {
                return customPlayer.location!!.clone().toVector().add(MathUtil.rotateAroundPoint(customPlayer.completeRotation, customPlayer.leftLegRotationPoint.x, customPlayer.leftLegRotationPoint.y, customPlayer.leftLegRotationPoint.z))
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector) {
                customPlayer.leftLegRotationPoint.add(position)
                customPlayer.leftLegRotation.multiply(Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z))
            }
        }, RIGHT_LEG {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.rightLeg
            }

            override fun getMarkerPosition(customPlayer: CustomPlayer): Vector {
                return customPlayer.location!!.clone().toVector().add(MathUtil.rotateAroundPoint(customPlayer.completeRotation, customPlayer.rightLegRotationPoint.x, customPlayer.rightLegRotationPoint.y, customPlayer.rightLegRotationPoint.z))
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector) {
                customPlayer.rightLegRotationPoint.add(position)
                customPlayer.rightLegRotation.multiply(Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z))
            }
        }, LEFT_ARM {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.leftArm
            }

            override fun getMarkerPosition(customPlayer: CustomPlayer): Vector {
                return customPlayer.location!!.clone().toVector().add(MathUtil.rotateAroundPoint(customPlayer.completeRotation, customPlayer.leftArmRotationPoint.x, customPlayer.leftArmRotationPoint.y, customPlayer.leftArmRotationPoint.z))
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector) {
                customPlayer.leftArmRotationPoint.add(position)
                customPlayer.leftArmRotation.multiply(Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z))
            }
        }, RIGHT_ARM {
            override fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand {
                return customPlayer.rightArm
            }

            override fun getMarkerPosition(customPlayer: CustomPlayer): Vector {
                return customPlayer.location!!.clone().toVector().add(MathUtil.rotateAroundPoint(customPlayer.completeRotation, customPlayer.rightArmRotationPoint.x, customPlayer.rightArmRotationPoint.y, customPlayer.rightArmRotationPoint.z))
            }

            override fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector) {
                customPlayer.rightArmRotationPoint.add(position)
                customPlayer.rightArmRotation.multiply(Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z))
            }
        };

        abstract fun getArmorStand(customPlayer: CustomPlayer): PacketEntityArmorStand
        abstract fun getMarkerPosition(customPlayer: CustomPlayer): Vector
        abstract fun addPositionRotation(customPlayer: CustomPlayer, position: Vector, rotation: Vector)

        fun setMarked(marked: Boolean, customPlayer: CustomPlayer) {
            getArmorStand(customPlayer).handle!!.setGlowingTag(marked)
            getArmorStand(customPlayer).updateMetadata()
        }
    }
}