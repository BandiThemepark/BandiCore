package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class ModelAttachment: AttachmentType("model", "MATERIAL, CUSTOM_MODEL_DATA") {
    var armorStand: PacketEntityArmorStand? = null
    private var debug = false
    var model: ItemStack? = null

    private var lastRotation: Quaternion? = null
    private var lastRotationDegrees: Vector? = null

    override fun onSpawn(location: Location, parent: Attachment) {
        armorStand = PacketEntityArmorStand()
        armorStand!!.spawn(location)

        armorStand!!.helmet = model
        armorStand!!.handle!!.isInvisible = true
        armorStand!!.handle!!.isNoGravity = true
        (armorStand!!.handle!! as ArmorStand).isMarker = true
        armorStand!!.updateMetadata()
    }

    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector) {
        if(debug) Bukkit.broadcast(Component.text("Rotation: $rotationDegrees"))

        val newPosition = mainPosition.clone().add(Vector(0.0, -1.4375, 0.0))

        if(lastRotationDegrees == null) lastRotationDegrees = rotationDegrees
        if(lastRotation == null) lastRotation = mainRotation

        if(lastRotationDegrees!!.x.toInt() == 0 && lastRotationDegrees!!.z.toInt() == 0) {
            armorStand?.moveEntity(newPosition.x, newPosition.y, newPosition.z)
            armorStand?.setHeadPose(0.0, lastRotationDegrees!!.y, 0.0)
        } else {
            armorStand?.moveWithHead(newPosition, lastRotation!!)
        }

        lastRotationDegrees = rotationDegrees
        lastRotation = mainRotation
    }

    override fun onDeSpawn() {
        armorStand!!.deSpawn()
        armorStand = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        model = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[1].toInt()).build()
        if(armorStand != null) armorStand!!.helmet = model
    }

    override fun markFor(player: Player) {
        armorStand!!.startGlowFor(player)
    }

    override fun unMarkFor(player: Player) {
        armorStand!!.endGlowFor(player)
    }
}