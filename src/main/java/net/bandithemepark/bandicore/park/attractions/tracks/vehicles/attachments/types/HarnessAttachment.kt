package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.util.Vector

class HarnessAttachment: ModelAttachment("harness", "MATERIAL, CUSTOM_MODEL_DATA_HARNESS, UP_ANGLE, REGION_ID?") {
    lateinit var parent: Attachment
    var upAngle = 0.0
    lateinit var lastLocation: Location

    var harnessesLocked = true
    var harnessPosition = 0.0
        set(value) {
            if(field == 0.0 && value != 0.0) {
                val spawnLocation = lastLocation.clone().add(Vector(0.0, -3.0, 0.0))
                spawnLocation.pitch = 0.0f
                spawnLocation.yaw = 0.0f

//                showHarness(spawnLocation)
            } else if(field != 0.0 && value == 0.0) {
//                hideHarness()
            }

            field = value

            if(value != 0.0) {
                parent.position.pitch = value
            }

            val harnessesOpen = value != 0.0
            parent.parent!!.getAllAttachments().filter { it.type is SeatAttachment && (it.type as SeatAttachment).harnessAttachment != null && (it.type as SeatAttachment).harnessAttachmentId == parent.id }.forEach {
                (it.type as SeatAttachment).seat!!.harnessesOpen = harnessesOpen
            }
        }

    override fun onSpawn(location: Location, parent: Attachment) {
        this.parent = parent
//        showHarness(location)
        lastLocation = location.clone()
    }

    override fun onUpdate(
        mainPosition: Vector,
        mainRotation: Quaternion,
        secondaryPositions: HashMap<Vector, Quaternion>,
        rotationDegrees: Vector
    ) {
        super.onUpdate(mainPosition, mainRotation, secondaryPositions, rotationDegrees)
        lastLocation = mainPosition.toLocation(lastLocation.world)

        if(state == State.UP) updateUpwardsInterpolation()
        if(state == State.DOWN) updateDownwardsInterpolation()
    }

    override fun onDeSpawn() {

    }

    override fun onMetadataLoad(metadata: List<String>) {
        model = ItemFactory(Material.matchMaterial(metadata[0].uppercase())!!).setCustomModelData(metadata[1].toInt()).build()
        upAngle = metadata[2].toDouble()
        if(metadata.size > 3) regionId = metadata[3]
    }

    var spawned = false
    fun showHarness(location: Location) {
        if(spawned) return
        spawned = true

        super.onSpawn(location, parent)
        BandiCore.instance.server.scoreboard.setGlowColor(displayEntity!!.handle.uuid.toString(), ChatColor.RED)
        // TODO Spawn hitbox
    }

    fun hideHarness() {
        if(!spawned) return
        spawned = false

        super.onDeSpawn()
        // TODO Despawn hitbox
    }

    var state = State.NONE
    var currentProgress = 0
    var startAngle = 0.0

    fun startUpwardsInterpolation() {
        state = State.UP
        currentProgress = 0
        startAngle = harnessPosition
    }

    private fun updateUpwardsInterpolation() {
        if(currentProgress >= 30) return

        currentProgress += 1
        val progress = currentProgress.toDouble() / 30.0
        harnessPosition = MathUtil.easeOutBounceInterpolation(startAngle, upAngle, progress)

        if(currentProgress == 30) {
            harnessPosition = upAngle
            state = State.NONE
            RideOP.get("cancancoaster")!!.updateMenu()
        }
    }

    fun startDownwardsInterpolation() {
        state = State.DOWN
        currentProgress = 0
        startAngle = harnessPosition
    }

    private fun updateDownwardsInterpolation() {
        if(currentProgress >= 30) return

        currentProgress += 1
        val progress = currentProgress.toDouble() / 30.0
        harnessPosition = MathUtil.cosineInterpolation(progress, startAngle, 0.0)

        if(currentProgress == 30) {
            harnessPosition = 0.0
            state = State.NONE
            RideOP.get("cancancoaster")!!.updateMenu()
        }
    }

    enum class State {
        UP, DOWN, NONE
    }
}