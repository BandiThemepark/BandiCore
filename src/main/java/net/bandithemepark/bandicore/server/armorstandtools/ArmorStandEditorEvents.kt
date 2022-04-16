package net.bandithemepark.bandicore.server.armorstandtools

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.EulerAngle

class ArmorStandEditorEvents: Listener {
    @EventHandler
    fun onArmorStandInteract(event: PlayerInteractAtEntityEvent) {
        if(event.hand == EquipmentSlot.HAND) {
            if(event.rightClicked is ArmorStand) {
                val session = ArmorStandEditor.getSession(event.player)

                if (session != null) {
                    event.isCancelled = true

                    if(event.player.inventory.heldItemSlot == 0) session.openGUI(event.rightClicked as ArmorStand)
                    if(event.player.inventory.heldItemSlot == 1) session.moveX(event.player.isSneaking, event.rightClicked as ArmorStand)
                    if(event.player.inventory.heldItemSlot == 2) session.moveY(event.player.isSneaking, event.rightClicked as ArmorStand)
                    if(event.player.inventory.heldItemSlot == 3) session.moveZ(event.player.isSneaking, event.rightClicked as ArmorStand)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.hand == EquipmentSlot.HAND) {
            if(event.player.equipment.itemInMainHand.type == Material.NETHER_STAR) {
                val session = ArmorStandEditor.getSession(event.player)

                if (session != null) {
                    event.isCancelled = true
                    session.openGUI()
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if(event.view.title == "Editing an ArmorStand") {
            if(event.view.topInventory == event.clickedInventory) {
                val session = ArmorStandEditor.getSession(event.whoClicked as Player)!!

                if(event.slot == 11) session.editing!!.equipment.helmet = event.cursor
                else if(event.slot == 19) session.editing!!.equipment.setItemInMainHand(event.cursor, true)
                else if(event.slot == 20) session.editing!!.equipment.chestplate = event.cursor
                else if(event.slot == 21) session.editing!!.equipment.setItemInOffHand(event.cursor, true)
                else if(event.slot == 29) session.editing!!.equipment.leggings = event.cursor
                else if(event.slot == 38) session.editing!!.equipment.boots = event.cursor
                else {
                    event.isCancelled = true

                    when(event.slot) {
                        15 -> {
                            session.editing!!.isGlowing = !session.editing!!.isGlowing
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        16 -> {
                            session.editing!!.isInvulnerable = !session.editing!!.isInvulnerable
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        23 -> {
                            session.editing!!.isSmall = !session.editing!!.isSmall
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        24 -> {
                            session.editing!!.isCustomNameVisible = !session.editing!!.isCustomNameVisible
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        25 -> {
                            // TODO Add a way to change the name of the armor stand (using something like a chat prompt)
                        }
                        32 -> {
                            session.editing!!.isInvisible = !session.editing!!.isInvisible
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        33 -> {
                            session.editing!!.setBasePlate(!session.editing!!.hasBasePlate())
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        34 -> {
                            session.editing!!.setArms(!session.editing!!.hasArms())
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        42 -> {
                            session.editing!!.setGravity(!session.editing!!.hasGravity())
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI(session.editing!!) })
                        }
                        43 -> {
                            session.editing!!.remove()
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })
                        }
                    }
                }
            }
        }

        if(event.view.title == "ArmorStandEditor GUI") {
            val session = ArmorStandEditor.getSession(event.whoClicked as Player)!!
            event.isCancelled = true

            if(event.slot == 10) session.mode = ArmorStandEditor.Mode.LEFT_LEG
            if(event.slot == 11) session.mode = ArmorStandEditor.Mode.RIGHT_LEG
            if(event.slot == 15) session.mode = ArmorStandEditor.Mode.HEAD
            if(event.slot == 16) session.mode = ArmorStandEditor.Mode.BODY
            if(event.slot == 19) session.mode = ArmorStandEditor.Mode.LEFT_ARM
            if(event.slot == 20) session.mode = ArmorStandEditor.Mode.RIGHT_ARM
            if(event.slot == 24) session.mode = ArmorStandEditor.Mode.POSITION
            if(event.slot == 25) session.mode = ArmorStandEditor.Mode.ROTATION

            if(event.slot in 45..53) {
                session.selectedAccuracy = event.slot-44
            }

            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { session.openGUI() })
        }
    }

    // General events
    @EventHandler
    fun onManipulate(event: PlayerArmorStandManipulateEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onArmorStandRemove(event: EntityDeathEvent) {
        if(event.entity is ArmorStand) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onArmorStandPlace(event: EntitySpawnEvent) {
        if(event.entity is ArmorStand) {
            (event.entity as ArmorStand).setArms(true)
            event.entity.setGravity(false)
            (event.entity as ArmorStand).setBasePlate(false)

            (event.entity as ArmorStand).headPose = EulerAngle.ZERO
            (event.entity as ArmorStand).bodyPose = EulerAngle.ZERO
            (event.entity as ArmorStand).leftArmPose = EulerAngle.ZERO
            (event.entity as ArmorStand).rightArmPose = EulerAngle.ZERO
            (event.entity as ArmorStand).leftLegPose = EulerAngle.ZERO
            (event.entity as ArmorStand).rightLegPose = EulerAngle.ZERO
        }
    }
}