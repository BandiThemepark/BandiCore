package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.joml.Matrix4f
import java.lang.Exception

open class ModelAttachment(id: String = "model", howToConfigure: String = "MATERIAL, CUSTOM_MODEL_DATA"): AttachmentType(id, howToConfigure) {

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
        try {
            parentArmorStand!!.handle.isInvisible = true
            parentArmorStand!!.handle.isNoGravity = true
            (parentArmorStand!!.handle as ArmorStand).isMarker = true
            parentArmorStand!!.updateMetadata()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage("PacketArmorStand handle is null? Spawn state: ${parentArmorStand!!.spawned}")
            e.printStackTrace()
        }

        // Spawn the display entity
        displayEntity = PacketItemDisplay()
        displayEntity!!.spawn(location)

        displayEntity!!.setItemStack(model)
        displayEntity!!.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        displayEntity!!.setInterpolationDuration(2)

        displayEntity!!.updateMetadata()

        // Attach the display entity to the ArmorStand
        parentArmorStand!!.addPassenger(displayEntity!!.handle.id)
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

        // Attach the display entity to the ArmorStand
        parentArmorStand!!.updatePassengers()
    }

    override fun onDeSpawn() {
        displayEntity?.deSpawn()
        displayEntity = null

        parentArmorStand?.deSpawn()
        parentArmorStand = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        model = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[1].toInt()).build()
        displayEntity?.setItemStack(model)
    }

    override fun markFor(player: Player) {
        displayEntity?.startGlowFor(player)
    }

    override fun unMarkFor(player: Player) {
        displayEntity?.endGlowFor(player)
    }
}