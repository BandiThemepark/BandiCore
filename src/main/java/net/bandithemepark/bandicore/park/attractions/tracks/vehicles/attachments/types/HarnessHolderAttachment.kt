package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class HarnessHolderAttachment: ModelAttachment("harness_holder", "MATERIAL, CUSTOM_MODEL_DATA_HARNESS, CUSTOM_MODEL_DATA_NO_HARNESS") {
    var modelNoHarness: ItemStack? = null
    lateinit var parent: Attachment
    lateinit var lastLocation: Location

    override fun onMetadataLoad(metadata: List<String>) {
        model = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[1].toInt()).build()
        modelNoHarness = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[2].toInt()).build()
    }

    override fun onSpawn(location: Location, parent: Attachment) {
        super.onSpawn(location, parent)
        lastLocation = location.clone()
        this.parent = parent
    }

    override fun onUpdate(
        mainPosition: Vector,
        mainRotation: Quaternion,
        secondaryPositions: HashMap<Vector, Quaternion>,
        rotationDegrees: Vector
    ) {
        super.onUpdate(mainPosition, mainRotation, secondaryPositions, rotationDegrees)
        lastLocation = mainPosition.toLocation(lastLocation.world)

        if(shouldShowHarness()) {
            displayEntity!!.setItemStack(modelNoHarness)
            displayEntity!!.updateMetadata()
            showHarnesses(true)
        } else {
            displayEntity!!.setItemStack(model)
            displayEntity!!.updateMetadata()
            showHarnesses(false)
        }
    }

    private fun shouldShowHarness(): Boolean {
        val harnessAttachments = parent.parent!!.getAllAttachments().filter { it.type is HarnessAttachment }
        if(harnessAttachments.isEmpty()) return true
        return harnessAttachments.any { (it.type as HarnessAttachment).harnessPosition != 0.0 }
    }

    private fun showHarnesses(show: Boolean) {
        val harnessAttachments = parent.parent!!.getAllAttachments().filter { it.type is HarnessAttachment }
        harnessAttachments.forEach {
            if(show) {
                (it.type as HarnessAttachment).showHarness(lastLocation)
            } else {
                (it.type as HarnessAttachment).hideHarness()
            }
        }
    }
}