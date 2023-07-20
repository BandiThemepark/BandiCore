package net.bandithemepark.bandicore.server.animatronics

import com.google.common.base.Preconditions
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.itemdisplay.PacketItemDisplay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftItemDisplay
import org.bukkit.entity.ItemDisplay
import java.io.File
import java.util.*

/**
 * Represents an animatronic, created with Animated Java in Blockbench
 * @param fileName The name of the file, without the extension. For example, if the file is named "animatronic.json", the fileName would be "animatronic". The default directory (plugins/BandiCore/animatronics/) is automatically included
 */
class Animatronic(fileName: String) {
    lateinit var namespace: String
    lateinit var itemMaterial: Material

    val nodes = mutableListOf<AnimatronicNode>()
    lateinit var defaultPose: AnimatronicPose
    val animations = mutableListOf<AnimatronicAnimation>()

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
    private val displayEntities = hashMapOf<UUID, PacketItemDisplay>()

    /**
     * Spawns the animatronic at the given location so that it can be animated afterwards.
     * Automatically applies the default pose
     * @param baseLocation The location to spawn the animatronic at. Used as the base for animations. Rotation is used as well
     */
    fun spawn(baseLocation: Location) {
        Preconditions.checkArgument(!spawned, "Animatronic is already spawned")

        for(node in nodes) {
            val displayEntity = PacketItemDisplay()
            displayEntity.spawn(baseLocation.clone())

            displayEntity.setItemStack(ItemFactory(itemMaterial).setCustomModelData(node.customModelData).build())
            displayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)

            displayEntity.setInterpolationDuration(2)
            displayEntity.setInterpolationDelay(-1)

            displayEntity.updateMetadata()
            displayEntities[node.uuid] = displayEntity
        }

        spawned = true
        BandiCore.instance.animatronicManager.spawnedAnimatronics.add(this)
        applyPose(defaultPose)
    }

    /**
     * Applies the given pose to the animatronic
     * Automatically called for the default pose on spawn
     * @param pose The pose to apply
     */
    private fun applyPose(pose: AnimatronicPose) {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")

        for(node in pose.nodes) {
            val displayEntity = displayEntities[node.uuid]!!

            displayEntity.setInterpolationDelay(-1)
            displayEntity.setTransformationMatrix(node.matrix)

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
        BandiCore.instance.animatronicManager.spawnedAnimatronics.remove(this)
    }

    /**
     * Applies the current animation frame to the animatronic
     */
    private fun applyCurrentAnimationFrame() {
        Preconditions.checkArgument(spawned, "Animatronic is not spawned")
        Preconditions.checkArgument(currentAnimation != null, "No animation is currently playing")

        applyPose(currentAnimation!!.frames.find { it.time == currentAnimationTime }!!.pose)
    }

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
                return
            }
        }

        applyCurrentAnimationFrame()
    }

}