package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class ModelAttachment: AttachmentType("model", "MATERIAL, CUSTOM_MODEL_DATA") {
    private var armorStand: PacketEntityArmorStand? = null
    var model: ItemStack? = null

    override fun onSpawn(location: Location) {
        armorStand = PacketEntityArmorStand()
        armorStand!!.spawn(location)

        armorStand!!.helmet = model
        armorStand!!.handle!!.isInvisible = true
        armorStand!!.handle!!.isNoGravity = true
        (armorStand!!.handle!! as ArmorStand).isMarker = true
        armorStand!!.updateMetadata()
    }

    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>) {
        val newPosition = mainPosition.clone().add(Vector(0.0, -1.4375, 0.0))
        armorStand!!.moveWithHead(newPosition, mainRotation)
    }

    override fun onDeSpawn() {
        armorStand!!.deSpawn()
        armorStand = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        model = ItemFactory.create(Material.matchMaterial(metadata[0].uppercase())!!, 1, metadata[1].toInt(), Component.text(""))
    }

    override fun markFor(player: Player) {
        val before = armorStand!!.handle!!.hasGlowingTag()
        armorStand!!.handle!!.setGlowingTag(true)
        armorStand!!.updateMetadataFor(player)
        armorStand!!.handle!!.setGlowingTag(before)
    }

    override fun unMarkFor(player: Player) {
        val before = armorStand!!.handle!!.hasGlowingTag()
        armorStand!!.handle!!.setGlowingTag(false)
        armorStand!!.updateMetadataFor(player)
        armorStand!!.handle!!.setGlowingTag(before)
    }
}