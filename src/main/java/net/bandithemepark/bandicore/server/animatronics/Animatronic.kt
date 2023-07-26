package net.bandithemepark.bandicore.server.animatronics

import com.google.common.base.Preconditions
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftItemDisplay
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.io.File
import java.util.*

/**
 * Represents an animatronic, created with Animated Java in Blockbench
 * @param fileName The name of the file, without the extension. For example, if the file is named "animatronic.json", the fileName would be "animatronic". The default directory (plugins/BandiCore/animatronics/) is automatically included
 */
class Animatronic(fileName: String) {
    lateinit var namespace: String
    lateinit var itemMaterial: Material

    var nodes = mutableListOf<AnimatronicNode>()
    lateinit var defaultPose: AnimatronicPose
    val animations = mutableListOf<AnimatronicAnimation>()

    var visibilityType: PacketEntity.VisibilityType = PacketEntity.VisibilityType.BLACKLIST
    var visibilityList = mutableListOf<Player>()

    init {
        val file = File("plugins/BandiCore/animatronics/$fileName.json")
        val json = JsonParser().parse(file.readText()).asJsonObject
        loadData(json)
    }

    /**
     * Loads the data from the JSON file
     * Called in the init block
     * @param json The JSON object to load data from
     */
    private fun loadData(json: JsonObject) {
        // Load main settings
        val projectSettings = json.getAsJsonObject("project_settings")
        namespace = projectSettings.get("project_namespace").asString
        itemMaterial = Material.matchMaterial(projectSettings.get("rig_item").asString.replace("minecraft:", "").uppercase())!!

        // Extra preparation
        val rigJson = json.getAsJsonObject("rig")

        // Load all nodes
        val nodeMapJson = rigJson.getAsJsonObject("node_map")
        for((uuidString, nodeJson) in nodeMapJson.entrySet()) {
            nodes.add(AnimatronicNode(uuidString, nodeJson.asJsonObject))
        }

        // Load default pose
        val defaultPoseJson = rigJson.getAsJsonArray("default_pose")
        defaultPose = AnimatronicPose(defaultPoseJson)

        // Load all animations
        val animationsJson = json.getAsJsonObject("animations")
        for((name, animationJson) in animationsJson.entrySet()) {
            animations.add(AnimatronicAnimation(name, animationJson.asJsonObject))
        }
    }

    var spawned = false
    val displayEntities = hashMapOf<UUID, PacketItemDisplay>()

    lateinit var basePosition: Vector private set
    lateinit var baseRotation: Quaternion

    /**
     * Spawns the animatronic at the given location so that it can be animated afterwards.
     * Automatically applies the default pose
     * @param baseLocation The location to spawn the animatronic at. Used as the base for animations.
     * @param baseRotation The rotation to spawn the animatronic with. Used as the base for animations.
     */
    fun spawn(baseLocation: Location, baseRotation: Quaternion) {
        Preconditions.checkArgument(!spawned, "Animatronic is already spawned")

        val spawnLocation = baseLocation.clone()
        spawnLocation.pitch = 0.0f
        spawnLocation.yaw = 0.0f

        for(node in nodes) {
            val displayEntity = PacketItemDisplay()
            displayEntity.visibilityType = visibilityType
            displayEntity.visibilityList = visibilityList
            displayEntity.spawn(spawnLocation.clone())

            displayEntity.setItemStack(ItemFactory(itemMaterial).setCustomModelData(node.customModelData).build())
            displayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)

            displayEntity.setInterpolationDuration(2)
            displayEntity.setInterpolationDelay(-1)

            displayEntity.updateMetadata()
            displayEntities[node.uuid] = displayEntity
        }

        this.baseRotation = baseRotation
        basePosition = baseLocation.toVector()

        spawned = true
        BandiCore.instance.animatronicManager.spawnedAnimatronics.add(this)
        applyPose(defaultPose)
    }

    /**
     * Changes the base position of the animatronic to the given position. Automatically teleports the animatronic to the new position. Do not use before spawn
     * @param basePosition The new base position
     */
    fun setBasePosition(basePosition: Vector) {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")
        this.basePosition = basePosition

        displayEntities.values.forEach {
            it.moveEntity(basePosition.x, basePosition.y, basePosition.z)
        }
    }

    /**
     * Applies the given pose to the animatronic
     * Automatically called for the default pose on spawn
     * @param pose The pose to apply
     */
    private fun applyPose(pose: AnimatronicPose) {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")

        for(node in pose.nodes) {
            val displayEntity = displayEntities[node.uuid] ?: continue
            displayEntity.setInterpolationDelay(-1)

            val oldMatrix = node.matrix.clone() as Matrix4f
            val beforeTranslation = oldMatrix.getTranslation(Vector3f())
            val beforeRotation = oldMatrix.getUnnormalizedRotation(Quaternionf())

            val matrix = Matrix4f()
                .rotate(baseRotation.toBukkitQuaternion())
                .translate(beforeTranslation)
                .rotate(beforeRotation)

            displayEntity.setTransformationMatrix(matrix)

            displayEntity.updateMetadata()
        }
    }

    var currentAnimation: AnimatronicAnimation? = null
    var currentAnimationTime = 0
    var animationLooping = false

    /**
     * Plays the animation with the given name.
     * @param name The name of the animation to play
     * @param loop Whether to loop the animation or not. When true, the animation will restart when it ends, and will continue until stopped
     */
    fun playAnimation(name: String, loop: Boolean) {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")

        currentAnimation = animations.find { it.name == name }
        currentAnimationTime = 0
        animationLooping = loop

        applyCurrentAnimationFrame()
    }

    /**
     * Stops the currently playing animation and resets to the default pose
     */
    fun stopAnimation() {
        currentAnimation = null
        applyPose(defaultPose)
    }

    /**
     * Removes the animatronic
     */
    fun deSpawn() {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")

        for(displayEntity in displayEntities.values) {
            displayEntity.deSpawn()
        }

        displayEntities.clear()
        spawned = false
        queuedForDeSpawn = true
    }

    var queuedForDeSpawn = false

    /**
     * Applies the current animation frame to the animatronic
     */
    private fun applyCurrentAnimationFrame() {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")
        if(currentAnimation == null) return

        applyPose(currentAnimation!!.frames.find { it.time == currentAnimationTime }!!.pose)
    }

    var onComplete: Runnable? = null

    /**
     * Updates the animatronic and its animations
     * Called every tick by the manager
     */
    fun tick() {
        if(!spawned) return
        if(currentAnimation == null) return

        currentAnimationTime++

        if(currentAnimationTime >= currentAnimation!!.duration) {
            if(animationLooping) {
                currentAnimationTime = 0
            } else {
                stopAnimation()
                if(onComplete != null) onComplete!!.run()

                return
            }
        }

        applyCurrentAnimationFrame()
    }
}