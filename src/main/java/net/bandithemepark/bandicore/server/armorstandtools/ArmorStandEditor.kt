//package net.bandithemepark.bandicore.server.armorstandtools
//
//import net.bandithemepark.bandicore.util.Util
//import net.kyori.adventure.text.Component
//import org.bukkit.Bukkit
//import org.bukkit.Material
//import org.bukkit.entity.ArmorStand
//import org.bukkit.entity.Player
//
//class ArmorStandEditor(val player: Player) {
//    val previousInventory = player.inventory.contents
//    var selectedAccuracy = 5
//    var editing: ArmorStand? = null
//    var mode = Mode.HEAD
//
//    init {
//        player.inventory.clear()
//        player.inventory.setItem(0, ItemFactory.create(Material.NETHER_STAR, Util.color("&aMenu")))
//        player.inventory.setItem(1, ItemFactory.create(Material.SHEARS, Util.color("&aMove X")))
//        player.inventory.setItem(2, ItemFactory.create(Material.SHEARS, Util.color("&aMove Y")))
//        player.inventory.setItem(3, ItemFactory.create(Material.SHEARS, Util.color("&aMove Z")))
//        player.updateInventory()
//
//        activeSessions.add(this)
//    }
//
//    fun finishSession() {
//        player.inventory.contents = previousInventory
//        player.updateInventory()
//
//        activeSessions.remove(this)
//    }
//
//    fun openGUI() {
//        val inv = Bukkit.createInventory(null, 54, Component.text("ArmorStandEditor GUI"))
//
//        for(slot in 0..53) {
//            inv.setItem(slot, ItemFactory.create(Material.GRAY_STAINED_GLASS_PANE, " "))
//        }
//
//        inv.setItem(10, ItemFactory.create(Material.LEATHER_BOOTS, Util.color("&aMove left leg")))
//        inv.setItem(11, ItemFactory.create(Material.LEATHER_BOOTS, Util.color("&aMove right leg")))
//        inv.setItem(15, ItemFactory.create(Material.PLAYER_HEAD, Util.color("&aMove head")))
//        inv.setItem(16, ItemFactory.create(Material.LEATHER_CHESTPLATE, Util.color("&aMove body")))
//        inv.setItem(19, ItemFactory.create(Material.WOODEN_SWORD, Util.color("&aMove left arm")))
//        inv.setItem(20, ItemFactory.create(Material.WOODEN_SWORD, Util.color("&aMove right arm")))
//        inv.setItem(24, ItemFactory.create(Material.ARMOR_STAND, Util.color("&aChange position")))
//        inv.setItem(25, ItemFactory.create(Material.COMPASS, Util.color("&aChange rotation")))
//
//        for(slot in 1..9) {
//            inv.setItem(slot+44, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("&aSet accuracy to $slot")))
//        }
//
//        for(slot in 1..selectedAccuracy) {
//            inv.setItem(slot+44, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("&aSet accuracy to $slot")))
//        }
//
//        player.openInventory(inv)
//    }
//
//    fun openGUI(armorStand: ArmorStand) {
//        editing = armorStand
//        val inv = Bukkit.createInventory(null, 54, Component.text("Editing an ArmorStand"))
//
//        for(slot in 0..53) {
//            inv.setItem(slot, ItemFactory.create(Material.GRAY_STAINED_GLASS_PANE, " "))
//        }
//
//        inv.setItem(11, armorStand.equipment.helmet)
//        inv.setItem(19, armorStand.equipment.itemInMainHand)
//        inv.setItem(20, armorStand.equipment.chestplate)
//        inv.setItem(21, armorStand.equipment.itemInOffHand)
//        inv.setItem(29, armorStand.equipment.leggings)
//        inv.setItem(38, armorStand.equipment.boots)
//
//        inv.setItem(15, ItemFactory.create(Material.GLOWSTONE, 1, 0, Util.color("&aToggle glowing"), Util.color("&7Currently ${convertBoolean(armorStand.isGlowing)}")))
//        inv.setItem(16, ItemFactory.create(Material.ENCHANTED_GOLDEN_APPLE, 1, 0, Util.color("&aToggle invulnerability"), Util.color("&7Currently ${convertBoolean(armorStand.isInvulnerable)}")))
//        inv.setItem(23, ItemFactory.create(Material.EMERALD, 1, 0, Util.color("&aToggle small"), Util.color("&7Currently ${convertBoolean(armorStand.isSmall)}")))
//        inv.setItem(24, ItemFactory.create(Material.NAME_TAG, 1, 0, Util.color("&aToggle name visibility"), Util.color("&7Currently ${convertBoolean(armorStand.isCustomNameVisible)}")))
//        inv.setItem(25, ItemFactory.create(Material.ANVIL, 1, 0, Util.color("&aSet name"), Util.color("&7Currently &f${armorStand.customName}")))
//        inv.setItem(32, ItemFactory.create(Material.GOLD_NUGGET, 1, 0, Util.color("&aToggle visibility"), Util.color("&7Currently ${convertBoolean(!armorStand.isInvisible)}")))
//        inv.setItem(33, ItemFactory.create(Material.SMOOTH_STONE_SLAB, 1, 0, Util.color("&aToggle baseplate"), Util.color("&7Currently ${convertBoolean(armorStand.hasBasePlate())}")))
//        inv.setItem(34, ItemFactory.create(Material.WOODEN_SWORD, 1, 0, Util.color("&aToggle arms"), Util.color("&7Currently ${convertBoolean(armorStand.hasArms())}")))
//        inv.setItem(42, ItemFactory.create(Material.GRAVEL, 1, 0, Util.color("&aToggle gravity"), Util.color("&7Currently ${convertBoolean(armorStand.hasGravity())}")))
//        inv.setItem(43, ItemFactory.create(Material.BARRIER, 1, 0, Util.color("&aRemove/delete"), Util.color("&7Removes the armor stand (forever!)")))
//
//        player.openInventory(inv)
//    }
//
//    private fun convertBoolean(boolean: Boolean): String {
//        return if(boolean) Util.color("&aEnabled")
//        else Util.color("&cDisabled")
//    }
//
//    companion object {
//        val activeSessions = mutableListOf<ArmorStandEditor>()
//
//        fun getSession(player: Player): ArmorStandEditor? {
//            for(session in activeSessions) {
//                if(session.player == player) {
//                    return session
//                }
//            }
//
//            return null
//        }
//
//        fun startSession(player: Player) {
//            ArmorStandEditor(player)
//        }
//    }
//
//    enum class Mode {
//        LEFT_LEG, RIGHT_LEG, HEAD, BODY, LEFT_ARM, RIGHT_ARM, POSITION, ROTATION
//    }
//
//    fun moveX(crouching: Boolean, armorStand: ArmorStand) {
//        editing = armorStand
//
//        if(mode == Mode.LEFT_LEG) {
//            var previous = editing!!.leftLegPose
//
//            previous = if(crouching) previous.setX(previous.x - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setX(previous.x + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.leftLegPose = previous
//        }
//
//        if(mode == Mode.RIGHT_LEG) {
//            var previous = editing!!.rightLegPose
//
//            previous = if(crouching) previous.setX(previous.x - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setX(previous.x + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.rightLegPose = previous
//        }
//
//        if(mode == Mode.HEAD) {
//            var previous = editing!!.headPose
//
//            previous = if(crouching) previous.setX(previous.x - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setX(previous.x + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.headPose = previous
//        }
//
//        if(mode == Mode.BODY) {
//            var previous = editing!!.bodyPose
//
//            previous = if(crouching) previous.setX(previous.x - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setX(previous.x + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.bodyPose = previous
//        }
//
//        if(mode == Mode.LEFT_ARM) {
//            var previous = editing!!.leftArmPose
//
//            previous = if(crouching) previous.setX(previous.x - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setX(previous.x + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.leftArmPose = previous
//        }
//
//        if(mode == Mode.RIGHT_ARM) {
//            var previous = editing!!.rightArmPose
//
//            previous = if(crouching) previous.setX(previous.x - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setX(previous.x + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.rightArmPose = previous
//        }
//
//        if(mode == Mode.POSITION) {
//            val previous = editing!!.location
//
//            if(crouching) previous.x -= selectedAccuracy.toDouble()/10.0
//            else previous.x += selectedAccuracy.toDouble()/10.0
//
//            editing!!.teleport(previous)
//        }
//
//        if(mode == Mode.ROTATION) {
//            val previous = editing!!.location
//
//            if(crouching) previous.yaw -= selectedAccuracy
//            else previous.yaw += selectedAccuracy
//
//            editing!!.teleport(previous)
//        }
//    }
//
//    fun moveY(crouching: Boolean, armorStand: ArmorStand) {
//        editing = armorStand
//
//        if(mode == Mode.LEFT_LEG) {
//            var previous = editing!!.leftLegPose
//
//            previous = if(crouching) previous.setY(previous.y - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setY(previous.y + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.leftLegPose = previous
//        }
//
//        if(mode == Mode.RIGHT_LEG) {
//            var previous = editing!!.rightLegPose
//
//            previous = if(crouching) previous.setY(previous.y - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setY(previous.y + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.rightLegPose = previous
//        }
//
//        if(mode == Mode.HEAD) {
//            var previous = editing!!.headPose
//
//            previous = if(crouching) previous.setY(previous.y - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setY(previous.y + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.headPose = previous
//        }
//
//        if(mode == Mode.BODY) {
//            var previous = editing!!.bodyPose
//
//            previous = if(crouching) previous.setY(previous.y - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setY(previous.y + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.bodyPose = previous
//        }
//
//        if(mode == Mode.LEFT_ARM) {
//            var previous = editing!!.leftArmPose
//
//            previous = if(crouching) previous.setY(previous.y - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setY(previous.y + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.leftArmPose = previous
//        }
//
//        if(mode == Mode.RIGHT_ARM) {
//            var previous = editing!!.rightArmPose
//
//            previous = if(crouching) previous.setY(previous.y - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setY(previous.y + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.rightArmPose = previous
//        }
//
//        if(mode == Mode.POSITION) {
//            val previous = editing!!.location
//
//            if(crouching) previous.y -= selectedAccuracy.toDouble()/10.0
//            else previous.y += selectedAccuracy.toDouble()/10.0
//
//            editing!!.teleport(previous)
//        }
//
//        if(mode == Mode.ROTATION) {
//            val previous = editing!!.location
//
//            if(crouching) previous.yaw -= selectedAccuracy
//            else previous.yaw += selectedAccuracy
//
//            editing!!.teleport(previous)
//        }
//    }
//
//    fun moveZ(crouching: Boolean, armorStand: ArmorStand) {
//        editing = armorStand
//
//        if(mode == Mode.LEFT_LEG) {
//            var previous = editing!!.leftLegPose
//
//            previous = if(crouching) previous.setZ(previous.z - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setZ(previous.z + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.leftLegPose = previous
//        }
//
//        if(mode == Mode.RIGHT_LEG) {
//            var previous = editing!!.rightLegPose
//
//            previous = if(crouching) previous.setZ(previous.z - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setZ(previous.z + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.rightLegPose = previous
//        }
//
//        if(mode == Mode.HEAD) {
//            var previous = editing!!.headPose
//
//            previous = if(crouching) previous.setZ(previous.z - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setZ(previous.z + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.headPose = previous
//        }
//
//        if(mode == Mode.BODY) {
//            var previous = editing!!.bodyPose
//
//            previous = if(crouching) previous.setZ(previous.z - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setZ(previous.z + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.bodyPose = previous
//        }
//
//        if(mode == Mode.LEFT_ARM) {
//            var previous = editing!!.leftArmPose
//
//            previous = if(crouching) previous.setZ(previous.z - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setZ(previous.z + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.leftArmPose = previous
//        }
//
//        if(mode == Mode.RIGHT_ARM) {
//            var previous = editing!!.rightArmPose
//
//            previous = if(crouching) previous.setZ(previous.z - Math.toRadians(selectedAccuracy.toDouble()))
//            else previous.setZ(previous.z + Math.toRadians(selectedAccuracy.toDouble()))
//
//            editing!!.rightArmPose = previous
//        }
//
//        if(mode == Mode.POSITION) {
//            val previous = editing!!.location
//
//            if(crouching) previous.z -= selectedAccuracy.toDouble()/10.0
//            else previous.z += selectedAccuracy.toDouble()/10.0
//
//            editing!!.teleport(previous)
//        }
//
//        if(mode == Mode.ROTATION) {
//            val previous = editing!!.location
//
//            if(crouching) previous.yaw -= selectedAccuracy
//            else previous.yaw += selectedAccuracy
//
//            editing!!.teleport(previous)
//        }
//    }
//}