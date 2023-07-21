package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.joml.Matrix4f

class ModelAttachment: AttachmentType("model", "MATERIAL, CUSTOM_MODEL_DATA") {
    //    var armorStand: PacketEntityArmorStand? = null
//    private var debug = false
//    var model: ItemStack? = null
//
//    private var lastRotation: Quaternion? = null
//    private var lastRotationDegrees: Vector? = null
//
//    override fun onSpawn(location: Location, parent: Attachment) {
//        armorStand = PacketEntityArmorStand()
//        armorStand!!.spawn(location)
//
//        armorStand!!.helmet = model
//        armorStand!!.handle!!.isInvisible = true
//        armorStand!!.handle!!.isNoGravity = true
//        (armorStand!!.handle!! as ArmorStand).isMarker = true
//        armorStand!!.updateMetadata()
//    }
//
//    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector) {
//        if(debug) Bukkit.broadcast(Component.text("Rotation: $rotationDegrees"))
//
//        val newPosition = mainPosition.clone().add(Vector(0.0, -1.4375, 0.0))
//
//        if(lastRotationDegrees == null) lastRotationDegrees = rotationDegrees
//        if(lastRotation == null) lastRotation = mainRotation
//
//        if(lastRotationDegrees!!.x.toInt() == 0 && lastRotationDegrees!!.z.toInt() == 0) {
//            armorStand?.moveEntity(newPosition.x, newPosition.y, newPosition.z)
//            armorStand?.setHeadPose(0.0, lastRotationDegrees!!.y, 0.0)
//        } else {
//            armorStand?.moveWithHead(newPosition, lastRotation!!)
//        }
//
//        lastRotationDegrees = rotationDegrees
//        lastRotation = mainRotation
//    }
//
//    override fun onDeSpawn() {
//        armorStand!!.deSpawn()
//        armorStand = null
//    }
//
//    override fun onMetadataLoad(metadata: List<String>) {
//        model = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[1].toInt()).build()
//        if(armorStand != null) armorStand!!.helmet = model
//    }
//
//    override fun markFor(player: Player) {
//        armorStand!!.startGlowFor(player)
//    }
//
//    override fun unMarkFor(player: Player) {
//        armorStand!!.endGlowFor(player)
//    }

    var parentArmorStand: PacketEntityArmorStand? = null
    var displayEntity: PacketItemDisplay? = null
    var model: ItemStack? = null
    var spawnLocation: Location? = null

    override fun onSpawn(location: Location, parent: Attachment) {
        val armorStandSpawnLocation = location.clone()
        armorStandSpawnLocation.pitch = 0.0f
        armorStandSpawnLocation.yaw = 0.0f

        // Spawn an ArmorStand to hold the item display (used for smoothness)
        parentArmorStand = PacketEntityArmorStand()
        parentArmorStand!!.spawn(armorStandSpawnLocation)
        parentArmorStand!!.handle!!.isInvisible = true
        parentArmorStand!!.handle!!.isNoGravity = true
        (parentArmorStand!!.handle!! as ArmorStand).isMarker = true
        parentArmorStand!!.updateMetadata()

        // Spawn the display entity
        displayEntity = PacketItemDisplay()
        displayEntity!!.spawn(location)

        displayEntity!!.setItemStack(model)
        displayEntity!!.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        displayEntity!!.setInterpolationDuration(2)

        displayEntity!!.updateMetadata()

        // Attach the display entity to the ArmorStand
        parentArmorStand!!.addPassenger(displayEntity!!.handle!!.id)
        parentArmorStand!!.updatePassengers()

        spawnLocation = location
    }

    var lastRotation: Quaternion? = null
    override fun onUpdate(
        mainPosition: Vector,
        mainRotation: Quaternion,
        secondaryPositions: HashMap<Vector, Quaternion>,
        rotationDegrees: Vector
    ) {
        if(displayEntity == null) {
            return
        }
        displayEntity!!.setInterpolationDelay(-1)

        if(lastRotation == null) lastRotation = mainRotation
        val matrix = Matrix4f().rotation(lastRotation!!.toBukkitQuaternion())
        displayEntity!!.setTransformationMatrix(matrix)
        displayEntity!!.updateMetadata()

        val position = mainPosition.add(Vector(0.0, 0.45, 0.0))
        parentArmorStand!!.moveEntity(position.x, position.y, position.z)

        lastRotation = mainRotation
    }

    override fun onDeSpawn() {
        displayEntity?.deSpawn()
        displayEntity = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        model = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[1].toInt()).build()
        displayEntity?.setItemStack(model)
    }

    override fun markFor(player: Player) {
        displayEntity!!.startGlowFor(player)
    }

    override fun unMarkFor(player: Player) {
        displayEntity!!.endGlowFor(player)
    }
}